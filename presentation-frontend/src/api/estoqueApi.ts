import { apiFetch } from './http';

export interface ItemEstoqueDto {
  id: string;
  nome: string;
  quantidadeTotal: number;
  quantidadeDisponivel: number;
  ativo: boolean;
  dataCriacao: string;
  dataAtualizacao: string;
}

export interface ItemSubstituicaoDto {
  id: string;
  itemOriginalId: string;
  itemSubstitutoId: string;
  fatorEquivalencia: number;
}

export interface ItemReservaDto {
  id: string;
  itemEstoqueId: string;
  quantidade: number;
}

export interface ReservaEstoqueDto {
  id: string;
  eventoId: string;
  dataInicio: string;
  dataFim: string;
  status: string;
  itensReservados: ItemReservaDto[];
}

export interface ItemPrevisaoDto {
  id?: string;
  itemEstoqueId: string;
  categoriaConsumo: string;
  quantidadeEstimada: number;
  quantidadeMinima: number;
  quantidadeMaxima: number;
  quantidadeFinal: number;
  explicacaoCalculo: string;
}

export interface RegistroHistoricoPrevisaoDto {
  id: string;
  versao: number;
  tipoRegistro: string;
  usuarioResponsavelId: string;
  dataHora: string;
  justificativa: string;
  itens: {
    itemEstoqueId: string;
    categoriaConsumo: string;
    quantidadeEstimada: number;
    quantidadeFinal: number;
  }[];
}

export interface PrevisaoConsumoDto {
  id: string;
  eventoId: string;
  geradoPorUsuarioId: string;
  dataGeracao: string;
  statusHistorico: string;
  fallbackUtilizado: boolean;
  invalidada: boolean;
  versaoAtual: number;
  totalEventosBase: number;
  itens: ItemPrevisaoDto[];
  historicoRegistros: RegistroHistoricoPrevisaoDto[];
}

export interface AlocacaoRedistribuicaoDto {
  id?: string;
  eventoId: string;
  itemEstoqueId: string;
  quantidadeAnterior: number;
  quantidadeRedistribuida: number;
  itemSubstitutoId?: string | null;
  quantidadeSubstituto: number;
}

export interface RegistroHistoricoCenarioDto {
  id: string;
  usuarioResponsavelId: string;
  dataHora: string;
  descricao: string;
  alocacoesSnapshot: AlocacaoRedistribuicaoDto[];
}

export interface CenarioRedistribuicaoDto {
  id: string;
  dataCriacao: string;
  geradoPorUsuarioId: string;
  periodoInicio: string;
  periodoFim: string;
  status: string;
  alocacoesAtuais: AlocacaoRedistribuicaoDto[];
  alocacoesOtimizadas: AlocacaoRedistribuicaoDto[];
  impactosPorEvento: {
    eventoId: string;
    itensImpactados: {
      itemEstoqueId: string;
      quantidadeAnterior: number;
      quantidadeRedistribuida: number;
      deficit: number;
      excesso: number;
    }[];
  }[];
  historico: RegistroHistoricoCenarioDto[];
  dataAplicacao?: string | null;
  aplicadoPorUsuarioId?: string | null;
}

export interface ConsumoEventoDto {
  id: string;
  eventoId: string;
  registradoPorUsuarioId: string;
  dataRegistro: string;
  valido: boolean;
  itensConsumidos: {
    itemEstoqueId: string;
    categoriaConsumo: string;
    quantidadeConsumida: number;
  }[];
}

const RESPONSAVEL_ID = 'gestor@empresa.com';

