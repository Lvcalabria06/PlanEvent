import { useState } from 'react';
import { CLASSIFICACAO_LABELS, PORTE_EVENTO_OPTIONS, TIPO_EVENTO_OPTIONS } from '../../../modules/planning/eventos/constants';
import type { CandidatoAnaliseLocalDto, EventoDto } from '../../../modules/planning/eventos/dto';
import { fromApiDateInput } from '../../../modules/planning/eventos/mappers';
import { useEventos } from '../EventosContext';

interface EventoAlocacaoPageProps {
	evento: EventoDto;
	onBack: () => void;
	onSaved: () => void;
}

function classificacaoBadgeClass(classificacao: string) {
	switch (classificacao) {
		case 'RECOMENDADO':
			return 'disponivel';
		case 'VIAVEL_COM_RESSALVA':
			return 'alocados';
		case 'INADEQUADO':
			return 'pendente';
		default:
			return 'inativo';
	}
}

export function EventoAlocacaoPage({ evento, onBack, onSaved }: EventoAlocacaoPageProps) {
	const { analisarLocais, definirAlocacao } = useEventos();

	const [tetoCusto, setTetoCusto] = useState(
		evento.tetoCustoEspacoInformado?.toString() ?? '',
	);
	const [candidatos, setCandidatos] = useState<CandidatoAnaliseLocalDto[]>([]);
	const [principalId, setPrincipalId] = useState(evento.localId ?? '');
	const [contingenciaIds, setContingenciaIds] = useState<string[]>(
		evento.locaisContingenciaOrdenados ?? [],
	);
	const [analisando, setAnalisando] = useState(false);
	const [salvando, setSalvando] = useState(false);
	const [feedback, setFeedback] = useState<string | null>(null);
	const [analiseError, setAnaliseError] = useState<string | null>(null);

	const handleAnalisar = async () => {
		const teto = parseFloat(tetoCusto);
		if (isNaN(teto) || teto <= 0) {
			setAnaliseError('Informe um teto de custo válido.');
			return;
		}
		setAnalisando(true);
		setAnaliseError(null);
		const resultado = await analisarLocais(evento.id, teto);
		if (!resultado) {
			setAnaliseError('Não foi possível analisar os locais. Verifique o backend.');
		} else {
			setCandidatos(resultado);
		}
		setAnalisando(false);
	};

	const toggleContingencia = (localId: string) => {
		if (localId === principalId) return;
		setContingenciaIds(prev => {
			if (prev.includes(localId)) return prev.filter(id => id !== localId);
			return [...prev, localId];
		});
	};

	const moveContingencia = (localId: string, direction: 'up' | 'down') => {
		const idx = contingenciaIds.indexOf(localId);
		if (idx === -1) return;
		const next = [...contingenciaIds];
		const swap = direction === 'up' ? idx - 1 : idx + 1;
		if (swap < 0 || swap >= next.length) return;
		[next[idx], next[swap]] = [next[swap], next[idx]];
		setContingenciaIds(next);
	};

	const handleSalvar = async () => {
		const teto = parseFloat(tetoCusto);
		if (!principalId) {
			setFeedback('Selecione um local principal.');
			return;
		}
		if (isNaN(teto) || teto <= 0) {
			setFeedback('Informe um teto de custo válido.');
			return;
		}
		setSalvando(true);
		setFeedback(null);
		const erro = await definirAlocacao(evento.id, principalId, teto, contingenciaIds);
		if (erro) {
			setFeedback(erro);
		} else {
			onSaved();
		}
		setSalvando(false);
	};

	const candidatosPrincipais = candidatos.filter(c => c.podeSerPrincipal);

	const tipoLabel =
		TIPO_EVENTO_OPTIONS.find(t => t.value === evento.tipo)?.label ?? evento.tipo;
	const porteLabel =
		PORTE_EVENTO_OPTIONS.find(p => p.value === evento.porte)?.label ?? evento.porte;
	const participantesLabel = evento.quantidadeEstimadaParticipantes.toLocaleString('pt-BR');

	return (
		<div className="module-page">
			<button type="button" className="back-link" onClick={onBack}>
				<svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
					<line x1="19" y1="12" x2="5" y2="12" />
					<polyline points="12 19 5 12 12 5" />
				</svg>
				Voltar ao evento
			</button>

			<div className="module-header">
				<div className="title-area">
					<h1>Planejamento de local</h1>
					<p>
						{evento.nome} · período {fromApiDateInput(evento.dataInicio)} a{' '}
						{fromApiDateInput(evento.dataFim)}
					</p>
				</div>
			</div>

			<div className="alert-box blue" style={{ marginBottom: '1rem' }}>
				<div className="alert-content">
					<p style={{ fontSize: '0.85rem', margin: 0 }}>
						Defina o local principal e as contingências <strong>antes</strong> de confirmar a
						preparação do evento. A análise classifica cada local conforme custo, capacidade e
						agenda.
					</p>
				</div>
			</div>

			<div className="content-card evento-context-card evento-card-interactive">
				<h3 className="evento-section-title">Contexto do evento (usado na análise)</h3>
				<div className="detail-grid">
					<div className="detail-field">
						<span className="detail-label">Participantes estimados</span>
						<p>{participantesLabel}</p>
					</div>
					<div className="detail-field">
						<span className="detail-label">Período</span>
						<p>{fromApiDateInput(evento.dataInicio)} a {fromApiDateInput(evento.dataFim)}</p>
					</div>
					<div className="detail-field">
						<span className="detail-label">Tipo / Porte</span>
						<p>{tipoLabel} · {porteLabel}</p>
					</div>
					<div className="detail-field full-width">
						<span className="detail-label">Requisitos de infraestrutura</span>
						<p>{evento.requisitosInfraestrutura?.trim() || 'Nenhum requisito informado'}</p>
					</div>
				</div>
			</div>

			<div className="content-card evento-card-interactive">
				<h3 className="evento-section-title">Parâmetros da análise</h3>
				<div className="form-grid">
					<div className="form-group">
						<label>Teto de custo do espaço (R$) *</label>
						<input
							type="number"
							min="0.01"
							step="0.01"
							className="form-input"
							value={tetoCusto}
							onChange={e => setTetoCusto(e.target.value)}
						/>
					</div>
					<div className="form-group" style={{ alignSelf: 'end' }}>
						<button
							type="button"
							className="action-btn"
							disabled={analisando}
							onClick={() => void handleAnalisar()}
						>
							{analisando ? 'Analisando...' : 'Analisar locais'}
						</button>
					</div>
				</div>
				{analiseError && <span className="error-message">{analiseError}</span>}
				{candidatos.length === 0 && !analiseError && (
					<p className="contact-secondary" style={{ marginTop: '0.75rem', fontSize: '0.85rem' }}>
						Clique em <strong>Analisar locais</strong> para classificar cada espaço com base nos
						dados deste evento e no teto informado.
					</p>
				)}
			</div>

			{candidatos.length > 0 && (
				<div className="content-card evento-card-interactive">
					<h3 className="evento-section-title">Resultado da análise</h3>
					<p className="evento-section-subtitle">
						Comparando {candidatos.length} local(is) com {participantesLabel} participantes e teto de{' '}
						{parseFloat(tetoCusto || '0').toLocaleString('pt-BR', {
							style: 'currency',
							currency: 'BRL',
						})}
						.
					</p>
					<div className="table-container">
						<table className="data-table">
							<thead>
									<tr>
										<th>Local</th>
										<th>Classificação</th>
										<th>Custo vs teto</th>
										<th>Capacidade vs evento</th>
										<th>Agenda</th>
										<th>Principal</th>
										<th>Contingência</th>
									</tr>
							</thead>
							<tbody>
								{candidatos.map(c => {
									const isPrincipal = principalId === c.localId;
									const isContingencia = contingenciaIds.includes(c.localId);
									return (
										<tr
											key={c.localId}
											className={`evento-candidato-row${isPrincipal ? ' is-principal' : ''}${isContingencia ? ' is-contingencia' : ''}`}
										>
											<td>
												<strong>{c.nomeLocal}</strong>
											</td>
											<td>
												<span className={`badge ${classificacaoBadgeClass(c.classificacao)}`}>
													{CLASSIFICACAO_LABELS[c.classificacao] ?? c.classificacao}
												</span>
												<div className="contact-secondary" style={{ fontSize: '0.8rem', marginTop: '0.25rem' }}>
													{c.justificativa}
												</div>
											</td>
											<td>
												{c.custo.toLocaleString('pt-BR', {
													style: 'currency',
													currency: 'BRL',
												})}
												<div className="contact-secondary">
													Teto:{' '}
													{parseFloat(tetoCusto || '0').toLocaleString('pt-BR', {
														style: 'currency',
														currency: 'BRL',
													})}
												</div>
												{c.acimaDoTeto && (
													<div className="evento-status-warn">Acima do teto</div>
												)}
											</td>
											<td>
												{c.capacidade.toLocaleString('pt-BR')} lugares
												<div className="contact-secondary">
													Evento: {participantesLabel}
												</div>
												{!c.capacidadeOk && (
													<div className="evento-status-warn">Insuficiente</div>
												)}
											</td>
										<td>
											{c.agendaOk ? (
												<span className="evento-status-ok">OK</span>
											) : (
												<span className="evento-status-bad">Conflito</span>
											)}
										</td>
										<td>
											{c.podeSerPrincipal ? (
												<input
													type="radio"
													checked={principalId === c.localId}
													onChange={() => {
														setPrincipalId(c.localId);
														setContingenciaIds(prev => prev.filter(id => id !== c.localId));
													}}
												/>
											) : (
												<span style={{ color: '#9ca3af' }}>—</span>
											)}
										</td>
										<td>
											{c.localId !== principalId && (
												<input
													type="checkbox"
													checked={contingenciaIds.includes(c.localId)}
													onChange={() => toggleContingencia(c.localId)}
												/>
											)}
										</td>
									</tr>
									);
								})}
							</tbody>
						</table>
					</div>
				</div>
			)}

			{contingenciaIds.length > 0 && (
				<div className="content-card evento-card-interactive">
					<h3 className="evento-section-title">Ordem das contingências</h3>
					<ul className="parties-list">
						{contingenciaIds.map((id, i) => {
							const cand = candidatos.find(c => c.localId === id);
							return (
								<li key={id}>
									<span className="party-index">{i + 1}</span>
									{cand?.nomeLocal ?? id}
									<div style={{ marginLeft: 'auto', display: 'flex', gap: '0.35rem' }}>
										<button
											type="button"
											className="evento-order-btn"
											disabled={i === 0}
											onClick={() => moveContingencia(id, 'up')}
											aria-label="Mover para cima"
										>
											↑
										</button>
										<button
											type="button"
											className="evento-order-btn"
											disabled={i === contingenciaIds.length - 1}
											onClick={() => moveContingencia(id, 'down')}
											aria-label="Mover para baixo"
										>
											↓
										</button>
									</div>
								</li>
							);
						})}
					</ul>
				</div>
			)}

			{feedback && (
				<div className="error-message" style={{ padding: '0.75rem', marginBottom: '1rem' }}>
					{feedback}
				</div>
			)}

			<div className="evento-sticky-actions">
				<button type="button" className="btn-outline" onClick={onBack}>
					Cancelar
				</button>
				<button
					type="button"
					className="action-btn"
					disabled={salvando || !principalId}
					onClick={() => void handleSalvar()}
				>
					{salvando ? 'Salvando...' : 'Salvar alocação'}
				</button>
			</div>

			{candidatos.length === 0 && candidatosPrincipais.length === 0 && principalId && (
				<div className="content-card" style={{ marginTop: '1rem' }}>
					<p style={{ color: '#6b7280', fontSize: '0.9rem' }}>
						Local principal selecionado: <strong>{principalId}</strong>. Execute a análise para
						ver classificações ou salve diretamente se já conhece os locais.
					</p>
				</div>
			)}
		</div>
	);
}
