/** Payload esperado do presentation-backend (espelha domain.fornecedor). */
export interface FornecedorDto {
	id: string;
	nome: string;
	cnpj: string;
	categoriaServico: string;
	contato: string;
	status: 'ATIVO' | 'INATIVO';
	createdAt: string;
	updatedAt: string;
}

export interface FornecedorCreateDto {
	nome: string;
	cnpj: string;
	categoriaServico: string;
	contato: string;
}

export type FornecedorUpdateDto = FornecedorCreateDto;
