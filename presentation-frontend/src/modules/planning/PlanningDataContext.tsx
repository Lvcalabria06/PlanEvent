import {
	createContext,
	useCallback,
	useContext,
	useMemo,
	useRef,
	useState,
	type ReactNode,
} from 'react';
import { normalizeCnpj, isCnpjValid } from '../../shared/utils/cnpj';
import { CONTRATANTE_PADRAO } from './constants';
import { contratosIniciais, fornecedoresIniciais } from './initialData';
import type { Contrato, ContratoInput, Fornecedor, FornecedorInput } from './types';

interface PlanningDataContextValue {
	fornecedores: Fornecedor[];
	contratos: Contrato[];
	criarFornecedor: (data: FornecedorInput) => string | null;
	atualizarFornecedor: (id: string, data: FornecedorInput) => boolean;
	alternarStatusFornecedor: (id: string) => string | null;
	excluirFornecedor: (id: string) => boolean;
	obterFornecedor: (id: string) => Fornecedor | undefined;
	fornecedoresAtivos: Fornecedor[];
	criarContrato: (data: ContratoInput) => string | null;
	atualizarContrato: (id: string, data: ContratoInput) => boolean;
	encerrarContrato: (id: string) => string | null;
	obterContrato: (id: string) => Contrato | undefined;
	fornecedorTemContratoAtivo: (fornecedorId: string) => boolean;
}

const PlanningDataContext = createContext<PlanningDataContextValue | null>(null);

function nowIso() {
	return new Date().toISOString();
}

