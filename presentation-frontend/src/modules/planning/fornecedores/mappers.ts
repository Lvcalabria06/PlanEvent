import type { CategoriaServico } from '../constants';
import type { Fornecedor, FornecedorInput } from '../types';
import type { FornecedorCreateDto, FornecedorDto, FornecedorUpdateDto } from './dto';

function asCategoriaServico(value: string): CategoriaServico {
	return value as CategoriaServico;
}

/** Backend expõe um único campo `contato`; a UI usa campos separados. */
export function fromFornecedorDto(dto: FornecedorDto): Fornecedor {
	return {
		id: dto.id,
		nome: dto.nome,
		cnpj: dto.cnpj,
		categoriaServico: asCategoriaServico(dto.categoriaServico),
		contato: dto.contato,
		pessoaContato: dto.contato,
		email: dto.contato.includes('@') ? dto.contato : '',
		telefone: '',
		endereco: '',
		status: dto.status,
		criadoEm: dto.createdAt,
		atualizadoEm: dto.updatedAt,
	};
}

export function toFornecedorCreateDto(input: FornecedorInput): FornecedorCreateDto {
	return {
		nome: input.nome.trim(),
		cnpj: input.cnpj.trim(),
		categoriaServico: input.categoriaServico,
		contato: input.email.trim() || input.contato.trim() || input.telefone.trim(),
	};
}

export function toFornecedorUpdateDto(input: FornecedorInput): FornecedorUpdateDto {
	return toFornecedorCreateDto(input);
}
