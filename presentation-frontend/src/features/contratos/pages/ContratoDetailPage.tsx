import { useState } from 'react';
import { CONTRACT_EVENTS } from '../../../modules/planning/constants';
import { usePlanningData } from '../../../modules/planning/PlanningDataContext';
import type { Contrato } from '../../../modules/planning/types';
import { ConfirmModal } from '../../../shared/components/ConfirmModal';

interface ContratoDetailPageProps {
	contrato: Contrato;
	onBack: () => void;
	onEdit: () => void;
}

export function ContratoDetailPage({ contrato: initial, onBack, onEdit }: ContratoDetailPageProps) {
	const { encerrarContrato, obterContrato } = usePlanningData();
	const contrato = obterContrato(initial.id) ?? initial;
	const [showCloseModal, setShowCloseModal] = useState(false);
	const [feedback, setFeedback] = useState<string | null>(null);

	const eventName =
		CONTRACT_EVENTS.find(e => e.id === contrato.eventoId)?.name ?? contrato.eventoId;

	const handleClose = () => {
		const erro = encerrarContrato(contrato.id);
		if (erro) {
			setFeedback(erro);
		} else {
			setFeedback(null);
		}
		setShowCloseModal(false);
	};

	return (
		<div className="module-page module-page-narrow">
			<button type="button" className="back-link" onClick={onBack}>
				<svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
					<line x1="19" y1="12" x2="5" y2="12" />
					<polyline points="12 19 5 12 12 5" />
				</svg>
				Voltar para Contratos
			</button>

			{feedback && (
				<div className="alert-box yellow" style={{ marginBottom: '1rem' }}>
					<div className="alert-content">
						<p style={{ fontSize: '0.85rem', margin: 0 }}>{feedback}</p>
					</div>
				</div>
			)}

			<div className="content-card" style={{ marginBottom: '1rem' }}>
				<div className="detail-header">
					<div>
						<p className="mono-text detail-id">{contrato.id}</p>
						<p style={{ color: '#6b7280', margin: '0.25rem 0 0.5rem' }}>{contrato.tipo}</p>
						<span className={`badge ${contrato.status === 'ATIVO' ? 'disponivel' : 'inativo'}`}>
							{contrato.status === 'ATIVO' ? 'Ativo' : 'Encerrado'}
						</span>
					</div>
					{contrato.status === 'ATIVO' && (
						<div className="detail-actions">
							<button type="button" className="btn-outline" onClick={onEdit}>
								Editar
							</button>
							<button
								type="button"
								className="btn-outline"
								style={{ color: '#ef4444', borderColor: '#fecaca' }}
								onClick={() => setShowCloseModal(true)}
							>
								Encerrar
							</button>
						</div>
					)}
				</div>

				<div className="detail-grid">
					<div className="detail-field">
						<span className="detail-label">Objeto</span>
						<p>{contrato.objeto}</p>
					</div>
					<div className="detail-field">
						<span className="detail-label">Categoria</span>
						<p>{contrato.categoria}</p>
					</div>
					<div className="detail-field">
						<span className="detail-label">Partes</span>
						<ul className="parties-list">
							{contrato.partes.map((parte, i) => (
								<li key={i}>
									<span className="party-index">{i + 1}</span>
									{parte}
								</li>
							))}
						</ul>
					</div>
					<div className="detail-field">
						<span className="detail-label">Evento</span>
						<p>{eventName}</p>
						<span className="mono-text contact-secondary">{contrato.eventoId}</span>
					</div>
					<div className="detail-field">
						<span className="detail-label">Valor Global</span>
						<p style={{ fontWeight: 700 }}>
							{contrato.valor.toLocaleString('pt-BR', {
								style: 'currency',
								currency: 'BRL',
							})}
						</p>
					</div>
					<div className="detail-field">
						<span className="detail-label">Vigência</span>
						<p>
							{new Date(contrato.dataInicio + 'T00:00:00').toLocaleDateString('pt-BR', {
								day: '2-digit',
								month: 'long',
								year: 'numeric',
							})}
						</p>
						<span className="contact-secondary">
							até{' '}
							{new Date(contrato.dataFim + 'T00:00:00').toLocaleDateString('pt-BR', {
								day: '2-digit',
								month: 'long',
								year: 'numeric',
							})}
						</span>
					</div>
				</div>

				<div className="metadata-block" style={{ marginTop: '1rem' }}>
					<div className="metadata-item">
						<div className="meta-label">Criado em</div>
						<div className="meta-value">
							{new Date(contrato.criadoEm).toLocaleString('pt-BR')}
						</div>
					</div>
					<div className="metadata-item">
						<div className="meta-label">Atualizado em</div>
						<div className="meta-value">
							{new Date(contrato.atualizadoEm).toLocaleString('pt-BR')}
						</div>
					</div>
				</div>
			</div>

			<div className="content-card">
				<h3 className="widget-title">Histórico de Status</h3>
				<div className="status-timeline">
					{[...contrato.historicoStatus].reverse().map((entry, i) => (
						<div key={i} className="status-timeline-item">
							<span
								className={`badge ${entry.status === 'ATIVO' ? 'disponivel' : 'inativo'}`}
							>
								{entry.status === 'ATIVO' ? 'Ativo' : 'Encerrado'}
							</span>
							<span className="contact-secondary">
								{new Date(entry.data).toLocaleString('pt-BR')}
							</span>
						</div>
					))}
				</div>
			</div>

			{showCloseModal && (
				<ConfirmModal
					title="Encerrar Contrato"
					description={
						<>
							<p>
								O contrato <strong className="mono-text">{contrato.id}</strong> será encerrado.
							</p>
							<ul style={{ fontSize: '0.85rem', color: '#4b5563', paddingLeft: '1.25rem' }}>
								<li>Não poderá mais ser editado</li>
								<li>Ficará indisponível para novas conciliações</li>
								<li>O histórico permanecerá registrado</li>
							</ul>
						</>
					}
					confirmLabel="Encerrar Contrato"
					onConfirm={handleClose}
					onCancel={() => setShowCloseModal(false)}
				/>
			)}
		</div>
	);
}
