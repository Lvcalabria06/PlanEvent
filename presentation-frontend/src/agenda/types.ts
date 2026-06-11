export interface EventoAgenda {
  id: string
  nome: string
  dataEvento: string
}

export type StatusCompromisso = 'Pendente' | 'Em andamento' | 'Concluído'
export type TipoCompromisso = 'Reunião' | 'Visita' | 'Apresentação' | 'Outro'
export type AgendaView = 'list' | 'create-compromisso' | 'edit-compromisso'
export type LembreteVinculo = 'evento' | 'compromisso'

export interface CompromissoAgenda {
  id: string
  eventoId: string
  titulo: string
  descricao: string
  data: string
  horaInicio: string
  horaFim: string
  tipo: TipoCompromisso
  local: string
  status: StatusCompromisso
}

export interface LembreteAgenda {
  id: string
  eventoId: string | null
  compromissoId: string | null
  data: string
  horario: string
  notificado: boolean
}

export const EVENTOS_PADRAO: EventoAgenda[] = [
  { id: 'evento-1', nome: 'Conferência Anual de TI 2026', dataEvento: '2026-05-15' },
  { id: 'evento-2', nome: 'Workshop de Liderança Q2', dataEvento: '2026-06-10' },
  { id: 'evento-3', nome: 'Convenção Anual 2026', dataEvento: '2026-04-20' },
]

export const COMPROMISSOS_INICIAIS: CompromissoAgenda[] = [
  {
    id: 'comp-1',
    eventoId: 'evento-1',
    titulo: 'Revisão do Plano de Segurança',
    descricao: 'Reunião com coordenador de segurança para validar protocolos e rotas de evacuação.',
    data: '2026-04-28',
    horaInicio: '09:00',
    horaFim: '10:30',
    tipo: 'Reunião',
    local: 'Sala de Reuniões B',
    status: 'Pendente',
  },
  {
    id: 'comp-2',
    eventoId: 'evento-1',
    titulo: 'Alinhamento com Equipe Técnica',
    descricao: 'Definição de responsáveis e cronograma de montagem.',
    data: '2026-04-28',
    horaInicio: '14:00',
    horaFim: '15:00',
    tipo: 'Reunião',
    local: 'Auditório Principal',
    status: 'Pendente',
  },
  {
    id: 'comp-3',
    eventoId: 'evento-1',
    titulo: 'Visita ao Local do Evento',
    descricao: 'Inspeção das instalações e validação de capacidade.',
    data: '2026-04-29',
    horaInicio: '10:00',
    horaFim: '12:00',
    tipo: 'Visita',
    local: 'Centro de Convenções',
    status: 'Pendente',
  },
  {
    id: 'comp-4',
    eventoId: 'evento-3',
    titulo: 'Briefing com Fornecedores',
    descricao: 'Alinhamento de entregas e prazos finais.',
    data: '2026-04-30',
    horaInicio: '11:00',
    horaFim: '12:30',
    tipo: 'Reunião',
    local: 'Sala Virtual',
    status: 'Em andamento',
  },
]

export const LEMBRETES_INICIAIS: LembreteAgenda[] = [
  { id: 'lem-1', eventoId: 'evento-1', compromissoId: 'comp-1', data: '2026-04-27', horario: '18:00', notificado: false },
  { id: 'lem-2', eventoId: 'evento-1', compromissoId: 'comp-2', data: '2026-04-28', horario: '08:00', notificado: false },
  { id: 'lem-3', eventoId: 'evento-1', compromissoId: null, data: '2026-04-29', horario: '09:00', notificado: false },
  { id: 'lem-4', eventoId: 'evento-1', compromissoId: null, data: '2026-05-01', horario: '14:00', notificado: false },
  { id: 'lem-5', eventoId: 'evento-3', compromissoId: 'comp-4', data: '2026-04-29', horario: '17:00', notificado: false },
  { id: 'lem-6', eventoId: 'evento-1', compromissoId: 'comp-3', data: '2026-04-28', horario: '19:00', notificado: false },
  { id: 'lem-7', eventoId: 'evento-2', compromissoId: null, data: '2026-05-05', horario: '10:00', notificado: false },
]
