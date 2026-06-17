import { useState } from 'react'
import './estoque.css'
import { useEstoque } from './EstoqueContext'
import { formatEventoLabel } from './types'
import type { EstoqueView } from './types'

const TABS: { id: EstoqueView; label: string }[] = [
  { id: 'itens', label: 'Itens' },
  { id: 'reservas', label: 'Reservas' },
  { id: 'previsao', label: 'Previsão' },
  { id: 'redistribuicao', label: 'Redistribuição' },
  { id: 'consumo', label: 'Consumo' },
]

function statusBadgeClass(status: string): string {
  const s = status.toUpperCase()
  if (s === 'PENDENTE') return 'pendente'
  if (s === 'CONFIRMADA' || s === 'APLICADA') return 'confirmada'
  if (s === 'APLICADA') return 'aplicada'
  return 'pendente'
}

export default function EstoqueModule() {
  const [view, setView] = useState<EstoqueView>('itens')
  const ctx = useEstoque()

  const itensAtivos = ctx.itens.filter((i) => i.ativo)
  const totalDisponivel = ctx.itens.reduce((s, i) => s + i.quantidadeDisponivel, 0)

  if (ctx.loading) {
    return (
      <div className="content-card" style={{ textAlign: 'center', padding: '2rem', color: '#6b7280' }}>
        Carregando dados de estoque…
      </div>
    )
  }

  return (
    <div className="estoque-root">
      <div className="estoque-header">
        <div>
          <h1>Gestão de Estoque</h1>
          <p>Previsão de consumo, reservas e redistribuição entre eventos</p>
        </div>
        <button type="button" className="estoque-btn estoque-btn-secondary" onClick={() => void ctx.refresh()}>
          Atualizar
        </button>
      </div>

      <div className="estoque-stats">
        <div className="estoque-stat">
          <div className="estoque-stat-value">{ctx.itens.length}</div>
          <div className="estoque-stat-label">Itens cadastrados</div>
        </div>
        <div className="estoque-stat">
          <div className="estoque-stat-value">{itensAtivos.length}</div>
          <div className="estoque-stat-label">Itens ativos</div>
        </div>
        <div className="estoque-stat">
          <div className="estoque-stat-value">{totalDisponivel}</div>
          <div className="estoque-stat-label">Unidades disponíveis</div>
        </div>
        <div className="estoque-stat">
          <div className="estoque-stat-value">{ctx.reservas.length}</div>
          <div className="estoque-stat-label">Reservas</div>
        </div>
      </div>

      <div className="estoque-tabs">
        {TABS.map((t) => (
          <button
            key={t.id}
            type="button"
            className={`estoque-tab ${view === t.id ? 'active' : ''}`}
            onClick={() => setView(t.id)}
          >
            {t.label}
          </button>
        ))}
      </div>

      {ctx.loading ? (
        <div className="estoque-loading">Carregando...</div>
      ) : (
        <>
          {view === 'itens' && <ItensView />}
          {view === 'reservas' && <ReservasView />}
          {view === 'previsao' && <PrevisaoView />}
          {view === 'redistribuicao' && <RedistribuicaoView />}
          {view === 'consumo' && <ConsumoView />}
        </>
      )}
    </div>
  )
}

