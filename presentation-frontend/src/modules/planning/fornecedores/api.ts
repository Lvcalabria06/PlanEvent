/**
 * Contratos de API para integração com presentation-backend.
 * Substituir os throws por chamadas apiRequest() quando os controllers Spring existirem.
 */

import { apiRequest } from '../../../shared/api/client';
import { ApiNotImplementedError } from '../../../shared/api/errors';
import type { FornecedorCreateDto, FornecedorDto, FornecedorUpdateDto } from './dto';

export const fornecedoresApiPaths = {
	list: '/fornecedores',
	byId: (id: string) => `/fornecedores/${id}`,
	desativar: (id: string) => `/fornecedores/${id}/desativar`,
} as const;

export async function listarFornecedoresApi(): Promise<FornecedorDto[]> {
	throw new ApiNotImplementedError('fornecedoresApi.listarFornecedores');
	// return apiRequest<FornecedorDto[]>(fornecedoresApiPaths.list);
}

export async function buscarFornecedorApi(_id: string): Promise<FornecedorDto> {
	throw new ApiNotImplementedError('fornecedoresApi.buscarFornecedor');
	// return apiRequest<FornecedorDto>(fornecedoresApiPaths.byId(id));
}

export async function cadastrarFornecedorApi(_payload: FornecedorCreateDto): Promise<FornecedorDto> {
	throw new ApiNotImplementedError('fornecedoresApi.cadastrarFornecedor');
	// return apiRequest<FornecedorDto>(fornecedoresApiPaths.list, {
	// 	method: 'POST',
	// 	body: JSON.stringify(payload),
	// });
}

export async function editarFornecedorApi(
	_id: string,
	_payload: FornecedorUpdateDto
): Promise<FornecedorDto> {
	throw new ApiNotImplementedError('fornecedoresApi.editarFornecedor');
	// return apiRequest<FornecedorDto>(fornecedoresApiPaths.byId(id), {
	// 	method: 'PUT',
	// 	body: JSON.stringify(payload),
	// });
}

export async function desativarFornecedorApi(_id: string): Promise<void> {
	throw new ApiNotImplementedError('fornecedoresApi.desativarFornecedor');
	// await apiRequest<void>(fornecedoresApiPaths.desativar(id), { method: 'POST' });
}

// Evita lint de imports não usados enquanto stubs estão ativos
void apiRequest;
