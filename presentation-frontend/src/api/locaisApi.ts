import { apiFetch } from './http';

export type StatusLocal = 'ATIVO' | 'INATIVO' | 'EM_MANUTENCAO';

export interface LocalDTO {
  id: string;
  nome: string;
  capacidade: number;
  endereco: string;
  tipo: string;
  infraestrutura: string;
  custo: number;
  status: StatusLocal;
  updatedAt: string;
  createdAt?: string;
  manutencoes?: ManutencaoDTO[];
  reservas?: ReservaDTO[];
}

export interface LocalCreateDTO {
  nome: string;
  capacidade: number;
  endereco: string;
  tipo: string;
  infraestrutura: string;
  custo: number;
}

export interface ManutencaoDTO {
  id: string;
  localId: string;
  dataInicio: string;
  dataFim: string;
  responsavel: string;
  descricao?: string;
  createdAt?: string;
  updatedAt?: string;
  registradoPor?: string;
}

export interface ManutencaoCreateDTO {
  dataInicio: string;
  dataFim: string;
  responsavel: string;
  descricao?: string;
}

export interface ReservaDTO {
  id: string;
  localId: string;
  eventoNome: string;
  dataInicio: string;
  dataFim: string;
  status: string;
}

// ── Locais ──────────────────────────────────────────────────
export const listarLocais = (): Promise<LocalDTO[]> =>
  apiFetch<LocalDTO[]>('/locais');

export const buscarLocal = (id: string): Promise<LocalDTO> =>
  apiFetch<LocalDTO>(`/locais/${id}`);

export const cadastrarLocal = (dto: LocalCreateDTO): Promise<LocalDTO> =>
  apiFetch<LocalDTO>('/locais', {
    method: 'POST',
    body: JSON.stringify(dto),
  });

export const editarLocal = (id: string, dto: LocalCreateDTO): Promise<LocalDTO> =>
  apiFetch<LocalDTO>(`/locais/${id}`, {
    method: 'PUT',
    body: JSON.stringify(dto),
  });

export const desativarLocal = (id: string): Promise<void> =>
  apiFetch<void>(`/locais/${id}/desativar`, { method: 'PATCH' });

// ── Manutenções ──────────────────────────────────────────────
export const listarManutencoes = (localId: string): Promise<ManutencaoDTO[]> =>
  apiFetch<ManutencaoDTO[]>(`/locais/${localId}/manutencoes`);

export const cadastrarManutencao = (
  localId: string,
  dto: ManutencaoCreateDTO
): Promise<ManutencaoDTO> =>
  apiFetch<ManutencaoDTO>(`/locais/${localId}/manutencoes`, {
    method: 'POST',
    body: JSON.stringify(dto),
  });

export const editarManutencao = (
  localId: string,
  manutencaoId: string,
  dto: ManutencaoCreateDTO
): Promise<ManutencaoDTO> =>
  apiFetch<ManutencaoDTO>(`/locais/${localId}/manutencoes/${manutencaoId}`, {
    method: 'PUT',
    body: JSON.stringify(dto),
  });

export const removerManutencao = (
  localId: string,
  manutencaoId: string
): Promise<void> =>
  apiFetch<void>(`/locais/${localId}/manutencoes/${manutencaoId}`, {
    method: 'DELETE',
  });
