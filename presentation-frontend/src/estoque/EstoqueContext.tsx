import { createContext, useCallback, useContext, useEffect, useState, type ReactNode } from 'react'
import { toast } from 'sonner'
import { ApiError } from '../api/http'
import { estoqueApi } from '../api/estoqueApi'
import { financeiroApi } from '../api/financeiroApi'
import type {
  CenarioRedistribuicao,
  ConsumoEvento,
  EventoRef,
  ItemEstoque,
  ItemSubstituicao,
  PrevisaoConsumo,
  ReservaEstoque,
} from './types'

function extrairMensagemErro(e: unknown): string {
  if (e instanceof ApiError) return e.message
  if (e instanceof Error) return e.message
  return 'Erro inesperado'
}

interface EstoqueContextValue {
  loading: boolean
  eventos: EventoRef[]
  itens: ItemEstoque[]
  substituicoes: ItemSubstituicao[]
  reservas: ReservaEstoque[]
  previsoes: PrevisaoConsumo[]
  cenarios: CenarioRedistribuicao[]
  consumos: ConsumoEvento[]
  refresh: () => Promise<void>
  cadastrarItem: (nome: string, quantidadeTotal: number) => Promise<void>
  editarItem: (id: string, nome: string, quantidadeTotal: number) => Promise<void>
  adicionarEstoque: (id: string, quantidade: number) => Promise<void>
  desativarItem: (id: string) => Promise<void>
  cadastrarSubstituicao: (itemOriginalId: string, itemSubstitutoId: string, fator: number) => Promise<void>
  criarReserva: (params: {
    eventoId: string
    dataInicio: string
    dataFim: string
    itens: { itemEstoqueId: string; quantidade: number }[]
  }) => Promise<void>
  confirmarReserva: (id: string) => Promise<void>
  cancelarReserva: (id: string) => Promise<void>
  gerarPrevisao: (eventoId: string) => Promise<void>
  recalcularPrevisao: (id: string) => Promise<void>
  ajustarPrevisao: (id: string, ajustes: Record<string, number>, justificativa: string) => Promise<void>
  gerarCenario: (periodoInicio: string, periodoFim: string) => Promise<void>
  aplicarCenario: (id: string) => Promise<void>
  registrarConsumo: (params: {
    eventoId: string
    itens: { itemEstoqueId: string; categoriaConsumo: string; quantidadeConsumida: number }[]
  }) => Promise<void>
}

const EstoqueContext = createContext<EstoqueContextValue | undefined>(undefined)

