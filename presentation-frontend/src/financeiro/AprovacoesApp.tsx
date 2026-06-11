import './financeiro.css';
import AprovacoesPage from './AprovacoesPage';
import FinanceiroShell from './components/FinanceiroShell';
import { useEventosFinanceiro } from './hooks/useEventosFinanceiro';

/**
 * Aprovações de despesas — montado apenas na aba "Aprovações" da sidebar.
 */
export default function AprovacoesApp() {
  const ctx = useEventosFinanceiro();

  return (
    <FinanceiroShell {...ctx}>
      {ctx.eventoId && (
        <AprovacoesPage eventoId={ctx.eventoId} eventoNome={ctx.eventoNome} />
      )}
    </FinanceiroShell>
  );
}
