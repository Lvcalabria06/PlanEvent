import { useMemo, useState } from 'react';
import {
	PORTE_EVENTO_OPTIONS,
	STATUS_ALOCACAO_LABELS,
	TIPO_EVENTO_OPTIONS,
} from '../../../modules/planning/eventos/constants';
import { formatPeriodo } from '../../../modules/planning/eventos/mappers';
import type { EventoDto } from '../../../modules/planning/eventos/dto';
import { useEventos } from '../EventosContext';

interface EventosListPageProps {
	onCreate: () => void;
	onEdit: (id: string) => void;
	onView: (id: string) => void;
	onPlanejarLocal: (id: string) => void;
}

function tipoLabel(tipo: string) {
	return TIPO_EVENTO_OPTIONS.find(t => t.value === tipo)?.label ?? tipo;
}

function porteLabel(porte: string) {
	return PORTE_EVENTO_OPTIONS.find(p => p.value === porte)?.label ?? porte;
}

function formatParticipantes(qtd: number) {
	return qtd.toLocaleString('pt-BR');
}

function StatusBadge({ evento }: { evento: EventoDto }) {
	if (evento.concluido) {
		return <span className="badge inativo">Concluído</span>;
	}
	if (evento.planejamentoConfirmado) {
		return <span className="badge disponivel">Preparação confirmada</span>;
	}
	if (evento.localId) {
		return <span className="badge alocados">Local definido</span>;
	}
	return <span className="badge pendente">Pendente</span>;
}

