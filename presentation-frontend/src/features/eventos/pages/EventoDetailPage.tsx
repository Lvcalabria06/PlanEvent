import { useEffect, useState } from 'react';
import {
	PORTE_EVENTO_OPTIONS,
	MOTIVO_ALERTA_LABELS,
	STATUS_ALOCACAO_LABELS,
	TIPO_EVENTO_OPTIONS,
} from '../../../modules/planning/eventos/constants';
import type { AlertaRiscoAlocacaoDto, EventoDto } from '../../../modules/planning/eventos/dto';
import { formatPeriodo } from '../../../modules/planning/eventos/mappers';
import {
	buscarOrcamentoApi,
	listarCategoriasOrcamentoApi,
	type CategoriaOrcamentoDto,
	type OrcamentoEventoDto,
} from '../../../modules/planning/eventos/orcamentoApi';
import { CATEGORIAS_ORCAMENTO } from '../../../modules/planning/eventos/constants';
import { ConfirmModal } from '../../../shared/components/ConfirmModal';
import { useEventos } from '../EventosContext';

interface EventoDetailPageProps {
	evento: EventoDto;
	onBack: () => void;
	onEdit: () => void;
	onPlanejarLocal: () => void;
}

function tipoLabel(tipo: string) {
	return TIPO_EVENTO_OPTIONS.find(t => t.value === tipo)?.label ?? tipo;
}

function porteLabel(porte: string) {
	return PORTE_EVENTO_OPTIONS.find(p => p.value === porte)?.label ?? porte;
}

function formatMoeda(value: number) {
	return value.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
}

