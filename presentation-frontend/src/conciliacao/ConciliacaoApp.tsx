import './conciliacao.css';
import ConciliacaoPage from './ConciliacaoPage';
import FinanceiroShell from '../financeiro/components/FinanceiroShell';
import { useEventosFinanceiro } from '../financeiro/hooks/useEventosFinanceiro';

export default function ConciliacaoApp() {
  const ctx = useEventosFinanceiro();

  return (
    <FinanceiroShell {...ctx}>
      {ctx.eventoId && (
        <ConciliacaoPage eventoId={ctx.eventoId} eventoNome={ctx.eventoNome} />
      )}
    </FinanceiroShell>
  );
}
