import { useCallback, useEffect, useState } from 'react';
import { financeiroApi } from '../api/financeiroApi';
import type { Despesa, FornecedorResumo } from '../api/financeiroApi';
import { ApiError } from '../api/http';

function formatBrl(v: number) {
  return v.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
}

interface Props {
  eventoId: string;
  eventoNome: string;
}

export default function AprovacoesPage({ eventoId, eventoNome }: Props) {
  const [pendentes, setPendentes] = useState<Despesa[]>([]);
  const [fornecedores, setFornecedores] = useState<FornecedorResumo[]>([]);
  const [erro, setErro] = useState<string | null>(null);
  const [rejeitarId, setRejeitarId] = useState<string | null>(null);
  const [motivo, setMotivo] = useState('');

  const carregar = useCallback(async () => {
    setErro(null);
    try {
      const [listaPendentes, listaFornecedores] = await Promise.all([
        financeiroApi.listarPendentes(eventoId),
        financeiroApi.listarFornecedores()
      ]);
      setPendentes(listaPendentes);
      setFornecedores(listaFornecedores);
    } catch (e) {
      setErro(e instanceof ApiError ? e.message : 'Erro ao carregar.');
    }
  }, [eventoId]);

  useEffect(() => {
    carregar();
  }, [carregar]);

  const aprovar = async (id: string) => {
    try {
      await financeiroApi.aprovarDespesa(eventoId, id, 'aprovador@empresa.com');
      await carregar();
    } catch (e) {
      setErro(e instanceof ApiError ? e.message : 'Erro ao aprovar.');
    }
  };

  const confirmarRejeicao = async () => {
    if (!rejeitarId || motivo.trim().length < 3) {
      setErro('Informe o motivo da rejeição (mín. 3 caracteres).');
      return;
    }
    try {
      await financeiroApi.rejeitarDespesa(
        eventoId,
        rejeitarId,
        'aprovador@empresa.com',
        motivo
      );
      setRejeitarId(null);
      setMotivo('');
      await carregar();
    } catch (e) {
      setErro(e instanceof ApiError ? e.message : 'Erro ao rejeitar.');
    }
  };

  return (
    <div>
      <div className="header-section" style={{ marginBottom: '1.5rem' }}>
        <div className="title-area">
          <h1 style={{ fontSize: '2rem', fontWeight: 700, color: '#111827', margin: 0 }}>
            Aprovações de Despesas
          </h1>
          <p style={{ color: '#6b7280', margin: '0.25rem 0 0 0' }}>
            Evento: <strong>{eventoNome}</strong> — despesas que atingiram 80% do orçamento
          </p>
        </div>
      </div>

      {erro && (
        <div className="error-message" style={{ marginBottom: '1rem', padding: '0.75rem' }}>
          {erro}
        </div>
      )}

      <div className="content-card">
        <div className="table-container">
          <table className="data-table">
            <thead>
              <tr>
                <th>Categoria</th>
                <th>Fornecedor</th>
                <th>Valor</th>
                <th>Data lançamento</th>
                <th>Ações</th>
              </tr>
            </thead>
            <tbody>
              {pendentes.length === 0 ? (
                <tr>
                  <td colSpan={5} style={{ textAlign: 'center', padding: '2rem', color: '#6b7280' }}>
                    Nenhuma despesa aguardando aprovação.
                  </td>
                </tr>
              ) : (
                pendentes.map((d) => (
                  <tr key={d.id}>
                    <td>
                      <span className="competency-tag">{d.categoria}</span>
                    </td>
                    <td>{fornecedores.find(f => f.id === d.fornecedorId)?.nome ?? d.fornecedorId}</td>
                    <td>{formatBrl(d.valor)}</td>
                    <td>{new Date(d.dataHoraLancamento).toLocaleString('pt-BR')}</td>
                    <td>
                      <button className="edit-link" style={{ color: '#10b981' }} onClick={() => aprovar(d.id)}>
                        Aprovar
                      </button>
                      <button
                        className="edit-link"
                        style={{ marginLeft: 12, color: '#ef4444' }}
                        onClick={() => setRejeitarId(d.id)}
                      >
                        Rejeitar
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {rejeitarId && (
        <div className="modal-overlay">
          <div className="modal-container">
            <h3 className="modal-title">Rejeitar despesa</h3>
            <div className="form-group">
              <label>Motivo *</label>
              <textarea
                className="form-input"
                rows={4}
                value={motivo}
                onChange={(e) => setMotivo(e.target.value)}
              />
            </div>
            <div className="modal-actions">
              <button type="button" className="modal-btn-cancelar" onClick={() => setRejeitarId(null)}>
                Cancelar
              </button>
              <button type="button" className="modal-btn-confirm" onClick={confirmarRejeicao}>
                Confirmar rejeição
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
