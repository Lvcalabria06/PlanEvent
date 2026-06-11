/** Status completos do domínio Java (domain.contrato.valueobject.StatusContrato). */
export type StatusContratoDomain =
	| 'RASCUNHO'
	| 'EM_NEGOCIACAO'
	| 'ASSINADO'
	| 'VIGENTE'
	| 'ENCERRADO'
	| 'CANCELADO';

export interface ParteContratoDto {
	contratoId?: string;
	nomeParte: string;
	tipoParte: string;
}

export interface ContratoDto {
	id: string;
	eventoId: string;
	fornecedorId: string;
	tipo: string;
	objeto: string;
	valor: number;
	dataInicio: string;
	dataFim: string;
	status: StatusContratoDomain;
	partes: ParteContratoDto[];
	createdAt: string;
	updatedAt: string;
}

export interface ContratoCreateDto {
	eventoId: string;
	fornecedorId: string;
	tipo: string;
	objeto: string;
	valor: number;
	dataInicio: string;
	dataFim: string;
	partes: ParteContratoDto[];
}

export type ContratoUpdateDto = Omit<ContratoCreateDto, 'eventoId' | 'fornecedorId'> & {
	eventoId?: string;
	fornecedorId?: string;
};
