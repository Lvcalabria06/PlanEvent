import type { ReactNode } from 'react';
import EventoSelectorBar from './EventoSelectorBar';
import type { EventoResumo } from '../../api/financeiroApi';

interface Props {
  eventos: EventoResumo[];
  eventoId: string;
  setEventoId: (id: string) => void;
  carregando: boolean;
  erro: boolean;
  children: ReactNode;
}

export default function FinanceiroShell({
  eventos,
  eventoId,
  setEventoId,
  carregando,
  erro,
  children,
}: Props) {
  if (carregando) {
    return (
      <div className="content-card" style={{ textAlign: 'center', padding: '2rem', color: '#6b7280' }}>
        Carregando financeiro…
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
      <div className="content-card" style={{ textAlign: 'center', padding: '2rem', color: '#6b7280' }}>
        <p style={{ margin: 0 }}>
          Nenhum evento cadastrado. Crie um evento primeiro para utilizar este módulo.
        </p>
      </div>
    );
  }

  return (
    <div className="financeiro-root">
      <EventoSelectorBar eventos={eventos} eventoId={eventoId} onChange={setEventoId} />
      {eventoId ? children : null}
    </div>
  );
}
