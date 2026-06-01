import type { CompromissoAgenda, EventoAgenda, LembreteAgenda } from './types'
import { formatarLembreteDataHora } from './agendaValidacao'

interface LembretesNotificacaoPopupProps {
  lembretes: LembreteAgenda[]
  compromissos: CompromissoAgenda[]
  eventos: EventoAgenda[]
  onClose: () => void
  onVerAgenda: () => void
  variant?: 'dropdown' | 'panel'
}

export default function LembretesNotificacaoPopup({
  lembretes,
  compromissos,
  eventos,
  onClose,
  onVerAgenda,
  variant = 'dropdown',
}: LembretesNotificacaoPopupProps) {
  const pendentes = lembretes.filter((l) => !l.notificado)

  function nomeEvento(eventoId: string): string {
    return eventos.find((e) => e.id === eventoId)?.nome ?? 'Evento'
  }

  function tituloLembrete(l: LembreteAgenda): string {
    const comp = l.compromissoId ? compromissos.find((c) => c.id === l.compromissoId) : null
    return comp?.titulo ?? nomeEvento(l.eventoId ?? '')
  }

  const conteudo = (
    <div
      className={variant === 'panel' ? 'lembretes-panel-inner' : 'lembretes-dropdown'}
      role="dialog"
      aria-label="Lembretes pendentes"
    >
        <div className="lembretes-dropdown-header">
          <div className="lembretes-dropdown-header-left">
            <div className="lembretes-dropdown-icon-wrap">
              <svg width="18" height="18" fill="#ea580c" viewBox="0 0 24 24">
                <path d="M12 22c1.1 0 2-.9 2-2h-4c0 1.1.9 2 2 2zm6-6v-5c0-3.07-1.63-5.64-4.5-6.32V4c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5v.68C7.64 5.36 6 7.92 6 11v5l-2 2v1h16v-1l-2-2z" />
              </svg>
            </div>
            <div>
              <strong className="lembretes-dropdown-title">Lembretes</strong>
              <p className="lembretes-dropdown-subtitle">{pendentes.length} pendentes</p>
            </div>
          </div>
          <button type="button" className="lembretes-panel-close" onClick={onClose} aria-label="Fechar">
            <svg width="18" height="18" fill="none" stroke="currentColor" strokeWidth="2">
              <line x1="18" y1="6" x2="6" y2="18" />
              <line x1="6" y1="6" x2="18" y2="18" />
            </svg>
          </button>
        </div>

        <div className="lembretes-dropdown-list">
          <div className="lembretes-panel-section-title">PENDENTES</div>
          {pendentes.length === 0 ? (
            <p className="lembretes-dropdown-empty">Nenhum lembrete pendente.</p>
          ) : (
            pendentes.map((l) => {
              const comp = l.compromissoId ? compromissos.find((c) => c.id === l.compromissoId) : null
              return (
                <div key={l.id} className="lembrete-item lembrete-item-dropdown">
                  <div className={`lembrete-item-icon ${l.compromissoId ? 'compromisso' : 'evento'}`}>
                    {l.compromissoId ? (
                      <svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                        <path d="M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71" />
                      </svg>
                    ) : (
                      <svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                        <rect x="3" y="4" width="18" height="18" rx="2" />
                      </svg>
                    )}
                  </div>
                  <div className="lembrete-item-body">
                    <p className="lembrete-item-title">{tituloLembrete(l)}</p>
                    <p className="lembrete-item-horario">
                      <svg width="14" height="14" fill="#ea580c" viewBox="0 0 24 24">
                        <path d="M12 22c1.1 0 2-.9 2-2h-4c0 1.1.9 2 2 2zm6-6v-5c0-3.07-1.63-5.64-4.5-6.32V4c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5v.68C7.64 5.36 6 7.92 6 11v5l-2 2v1h16v-1l-2-2z" />
                      </svg>
                      {formatarLembreteDataHora(l.data, l.horario)}
                    </p>
                    <p className="lembrete-item-contexto">
                      {comp ? (
                        <>
                          <svg width="12" height="12" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                            <circle cx="12" cy="12" r="10" />
                            <polyline points="12 6 12 12 16 14" />
                          </svg>
                          Compromisso: {comp.data} às {comp.horaInicio}
                        </>
                      ) : (
                        <span className="lembrete-avulso">Lembrete avulso de evento</span>
                      )}
                    </p>
                  </div>
                  <span className="lembrete-item-arrow" aria-hidden>
                    →
                  </span>
                </div>
              )
            })
          )}
        </div>

        <div className="lembretes-dropdown-footer">
          <button
            type="button"
            className="lembretes-ver-agenda-btn"
            onClick={() => {
              onVerAgenda()
              onClose()
            }}
          >
            <svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
              <rect x="3" y="4" width="18" height="18" rx="2" />
              <line x1="3" y1="10" x2="21" y2="10" />
            </svg>
            Ver todos na Agenda
          </button>
        </div>
    </div>
  )

  if (variant === 'panel') {
    return conteudo
  }

  return (
    <>
      <div className="lembretes-dropdown-backdrop" onClick={onClose} aria-hidden />
      {conteudo}
    </>
  )
}
