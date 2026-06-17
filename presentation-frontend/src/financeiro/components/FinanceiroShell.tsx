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
      <div className="content-card" style={{ textAlign: 'center', padding: '4rem 2rem', color: '#4b5563', display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '1rem' }}>
        <div style={{ fontSize: '3rem', opacity: 0.5 }}>📅</div>
        <h2 style={{ margin: 0, fontWeight: 600 }}>Nenhum evento cadastrado</h2>
        <p style={{ margin: 0, maxWidth: '400px' }}>
          Para começar a lançar despesas e emitir relatórios financeiros, você precisa criar um evento primeiro.
        </p>
      </div>
    );
  }

  if (!eventoId) {
    return (
      <div className="financeiro-root">
        <EventoSelectorBar eventos={eventos} eventoId={eventoId} onChange={setEventoId} />
        <div className="content-card" style={{ textAlign: 'center', padding: '4rem 2rem', color: '#4b5563', marginTop: '1.5rem', display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '1rem' }}>
          <div style={{ fontSize: '3rem', opacity: 0.5 }}>👆</div>
          <h2 style={{ margin: 0, fontWeight: 600 }}>Nenhum evento selecionado</h2>
          <p style={{ margin: 0 }}>
            Selecione um evento na barra superior para gerenciar seu orçamento e despesas.
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="financeiro-root">
      <EventoSelectorBar eventos={eventos} eventoId={eventoId} onChange={setEventoId} />
      {children}
    </div>
  );
}