function ItensView() {
  const { itens, substituicoes, cadastrarItem, editarItem, adicionarEstoque, desativarItem, cadastrarSubstituicao } = useEstoque()
  const [nome, setNome] = useState('')
  const [qtd, setQtd] = useState(0)
  const [editId, setEditId] = useState<string | null>(null)
  const [addQtd] = useState(10)
  const [subOriginal, setSubOriginal] = useState(itens[0]?.id ?? '')
  const [subSubstituto, setSubSubstituto] = useState(itens[1]?.id ?? '')
  const [subFator, setSubFator] = useState(1)

  const nomeItem = (id: string) => itens.find((i) => i.id === id)?.nome ?? id.slice(0, 8)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!nome.trim() || qtd < 0) return
    if (editId) {
      await editarItem(editId, nome, qtd)
      setEditId(null)
    } else {
      await cadastrarItem(nome, qtd)
    }
    setNome('')
    setQtd(0)
  }

  return (
    <>
      <div className="estoque-card">
        <h3 style={{ margin: '0 0 1rem', fontSize: '1rem' }}>{editId ? 'Editar item' : 'Cadastrar item'}</h3>
        <form className="estoque-form" onSubmit={(e) => void handleSubmit(e)}>
          <label>
            Nome
            <input value={nome} onChange={(e) => setNome(e.target.value)} required />
          </label>
          <label>
            Quantidade total
            <input type="number" min={0} value={qtd} onChange={(e) => setQtd(Number(e.target.value))} required />
          </label>
          <div className="estoque-actions">
            <button type="submit" className="estoque-btn estoque-btn-primary">
              {editId ? 'Salvar' : 'Cadastrar'}
            </button>
            {editId && (
              <button type="button" className="estoque-btn estoque-btn-secondary" onClick={() => setEditId(null)}>
                Cancelar
              </button>
            )}
          </div>
        </form>
      </div>

      <div className="estoque-card">
        {itens.length === 0 ? (
          <div className="estoque-empty">Nenhum item cadastrado</div>
        ) : (
          <table className="estoque-table">
            <thead>
              <tr>
                <th>Nome</th>
                <th>Total</th>
                <th>Disponível</th>
                <th>Status</th>
                <th>Ações</th>
              </tr>
            </thead>
            <tbody>
              {itens.map((item) => (
                <tr key={item.id}>
                  <td>{item.nome}</td>
                  <td>{item.quantidadeTotal}</td>
                  <td>{item.quantidadeDisponivel}</td>
                  <td>
                    <span className={`estoque-badge ${item.ativo ? 'ativo' : 'inativo'}`}>
                      {item.ativo ? 'Ativo' : 'Inativo'}
                    </span>
                  </td>
                  <td>
                    <div className="estoque-actions">
                      <button
                        type="button"
                        className="estoque-btn estoque-btn-secondary"
                        onClick={() => {
                          setEditId(item.id)
                          setNome(item.nome)
                          setQtd(item.quantidadeTotal)
                        }}
                      >
                        Editar
                      </button>
                      <button
                        type="button"
                        className="estoque-btn estoque-btn-primary"
                        onClick={() => void adicionarEstoque(item.id, addQtd)}
                      >
                        +{addQtd}
                      </button>
                      {item.ativo && (
                        <button
                          type="button"
                          className="estoque-btn estoque-btn-danger"
                          onClick={() => void desativarItem(item.id)}
                        >
                          Desativar
                        </button>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      <div className="estoque-card">
        <h3 style={{ margin: '0 0 1rem', fontSize: '1rem' }}>Equivalências entre itens</h3>
        <div className="estoque-form">
          <label>
            Item original
            <select value={subOriginal} onChange={(e) => setSubOriginal(e.target.value)}>
              {itens.filter((i) => i.ativo).map((i) => (
                <option key={i.id} value={i.id}>{i.nome}</option>
              ))}
            </select>
          </label>
          <label>
            Item substituto
            <select value={subSubstituto} onChange={(e) => setSubSubstituto(e.target.value)}>
              {itens.filter((i) => i.ativo).map((i) => (
                <option key={i.id} value={i.id}>{i.nome}</option>
              ))}
            </select>
          </label>
          <label>
            Fator de equivalência
            <input type="number" min={0.1} step={0.1} value={subFator} onChange={(e) => setSubFator(Number(e.target.value))} />
          </label>
          <button
            type="button"
            className="estoque-btn estoque-btn-primary"
            onClick={() => void cadastrarSubstituicao(subOriginal, subSubstituto, subFator)}
          >
            Cadastrar substituição
          </button>
        </div>
        {substituicoes.length > 0 && (
          <table className="estoque-table" style={{ marginTop: '1rem' }}>
            <thead>
              <tr>
                <th>Original</th>
                <th>Substituto</th>
                <th>Fator</th>
              </tr>
            </thead>
            <tbody>
              {substituicoes.map((s) => (
                <tr key={s.id}>
                  <td>{nomeItem(s.itemOriginalId)}</td>
                  <td>{nomeItem(s.itemSubstitutoId)}</td>
                  <td>{s.fatorEquivalencia}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </>
  )
}

function ReservasView() {
  const { reservas, eventos, itens, criarReserva, confirmarReserva, cancelarReserva } = useEstoque()
  const [eventoId, setEventoId] = useState(eventos[0]?.id ?? '')
  const [inicio, setInicio] = useState('2026-06-01T08:00:00')
  const [fim, setFim] = useState('2026-06-01T18:00:00')
  const [itemId, setItemId] = useState(itens[0]?.id ?? '')
  const [qtd, setQtd] = useState(10)

  const nomeEvento = (id: string) => formatEventoLabel(id, eventos)
  const nomeItem = (id: string) => itens.find((i) => i.id === id)?.nome ?? id

  const handleCriar = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!eventoId || !itemId) return
    await criarReserva({ eventoId, dataInicio: inicio, dataFim: fim, itens: [{ itemEstoqueId: itemId, quantidade: qtd }] })
  }

  return (
    <>
      <div className="estoque-card">
        <h3 style={{ margin: '0 0 1rem', fontSize: '1rem' }}>Nova reserva</h3>
        <form className="estoque-form" onSubmit={(e) => void handleCriar(e)}>
          <label>
            Evento
            <select value={eventoId} onChange={(e) => setEventoId(e.target.value)}>
              {eventos.map((ev) => (
                <option key={ev.id} value={ev.id}>{formatEventoLabel(ev.id, eventos)}</option>
              ))}
            </select>
          </label>
          <label>
            Início
            <input type="datetime-local" value={inicio.slice(0, 16)} onChange={(e) => setInicio(`${e.target.value}:00`)} />
          </label>
          <label>
            Fim
            <input type="datetime-local" value={fim.slice(0, 16)} onChange={(e) => setFim(`${e.target.value}:00`)} />
          </label>
          <label>
            Item
            <select value={itemId} onChange={(e) => setItemId(e.target.value)}>
              {itens.filter((i) => i.ativo).map((i) => (
                <option key={i.id} value={i.id}>{i.nome} ({i.quantidadeDisponivel} disp.)</option>
              ))}
            </select>
          </label>
          <label>
            Quantidade
            <input type="number" min={1} value={qtd} onChange={(e) => setQtd(Number(e.target.value))} />
          </label>
          <button type="submit" className="estoque-btn estoque-btn-primary">Criar reserva</button>
        </form>
      </div>

      <div className="estoque-card">
        {reservas.length === 0 ? (
          <div className="estoque-empty">Nenhuma reserva</div>
        ) : (
          <table className="estoque-table">
            <thead>
              <tr>
                <th>Evento</th>
                <th>Período</th>
                <th>Itens</th>
                <th>Status</th>
                <th>Ações</th>
              </tr>
            </thead>
            <tbody>
              {reservas.map((r) => (
                <tr key={r.id}>
                  <td>{nomeEvento(r.eventoId)}</td>
                  <td>{r.dataInicio.slice(0, 16)} — {r.dataFim.slice(0, 16)}</td>
                  <td>
                    {r.itensReservados.map((it) => (
                      <div key={it.id}>{nomeItem(it.itemEstoqueId)}: {it.quantidade}</div>
                    ))}
                  </td>
                  <td>
                    <span className={`estoque-badge ${statusBadgeClass(r.status)}`}>{r.status}</span>
                  </td>
                  <td>
                    <div className="estoque-actions">
                      {r.status === 'PENDENTE' && (
                        <button type="button" className="estoque-btn estoque-btn-primary" onClick={() => void confirmarReserva(r.id)}>
                          Confirmar
                        </button>
                      )}
                      {r.status !== 'CANCELADA' && r.status !== 'FINALIZADA' && (
                        <button type="button" className="estoque-btn estoque-btn-danger" onClick={() => void cancelarReserva(r.id)}>
                          Cancelar
                        </button>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </>
  )
}

function PrevisaoView() {
  const { eventos, itens, previsoes, gerarPrevisao, recalcularPrevisao, ajustarPrevisao } = useEstoque()
  const [eventoId, setEventoId] = useState(eventos[0]?.id ?? '')
  const [ajustes, setAjustes] = useState<Record<string, number>>({})
  const [justificativa, setJustificativa] = useState('Ajuste manual via interface')

  const nomeEvento = (id: string) => formatEventoLabel(id, eventos)
  const nomeItem = (id: string) => itens.find((i) => i.id === id)?.nome ?? id.slice(0, 8)

  const iniciarAjuste = (prevId: string, itemEstoqueId: string, valorAtual: number) => {
    setAjustes((a) => ({ ...a, [`${prevId}:${itemEstoqueId}`]: valorAtual }))
  }

  const aplicarAjuste = async (prevId: string) => {
    const mapa: Record<string, number> = {}
    Object.entries(ajustes).forEach(([key, val]) => {
      const [pid, itemId] = key.split(':')
      if (pid === prevId) mapa[itemId] = val
    })
    if (Object.keys(mapa).length === 0) return
    await ajustarPrevisao(prevId, mapa, justificativa)
    setAjustes({})
  }

  return (
    <>
      <div className="estoque-card">
        <h3 style={{ margin: '0 0 1rem', fontSize: '1rem' }}>Gerar previsão de consumo</h3>
        <div className="estoque-form">
          <label>
            Evento
            <select value={eventoId} onChange={(e) => setEventoId(e.target.value)}>
              {eventos.map((ev) => (
                <option key={ev.id} value={ev.id}>{formatEventoLabel(ev.id, eventos)}</option>
              ))}
            </select>
          </label>
          <button type="button" className="estoque-btn estoque-btn-primary" onClick={() => void gerarPrevisao(eventoId)}>
            Gerar previsão
          </button>
        </div>
      </div>

      {previsoes.map((prev) => (
        <div key={prev.id} className="estoque-card">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '0.5rem' }}>
            <div>
              <h3 style={{ margin: '0 0 0.5rem', fontSize: '1rem' }}>
                {nomeEvento(prev.eventoId)} — v{prev.versaoAtual}
              </h3>
              <p style={{ fontSize: '0.8rem', color: '#6b7280', margin: 0 }}>
                {prev.fallbackUtilizado ? 'Fallback utilizado' : `Baseado em ${prev.totalEventosBase} evento(s) histórico(s)`}
                {' · '}{prev.statusHistorico}
                {prev.invalidada && ' · Invalidada'}
              </p>
            </div>
            {!prev.invalidada && (
              <button type="button" className="estoque-btn estoque-btn-secondary" onClick={() => void recalcularPrevisao(prev.id)}>
                Recalcular
              </button>
            )}
          </div>

          <table className="estoque-table" style={{ marginTop: '1rem' }}>
            <thead>
              <tr>
                <th>Item</th>
                <th>Categoria</th>
                <th>Estimada</th>
                <th>Mín–Máx</th>
                <th>Final</th>
                <th>Explicação</th>
                {!prev.invalidada && <th>Ajuste</th>}
              </tr>
            </thead>
            <tbody>
              {prev.itens.map((it) => {
                const key = `${prev.id}:${it.itemEstoqueId}`
                return (
                  <tr key={it.itemEstoqueId + it.categoriaConsumo}>
                    <td>{nomeItem(it.itemEstoqueId)}</td>
                    <td>{it.categoriaConsumo}</td>
                    <td>{it.quantidadeEstimada}</td>
                    <td>{it.quantidadeMinima} – {it.quantidadeMaxima}</td>
                    <td>{it.quantidadeFinal}</td>
                    <td style={{ fontSize: '0.75rem', maxWidth: '220px' }}>{it.explicacaoCalculo || '—'}</td>
                    {!prev.invalidada && (
                      <td>
                        <input
                          type="number"
                          min={0}
                          style={{ width: '70px' }}
                          value={ajustes[key] ?? it.quantidadeFinal}
                          onChange={(e) => iniciarAjuste(prev.id, it.itemEstoqueId, Number(e.target.value))}
                        />
                      </td>
                    )}
                  </tr>
                )
              })}
            </tbody>
          </table>

          {!prev.invalidada && prev.itens.length > 0 && (
            <div className="estoque-form" style={{ marginTop: '1rem' }}>
              <label>
                Justificativa do ajuste
                <input value={justificativa} onChange={(e) => setJustificativa(e.target.value)} />
              </label>
              <button type="button" className="estoque-btn estoque-btn-primary" onClick={() => void aplicarAjuste(prev.id)}>
                Salvar ajustes
              </button>
            </div>
          )}

          {prev.historicoRegistros?.length > 0 && (
            <div style={{ marginTop: '1.5rem' }}>
              <h4 style={{ margin: '0 0 0.75rem', fontSize: '0.9rem' }}>Histórico de versões</h4>
              {prev.historicoRegistros.map((reg) => (
                <div key={reg.id} style={{ marginBottom: '0.75rem', padding: '0.75rem', background: '#f9fafb', borderRadius: '6px' }}>
                  <div style={{ fontSize: '0.8rem', color: '#374151' }}>
                    <strong>v{reg.versao}</strong> · {reg.tipoRegistro} · {reg.dataHora.slice(0, 16)}
                    {reg.justificativa && ` · ${reg.justificativa}`}
                  </div>
                  <div style={{ fontSize: '0.75rem', color: '#6b7280', marginTop: '0.25rem' }}>
                    {reg.itens.map((it) => (
                      <span key={it.itemEstoqueId + it.categoriaConsumo} style={{ marginRight: '1rem' }}>
                        {it.categoriaConsumo}: {it.quantidadeFinal}
                      </span>
                    ))}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      ))}

      {previsoes.length === 0 && (
        <div className="estoque-card estoque-empty">Gere uma previsão selecionando um evento acima</div>
      )}
    </>
  )
}

function RedistribuicaoView() {
  const { cenarios, gerarCenario, aplicarCenario, eventos, itens } = useEstoque()
  const [inicio, setInicio] = useState(() => {
    const d = new Date()
    d.setDate(d.getDate() + 6)
    return d.toISOString().slice(0, 10) + 'T00:00:00'
  })
  const [fim, setFim] = useState(() => {
    const d = new Date()
    d.setDate(d.getDate() + 14)
    return d.toISOString().slice(0, 10) + 'T23:59:59'
  })

  const nomeEvento = (id: string) => formatEventoLabel(id, eventos)
  const nomeItem = (id: string) => itens.find((i) => i.id === id)?.nome ?? id.slice(0, 8)

  const renderAlocacoes = (titulo: string, alocacoes: typeof cenarios[0]['alocacoesAtuais']) => (
    <div style={{ flex: 1, minWidth: '240px' }}>
      <h4 style={{ margin: '0 0 0.5rem', fontSize: '0.85rem' }}>{titulo}</h4>
      {alocacoes.length === 0 ? (
        <p style={{ fontSize: '0.8rem', color: '#9ca3af' }}>Nenhuma alocação</p>
      ) : (
        <table className="estoque-table">
          <thead>
            <tr>
              <th>Evento</th>
              <th>Item</th>
              <th>Anterior</th>
              <th>Nova</th>
              <th>Substituto</th>
            </tr>
          </thead>
          <tbody>
            {alocacoes.map((a, idx) => (
              <tr key={(a.id ?? idx) + a.eventoId}>
                <td>{nomeEvento(a.eventoId)}</td>
                <td>{nomeItem(a.itemEstoqueId)}</td>
                <td>{a.quantidadeAnterior}</td>
                <td>{a.quantidadeRedistribuida}</td>
                <td>
                  {a.itemSubstitutoId
                    ? `${nomeItem(a.itemSubstitutoId)} (${a.quantidadeSubstituto})`
                    : '—'}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  )

  return (
    <>
      <div className="estoque-card">
        <h3 style={{ margin: '0 0 1rem', fontSize: '1rem' }}>Gerar cenário de redistribuição</h3>
        <p style={{ fontSize: '0.8rem', color: '#6b7280', margin: '0 0 1rem' }}>
          Use um período que cubra as reservas concorrentes (ex.: próximos 7–14 dias) para simular escassez.
        </p>
        <div className="estoque-form">
          <label>
            Período início
            <input type="datetime-local" value={inicio.slice(0, 16)} onChange={(e) => setInicio(`${e.target.value}:00`)} />
          </label>
          <label>
            Período fim
            <input type="datetime-local" value={fim.slice(0, 16)} onChange={(e) => setFim(`${e.target.value}:00`)} />
          </label>
          <button type="button" className="estoque-btn estoque-btn-primary" onClick={() => void gerarCenario(inicio, fim)}>
            Gerar cenário
          </button>
        </div>
      </div>

      {cenarios.map((c) => (
        <div key={c.id} className="estoque-card">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
            <div>
              <strong>Cenário {c.id.slice(0, 8)}</strong>
              <span className={`estoque-badge ${statusBadgeClass(c.status)}`} style={{ marginLeft: '0.5rem' }}>
                {c.status}
              </span>
            </div>
            {c.status === 'PENDENTE' && (
              <button type="button" className="estoque-btn estoque-btn-primary" onClick={() => void aplicarCenario(c.id)}>
                Aplicar redistribuição
              </button>
            )}
          </div>
          <p style={{ fontSize: '0.8rem', color: '#6b7280' }}>
            {c.periodoInicio.slice(0, 16)} — {c.periodoFim.slice(0, 16)}
          </p>

          <div style={{ display: 'flex', gap: '1.5rem', flexWrap: 'wrap', marginTop: '1rem' }}>
            {renderAlocacoes('Alocações atuais', c.alocacoesAtuais)}
            {renderAlocacoes('Alocações otimizadas', c.alocacoesOtimizadas)}
          </div>

          {c.impactosPorEvento?.length > 0 && (
            <div style={{ marginTop: '1rem' }}>
              <h4 style={{ margin: '0 0 0.5rem', fontSize: '0.85rem' }}>Impactos por evento</h4>
              {c.impactosPorEvento.map((imp) => (
                <div key={imp.eventoId} style={{ marginTop: '0.5rem' }}>
                  <strong>{nomeEvento(imp.eventoId)}</strong>
                  {imp.itensImpactados.map((it) => (
                    <div key={it.itemEstoqueId} style={{ fontSize: '0.8rem', color: '#374151' }}>
                      {nomeItem(it.itemEstoqueId)}: anterior {it.quantidadeAnterior} → redistribuída {it.quantidadeRedistribuida}
                      {it.deficit > 0 && ` · déficit ${it.deficit}`}
                      {it.excesso > 0 && ` · excesso ${it.excesso}`}
                    </div>
                  ))}
                </div>
              ))}
            </div>
          )}

          {c.historico?.length > 0 && (
            <div style={{ marginTop: '1.5rem' }}>
              <h4 style={{ margin: '0 0 0.75rem', fontSize: '0.9rem' }}>Histórico do cenário</h4>
              {c.historico.map((reg) => (
                <div key={reg.id} style={{ marginBottom: '0.5rem', fontSize: '0.8rem', color: '#374151' }}>
                  {reg.dataHora.slice(0, 16)} — {reg.descricao} ({reg.usuarioResponsavelId})
                </div>
              ))}
            </div>
          )}
        </div>
      ))}

      {cenarios.length === 0 && (
        <div className="estoque-card estoque-empty">Nenhum cenário gerado</div>
      )}
    </>
  )
}

function ConsumoView() {
  const { consumos, eventos, itens, registrarConsumo } = useEstoque()
  const [eventoId, setEventoId] = useState(eventos[0]?.id ?? '')
  const [itemId, setItemId] = useState(itens[0]?.id ?? '')
  const [categoria, setCategoria] = useState('bebida')
  const [qtd, setQtd] = useState(50)

  const nomeEvento = (id: string) => formatEventoLabel(id, eventos)

  const handleRegistrar = async (e: React.FormEvent) => {
    e.preventDefault()
    await registrarConsumo({
      eventoId,
      itens: [{ itemEstoqueId: itemId, categoriaConsumo: categoria, quantidadeConsumida: qtd }],
    })
  }

  return (
    <>
      <div className="estoque-card">
        <h3 style={{ margin: '0 0 1rem', fontSize: '1rem' }}>Registrar consumo pós-evento</h3>
        <form className="estoque-form" onSubmit={(e) => void handleRegistrar(e)}>
          <label>
            Evento
            <select value={eventoId} onChange={(e) => setEventoId(e.target.value)}>
              {eventos.map((ev) => (
                <option key={ev.id} value={ev.id}>{formatEventoLabel(ev.id, eventos)}</option>
              ))}
            </select>
          </label>
          <label>
            Item
            <select value={itemId} onChange={(e) => setItemId(e.target.value)}>
              {itens.map((i) => (
                <option key={i.id} value={i.id}>{i.nome}</option>
              ))}
            </select>
          </label>
          <label>
            Categoria
            <input value={categoria} onChange={(e) => setCategoria(e.target.value)} />
          </label>
          <label>
            Quantidade consumida
            <input type="number" min={1} value={qtd} onChange={(e) => setQtd(Number(e.target.value))} />
          </label>
          <button type="submit" className="estoque-btn estoque-btn-primary">Registrar</button>
        </form>
      </div>

      <div className="estoque-card">
        {consumos.length === 0 ? (
          <div className="estoque-empty">Nenhum consumo registrado</div>
        ) : (
          <table className="estoque-table">
            <thead>
              <tr>
                <th>Evento</th>
                <th>Data</th>
                <th>Itens</th>
                <th>Válido</th>
              </tr>
            </thead>
            <tbody>
              {consumos.map((c) => (
                <tr key={c.id}>
                  <td>{nomeEvento(c.eventoId)}</td>
                  <td>{c.dataRegistro.slice(0, 16)}</td>
                  <td>
                    {c.itensConsumidos.map((it, idx) => (
                      <div key={idx}>{it.categoriaConsumo}: {it.quantidadeConsumida}</div>
                    ))}
                  </td>
                  <td>{c.valido ? 'Sim' : 'Não'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </>
  )
}
