export type TipoEventoDto =
	| 'CORPORATIVO'
	| 'SOCIAL'
	| 'ACADEMICO'
	| 'ESPORTIVO'
	| 'OUTRO';

export type PorteEventoDto = 'PEQUENO' | 'MEDIO' | 'GRANDE';

export type ClassificacaoAlocacaoLocalDto =
	| 'RECOMENDADO'
	| 'VIAVEL_COM_RESSALVA'
	| 'INADEQUADO'
	| 'INDISPONIVEL';

export type StatusAlocacaoEventoDto =
	| 'SEM_LOCAL_DEFINIDO'
	| 'LOCAL_DEFINIDO_PREPARACAO_PENDENTE'
	| 'PREPARACAO_CONFIRMADA_AGUARDANDO_RISCO'
	| 'EVENTO_CONCLUIDO';

export interface TrocaLocalPlanejamentoDto {
	dataHora: string;
	usuarioId: string;
	motivo: string;
	localAnteriorId: string;
	localAnteriorNome: string | null;
	localNovoId: string;
	localNovoNome: string | null;
}

export interface EventoDto {
	id: string;
	nome: string;
	tipo: TipoEventoDto;
	porte: PorteEventoDto;
	quantidadeEstimadaParticipantes: number;
	objetivo: string;
	localId: string | null;
	nomeLocalPrincipal: string | null;
	custoLocalPrincipal: number | null;
	capacidadeLocalPrincipal: number | null;
	planejamentoConfirmado: boolean;
	concluido: boolean;
	cancelado: boolean;
	dataInicio: string | null;
	dataFim: string | null;
	tetoCustoEspacoInformado: number | null;
	requisitosInfraestrutura: string | null;
	locaisContingenciaOrdenados: string[];
	nomesLocaisContingencia: string[];
	statusAlocacao: StatusAlocacaoEventoDto;
	podePlanejarLocal: boolean;
	historicoTrocasLocal: TrocaLocalPlanejamentoDto[];
	dataCriacao: string;
	dataAtualizacao: string;
}

export interface CriarEventoDto {
	nome: string;
	tipo: TipoEventoDto;
	porte: PorteEventoDto;
	quantidadeEstimadaParticipantes: number;
	objetivo: string;
	dataInicio: string;
	dataFim: string;
	requisitosInfraestrutura?: string | null;
}

export type EditarEventoDto = CriarEventoDto;

export interface CandidatoAnaliseLocalDto {
	localId: string;
	nomeLocal: string;
	classificacao: ClassificacaoAlocacaoLocalDto;
	justificativa: string;
	custo: number;
	capacidade: number;
	acimaDoTeto: boolean;
	capacidadeOk: boolean;
	agendaOk: boolean;
	podeSerPrincipal: boolean;
}

export interface ResultadoAnaliseAlocacaoDto {
	eventoId: string;
	candidatos: CandidatoAnaliseLocalDto[];
}

export interface RegistrarParametrosAlocacaoDto {
	tetoCusto: number;
	dataInicio?: string | null;
	dataFim?: string | null;
}

export interface DefinirAlocacaoLocalDto {
	localPrincipalId: string;
	tetoCusto: number;
	localIdsContingenciaOrdenados: string[];
}

export interface AlertaRiscoAlocacaoDto {
	eventoId: string;
	localPrincipalId: string;
	descricao: string;
	motivos: string[];
	melhorSubstitutoSugeridoId: string | null;
	melhorSubstitutoSugeridoNome: string | null;
}

export interface EventoResumo {
	id: string;
	name: string;
}
