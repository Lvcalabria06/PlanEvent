import type { CompromissoAgenda, LembreteAgenda } from './types'

export const REFERENCIA_AGORA = new Date('2026-04-28T08:00:00')

export function dataHoraIso(data: string, hora: string): Date {
  return new Date(`${data}T${hora}:00`)
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
  } = params

  if (!formEventoId || !formTitulo.trim() || !formData || !formHoraInicio || !formHoraFim || !formLocal.trim()) {
    return 'Preencha todos os campos obrigatórios.'
  }

  if (formHoraFim <= formHoraInicio) {
    return 'O horário de término deve ser posterior ao início.'
  }

  const inicio = dataHoraIso(formData, formHoraInicio)
  if (inicio < REFERENCIA_AGORA) {
    return 'Não é permitido criar compromissos em datas passadas.'
  }

  const sobreposto = compromissos.some(
    (c) =>
      c.data === formData &&
      c.id !== compromissoEditId &&
      c.status !== 'Concluído' &&
      formHoraInicio < c.horaFim &&
      c.horaInicio < formHoraFim
  )
  if (sobreposto) {
    return 'Não é possível agendar compromissos em horários sobrepostos.'
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
  eventos: { id: string; dataEvento: string }[]
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
  if (horarioLembrete < REFERENCIA_AGORA) {
    return 'Não é permitido criar lembretes com horário no passado.'
  }

  const comp =
    lembreteVinculo === 'compromisso'
      ? compromissos.find((c) => c.id === lembreteCompromissoId)
      : null

  if (comp?.status === 'Concluído') {
    return 'Não é permitido criar lembretes para compromissos finalizados.'
  }

  let inicioReferencia: Date | null = null
  if (comp) {
    inicioReferencia = dataHoraIso(comp.data, comp.horaInicio)
  } else if (lembreteVinculo === 'evento') {
    const ev = eventos.find((e) => e.id === lembreteEventoId)
    if (ev) {
      inicioReferencia = dataHoraIso(ev.dataEvento, '23:59')
    }
  }

  if (inicioReferencia && horarioLembrete >= inicioReferencia) {
    return 'O horário do lembrete deve ser anterior ao início do evento/compromisso.'
  }

  const eventoIdVinculo = comp?.eventoId ?? lembreteEventoId
  const duplicado = lembretes.some((l) => {
    if (l.notificado) return false
    const mesmoHorario = l.data === lembreteData && l.horario === lembreteHorario
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
