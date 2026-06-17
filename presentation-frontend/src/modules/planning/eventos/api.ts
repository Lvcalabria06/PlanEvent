import { apiRequest } from '../../../shared/api/client';
import type {
	AlertaRiscoAlocacaoDto,
	CriarEventoDto,
	DefinirAlocacaoLocalDto,
	EditarEventoDto,
	EventoDto,
	RegistrarParametrosAlocacaoDto,
	ResultadoAnaliseAlocacaoDto,
} from './dto';

const base = (id: string) => `/eventos/${id}`;

export const eventosApiPaths = {
	list: '/eventos',
	byId: (id: string) => base(id),
	confirmar: (id: string) => `${base(id)}/confirmar-preparacao`,
	cancelar: (id: string) => `${base(id)}/cancelar`,
	analise: (id: string, tetoCusto: number) =>
		`${base(id)}/locais/analise?tetoCusto=${encodeURIComponent(String(tetoCusto))}`,
	parametros: (id: string) => `${base(id)}/parametros-alocacao`,
	definirAlocacao: (id: string) => `${base(id)}/definir-alocacao-local`,
	risco: (id: string) => `${base(id)}/risco-alocacao`,
	troca: (id: string) => `${base(id)}/troca-local-contingencia`,
} as const;

export function listarEventosApi(): Promise<EventoDto[]> {
	return apiRequest<EventoDto[]>(eventosApiPaths.list);
}

export function buscarEventoApi(id: string): Promise<EventoDto> {
	return apiRequest<EventoDto>(eventosApiPaths.byId(id));
}

export function criarEventoApi(payload: CriarEventoDto): Promise<EventoDto> {
	return apiRequest<EventoDto>(eventosApiPaths.list, {
		method: 'POST',
		body: JSON.stringify(payload),
	});
}

export function editarEventoApi(id: string, payload: EditarEventoDto): Promise<EventoDto> {
	return apiRequest<EventoDto>(eventosApiPaths.byId(id), {
		method: 'PUT',
		body: JSON.stringify(payload),
	});
}

export function confirmarPreparacaoApi(id: string): Promise<EventoDto> {
	return apiRequest<EventoDto>(eventosApiPaths.confirmar(id), { method: 'POST' });
}

export function cancelarEventoApi(id: string): Promise<void> {
	return apiRequest<void>(eventosApiPaths.cancelar(id), { method: 'POST' });
}

export function analisarLocaisApi(id: string, tetoCusto: number): Promise<ResultadoAnaliseAlocacaoDto> {
	return apiRequest<ResultadoAnaliseAlocacaoDto>(eventosApiPaths.analise(id, tetoCusto));
}

export function registrarParametrosApi(
	id: string,
	payload: RegistrarParametrosAlocacaoDto,
): Promise<EventoDto> {
	return apiRequest<EventoDto>(eventosApiPaths.parametros(id), {
		method: 'POST',
		body: JSON.stringify(payload),
	});
}

export function definirAlocacaoApi(id: string, payload: DefinirAlocacaoLocalDto): Promise<EventoDto> {
	return apiRequest<EventoDto>(eventosApiPaths.definirAlocacao(id), {
		method: 'POST',
		body: JSON.stringify(payload),
	});
}

export async function avaliarRiscoApi(id: string): Promise<AlertaRiscoAlocacaoDto | null> {
	const response = await fetch(`/api/v1${eventosApiPaths.risco(id)}`, {
		headers: { 'Content-Type': 'application/json' },
	});
	if (response.status === 204) return null;
	if (!response.ok) {
		const body = await response.json().catch(() => undefined);
		const message =
			typeof body === 'object' && body !== null && 'message' in body
				? String(body.message)
				: `Erro HTTP ${response.status}`;
		throw new Error(message);
	}
	return response.json() as Promise<AlertaRiscoAlocacaoDto>;
}

export function trocaContingenciaApi(
	id: string,
	payload: { novoLocalId: string; usuarioId: string; motivo: string },
): Promise<EventoDto> {
	return apiRequest<EventoDto>(eventosApiPaths.troca(id), {
		method: 'POST',
		body: JSON.stringify(payload),
	});
}
