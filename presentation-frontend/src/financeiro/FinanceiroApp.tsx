import { MemoryRouter, Navigate, Route, Routes, useNavigate, useLocation } from 'react-router';
import './financeiro.css';
import FinanceiroPage from './FinanceiroPage';
import FinanceiroShell from './components/FinanceiroShell';
import { useEventosFinanceiro } from './hooks/useEventosFinanceiro';

type AbaFinanceiro = 'despesas' | 'relatorios';

function FinanceiroRoutes({
  eventoId,
  eventoNome,
}: {
  eventoId: string;
  eventoNome: string;
}) {
  const navigate = useNavigate();
  const location = useLocation();

  const abaInicial: AbaFinanceiro = location.pathname.includes('/relatorios')
    ? 'relatorios'
    : 'despesas';

  const onAbaChange = (aba: AbaFinanceiro) => {
    navigate(aba === 'relatorios' ? '/financeiro/relatorios' : '/financeiro/despesas');
  };

  return (
    <FinanceiroPage
      eventoId={eventoId}
      eventoNome={eventoNome}
      abaInicial={abaInicial}
      onAbaChange={onAbaChange}
    />
  );
}

/**
 * Módulo Financeiro — rotas internas /financeiro/despesas e /financeiro/relatorios.
 * Montado apenas na aba "Financeiro" da sidebar (MemoryRouter isolado, como TarefasApp).
 */
export default function FinanceiroApp() {
  const ctx = useEventosFinanceiro();

  return (
    <FinanceiroShell {...ctx}>
      {ctx.eventoId && (
        <MemoryRouter initialEntries={['/financeiro/despesas']}>
          <Routes>
            <Route
              path="/financeiro/despesas"
              element={
                <FinanceiroRoutes eventoId={ctx.eventoId} eventoNome={ctx.eventoNome} />
              }
            />
            <Route
              path="/financeiro/relatorios"
              element={
                <FinanceiroRoutes eventoId={ctx.eventoId} eventoNome={ctx.eventoNome} />
              }
            />
            <Route path="*" element={<Navigate to="/financeiro/despesas" replace />} />
          </Routes>
        </MemoryRouter>
      )}
    </FinanceiroShell>
  );
}
