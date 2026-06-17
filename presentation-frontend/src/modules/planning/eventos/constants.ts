import type { PorteEventoDto, TipoEventoDto } from './dto';

export const TIPO_EVENTO_OPTIONS: { value: TipoEventoDto; label: string }[] = [
	{ value: 'CORPORATIVO', label: 'Corporativo' },
	{ value: 'ACADEMICO', label: 'Acadêmico' },
	{ value: 'SOCIAL', label: 'Social' },
	{ value: 'ESPORTIVO', label: 'Esportivo' },
	{ value: 'OUTRO', label: 'Outro' },
];

export const PORTE_EVENTO_OPTIONS: { value: PorteEventoDto; label: string }[] = [
	{ value: 'PEQUENO', label: 'Pequeno' },
	{ value: 'MEDIO', label: 'Médio' },
	{ value: 'GRANDE', label: 'Grande' },
];

export const CLASSIFICACAO_LABELS: Record<string, string> = {
	RECOMENDADO: 'Recomendado',
	VIAVEL_COM_RESSALVA: 'Viável com ressalva',
	INADEQUADO: 'Inadequado',
	INDISPONIVEL: 'Indisponível',
};

export const STATUS_ALOCACAO_LABELS: Record<string, string> = {
	SEM_LOCAL_DEFINIDO: 'Sem local definido',
	LOCAL_DEFINIDO_PREPARACAO_PENDENTE: 'Local definido — confirme a preparação',
	PREPARACAO_CONFIRMADA_AGUARDANDO_RISCO: 'Aguardando avaliação de risco',
	EVENTO_CONCLUIDO: 'Evento concluído',
};

export const MOTIVO_ALERTA_LABELS: Record<string, string> = {
	CUSTO_ACIMA_DO_TETO: 'Custo acima do teto',
	CAPACIDADE_INSUFICIENTE: 'Capacidade insuficiente',
	CONFLITO_AGENDA: 'Conflito de agenda',
	INDISPONIBILIDADE_OU_BLOQUEIO: 'Indisponibilidade ou bloqueio',
	MANUTENCAO: 'Manutenção programada',
	RESERVA_EXISTENTE: 'Reserva existente no período',
	INFRAESTRUTURA_INSUFICIENTE: 'Infraestrutura insuficiente',
	LOCAL_INATIVO: 'Local inativo',
	PRINCIPAL_NAO_LOCALIZADO: 'Local principal não encontrado',
};
