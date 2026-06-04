import { useMemo, useState } from 'react';
import { CONTRACT_EVENTS } from '../../../modules/planning/constants';
import { usePlanningData } from '../../../modules/planning/PlanningDataContext';
import type { Contrato, StatusContratoUi } from '../../../modules/planning/types';
import { ConfirmModal } from '../../../shared/components/ConfirmModal';

interface ContratosListPageProps {
	onCreate: () => void;
	onEdit: (id: string) => void;
	onView: (id: string) => void;
}

function StatusBadge({ status }: { status: StatusContratoUi }) {
	return (
		<span className={`badge ${status === 'ATIVO' ? 'disponivel' : 'inativo'}`}>
			{status === 'ATIVO' ? 'Ativo' : 'Encerrado'}
		</span>
	);
}

export function ContratosListPage({ onCreate, onEdit, onView }: ContratosListPageProps) {
	const { contratos, encerrarContrato } = usePlanningData();
	const [search, setSearch] = useState('');
	const [statusFilter, setStatusFilter] = useState<StatusContratoUi | 'TODOS'>('TODOS');
	const [toClose, setToClose] = useState<Contrato | null>(null);
	const [feedback, setFeedback] = useState<string | null>(null);

	const filtered = useMemo(() => {
		const q = search.toLowerCase().trim();
		return contratos.filter(c => {
			const matchSearch =
				!q ||
				c.id.toLowerCase().includes(q) ||
				c.tipo.toLowerCase().includes(q) ||
				c.objeto.toLowerCase().includes(q);
			const matchStatus = statusFilter === 'TODOS' || c.status === statusFilter;
			return matchSearch && matchStatus;
		});
	}, [contratos, search, statusFilter]);

	const totalValue = contratos.reduce((s, c) => s + c.valor, 0);
	const activeCount = contratos.filter(c => c.status === 'ATIVO').length;
	const closedCount = contratos.filter(c => c.status === 'ENCERRADO').length;
	const eventCount = new Set(contratos.map(c => c.eventoId)).size;

	const getEventName = (id: string) =>
		CONTRACT_EVENTS.find(e => e.id === id)?.name ?? id;

	const handleClose = () => {
		if (!toClose) return;
		const erro = encerrarContrato(toClose.id);
		setFeedback(erro);
		setToClose(null);
	};

	return (
		<div className="module-page">
			<div className="module-header">
				<div className="title-area">
					<h1>Gestão de Contratos</h1>
					<p>Cadastro, edição e encerramento de contratos vinculados a eventos</p>
				</div>
				<button type="button" className="action-btn" onClick={onCreate}>
					<svg width="18" height="18" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
						<line x1="12" y1="5" x2="12" y2="19" />
						<line x1="5" y1="12" x2="19" y2="12" />
					</svg>
					Novo Contrato
				</button>
			</div>

			{feedback && (
				<div className="alert-box yellow" style={{ marginBottom: '1rem' }}>
					<div className="alert-content">
						<p style={{ fontSize: '0.85rem', margin: 0 }}>{feedback}</p>
					</div>
				</div>
			)}

			<div className="stats-grid">
				<div className="stat-card">
					<div className="stat-info">
						<div className="stat-label">Contratos Ativos</div>
						<div className="stat-value">{activeCount}</div>
						<div className="stat-sub">{closedCount} encerrado(s)</div>
					</div>
					<div className="stat-icon ativos">
						<svg width="20" height="20" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
							<path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
							<polyline points="14 2 14 8 20 8" />
						</svg>
					</div>
				</div>
				<div className="stat-card">
					<div className="stat-info">
						<div className="stat-label">Valor Total Contratado</div>
						<div className="stat-value" style={{ fontSize: '1.65rem' }}>
							R$ {(totalValue / 1000).toFixed(1)}K
						</div>
					</div>
					<div className="stat-icon disponiveis">
						<svg width="20" height="20" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
							<line x1="12" y1="1" x2="12" y2="23" />
							<path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6" />
						</svg>
					</div>
				</div>
				<div className="stat-card">
					<div className="stat-info">
						<div className="stat-label">Eventos Cobertos</div>
						<div className="stat-value">{eventCount}</div>
					</div>
					<div className="stat-icon alocados">
						<svg width="20" height="20" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
							<rect x="3" y="4" width="18" height="18" rx="2" />
							<line x1="16" y1="2" x2="16" y2="6" />
							<line x1="8" y1="2" x2="8" y2="6" />
						</svg>
					</div>
				</div>
			</div>

			<div className="filter-panel">
				<input
					type="search"
					className="form-input filter-search"
					placeholder="Buscar por ID, tipo ou objeto..."
					value={search}
					onChange={e => setSearch(e.target.value)}
				/>
				<select
					className="form-select filter-select"
					value={statusFilter}
					onChange={e => setStatusFilter(e.target.value as StatusContratoUi | 'TODOS')}
				>
					<option value="TODOS">Todos os status</option>
					<option value="ATIVO">Ativo</option>
					<option value="ENCERRADO">Encerrado</option>
				</select>
			</div>

			<div className="content-card">
				{filtered.length === 0 ? (
					<div className="empty-state">
						<p className="empty-state-title">
							{search || statusFilter !== 'TODOS'
								? 'Nenhum contrato encontrado'
								: 'Nenhum contrato cadastrado'}
						</p>
						<p className="empty-state-text">
							{search || statusFilter !== 'TODOS'
								? 'Ajuste os filtros para ver resultados diferentes.'
								: 'Crie o primeiro contrato para começar.'}
						</p>
						{!search && statusFilter === 'TODOS' && (
							<button type="button" className="action-btn" onClick={onCreate}>
								Novo Contrato
							</button>
						)}
					</div>
				) : (
					<>
						<div className="table-container">
							<table className="data-table">
								<thead>
									<tr>
										<th>ID / Tipo</th>
										<th>Objeto</th>
										<th>Evento</th>
										<th>Valor</th>
										<th>Vigência</th>
										<th>Status</th>
										<th>Atualizado em</th>
										<th>Ações</th>
									</tr>
								</thead>
								<tbody>
									{filtered.map(contrato => (
										<tr key={contrato.id}>
											<td>
												<button
													type="button"
													className="link-button mono-text"
													onClick={() => onView(contrato.id)}
												>
													{contrato.id}
												</button>
												<div className="contact-secondary">{contrato.tipo}</div>
											</td>
											<td className="objeto-cell">{contrato.objeto}</td>
											<td>{getEventName(contrato.eventoId)}</td>
											<td>
												<strong>
													{contrato.valor.toLocaleString('pt-BR', {
														style: 'currency',
														currency: 'BRL',
													})}
												</strong>
											</td>
											<td className="date-cell">
												<span>
													{new Date(contrato.dataInicio + 'T00:00:00').toLocaleDateString('pt-BR')}
												</span>
												<span className="contact-secondary">
													até{' '}
													{new Date(contrato.dataFim + 'T00:00:00').toLocaleDateString('pt-BR')}
												</span>
											</td>
											<td>
												<StatusBadge status={contrato.status} />
											</td>
											<td className="date-cell">
												<span>{new Date(contrato.atualizadoEm).toLocaleDateString('pt-BR')}</span>
												<span className="contact-secondary">
													{new Date(contrato.atualizadoEm).toLocaleTimeString('pt-BR', {
														hour: '2-digit',
														minute: '2-digit',
													})}
												</span>
											</td>
											<td>
												<div className="row-actions">
													<button
														type="button"
														className="edit-link"
														onClick={() => onView(contrato.id)}
													>
														Ver
													</button>
													{contrato.status === 'ATIVO' && (
														<>
															<button
																type="button"
																className="edit-link"
																style={{ marginLeft: '0.75rem' }}
																onClick={() => onEdit(contrato.id)}
															>
																Editar
															</button>
															<button
																type="button"
																className="edit-link"
																style={{ marginLeft: '0.75rem', color: '#ef4444' }}
																onClick={() => setToClose(contrato)}
															>
																Encerrar
															</button>
														</>
													)}
												</div>
											</td>
										</tr>
									))}
								</tbody>
							</table>
						</div>
						<div className="table-footer">
							{filtered.length} de {contratos.length} contrato(s)
						</div>
					</>
				)}
			</div>

			{toClose && (
				<ConfirmModal
					title="Encerrar Contrato"
					description={
						<>
							<p>Você está prestes a encerrar o contrato:</p>
							<div className="highlight-box">
								<strong className="mono-text">{toClose.id}</strong>
								<span>{toClose.objeto}</span>
							</div>
							<p>Após o encerramento, o contrato não poderá mais ser editado.</p>
						</>
					}
					confirmLabel="Encerrar Contrato"
					onConfirm={handleClose}
					onCancel={() => setToClose(null)}
				/>
			)}
		</div>
	);
}
