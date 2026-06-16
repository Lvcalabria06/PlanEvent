import { apiFetch } from './http';

export interface EventoResumo {
  id: string;
  nome: string;
  concluido: boolean;
}

export interface FornecedorResumo {
  id: string;
  nome: string;
  categoriaServico: string;
}

export interface Despesa {
  id: string;
  eventoId: string;
  categoria: string;
  fornecedorId: string;
  valor: number;
  data: string;
  lancadoPorUsuarioId: string;
  dataHoraLancamento: string;
  status: string;
  aprovadorId?: string;
  motivoRejeicao?: string;
}

export interface Desvio {
  categoria: string;
  valorPrevisto: number;
  valorRealizado: number;
  desvioPercentual: number;
  classificacao: string;
}

export interface Relatorio {
  id: string | null;
  eventoId: string;
  geradoPorUsuarioId: string;
  dataGeracao: string | null;
  tipo: string | null;
  motivoNovaVersaoOficial: string | null;
  totalGeralPrevisto: number;
  totalGeralRealizado: number;
  itensPorCategoria: {
    categoria: string;
    valorPrevisto: number;
    valorRealizado: number;
    percentualVariacao: number;
    classificacao: string;
  }[];
  saudeFinanceira: { score: number; classificacao: string };
  comparativo: {
    relatorioAnteriorId: string;
    variacaoScore: number;
    tendencia: string;
    categoriasComPiora: string[];
  } | null;
  coberturaContratual: {
    totalDespesasAtivas: number;
    despesasCobertas: number;
    despesasDescobertas: number;
    percentualCobertura: number;
  } | null;
  recomendacoes: { tipo: string; mensagem: string; categoriaRelacionada: string | null }[];
  conteudo: string;
}

export interface Simulacao {
  id: string;
  preview: Relatorio;
  criadaEm: string;
}

const base = (eventoId: string) => `/eventos/${eventoId}/financeiro`;

export const financeiroApi = {
  listarEventos: () => apiFetch<EventoResumo[]>('/eventos'),
  listarFornecedores: () => apiFetch<FornecedorResumo[]>('/fornecedores'),

  listarDespesas: (eventoId: string, params?: { categoria?: string; fornecedorId?: string }) => {
    const q = new URLSearchParams();
    if (params?.categoria) q.set('categoria', params.categoria);
    if (params?.fornecedorId) q.set('fornecedorId', params.fornecedorId);
    const qs = q.toString();
    return apiFetch<Despesa[]>(`${base(eventoId)}/despesas${qs ? `?${qs}` : ''}`);
  },

  listarPendentes: (eventoId: string) =>
    apiFetch<Despesa[]>(`${base(eventoId)}/despesas/pendentes`),

  listarDesvios: (eventoId: string) =>
    apiFetch<Desvio[]>(`${base(eventoId)}/despesas/desvios`),

  registrarDespesa: (
    eventoId: string,
    body: { categoria: string; fornecedorId: string; valor: number; data: string }
  ) =>
    apiFetch<Despesa>(`${base(eventoId)}/despesas`, {
      method: 'POST',
      body: JSON.stringify(body),
    }),

  atualizarDespesa: (eventoId: string, despesaId: string, body: { valor: number; data?: string }) =>
    apiFetch<Despesa>(`${base(eventoId)}/despesas/${despesaId}`, {
      method: 'PUT',
      body: JSON.stringify(body),
    }),

  excluirDespesa: (eventoId: string, despesaId: string) =>
    apiFetch<void>(`${base(eventoId)}/despesas/${despesaId}`, { method: 'DELETE' }),

  aprovarDespesa: (eventoId: string, despesaId: string, aprovadorId: string) =>
    apiFetch<Despesa>(`${base(eventoId)}/despesas/${despesaId}/aprovar`, {
      method: 'POST',
      body: JSON.stringify({ aprovadorId }),
    }),

  rejeitarDespesa: (eventoId: string, despesaId: string, aprovadorId: string, motivo: string) =>
    apiFetch<Despesa>(`${base(eventoId)}/despesas/${despesaId}/rejeitar`, {
      method: 'POST',
      body: JSON.stringify({ aprovadorId, motivo }),
    }),

  listarRelatorios: (eventoId: string) =>
    apiFetch<Relatorio[]>(`${base(eventoId)}/relatorios`),

  buscarRelatorio: (eventoId: string, relatorioId: string) =>
    apiFetch<Relatorio>(`${base(eventoId)}/relatorios/${relatorioId}`),

  simularRelatorio: (eventoId: string) =>
    apiFetch<Simulacao>(`${base(eventoId)}/relatorios/simular`, { method: 'POST' }),

  confirmarSimulacao: (
    eventoId: string,
    simulacaoId: string,
    body: { tipo: string; motivoNovaVersaoOficial?: string }
  ) =>
    apiFetch<Relatorio>(`${base(eventoId)}/relatorios/simulacoes/${simulacaoId}/confirmar`, {
      method: 'POST',
      body: JSON.stringify(body),
    }),

  gerarPreliminar: (eventoId: string) =>
    apiFetch<Relatorio>(`${base(eventoId)}/relatorios/preliminar`, { method: 'POST' }),

  gerarOficial: (eventoId: string, motivoNovaVersaoOficial?: string) =>
    apiFetch<Relatorio>(`${base(eventoId)}/relatorios/oficial`, {
      method: 'POST',
      body: JSON.stringify({ motivoNovaVersaoOficial: motivoNovaVersaoOficial ?? null }),
    }),
};
