import type { CategoriaServico } from '../constants';
import type { Contrato, ContratoInput, StatusContratoUi } from '../types';
import type { ContratoCreateDto, ContratoDto, ContratoUpdateDto, StatusContratoDomain } from './dto';

function asCategoriaServico(value: string | undefined): CategoriaServico {
	return (value ?? 'Outros') as CategoriaServico;
}

export function statusDomainToUi(status: StatusContratoDomain): StatusContratoUi {
	return status === 'ENCERRADO' || status === 'CANCELADO' ? 'ENCERRADO' : 'ATIVO';
}

export function statusUiToDomain(status: StatusContratoUi): StatusContratoDomain {
	return status === 'ENCERRADO' ? 'ENCERRADO' : 'VIGENTE';
}

function toDateOnly(iso: string): string {
	return iso.slice(0, 10);
}

export function fromContratoDto(dto: ContratoDto, categoria?: CategoriaServico): Contrato {
	const statusUi = statusDomainToUi(dto.status);
	return {
		id: dto.id,
		tipo: dto.tipo,
		partes: dto.partes.map(p => p.nomeParte),
		objeto: dto.objeto,
		valor: dto.valor,
		dataInicio: toDateOnly(dto.dataInicio),
		dataFim: toDateOnly(dto.dataFim),
		eventoId: dto.eventoId,
		fornecedorId: dto.fornecedorId,
		categoria: categoria ?? 'Outros',
		status: statusUi,
		criadoEm: dto.createdAt,
		atualizadoEm: dto.updatedAt,
		historicoStatus: [{ status: statusUi, data: dto.createdAt }],
	};
}

export function toContratoCreateDto(input: ContratoInput): ContratoCreateDto {
	return {
		eventoId: input.eventoId,
		fornecedorId: input.fornecedorId,
		tipo: input.tipo,
		objeto: input.objeto.trim(),
		valor: input.valor,
		dataInicio: `${input.dataInicio}T00:00:00`,
		dataFim: `${input.dataFim}T23:59:59`,
		partes: input.partes.map((nome, index) => ({
			nomeParte: nome,
			tipoParte: index === 0 ? 'CONTRATANTE' : 'FORNECEDOR',
		})),
	};
}

export function toContratoUpdateDto(input: ContratoInput): ContratoUpdateDto {
	return toContratoCreateDto(input);
}

export function asCategoriaFromFornecedor(categoriaServico: string): CategoriaServico {
	return asCategoriaServico(categoriaServico);
}
