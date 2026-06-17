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

// ── Turnos Operacionais ──────────────────────────────────────
export interface TurnoDTO {
  id: string;
  localId: string;
  nome: string;
  horaInicio: string;
  horaFim: string;
  diasDaSemana: string;
  status: 'ATIVO' | 'INATIVO';
  capacidade?: number;
  observacoes?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface TurnoCreateDTO {
  nome: string;
  horaInicio: string;
  horaFim: string;
  diasDaSemana: string;
  capacidade?: number;
  observacoes?: string;
}

export const listarTurnos = (localId: string): Promise<TurnoDTO[]> =>
  apiFetch<TurnoDTO[]>(`/locais/${localId}/turnos`);

export const cadastrarTurno = (localId: string, dto: TurnoCreateDTO): Promise<TurnoDTO> =>
  apiFetch<TurnoDTO>(`/locais/${localId}/turnos`, {
    method: 'POST',
    body: JSON.stringify(dto),
  });

export const editarTurno = (localId: string, turnoId: string, dto: TurnoCreateDTO): Promise<TurnoDTO> =>
  apiFetch<TurnoDTO>(`/locais/${localId}/turnos/${turnoId}`, {
    method: 'PUT',
    body: JSON.stringify(dto),
  });

export const desativarTurno = (localId: string, turnoId: string): Promise<void> =>
  apiFetch<void>(`/locais/${localId}/turnos/${turnoId}/desativar`, { method: 'PATCH' });

// ── Avaliação Contextual ─────────────────────────────────────
export interface AvaliacaoContextualDTO {
  id: string;
  eventoId: string;
  localId: string;
  tipoEvento: string;
  porteEvento: string;
  participantesContexto: number;
  notasPorCriterio: Record<string, number>;
  notaFinal: number;
  justificativa: string;
  usuarioResponsavel: string;
  dataHoraRegistro: string;
}

export interface AvaliacaoCreateDTO {
  eventoId: string;
  notasPorCriterio: Record<string, number>;
  justificativa: string;
  usuarioResponsavel: string;
}

export interface ResumoDesempenhoDTO {
  notaMediaGeral: number;
  notaMediaContexto: number;
  totalAvaliacoesLocal: number;
  totalAvaliacoesContexto: number;
  baixaBaseHistoricaContexto: boolean;
  classificacaoGeral: string;
  classificacaoContextual: string;
}

export const listarAvaliacoes = (localId: string): Promise<AvaliacaoContextualDTO[]> =>
  apiFetch<AvaliacaoContextualDTO[]>(`/locais/${localId}/avaliacoes`);

export const registrarAvaliacao = (localId: string, dto: AvaliacaoCreateDTO): Promise<AvaliacaoContextualDTO> =>
  apiFetch<AvaliacaoContextualDTO>(`/locais/${localId}/avaliacoes`, {
    method: 'POST',
    body: JSON.stringify(dto),
  });

export const consultarResumoDesempenho = (
  localId: string,
  tipoEvento: string,
  porteEvento: string
): Promise<ResumoDesempenhoDTO> =>
  apiFetch<ResumoDesempenhoDTO>(
    `/locais/${localId}/avaliacoes/resumo?tipoEvento=${tipoEvento}&porteEvento=${porteEvento}`
  );
