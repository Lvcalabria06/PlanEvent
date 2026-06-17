import { apiRequest } from '../shared/api/client';
import type { ContratoDto } from '../modules/planning/contratos/dto';

export type StatusConciliacao = 'COBERTA' | 'DESCOBERTA';
export type MetodoConciliacao = 'AUTOMATICO' | 'MANUAL';

export interface DespesaResumoConciliacao {
  id: string;
  eventoId: string;
  categoria: string;
  fornecedorId: string;
  valor: number;
  data: string;
  status: string;
}

export interface VinculoConciliacao {
  id: string;
  eventoId: string;
  despesaId: string;
  contratoId: string;
  metodo: MetodoConciliacao;
  responsavelId: string;
  dataConciliacao: string;
  createdAt: string;
  updatedAt: string;
}

export interface ItemRelatorioConciliacao {
  despesaId: string;
  contratoId: string | null;
  status: StatusConciliacao;
  metodo: MetodoConciliacao | null;
}

export interface RelatorioConciliacao {
  id: string;
  eventoId: string;
  responsavelId: string;
  dataGeracao: string;
  itens: ItemRelatorioConciliacao[];
}

const RESPONSAVEL_ID = 'gestor@empresa.com';
const base = '/conciliacao';

export const conciliacaoApi = {
  executarAutomatica: (eventoId: string) =>
    apiRequest<void>(`${base}/automatica`, {
      method: 'POST',
      body: JSON.stringify({ eventoId, responsavelId: RESPONSAVEL_ID }),
    }),

  vincularManualmente: (despesaId: string, contratoId: string) =>
    apiRequest<VinculoConciliacao>(`${base}/vincular`, {
      method: 'POST',
      body: JSON.stringify({ despesaId, contratoId, responsavelId: RESPONSAVEL_ID }),
    }),

  listarDespesasDescobertas: (eventoId: string) =>
    apiRequest<DespesaResumoConciliacao[]>(`${base}/eventos/${eventoId}/despesas-descobertas`),

  listarContratosExtrapolados: (eventoId: string) =>
    apiRequest<ContratoDto[]>(`${base}/eventos/${eventoId}/contratos-extrapolados`),

  listarVinculos: (eventoId: string) =>
    apiRequest<VinculoConciliacao[]>(`${base}/eventos/${eventoId}/vinculos`),

  gerarRelatorio: (eventoId: string) =>
    apiRequest<RelatorioConciliacao>(`${base}/eventos/${eventoId}/relatorio`, {
      method: 'POST',
      body: JSON.stringify({ responsavelId: RESPONSAVEL_ID }),
    }),
};
