import { useMemo, useState } from 'react'
import './agenda.css'
import { useAgenda } from './AgendaContext'
import LembretesNotificacaoPopup from './LembretesNotificacaoPopup'
import type { TipoCompromisso } from './types'

export type { EventoAgenda, CompromissoAgenda, LembreteAgenda } from './types'

type AgendaView = 'list' | 'create-compromisso' | 'edit-compromisso'
type LembreteVinculo = 'evento' | 'compromisso'

const DIAS_ABRIL_2026 = [
  { key: '2026-04-26', label: 'Dom', num: 26 },
  { key: '2026-04-27', label: 'Seg', num: 27 },
  { key: '2026-04-28', label: 'Ter', num: 28 },
  { key: '2026-04-29', label: 'Qua', num: 29 },
  { key: '2026-04-30', label: 'Qui', num: 30 },
]

function formatarDataLonga(iso: string): string {
  const d = new Date(iso + 'T12:00:00')
  return d.toLocaleDateString('pt-BR', {
    weekday: 'long',
    day: 'numeric',
    month: 'long',
    year: 'numeric',
  })
}

export default function AgendaModule() {
  const {
    eventos,
    compromissos,
    lembretes,
    setCompromissos,
    setLembretes,
    criarCompromisso: criarCompromissoCtx,
    atualizarCompromisso,
    criarLembrete: criarLembreteCtx,
  } = useAgenda()

  const [agendaView, setAgendaView] = useState<AgendaView>('list')
  const [dataSelecionada, setDataSelecionada] = useState('2026-04-28')
  const [compromissoEditId, setCompromissoEditId] = useState<string | null>(null)

  const [showLembreteModal, setShowLembreteModal] = useState(false)
  const [showLembretesPanel, setShowLembretesPanel] = useState(false)

  const [formEventoId, setFormEventoId] = useState('')
  const [formTitulo, setFormTitulo] = useState('')
  const [formDescricao, setFormDescricao] = useState('')
  const [formData, setFormData] = useState('')
  const [formTipo, setFormTipo] = useState<TipoCompromisso>('Reunião')
  const [formHoraInicio, setFormHoraInicio] = useState('')
  const [formHoraFim, setFormHoraFim] = useState('')
  const [formLocal, setFormLocal] = useState('')
  const [formErro, setFormErro] = useState('')

  const [lembreteVinculo, setLembreteVinculo] = useState<LembreteVinculo>('evento')
  const [lembreteEventoId, setLembreteEventoId] = useState('')
  const [lembreteFiltroEventoId, setLembreteFiltroEventoId] = useState('')
  const [lembreteCompromissoId, setLembreteCompromissoId] = useState('')
  const [lembreteData, setLembreteData] = useState('')
  const [lembreteHorario, setLembreteHorario] = useState('')
  const [lembreteErro, setLembreteErro] = useState('')

  const compromissoEmEdicao = compromissos.find((c) => c.id === compromissoEditId)

  const stats = useMemo(() => {
    const hoje = '2026-04-28'
    const hojeList = compromissos.filter((c) => c.data === hoje)
    const concluidosHoje = hojeList.filter((c) => c.status === 'Concluído').length
    const inicioSemana = new Date('2026-04-27T12:00:00')
    const fimSemana = new Date('2026-05-03T12:00:00')
    const semana = compromissos.filter((c) => {
      const d = new Date(c.data + 'T12:00:00')
      return d >= inicioSemana && d <= fimSemana
    })
    const pendentesSemana = semana.filter((c) => c.status !== 'Concluído').length
    const comLembrete = compromissos.filter((c) =>
      lembretes.some((l) => l.compromissoId === c.id && !l.notificado)
    ).length
    const avulsos = lembretes.filter((l) => !l.compromissoId && !l.notificado).length
    return {
      hoje: hojeList.length,
      concluidosHoje,
      semana: semana.length,
      pendentesSemana,
      comLembrete,
      avulsos,
      lembretesPendentes: lembretes.filter((l) => !l.notificado).length,
    }
  }, [compromissos, lembretes])

  const compromissosDoDia = useMemo(
    () =>
      compromissos
        .filter((c) => c.data === dataSelecionada)
        .sort((a, b) => a.horaInicio.localeCompare(b.horaInicio)),
    [compromissos, dataSelecionada]
  )

  const contagemPorDia = useMemo(() => {
    const map: Record<string, number> = {}
    compromissos.forEach((c) => {
      map[c.data] = (map[c.data] || 0) + 1
    })
    return map
  }, [compromissos])

  const compromissosFiltradosLembrete = useMemo(() => {
    if (!lembreteFiltroEventoId) return compromissos
    return compromissos.filter((c) => c.eventoId === lembreteFiltroEventoId)
  }, [compromissos, lembreteFiltroEventoId])

  function resetFormCompromisso() {
    setFormEventoId('')
    setFormTitulo('')
    setFormDescricao('')
    setFormData(dataSelecionada)
    setFormTipo('Reunião')
    setFormHoraInicio('')
    setFormHoraFim('')
    setFormLocal('')
    setFormErro('')
  }

  function abrirCriarCompromisso() {
    resetFormCompromisso()
    setFormData(dataSelecionada)
    setAgendaView('create-compromisso')
  }

  function abrirEditarCompromisso(id: string) {
    const c = compromissos.find((x) => x.id === id)
    if (!c) return
    setCompromissoEditId(id)
    setFormEventoId(c.eventoId)
    setFormTitulo(c.titulo)
    setFormDescricao(c.descricao)
    setFormData(c.data)
    setFormTipo(c.tipo)
    setFormHoraInicio(c.horaInicio)
    setFormHoraFim(c.horaFim)
    setFormLocal(c.local)
    setFormErro('')
    setAgendaView('edit-compromisso')
  }

  function salvarCompromisso(e: React.FormEvent) {
    e.preventDefault()

    const dados = {
      eventoId: formEventoId,
      titulo: formTitulo.trim(),
      descricao: formDescricao.trim(),
      data: formData,
      horaInicio: formHoraInicio,
      horaFim: formHoraFim,
      tipo: formTipo,
      local: formLocal.trim(),
    }

    if (agendaView === 'create-compromisso') {
      const erroCtx = criarCompromissoCtx(dados)
      if (erroCtx) {
        setFormErro(erroCtx)
        return
      }
      setDataSelecionada(formData)
    } else if (compromissoEditId) {
      const erro = atualizarCompromisso(compromissoEditId, dados)
      if (erro) {
        setFormErro(erro)
        return
      }
    }

    setFormErro('')
    setAgendaView('list')
    setCompromissoEditId(null)
  }

  function excluirCompromisso() {
    if (!compromissoEditId) return
    const c = compromissos.find((x) => x.id === compromissoEditId)
    if (c?.status === 'Em andamento') {
      setFormErro('Compromissos em andamento não podem ser excluídos.')
      return
    }
    setCompromissos((prev) => prev.filter((x) => x.id !== compromissoEditId))
    setLembretes((prev) => prev.filter((l) => l.compromissoId !== compromissoEditId))
    setAgendaView('list')
    setCompromissoEditId(null)
  }

  function avancarStatus() {
    if (!compromissoEditId || !compromissoEmEdicao) return
    if (compromissoEmEdicao.status === 'Pendente') {
      setCompromissos((prev) =>
        prev.map((c) => (c.id === compromissoEditId ? { ...c, status: 'Em andamento' } : c))
      )
    } else if (compromissoEmEdicao.status === 'Em andamento') {
      setCompromissos((prev) =>
        prev.map((c) => (c.id === compromissoEditId ? { ...c, status: 'Concluído' } : c))
      )
    }
  }

  function abrirModalLembrete() {
    setLembreteVinculo('evento')
    setLembreteEventoId(eventos[0]?.id ?? '')
    setLembreteFiltroEventoId('')
    setLembreteCompromissoId('')
    setLembreteData(dataSelecionada)
    setLembreteHorario('')
    setLembreteErro('')
    setShowLembreteModal(true)
  }

  function criarLembrete(e: React.FormEvent) {
    e.preventDefault()
    const erro = criarLembreteCtx({
      vinculo: lembreteVinculo,
      eventoId: lembreteEventoId,
      compromissoId: lembreteCompromissoId,
      data: lembreteData,
      horario: lembreteHorario,
    })
    if (erro) {
      setLembreteErro(erro)
      return
    }
    setLembreteErro('')
    setShowLembreteModal(false)
  }

  function contarLembretesCompromisso(compromissoId: string): number {
    return lembretes.filter((l) => l.compromissoId === compromissoId && !l.notificado).length
  }

  function nomeEvento(eventoId: string): string {
    return eventos.find((e) => e.id === eventoId)?.nome ?? 'Evento'
  }

  function renderEventLinkBox() {
    return (
      <div className="agenda-event-link-box">
        <div className="agenda-event-link-header">
          <span className="agenda-event-link-title">Vínculo com Evento</span>
          <span className="badge-obrigatorio">Obrigatório</span>
        </div>
        <div className="form-group">
          <label>Evento Relacionado *</label>
          <select
            className="form-select"
            value={formEventoId}
            onChange={(e) => setFormEventoId(e.target.value)}
          >
            <option value="">Selecione um evento...</option>
            {eventos.map((ev) => (
              <option key={ev.id} value={ev.id}>
                {ev.nome} — {new Date(ev.dataEvento + 'T12:00:00').toLocaleDateString('pt-BR')}
              </option>
            ))}
          </select>
        </div>
        <p className="agenda-event-help">
          Todo compromisso deve estar vinculado a um evento ativo do sistema.
        </p>
      </div>
    )
  }

  function renderFormCompromisso(tituloPagina: string, mostrarStatus: boolean) {
    const status = compromissoEmEdicao?.status ?? 'Pendente'
    const proximoStatus =
      status === 'Pendente' ? 'Em andamento' : status === 'Em andamento' ? 'Concluído' : null

    return (
      <div className="content-card">
        <button
          type="button"
          className="back-link"
          onClick={() => {
            setAgendaView('list')
            setCompromissoEditId(null)
          }}
        >
          <svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2" style={{ marginRight: 6 }}>
            <line x1="19" y1="12" x2="5" y2="12" />
            <polyline points="12 19 5 12 12 5" />
          </svg>
          Voltar para Agenda
        </button>

        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
          <h2 style={{ fontSize: '1.5rem', fontWeight: 700, color: '#111827', margin: 0 }}>{tituloPagina}</h2>
          {mostrarStatus && (
            <button type="button" className="btn-excluir" onClick={excluirCompromisso}>
              <svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2">
                <polyline points="3 6 5 6 21 6" />
                <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" />
              </svg>
              Excluir
            </button>
          )}
        </div>

        {mostrarStatus && proximoStatus && (
          <div className="status-tracker-card">
            <div className="status-tracker-label">STATUS DO COMPROMISSO</div>
            <div className="status-steps">
              <div
                className={`status-step ${status === 'Pendente' ? 'active' : 'done'}`}
              >
                <div className="status-step-circle" />
                Pendente
              </div>
              <div
                className={`status-step ${status === 'Em andamento' ? 'active' : status === 'Concluído' ? 'done' : ''}`}
              >
                <div className="status-step-circle" />
                Em andamento
              </div>
              <div className={`status-step ${status === 'Concluído' ? 'active done' : ''}`}>
                <div className="status-step-circle" />
                Concluído
              </div>
            </div>
            <div className="status-action-box">
              <p style={{ margin: 0, fontSize: '0.85rem', color: '#374151', flex: 1 }}>
                Avançar para <strong>{proximoStatus}</strong>. O fluxo é progressivo e não pode ser revertido.
              </p>
              <button type="button" className="action-btn" onClick={avancarStatus}>
                Marcar como {proximoStatus}
              </button>
            </div>
          </div>
        )}

        {formErro && (
          <div
            className="error-message"
            style={{
              padding: '0.75rem',
              backgroundColor: '#fee2e2',
              borderRadius: '0.375rem',
              marginBottom: '1.25rem',
              border: '1px solid #fca5a5',
            }}
          >
            {formErro}
          </div>
        )}

        <form onSubmit={salvarCompromisso}>
          {renderEventLinkBox()}

          <div className="form-group" style={{ marginBottom: '1rem' }}>
            <label>Título do Compromisso *</label>
            <input
              type="text"
              className="form-input"
              placeholder="Ex: Reunião com Fornecedor"
              value={formTitulo}
              onChange={(e) => setFormTitulo(e.target.value)}
            />
          </div>

          <div className="form-group" style={{ marginBottom: '1rem' }}>
            <label>Descrição</label>
            <textarea
              className="form-input"
              rows={3}
              placeholder="Detalhes adicionais sobre o compromisso..."
              value={formDescricao}
              onChange={(e) => setFormDescricao(e.target.value)}
              style={{ resize: 'vertical' }}
            />
          </div>

          <div className="form-grid" style={{ gridTemplateColumns: '1fr 1fr', marginBottom: '1rem' }}>
            <div className="form-group">
              <label>Data *</label>
              <input
                type="date"
                className="form-input"
                value={formData}
                onChange={(e) => setFormData(e.target.value)}
              />
            </div>
            <div className="form-group">
              <label>Tipo *</label>
              <select className="form-select" value={formTipo} onChange={(e) => setFormTipo(e.target.value as TipoCompromisso)}>
                <option value="Reunião">Reunião</option>
                <option value="Visita">Visita</option>
                <option value="Apresentação">Apresentação</option>
                <option value="Outro">Outro</option>
              </select>
            </div>
          </div>

          <div className="form-grid" style={{ gridTemplateColumns: '1fr 1fr', marginBottom: '1rem' }}>
            <div className="form-group">
              <label>Horário de Início *</label>
              <input
                type="time"
                className="form-input"
                value={formHoraInicio}
                onChange={(e) => setFormHoraInicio(e.target.value)}
              />
            </div>
            <div className="form-group">
              <label>Horário de Término *</label>
              <input
                type="time"
                className="form-input"
                value={formHoraFim}
                onChange={(e) => setFormHoraFim(e.target.value)}
              />
            </div>
          </div>

          <div className="form-group" style={{ marginBottom: '1.5rem' }}>
            <label>Local *</label>
            <input
              type="text"
              className="form-input"
              placeholder="Ex: Sala de Reuniões A"
              value={formLocal}
              onChange={(e) => setFormLocal(e.target.value)}
            />
          </div>

          <div className="form-actions">
            <button
              type="button"
              className="btn-outline"
              onClick={() => {
                setAgendaView('list')
                setCompromissoEditId(null)
              }}
            >
              Cancelar
            </button>
            <button type="submit" className="action-btn">
              {agendaView === 'create-compromisso' ? 'Criar Compromisso' : 'Salvar Alterações'}
            </button>
          </div>
        </form>
      </div>
    )
  }

  if (agendaView === 'create-compromisso') {
    return <div className="agenda-container container">{renderFormCompromisso('Novo Compromisso', false)}</div>
  }

  if (agendaView === 'edit-compromisso') {
    return <div className="agenda-container container">{renderFormCompromisso('Editar Compromisso', true)}</div>
  }

  return (
    <div className="agenda-container container">
      <div className="agenda-header">
        <div className="title-area">
          <h1 style={{ fontSize: '2rem', fontWeight: 700, color: '#111827', margin: 0 }}>Agenda</h1>
          <p style={{ color: '#6b7280', fontSize: '0.95rem', margin: '0.25rem 0 0 0' }}>
            Gerencie compromissos e lembretes automáticos
          </p>
        </div>
        <div className="agenda-header-actions">
          <button type="button" className="btn-lembrete" onClick={abrirModalLembrete}>
            <svg width="18" height="18" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
              <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9" />
              <path d="M13.73 21a2 2 0 0 1-3.46 0" />
            </svg>
            Novo Lembrete
          </button>
          <button type="button" className="btn-compromisso" onClick={abrirCriarCompromisso}>
            + Novo Compromisso
          </button>
        </div>
      </div>

      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-info">
            <div className="stat-label">Compromissos Hoje</div>
            <div className="stat-value">{stats.hoje}</div>
            <div className="stat-sub">{stats.concluidosHoje} concluídos</div>
          </div>
          <div className="stat-icon agenda-hoje">
            <svg width="20" height="20" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
              <rect x="3" y="4" width="18" height="18" rx="2" />
              <line x1="16" y1="2" x2="16" y2="6" />
              <line x1="8" y1="2" x2="8" y2="6" />
              <line x1="3" y1="10" x2="21" y2="10" />
            </svg>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-info">
            <div className="stat-label">Esta Semana</div>
            <div className="stat-value">{stats.semana}</div>
            <div className="stat-sub">{stats.pendentesSemana} pendentes</div>
          </div>
          <div className="stat-icon agenda-semana">
            <svg width="20" height="20" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
              <circle cx="12" cy="12" r="10" />
              <polyline points="12 6 12 12 16 14" />
            </svg>
          </div>
        </div>
        <div
          className="stat-card"
          style={{ cursor: 'pointer' }}
          onClick={() => setShowLembretesPanel(true)}
          role="button"
          tabIndex={0}
          onKeyDown={(e) => e.key === 'Enter' && setShowLembretesPanel(true)}
        >
          <div className="stat-info">
            <div className="stat-label">Com Lembrete</div>
            <div className="stat-value">{stats.comLembrete}</div>
            <div className="stat-sub">{stats.lembretesPendentes} lembretes pendentes</div>
          </div>
          <div className="stat-icon agenda-com-lembrete">
            <svg width="20" height="20" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
              <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9" />
            </svg>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-info">
            <div className="stat-label">Lembretes Avulsos</div>
            <div className="stat-value">{stats.avulsos}</div>
            <div className="stat-sub">{stats.avulsos} ativos</div>
          </div>
          <div className="stat-icon agenda-avulsos">
            <svg width="20" height="20" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
              <rect x="3" y="4" width="18" height="18" rx="2" />
              <line x1="3" y1="10" x2="21" y2="10" />
            </svg>
          </div>
        </div>
      </div>

      <div className="agenda-body">
        <div className="agenda-date-column">
          <div className="agenda-month-label">Abril 2026</div>
          <div className="agenda-date-list">
            {DIAS_ABRIL_2026.map((dia) => (
              <button
                key={dia.key}
                type="button"
                className={`agenda-date-item ${dataSelecionada === dia.key ? 'active' : ''}`}
                onClick={() => setDataSelecionada(dia.key)}
              >
                <span>
                  <span className="agenda-date-item-day">{dia.label}</span> {dia.num}
                </span>
                {(contagemPorDia[dia.key] ?? 0) > 0 && (
                  <span className="agenda-date-badge">{contagemPorDia[dia.key]}</span>
                )}
              </button>
            ))}
          </div>
        </div>

        <div className="agenda-schedule-column">
          <h3 className="agenda-day-title">{formatarDataLonga(dataSelecionada)}</h3>

          {compromissosDoDia.length === 0 ? (
            <div className="agenda-empty-day">Nenhum compromisso neste dia.</div>
          ) : (
            compromissosDoDia.map((c) => {
              const qtdLembretes = contarLembretesCompromisso(c.id)
              const statusClass =
                c.status === 'Em andamento' ? 'em-andamento' : c.status === 'Concluído' ? 'concluido' : ''
              return (
                <div key={c.id} className="compromisso-card">
                  <div className="compromisso-card-header">
                    <div className="compromisso-tags">
                      <span className="tag-tipo">{c.tipo}</span>
                      <span className={`tag-status ${statusClass}`}>{c.status}</span>
                      {qtdLembretes > 0 && (
                        <span className="tag-lembrete">
                          <svg width="12" height="12" fill="currentColor" viewBox="0 0 24 24">
                            <path d="M12 22c1.1 0 2-.9 2-2h-4c0 1.1.9 2 2 2zm6-6v-5c0-3.07-1.63-5.64-4.5-6.32V4c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5v.68C7.64 5.36 6 7.92 6 11v5l-2 2v1h16v-1l-2-2z" />
                          </svg>
                          {qtdLembretes} lembrete{qtdLembretes > 1 ? 's' : ''}
                        </span>
                      )}
                    </div>
                    <button type="button" className="btn-editar-link" onClick={() => abrirEditarCompromisso(c.id)}>
                      Editar
                    </button>
                  </div>
                  <h4 className="compromisso-card-title">{c.titulo}</h4>
                  <div className="compromisso-evento-link">
                    <svg width="14" height="14" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                      <path d="M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71" />
                      <path d="M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71" />
                    </svg>
                    {nomeEvento(c.eventoId)}
                  </div>
                  {c.descricao && <p className="compromisso-descricao">{c.descricao}</p>}
                  <div className="compromisso-meta">
                    <span>
                      <svg width="14" height="14" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                        <circle cx="12" cy="12" r="10" />
                        <polyline points="12 6 12 12 16 14" />
                      </svg>
                      {c.horaInicio} - {c.horaFim}
                    </span>
                    <span>
                      <svg width="14" height="14" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                        <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z" />
                        <circle cx="12" cy="10" r="3" />
                      </svg>
                      {c.local}
                    </span>
                  </div>
                </div>
              )
            })
          )}

          <div className="info-box" style={{ marginTop: '1rem' }}>
            <div className="info-box-title">
              <svg width="16" height="16" fill="currentColor" viewBox="0 0 20 20">
                <path
                  fillRule="evenodd"
                  d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z"
                  clipRule="evenodd"
                />
              </svg>
              Sistema de Validação Ativo
            </div>
            <ul className="info-box-list">
              <li>Todo compromisso deve estar vinculado a um Evento.</li>
              <li>Não é possível agendar compromissos em horários sobrepostos ou datas passadas.</li>
            </ul>
          </div>
        </div>
      </div>

      {showLembretesPanel && (
        <>
          <div
            className="modal-overlay"
            style={{ zIndex: 99 }}
            onClick={() => setShowLembretesPanel(false)}
            aria-hidden
          />
          <aside className="lembretes-panel" style={{ zIndex: 100 }}>
            <LembretesNotificacaoPopup
              variant="panel"
              lembretes={lembretes}
              compromissos={compromissos}
              eventos={eventos}
              onClose={() => setShowLembretesPanel(false)}
              onVerAgenda={() => setShowLembretesPanel(false)}
            />
          </aside>
        </>
      )}

      {showLembreteModal && (
        <div className="modal-overlay lembrete-modal">
          <div className="modal-container" onClick={(e) => e.stopPropagation()}>
            <div className="lembrete-modal-header">
              <div className="lembrete-modal-icon">
                <svg width="22" height="22" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M12 22c1.1 0 2-.9 2-2h-4c0 1.1.9 2 2 2zm6-6v-5c0-3.07-1.63-5.64-4.5-6.32V4c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5v.68C7.64 5.36 6 7.92 6 11v5l-2 2v1h16v-1l-2-2z" />
                </svg>
              </div>
              <div style={{ flex: 1 }}>
                <h3 className="modal-title" style={{ margin: 0 }}>
                  Novo Lembrete
                </h3>
                <p className="modal-description" style={{ margin: '0.25rem 0 0' }}>
                  Configure um lembrete para evento ou compromisso
                </p>
              </div>
              <button type="button" className="lembretes-panel-close" onClick={() => setShowLembreteModal(false)}>
                <svg width="20" height="20" fill="none" stroke="currentColor" strokeWidth="2">
                  <line x1="18" y1="6" x2="6" y2="18" />
                  <line x1="6" y1="6" x2="18" y2="18" />
                </svg>
              </button>
            </div>

            <form onSubmit={criarLembrete}>
              <p style={{ fontWeight: 600, fontSize: '0.9rem', marginBottom: '0.75rem' }}>
                A que este lembrete se refere?
              </p>
              <div className="lembrete-tipo-toggle">
                <button
                  type="button"
                  className={`lembrete-tipo-btn ${lembreteVinculo === 'evento' ? 'selected evento' : ''}`}
                  onClick={() => setLembreteVinculo('evento')}
                >
                  <svg width="24" height="24" fill="none" stroke="#7c3aed" strokeWidth="2" viewBox="0 0 24 24">
                    <rect x="3" y="4" width="18" height="18" rx="2" />
                  </svg>
                  <div className="lembrete-tipo-btn-title">Evento</div>
                  <div className="lembrete-tipo-btn-sub">Lembrete avulso</div>
                </button>
                <button
                  type="button"
                  className={`lembrete-tipo-btn ${lembreteVinculo === 'compromisso' ? 'selected compromisso' : ''}`}
                  onClick={() => setLembreteVinculo('compromisso')}
                >
                  <svg width="24" height="24" fill="none" stroke="#2563eb" strokeWidth="2" viewBox="0 0 24 24">
                    <path d="M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71" />
                  </svg>
                  <div className="lembrete-tipo-btn-title">Compromisso</div>
                  <div className="lembrete-tipo-btn-sub">Específico</div>
                </button>
              </div>

              <div className="alert-box blue" style={{ marginBottom: '1rem' }}>
                <div className="alert-content">
                  <p style={{ margin: 0, fontSize: '0.85rem' }}>
                    {lembreteVinculo === 'evento'
                      ? 'Este lembrete será vinculado diretamente a um evento, sem associação com um compromisso específico.'
                      : 'Selecione o evento para filtrar os compromissos disponíveis e, em seguida, escolha o compromisso ao qual deseja vincular o lembrete.'}
                  </p>
                </div>
              </div>

              {lembreteErro && (
                <div
                  style={{
                    padding: '0.75rem',
                    backgroundColor: '#fee2e2',
                    borderRadius: '0.375rem',
                    marginBottom: '1rem',
                    fontSize: '0.85rem',
                    color: '#991b1b',
                  }}
                >
                  {lembreteErro}
                </div>
              )}

              {lembreteVinculo === 'evento' ? (
                <div className="form-group" style={{ marginBottom: '1rem' }}>
                  <label>Evento *</label>
                  <select
                    className="form-select"
                    value={lembreteEventoId}
                    onChange={(e) => setLembreteEventoId(e.target.value)}
                  >
                    <option value="">Selecione o evento...</option>
                    {eventos.map((ev) => (
                      <option key={ev.id} value={ev.id}>
                        {ev.nome}
                      </option>
                    ))}
                  </select>
                </div>
              ) : (
                <>
                  <div className="form-group" style={{ marginBottom: '1rem' }}>
                    <label>Filtrar por Evento</label>
                    <select
                      className="form-select"
                      value={lembreteFiltroEventoId}
                      onChange={(e) => {
                        setLembreteFiltroEventoId(e.target.value)
                        setLembreteCompromissoId('')
                      }}
                    >
                      <option value="">Todos os eventos</option>
                      {eventos.map((ev) => (
                        <option key={ev.id} value={ev.id}>
                          {ev.nome}
                        </option>
                      ))}
                    </select>
                  </div>
                  <div className="form-group" style={{ marginBottom: '1rem' }}>
                    <label>Compromisso *</label>
                    <select
                      className="form-select"
                      value={lembreteCompromissoId}
                      onChange={(e) => setLembreteCompromissoId(e.target.value)}
                    >
                      <option value="">Selecione o compromisso...</option>
                      {compromissosFiltradosLembrete.map((c) => (
                        <option key={c.id} value={c.id}>
                          {c.titulo} — {c.data} {c.horaInicio}
                        </option>
                      ))}
                    </select>
                  </div>
                </>
              )}

              <div className="form-grid" style={{ gridTemplateColumns: '1fr 1fr', marginBottom: '1.5rem' }}>
                <div className="form-group">
                  <label>Data do Lembrete *</label>
                  <input
                    type="date"
                    className="form-input"
                    value={lembreteData}
                    onChange={(e) => setLembreteData(e.target.value)}
                  />
                </div>
                <div className="form-group">
                  <label>Horário *</label>
                  <input
                    type="time"
                    className="form-input"
                    value={lembreteHorario}
                    onChange={(e) => setLembreteHorario(e.target.value)}
                  />
                </div>
              </div>

              <div className="modal-actions">
                <button type="button" className="modal-btn-cancelar" onClick={() => setShowLembreteModal(false)}>
                  Cancelar
                </button>
                <button type="submit" className="btn-criar-lembrete">
                  Criar Lembrete
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}
