import { useCallback, useEffect, useMemo, useState } from 'react';
import { toast } from 'sonner';
import {
  conciliacaoApi,
  type DespesaResumoConciliacao,
  type MetodoConciliacao,
  type RelatorioConciliacao,
  type StatusConciliacao,
  type VinculoConciliacao,
} from '../api/conciliacaoApi';
import { financeiroApi, type Despesa, type FornecedorResumo } from '../api/financeiroApi';
import { ApiError } from '../shared/api/errors';
import { listarContratosPorEventoApi } from '../modules/planning/contratos/api';
import type { ContratoDto } from '../modules/planning/contratos/dto';

type Tab = 'dashboard' | 'despesas' | 'descobertas' | 'extrapolados' | 'relatorio';

interface DespesaConciliacaoView extends DespesaResumoConciliacao {
  coverageStatus: StatusConciliacao;
  contratoId?: string;
  metodo?: MetodoConciliacao;
}

interface ExtrapoladoView {
  contrato: ContratoDto;
  totalConciliado: number;
  excess: number;
}

interface Props {
  eventoId: string;
  eventoNome: string;
}

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

function formatDate(iso: string) {
  return new Date(iso).toLocaleDateString('pt-BR');
}

function isContratoAtivo(status: string) {
  return status !== 'ENCERRADO' && status !== 'CANCELADO';
}

function isVigente(contrato: ContratoDto, dataDespesa: string) {
  const d = new Date(dataDespesa).getTime();
  return d >= new Date(contrato.dataInicio).getTime() && d <= new Date(contrato.dataFim).getTime();
}

function CoverageBadge({ status }: { status: StatusConciliacao }) {
  return status === 'COBERTA' ? (
    <span className="conc-badge-coberta">Coberta</span>
  ) : (
    <span className="conc-badge-descoberta">Descoberta</span>
  );
}

function MetodoBadge({ metodo }: { metodo?: MetodoConciliacao }) {
  if (!metodo) return <span style={{ color: '#9ca3af', fontSize: '0.75rem' }}>—</span>;
  return (
    <span className={metodo === 'MANUAL' ? 'conc-badge-metodo-manual' : 'conc-badge-metodo-auto'}>
      {metodo === 'MANUAL' ? 'Manual' : 'Automático'}
    </span>
  );
}

