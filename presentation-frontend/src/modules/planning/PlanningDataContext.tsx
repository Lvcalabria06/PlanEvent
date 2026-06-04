import {
	createContext,
	useCallback,
	useContext,
	useEffect,
	useMemo,
	useState,
	type ReactNode,
} from 'react';
import { isApiNotImplementedError } from '../../shared/api/errors';
import { CONTRACT_EVENTS } from './constants';
import {
	cadastrarFornecedorApi,
	desativarFornecedorApi,
	editarFornecedorApi,
	listarFornecedoresApi,
} from './fornecedores/api';
import { fromFornecedorDto, toFornecedorCreateDto, toFornecedorUpdateDto } from './fornecedores/mappers';
import {
	criarContratoApi,
	editarContratoApi,
	encerrarContratoApi,
	listarContratosApi,
} from './contratos/api';
import { fromContratoDto, toContratoCreateDto, toContratoUpdateDto } from './contratos/mappers';
import { listarEventosApi } from './eventos/api';
import type { EventoResumo } from './eventos/dto';
import { fromEventoDto } from './eventos/mappers';
import type { Contrato, ContratoInput, Fornecedor, FornecedorInput } from './types';

export type { EventoResumo };

interface PlanningDataContextValue {
	fornecedores: Fornecedor[];
	contratos: Contrato[];
	eventos: EventoResumo[];
	loading: boolean;
	integrationPending: boolean;
	error: string | null;
	refetch: () => Promise<void>;
	criarFornecedor: (data: FornecedorInput) => Promise<string | null>;
	atualizarFornecedor: (id: string, data: FornecedorInput) => Promise<boolean>;
	desativarFornecedor: (id: string) => Promise<string | null>;
	obterFornecedor: (id: string) => Fornecedor | undefined;
	fornecedoresAtivos: Fornecedor[];
	criarContrato: (data: ContratoInput) => Promise<string | null>;
	atualizarContrato: (id: string, data: ContratoInput) => Promise<boolean>;
	encerrarContrato: (id: string) => Promise<string | null>;
	obterContrato: (id: string) => Contrato | undefined;
	fornecedorTemContratoAtivo: (fornecedorId: string) => boolean;
}

const PlanningDataContext = createContext<PlanningDataContextValue | null>(null);

const EVENTOS_FALLBACK: EventoResumo[] = CONTRACT_EVENTS.map(e => ({
	id: e.id,
	name: e.name,
}));

