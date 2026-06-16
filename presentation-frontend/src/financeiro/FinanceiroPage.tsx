import { useCallback, useEffect, useState } from 'react';
import { financeiroApi } from '../api/financeiroApi';
import type { Despesa, Desvio, FornecedorResumo, Relatorio, Simulacao } from '../api/financeiroApi';
import { ApiError } from '../api/http';

const CATEGORIAS = [
  'ALIMENTACAO',
  'DECORACAO',
  'EQUIPAMENTO',
  'LOGISTICA',
  'MARKETING',
  'SERVICO',
  'OUTRO',
];

function formatBrl(v: number) {
  return v.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
}

function statusLabel(status: string) {
  switch (status) {
    case 'REGISTRADA':
      return 'Registrada';
    case 'PENDENTE_APROVACAO':
      return 'Pendente';
    case 'APROVADA':
      return 'Aprovada';
    case 'REJEITADA':
      return 'Rejeitada';
    default:
      return status;
  }
}

function statusClass(status: string) {
  switch (status) {
    case 'REGISTRADA':
      return 'fin-status-registrada';
    case 'PENDENTE_APROVACAO':
      return 'fin-status-pendente';
    case 'APROVADA':
      return 'fin-status-aprovada';
    case 'REJEITADA':
      return 'fin-status-rejeitada';
    default:
      return '';
  }
}

function classificacaoClass(c: string) {
  if (c === 'CRITICO') return 'fin-class-critico';
  if (c === 'ATENCAO' || c === 'ATENÇÃO') return 'fin-class-atencao';
  return 'fin-class-normal';
}

interface Props {
  eventoId: string;
  eventoNome: string;
  abaInicial?: 'despesas' | 'relatorios';
  onAbaChange?: (aba: 'despesas' | 'relatorios') => void;
}