export default function ConciliacaoPage({ eventoId, eventoNome }: Props) {
  const [aba, setAba] = useState<Tab>('dashboard');
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState<string | null>(null);
  const [executandoAuto, setExecutandoAuto] = useState(false);
  const [gerandoRelatorio, setGerandoRelatorio] = useState(false);

  const [despesas, setDespesas] = useState<Despesa[]>([]);
  const [vinculos, setVinculos] = useState<VinculoConciliacao[]>([]);
  const [descobertas, setDescobertas] = useState<DespesaResumoConciliacao[]>([]);
  const [extrapolados, setExtrapolados] = useState<ContratoDto[]>([]);
  const [contratos, setContratos] = useState<ContratoDto[]>([]);
  const [fornecedores, setFornecedores] = useState<FornecedorResumo[]>([]);
  const [relatorios, setRelatorios] = useState<RelatorioConciliacao[]>([]);
  const [relatorioExpandido, setRelatorioExpandido] = useState<string | null>(null);

  const [filtroBusca, setFiltroBusca] = useState('');
  const [filtroCategoria, setFiltroCategoria] = useState('');
  const [filtroStatus, setFiltroStatus] = useState('');
  const [filtroFornecedor, setFiltroFornecedor] = useState('');

  const [despesaParaVincular, setDespesaParaVincular] = useState<DespesaConciliacaoView | null>(null);
  const [contratoSelecionado, setContratoSelecionado] = useState('');
  const [confirmarSubstituicao, setConfirmarSubstituicao] = useState(false);
  const [vinculando, setVinculando] = useState(false);

  const [mostrarResultadoAuto, setMostrarResultadoAuto] = useState(false);

  const fornecedorNome = useCallback(
    (id: string) => fornecedores.find((f) => f.id === id)?.nome ?? id,
    [fornecedores]
  );

  const vinculoPorDespesa = useMemo(
    () => new Map(vinculos.map((v) => [v.despesaId, v])),
    [vinculos]
  );

  const despesasElegiveis: DespesaConciliacaoView[] = useMemo(() => {
    return despesas
      .filter((d) => d.valor > 0)
      .map((d) => {
        const v = vinculoPorDespesa.get(d.id);
        return {
          id: d.id,
          eventoId: d.eventoId,
          categoria: d.categoria,
          fornecedorId: d.fornecedorId,
          valor: d.valor,
          data: d.data,
          status: d.status,
          coverageStatus: v ? 'COBERTA' : 'DESCOBERTA',
          contratoId: v?.contratoId,
          metodo: v?.metodo,
        };
      });
  }, [despesas, vinculoPorDespesa]);

  const extrapoladosView: ExtrapoladoView[] = useMemo(() => {
    const valorPorDespesa = new Map(despesas.map((d) => [d.id, d.valor]));
    const totalPorContrato = new Map<string, number>();
    for (const v of vinculos) {
      const valor = valorPorDespesa.get(v.despesaId) ?? 0;
      totalPorContrato.set(v.contratoId, (totalPorContrato.get(v.contratoId) ?? 0) + valor);
    }
    return extrapolados.map((contrato) => {
      const totalConciliado = totalPorContrato.get(contrato.id) ?? 0;
      return {
        contrato,
        totalConciliado,
        excess: totalConciliado - contrato.valor,
      };
    });
  }, [extrapolados, vinculos, despesas]);

  const cobertas = despesasElegiveis.filter((d) => d.coverageStatus === 'COBERTA');
  const totalElegivel = despesasElegiveis.reduce((s, d) => s + d.valor, 0);
  const totalCoberto = cobertas.reduce((s, d) => s + d.valor, 0);
  const totalDescoberto = totalElegivel - totalCoberto;
  const percentualCobertura =
    totalElegivel > 0 ? Math.round((totalCoberto / totalElegivel) * 100) : 0;

  const despesasFiltradas = useMemo(() => {
    return despesasElegiveis.filter((d) => {
      if (filtroBusca && !d.id.toLowerCase().includes(filtroBusca.toLowerCase())) return false;
      if (filtroCategoria && d.categoria !== filtroCategoria) return false;
      if (filtroStatus && d.coverageStatus !== filtroStatus) return false;
      if (
        filtroFornecedor &&
        !fornecedorNome(d.fornecedorId).toLowerCase().includes(filtroFornecedor.toLowerCase())
      ) {
        return false;
      }
      return true;
    });
  }, [despesasElegiveis, filtroBusca, filtroCategoria, filtroStatus, filtroFornecedor, fornecedorNome]);

  const contratosValidosParaVinculo = useMemo(() => {
    if (!despesaParaVincular) return [];
    return contratos.filter(
      (c) =>
        c.eventoId === despesaParaVincular.eventoId &&
        isContratoAtivo(c.status) &&
        isVigente(c, despesaParaVincular.data)
    );
  }, [contratos, despesaParaVincular]);

  const saldoContrato = useCallback(
    (contratoId: string) => {
      const contrato = contratos.find((c) => c.id === contratoId);
      if (!contrato) return 0;
      const total = vinculos
        .filter((v) => v.contratoId === contratoId)
        .reduce((s, v) => {
          const d = despesas.find((x) => x.id === v.despesaId);
          return s + (d?.valor ?? 0);
        }, 0);
      return contrato.valor - total;
    },
    [contratos, vinculos, despesas]
  );

  const tratarErro = (e: unknown) => {
    const msg = e instanceof ApiError ? e.message : 'Erro inesperado.';
    setErro(msg);
    toast.error(msg);
  };

  const recarregar = useCallback(async () => {
    setCarregando(true);
    setErro(null);
    try {
      const [desp, vinc, desc, extrap, cont, forn] = await Promise.all([
        financeiroApi.listarDespesas(eventoId),
        conciliacaoApi.listarVinculos(eventoId),
        conciliacaoApi.listarDespesasDescobertas(eventoId),
        conciliacaoApi.listarContratosExtrapolados(eventoId),
        listarContratosPorEventoApi(eventoId),
        financeiroApi.listarFornecedores(),
      ]);
      setDespesas(desp);
      setVinculos(vinc);
      setDescobertas(desc);
      setExtrapolados(extrap);
      setContratos(cont);
      setFornecedores(forn);
    } catch (e) {
      tratarErro(e);
    } finally {
      setCarregando(false);
    }
  }, [eventoId]);

  useEffect(() => {
    recarregar();
  }, [recarregar]);

  const executarAutomatica = async () => {
    setExecutandoAuto(true);
    setErro(null);
    try {
      await conciliacaoApi.executarAutomatica(eventoId);
      await recarregar();
      setMostrarResultadoAuto(true);
      toast.success('Conciliação automática concluída.');
    } catch (e) {
      tratarErro(e);
    } finally {
      setExecutandoAuto(false);
    }
  };

  const confirmarVinculoManual = async () => {
    if (!despesaParaVincular || !contratoSelecionado) return;
    if (despesaParaVincular.contratoId && !confirmarSubstituicao) {
      setConfirmarSubstituicao(true);
      return;
    }
    setVinculando(true);
    try {
      await conciliacaoApi.vincularManualmente(despesaParaVincular.id, contratoSelecionado);
      toast.success('Vínculo registrado com sucesso.');
      setDespesaParaVincular(null);
      setContratoSelecionado('');
      setConfirmarSubstituicao(false);
      await recarregar();
    } catch (e) {
      tratarErro(e);
    } finally {
      setVinculando(false);
    }
  };

  const gerarRelatorio = async () => {
    setGerandoRelatorio(true);
    try {
      const relatorio = await conciliacaoApi.gerarRelatorio(eventoId);
      setRelatorios((prev) => [relatorio, ...prev]);
      setRelatorioExpandido(relatorio.id);
      toast.success('Relatório de conciliação gerado.');
    } catch (e) {
      tratarErro(e);
    } finally {
      setGerandoRelatorio(false);
    }
  };

  const tabs: { id: Tab; label: string; badge?: number }[] = [
    { id: 'dashboard', label: 'Dashboard' },
    { id: 'despesas', label: 'Despesas' },
    { id: 'descobertas', label: 'Descobertas', badge: descobertas.length || undefined },
    { id: 'extrapolados', label: 'Extrapolados', badge: extrapolados.length || undefined },
    { id: 'relatorio', label: 'Relatório' },
  ];

  if (carregando && despesas.length === 0) {
    return (
      <div className="content-card" style={{ textAlign: 'center', padding: '2rem', color: '#6b7280' }}>
        Carregando conciliação…
      </div>
    );
  }

  return (
    <div className="conc-root">
      <div className="conc-header">
        <h2>Conciliação</h2>
        <p>
          Conciliação de contratos com despesas — <strong>{eventoNome}</strong>
        </p>
      </div>

      {erro && (
        <div className="alert-box yellow">
          <div className="alert-content">
            <p style={{ margin: 0, fontSize: '0.875rem' }}>{erro}</p>
          </div>
        </div>
      )}

      <div className="conc-tabs">
        {tabs.map((tab) => (
          <button
            key={tab.id}
            type="button"
            className={`conc-tab ${aba === tab.id ? 'active' : ''}`}
            onClick={() => setAba(tab.id)}
          >
            {tab.label}
            {tab.badge !== undefined && tab.badge > 0 && (
              <span className="conc-tab-badge">{tab.badge}</span>
            )}
          </button>
        ))}
      </div>

      {aba === 'dashboard' && (
        <>
          <div className="conc-kpi-grid">
            <div className="conc-kpi-card">
              <div className="conc-kpi-label">Despesas Elegíveis</div>
              <div className="conc-kpi-value">{formatBrl(totalElegivel)}</div>
              <div className="conc-kpi-sub">{despesasElegiveis.length} despesa(s)</div>
            </div>
            <div className="conc-kpi-card green">
              <div className="conc-kpi-label">Total Coberto</div>
              <div className="conc-kpi-value" style={{ color: '#166534' }}>
                {formatBrl(totalCoberto)}
              </div>
              <div className="conc-kpi-sub">
                {cobertas.length} despesa(s) — {percentualCobertura}%
              </div>
            </div>
            <div className="conc-kpi-card red">
              <div className="conc-kpi-label">Total Descoberto</div>
              <div className="conc-kpi-value" style={{ color: '#991b1b' }}>
                {formatBrl(totalDescoberto)}
              </div>
              <div className="conc-kpi-sub">{descobertas.length} despesa(s) sem cobertura</div>
            </div>
            <div className={`conc-kpi-card ${extrapolados.length > 0 ? 'orange' : ''}`}>
              <div className="conc-kpi-label">Contratos Extrapolados</div>
              <div className="conc-kpi-value">{extrapolados.length}</div>
              <div className="conc-kpi-sub">
                {extrapolados.length > 0 ? 'Valor excedente detectado' : 'Nenhum excedente'}
              </div>
            </div>
          </div>

          {totalElegivel > 0 && (
            <div className="content-card">
              <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.75rem' }}>
                <span style={{ fontWeight: 600, fontSize: '0.9rem' }}>Cobertura Geral</span>
                <span style={{ fontWeight: 700 }}>{percentualCobertura}%</span>
              </div>
              <div className="conc-coverage-bar">
                <div className="conc-coverage-fill" style={{ width: `${percentualCobertura}%` }} />
              </div>
              <div
                style={{
                  display: 'flex',
                  justifyContent: 'space-between',
                  marginTop: '0.5rem',
                  fontSize: '0.75rem',
                  color: '#9ca3af',
                }}
              >
                <span>{formatBrl(totalCoberto)} coberto</span>
                <span>{formatBrl(totalDescoberto)} descoberto</span>
              </div>
            </div>
          )}

          <div className="content-card">
            <h3 className="widget-title">Conciliação Automática</h3>
            <p style={{ fontSize: '0.85rem', color: '#6b7280', marginBottom: '1rem' }}>
              O sistema verificará cada despesa elegível e vinculará automaticamente ao contrato
              compatível (mesmo fornecedor e vigência). Vínculos manuais existentes podem ser
              substituídos pela conciliação automática.
            </p>
            <button
              type="button"
              className="action-btn"
              disabled={executandoAuto || despesasElegiveis.length === 0}
              onClick={executarAutomatica}
            >
              {executandoAuto ? 'Processando…' : 'Executar Conciliação Automática'}
            </button>
          </div>

          <div className="conc-quick-links">
            {[
              { tab: 'despesas' as Tab, label: 'Despesas para Conciliação', desc: `${despesasElegiveis.length} elegíveis`, cls: '' },
              { tab: 'descobertas' as Tab, label: 'Despesas Descobertas', desc: `${descobertas.length} sem cobertura`, cls: descobertas.length > 0 ? 'red' : '' },
              { tab: 'extrapolados' as Tab, label: 'Contratos Extrapolados', desc: `${extrapolados.length} com excedente`, cls: extrapolados.length > 0 ? 'orange' : '' },
              { tab: 'relatorio' as Tab, label: 'Relatório de Conciliação', desc: 'Gerar snapshot imutável', cls: 'purple' },
            ].map((item) => (
              <button
                key={item.tab}
                type="button"
                className={`conc-quick-link ${item.cls}`}
                onClick={() => setAba(item.tab)}
              >
                <div style={{ flex: 1, minWidth: 0 }}>
                  <div style={{ fontWeight: 500, fontSize: '0.9rem' }}>{item.label}</div>
                  <div style={{ fontSize: '0.75rem', color: '#6b7280' }}>{item.desc}</div>
                </div>
                <span style={{ color: '#9ca3af' }}>→</span>
              </button>
            ))}
          </div>
        </>
      )}

      {aba === 'despesas' && (
        <div className="content-card" style={{ padding: 0 }}>
          <div className="conc-filters">
            <input
              type="text"
              className="form-input"
              placeholder="Buscar por ID…"
              value={filtroBusca}
              onChange={(e) => setFiltroBusca(e.target.value)}
            />
            <select
              className="form-select"
              value={filtroCategoria}
              onChange={(e) => setFiltroCategoria(e.target.value)}
            >
              <option value="">Todas as categorias</option>
              {CATEGORIAS.map((c) => (
                <option key={c} value={c}>
                  {c}
                </option>
              ))}
            </select>
            <select
              className="form-select"
              value={filtroStatus}
              onChange={(e) => setFiltroStatus(e.target.value)}
            >
              <option value="">Todos os status</option>
              <option value="COBERTA">Coberta</option>
              <option value="DESCOBERTA">Descoberta</option>
            </select>
            <input
              type="text"
              className="form-input"
              placeholder="Filtrar fornecedor…"
              value={filtroFornecedor}
              onChange={(e) => setFiltroFornecedor(e.target.value)}
            />
          </div>
          <div className="table-container">
            <table className="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Fornecedor</th>
                  <th>Categoria</th>
                  <th>Data</th>
                  <th>Valor</th>
                  <th>Status</th>
                  <th>Contrato</th>
                  <th>Método</th>
                  <th>Ações</th>
                </tr>
              </thead>
              <tbody>
                {despesasFiltradas.length === 0 ? (
                  <tr>
                    <td colSpan={9} style={{ textAlign: 'center', padding: '2rem', color: '#6b7280' }}>
                      Nenhuma despesa encontrada.
                    </td>
                  </tr>
                ) : (
                  despesasFiltradas.map((d) => (
                    <tr key={d.id}>
                      <td>
                        <span style={{ fontFamily: 'monospace', fontSize: '0.75rem' }}>{d.id}</span>
                      </td>
                      <td>{fornecedorNome(d.fornecedorId)}</td>
                      <td>
                        <span className="competency-tag">{d.categoria}</span>
                      </td>
                      <td>{formatDate(d.data)}</td>
                      <td>{formatBrl(d.valor)}</td>
                      <td>
                        <CoverageBadge status={d.coverageStatus} />
                      </td>
                      <td>
                        {d.contratoId ? (
                          <span style={{ fontFamily: 'monospace', fontSize: '0.75rem', color: '#2563eb' }}>
                            {d.contratoId}
                          </span>
                        ) : (
                          '—'
                        )}
                      </td>
                      <td>
                        <MetodoBadge metodo={d.metodo} />
                      </td>
                      <td>
                        <button
                          type="button"
                          className="edit-link"
                          onClick={() => {
                            setDespesaParaVincular(d);
                            setContratoSelecionado('');
                            setConfirmarSubstituicao(false);
                          }}
                        >
                          {d.contratoId ? 'Substituir' : 'Vincular'}
                        </button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
          <div style={{ padding: '0.75rem 1rem', fontSize: '0.75rem', color: '#6b7280', borderTop: '1px solid #e5e7eb' }}>
            {despesasFiltradas.length} de {despesasElegiveis.length} despesa(s) elegível(is)
          </div>
        </div>
      )}

      {aba === 'descobertas' && (
        <>
          {descobertas.length === 0 ? (
            <div className="content-card conc-empty-state">
              <h3 style={{ color: '#111827', marginBottom: '0.5rem' }}>Todas as despesas estão cobertas!</h3>
              <p style={{ margin: 0 }}>Não há despesas sem cobertura contratual para este evento.</p>
            </div>
          ) : (
            <>
              <div className="conc-alert red">
                <div>
                  <strong>{descobertas.length} despesa(s) sem cobertura contratual</strong>
                  <p style={{ margin: '0.25rem 0 0', fontSize: '0.8rem' }}>
                    Total descoberto:{' '}
                    <strong>
                      {formatBrl(descobertas.reduce((s, d) => s + d.valor, 0))}
                    </strong>
                  </p>
                </div>
              </div>
              <div className="content-card" style={{ padding: 0 }}>
                <div className="table-container">
                  <table className="data-table">
                    <thead>
                      <tr>
                        <th>ID</th>
                        <th>Fornecedor</th>
                        <th>Categoria</th>
                        <th>Data</th>
                        <th>Valor</th>
                        <th>Ações</th>
                      </tr>
                    </thead>
                    <tbody>
                      {descobertas.map((d) => {
                        const view: DespesaConciliacaoView = {
                          ...d,
                          coverageStatus: 'DESCOBERTA',
                        };
                        return (
                          <tr key={d.id}>
                            <td>
                              <span style={{ fontFamily: 'monospace', fontSize: '0.75rem' }}>{d.id}</span>
                            </td>
                            <td>{fornecedorNome(d.fornecedorId)}</td>
                            <td>
                              <span className="competency-tag">{d.categoria}</span>
                            </td>
                            <td>{formatDate(d.data)}</td>
                            <td style={{ color: '#991b1b', fontWeight: 500 }}>{formatBrl(d.valor)}</td>
                            <td>
                              <button
                                type="button"
                                className="edit-link"
                                onClick={() => {
                                  setDespesaParaVincular(view);
                                  setContratoSelecionado('');
                                  setConfirmarSubstituicao(false);
                                }}
                              >
                                Vincular manualmente
                              </button>
                            </td>
                          </tr>
                        );
                      })}
                    </tbody>
                  </table>
                </div>
              </div>
            </>
          )}
        </>
      )}

      {aba === 'extrapolados' && (
        <>
          {extrapoladosView.length === 0 ? (
            <div className="content-card conc-empty-state">
              <h3 style={{ color: '#111827', marginBottom: '0.5rem' }}>Nenhum contrato extrapolado</h3>
              <p style={{ margin: 0 }}>Todos os contratos estão dentro dos valores contratados.</p>
            </div>
          ) : (
            <>
              <div className="conc-alert orange">
                <div>
                  <strong>{extrapoladosView.length} contrato(s) com valor extrapolado</strong>
                  <p style={{ margin: '0.25rem 0 0', fontSize: '0.8rem' }}>
                    Excedente total:{' '}
                    <strong>
                      {formatBrl(extrapoladosView.reduce((s, e) => s + e.excess, 0))}
                    </strong>
                  </p>
                </div>
              </div>
              {extrapoladosView.map(({ contrato, totalConciliado, excess }) => {
                const pct = Math.round((excess / (contrato.valor || 1)) * 100);
                return (
                  <div key={contrato.id} className="conc-extrap-card">
                    <div style={{ display: 'flex', justifyContent: 'space-between', gap: '1rem' }}>
                      <div>
                        <span style={{ fontFamily: 'monospace', fontSize: '0.75rem', fontWeight: 700, color: '#ea580c' }}>
                          {contrato.id}
                        </span>
                        <p style={{ margin: '0.25rem 0 0', fontWeight: 500 }}>{contrato.tipo}</p>
                        <p style={{ margin: '0.25rem 0 0', fontSize: '0.8rem', color: '#6b7280' }}>
                          {contrato.objeto}
                        </p>
                      </div>
                      <span className="badge" style={{ background: '#ffedd5', color: '#9a3412' }}>
                        +{pct}% excedido
                      </span>
                    </div>
                    <div className="conc-extrap-grid">
                      <div>
                        <div style={{ fontSize: '0.75rem', color: '#9ca3af' }}>Valor Contratado</div>
                        <div style={{ fontWeight: 600 }}>{formatBrl(contrato.valor)}</div>
                      </div>
                      <div>
                        <div style={{ fontSize: '0.75rem', color: '#9ca3af' }}>Total Conciliado</div>
                        <div style={{ fontWeight: 600, color: '#ea580c' }}>{formatBrl(totalConciliado)}</div>
                      </div>
                      <div>
                        <div style={{ fontSize: '0.75rem', color: '#9ca3af' }}>Excedente</div>
                        <div style={{ fontWeight: 600, color: '#dc2626' }}>{formatBrl(excess)}</div>
                      </div>
                    </div>
                  </div>
                );
              })}
            </>
          )}
        </>
      )}

      {aba === 'relatorio' && (
        <>
          <div className="content-card">
            <h3 className="widget-title">Gerar Relatório de Conciliação</h3>
            <p style={{ fontSize: '0.85rem', color: '#6b7280', marginBottom: '1rem' }}>
              O relatório é um snapshot imutável do estado atual da conciliação. Uma vez gerado,
              não será afetado por alterações futuras.
            </p>
            <button
              type="button"
              className="action-btn"
              disabled={gerandoRelatorio || despesasElegiveis.length === 0}
              onClick={gerarRelatorio}
              style={{ background: '#7c3aed' }}
            >
              {gerandoRelatorio ? 'Gerando…' : 'Gerar Relatório'}
            </button>
            {despesasElegiveis.length === 0 && (
              <p style={{ fontSize: '0.8rem', color: '#ea580c', marginTop: '0.5rem' }}>
                Não há despesas elegíveis para gerar o relatório.
              </p>
            )}
          </div>

          {relatorios.length === 0 ? (
            <div className="content-card conc-empty-state">
              <p style={{ margin: 0 }}>Nenhum relatório gerado nesta sessão.</p>
            </div>
          ) : (
            relatorios.map((rel) => (
              <div key={rel.id} className="content-card" style={{ padding: 0 }}>
                <div
                  className="conc-report-header"
                  onClick={() =>
                    setRelatorioExpandido(relatorioExpandido === rel.id ? null : rel.id)
                  }
                >
                  <div>
                    <div style={{ fontFamily: 'monospace', fontWeight: 600 }}>{rel.id}</div>
                    <div style={{ fontSize: '0.75rem', color: '#6b7280' }}>
                      {rel.itens.length} item(ns) •{' '}
                      {rel.itens.filter((i) => i.status === 'COBERTA').length} coberto(s)
                    </div>
                  </div>
                  <div style={{ textAlign: 'right', fontSize: '0.75rem', color: '#9ca3af' }}>
                    <div>{new Date(rel.dataGeracao).toLocaleString('pt-BR')}</div>
                    <div>{rel.responsavelId}</div>
                  </div>
                </div>
                {relatorioExpandido === rel.id && (
                  <div className="conc-report-item">
                    <div className="table-container">
                      <table className="data-table">
                        <thead>
                          <tr>
                            <th>Despesa</th>
                            <th>Contrato</th>
                            <th>Status</th>
                            <th>Método</th>
                          </tr>
                        </thead>
                        <tbody>
                          {rel.itens.map((item) => (
                            <tr key={item.despesaId}>
                              <td>
                                <span style={{ fontFamily: 'monospace', fontSize: '0.75rem' }}>
                                  {item.despesaId}
                                </span>
                              </td>
                              <td>
                                {item.contratoId ?? '—'}
                              </td>
                              <td>
                                <CoverageBadge status={item.status} />
                              </td>
                              <td>
                                <MetodoBadge metodo={item.metodo ?? undefined} />
                              </td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                  </div>
                )}
              </div>
            ))
          )}
        </>
      )}

      {mostrarResultadoAuto && (
        <div className="modal-overlay">
          <div className="modal-container">
            <div className="modal-body">
              <div className="modal-content-text" style={{ width: '100%' }}>
                <h3 className="modal-title">Conciliação Concluída</h3>
                <p className="modal-description">
                  {cobertas.length} despesa(s) coberta(s) • {descobertas.length} descoberta(s)
                </p>
              </div>
            </div>
            <div className="modal-actions">
              <button
                type="button"
                className="modal-btn-confirm"
                onClick={() => setMostrarResultadoAuto(false)}
              >
                Ver Resultados
              </button>
            </div>
          </div>
        </div>
      )}

      {despesaParaVincular && (
        <div className="modal-overlay">
          <div className="modal-container" style={{ maxWidth: 520 }}>
            <div className="modal-body" style={{ flexDirection: 'column', alignItems: 'stretch' }}>
              <h3 className="modal-title" style={{ marginBottom: '0.5rem' }}>
                {despesaParaVincular.contratoId ? 'Substituir Vínculo' : 'Vincular Manualmente'}
              </h3>
              <div style={{ background: '#f9fafb', borderRadius: '0.5rem', padding: '0.75rem', marginBottom: '1rem' }}>
                <div style={{ fontSize: '0.75rem', color: '#6b7280' }}>Despesa</div>
                <div style={{ fontFamily: 'monospace', fontSize: '0.85rem' }}>{despesaParaVincular.id}</div>
                <div style={{ fontSize: '0.8rem', color: '#6b7280', marginTop: '0.35rem' }}>
                  {fornecedorNome(despesaParaVincular.fornecedorId)} • {despesaParaVincular.categoria} •{' '}
                  {formatBrl(despesaParaVincular.valor)} • {formatDate(despesaParaVincular.data)}
                </div>
              </div>

              <div className="alert-box blue" style={{ marginBottom: '1rem' }}>
                <div className="alert-content">
                  <p style={{ margin: 0, fontSize: '0.8rem' }}>
                    Somente contratos ativos, do mesmo evento e com vigência válida na data da despesa
                    estão disponíveis.
                  </p>
                </div>
              </div>

              {contratosValidosParaVinculo.length === 0 ? (
                <p style={{ textAlign: 'center', color: '#6b7280', fontSize: '0.875rem' }}>
                  Nenhum contrato válido disponível para esta despesa.
                </p>
              ) : (
                <div style={{ maxHeight: 220, overflowY: 'auto', marginBottom: '1rem' }}>
                  {contratosValidosParaVinculo.map((c) => (
                    <button
                      key={c.id}
                      type="button"
                      className={`conc-contract-option ${contratoSelecionado === c.id ? 'selected' : ''}`}
                      onClick={() => setContratoSelecionado(c.id)}
                    >
                      <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                        <span style={{ fontFamily: 'monospace', fontSize: '0.75rem', fontWeight: 700, color: '#2563eb' }}>
                          {c.id}
                        </span>
                        <span
                          style={{
                            fontSize: '0.75rem',
                            color: saldoContrato(c.id) < 0 ? '#dc2626' : '#166534',
                          }}
                        >
                          Saldo: {formatBrl(saldoContrato(c.id))}
                        </span>
                      </div>
                      <p style={{ margin: '0.25rem 0 0', fontSize: '0.8rem' }}>{c.objeto}</p>
                      <p style={{ margin: '0.15rem 0 0', fontSize: '0.75rem', color: '#9ca3af' }}>
                        {c.tipo} • {formatDate(c.dataInicio)} – {formatDate(c.dataFim)}
                      </p>
                    </button>
                  ))}
                </div>
              )}

              {confirmarSubstituicao && (
                <div className="conc-alert orange" style={{ marginBottom: '1rem' }}>
                  <div style={{ fontSize: '0.85rem' }}>
                    O vínculo anterior será substituído pelo contrato selecionado. Confirmar?
                  </div>
                </div>
              )}
            </div>
            <div className="modal-actions">
              <button
                type="button"
                className="modal-btn-cancelar"
                onClick={() => {
                  setDespesaParaVincular(null);
                  setContratoSelecionado('');
                  setConfirmarSubstituicao(false);
                }}
              >
                Cancelar
              </button>
              <button
                type="button"
                className="modal-btn-confirm"
                disabled={!contratoSelecionado || vinculando}
                onClick={confirmarVinculoManual}
              >
                {confirmarSubstituicao ? 'Confirmar Substituição' : 'Vincular'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
