export const CONTRACT_CATEGORIES = [
	'Buffet/Alimentação',
	'Espaço/Local',
	'Audiovisual/TI',
	'Segurança',
	'Decoração',
	'Transporte',
	'Marketing/Comunicação',
	'Outros',
] as const;

export type CategoriaServico = (typeof CONTRACT_CATEGORIES)[number];

export const CONTRACT_TYPES = [
	'Prestação de Serviços',
	'Locação de Espaço',
	'Fornecimento de Materiais',
	'Segurança Patrimonial',
	'Consultoria',
	'Licença de Software',
	'Transporte e Logística',
	'Marketing e Comunicação',
	'Outros',
] as const;

export const CONTRACT_EVENTS = [
	{ id: 'evento-1', name: 'Convenção Anual 2026' },
	{ id: 'evento-2', name: 'Summit de Liderança Corporativa 2026' },
] as const;

export const CONTRATANTE_PADRAO = 'Empresa XYZ Ltda';