export function PlanningDataProvider({ children }: { children: ReactNode }) {
	const [fornecedores, setFornecedores] = useState<Fornecedor[]>(fornecedoresIniciais);
	const [contratos, setContratos] = useState<Contrato[]>(contratosIniciais);
	const fornecedorCounter = useRef(1);
	const contratoCounter = useRef(1);

	const fornecedorTemContratoAtivo = useCallback(
		(fornecedorId: string) =>
			contratos.some(c => c.fornecedorId === fornecedorId && c.status === 'ATIVO'),
		[contratos]
	);

	const cnpjDuplicado = useCallback(
		(cnpj: string, ignoreId?: string) => {
			const norm = normalizeCnpj(cnpj);
			return fornecedores.some(
				f => f.id !== ignoreId && normalizeCnpj(f.cnpj) === norm
			);
		},
		[fornecedores]
	);

	const criarFornecedor = useCallback(
		(data: FornecedorInput): string | null => {
			if (!data.nome.trim() || !data.categoriaServico || !data.contato.trim()) {
				return null;
			}
			if (!isCnpjValid(data.cnpj)) return null;
			if (cnpjDuplicado(data.cnpj)) return null;

			const id = `SUP-${String(fornecedorCounter.current++).padStart(5, '0')}`;
			const now = nowIso();
			const novo: Fornecedor = {
				...data,
				id,
				nome: data.nome.trim(),
				cnpj: data.cnpj.trim(),
				contato: data.contato.trim(),
				pessoaContato: data.pessoaContato.trim(),
				email: data.email.trim(),
				telefone: data.telefone.trim(),
				endereco: data.endereco.trim(),
				status: 'ATIVO',
				criadoEm: now,
				atualizadoEm: now,
			};
			setFornecedores(prev => [...prev, novo]);
			return id;
		},
		[cnpjDuplicado]
	);

	const atualizarFornecedor = useCallback(
		(id: string, data: FornecedorInput): boolean => {
			const atual = fornecedores.find(f => f.id === id);
			if (!atual || atual.status === 'INATIVO') return false;
			if (!isCnpjValid(data.cnpj)) return false;
			if (cnpjDuplicado(data.cnpj, id)) return false;

			setFornecedores(prev =>
				prev.map(f =>
					f.id === id
						? {
								...f,
								nome: data.nome.trim(),
								cnpj: data.cnpj.trim(),
								categoriaServico: data.categoriaServico,
								contato: data.contato.trim(),
								pessoaContato: data.pessoaContato.trim(),
								email: data.email.trim(),
								telefone: data.telefone.trim(),
								endereco: data.endereco.trim(),
								atualizadoEm: nowIso(),
							}
						: f
				)
			);
			return true;
		},
		[fornecedores, cnpjDuplicado]
	);

	const alternarStatusFornecedor = useCallback(
		(id: string): string | null => {
			const fornecedor = fornecedores.find(f => f.id === id);
			if (!fornecedor) return 'Fornecedor não encontrado.';

			if (fornecedor.status === 'ATIVO' && fornecedorTemContratoAtivo(id)) {
				return 'Não é possível inativar fornecedor com contrato ativo vinculado.';
			}

			const novoStatus = fornecedor.status === 'ATIVO' ? 'INATIVO' : 'ATIVO';
			setFornecedores(prev =>
				prev.map(f =>
					f.id === id ? { ...f, status: novoStatus, atualizadoEm: nowIso() } : f
				)
			);
			return null;
		},
		[fornecedores, fornecedorTemContratoAtivo]
	);

	const excluirFornecedor = useCallback(
		(id: string): boolean => {
			if (fornecedorTemContratoAtivo(id)) return false;
			setFornecedores(prev => prev.filter(f => f.id !== id));
			return true;
		},
		[fornecedorTemContratoAtivo]
	);

	const obterFornecedor = useCallback(
		(id: string) => fornecedores.find(f => f.id === id),
		[fornecedores]
	);

	const criarContrato = useCallback(
		(data: ContratoInput): string | null => {
			const fornecedor = fornecedores.find(f => f.id === data.fornecedorId);
			if (!fornecedor || fornecedor.status !== 'ATIVO') return null;
			if (!data.objeto.trim() || data.valor <= 0) return null;
			if (!data.dataInicio || !data.dataFim || data.dataFim <= data.dataInicio) return null;
			if (data.partes.length < 2) return null;

			const id = `CTR-${String(contratoCounter.current++).padStart(5, '0')}`;
			const now = nowIso();
			const novo: Contrato = {
				...data,
				id,
				objeto: data.objeto.trim(),
				status: 'ATIVO',
				criadoEm: now,
				atualizadoEm: now,
				historicoStatus: [{ status: 'ATIVO', data: now }],
			};
			setContratos(prev => [...prev, novo]);
			return id;
		},
		[fornecedores]
	);

	const atualizarContrato = useCallback(
		(id: string, data: ContratoInput): boolean => {
			const contrato = contratos.find(c => c.id === id);
			if (!contrato || contrato.status === 'ENCERRADO') return false;

			const fornecedor = fornecedores.find(f => f.id === data.fornecedorId);
			if (!fornecedor || fornecedor.status !== 'ATIVO') return false;
			if (!data.objeto.trim() || data.valor <= 0) return false;
			if (!data.dataInicio || !data.dataFim || data.dataFim <= data.dataInicio) return false;
			if (data.partes.length < 2) return false;

			setContratos(prev =>
				prev.map(c =>
					c.id === id
						? {
								...c,
								...data,
								objeto: data.objeto.trim(),
								atualizadoEm: nowIso(),
							}
						: c
				)
			);
			return true;
		},
		[contratos, fornecedores]
	);

	const encerrarContrato = useCallback(
		(id: string): string | null => {
			const contrato = contratos.find(c => c.id === id);
			if (!contrato) return 'Contrato não encontrado.';
			if (contrato.status === 'ENCERRADO') return 'Este contrato já está encerrado.';
			if (
				!contrato.objeto ||
				!contrato.valor ||
				!contrato.dataInicio ||
				!contrato.dataFim ||
				contrato.partes.length < 2
			) {
				return 'O contrato possui informações incompletas.';
			}

			const now = nowIso();
			setContratos(prev =>
				prev.map(c =>
					c.id === id
						? {
								...c,
								status: 'ENCERRADO',
								atualizadoEm: now,
								historicoStatus: [
									...c.historicoStatus,
									{ status: 'ENCERRADO', data: now },
								],
							}
						: c
				)
			);
			return null;
		},
		[contratos]
	);

	const obterContrato = useCallback(
		(id: string) => contratos.find(c => c.id === id),
		[contratos]
	);

	const fornecedoresAtivos = useMemo(
		() => fornecedores.filter(f => f.status === 'ATIVO'),
		[fornecedores]
	);

	const value = useMemo(
		() => ({
			fornecedores,
			contratos,
			criarFornecedor,
			atualizarFornecedor,
			alternarStatusFornecedor,
			excluirFornecedor,
			obterFornecedor,
			fornecedoresAtivos,
			criarContrato,
			atualizarContrato,
			encerrarContrato,
			obterContrato,
			fornecedorTemContratoAtivo,
		}),
		[
			fornecedores,
			contratos,
			criarFornecedor,
			atualizarFornecedor,
			alternarStatusFornecedor,
			excluirFornecedor,
			obterFornecedor,
			fornecedoresAtivos,
			criarContrato,
			atualizarContrato,
			encerrarContrato,
			obterContrato,
			fornecedorTemContratoAtivo,
		]
	);

	return (
		<PlanningDataContext.Provider value={value}>{children}</PlanningDataContext.Provider>
	);
}

export function usePlanningData() {
	const ctx = useContext(PlanningDataContext);
	if (!ctx) {
		throw new Error('usePlanningData deve ser usado dentro de PlanningDataProvider');
	}
	return ctx;
}

export function buildPartes(contratante: string, fornecedorNome: string): string[] {
	return [contratante.trim() || CONTRATANTE_PADRAO, fornecedorNome.trim()].filter(Boolean);
}