export default function FinanceiroPage({
  eventoId,
  eventoNome,
  abaInicial = 'despesas',
  onAbaChange,
}: Props) {
  const [aba, setAba] = useState<'despesas' | 'relatorios'>(abaInicial);
  const [erro, setErro] = useState<string | null>(null);
  const [carregando, setCarregando] = useState(false);

  const [despesas, setDespesas] = useState<Despesa[]>([]);
  const [desvios, setDesvios] = useState<Desvio[]>([]);
  const [fornecedores, setFornecedores] = useState<FornecedorResumo[]>([]);
  const [filtroCategoria, setFiltroCategoria] = useState('');
  const [filtroFornecedor, setFiltroFornecedor] = useState('');

  const [mostrarForm, setMostrarForm] = useState(false);
  const [editando, setEditando] = useState<Despesa | null>(null);
  const [formCategoria, setFormCategoria] = useState('ALIMENTACAO');
  const [formFornecedorId, setFormFornecedorId] = useState('');
  const [formValor, setFormValor] = useState('');
  const [formData, setFormData] = useState('');

  const [relatorios, setRelatorios] = useState<Relatorio[]>([]);
  const [simulacao, setSimulacao] = useState<Simulacao | null>(null);
  const [relatorioDetalhe, setRelatorioDetalhe] = useState<Relatorio | null>(null);
  const [motivoOficial, setMotivoOficial] = useState('');
  const [mostrarModalOficial, setMostrarModalOficial] = useState(false);

  const tratarErro = (e: unknown) => {
    setErro(e instanceof ApiError ? e.message : 'Erro inesperado.');
  };

  const carregarDespesas = useCallback(async () => {
    const lista = await financeiroApi.listarDespesas(eventoId, {
      categoria: filtroCategoria || undefined,
      fornecedorId: filtroFornecedor || undefined,
    });
    setDespesas(lista);
    const d = await financeiroApi.listarDesvios(eventoId);
    setDesvios(d);
  }, [eventoId, filtroCategoria, filtroFornecedor]);

  const carregarRelatorios = useCallback(async () => {
    const lista = await financeiroApi.listarRelatorios(eventoId);
    setRelatorios(lista);
  }, [eventoId]);

  const recarregar = useCallback(async () => {
    setCarregando(true);
    setErro(null);
    try {
      const f = await financeiroApi.listarFornecedores();
      setFornecedores(f);
      if (!formFornecedorId && f.length > 0) setFormFornecedorId(f[0].id);
      await carregarDespesas();
      await carregarRelatorios();
    } catch (e) {
      tratarErro(e);
    } finally {
      setCarregando(false);
    }
  }, [carregarDespesas, carregarRelatorios, formFornecedorId]);

  useEffect(() => {
    recarregar();
  }, [recarregar]);

  useEffect(() => {
    setAba(abaInicial);
  }, [abaInicial]);

  const trocarAba = (nova: 'despesas' | 'relatorios') => {
    setAba(nova);
    onAbaChange?.(nova);
  };

  const abrirNova = () => {
    setEditando(null);
    setFormCategoria('ALIMENTACAO');
    setFormValor('');
    setFormData(new Date().toISOString().slice(0, 16));
    if (fornecedores.length > 0) setFormFornecedorId(fornecedores[0].id);
    setMostrarForm(true);
  };

  const abrirEditar = (d: Despesa) => {
    if (d.status !== 'REGISTRADA') return;
    setEditando(d);
    setFormCategoria(d.categoria);
    setFormFornecedorId(d.fornecedorId);
    setFormValor(String(d.valor));
    setFormData(d.data ? d.data.slice(0, 16) : '');
    setMostrarForm(true);
  };

  const salvarDespesa = async (e: React.FormEvent) => {
    e.preventDefault();
    setErro(null);
    try {
      const valor = parseFloat(formValor);
      const data = new Date(formData).toISOString();
      if (editando) {
        await financeiroApi.atualizarDespesa(eventoId, editando.id, { valor, data });
      } else {
        await financeiroApi.registrarDespesa(eventoId, {
          categoria: formCategoria,
          fornecedorId: formFornecedorId,
          valor,
          data,
        });
      }
      setMostrarForm(false);
      await carregarDespesas();
    } catch (err) {
      tratarErro(err);
    }
  };

  const excluir = async (d: Despesa) => {
    if (!window.confirm('Excluir esta despesa?')) return;
    try {
      await financeiroApi.excluirDespesa(eventoId, d.id);
      await carregarDespesas();
    } catch (err) {
      tratarErro(err);
    }
  };

  const simular = async () => {
    try {
      const s = await financeiroApi.simularRelatorio(eventoId);
      setSimulacao(s);
      setRelatorioDetalhe(s.preview);
    } catch (err) {
      tratarErro(err);
    }
  };

  const confirmarSimulacao = async (tipo: 'PRELIMINAR' | 'OFICIAL') => {
    if (!simulacao) return;
    if (tipo === 'OFICIAL' && !motivoOficial.trim() && relatorios.some((r) => r.tipo === 'OFICIAL')) {
      setErro('Informe o motivo da nova versão oficial.');
      return;
    }
    try {
      await financeiroApi.confirmarSimulacao(eventoId, simulacao.id, {
        tipo,
        motivoNovaVersaoOficial: tipo === 'OFICIAL' ? motivoOficial : undefined,
      });
      setSimulacao(null);
      setRelatorioDetalhe(null);
      setMotivoOficial('');
      await carregarRelatorios();
    } catch (err) {
      tratarErro(err);
    }
  };

  const pendentes = despesas.filter((d) => d.status === 'PENDENTE_APROVACAO').length;
  const totalRealizado = despesas
    .filter((d) => d.status !== 'REJEITADA')
    .reduce((s, d) => s + d.valor, 0);
  const ultimoRel = relatorios.length > 0 ? relatorios[relatorios.length - 1] : null;

  return (
    <div>
      <div className="header-section" style={{ marginBottom: '1.5rem' }}>
        <div className="title-area">
          <h1 style={{ fontSize: '2rem', fontWeight: 700, color: '#111827', margin: 0 }}>Financeiro</h1>
          <p style={{ color: '#6b7280', margin: '0.25rem 0 0 0' }}>
            Evento: <strong>{eventoNome}</strong>
          </p>
        </div>
        <div className="tab-nav">
          <button
            className={`tab-button ${aba === 'despesas' ? 'active' : ''}`}
            onClick={() => trocarAba('despesas')}
          >
            Despesas
          </button>
          <button
            className={`tab-button ${aba === 'relatorios' ? 'active' : ''}`}
            onClick={() => trocarAba('relatorios')}
          >
            Relatórios
          </button>
        </div>
      </div>

      {erro && (
        <div className="error-message" style={{ marginBottom: '1rem', padding: '0.75rem' }}>
          {erro}
        </div>
      )}

      {aba === 'despesas' && (
        <>
          <div className="stats-grid">
            <div className="stat-card">
              <div className="stat-info">
                <div className="stat-label">Despesas</div>
                <div className="stat-value">{despesas.length}</div>
              </div>
            </div>
            <div className="stat-card">
              <div className="stat-info">
                <div className="stat-label">Realizado ativo</div>
                <div className="stat-value" style={{ fontSize: '1.4rem' }}>
                  {formatBrl(totalRealizado)}
                </div>
              </div>
            </div>
            <div className="stat-card">
              <div className="stat-info">
                <div className="stat-label">Pendentes aprovação</div>
                <div className="stat-value">{pendentes}</div>
              </div>
            </div>
            <div className="stat-card">
              <div className="stat-info">
                <div className="stat-label">Categorias críticas</div>
                <div className="stat-value">
                  {desvios.filter((d) => d.classificacao === 'CRITICO').length}
                </div>
              </div>
            </div>
          </div>

          <div className="content-card" style={{ marginBottom: '1.5rem' }}>
            <h3 className="widget-title">Desvio por categoria</h3>
            <div className="fin-desvio-grid">
              {desvios.map((d) => (
                <div key={d.categoria} className="fin-desvio-card">
                  <div className="fin-desvio-card-title">{d.categoria}</div>
                  <div className="fin-desvio-meta">
                    Prev: {formatBrl(d.valorPrevisto)} · Real: {formatBrl(d.valorRealizado)}
                  </div>
                  <div className={`badge ${classificacaoClass(d.classificacao)}`}>
                    {d.desvioPercentual.toFixed(1)}% · {d.classificacao}
                  </div>
                </div>
              ))}
            </div>
          </div>

          <div className="content-card">
            <div className="card-header">
              <h2 className="card-title">Despesas do evento</h2>
              <button className="action-btn" onClick={abrirNova}>
                + Nova Despesa
              </button>
            </div>

            <div className="form-grid" style={{ gridTemplateColumns: '1fr 1fr 1fr auto', marginBottom: '1rem' }}>
              <div className="form-group">
                <label>Categoria</label>
                <select
                  className="form-select"
                  value={filtroCategoria}
                  onChange={(e) => setFiltroCategoria(e.target.value)}
                >
                  <option value="">Todas</option>
                  {CATEGORIAS.map((c) => (
                    <option key={c} value={c}>
                      {c}
                    </option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label>Fornecedor</label>
                <select
                  className="form-select"
                  value={filtroFornecedor}
                  onChange={(e) => setFiltroFornecedor(e.target.value)}
                >
                  <option value="">Todos</option>
                  {fornecedores.map((f) => (
                    <option key={f.id} value={f.id}>
                      {f.nome}
                    </option>
                  ))}
                </select>
              </div>
              <div className="form-group" style={{ alignSelf: 'end' }}>
                <button className="btn-outline" onClick={() => carregarDespesas()}>
                  Pesquisar
                </button>
              </div>
            </div>

            <div className="table-container">
              <table className="data-table">
                <thead>
                  <tr>
                    <th>Data</th>
                    <th>Categoria</th>
                    <th>Fornecedor</th>
                    <th>Valor</th>
                    <th>Status</th>
                    <th>Ações</th>
                  </tr>
                </thead>
                <tbody>
                  {despesas.length === 0 ? (
                    <tr>
                      <td colSpan={6} style={{ textAlign: 'center', padding: '2rem', color: '#6b7280' }}>
                        Nenhuma despesa encontrada.
                      </td>
                    </tr>
                  ) : (
                    despesas.map((d) => (
                      <tr key={d.id} style={{ opacity: d.status === 'REJEITADA' ? 0.6 : 1 }}>
                        <td>{new Date(d.data).toLocaleDateString('pt-BR')}</td>
                        <td>
                          <span className="competency-tag">{d.categoria}</span>
                        </td>
                        <td>
                          {fornecedores.find((f) => f.id === d.fornecedorId)?.nome ?? d.fornecedorId}
                        </td>
                        <td>{formatBrl(d.valor)}</td>
                        <td>
                          <span className={`badge ${statusClass(d.status)}`}>
                            {statusLabel(d.status)}
                          </span>
                        </td>
                        <td>
                          {d.status === 'REGISTRADA' && (
                            <>
                              <button className="edit-link" onClick={() => abrirEditar(d)}>
                                Editar
                              </button>
                              <button
                                className="edit-link"
                                style={{ marginLeft: 8, color: '#ef4444' }}
                                onClick={() => excluir(d)}
                              >
                                Excluir
                              </button>
                            </>
                          )}
                          {d.status !== 'REGISTRADA' && (
                            <span style={{ color: '#9ca3af', fontSize: '0.85rem' }}>Somente leitura</span>
                          )}
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </>
      )}

      {aba === 'relatorios' && (
        <>
          <div className="stats-grid">
            <div className="stat-card">
              <div className="stat-info">
                <div className="stat-label">Relatórios emitidos</div>
                <div className="stat-value">{relatorios.length}</div>
              </div>
            </div>
            <div className="stat-card">
              <div className="stat-info">
                <div className="stat-label">Última emissão</div>
                <div className="stat-value" style={{ fontSize: '1rem' }}>
                  {ultimoRel?.dataGeracao
                    ? new Date(ultimoRel.dataGeracao).toLocaleString('pt-BR')
                    : '—'}
                </div>
              </div>
            </div>
            <div className="stat-card">
              <div className="stat-info">
                <div className="stat-label">Saúde (último)</div>
                <div className="stat-value" style={{ fontSize: '1.2rem' }}>
                  {ultimoRel ? (
                    <span className={`badge ${classificacaoClass(ultimoRel.saudeFinanceira.classificacao)}`}>
                      {ultimoRel.saudeFinanceira.classificacao} ({ultimoRel.saudeFinanceira.score.toFixed(0)})
                    </span>
                  ) : (
                    '—'
                  )}
                </div>
              </div>
            </div>
          </div>

          <div className="content-card" style={{ marginBottom: '1rem' }}>
            <div className="card-header">
              <h2 className="card-title">Emissão de relatórios</h2>
              <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap' }}>
                <button className="btn-outline" onClick={simular}>
                  Simular relatório
                </button>
                <button
                  className="btn-outline"
                  onClick={async () => {
                    try {
                      const r = await financeiroApi.gerarPreliminar(eventoId);
                      setRelatorioDetalhe(r);
                      await carregarRelatorios();
                    } catch (err) {
                      tratarErro(err);
                    }
                  }}
                >
                  Gerar preliminar
                </button>
                <button className="action-btn" onClick={() => setMostrarModalOficial(true)}>
                  Gerar oficial
                </button>
              </div>
            </div>

            {simulacao && (
              <div className="alert-box yellow" style={{ marginBottom: '1rem' }}>
                <div className="alert-content">
                  <strong>Modo simulação</strong> — nada foi salvo. Confirme para persistir.
                  <div style={{ marginTop: '0.75rem', display: 'flex', gap: '0.5rem' }}>
                    <button className="btn-outline" onClick={() => { setSimulacao(null); setRelatorioDetalhe(null); }}>
                      Descartar
                    </button>
                    <button className="btn-outline" onClick={() => confirmarSimulacao('PRELIMINAR')}>
                      Confirmar preliminar
                    </button>
                    <button className="action-btn" onClick={() => confirmarSimulacao('OFICIAL')}>
                      Confirmar oficial
                    </button>
                  </div>
                </div>
              </div>
            )}
          </div>

          {relatorioDetalhe && (
            <div className="content-card fin-relatorio-detalhe">
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <h3 className="widget-title">
                  {relatorioDetalhe.id ? 'Relatório persistido' : 'Pré-visualização'}
                  {relatorioDetalhe.tipo && (
                    <span className={`badge fin-tipo-${relatorioDetalhe.tipo.toLowerCase()}`} style={{ marginLeft: 8 }}>
                      {relatorioDetalhe.tipo}
                    </span>
                  )}
                </h3>
                {relatorioDetalhe.id && (
                  <span className="badge fin-imutavel">Documento imutável</span>
                )}
              </div>
              <p style={{ color: '#6b7280', fontSize: '0.9rem' }}>
                Previsto: {formatBrl(relatorioDetalhe.totalGeralPrevisto)} · Realizado:{' '}
                {formatBrl(relatorioDetalhe.totalGeralRealizado)} · Score:{' '}
                {relatorioDetalhe.saudeFinanceira.score.toFixed(1)} (
                {relatorioDetalhe.saudeFinanceira.classificacao})
              </p>
              {relatorioDetalhe.comparativo && (
                <div className="alert-box blue" style={{ marginTop: '1rem' }}>
                  Comparativo: {relatorioDetalhe.comparativo.tendencia} (Δ score{' '}
                  {relatorioDetalhe.comparativo.variacaoScore.toFixed(1)})
                </div>
              )}
              {relatorioDetalhe.recomendacoes.length > 0 && (
                <div style={{ marginTop: '1rem' }}>
                  <h4 style={{ margin: '0 0 0.5rem 0' }}>Recomendações</h4>
                  {relatorioDetalhe.recomendacoes.map((r, i) => (
                    <div key={i} className="fin-recomendacao-card">
                      <strong>{r.tipo}</strong>
                      <p style={{ margin: '0.25rem 0 0 0', fontSize: '0.9rem' }}>{r.mensagem}</p>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}

          <div className="content-card">
            <h3 className="widget-title">Histórico</h3>
            <div className="table-container">
              <table className="data-table">
                <thead>
                  <tr>
                    <th>Data</th>
                    <th>Tipo</th>
                    <th>Score</th>
                    <th>Previsto</th>
                    <th>Realizado</th>
                    <th></th>
                  </tr>
                </thead>
                <tbody>
                  {relatorios.map((r) => (
                    <tr key={r.id!}>
                      <td>{r.dataGeracao ? new Date(r.dataGeracao).toLocaleString('pt-BR') : '—'}</td>
                      <td>
                        <span className={`badge fin-tipo-${(r.tipo ?? 'preliminar').toLowerCase()}`}>
                          {r.tipo}
                        </span>
                      </td>
                      <td>{r.saudeFinanceira.score.toFixed(0)}</td>
                      <td>{formatBrl(r.totalGeralPrevisto)}</td>
                      <td>{formatBrl(r.totalGeralRealizado)}</td>
                      <td>
                        <button
                          className="edit-link"
                          onClick={async () => {
                            const full = await financeiroApi.buscarRelatorio(eventoId, r.id!);
                            setRelatorioDetalhe(full);
                          }}
                        >
                          Visualizar
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </>
      )}

      {mostrarForm && (
        <div className="modal-overlay">
          <div className="modal-container" style={{ maxWidth: 520 }}>
            <h3 className="modal-title">{editando ? 'Editar despesa' : 'Nova despesa'}</h3>
            <form onSubmit={salvarDespesa}>
              {!editando && (
                <>
                  <div className="form-group">
                    <label>Categoria *</label>
                    <select
                      className="form-select"
                      value={formCategoria}
                      onChange={(e) => setFormCategoria(e.target.value)}
                    >
                      {CATEGORIAS.map((c) => (
                        <option key={c} value={c}>
                          {c}
                        </option>
                      ))}
                    </select>
                  </div>
                  <div className="form-group">
                    <label>Fornecedor *</label>
                    <select
                      className="form-select"
                      value={formFornecedorId}
                      onChange={(e) => setFormFornecedorId(e.target.value)}
                      required
                    >
                      {fornecedores.map((f) => (
                        <option key={f.id} value={f.id}>
                          {f.nome}
                        </option>
                      ))}
                    </select>
                  </div>
                </>
              )}
              <div className="form-group">
                <label>Valor (R$) *</label>
                <input
                  type="number"
                  step="0.01"
                  min="0.01"
                  className="form-input"
                  value={formValor}
                  onChange={(e) => setFormValor(e.target.value)}
                  required
                />
              </div>
              <div className="form-group">
                <label>Data *</label>
                <input
                  type="datetime-local"
                  className="form-input"
                  value={formData}
                  onChange={(e) => setFormData(e.target.value)}
                  required
                />
              </div>
              <div className="alert-box blue">
                <div className="alert-content">
                  <ul style={{ margin: 0, paddingLeft: '1.1rem', fontSize: '0.85rem' }}>
                    <li>Fornecedor obrigatório e vinculado ao cadastro</li>
                    <li>Edição apenas enquanto status Registrada</li>
                    <li>≥ 80% do orçamento envia para aprovação automaticamente</li>
                  </ul>
                </div>
              </div>
              <div className="modal-actions">
                <button type="button" className="modal-btn-cancelar" onClick={() => setMostrarForm(false)}>
                  Cancelar
                </button>
                <button type="submit" className="modal-btn-confirm">
                  Salvar
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {mostrarModalOficial && (
        <div className="modal-overlay">
          <div className="modal-container">
            <h3 className="modal-title">Emitir relatório oficial</h3>
            {relatorios.some((r) => r.tipo === 'OFICIAL') && (
              <div className="form-group">
                <label>Motivo da nova versão oficial *</label>
                <textarea
                  className="form-input"
                  rows={3}
                  value={motivoOficial}
                  onChange={(e) => setMotivoOficial(e.target.value)}
                />
              </div>
            )}
            <div className="modal-actions">
              <button type="button" className="modal-btn-cancelar" onClick={() => setMostrarModalOficial(false)}>
                Cancelar
              </button>
              <button
                type="button"
                className="modal-btn-confirm"
                onClick={async () => {
                  try {
                    const r = await financeiroApi.gerarOficial(
                      eventoId,
                      motivoOficial || undefined
                    );
                    setRelatorioDetalhe(r);
                    setMostrarModalOficial(false);
                    await carregarRelatorios();
                  } catch (err) {
                    tratarErro(err);
                  }
                }}
              >
                Emitir oficial
              </button>
            </div>
          </div>
        </div>
      )}

      {carregando && (
        <p style={{ color: '#6b7280', fontSize: '0.85rem' }}>Carregando...</p>
      )}
    </div>
  );
}