export function PlanningDataProvider({ children }: { children: ReactNode }) {
	const [fornecedores, setFornecedores] = useState<Fornecedor[]>([]);
	const [contratos, setContratos] = useState<Contrato[]>([]);
	const [eventos, setEventos] = useState<EventoResumo[]>(EVENTOS_FALLBACK);
	const [loading, setLoading] = useState(true);
	const [integrationPending, setIntegrationPending] = useState(true);
	const [error, setError] = useState<string | null>(null);

	const loadAll = useCallback(async () => {
		setLoading(true);
		setError(null);

		let pending = false;

		try {
			const [fornecedoresDto, contratosDto, eventosDto] = await Promise.all([
				listarFornecedoresApi(),
				listarContratosApi(),
				listarEventosApi(),
			]);

			setFornecedores(fornecedoresDto.map(fromFornecedorDto));
			setContratos(contratosDto.map(dto => fromContratoDto(dto)));
			setEventos(eventosDto.map(fromEventoDto));
			setIntegrationPending(false);
		} catch (err) {
			if (isApiNotImplementedError(err)) {
				pending = true;
				setFornecedores([]);
				setContratos([]);
				setEventos(EVENTOS_FALLBACK);
			} else {
				setError(err instanceof Error ? err.message : 'Erro ao carregar dados.');
			}
		} finally {
			setIntegrationPending(pending);
			setLoading(false);
		}
	}, []);

	useEffect(() => {
		void loadAll();
	}, [loadAll]);

	const fornecedorTemContratoAtivo = useCallback(
		(fornecedorId: string) =>
			contratos.some(c => c.fornecedorId === fornecedorId && c.status === 'ATIVO'),
		[contratos]
	);

	const criarFornecedor = useCallback(async (data: FornecedorInput): Promise<string | null> => {
		try {
			const dto = await cadastrarFornecedorApi(toFornecedorCreateDto(data));
			const fornecedor = fromFornecedorDto(dto);
			setFornecedores(prev => [...prev, fornecedor]);
			setIntegrationPending(false);
			return fornecedor.id;
		} catch (err) {
			if (isApiNotImplementedError(err)) return null;
			throw err;
		}
	}, []);

	const atualizarFornecedor = useCallback(
		async (id: string, data: FornecedorInput): Promise<boolean> => {
			try {
				const dto = await editarFornecedorApi(id, toFornecedorUpdateDto(data));
				const fornecedor = fromFornecedorDto(dto);
				setFornecedores(prev => prev.map(f => (f.id === id ? fornecedor : f)));
				setIntegrationPending(false);
				return true;
			} catch (err) {
				if (isApiNotImplementedError(err)) return false;
				throw err;
			}
		},
		[]
	);

	const desativarFornecedor = useCallback(async (id: string): Promise<string | null> => {
		try {
			await desativarFornecedorApi(id);
			setFornecedores(prev =>
				prev.map(f =>
					f.id === id ? { ...f, status: 'INATIVO', atualizadoEm: new Date().toISOString() } : f
				)
			);
			setIntegrationPending(false);
			return null;
		} catch (err) {
			if (isApiNotImplementedError(err)) {
				return 'Integração com backend pendente.';
			}
			return err instanceof Error ? err.message : 'Erro ao desativar fornecedor.';
		}
	}, []);

	const obterFornecedor = useCallback(
		(id: string) => fornecedores.find(f => f.id === id),
		[fornecedores]
	);

	const criarContrato = useCallback(async (data: ContratoInput): Promise<string | null> => {
		try {
			const dto = await criarContratoApi(toContratoCreateDto(data));
			const fornecedor = fornecedores.find(f => f.id === data.fornecedorId);
			const contrato = fromContratoDto(dto, fornecedor?.categoriaServico ?? data.categoria);
			setContratos(prev => [...prev, contrato]);
			setIntegrationPending(false);
			return contrato.id;
		} catch (err) {
			if (isApiNotImplementedError(err)) return null;
			throw err;
		}
	}, [fornecedores]);

	const atualizarContrato = useCallback(
		async (id: string, data: ContratoInput): Promise<boolean> => {
			try {
				const dto = await editarContratoApi(id, toContratoUpdateDto(data));
				const fornecedor = fornecedores.find(f => f.id === data.fornecedorId);
				const contrato = fromContratoDto(dto, fornecedor?.categoriaServico ?? data.categoria);
				setContratos(prev => prev.map(c => (c.id === id ? contrato : c)));
				setIntegrationPending(false);
				return true;
			} catch (err) {
				if (isApiNotImplementedError(err)) return false;
				throw err;
			}
		},
		[fornecedores]
	);

	const encerrarContrato = useCallback(async (id: string): Promise<string | null> => {
		try {
			const dto = await encerrarContratoApi(id);
			const atual = contratos.find(c => c.id === id);
			const contrato = fromContratoDto(dto, atual?.categoria);
			setContratos(prev => prev.map(c => (c.id === id ? contrato : c)));
			setIntegrationPending(false);
			return null;
		} catch (err) {
			if (isApiNotImplementedError(err)) {
				return 'Integração com backend pendente.';
			}
			return err instanceof Error ? err.message : 'Erro ao encerrar contrato.';
		}
	}, [contratos]);

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
			eventos,
			loading,
			integrationPending,
			error,
			refetch: loadAll,
			criarFornecedor,
			atualizarFornecedor,
			desativarFornecedor,
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
			eventos,
			loading,
			integrationPending,
			error,
			loadAll,
			criarFornecedor,
			atualizarFornecedor,
			desativarFornecedor,
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
