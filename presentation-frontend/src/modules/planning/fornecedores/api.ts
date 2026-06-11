import { apiRequest } from '../../../shared/api/client';
import type { FornecedorCreateDto, FornecedorDto, FornecedorUpdateDto } from './dto';

export const fornecedoresApiPaths = {
	list: '/fornecedores',
	byId: (id: string) => `/fornecedores/${id}`,
	desativar: (id: string) => `/fornecedores/${id}/desativar`,
} as const;

export function listarFornecedoresApi(): Promise<FornecedorDto[]> {
	return apiRequest<FornecedorDto[]>(fornecedoresApiPaths.list);
}

export function buscarFornecedorApi(id: string): Promise<FornecedorDto> {
	return apiRequest<FornecedorDto>(fornecedoresApiPaths.byId(id));
}

export function cadastrarFornecedorApi(payload: FornecedorCreateDto): Promise<FornecedorDto> {
	return apiRequest<FornecedorDto>(fornecedoresApiPaths.list, {
		method: 'POST',
		body: JSON.stringify(payload),
	});
}

export function editarFornecedorApi(id: string, payload: FornecedorUpdateDto): Promise<FornecedorDto> {
	return apiRequest<FornecedorDto>(fornecedoresApiPaths.byId(id), {
		method: 'PUT',
		body: JSON.stringify(payload),
	});
}

export function desativarFornecedorApi(id: string): Promise<void> {
	return apiRequest<void>(fornecedoresApiPaths.desativar(id), { method: 'POST' });
}
