import { createContext, useCallback, useContext, useEffect, useMemo, useRef, useState, type ReactNode } from 'react'
import { toast } from 'sonner'
import { validarCompromissoForm, validarLembreteForm } from './agendaValidacao'
import {
  type CompromissoAgenda,
  type EventoAgenda,
  type LembreteAgenda,
  type LembreteVinculo,
  type StatusCompromisso,
  type TipoCompromisso,
} from './types'

const API: string =
  (import.meta as { env?: { VITE_API_URL?: string } }).env?.VITE_API_URL ?? 'http://localhost:3000/api'

type BackendCompromisso = {
  id: string
  gestorId: string
  eventoId: string
  titulo: string
  descricao?: string
  dataInicio: string
  dataFim: string
  status: 'AGENDADO' | 'EM_ANDAMENTO' | 'REALIZADO' | 'CANCELADO' | 'ADIADO'
}

type BackendLembrete = {
  id: string
  compromissoId: string | null
  eventoId: string | null
  horario: string
  notificado: boolean
}

type BackendFuncionario = {
  id: string
  nome: string
  cargo: string
}

type BackendAlerta = {
  lembreteId: string
  mensagem: string
  horario: string
}

async function api<T>(path: string, options?: RequestInit): Promise<T> {
  const res = await fetch(`${API}${path}`, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  })
  if (!res.ok) {
    let msg = `Erro ${res.status}`
    try {
      const body = await res.json()
      msg = body?.erro ?? msg
    } catch {
      /* corpo vazio */
    }
    throw new Error(msg)
  }
  if (res.status === 204) return undefined as T
  const text = await res.text()
  return (text ? JSON.parse(text) : undefined) as T
}

function toDateTime(data: string, hora: string): string {
  return `${data}T${hora.length === 5 ? hora : hora.slice(0, 5)}:00`
}

function statusFromBackend(status: BackendCompromisso['status']): StatusCompromisso {
  switch (status) {
    case 'EM_ANDAMENTO':
      return 'Em andamento'
    case 'REALIZADO':
      return 'Concluído'
    default:
      return 'Pendente'
  }
}

function mapCompromissoFromBackend(b: BackendCompromisso): CompromissoAgenda {
  return {
    id: b.id,
    eventoId: b.eventoId,
    titulo: b.titulo,
    descricao: b.descricao ?? '',
    data: b.dataInicio.slice(0, 10),
    horaInicio: b.dataInicio.slice(11, 16),
    horaFim: b.dataFim.slice(11, 16),
    tipo: 'Reunião' as TipoCompromisso,
    local: '',
    status: statusFromBackend(b.status),
  }
}

function mapLembreteFromBackend(b: BackendLembrete): LembreteAgenda {
  return {
    id: b.id,
    eventoId: b.eventoId,
    compromissoId: b.compromissoId,
    data: b.horario.slice(0, 10),
    horario: b.horario.slice(11, 16),
    notificado: b.notificado,
  }
}

interface AgendaContextValue {
  gestorId: string
  loading: boolean
  eventos: EventoAgenda[]
  compromissos: CompromissoAgenda[]
  lembretes: LembreteAgenda[]
  lembretesPendentes: LembreteAgenda[]
  refresh: () => Promise<void>
  criarCompromisso: (dados: Omit<CompromissoAgenda, 'id' | 'status'>) => Promise<string | null>
  atualizarCompromisso: (id: string, dados: Partial<CompromissoAgenda>) => Promise<string | null>
  removerCompromisso: (id: string) => Promise<string | null>
  avancarStatusCompromisso: (id: string, statusAtual: StatusCompromisso) => Promise<string | null>
  criarLembrete: (params: {
    vinculo: LembreteVinculo
    eventoId: string
    compromissoId: string
    data: string
    horario: string
  }) => Promise<string | null>
  atualizarLembrete: (id: string, data: string, horario: string) => Promise<string | null>
  removerLembrete: (id: string) => Promise<string | null>
}

const AgendaContext = createContext<AgendaContextValue | null>(null)