export const estoqueApi = {
  listarItens: () => apiFetch<ItemEstoqueDto[]>('/itens-estoque'),

  cadastrarItem: (nome: string, quantidadeTotal: number) =>
    apiFetch<ItemEstoqueDto>('/itens-estoque', {
      method: 'POST',
      body: JSON.stringify({ nome, quantidadeTotal }),
    }),

  editarItem: (id: string, nome: string, quantidadeTotal: number) =>
    apiFetch<ItemEstoqueDto>(`/itens-estoque/${id}`, {
      method: 'PUT',
      body: JSON.stringify({ nome, quantidadeTotal }),
    }),

  adicionarEstoque: (id: string, quantidade: number) =>
    apiFetch<ItemEstoqueDto>(`/itens-estoque/${id}/adicionar`, {
      method: 'POST',
      body: JSON.stringify({ quantidade }),
    }),

  desativarItem: (id: string) =>
    apiFetch<void>(`/itens-estoque/${id}/desativar`, { method: 'POST' }),

  listarSubstituicoes: () => apiFetch<ItemSubstituicaoDto[]>('/itens-estoque/substituicoes'),

  cadastrarSubstituicao: (itemOriginalId: string, itemSubstitutoId: string, fatorEquivalencia: number) =>
    apiFetch<ItemSubstituicaoDto>('/itens-estoque/substituicoes', {
      method: 'POST',
      body: JSON.stringify({ itemOriginalId, itemSubstitutoId, fatorEquivalencia }),
    }),

  listarReservas: () => apiFetch<ReservaEstoqueDto[]>('/reservas-estoque'),

  criarReserva: (payload: {
    eventoId: string;
    dataInicio: string;
    dataFim: string;
    itens: { itemEstoqueId: string; quantidade: number }[];
  }) =>
    apiFetch<ReservaEstoqueDto>('/reservas-estoque', {
      method: 'POST',
      body: JSON.stringify(payload),
    }),

  confirmarReserva: (id: string) =>
    apiFetch<ReservaEstoqueDto>(`/reservas-estoque/${id}/confirmar`, { method: 'POST' }),

  cancelarReserva: (id: string) =>
    apiFetch<void>(`/reservas-estoque/${id}/cancelar`, { method: 'POST' }),

  listarPrevisoes: () => apiFetch<PrevisaoConsumoDto[]>('/previsoes-consumo'),

  buscarPrevisaoPorEvento: (eventoId: string) =>
    apiFetch<PrevisaoConsumoDto>(`/previsoes-consumo/por-evento/${eventoId}`),

  gerarPrevisao: (eventoId: string) =>
    apiFetch<PrevisaoConsumoDto>('/previsoes-consumo', {
      method: 'POST',
      body: JSON.stringify({ eventoId, usuarioId: RESPONSAVEL_ID }),
    }),

  ajustarPrevisao: (
    id: string,
    quantidadesAjustadas: Record<string, number>,
    justificativa: string,
  ) =>
    apiFetch<PrevisaoConsumoDto>(`/previsoes-consumo/${id}/ajustar`, {
      method: 'PUT',
      body: JSON.stringify({
        quantidadesAjustadas,
        usuarioId: RESPONSAVEL_ID,
        justificativa,
      }),
    }),

  recalcularPrevisao: (id: string) =>
    apiFetch<PrevisaoConsumoDto>(`/previsoes-consumo/${id}/recalcular?usuarioId=${encodeURIComponent(RESPONSAVEL_ID)}`, {
      method: 'POST',
    }),

  listarCenarios: () => apiFetch<CenarioRedistribuicaoDto[]>('/cenarios-redistribuicao'),

  gerarCenario: (periodoInicio: string, periodoFim: string) =>
    apiFetch<CenarioRedistribuicaoDto>('/cenarios-redistribuicao', {
      method: 'POST',
      body: JSON.stringify({
        usuarioId: RESPONSAVEL_ID,
        periodoInicio,
        periodoFim,
      }),
    }),

  aplicarCenario: (id: string) =>
    apiFetch<CenarioRedistribuicaoDto>(`/cenarios-redistribuicao/${id}/aplicar`, {
      method: 'POST',
      body: JSON.stringify({ usuarioId: RESPONSAVEL_ID }),
    }),

  listarConsumos: () => apiFetch<ConsumoEventoDto[]>('/consumos-evento'),

  registrarConsumo: (payload: {
    eventoId: string;
    itens: { itemEstoqueId: string; categoriaConsumo: string; quantidadeConsumida: number }[];
  }) =>
    apiFetch<ConsumoEventoDto>('/consumos-evento', {
      method: 'POST',
      body: JSON.stringify({ ...payload, usuarioId: RESPONSAVEL_ID }),
    }),
};