export function EstoqueProvider({ eventos: eventosIniciais, children }: { eventos: EventoRef[]; children: ReactNode }) {
  const [loading, setLoading] = useState(true)
  const [eventos, setEventos] = useState<EventoRef[]>(eventosIniciais)
  const [itens, setItens] = useState<ItemEstoque[]>([])
  const [substituicoes, setSubstituicoes] = useState<ItemSubstituicao[]>([])
  const [reservas, setReservas] = useState<ReservaEstoque[]>([])
  const [previsoes, setPrevisoes] = useState<PrevisaoConsumo[]>([])
  const [cenarios, setCenarios] = useState<CenarioRedistribuicao[]>([])
  const [consumos, setConsumos] = useState<ConsumoEvento[]>([])

  const refresh = useCallback(async () => {
    setLoading(true)
    try {
      const [itensData, substituicoesData, reservasData, cenariosData, consumosData, previsoesData, eventosData] =
        await Promise.all([
        estoqueApi.listarItens(),
        estoqueApi.listarSubstituicoes(),
        estoqueApi.listarReservas(),
        estoqueApi.listarCenarios(),
        estoqueApi.listarConsumos(),
        estoqueApi.listarPrevisoes(),
        financeiroApi.listarEventos().catch(() => []),
      ])
      setEventos(eventosData.map((e) => ({ id: e.id, nome: e.nome })))
      setItens(itensData)
      setSubstituicoes(substituicoesData)
      setReservas(reservasData)
      setCenarios(cenariosData)
      setConsumos(consumosData)
      setPrevisoes(previsoesData)
    } catch (e) {
      toast.error(extrairMensagemErro(e))
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    void refresh()
  }, [refresh])

  const cadastrarItem = async (nome: string, quantidadeTotal: number) => {
    try {
      await estoqueApi.cadastrarItem(nome, quantidadeTotal)
      toast.success('Item cadastrado')
      await refresh()
    } catch (e) {
      toast.error(extrairMensagemErro(e))
    }
  }

  const editarItem = async (id: string, nome: string, quantidadeTotal: number) => {
    try {
      await estoqueApi.editarItem(id, nome, quantidadeTotal)
      toast.success('Item atualizado')
      await refresh()
    } catch (e) {
      toast.error(extrairMensagemErro(e))
    }
  }

  const adicionarEstoque = async (id: string, quantidade: number) => {
    try {
      await estoqueApi.adicionarEstoque(id, quantidade)
      toast.success('Estoque adicionado')
      await refresh()
    } catch (e) {
      toast.error(extrairMensagemErro(e))
    }
  }

  const desativarItem = async (id: string) => {
    try {
      await estoqueApi.desativarItem(id)
      toast.success('Item desativado')
      await refresh()
    } catch (e) {
      toast.error(extrairMensagemErro(e))
    }
  }

  const cadastrarSubstituicao = async (itemOriginalId: string, itemSubstitutoId: string, fator: number) => {
    try {
      await estoqueApi.cadastrarSubstituicao(itemOriginalId, itemSubstitutoId, fator)
      toast.success('Substituição cadastrada')
      await refresh()
    } catch (e) {
      toast.error(extrairMensagemErro(e))
    }
  }

  const criarReserva = async (params: {
    eventoId: string
    dataInicio: string
    dataFim: string
    itens: { itemEstoqueId: string; quantidade: number }[]
  }) => {
    try {
      await estoqueApi.criarReserva(params)
      toast.success('Reserva criada')
      await refresh()
    } catch (e) {
      toast.error(extrairMensagemErro(e))
    }
  }

  const confirmarReserva = async (id: string) => {
    try {
      await estoqueApi.confirmarReserva(id)
      toast.success('Reserva confirmada')
      await refresh()
    } catch (e) {
      toast.error(extrairMensagemErro(e))
    }
  }

  const cancelarReserva = async (id: string) => {
    try {
      await estoqueApi.cancelarReserva(id)
      toast.success('Reserva cancelada')
      await refresh()
    } catch (e) {
      toast.error(extrairMensagemErro(e))
    }
  }

  const gerarPrevisao = async (eventoId: string) => {
    try {
      const prev = await estoqueApi.gerarPrevisao(eventoId)
      setPrevisoes((p) => [prev, ...p.filter((x) => x.eventoId !== eventoId)])
      toast.success('Previsão gerada')
    } catch (e) {
      toast.error(extrairMensagemErro(e))
    }
  }

  const recalcularPrevisao = async (id: string) => {
    try {
      const prev = await estoqueApi.recalcularPrevisao(id)
      setPrevisoes((p) => p.map((x) => (x.id === id ? prev : x)))
      toast.success('Previsão recalculada')
    } catch (e) {
      toast.error(extrairMensagemErro(e))
    }
  }

  const ajustarPrevisao = async (id: string, ajustes: Record<string, number>, justificativa: string) => {
    try {
      const prev = await estoqueApi.ajustarPrevisao(id, ajustes, justificativa)
      setPrevisoes((p) => p.map((x) => (x.id === id ? prev : x)))
      toast.success('Previsão ajustada')
    } catch (e) {
      toast.error(extrairMensagemErro(e))
    }
  }

  const gerarCenario = async (periodoInicio: string, periodoFim: string) => {
    try {
      await estoqueApi.gerarCenario(periodoInicio, periodoFim)
      toast.success('Cenário gerado')
      await refresh()
    } catch (e) {
      toast.error(extrairMensagemErro(e))
    }
  }

  const aplicarCenario = async (id: string) => {
    try {
      await estoqueApi.aplicarCenario(id)
      toast.success('Redistribuição aplicada')
      await refresh()
    } catch (e) {
      toast.error(extrairMensagemErro(e))
    }
  }

  const registrarConsumo = async (params: {
    eventoId: string
    itens: { itemEstoqueId: string; categoriaConsumo: string; quantidadeConsumida: number }[]
  }) => {
    try {
      await estoqueApi.registrarConsumo(params)
      toast.success('Consumo registrado')
      await refresh()
    } catch (e) {
      toast.error(extrairMensagemErro(e))
    }
  }

  return (
    <EstoqueContext.Provider
      value={{
        loading,
        eventos,
        itens,
        substituicoes,
        reservas,
        previsoes,
        cenarios,
        consumos,
        refresh,
        cadastrarItem,
        editarItem,
        adicionarEstoque,
        desativarItem,
        cadastrarSubstituicao,
        criarReserva,
        confirmarReserva,
        cancelarReserva,
        gerarPrevisao,
        recalcularPrevisao,
        ajustarPrevisao,
        gerarCenario,
        aplicarCenario,
        registrarConsumo,
      }}
    >
      {children}
    </EstoqueContext.Provider>
  )
}

export function useEstoque() {
  const ctx = useContext(EstoqueContext)
  if (!ctx) throw new Error('useEstoque deve ser usado dentro de EstoqueProvider')
  return ctx
}
