import type { EventoResumo } from '../../api/financeiroApi';

interface Props {
  eventos: EventoResumo[];
  eventoId: string;
  onChange: (id: string) => void;
}

export default function EventoSelectorBar({ eventos, eventoId, onChange }: Props) {
  if (eventos.length <= 1) return null;

  return (
    <div className="fin-evento-select">
      <label htmlFor="fin-evento-select">Evento</label>
      <select
        id="fin-evento-select"
        className="form-select"
        style={{ maxWidth: 360 }}
        value={eventoId}
        onChange={(e) => onChange(e.target.value)}
      >
        {eventos.map((e) => (
          <option key={e.id} value={e.id}>
            {e.nome || e.id}
          </option>
        ))}
      </select>
    </div>
  );
}
