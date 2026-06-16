import { useCallback, useEffect, useState } from 'react';
import { financeiroApi } from '../api/financeiroApi';
import type {
  AcaoPosRelatorio,
  CategoriaOrcamento,
  ComparativoRelatorioPar,
  Despesa,
  Desvio,
  FornecedorResumo,
  Relatorio,
  Simulacao,
} from '../api/financeiroApi';
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

function statusLabel(s: string) {
  const map: Record<string, string> = {
    REGISTRADA: 'Registrada',
    PENDENTE_APROVACAO: 'Pendente',
    APROVADA: 'Aprovada',
    REJEITADA: 'Rejeitada',
  };
  return map[s] ?? s;
}

function statusClass(s: string) {
  const map: Record<string, string> = {
    REGISTRADA: 'fin-status-registrada',
    PENDENTE_APROVACAO: 'fin-status-pendente',
    APROVADA: 'fin-status-aprovada',
    REJEITADA: 'fin-status-rejeitada',
  };
  return map[s] ?? '';
}

function classificacaoClass(c: string) {
  if (c === 'CRITICO') return 'fin-class-critico';
  if (c === 'ATENCAO' || c === 'ATENÇÃO') return 'fin-class-atencao';
  return 'fin-class-normal';
}

function fillClass(c: string) {
  if (c === 'CRITICO') return 'fill-critico';
  if (c === 'ATENCAO' || c === 'ATENÇÃO') return 'fill-atencao';
  return 'fill-normal';
}

function scoreClass(c: string) {
  if (c === 'CRITICO') return 'score-critico';
  if (c === 'ATENCAO') return 'score-atencao';
  return 'score-saudavel';
}

interface HipoteticaRow {
  categoria: string;
  valor: string;
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

  // --- Despesas ---
  const [despesas, setDespesas] = useState<Despesa[]>([]);
  const [desvios, setDesvios] = useState<Desvio[]>([]);
  const [categoriasOrcamento, setCategoriasOrcamento] = useState<CategoriaOrcamento[]>([]);
  const [fornecedores, setFornecedores] = useState<FornecedorResumo[]>([]);
  const [filtroCategoria, setFiltroCategoria] = useState('');
  const [filtroFornecedor, setFiltroFornecedor] = useState('');

  const [mostrarForm, setMostrarForm] = useState(false);
  const [editando, setEditando] = useState<Despesa | null>(null);
  const [formCategoria, setFormCategoria] = useState('ALIMENTACAO');
  const [formFornecedorId, setFormFornecedorId] = useState('');
  const [formValor, setFormValor] = useState('');
  const [formData, setFormData] = useState('');

  // --- Relatórios ---
  const [relatorios, setRelatorios] = useState<Relatorio[]>([]);
  const [simulacao, setSimulacao] = useState<Simulacao | null>(null);
  const [relatorioDetalhe, setRelatorioDetalhe] = useState<Relatorio | null>(null);
  const [motivoOficial, setMotivoOficial] = useState('');
  const [mostrarModalOficial, setMostrarModalOficial] = useState(false);

  // What-if
  const [mostrarWhatIf, setMostrarWhatIf] = useState(false);
  const [wiIncluirPendentes, setWiIncluirPendentes] = useState(true);
  const [wiPessimista, setWiPessimista] = useState(false);
  const [wiHipoteticas, setWiHipoteticas] = useState<HipoteticaRow[]>([]);

  // Comparativo par
  const [mostrarComparativo, setMostrarComparativo] = useState(false);
  const [comparativoBaseId, setComparativoBaseId] = useState('');
  const [comparativoComparadoId, setComparativoComparadoId] = useState('');
  const [comparativoPar, setComparativoPar] = useState<ComparativoRelatorioPar | null>(null);

