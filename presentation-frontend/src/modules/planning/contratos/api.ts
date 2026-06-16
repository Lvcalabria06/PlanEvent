import { apiRequest } from '../../../shared/api/client';
import type { ContratoCreateDto, ContratoDto, ContratoUpdateDto } from './dto';

export const contratosApiPaths = {
	list: '/contratos',
	byEvento: (eventoId: string) => `/contratos/por-evento/${eventoId}`,
	byId: (id: string) => `/contratos/${id}`,
	encerrar: (id: string) => `/contratos/${id}/encerrar`,
} as const;

export function listarContratosApi(): Promise<ContratoDto[]> {
	return apiRequest<ContratoDto[]>(contratosApiPaths.list);
}

export function listarContratosPorEventoApi(eventoId: string): Promise<ContratoDto[]> {
	return apiRequest<ContratoDto[]>(contratosApiPaths.byEvento(eventoId));
}

export function buscarContratoApi(id: string): Promise<ContratoDto> {
	return apiRequest<ContratoDto>(contratosApiPaths.byId(id));
}

export function criarContratoApi(payload: ContratoCreateDto): Promise<ContratoDto> {
	return apiRequest<ContratoDto>(contratosApiPaths.list, {
		method: 'POST',
		body: JSON.stringify(payload),
	});
}

export function editarContratoApi(id: string, payload: ContratoUpdateDto): Promise<ContratoDto> {
	return apiRequest<ContratoDto>(contratosApiPaths.byId(id), {
		method: 'PUT',
		body: JSON.stringify(payload),
	});
}

export function encerrarContratoApi(id: string): Promise<ContratoDto> {
	return apiRequest<ContratoDto>(contratosApiPaths.encerrar(id), { method: 'POST' });
}
