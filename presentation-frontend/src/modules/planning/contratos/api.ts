/**
 * Contratos de API para integração com presentation-backend.
 * Substituir os throws por chamadas apiRequest() quando os controllers Spring existirem.
 */

import { apiRequest } from '../../../shared/api/client';
import { ApiNotImplementedError } from '../../../shared/api/errors';
import type { ContratoCreateDto, ContratoDto, ContratoUpdateDto } from './dto';

export const contratosApiPaths = {
	list: '/contratos',
	byEvento: (eventoId: string) => `/eventos/${eventoId}/contratos`,
	byId: (id: string) => `/contratos/${id}`,
	encerrar: (id: string) => `/contratos/${id}/encerrar`,
} as const;

export async function listarContratosApi(): Promise<ContratoDto[]> {
	throw new ApiNotImplementedError('contratosApi.listarContratos');
	// return apiRequest<ContratoDto[]>(contratosApiPaths.list);
}

export async function listarContratosPorEventoApi(_eventoId: string): Promise<ContratoDto[]> {
	throw new ApiNotImplementedError('contratosApi.listarContratosPorEvento');
	// return apiRequest<ContratoDto[]>(contratosApiPaths.byEvento(eventoId));
}

export async function buscarContratoApi(_id: string): Promise<ContratoDto> {
	throw new ApiNotImplementedError('contratosApi.buscarContrato');
	// return apiRequest<ContratoDto>(contratosApiPaths.byId(id));
}

export async function criarContratoApi(_payload: ContratoCreateDto): Promise<ContratoDto> {
	throw new ApiNotImplementedError('contratosApi.criarContrato');
	// return apiRequest<ContratoDto>(contratosApiPaths.list, {
	// 	method: 'POST',
	// 	body: JSON.stringify(payload),
	// });
}

export async function editarContratoApi(
	_id: string,
	_payload: ContratoUpdateDto
): Promise<ContratoDto> {
	throw new ApiNotImplementedError('contratosApi.editarContrato');
	// return apiRequest<ContratoDto>(contratosApiPaths.byId(id), {
	// 	method: 'PUT',
	// 	body: JSON.stringify(payload),
	// });
}

export async function encerrarContratoApi(_id: string): Promise<ContratoDto> {
	throw new ApiNotImplementedError('contratosApi.encerrarContrato');
	// return apiRequest<ContratoDto>(contratosApiPaths.encerrar(id), { method: 'POST' });
}

void apiRequest;