export function EventosListPage({
	onCreate,
	onEdit,
	onView,
	onPlanejarLocal,
}: EventosListPageProps) {
	const { eventos, loading, error } = useEventos();
	const [search, setSearch] = useState('');
	const [statusFilter, setStatusFilter] = useState<'TODOS' | 'PENDENTE' | 'LOCAL' | 'CONFIRMADO'>(
		'TODOS',
	);

	const filtered = useMemo(() => {
		const q = search.toLowerCase().trim();
		return eventos.filter(e => {
			const matchSearch =
				!q ||
				e.id.toLowerCase().includes(q) ||
				e.nome.toLowerCase().includes(q) ||
				(e.nomeLocalPrincipal?.toLowerCase().includes(q) ?? false);
			const matchStatus =
				statusFilter === 'TODOS' ||
				(statusFilter === 'PENDENTE' && !e.localId && !e.concluido) ||
				(statusFilter === 'LOCAL' && e.localId && !e.planejamentoConfirmado) ||
				(statusFilter === 'CONFIRMADO' && e.planejamentoConfirmado && !e.concluido);
			return matchSearch && matchStatus;
		});
	}, [eventos, search, statusFilter]);

	const semLocal = eventos.filter(e => !e.localId && !e.concluido).length;
	const comLocal = eventos.filter(e => e.localId && !e.planejamentoConfirmado).length;
	const confirmados = eventos.filter(e => e.planejamentoConfirmado && !e.concluido).length;

	if (loading) {
		return (
			<div className="module-page">
				<div className="eventos-loading">
					<div className="eventos-skeleton eventos-skeleton-header" />
					<div className="eventos-skeleton-stats">
						<div className="eventos-skeleton eventos-skeleton-stat" />
						<div className="eventos-skeleton eventos-skeleton-stat" />
						<div className="eventos-skeleton eventos-skeleton-stat" />
					</div>
					<div className="eventos-skeleton eventos-skeleton-table" />
				</div>
			</div>
		);
	}

	return (
		<div className="module-page">
			<div className="module-header">
				<div className="title-area">
					<h1>Gestão de Eventos</h1>
					<p>Cadastro, planejamento de local e confirmação da preparação inicial</p>
				</div>
				<button type="button" className="action-btn" onClick={onCreate}>
					<svg width="18" height="18" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
						<line x1="12" y1="5" x2="12" y2="19" />
						<line x1="5" y1="12" x2="19" y2="12" />
					</svg>
					Novo Evento
				</button>
			</div>

			{error && (
				<div className="error-message" style={{ padding: '0.75rem', marginBottom: '1rem' }}>
					{error}
				</div>
			)}

			<div className="alert-box blue" style={{ marginBottom: '1rem' }}>
				<div className="alert-content">
					<p style={{ fontSize: '0.85rem', margin: 0 }}>
						Fluxo recomendado: cadastrar evento → planejar local (antes de confirmar) → confirmar
						preparação → avaliar risco e trocar local se necessário.
					</p>
				</div>
			</div>

			<div className="eventos-flow" aria-label="Fluxo do evento">
				<div className="eventos-flow-step active">
					<span className="step-num">1</span>
					Cadastrar
				</div>
				<span className="eventos-flow-arrow" aria-hidden="true">→</span>
				<div className="eventos-flow-step active">
					<span className="step-num">2</span>
					Planejar local
				</div>
				<span className="eventos-flow-arrow" aria-hidden="true">→</span>
				<div className="eventos-flow-step active">
					<span className="step-num">3</span>
					Confirmar preparação
				</div>
				<span className="eventos-flow-arrow" aria-hidden="true">→</span>
				<div className="eventos-flow-step active">
					<span className="step-num">4</span>
					Operar / contingência
				</div>
			</div>

			<div className="stats-grid">
				<button
					type="button"
					className={`stat-card evento-stat-clickable${statusFilter === 'TODOS' ? ' evento-stat-active' : ''}`}
					onClick={() => setStatusFilter('TODOS')}
					aria-pressed={statusFilter === 'TODOS'}
				>
					<div className="stat-info">
						<div className="stat-label">Total de Eventos</div>
						<div className="stat-value">{eventos.length}</div>
					</div>
					<div className="stat-icon ativos">
						<svg width="20" height="20" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
							<rect x="3" y="4" width="18" height="18" rx="2" />
							<line x1="16" y1="2" x2="16" y2="6" />
							<line x1="8" y1="2" x2="8" y2="6" />
						</svg>
					</div>
				</button>
				<button
					type="button"
					className={`stat-card evento-stat-clickable${statusFilter === 'PENDENTE' ? ' evento-stat-active' : ''}`}
					onClick={() => setStatusFilter('PENDENTE')}
					aria-pressed={statusFilter === 'PENDENTE'}
				>
					<div className="stat-info">
						<div className="stat-label">Sem local</div>
						<div className="stat-value">{semLocal}</div>
						<div className="stat-sub">aguardando planejamento</div>
					</div>
					<div className="stat-icon pendente">
						<svg width="20" height="20" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
							<circle cx="12" cy="12" r="10" />
							<line x1="12" y1="8" x2="12" y2="12" />
						</svg>
					</div>
				</button>
				<button
					type="button"
					className={`stat-card evento-stat-clickable${statusFilter === 'LOCAL' || statusFilter === 'CONFIRMADO' ? ' evento-stat-active' : ''}`}
					onClick={() => setStatusFilter('LOCAL')}
					aria-pressed={statusFilter === 'LOCAL' || statusFilter === 'CONFIRMADO'}
				>
					<div className="stat-info">
						<div className="stat-label">Local definido</div>
						<div className="stat-value">{comLocal}</div>
						<div className="stat-sub">{confirmados} confirmado(s)</div>
					</div>
					<div className="stat-icon alocados">
						<svg width="20" height="20" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
							<path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z" />
							<circle cx="12" cy="10" r="3" />
						</svg>
					</div>
				</button>
			</div>

			<div className="filter-panel">
				<input
					type="search"
					className="form-input filter-search"
					placeholder="Buscar por nome ou local..."
					value={search}
					onChange={e => setSearch(e.target.value)}
				/>
				<select
					className="form-select filter-select"
					value={statusFilter}
					onChange={e =>
						setStatusFilter(e.target.value as 'TODOS' | 'PENDENTE' | 'LOCAL' | 'CONFIRMADO')
					}
				>
					<option value="TODOS">Todos os status</option>
					<option value="PENDENTE">Sem local</option>
					<option value="LOCAL">Local definido (não confirmado)</option>
					<option value="CONFIRMADO">Preparação confirmada</option>
				</select>
			</div>

			<div className="content-card">
				{filtered.length === 0 ? (
					<div className="empty-state">
						<div className="empty-state-icon" aria-hidden="true">
							<svg width="28" height="28" fill="none" stroke="currentColor" strokeWidth="1.75" viewBox="0 0 24 24">
								<rect x="3" y="4" width="18" height="18" rx="2" />
								<line x1="16" y1="2" x2="16" y2="6" />
								<line x1="8" y1="2" x2="8" y2="6" />
								<line x1="3" y1="10" x2="21" y2="10" />
							</svg>
						</div>
						<p className="empty-state-title">
							{search || statusFilter !== 'TODOS'
								? 'Nenhum evento encontrado'
								: 'Nenhum evento cadastrado'}
						</p>
						<p className="empty-state-text">
							{search || statusFilter !== 'TODOS'
								? 'Ajuste os filtros para ver resultados diferentes.'
								: 'Crie o primeiro evento para começar o planejamento.'}
						</p>
						{!search && statusFilter === 'TODOS' && (
							<button type="button" className="action-btn" onClick={onCreate}>
								Novo Evento
							</button>
						)}
					</div>
				) : (
					<>
						<div className="table-container">
							<table className="data-table">
								<thead>
									<tr>
										<th>Evento</th>
										<th>Tipo / Porte</th>
										<th>Período</th>
										<th>Participantes</th>
										<th>Local</th>
										<th>Status</th>
										<th>Ações</th>
									</tr>
								</thead>
								<tbody>
									{filtered.map(evento => (
										<tr key={evento.id}>
											<td className="member-info">
												<button
													type="button"
													className="link-button evento-name-link"
													onClick={() => onView(evento.id)}
												>
													{evento.nome}
												</button>
											</td>
											<td>
												<span>{tipoLabel(evento.tipo)}</span>
												<div className="contact-secondary">{porteLabel(evento.porte)}</div>
											</td>
											<td className="date-cell">
												{formatPeriodo(evento.dataInicio, evento.dataFim)}
											</td>
											<td>{formatParticipantes(evento.quantidadeEstimadaParticipantes)}</td>
											<td>
												{evento.nomeLocalPrincipal ?? (
													<span className="contact-secondary">—</span>
												)}
											</td>
											<td>
												<StatusBadge evento={evento} />
												{evento.statusAlocacao !== 'SEM_LOCAL_DEFINIDO' && (
													<div className="contact-secondary" style={{ marginTop: '0.25rem' }}>
														{STATUS_ALOCACAO_LABELS[evento.statusAlocacao] ??
															evento.statusAlocacao}
													</div>
												)}
											</td>
											<td className="actions-cell">
												<div className="evento-actions-stack">
													{evento.podePlanejarLocal && (
														<button
															type="button"
															className="table-cta-btn"
															onClick={() => onPlanejarLocal(evento.id)}
															title="Abrir planejamento de alocação de local"
														>
															<svg
																width="14"
																height="14"
																fill="none"
																stroke="currentColor"
																strokeWidth="2"
																viewBox="0 0 24 24"
																aria-hidden="true"
															>
																<path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z" />
																<circle cx="12" cy="10" r="3" />
															</svg>
															Planejar local
														</button>
													)}
													<div className="row-actions-secondary">
														<button
															type="button"
															className="edit-link"
															onClick={() => onView(evento.id)}
														>
															Ver detalhes
														</button>
														{!evento.planejamentoConfirmado && !evento.concluido && (
															<>
																<span className="action-sep" aria-hidden="true">·</span>
																<button
																	type="button"
																	className="edit-link"
																	onClick={() => onEdit(evento.id)}
																>
																	Editar
																</button>
															</>
														)}
													</div>
												</div>
											</td>
										</tr>
									))}
								</tbody>
							</table>
						</div>
						<div className="table-footer">
							{filtered.length} de {eventos.length} evento(s)
						</div>
					</>
				)}
			</div>
		</div>
	);
}
