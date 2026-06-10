import type { CompromissoAgenda, EventoAgenda, LembreteAgenda, StatusCompromisso } from './types'

export function dataHoraIso(data: string, hora: string): Date {
  return new Date(`${data}T${hora.length >= 5 ? hora.slice(0, 5) : hora}:00`)
}

export function compromissoEstaFinalizado(status: StatusCompromisso): boolean {
  return status === 'Concluído'
}

function temSobreposicao(
  inicio: Date,
  fim: Date,
  outro: CompromissoAgenda,
  compromissoEditId: string | null
): boolean {
  if (outro.id === compromissoEditId) return false
  if (compromissoEstaFinalizado(outro.status)) return false
  const outroInicio = dataHoraIso(outro.data, outro.horaInicio)
  const outroFim = dataHoraIso(outro.data, outro.horaFim)
  return inicio < outroFim && outroInicio < fim
}

export function validarCompromissoForm(params: {
  formEventoId: string
  formTitulo: string
  formData: string
  formHoraInicio: string
  formHoraFim: string
  formLocal: string
  compromissos: CompromissoAgenda[]
  compromissoEditId: string | null
  eventos: EventoAgenda[]
  statusAtual?: StatusCompromisso
}): string | null {
  const {
    formEventoId,
    formTitulo,
    formData,
    formHoraInicio,
    formHoraFim,
    formLocal,
    compromissos,
    compromissoEditId,
    eventos,
    statusAtual,
  } = params

  if (statusAtual && compromissoEstaFinalizado(statusAtual)) {
    return 'Não é permitido editar compromissos já concluídos.'
  }

  if (!formEventoId || !formTitulo.trim() || !formData || !formHoraInicio || !formHoraFim || !formLocal.trim()) {
    return 'Preencha todos os campos obrigatórios.'
  }

  const evento = eventos.find((e) => e.id === formEventoId)
  if (!evento) {
    return 'Selecione um evento válido.'
  }

  if (formHoraFim <= formHoraInicio) {
    return 'O horário de término deve ser posterior ao início.'
  }

  const inicio = dataHoraIso(formData, formHoraInicio)
  const fim = dataHoraIso(formData, formHoraFim)

  if (inicio < new Date()) {
    return 'Não é permitido criar compromissos em datas passadas.'
  }

  const sobreposto = compromissos.some((c) => temSobreposicao(inicio, fim, c, compromissoEditId))
  if (sobreposto) {
    return 'Já existe um compromisso nesse horário para o gestor.'
  }

  return null
}

export function validarLembreteForm(params: {
  lembreteVinculo: 'evento' | 'compromisso'
  lembreteEventoId: string
  lembreteCompromissoId: string
  lembreteData: string
  lembreteHorario: string
  compromissos: CompromissoAgenda[]
  lembretes: LembreteAgenda[]
  eventos: EventoAgenda[]
  lembreteEditId?: string | null
}): string | null {
  const {
    lembreteVinculo,
    lembreteEventoId,
    lembreteCompromissoId,
    lembreteData,
    lembreteHorario,
    compromissos,
    lembretes,
    eventos,
    lembreteEditId = null,
  } = params

  if (lembreteVinculo === 'evento' && !lembreteEventoId) {
    return 'Selecione o evento.'
  }
  if (lembreteVinculo === 'compromisso' && !lembreteCompromissoId) {
    return 'Selecione o compromisso.'
  }
  if (!lembreteData || !lembreteHorario) {
    return 'Informe data e horário do lembrete.'
  }

  const horarioLembrete = dataHoraIso(lembreteData, lembreteHorario)
  const agora = new Date()

  if (horarioLembrete < agora) {
    return 'Não é permitido criar lembretes com horário no passado.'
  }

  const comp =
    lembreteVinculo === 'compromisso'
      ? compromissos.find((c) => c.id === lembreteCompromissoId)
      : null

  if (lembreteVinculo === 'compromisso' && !comp) {
    return 'Compromisso não encontrado.'
  }

  if (comp?.status === 'Concluído') {
    return 'Não é permitido criar ou editar lembretes de compromissos finalizados.'
  }

  let inicioReferencia: Date | null = null
  if (comp) {
    inicioReferencia = dataHoraIso(comp.data, comp.horaInicio)
  } else if (lembreteVinculo === 'evento') {
    const ev = eventos.find((e) => e.id === lembreteEventoId)
    if (!ev) {
      return 'Evento não encontrado.'
    }
    inicioReferencia = dataHoraIso(ev.dataEvento, '23:59')
  }

  if (inicioReferencia && horarioLembrete >= inicioReferencia) {
    return 'O horário do lembrete deve ser anterior ao início do evento/compromisso.'
  }

  const eventoIdVinculo = comp?.eventoId ?? lembreteEventoId
  const duplicado = lembretes.some((l) => {
    if (l.id === lembreteEditId) return false
    if (l.notificado) return false
    const mesmoHorario = l.data === lembreteData && l.horario === lembreteHorario.slice(0, 5)
    if (!mesmoHorario) return false
    if (lembreteVinculo === 'compromisso') {
      return l.compromissoId === lembreteCompromissoId
    }
    return !l.compromissoId && l.eventoId === eventoIdVinculo
  })
  if (duplicado) {
    return 'Já existe um lembrete com esse horário para este vínculo.'
  }

  return null
}

export function formatarLembreteDataHora(data: string, horario: string): string {
  const d = new Date(`${data}T${horario}:00`)
  const dia = d.toLocaleDateString('pt-BR', { day: '2-digit', month: 'short' }).replace('.', '')
  const hora = d.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' })
  return `${dia} às ${hora}`
}
