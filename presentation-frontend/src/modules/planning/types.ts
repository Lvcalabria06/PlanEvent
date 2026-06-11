import type { CategoriaServico } from './constants';

export type StatusFornecedor = 'ATIVO' | 'INATIVO';
export type StatusContratoUi = 'ATIVO' | 'ENCERRADO';

export interface Fornecedor {
	id: string;
	nome: string;
	cnpj: string;
	categoriaServico: CategoriaServico;
	contato: string;
	pessoaContato: string;
	email: string;
	telefone: string;
	endereco: string;
	status: StatusFornecedor;
	criadoEm: string;
	atualizadoEm: string;
}

export interface StatusHistoricoContrato {
	status: StatusContratoUi;
	data: string;
	motivo?: string;
}

export interface Contrato {
	id: string;
	tipo: string;
	partes: string[];
	objeto: string;
	valor: number;
	dataInicio: string;
	dataFim: string;
	eventoId: string;
	fornecedorId: string;
	categoria: CategoriaServico;
	status: StatusContratoUi;
	criadoEm: string;
	atualizadoEm: string;
	historicoStatus: StatusHistoricoContrato[];
}

export type FornecedorInput = Omit<
	Fornecedor,
	'id' | 'status' | 'criadoEm' | 'atualizadoEm'
>;

export type ContratoInput = Omit<
	Contrato,
	'id' | 'status' | 'criadoEm' | 'atualizadoEm' | 'historicoStatus'
>;
