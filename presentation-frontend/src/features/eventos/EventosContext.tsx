import {
	createContext,
	useCallback,
	useContext,
	useEffect,
	useMemo,
	useState,
	type ReactNode,
} from 'react';
import {
	analisarLocaisApi,
	avaliarRiscoApi,
	buscarEventoApi,
	cancelarEventoApi,
	confirmarPreparacaoApi,
	criarEventoApi,
	definirAlocacaoApi,
	editarEventoApi,
	listarEventosApi,
	registrarParametrosApi,
	trocaContingenciaApi,
} from '../../modules/planning/eventos/api';
import type {
	AlertaRiscoAlocacaoDto,
	CandidatoAnaliseLocalDto,
	CriarEventoDto,
	EventoDto,
} from '../../modules/planning/eventos/dto';

interface EventosContextValue {
	eventos: EventoDto[];
	loading: boolean;
	error: string | null;
	refetch: () => Promise<void>;
	obterEvento: (id: string) => EventoDto | undefined;
	buscarEvento: (id: string) => Promise<EventoDto | null>;
	criarEvento: (data: CriarEventoDto) => Promise<string | null>;
	editarEvento: (id: string, data: CriarEventoDto) => Promise<boolean>;
	confirmarPreparacao: (id: string) => Promise<string | null>;
	cancelarEvento: (id: string) => Promise<string | null>;
	analisarLocais: (id: string, tetoCusto: number) => Promise<CandidatoAnaliseLocalDto[] | null>;
	registrarParametros: (
		id: string,
		tetoCusto: number,
		dataInicio?: string,
		dataFim?: string,
	) => Promise<boolean>;
	definirAlocacao: (
		id: string,
		localPrincipalId: string,
		tetoCusto: number,
		contingenciaIds: string[],
	) => Promise<string | null>;
	avaliarRisco: (id: string) => Promise<AlertaRiscoAlocacaoDto | null>;
	trocarLocal: (
		id: string,
		novoLocalId: string,
		usuarioId: string,
		motivo: string,
	) => Promise<string | null>;
}

const EventosContext = createContext<EventosContextValue | null>(null);

export function EventosProvider({ children }: { children: ReactNode }) {
	const [eventos, setEventos] = useState<EventoDto[]>([]);
	const [loading, setLoading] = useState(true);
	const [error, setError] = useState<string | null>(null);

	const mergeEvento = useCallback((evento: EventoDto) => {
		setEventos(prev => {
			const idx = prev.findIndex(e => e.id === evento.id);
			if (idx === -1) return [...prev, evento];
			const copy = [...prev];
			copy[idx] = evento;
			return copy;
		});
	}, []);

	const refetch = useCallback(async () => {
		setLoading(true);
		setError(null);
		try {
			const lista = await listarEventosApi();
			setEventos(lista);
		} catch (err) {
			setError(err instanceof Error ? err.message : 'Erro ao carregar eventos.');
		} finally {
			setLoading(false);
		}
	}, []);

	const obterEvento = useCallback((id: string) => eventos.find(e => e.id === id), [eventos]);

	const buscarEvento = useCallback(
		async (id: string) => {
			try {
				const evento = await buscarEventoApi(id);
				mergeEvento(evento);
				return evento;
			} catch (err) {
				setError(err instanceof Error ? err.message : 'Erro ao buscar evento.');
				return null;
			}
		},
		[mergeEvento],
	);

	const criarEvento = useCallback(
		async (data: CriarEventoDto) => {
			try {
				const evento = await criarEventoApi(data);
				mergeEvento(evento);
				return evento.id;
			} catch {
				return null;
			}
		},
		[mergeEvento],
	);

	const editarEvento = useCallback(
		async (id: string, data: CriarEventoDto) => {
			try {
				const evento = await editarEventoApi(id, data);
				mergeEvento(evento);
				return true;
			} catch {
				return false;
			}
		},
		[mergeEvento],
	);

	const confirmarPreparacao = useCallback(
		async (id: string) => {
			try {
				const evento = await confirmarPreparacaoApi(id);
				mergeEvento(evento);
				return null;
			} catch (err) {
				return err instanceof Error ? err.message : 'Erro ao confirmar preparação.';
			}
		},
		[mergeEvento],
	);

	const cancelarEvento = useCallback(async (id: string) => {
		try {
			await cancelarEventoApi(id);
			setEventos(prev => prev.filter(e => e.id !== id));
			return null;
		} catch (err) {
			return err instanceof Error ? err.message : 'Erro ao cancelar evento.';
		}
	}, []);

	const analisarLocais = useCallback(async (id: string, tetoCusto: number) => {
		try {
			const resultado = await analisarLocaisApi(id, tetoCusto);
			return resultado.candidatos;
		} catch {
			return null;
		}
	}, []);

	const registrarParametros = useCallback(
		async (id: string, tetoCusto: number, dataInicio?: string, dataFim?: string) => {
			try {
				const evento = await registrarParametrosApi(id, {
					tetoCusto,
					dataInicio: dataInicio || null,
					dataFim: dataFim || null,
				});
				mergeEvento(evento);
				return true;
			} catch {
				return false;
			}
		},
		[mergeEvento],
	);

	const definirAlocacao = useCallback(
		async (
			id: string,
			localPrincipalId: string,
			tetoCusto: number,
			contingenciaIds: string[],
		) => {
			try {
				const evento = await definirAlocacaoApi(id, {
					localPrincipalId,
					tetoCusto,
					localIdsContingenciaOrdenados: contingenciaIds,
				});
				mergeEvento(evento);
				return null;
			} catch (err) {
				return err instanceof Error ? err.message : 'Erro ao definir alocação.';
			}
		},
		[mergeEvento],
	);

	const avaliarRisco = useCallback(async (id: string) => {
		return await avaliarRiscoApi(id);
	}, []);

	const trocarLocal = useCallback(
		async (id: string, novoLocalId: string, usuarioId: string, motivo: string) => {
			try {
				const evento = await trocaContingenciaApi(id, {
					novoLocalId,
					usuarioId,
					motivo,
				});
				mergeEvento(evento);
				return null;
			} catch (err) {
				return err instanceof Error ? err.message : 'Erro na troca de local.';
			}
		},
		[mergeEvento],
	);

	const value = useMemo(
		() => ({
			eventos,
			loading,
			error,
			refetch,
			obterEvento,
			buscarEvento,
			criarEvento,
			editarEvento,
			confirmarPreparacao,
			cancelarEvento,
			analisarLocais,
			registrarParametros,
			definirAlocacao,
			avaliarRisco,
			trocarLocal,
		}),
		[
			eventos,
			loading,
			error,
			refetch,
			obterEvento,
			buscarEvento,
			criarEvento,
			editarEvento,
			confirmarPreparacao,
			cancelarEvento,
			analisarLocais,
			registrarParametros,
			definirAlocacao,
			avaliarRisco,
			trocarLocal,
		],
	);

	useEffect(() => {
		refetch();
	}, [refetch]);

	return <EventosContext.Provider value={value}>{children}</EventosContext.Provider>;
}

export function useEventos() {
	const ctx = useContext(EventosContext);
	if (!ctx) throw new Error('useEventos deve ser usado dentro de EventosProvider');
	return ctx;
}