export function EventoDetailPage({
	evento: initial,
	onBack,
	onEdit,
	onPlanejarLocal,
}: EventoDetailPageProps) {
	const {
		obterEvento,
		buscarEvento,
		confirmarPreparacao,
		avaliarRisco,
		trocarLocal,
	} = useEventos();
	const evento = obterEvento(initial.id) ?? initial;

	const [showConfirmModal, setShowConfirmModal] = useState(false);
	const [confirmLoading, setConfirmLoading] = useState(false);
	const [confirmError, setConfirmError] = useState<string | null>(null);

	const [risco, setRisco] = useState<AlertaRiscoAlocacaoDto | null>(null);
	const [riscoLoading, setRiscoLoading] = useState(false);
	const [riscoChecked, setRiscoChecked] = useState(false);
	const [riscoError, setRiscoError] = useState<string | null>(null);

	const [trocaLocalId, setTrocaLocalId] = useState('');
	const [trocaMotivo, setTrocaMotivo] = useState('');
	const trocaUsuario = 'usuario-demo';
	const [trocaLoading, setTrocaLoading] = useState(false);
	const [trocaFeedback, setTrocaFeedback] = useState<string | null>(null);

	const [orcamento, setOrcamento] = useState<OrcamentoEventoDto | null>(null);
	const [categoriasOrcamento, setCategoriasOrcamento] = useState<CategoriaOrcamentoDto[]>([]);

	useEffect(() => {
		void buscarEvento(evento.id);
	}, [evento.id, buscarEvento]);

	useEffect(() => {
		let cancelled = false;
		void (async () => {
			const orc = await buscarOrcamentoApi(evento.id);
			if (cancelled) return;
			if (!orc) {
				setOrcamento(null);
				setCategoriasOrcamento([]);
				return;
			}
			const cats = await listarCategoriasOrcamentoApi(evento.id);
			if (!cancelled) {
				setOrcamento(orc);
				setCategoriasOrcamento(cats);
			}
		})();
		return () => {
			cancelled = true;
		};
	}, [evento.id]);

	const podeConfirmar =
		evento.localId && !evento.planejamentoConfirmado && !evento.concluido;
	const podeEditar = !evento.planejamentoConfirmado && !evento.concluido;
	const podeTrocar =
		evento.planejamentoConfirmado &&
		!evento.concluido &&
		evento.locaisContingenciaOrdenados.length > 0;

	const custoAcimaDoTeto =
		evento.custoLocalPrincipal != null &&
		evento.tetoCustoEspacoInformado != null &&
		evento.custoLocalPrincipal > evento.tetoCustoEspacoInformado;

	const capacidadeInsuficiente =
		evento.capacidadeLocalPrincipal != null &&
		evento.quantidadeEstimadaParticipantes > evento.capacidadeLocalPrincipal;

	const handleConfirmar = async () => {
		setConfirmLoading(true);
		setConfirmError(null);
		const erro = await confirmarPreparacao(evento.id);
		if (erro) setConfirmError(erro);
		setShowConfirmModal(false);
		setConfirmLoading(false);
	};

	const handleAvaliarRisco = async () => {
		setRiscoLoading(true);
		setRiscoError(null);
		try {
			const alerta = await avaliarRisco(evento.id);
			setRisco(alerta);
			setRiscoChecked(true);
		} catch (err) {
			setRisco(null);
			setRiscoChecked(true);
			setRiscoError(err instanceof Error ? err.message : 'Erro ao avaliar risco do local.');
		} finally {
			setRiscoLoading(false);
		}
	};

	const handleTroca = async () => {
		if (!trocaLocalId || !trocaMotivo.trim()) {
			setTrocaFeedback('Selecione um local de contingência e informe o motivo.');
			return;
		}
		setTrocaLoading(true);
		setTrocaFeedback(null);
		const erro = await trocarLocal(evento.id, trocaLocalId, trocaUsuario, trocaMotivo.trim());
		if (erro) {
			setTrocaFeedback(erro);
		} else {
			setTrocaMotivo('');
			setTrocaLocalId('');
			setTrocaFeedback('Local alterado com sucesso.');
			setRiscoChecked(false);
			setRisco(null);
		}
		setTrocaLoading(false);
	};

	return (
		<div className="module-page module-page-narrow">
			<button type="button" className="back-link" onClick={onBack}>
				<svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
					<line x1="19" y1="12" x2="5" y2="12" />
					<polyline points="12 19 5 12 12 5" />
				</svg>
				Voltar para Eventos
			</button>

			{confirmError && (
				<div className="error-message" style={{ padding: '0.75rem', marginBottom: '1rem' }}>
					{confirmError}
				</div>
			)}

			<div className="content-card evento-card-interactive">
				<div className="detail-header">
					<div>
						<h2 className="evento-form-title" style={{ fontSize: '1.35rem' }}>{evento.nome}</h2>
						<p className="mono-text contact-secondary" style={{ margin: '0.25rem 0' }}>
							{evento.id}
						</p>
						<span
							className={`badge ${
								evento.concluido
									? 'inativo'
									: evento.planejamentoConfirmado
										? 'disponivel'
										: evento.localId
											? 'alocados'
											: 'pendente'
							}`}
						>
							{STATUS_ALOCACAO_LABELS[evento.statusAlocacao] ?? evento.statusAlocacao}
						</span>
					</div>
					<div className="detail-actions">
						{podeEditar && (
							<button type="button" className="btn-outline" onClick={onEdit}>
								Editar
							</button>
						)}
						{evento.podePlanejarLocal && (
							<button type="button" className="action-btn" onClick={onPlanejarLocal}>
								Planejar local
							</button>
						)}
						{podeConfirmar && (
							<button
								type="button"
								className="action-btn"
								onClick={() => setShowConfirmModal(true)}
							>
								Confirmar preparação
							</button>
						)}
					</div>
				</div>

				<div className="detail-grid">
					<div className="detail-field">
						<span className="detail-label">Tipo / Porte</span>
						<p>{tipoLabel(evento.tipo)} · {porteLabel(evento.porte)}</p>
					</div>
					<div className="detail-field">
						<span className="detail-label">Período</span>
						<p>{formatPeriodo(evento.dataInicio, evento.dataFim)}</p>
					</div>
					<div className="detail-field">
						<span className="detail-label">Participantes estimados</span>
						<p>{evento.quantidadeEstimadaParticipantes}</p>
					</div>
					<div className="detail-field">
						<span className="detail-label">Objetivo</span>
						<p>{evento.objetivo}</p>
					</div>
					{evento.requisitosInfraestrutura && (
						<div className="detail-field full-width">
							<span className="detail-label">Requisitos de infraestrutura</span>
							<p>{evento.requisitosInfraestrutura}</p>
						</div>
					)}
					<div className="detail-field">
						<span className="detail-label">Local principal</span>
						<p>{evento.nomeLocalPrincipal ?? 'Não definido'}</p>
						{evento.localId && evento.custoLocalPrincipal != null && (
							<p className="contact-secondary" style={{ marginTop: '0.25rem' }}>
								Custo do espaço: <strong>{formatMoeda(evento.custoLocalPrincipal)}</strong>
							</p>
						)}
						{evento.localId && evento.capacidadeLocalPrincipal != null && (
							<p className="contact-secondary" style={{ margin: '0.15rem 0 0' }}>
								Capacidade: {evento.capacidadeLocalPrincipal.toLocaleString('pt-BR')} lugares
							</p>
						)}
						{evento.localId && (
							<span className="mono-text contact-secondary">{evento.localId}</span>
						)}
					</div>
					<div className="detail-field">
						<span className="detail-label">Teto de custo do espaço</span>
						<p>
							{evento.tetoCustoEspacoInformado != null
								? formatMoeda(evento.tetoCustoEspacoInformado)
								: '—'}
						</p>
						<p className="contact-secondary" style={{ marginTop: '0.25rem', fontSize: '0.8rem' }}>
							Limite para alocação de local (Planejar local), não é o orçamento financeiro.
						</p>
						{custoAcimaDoTeto && (
							<p className="evento-status-warn" style={{ marginTop: '0.35rem' }}>
								Custo do local acima do teto informado no planejamento.
							</p>
						)}
					</div>
					<div className="detail-field full-width">
						<span className="detail-label">Orçamento previsto do evento</span>
						{orcamento ? (
							<>
								<p>
									Total: <strong>{formatMoeda(orcamento.valorTotal)}</strong>
								</p>
								{categoriasOrcamento.length > 0 && (
									<ul className="evento-orcamento-resumo-list">
										{categoriasOrcamento.map(cat => (
											<li key={cat.id}>
												{CATEGORIAS_ORCAMENTO.find(c => c.value === cat.categoria)?.label ??
													cat.categoria}
												: {formatMoeda(cat.valorPrevisto)}
											</li>
										))}
									</ul>
								)}
							</>
						) : (
							<p className="contact-secondary">
								Não cadastrado. Edite o evento para definir o orçamento previsto.
							</p>
						)}
					</div>
					{evento.localId && capacidadeInsuficiente && (
						<div className="detail-field full-width">
							<div className="alert-box yellow" style={{ margin: 0 }}>
								<div className="alert-content">
									<p style={{ fontSize: '0.85rem', margin: 0 }}>
										Capacidade do local ({evento.capacidadeLocalPrincipal?.toLocaleString('pt-BR')})
										é menor que os participantes estimados (
										{evento.quantidadeEstimadaParticipantes.toLocaleString('pt-BR')}).
										Considere avaliar o risco ou trocar o local.
									</p>
								</div>
							</div>
						</div>
					)}
					{evento.nomesLocaisContingencia.length > 0 && (
						<div className="detail-field full-width">
							<span className="detail-label">Locais de contingência (ordem)</span>
							<ul className="parties-list">
								{evento.nomesLocaisContingencia.map((nome, i) => (
									<li key={i}>
										<span className="party-index">{i + 1}</span>
										{nome}
									</li>
								))}
							</ul>
						</div>
					)}
				</div>
			</div>

			{evento.planejamentoConfirmado && !evento.concluido && (
				<div className="content-card evento-card-interactive">
					<h3 className="evento-section-title">Avaliação de risco</h3>
					<p className="evento-section-subtitle">
						Após confirmar a preparação, avalie se o local principal apresenta risco operacional.
					</p>
					<button
						type="button"
						className="btn-outline"
						disabled={riscoLoading}
						onClick={() => void handleAvaliarRisco()}
					>
						{riscoLoading ? 'Avaliando...' : 'Avaliar risco do local'}
					</button>
					{riscoError && (
						<div className="error-message" style={{ padding: '0.75rem', marginTop: '1rem' }}>
							{riscoError}
						</div>
					)}
					{riscoChecked && !risco && !riscoError && (
						<div className="alert-box blue" style={{ marginTop: '1rem' }}>
							<div className="alert-content">
								<p style={{ fontSize: '0.85rem', margin: 0 }}>
									Nenhum alerta de risco identificado para o local principal.
								</p>
							</div>
						</div>
					)}
					{risco && (
						<div className="alert-box yellow" style={{ marginTop: '1rem' }}>
							<div className="alert-content">
								<h4>Alerta de risco</h4>
								<p style={{ fontSize: '0.85rem' }}>{risco.descricao}</p>
								{risco.motivos.length > 0 && (
									<ul>
										{risco.motivos.map((m, i) => (
											<li key={i}>{MOTIVO_ALERTA_LABELS[m] ?? m}</li>
										))}
									</ul>
								)}
								{risco.melhorSubstitutoSugeridoId && (
									<p style={{ fontSize: '0.85rem', margin: 0 }}>
										Substituto sugerido:{' '}
										<strong>
											{risco.melhorSubstitutoSugeridoNome ?? risco.melhorSubstitutoSugeridoId}
										</strong>
									</p>
								)}
							</div>
						</div>
					)}
				</div>
			)}

			{podeTrocar && (
				<div className="content-card evento-card-interactive">
					<h3 className="evento-section-title">Troca de local (contingência)</h3>
					<p className="evento-section-subtitle">
						Substitua o local principal por um dos locais de contingência cadastrados.
					</p>
					{trocaFeedback && (
						<div
							className={`alert-box ${trocaFeedback.includes('sucesso') ? 'blue' : 'yellow'}`}
							style={{ marginBottom: '1rem' }}
						>
							<div className="alert-content">
								<p style={{ fontSize: '0.85rem', margin: 0 }}>{trocaFeedback}</p>
							</div>
						</div>
					)}
					<div className="form-grid">
						<div className="form-group full-width">
							<label>Novo local (contingência)</label>
							<select
								className="form-select"
								value={trocaLocalId}
								onChange={e => setTrocaLocalId(e.target.value)}
							>
								<option value="">— Selecione —</option>
								{evento.locaisContingenciaOrdenados.map((localId, i) => (
									<option key={localId} value={localId}>
										{i + 1}. {evento.nomesLocaisContingencia[i] ?? localId}
									</option>
								))}
							</select>
						</div>
						<div className="form-group full-width">
							<label>Motivo da troca *</label>
							<textarea
								className="form-input"
								rows={2}
								value={trocaMotivo}
								onChange={e => setTrocaMotivo(e.target.value)}
								style={{ resize: 'vertical' }}
							/>
						</div>
					</div>
					<div className="form-actions">
						<button
							type="button"
							className="action-btn"
							disabled={trocaLoading}
							onClick={() => void handleTroca()}
						>
							{trocaLoading ? 'Processando...' : 'Registrar troca'}
						</button>
					</div>
				</div>
			)}

			{evento.historicoTrocasLocal.length > 0 && (
				<div className="content-card evento-card-interactive">
					<h3 className="evento-section-title">Histórico de trocas de local</h3>
					<div className="table-container">
						<table className="data-table">
							<thead>
								<tr>
									<th>Data</th>
									<th>De</th>
									<th>Para</th>
									<th>Motivo</th>
								</tr>
							</thead>
							<tbody>
								{evento.historicoTrocasLocal.map((t, i) => (
									<tr key={i}>
										<td className="date-cell">
											{new Date(t.dataHora).toLocaleString('pt-BR')}
										</td>
										<td>{t.localAnteriorNome ?? t.localAnteriorId}</td>
										<td>{t.localNovoNome ?? t.localNovoId}</td>
										<td>{t.motivo}</td>
									</tr>
								))}
							</tbody>
						</table>
					</div>
				</div>
			)}

			{showConfirmModal && (
				<ConfirmModal
					title="Confirmar preparação inicial"
					description={
						<>
							<p>
								Confirma a preparação do evento com o local principal{' '}
								<strong>{evento.nomeLocalPrincipal}</strong>?
							</p>
							<p style={{ fontSize: '0.85rem', color: '#6b7280' }}>
								Após confirmar, o planejamento de local não poderá ser alterado nesta etapa.
								Período: {formatPeriodo(evento.dataInicio, evento.dataFim)}.
							</p>
						</>
					}
					confirmLabel={confirmLoading ? 'Confirmando...' : 'Confirmar preparação'}
					onConfirm={() => void handleConfirmar()}
					onCancel={() => setShowConfirmModal(false)}
				/>
			)}
		</div>
	);
}
