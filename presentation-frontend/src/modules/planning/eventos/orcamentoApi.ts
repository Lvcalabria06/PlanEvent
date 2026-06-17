import { apiRequest } from '../../../shared/api/client';
import { ApiError } from '../../../shared/api/errors';

export interface OrcamentoEventoDto {
	id: string;
	eventoId: string;
	valorTotal: number;
	dataCriacao: string;
}

export interface CategoriaOrcamentoDto {
	id: string;
	orcamentoId: string;
	categoria: string;
	valorPrevisto: number;
}

const base = (eventoId: string) => `/eventos/${eventoId}/financeiro/orcamento`;

export async function buscarOrcamentoApi(eventoId: string): Promise<OrcamentoEventoDto | null> {
	try {
		return await apiRequest<OrcamentoEventoDto>(base(eventoId));
	} catch (error) {
		if (error instanceof ApiError && error.status === 400) {
			return null;
		}
		throw error;
	}
}

export function listarCategoriasOrcamentoApi(eventoId: string): Promise<CategoriaOrcamentoDto[]> {
	return apiRequest<CategoriaOrcamentoDto[]>(`${base(eventoId)}/categorias`);
}

export function criarOrcamentoApi(eventoId: string, valorTotal: number): Promise<OrcamentoEventoDto> {
	return apiRequest<OrcamentoEventoDto>(base(eventoId), {
		method: 'POST',
		body: JSON.stringify({ valorTotal }),
	});
}

export function adicionarCategoriaOrcamentoApi(
	eventoId: string,
	categoria: string,
	valorPrevisto: number,
): Promise<CategoriaOrcamentoDto> {
	return apiRequest<CategoriaOrcamentoDto>(`${base(eventoId)}/categorias`, {
		method: 'POST',
		body: JSON.stringify({ categoria, valorPrevisto }),
	});
}

export function atualizarCategoriaOrcamentoApi(
	eventoId: string,
	categoria: string,
	valorPrevisto: number,
): Promise<CategoriaOrcamentoDto> {
	return apiRequest<CategoriaOrcamentoDto>(`${base(eventoId)}/categorias/${categoria}`, {
		method: 'PUT',
		body: JSON.stringify({ valorPrevisto }),
	});
}

/**
 * Persiste orçamento previsto (total + categorias) via API do módulo Financeiro.
 */
export async function persistirOrcamentoEvento(
	eventoId: string,
	valorTotal: number,
	valoresPorCategoria: Record<string, number>,
	orcamentoExistente: OrcamentoEventoDto | null,
	categoriasExistentes: CategoriaOrcamentoDto[],
): Promise<void> {
	if (!orcamentoExistente) {
		await criarOrcamentoApi(eventoId, valorTotal);
		for (const [categoria, valor] of Object.entries(valoresPorCategoria)) {
			if (valor > 0) {
				await adicionarCategoriaOrcamentoApi(eventoId, categoria, valor);
			}
		}
		return;
	}

	for (const [categoria, valor] of Object.entries(valoresPorCategoria)) {
		if (valor <= 0) continue;
		const existente = categoriasExistentes.find(c => c.categoria === categoria);
		if (existente) {
			await atualizarCategoriaOrcamentoApi(eventoId, categoria, valor);
		} else {
			await adicionarCategoriaOrcamentoApi(eventoId, categoria, valor);
		}
	}
}
