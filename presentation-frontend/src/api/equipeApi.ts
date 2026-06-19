const BASE = '/api';

export interface FuncionarioApiDto {
  id: string;
  nome: string;
  cargo: string;
  disponibilidade: string;
  ativo: boolean;
  competencias?: string[];
  createdAt: string;
  updatedAt: string;
}

interface MembroEquipeApiDto {
  id: string;
  funcionarioId: string;
  lider: boolean;
  dataEntrada: string;
}

export interface EquipeApiDto {
  id: string;
  eventoId: string;
  nome: string;
  membros: MembroEquipeApiDto[];
  dataCriacao: string;
  dataAtualizacao: string;
}

async function req<T>(path: string, options?: RequestInit): Promise<T> {
  const res = await fetch(`${BASE}${path}`, {
    headers: { 'Content-Type': 'application/json', ...options?.headers },
    ...options,
  });
  if (!res.ok) throw new Error(`HTTP ${res.status}`);
  if (res.status === 204) return undefined as T;
  return res.json() as Promise<T>;
}

// --- Funcionários ---
export const listarFuncionarios = (): Promise<FuncionarioApiDto[]> =>
  req('/funcionarios');

export const cadastrarFuncionario = (body: { nome: string; cargo: string; disponibilidade: string; competencias: string[] }): Promise<FuncionarioApiDto> =>
  req('/funcionarios', { method: 'POST', body: JSON.stringify(body) });

export const editarFuncionario = (id: string, body: { nome: string; cargo: string; disponibilidade: string; competencias: string[] }): Promise<FuncionarioApiDto> =>
  req(`/funcionarios/${id}`, { method: 'PUT', body: JSON.stringify(body) });

export const inativarFuncionario = (id: string): Promise<void> =>
  req(`/funcionarios/${id}`, { method: 'DELETE' });

export const filtrarFuncionarios = (expressao: string): Promise<FuncionarioApiDto[]> =>
  req('/funcionarios/filtrar', { method: 'POST', body: JSON.stringify({ expressao }) });

// --- Equipes ---
export const listarEquipesPorEvento = (eventoId: string): Promise<EquipeApiDto[]> =>
  req(`/equipes/por-evento/${eventoId}`);

export const criarEquipe = (body: { eventoId: string; nome: string; membros: { funcionarioId: string; lider: boolean }[] }): Promise<EquipeApiDto> =>
  req('/equipes', { method: 'POST', body: JSON.stringify(body) });

export const editarEquipe = (id: string, body: { nome: string; membros: { funcionarioId: string; lider: boolean }[] }): Promise<EquipeApiDto> =>
  req(`/equipes/${id}`, { method: 'PUT', body: JSON.stringify(body) });

export const removerEquipe = (id: string): Promise<void> =>
  req(`/equipes/${id}`, { method: 'DELETE' });

export const filtrarMembros = (equipeId: string, expressao: string): Promise<MembroEquipeApiDto[]> =>
  req(`/equipes/${equipeId}/filtrar-membros`, { method: 'POST', body: JSON.stringify({ expressao }) });
