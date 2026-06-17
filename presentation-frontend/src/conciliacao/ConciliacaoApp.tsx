import './conciliacao.css';
import ConciliacaoPage from './ConciliacaoPage';
import { useEventosFinanceiro } from '../financeiro/hooks/useEventosFinanceiro';

export default function ConciliacaoApp() {
  const { eventos, eventoId, setEventoId, eventoNome, carregando, erro } = useEventosFinanceiro();

  if (carregando) {
    return (
      <div className="content-card" style={{ textAlign: 'center', padding: '2rem', color: '#6b7280' }}>
        Carregando conciliação…
      </div>
    );
  }

  if (erro) {
    return (
      <div className="content-card" style={{ textAlign: 'center', padding: '2rem', color: '#6b7280' }}>
        <p style={{ margin: 0 }}>
          Não foi possível carregar os eventos. Verifique se o backend está em execução na porta 3000.
        </p>
      </div>
    );
  }

  if (eventos.length === 0) {
    return (
      <div className="content-card" style={{ textAlign: 'center', padding: '3rem', color: '#6b7280' }}>
        <svg width="48" height="48" fill="none" stroke="currentColor" strokeWidth="1.5" viewBox="0 0 24 24" style={{ marginBottom: '1rem', color: '#9ca3af' }}>
          <rect x="3" y="4" width="18" height="18" rx="2" ry="2" />
          <line x1="16" y1="2" x2="16" y2="6" />
          <line x1="8" y1="2" x2="8" y2="6" />
          <line x1="3" y1="10" x2="21" y2="10" />
        </svg>
        <h3 style={{ fontSize: '1.1rem', fontWeight: 600, color: '#374151', margin: '0 0 0.5rem 0' }}>
          Nenhum evento cadastrado
        </h3>
        <p style={{ margin: 0, fontSize: '0.9rem' }}>
          Crie um evento na seção <strong>Eventos</strong> para utilizar a conciliação.
        </p>
      </div>
    );
  }

  return (
    <div>
      <div className="fin-evento-select" style={{ marginBottom: '1.5rem' }}>
        <label htmlFor="conc-evento-select">Evento</label>
        <select
          id="conc-evento-select"
          className="form-select"
          style={{ maxWidth: 400 }}
          value={eventoId}
          onChange={(e) => setEventoId(e.target.value)}
        >
          {eventos.map((e) => (
            <option key={e.id} value={e.id}>
              {e.nome || e.id}
            </option>
          ))}
        </select>
      </div>
      {eventoId && <ConciliacaoPage eventoId={eventoId} eventoNome={eventoNome} />}
    </div>
  );
}