  // Ações pós-relatório
  const [acoesRelatorio, setAcoesRelatorio] = useState<AcaoPosRelatorio[]>([]);
  const [mostrarFormAcao, setMostrarFormAcao] = useState(false);
  const [formAcaoTipo, setFormAcaoTipo] = useState('SAUDE_CRITICA');
  const [formAcaoDescricao, setFormAcaoDescricao] = useState('');

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
      const [f, cats] = await Promise.all([
        financeiroApi.listarFornecedores(),
        financeiroApi.listarCategoriasOrcamento(eventoId).catch(() => [] as CategoriaOrcamento[]),
      ]);
      setFornecedores(f);
      setCategoriasOrcamento(cats);
      if (!formFornecedorId && f.length > 0) setFormFornecedorId(f[0].id);
      await carregarDespesas();
      await carregarRelatorios();
    } catch (e) {
      tratarErro(e);
    } finally {
      setCarregando(false);
    }
  }, [carregarDespesas, carregarRelatorios, eventoId, formFornecedorId]);

  useEffect(() => { recarregar(); }, [recarregar]);
  useEffect(() => { setAba(abaInicial); }, [abaInicial]);

  const trocarAba = (nova: 'despesas' | 'relatorios') => {
    setAba(nova);
    onAbaChange?.(nova);
  };

  // --- Cálculo de desvio em tempo real no formulário ---
  const desvioFormulario = (() => {
    const val = parseFloat(formValor);
    if (!formCategoria || isNaN(val) || val <= 0) return null;
    const cat = categoriasOrcamento.find((c) => c.categoria === formCategoria);
    if (!cat) return null;
    const desvioAtual = desvios.find((d) => d.categoria === formCategoria);
    const realizadoAtual = desvioAtual?.valorRealizado ?? 0;
    const valorAnterior = editando?.valor ?? 0;
    const novoTotal = realizadoAtual - valorAnterior + val;
    const pct = cat.valorPrevisto > 0 ? (novoTotal / cat.valorPrevisto) * 100 : 0;
    return { novoTotal, previsto: cat.valorPrevisto, pct };
  })();

  const hintDesvio = (() => {
    if (!desvioFormulario) return null;
    const { pct } = desvioFormulario;
    if (pct > 100) return { cls: 'bloqueado', msg: `Bloqueado: ultrapassaria 100% do orçamento (${pct.toFixed(1)}%)` };
    if (pct >= 80) return { cls: 'atencao', msg: `⚠ Atingirá ${pct.toFixed(1)}% do orçamento — será enviada para aprovação automaticamente` };
    if (pct >= 50) return { cls: 'atencao', msg: `${pct.toFixed(1)}% do orçamento de ${formCategoria} (previsto: ${formatBrl(desvioFormulario.previsto)})` };
    return { cls: 'normal', msg: `${pct.toFixed(1)}% do orçamento de ${formCategoria} (previsto: ${formatBrl(desvioFormulario.previsto)})` };
  })();

  // --- Form despesa ---
  const abrirNova = () => {
    setEditando(null);
    setFormCategoria('ALIMENTACAO');
    setFormValor('');
    setFormData(new Date().toISOString().slice(0, 16));
    if (fornecedores.length > 0) setFormFornecedorId(fornecedores[0].id);
    setMostrarForm(true);
    setErro(null);
  };

  const abrirEditar = (d: Despesa) => {
    if (d.status !== 'REGISTRADA') return;
    setEditando(d);
    setFormCategoria(d.categoria);
    setFormFornecedorId(d.fornecedorId);
    setFormValor(String(d.valor));
    setFormData(d.data ? d.data.slice(0, 16) : '');
    setMostrarForm(true);
    setErro(null);
  };

  const salvarDespesa = async (e: React.FormEvent) => {
    e.preventDefault();
    setErro(null);
    if (hintDesvio?.cls === 'bloqueado') {
      setErro('Despesa bloqueada: ultrapassaria 100% do orçamento da categoria.');
      return;
    }
    try {
      const valor = parseFloat(formValor);
      const data = new Date(formData).toISOString();
      let salva: Despesa;
      if (editando) {
        salva = await financeiroApi.atualizarDespesa(eventoId, editando.id, { valor, data });
      } else {
        salva = await financeiroApi.registrarDespesa(eventoId, {
          categoria: formCategoria,
          fornecedorId: formFornecedorId,
          valor,
          data,
        });
      }
      setMostrarForm(false);
      await carregarDespesas();
      if (salva.status === 'PENDENTE_APROVACAO') {
        setErro(null);
        // aviso não-bloqueante
        setTimeout(() =>
          setErro('Despesa enviada automaticamente para aprovação (≥ 80% do orçamento da categoria).')
        , 100);
      }
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

  // --- Relatórios ---
  const simular = async () => {
    try {
      const s = await financeiroApi.simularRelatorio(eventoId);
      setSimulacao(s);
      setRelatorioDetalhe(s.preview);
      setMostrarWhatIf(false);
    } catch (err) {
      tratarErro(err);
    }
  };

  const simularWhatIf = async () => {
    try {
      const hipoteticas = wiHipoteticas
        .filter((h) => h.categoria && parseFloat(h.valor) > 0)
        .map((h) => ({ categoria: h.categoria, valor: parseFloat(h.valor) }));
      const s = await financeiroApi.simularWhatIf(eventoId, {
        incluirPendentes: wiIncluirPendentes,
        cenarioPessimistaCobertura: wiPessimista,
        despesasHipoteticas: hipoteticas,
      });
      setSimulacao(s);
      setRelatorioDetalhe(s.preview);
      setMostrarWhatIf(false);
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
      const r = await financeiroApi.confirmarSimulacao(eventoId, simulacao.id, {
        tipo,
        motivoNovaVersaoOficial: tipo === 'OFICIAL' ? motivoOficial : undefined,
      });
      setSimulacao(null);
      setRelatorioDetalhe(r);
      setMotivoOficial('');
      await carregarRelatorios();
    } catch (err) {
      tratarErro(err);
    }
  };

  const carregarAcoes = async (relatorioId: string) => {
    try {
      const acoes = await financeiroApi.listarAcoesPosRelatorio(relatorioId);
      setAcoesRelatorio(acoes);
    } catch {
      setAcoesRelatorio([]);
    }
  };

  const verRelatorio = async (r: Relatorio) => {
    try {
      const full = await financeiroApi.buscarRelatorio(eventoId, r.id!);
      setRelatorioDetalhe(full);
      setSimulacao(null);
      await carregarAcoes(full.id!);
    } catch (err) {
      tratarErro(err);
    }
  };

  const registrarAcao = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!relatorioDetalhe?.id) return;
    try {
      await financeiroApi.registrarAcaoPosRelatorio(relatorioDetalhe.id, {
        tipoRecomendacao: formAcaoTipo,
        descricao: formAcaoDescricao,
      });
      setMostrarFormAcao(false);
      setFormAcaoDescricao('');
      await carregarAcoes(relatorioDetalhe.id);
    } catch (err) {
      tratarErro(err);
    }
  };

  const tratarAcao = async (acaoId: string) => {
    if (!relatorioDetalhe?.id) return;
    try {
      await financeiroApi.marcarAcaoComoTratada(relatorioDetalhe.id, acaoId);
      await carregarAcoes(relatorioDetalhe.id);
    } catch (err) {
      tratarErro(err);
    }
  };

  const buscarComparativo = async () => {
    if (!comparativoBaseId || !comparativoComparadoId) return;
    try {
      const c = await financeiroApi.compararRelatorios(eventoId, comparativoBaseId, comparativoComparadoId);
      setComparativoPar(c);
    } catch (err) {
      tratarErro(err);
    }
  };

  // --- Stats ---
  const pendentes = despesas.filter((d) => d.status === 'PENDENTE_APROVACAO').length;
  const totalRealizado = despesas
    .filter((d) => d.status !== 'REJEITADA')
    .reduce((s, d) => s + d.valor, 0);
  const ultimoRel = relatorios.length > 0 ? relatorios[relatorios.length - 1] : null;

  return (
    <div>
      {/* Cabeçalho com abas */}
      <div className="header-section" style={{ marginBottom: '1.5rem' }}>
        <div className="title-area">
          <h1 style={{ fontSize: '2rem', fontWeight: 700, color: '#111827', margin: 0 }}>Financeiro</h1>
          <p style={{ color: '#6b7280', margin: '0.25rem 0 0 0' }}>
            Evento: <strong>{eventoNome}</strong>
          </p>
        </div>
        <div className="tab-nav">
          <button className={`tab-button ${aba === 'despesas' ? 'active' : ''}`} onClick={() => trocarAba('despesas')}>
            Despesas
          </button>
          <button className={`tab-button ${aba === 'relatorios' ? 'active' : ''}`} onClick={() => trocarAba('relatorios')}>
            Relatórios
          </button>
        </div>
      </div>

      {erro && (
        <div className="error-message" style={{ marginBottom: '1rem', padding: '0.75rem' }}>
          {erro}
          <button style={{ float: 'right', background: 'none', border: 'none', cursor: 'pointer' }} onClick={() => setErro(null)}>✕</button>
        </div>
      )}

      {/* ===== ABA DESPESAS ===== */}
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
                <div className="stat-value" style={{ fontSize: '1.4rem' }}>{formatBrl(totalRealizado)}</div>
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
                <div className="stat-value">{desvios.filter((d) => d.classificacao === 'CRITICO').length}</div>
              </div>
            </div>
          </div>

          {/* Desvio por categoria */}
          {desvios.length > 0 && (
            <div className="content-card" style={{ marginBottom: '1.5rem' }}>
              <h3 className="widget-title">Desvio orçamentário por categoria</h3>
              <div className="fin-desvio-grid">
                {desvios.map((d) => (
                  <div key={d.categoria} className="fin-desvio-card">
                    <div className="fin-desvio-card-title">{d.categoria}</div>
                    <div className="fin-desvio-meta">
                      Prev: {formatBrl(d.valorPrevisto)} · Real: {formatBrl(d.valorRealizado)}
                    </div>
                    <div className="fin-categoria-progress">
                      <div
                        className={`fin-categoria-progress-fill ${fillClass(d.classificacao)}`}
                        style={{ width: `${Math.min(100, Math.max(0, (d.valorRealizado / d.valorPrevisto) * 100))}%` }}
                      />
                    </div>
                    <div className={`badge ${classificacaoClass(d.classificacao)}`} style={{ marginTop: '0.35rem' }}>
                      {d.desvioPercentual.toFixed(1)}% · {d.classificacao}
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Tabela de despesas */}
          <div className="content-card">
            <div className="card-header">
              <h2 className="card-title">Despesas do evento</h2>
              <button className="action-btn" onClick={abrirNova}>+ Nova Despesa</button>
            </div>

            <div className="form-grid" style={{ gridTemplateColumns: '1fr 1fr auto', marginBottom: '1rem' }}>
              <div className="form-group">
                <label>Categoria</label>
                <select className="form-select" value={filtroCategoria} onChange={(e) => setFiltroCategoria(e.target.value)}>
                  <option value="">Todas</option>
                  {CATEGORIAS.map((c) => <option key={c} value={c}>{c}</option>)}
                </select>
              </div>
              <div className="form-group">
                <label>Fornecedor</label>
                <select className="form-select" value={filtroFornecedor} onChange={(e) => setFiltroFornecedor(e.target.value)}>
                  <option value="">Todos</option>
                  {fornecedores.map((f) => <option key={f.id} value={f.id}>{f.nome}</option>)}
                </select>
              </div>
              <div className="form-group" style={{ alignSelf: 'end' }}>
                <button className="btn-outline" onClick={() => carregarDespesas()}>Pesquisar</button>
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
                        <td><span className="competency-tag">{d.categoria}</span></td>
                        <td>{fornecedores.find((f) => f.id === d.fornecedorId)?.nome ?? d.fornecedorId}</td>
                        <td>{formatBrl(d.valor)}</td>
                        <td><span className={`badge ${statusClass(d.status)}`}>{statusLabel(d.status)}</span></td>
                        <td>
                          {d.status === 'REGISTRADA' && (
                            <>
                              <button className="edit-link" onClick={() => abrirEditar(d)}>Editar</button>
                              <button className="edit-link" style={{ marginLeft: 8, color: '#ef4444' }} onClick={() => excluir(d)}>Excluir</button>
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

      {/* ===== ABA RELATÓRIOS ===== */}
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
                  {ultimoRel?.dataGeracao ? new Date(ultimoRel.dataGeracao).toLocaleString('pt-BR') : '—'}
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
                  ) : '—'}
                </div>
              </div>
            </div>
          </div>

          {/* Ações de emissão */}
          <div className="content-card" style={{ marginBottom: '1rem' }}>
            <div className="card-header">
              <h2 className="card-title">Emissão de relatórios</h2>
              <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap' }}>
                <button className="btn-outline" onClick={simular}>Simular</button>
                <button className="btn-outline" onClick={() => setMostrarWhatIf((v) => !v)}>
                  {mostrarWhatIf ? 'Ocultar what-if' : 'Simular what-if'}
                </button>
                <button className="btn-outline" onClick={async () => {
                  try {
                    const r = await financeiroApi.gerarPreliminar(eventoId);
                    setRelatorioDetalhe(r);
                    setSimulacao(null);
                    await carregarRelatorios();
                    if (r.id) await carregarAcoes(r.id);
                  } catch (err) { tratarErro(err); }
                }}>Gerar preliminar</button>
                <button className="action-btn" onClick={() => setMostrarModalOficial(true)}>Gerar oficial</button>
              </div>
            </div>

            {/* Painel what-if */}
            {mostrarWhatIf && (
              <div className="fin-whatiif-panel">
                <h4>Simulação what-if — cenários hipotéticos</h4>
                <div style={{ display: 'flex', gap: '1.5rem', marginBottom: '0.75rem', flexWrap: 'wrap' }}>
                  <label style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', fontSize: '0.9rem' }}>
                    <input type="checkbox" checked={wiIncluirPendentes} onChange={(e) => setWiIncluirPendentes(e.target.checked)} />
                    Incluir despesas pendentes de aprovação
                  </label>
                  <label style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', fontSize: '0.9rem' }}>
                    <input type="checkbox" checked={wiPessimista} onChange={(e) => setWiPessimista(e.target.checked)} />
                    Cenário pessimista de cobertura contratual
                  </label>
                </div>

                <div style={{ marginBottom: '0.5rem', fontSize: '0.85rem', fontWeight: 600, color: '#4f46e5' }}>
                  Despesas hipotéticas (projeção)
                </div>
                {wiHipoteticas.map((h, i) => (
                  <div key={i} className="fin-hipotetica-row">
                    <select className="form-select" value={h.categoria} onChange={(e) => {
                      const novo = [...wiHipoteticas];
                      novo[i].categoria = e.target.value;
                      setWiHipoteticas(novo);
                    }}>
                      {CATEGORIAS.map((c) => <option key={c} value={c}>{c}</option>)}
                    </select>
                    <input type="number" min="0.01" step="0.01" className="form-input" placeholder="Valor R$"
                      value={h.valor} onChange={(e) => {
                        const novo = [...wiHipoteticas];
                        novo[i].valor = e.target.value;
                        setWiHipoteticas(novo);
                      }} />
                    <button type="button" className="btn-outline" style={{ color: '#ef4444' }}
                      onClick={() => setWiHipoteticas(wiHipoteticas.filter((_, j) => j !== i))}>✕</button>
                  </div>
                ))}
                <div style={{ display: 'flex', gap: '0.5rem', marginTop: '0.5rem' }}>
                  <button type="button" className="btn-outline" onClick={() =>
                    setWiHipoteticas([...wiHipoteticas, { categoria: 'ALIMENTACAO', valor: '' }])
                  }>+ Adicionar despesa hipotética</button>
                  <button type="button" className="action-btn" onClick={simularWhatIf}>Simular cenário</button>
                </div>
              </div>
            )}

            {/* Banner simulação ativa */}
            {simulacao && (
              <div className="alert-box yellow" style={{ marginBottom: '1rem' }}>
                <div className="alert-content">
                  <strong>Modo simulação</strong> — nada foi salvo. Confirme para persistir.
                  <div style={{ marginTop: '0.75rem', display: 'flex', gap: '0.5rem', flexWrap: 'wrap' }}>
                    <button className="btn-outline" onClick={() => { setSimulacao(null); setRelatorioDetalhe(null); }}>Descartar</button>
                    <button className="btn-outline" onClick={() => confirmarSimulacao('PRELIMINAR')}>Confirmar preliminar</button>
                    <button className="action-btn" onClick={() => confirmarSimulacao('OFICIAL')}>Confirmar oficial</button>
                  </div>
                </div>
              </div>
            )}
          </div>

          {/* Comparativo par */}
          {relatorios.length >= 2 && (
            <div className="content-card" style={{ marginBottom: '1rem' }}>
              <div className="card-header">
                <h3 className="widget-title" style={{ margin: 0 }}>Comparar dois relatórios</h3>
                <button className="btn-outline" onClick={() => setMostrarComparativo((v) => !v)}>
                  {mostrarComparativo ? 'Ocultar' : 'Abrir comparativo'}
                </button>
              </div>
              {mostrarComparativo && (
                <div style={{ display: 'flex', gap: '0.75rem', marginTop: '1rem', flexWrap: 'wrap', alignItems: 'flex-end' }}>
                  <div className="form-group" style={{ margin: 0, flex: 1 }}>
                    <label>Relatório base</label>
                    <select className="form-select" value={comparativoBaseId} onChange={(e) => setComparativoBaseId(e.target.value)}>
                      <option value="">Selecione...</option>
                      {relatorios.filter((r) => r.id).map((r) => (
                        <option key={r.id!} value={r.id!}>
                          {r.tipo} — {r.dataGeracao ? new Date(r.dataGeracao).toLocaleString('pt-BR') : r.id}
                        </option>
                      ))}
                    </select>
                  </div>
                  <div className="form-group" style={{ margin: 0, flex: 1 }}>
                    <label>Relatório comparado</label>
                    <select className="form-select" value={comparativoComparadoId} onChange={(e) => setComparativoComparadoId(e.target.value)}>
                      <option value="">Selecione...</option>
                      {relatorios.filter((r) => r.id && r.id !== comparativoBaseId).map((r) => (
                        <option key={r.id!} value={r.id!}>
                          {r.tipo} — {r.dataGeracao ? new Date(r.dataGeracao).toLocaleString('pt-BR') : r.id}
                        </option>
                      ))}
                    </select>
                  </div>
                  <button className="action-btn" onClick={buscarComparativo}>Comparar</button>
                </div>
              )}
              {comparativoPar && mostrarComparativo && (
                <div style={{ marginTop: '1rem' }}>
                  <div className="fin-comparativo-par">
                    <div className="fin-comparativo-par-col">
                      <div style={{ fontWeight: 600, marginBottom: '0.25rem' }}>Variação do score</div>
                      <span className={`badge ${scoreClass(comparativoPar.variacaoScore >= 0 ? 'SAUDAVEL' : 'CRITICO')}`}>
                        {comparativoPar.variacaoScore >= 0 ? '+' : ''}{comparativoPar.variacaoScore.toFixed(1)} pts
                      </span>
                    </div>
                    <div className="fin-comparativo-par-col">
                      <div style={{ fontWeight: 600, marginBottom: '0.25rem' }}>Tendência</div>
                      <span className={`badge ${comparativoPar.tendencia === 'MELHOROU' ? 'fin-class-normal' : comparativoPar.tendencia === 'PIOROU' ? 'fin-class-critico' : 'fin-class-atencao'}`}>
                        {comparativoPar.tendencia}
                      </span>
                    </div>
                    <div className="fin-comparativo-par-col">
                      <div style={{ fontWeight: 600, marginBottom: '0.25rem' }}>Variação realizado</div>
                      <span>{formatBrl(Number(comparativoPar.variacaoTotalRealizado))}</span>
                    </div>
                    <div className="fin-comparativo-par-col">
                      <div style={{ fontWeight: 600, marginBottom: '0.25rem' }}>Categorias com piora</div>
                      {comparativoPar.categoriasComPiora.length === 0 ? (
                        <span style={{ color: '#6b7280', fontSize: '0.85rem' }}>Nenhuma</span>
                      ) : (
                        comparativoPar.categoriasComPiora.map((c) => (
                          <span key={c} className="competency-tag" style={{ background: '#fee2e2', color: '#991b1b' }}>{c}</span>
                        ))
                      )}
                    </div>
                  </div>
                </div>
              )}
            </div>
          )}

          {/* Detalhe do relatório */}
          {relatorioDetalhe && (
            <div className="content-card fin-relatorio-detalhe">
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.75rem' }}>
                <h3 className="widget-title" style={{ margin: 0 }}>
                  {relatorioDetalhe.id ? 'Relatório persistido' : 'Pré-visualização'}
                  {relatorioDetalhe.tipo && (
                    <span className={`badge fin-tipo-${relatorioDetalhe.tipo.toLowerCase()}`} style={{ marginLeft: 8 }}>
                      {relatorioDetalhe.tipo}
                    </span>
                  )}
                </h3>
                <div style={{ display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
                  {relatorioDetalhe.id && <span className="badge fin-imutavel">Documento imutável</span>}
                  <button className="btn-outline" style={{ fontSize: '0.8rem' }} onClick={() => setRelatorioDetalhe(null)}>✕ Fechar</button>
                </div>
              </div>

              {/* Score de saúde */}
              <div style={{ marginBottom: '1rem' }}>
                <div style={{ fontSize: '0.85rem', color: '#6b7280', marginBottom: '0.35rem' }}>Score de saúde financeira</div>
                <div className="fin-score-gauge">
                  <span className={`fin-score-number ${scoreClass(relatorioDetalhe.saudeFinanceira.classificacao)}`}>
                    {relatorioDetalhe.saudeFinanceira.score.toFixed(0)}
                  </span>
                  <div className="fin-score-bar">
                    <div
                      className={`fin-score-bar-fill ${scoreClass(relatorioDetalhe.saudeFinanceira.classificacao)}`}
                      style={{ width: `${Math.min(100, relatorioDetalhe.saudeFinanceira.score)}%` }}
                    />
                  </div>
                  <span className={`badge ${classificacaoClass(relatorioDetalhe.saudeFinanceira.classificacao)}`}>
                    {relatorioDetalhe.saudeFinanceira.classificacao}
                  </span>
                </div>
                <div style={{ fontSize: '0.85rem', color: '#6b7280' }}>
                  Previsto: {formatBrl(relatorioDetalhe.totalGeralPrevisto)} · Realizado: {formatBrl(relatorioDetalhe.totalGeralRealizado)}
                </div>
              </div>

              {/* Cobertura contratual */}
              {relatorioDetalhe.coberturaContratual && (
                <div style={{ marginBottom: '1rem' }}>
                  <div style={{ fontSize: '0.85rem', fontWeight: 600, marginBottom: '0.35rem' }}>Cobertura contratual</div>
                  <div className="fin-cobertura-bar">
                    <div
                      className="fin-cobertura-coberta"
                      style={{ width: `${relatorioDetalhe.coberturaContratual.percentualCobertura}%` }}
                      title={`Cobertas: ${relatorioDetalhe.coberturaContratual.despesasCobertas}`}
                    />
                    <div
                      className="fin-cobertura-descoberta"
                      style={{ width: `${100 - relatorioDetalhe.coberturaContratual.percentualCobertura}%` }}
                      title={`Descobertas: ${relatorioDetalhe.coberturaContratual.despesasDescobertas}`}
                    />
                  </div>
                  <div style={{ fontSize: '0.8rem', color: '#6b7280', display: 'flex', gap: '1rem' }}>
                    <span>✅ {relatorioDetalhe.coberturaContratual.despesasCobertas} cobertas</span>
                    <span>🔴 {relatorioDetalhe.coberturaContratual.despesasDescobertas} descobertas</span>
                    <span>{relatorioDetalhe.coberturaContratual.percentualCobertura.toFixed(1)}% cobertura</span>
                  </div>
                </div>
              )}

              {/* Comparativo automático */}
              {relatorioDetalhe.comparativo && (
                <div className="alert-box blue" style={{ marginBottom: '1rem' }}>
                  <div className="alert-content">
                    <strong>Comparativo com emissão anterior:</strong>{' '}
                    {relatorioDetalhe.comparativo.tendencia}{' '}
                    (Δ score {relatorioDetalhe.comparativo.variacaoScore.toFixed(1)} pts)
                    {relatorioDetalhe.comparativo.categoriasComPiora.length > 0 && (
                      <div style={{ marginTop: '0.35rem', fontSize: '0.85rem' }}>
                        Categorias com piora:{' '}
                        {relatorioDetalhe.comparativo.categoriasComPiora.map((c) => (
                          <span key={c} className="competency-tag" style={{ background: '#fee2e2', color: '#991b1b' }}>{c}</span>
                        ))}
                      </div>
                    )}
                  </div>
                </div>
              )}

              {/* Itens por categoria */}
              {relatorioDetalhe.itensPorCategoria.length > 0 && (
                <div style={{ marginBottom: '1rem' }}>
                  <h4 style={{ margin: '0 0 0.5rem 0', fontSize: '0.9rem' }}>Por categoria</h4>
                  <div className="table-container">
                    <table className="data-table fin-categoria-table">
                      <thead>
                        <tr>
                          <th>Categoria</th>
                          <th>Previsto</th>
                          <th>Realizado</th>
                          <th>Variação</th>
                          <th>Classificação</th>
                        </tr>
                      </thead>
                      <tbody>
                        {relatorioDetalhe.itensPorCategoria.map((item) => (
                          <tr key={item.categoria}>
                            <td>{item.categoria}</td>
                            <td>{formatBrl(item.valorPrevisto)}</td>
                            <td>{formatBrl(item.valorRealizado)}</td>
                            <td>
                              {item.percentualVariacao.toFixed(1)}%
                              <div className="fin-categoria-progress">
                                <div
                                  className={`fin-categoria-progress-fill ${fillClass(item.classificacao)}`}
                                  style={{ width: `${Math.min(100, Math.max(0, (item.valorRealizado / item.valorPrevisto) * 100))}%` }}
                                />
                              </div>
                            </td>
                            <td>
                              <span className={`badge ${classificacaoClass(item.classificacao)}`}>{item.classificacao}</span>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                </div>
              )}

              {/* Recomendações */}
              {relatorioDetalhe.recomendacoes.length > 0 && (
                <div style={{ marginBottom: '1rem' }}>
                  <h4 style={{ margin: '0 0 0.5rem 0', fontSize: '0.9rem' }}>Recomendações</h4>
                  {relatorioDetalhe.recomendacoes.map((r, i) => (
                    <div key={i} className="fin-recomendacao-card">
                      <strong>{r.tipo}</strong>
                      {r.categoriaRelacionada && (
                        <span className="competency-tag" style={{ marginLeft: 8 }}>{r.categoriaRelacionada}</span>
                      )}
                      <p style={{ margin: '0.25rem 0 0 0', fontSize: '0.9rem' }}>{r.mensagem}</p>
                    </div>
                  ))}
                </div>
              )}

              {/* Ações pós-relatório (apenas em relatórios persistidos) */}
              {relatorioDetalhe.id && (
                <div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.5rem' }}>
                    <h4 style={{ margin: 0, fontSize: '0.9rem' }}>Ações pós-relatório</h4>
                    <button className="btn-outline" style={{ fontSize: '0.8rem' }} onClick={() => setMostrarFormAcao(true)}>
                      + Registrar ação
                    </button>
                  </div>
                  {acoesRelatorio.length === 0 ? (
                    <p style={{ color: '#6b7280', fontSize: '0.85rem' }}>Nenhuma ação registrada ainda.</p>
                  ) : (
                    acoesRelatorio.map((a) => (
                      <div key={a.id} className={`fin-acao-card ${a.status === 'TRATADA' ? 'tratada' : ''}`}>
                        <div style={{ flex: 1 }}>
                          <div style={{ display: 'flex', gap: '0.5rem', alignItems: 'center', marginBottom: '0.25rem' }}>
                            <strong style={{ fontSize: '0.85rem' }}>{a.tipoRecomendacao}</strong>
                            <span className={a.status === 'TRATADA' ? 'fin-acao-status-tratada' : 'fin-acao-status-pendente'}>
                              {a.status}
                            </span>
                          </div>
                          <p style={{ margin: 0, fontSize: '0.85rem', color: '#374151' }}>{a.descricao}</p>
                          <p style={{ margin: '0.25rem 0 0 0', fontSize: '0.75rem', color: '#9ca3af' }}>
                            {new Date(a.criadaEm).toLocaleString('pt-BR')}
                            {a.tratadaEm && ` · Tratada em ${new Date(a.tratadaEm).toLocaleString('pt-BR')}`}
                          </p>
                        </div>
                        {a.status === 'PENDENTE' && (
                          <button className="edit-link" style={{ color: '#10b981', whiteSpace: 'nowrap' }}
                            onClick={() => tratarAcao(a.id)}>
                            Marcar tratada
                          </button>
                        )}
                      </div>
                    ))
                  )}
                </div>
              )}
            </div>
          )}

          {/* Histórico */}
          <div className="content-card">
            <h3 className="widget-title">Histórico de relatórios</h3>
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
                  {relatorios.length === 0 ? (
                    <tr>
                      <td colSpan={6} style={{ textAlign: 'center', padding: '2rem', color: '#6b7280' }}>
                        Nenhum relatório emitido.
                      </td>
                    </tr>
                  ) : (
                    relatorios.map((r) => (
                      <tr key={r.id!}>
                        <td>{r.dataGeracao ? new Date(r.dataGeracao).toLocaleString('pt-BR') : '—'}</td>
                        <td>
                          <span className={`badge fin-tipo-${(r.tipo ?? 'preliminar').toLowerCase()}`}>{r.tipo}</span>
                        </td>
                        <td>
                          <span className={`badge ${classificacaoClass(r.saudeFinanceira.classificacao)}`}>
                            {r.saudeFinanceira.score.toFixed(0)}
                          </span>
                        </td>
                        <td>{formatBrl(r.totalGeralPrevisto)}</td>
                        <td>{formatBrl(r.totalGeralRealizado)}</td>
                        <td>
                          <button className="edit-link" onClick={() => verRelatorio(r)}>Visualizar</button>
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

      {/* ===== MODAL NOVA/EDITAR DESPESA ===== */}
      {mostrarForm && (
        <div className="modal-overlay">
          <div className="modal-container" style={{ maxWidth: 520 }}>
            <h3 className="modal-title">{editando ? 'Editar despesa' : 'Nova despesa'}</h3>
            <form onSubmit={salvarDespesa}>
              {!editando && (
                <>
                  <div className="form-group">
                    <label>Categoria *</label>
                    <select className="form-select" value={formCategoria} onChange={(e) => setFormCategoria(e.target.value)}>
                      {CATEGORIAS.map((c) => <option key={c} value={c}>{c}</option>)}
                    </select>
                  </div>
                  <div className="form-group">
                    <label>Fornecedor *</label>
                    <select className="form-select" value={formFornecedorId} onChange={(e) => setFormFornecedorId(e.target.value)} required>
                      {fornecedores.map((f) => <option key={f.id} value={f.id}>{f.nome}</option>)}
                    </select>
                  </div>
                </>
              )}
              <div className="form-group">
                <label>Valor (R$) *</label>
                <input type="number" step="0.01" min="0.01" className="form-input"
                  value={formValor} onChange={(e) => setFormValor(e.target.value)} required />
                {hintDesvio && (
                  <div className={`fin-orcamento-hint ${hintDesvio.cls}`}>{hintDesvio.msg}</div>
                )}
              </div>
              <div className="form-group">
                <label>Data *</label>
                <input type="datetime-local" className="form-input"
                  value={formData} onChange={(e) => setFormData(e.target.value)} required />
              </div>
              <div className="alert-box blue">
                <div className="alert-content">
                  <ul style={{ margin: 0, paddingLeft: '1.1rem', fontSize: '0.85rem' }}>
                    <li>Fornecedor obrigatório e vinculado ao cadastro</li>
                    <li>Edição apenas enquanto status Registrada</li>
                    <li>≥ 80% do orçamento envia para aprovação automaticamente</li>
                    <li>Bloqueado ao ultrapassar 100% do orçamento</li>
                  </ul>
                </div>
              </div>
              <div className="modal-actions">
                <button type="button" className="modal-btn-cancelar" onClick={() => setMostrarForm(false)}>Cancelar</button>
                <button type="submit" className="modal-btn-confirm" disabled={hintDesvio?.cls === 'bloqueado'}>Salvar</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* ===== MODAL EMITIR OFICIAL ===== */}
      {mostrarModalOficial && (
        <div className="modal-overlay">
          <div className="modal-container">
            <h3 className="modal-title">Emitir relatório oficial</h3>
            {relatorios.some((r) => r.tipo === 'OFICIAL') && (
              <div className="form-group">
                <label>Motivo da nova versão oficial *</label>
                <textarea className="form-input" rows={3} value={motivoOficial}
                  onChange={(e) => setMotivoOficial(e.target.value)} />
              </div>
            )}
            <div className="modal-actions">
              <button type="button" className="modal-btn-cancelar" onClick={() => setMostrarModalOficial(false)}>Cancelar</button>
              <button type="button" className="modal-btn-confirm" onClick={async () => {
                try {
                  const r = await financeiroApi.gerarOficial(eventoId, motivoOficial || undefined);
                  setRelatorioDetalhe(r);
                  setMostrarModalOficial(false);
                  await carregarRelatorios();
                  if (r.id) await carregarAcoes(r.id);
                } catch (err) { tratarErro(err); }
              }}>Emitir oficial</button>
            </div>
          </div>
        </div>
      )}

      {/* ===== MODAL REGISTRAR AÇÃO PÓS-RELATÓRIO ===== */}
      {mostrarFormAcao && (
        <div className="modal-overlay">
          <div className="modal-container" style={{ maxWidth: 480 }}>
            <h3 className="modal-title">Registrar ação pós-relatório</h3>
            <form onSubmit={registrarAcao}>
              <div className="form-group">
                <label>Tipo da recomendação *</label>
                <select className="form-select" value={formAcaoTipo} onChange={(e) => setFormAcaoTipo(e.target.value)}>
                  <option value="SAUDE_CRITICA">Saúde crítica</option>
                  <option value="CATEGORIA_CRITICA">Categoria crítica</option>
                  <option value="EVOLUCAO_PIOROU">Evolução piorou</option>
                  <option value="COBERTURA_CONTRATUAL">Cobertura contratual</option>
                  <option value="REVISAR_ORCAMENTO">Revisar orçamento</option>
                </select>
              </div>
              <div className="form-group">
                <label>Descrição da ação *</label>
                <textarea className="form-input" rows={3} required
                  value={formAcaoDescricao} onChange={(e) => setFormAcaoDescricao(e.target.value)}
                  placeholder="Descreva a ação que será tomada..." />
              </div>
              <div className="modal-actions">
                <button type="button" className="modal-btn-cancelar" onClick={() => setMostrarFormAcao(false)}>Cancelar</button>
                <button type="submit" className="modal-btn-confirm">Registrar</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {carregando && (
        <p style={{ color: '#6b7280', fontSize: '0.85rem', textAlign: 'center', padding: '1rem' }}>Carregando...</p>
      )}
    </div>
  );
}