export function AgendaProvider({ eventos: eventosProp, children }: { eventos: EventoAgenda[]; children: ReactNode }) {
  const [gestorId, setGestorId] = useState('')
  const [loading, setLoading] = useState(true)
  const [compromissos, setCompromissos] = useState<CompromissoAgenda[]>([])
  const [lembretes, setLembretes] = useState<LembreteAgenda[]>([])
  const alertasExibidosRef = useRef<Set<string>>(new Set())

  const eventos = useMemo(() => {
    const merged = [...eventosProp]
    for (const c of compromissos) {
      if (!merged.some((e) => e.id === c.eventoId)) {
        merged.push({ id: c.eventoId, nome: `Evento`, dataEvento: c.data })
      }
    }
    return merged
  }, [eventosProp, compromissos])

  const lembretesPendentes = useMemo(() => lembretes.filter((l) => !l.notificado), [lembretes])

  const refresh = useCallback(async () => {
    const [comps, lems] = await Promise.all([
      api<BackendCompromisso[]>('/compromissos'),
      api<BackendLembrete[]>('/lembretes'),
    ])
    setCompromissos(comps.map(mapCompromissoFromBackend))
    setLembretes(lems.map(mapLembreteFromBackend))
  }, [])

  const processarAlertas = useCallback(
    async (_gestor: string) => {
      try {
        const alertas = await api<BackendAlerta[]>('/lembretes/processar-vencidos', { method: 'POST' })
        for (const alerta of alertas) {
          if (alertasExibidosRef.current.has(alerta.lembreteId)) continue
          alertasExibidosRef.current.add(alerta.lembreteId)
          toast.info(alerta.mensagem, { duration: 8000 })
          if (typeof Notification !== 'undefined' && Notification.permission === 'granted') {
            new Notification('Lembrete', { body: alerta.mensagem })
          }
        }
        const lems = await api<BackendLembrete[]>('/lembretes')
        setLembretes(lems.map(mapLembreteFromBackend))
      } catch {
        /* backend indisponível */
      }
    },
    []
  )

  useEffect(() => {
    if (!gestorId) return
    if (typeof Notification !== 'undefined' && Notification.permission === 'default') {
      Notification.requestPermission().catch(() => {})
    }
    processarAlertas(gestorId)
    const intervalo = window.setInterval(() => processarAlertas(gestorId), 30_000)
    return () => window.clearInterval(intervalo)
  }, [gestorId, processarAlertas])

  useEffect(() => {
    let ativo = true
    async function init() {
      try {
        const funcionarios = await api<BackendFuncionario[]>('/funcionarios')
        const gestor =
          funcionarios.find((f) => f.cargo === 'GERENTE' && f.nome === 'Maria Silva') ??
          funcionarios.find((f) => f.cargo === 'GERENTE') ??
          funcionarios[0]
        if (!ativo || !gestor) {
          setLoading(false)
          return
        }
        setGestorId(gestor.id)
        const [comps, lems] = await Promise.all([
          api<BackendCompromisso[]>('/compromissos'),
          api<BackendLembrete[]>('/lembretes'),
        ])
        if (!ativo) return
        setCompromissos(comps.map(mapCompromissoFromBackend))
        setLembretes(lems.map(mapLembreteFromBackend))
      } catch {
        /* backend indisponível — mantém listas vazias */
      } finally {
        if (ativo) setLoading(false)
      }
    }
    init()
    return () => {
      ativo = false
    }
  }, [])

  const criarCompromisso = async (
    dados: Omit<CompromissoAgenda, 'id' | 'status'>
  ): Promise<string | null> => {
    const erro = validarCompromissoForm({
      formEventoId: dados.eventoId,
      formTitulo: dados.titulo,
      formData: dados.data,
      formHoraInicio: dados.horaInicio,
      formHoraFim: dados.horaFim,
      formLocal: dados.local,
      compromissos,
      compromissoEditId: null,
      eventos,
    })
    if (erro) return erro

    try {
      const criado = await api<BackendCompromisso>('/compromissos', {
        method: 'POST',
        body: JSON.stringify({
          gestorId,
          eventoId: dados.eventoId,
          titulo: dados.titulo,
          descricao: dados.descricao,
          dataInicio: toDateTime(dados.data, dados.horaInicio),
          dataFim: toDateTime(dados.data, dados.horaFim),
        }),
      })
      setCompromissos((prev) => [...prev, mapCompromissoFromBackend(criado)])
      return null
    } catch (e) {
      return e instanceof Error ? e.message : 'Erro ao criar compromisso.'
    }
  }

  const atualizarCompromisso = async (
    id: string,
    dados: Partial<CompromissoAgenda>
  ): Promise<string | null> => {
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
      eventos,
      statusAtual: atual.status,
    })
    if (erro) return erro

    try {
      const editado = await api<BackendCompromisso>(`/compromissos/${id}`, {
        method: 'PUT',
        body: JSON.stringify({
          titulo: merged.titulo,
          descricao: merged.descricao,
          dataInicio: toDateTime(merged.data, merged.horaInicio),
          dataFim: toDateTime(merged.data, merged.horaFim),
        }),
      })
      setCompromissos((prev) => prev.map((c) => (c.id === id ? mapCompromissoFromBackend(editado) : c)))
      return null
    } catch (e) {
      return e instanceof Error ? e.message : 'Erro ao editar compromisso.'
    }
  }

  const removerCompromisso = async (id: string): Promise<string | null> => {
    try {
      await api<void>(`/compromissos/${id}`, { method: 'DELETE' })
      setCompromissos((prev) => prev.filter((c) => c.id !== id))
      setLembretes((prev) => prev.filter((l) => l.compromissoId !== id))
      return null
    } catch (e) {
      return e instanceof Error ? e.message : 'Erro ao remover compromisso.'
    }
  }

  const avancarStatusCompromisso = async (
    id: string,
    statusAtual: StatusCompromisso
  ): Promise<string | null> => {
    try {
      const endpoint =
        statusAtual === 'Pendente' ? 'iniciar' : statusAtual === 'Em andamento' ? 'concluir' : null
      if (!endpoint) return 'Status não pode ser avançado.'
      const atualizado = await api<BackendCompromisso>(`/compromissos/${id}/${endpoint}`, {
        method: 'POST',
      })
      setCompromissos((prev) =>
        prev.map((c) => (c.id === id ? mapCompromissoFromBackend(atualizado) : c))
      )
      return null
    } catch (e) {
      return e instanceof Error ? e.message : 'Erro ao atualizar status.'
    }
  }

  const criarLembrete = async (params: {
    vinculo: LembreteVinculo
    eventoId: string
    compromissoId: string
    data: string
    horario: string
  }): Promise<string | null> => {
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

    try {
      const criado = await api<BackendLembrete>('/lembretes', {
        method: 'POST',
        body: JSON.stringify({
          compromissoId: params.vinculo === 'compromisso' ? params.compromissoId : null,
          eventoId: params.vinculo === 'compromisso' ? eventoId : params.eventoId,
          horario: toDateTime(params.data, params.horario),
        }),
      })
      setLembretes((prev) => [...prev, mapLembreteFromBackend(criado)])
      return null
    } catch (e) {
      return e instanceof Error ? e.message : 'Erro ao criar lembrete.'
    }
  }

  const atualizarLembrete = async (id: string, data: string, horario: string): Promise<string | null> => {
    const atual = lembretes.find((l) => l.id === id)
    if (!atual) return 'Lembrete não encontrado.'
    if (atual.notificado) return 'Não é permitido editar lembretes já notificados.'

    const vinculo: LembreteVinculo = atual.compromissoId ? 'compromisso' : 'evento'
    const erro = validarLembreteForm({
      lembreteVinculo: vinculo,
      lembreteEventoId: atual.eventoId ?? '',
      lembreteCompromissoId: atual.compromissoId ?? '',
      lembreteData: data,
      lembreteHorario: horario,
      compromissos,
      lembretes,
      eventos,
      lembreteEditId: id,
    })
    if (erro) return erro

    try {
      const editado = await api<BackendLembrete>(`/lembretes/${id}`, {
        method: 'PUT',
        body: JSON.stringify({ horario: toDateTime(data, horario) }),
      })
      setLembretes((prev) => prev.map((l) => (l.id === id ? mapLembreteFromBackend(editado) : l)))
      return null
    } catch (e) {
      return e instanceof Error ? e.message : 'Erro ao editar lembrete.'
    }
  }

  const removerLembrete = async (id: string): Promise<string | null> => {
    try {
      await api<void>(`/lembretes/${id}`, { method: 'DELETE' })
      setLembretes((prev) => prev.filter((l) => l.id !== id))
      return null
    } catch (e) {
      return e instanceof Error ? e.message : 'Erro ao remover lembrete.'
    }
  }

  const value: AgendaContextValue = {
    gestorId,
    loading,
    eventos,
    compromissos,
    lembretes,
    lembretesPendentes,
    refresh,
    criarCompromisso,
    atualizarCompromisso,
    removerCompromisso,
    avancarStatusCompromisso,
    criarLembrete,
    atualizarLembrete,
    removerLembrete,
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
