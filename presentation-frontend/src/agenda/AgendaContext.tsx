import { createContext, useContext, useMemo, useState, type ReactNode } from 'react'
import { validarCompromissoForm, validarLembreteForm } from './agendaValidacao'
import {
  COMPROMISSOS_INICIAIS,
  LEMBRETES_INICIAIS,
  type CompromissoAgenda,
  type EventoAgenda,
  type LembreteAgenda,
  type LembreteVinculo,
  type TipoCompromisso,
} from './types'

function gerarId(prefixo: string): string {
  return `${prefixo}-${Date.now()}-${Math.random().toString(36).slice(2, 7)}`
}

interface AgendaContextValue {
  eventos: EventoAgenda[]
  compromissos: CompromissoAgenda[]
  lembretes: LembreteAgenda[]
  lembretesPendentes: LembreteAgenda[]
  setCompromissos: React.Dispatch<React.SetStateAction<CompromissoAgenda[]>>
  setLembretes: React.Dispatch<React.SetStateAction<LembreteAgenda[]>>
  criarCompromisso: (dados: Omit<CompromissoAgenda, 'id' | 'status'>) => string | null
  atualizarCompromisso: (id: string, dados: Partial<CompromissoAgenda>) => string | null
  criarLembrete: (params: {
    vinculo: LembreteVinculo
    eventoId: string
    compromissoId: string
    data: string
    horario: string
  }) => string | null
}

const AgendaContext = createContext<AgendaContextValue | null>(null)

export function AgendaProvider({ eventos, children }: { eventos: EventoAgenda[]; children: ReactNode }) {
  const [compromissos, setCompromissos] = useState<CompromissoAgenda[]>(COMPROMISSOS_INICIAIS)
  const [lembretes, setLembretes] = useState<LembreteAgenda[]>(LEMBRETES_INICIAIS)

  const lembretesPendentes = useMemo(() => lembretes.filter((l) => !l.notificado), [lembretes])

  const criarCompromisso = (dados: Omit<CompromissoAgenda, 'id' | 'status'>): string | null => {
    const erro = validarCompromissoForm({
      formEventoId: dados.eventoId,
      formTitulo: dados.titulo,
      formData: dados.data,
      formHoraInicio: dados.horaInicio,
      formHoraFim: dados.horaFim,
      formLocal: dados.local,
      compromissos,
      compromissoEditId: null,
    })
    if (erro) return erro

    setCompromissos((prev) => [
      ...prev,
      { ...dados, id: gerarId('comp'), status: 'Pendente' },
    ])
    return null
  }

  const atualizarCompromisso = (id: string, dados: Partial<CompromissoAgenda>): string | null => {
    const atual = compromissos.find((c) => c.id === id)
    if (!atual) return 'Compromisso não encontrado.'

    const merged = { ...atual, ...dados }
    const erro = validarCompromissoForm({
      formEventoId: merged.eventoId,
      formTitulo: merged.titulo,
      formData: merged.data,
      formHoraInicio: merged.horaInicio,
      formHoraFim: merged.horaFim,
      formLocal: merged.local,
      compromissos,
      compromissoEditId: id,
    })
    if (erro) return erro

    setCompromissos((prev) => prev.map((c) => (c.id === id ? merged : c)))
    return null
  }

  const criarLembrete = (params: {
    vinculo: LembreteVinculo
    eventoId: string
    compromissoId: string
    data: string
    horario: string
  }): string | null => {
    const erro = validarLembreteForm({
      lembreteVinculo: params.vinculo,
      lembreteEventoId: params.eventoId,
      lembreteCompromissoId: params.compromissoId,
      lembreteData: params.data,
      lembreteHorario: params.horario,
      compromissos,
      lembretes,
      eventos,
    })
    if (erro) return erro

    const comp = params.compromissoId ? compromissos.find((c) => c.id === params.compromissoId) : null
    const eventoId =
      params.vinculo === 'compromisso' ? comp?.eventoId ?? params.eventoId : params.eventoId

    setLembretes((prev) => [
      ...prev,
      {
        id: gerarId('lem'),
        eventoId: eventoId || null,
        compromissoId: params.vinculo === 'compromisso' ? params.compromissoId : null,
        data: params.data,
        horario: params.horario,
        notificado: false,
      },
    ])
    return null
  }

  const value: AgendaContextValue = {
    eventos,
    compromissos,
    lembretes,
    lembretesPendentes,
    setCompromissos,
    setLembretes,
    criarCompromisso,
    atualizarCompromisso,
    criarLembrete,
  }

  return <AgendaContext.Provider value={value}>{children}</AgendaContext.Provider>
}

export function useAgenda(): AgendaContextValue {
  const ctx = useContext(AgendaContext)
  if (!ctx) {
    throw new Error('useAgenda deve ser usado dentro de AgendaProvider')
  }
  return ctx
}

export type { TipoCompromisso }
