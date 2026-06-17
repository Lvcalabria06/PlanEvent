export type EventoRef = { id: string; nome: string; dataEvento?: string }

export function idEventoCurto(id: string): string {
  return id.length > 8 ? `${id.slice(0, 8)}…` : id
}

export function formatEventoLabel(id: string, eventos: EventoRef[]): string {
  const ev = eventos.find((e) => e.id === id)
  const idFmt = idEventoCurto(id)
  if (ev?.nome?.trim()) {
    return `${ev.nome.trim()} (${idFmt})`
  }
  return `Evento ${idFmt}`
}

export type ItemEstoque = {
  id: string
  nome: string
  quantidadeTotal: number
  quantidadeDisponivel: number
  ativo: boolean
  dataCriacao: string
  dataAtualizacao: string
}

export type ItemSubstituicao = {
  id: string
  itemOriginalId: string
  itemSubstitutoId: string
  fatorEquivalencia: number
}

export type ItemReserva = {
  id: string
  itemEstoqueId: string
  quantidade: number
}

export type ReservaEstoque = {
  id: string
  eventoId: string
  dataInicio: string
  dataFim: string
  status: string
  itensReservados: ItemReserva[]
}

export type ItemPrevisao = {
  id?: string
  itemEstoqueId: string
  categoriaConsumo: string
  quantidadeEstimada: number
  quantidadeMinima: number
  quantidadeMaxima: number
  quantidadeFinal: number
  explicacaoCalculo: string
}

export type RegistroHistoricoPrevisao = {
  id: string
  versao: number
  tipoRegistro: string
  usuarioResponsavelId: string
  dataHora: string
  justificativa: string
  itens: { itemEstoqueId: string; categoriaConsumo: string; quantidadeEstimada: number; quantidadeFinal: number }[]
}

export type PrevisaoConsumo = {
  id: string
  eventoId: string
  geradoPorUsuarioId: string
  dataGeracao: string
  statusHistorico: string
  fallbackUtilizado: boolean
  invalidada: boolean
  versaoAtual: number
  totalEventosBase: number
  itens: ItemPrevisao[]
  historicoRegistros: RegistroHistoricoPrevisao[]
}

export type AlocacaoRedistribuicao = {
  id?: string
  eventoId: string
  itemEstoqueId: string
  quantidadeAnterior: number
  quantidadeRedistribuida: number
  itemSubstitutoId?: string | null
  quantidadeSubstituto: number
}

export type RegistroHistoricoCenario = {
  id: string
  usuarioResponsavelId: string
  dataHora: string
  descricao: string
  alocacoesSnapshot: AlocacaoRedistribuicao[]
}

export type CenarioRedistribuicao = {
  id: string
  dataCriacao: string
  geradoPorUsuarioId: string
  periodoInicio: string
  periodoFim: string
  status: string
  alocacoesAtuais: AlocacaoRedistribuicao[]
  alocacoesOtimizadas: AlocacaoRedistribuicao[]
  impactosPorEvento: {
    eventoId: string
    itensImpactados: {
      itemEstoqueId: string
      quantidadeAnterior: number
      quantidadeRedistribuida: number
      deficit: number
      excesso: number
    }[]
  }[]
  historico: RegistroHistoricoCenario[]
  dataAplicacao?: string | null
  aplicadoPorUsuarioId?: string | null
}

export type ConsumoEvento = {
  id: string
  eventoId: string
  registradoPorUsuarioId: string
  dataRegistro: string
  valido: boolean
  itensConsumidos: { itemEstoqueId: string; categoriaConsumo: string; quantidadeConsumida: number }[]
}

export type EstoqueView = 'itens' | 'reservas' | 'previsao' | 'redistribuicao' | 'consumo'
