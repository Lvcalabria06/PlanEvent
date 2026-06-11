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

const TIPO_UI_TO_BACKEND: Record<string, string> = {
	'Prestação de Serviços': 'PRESTACAO_SERVICO',
	'Locação de Espaço': 'LOCACAO',
	'Fornecimento de Materiais': 'FORNECEDOR',
	'Segurança Patrimonial': 'PRESTACAO_SERVICO',
	'Consultoria': 'PRESTACAO_SERVICO',
	'Licença de Software': 'OUTRO',
	'Transporte e Logística': 'PRESTACAO_SERVICO',
	'Marketing e Comunicação': 'PRESTACAO_SERVICO',
	'Outros': 'OUTRO',
};

const TIPO_BACKEND_TO_UI: Record<string, string> = {
	'PRESTACAO_SERVICO': 'Prestação de Serviços',
	'LOCACAO': 'Locação de Espaço',
	'FORNECEDOR': 'Fornecimento de Materiais',
	'PATROCINIO': 'Outros',
	'OUTRO': 'Outros',
};

function tipoUiToBackend(tipo: string): string {
	return TIPO_UI_TO_BACKEND[tipo] ?? tipo;
}

function tipoBackendToUi(tipo: string): string {
	return TIPO_BACKEND_TO_UI[tipo] ?? tipo;
}

export function fromContratoDto(dto: ContratoDto, categoria?: CategoriaServico): Contrato {
	const statusUi = statusDomainToUi(dto.status);
	return {
		id: dto.id,
		tipo: tipoBackendToUi(dto.tipo),
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
		tipo: tipoUiToBackend(input.tipo),
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
