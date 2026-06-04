import { useMemo, useState } from 'react';
import type { CategoriaServico } from '../../../modules/planning/constants';
import { CONTRACT_CATEGORIES } from '../../../modules/planning/constants';
import { usePlanningData } from '../../../modules/planning/PlanningDataContext';
import type { Fornecedor, StatusFornecedor } from '../../../modules/planning/types';
import { ConfirmModal } from '../../../shared/components/ConfirmModal';
import { IntegrationPendingBanner } from '../../../shared/components/IntegrationPendingBanner';

interface FornecedoresListPageProps {
	onCreate: () => void;
	onEdit: (id: string) => void;
}

function CategoryBadge({ category }: { category: CategoriaServico }) {
	return <span className={`category-badge category-${categorySlug(category)}`}>{category}</span>;
}

function categorySlug(category: string) {
	return category
		.normalize('NFD')
		.replace(/[\u0300-\u036f]/g, '')
		.replace(/[^a-zA-Z0-9]+/g, '-')
		.toLowerCase();
}

function StatusBadge({ status }: { status: StatusFornecedor }) {
	return (
		<span className={`badge ${status === 'ATIVO' ? 'disponivel' : 'inativo'}`}>
			{status === 'ATIVO' ? 'Ativo' : 'Inativo'}
		</span>
	);
}

export function FornecedoresListPage({ onCreate, onEdit }: FornecedoresListPageProps) {
	const {
		fornecedores,
		desativarFornecedor,
		loading,
		integrationPending,
		error,
	} = usePlanningData();

	const [search, setSearch] = useState('');
	const [statusFilter, setStatusFilter] = useState<StatusFornecedor | 'TODOS'>('TODOS');
	const [categoryFilter, setCategoryFilter] = useState<CategoriaServico | 'TODAS'>('TODAS');
	const [toDeactivate, setToDeactivate] = useState<Fornecedor | null>(null);
	const [feedback, setFeedback] = useState<string | null>(null);
	const [actionLoading, setActionLoading] = useState(false);

	const filtered = useMemo(() => {
		const q = search.toLowerCase().trim();
		return fornecedores.filter(f => {
			const matchSearch =
				!q ||
				f.nome.toLowerCase().includes(q) ||
				f.cnpj.includes(q) ||
				f.pessoaContato.toLowerCase().includes(q) ||
				f.email.toLowerCase().includes(q);
			const matchStatus = statusFilter === 'TODOS' || f.status === statusFilter;
			const matchCategory = categoryFilter === 'TODAS' || f.categoriaServico === categoryFilter;
			return matchSearch && matchStatus && matchCategory;
		});
	}, [fornecedores, search, statusFilter, categoryFilter]);

	const activeCount = fornecedores.filter(f => f.status === 'ATIVO').length;
	const inactiveCount = fornecedores.filter(f => f.status === 'INATIVO').length;
	const categoryCount = new Set(fornecedores.map(f => f.categoriaServico)).size;

	const handleDeactivate = async () => {
		if (!toDeactivate) return;
		setActionLoading(true);
		const erro = await desativarFornecedor(toDeactivate.id);
		setFeedback(erro);
		if (!erro) setFeedback(null);
		setToDeactivate(null);
		setActionLoading(false);
	};

	if (loading) {
		return (
			<div className="module-page">
				<p style={{ color: '#6b7280' }}>Carregando fornecedores...</p>
			</div>
		);
	}

	return (
		<div className="module-page">
			<div className="module-header">
				<div className="title-area">
					<h1>Gestão de Fornecedores</h1>
					<p>Cadastro, edição e gerenciamento de fornecedores vinculados a contratos</p>
				</div>
				<button type="button" className="action-btn" onClick={onCreate}>
					<svg width="18" height="18" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
						<line x1="12" y1="5" x2="12" y2="19" />
						<line x1="5" y1="12" x2="19" y2="12" />
					</svg>
					Novo Fornecedor
				</button>
			</div>

			{integrationPending && <IntegrationPendingBanner />}

			{error && (
				<div className="error-message" style={{ padding: '0.75rem', marginBottom: '1rem' }}>
					{error}
				</div>
			)}

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
						<div className="stat-label">Fornecedores Ativos</div>
						<div className="stat-value">{activeCount}</div>
						<div className="stat-sub">{inactiveCount} inativo(s)</div>
					</div>
					<div className="stat-icon ativos">
						<svg width="20" height="20" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
							<rect x="2" y="7" width="20" height="14" rx="2" />
							<path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16" />
						</svg>
					</div>
				</div>
				<div className="stat-card">
					<div className="stat-info">
						<div className="stat-label">Total Cadastrado</div>
						<div className="stat-value">{fornecedores.length}</div>
					</div>
					<div className="stat-icon disponiveis">
						<svg width="20" height="20" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
							<path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" />
						</svg>
					</div>
				</div>
				<div className="stat-card">
					<div className="stat-info">
						<div className="stat-label">Categorias Cobertas</div>
						<div className="stat-value">{categoryCount}</div>
						<div className="stat-sub">de {CONTRACT_CATEGORIES.length} categorias</div>
					</div>
					<div className="stat-icon alocados">
						<svg width="20" height="20" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
							<path d="M12 2v20M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6" />
						</svg>
					</div>
				</div>
			</div>

			<div className="filter-panel">
				<input
					type="search"
					className="form-input filter-search"
					placeholder="Buscar por nome, CNPJ, contato ou e-mail..."
					value={search}
					onChange={e => setSearch(e.target.value)}
				/>
				<select
					className="form-select filter-select"
					value={statusFilter}
					onChange={e => setStatusFilter(e.target.value as StatusFornecedor | 'TODOS')}
				>
					<option value="TODOS">Todos os status</option>
					<option value="ATIVO">Ativo</option>
					<option value="INATIVO">Inativo</option>
				</select>
				<select
					className="form-select filter-select"
					value={categoryFilter}
					onChange={e => setCategoryFilter(e.target.value as CategoriaServico | 'TODAS')}
				>
					<option value="TODAS">Todas as categorias</option>
					{CONTRACT_CATEGORIES.map(c => (
						<option key={c} value={c}>{c}</option>
					))}
				</select>
			</div>

			<div className="content-card">
				{filtered.length === 0 ? (
					<div className="empty-state">
						<p className="empty-state-title">
							{search || statusFilter !== 'TODOS' || categoryFilter !== 'TODAS'
								? 'Nenhum fornecedor encontrado'
								: 'Nenhum fornecedor cadastrado'}
						</p>
						<p className="empty-state-text">
							{search || statusFilter !== 'TODOS' || categoryFilter !== 'TODAS'
								? 'Ajuste os filtros para ver resultados diferentes.'
								: 'Cadastre o primeiro fornecedor para começar.'}
						</p>
						{!search && statusFilter === 'TODOS' && categoryFilter === 'TODAS' && (
							<button type="button" className="action-btn" onClick={onCreate}>
								Novo Fornecedor
							</button>
						)}
					</div>
				) : (
					<>
						<div className="table-container">
							<table className="data-table">
								<thead>
									<tr>
										<th>Fornecedor</th>
										<th>Categoria</th>
										<th>Contato</th>
										<th>Status</th>
										<th>Atualizado em</th>
										<th>Ações</th>
									</tr>
								</thead>
								<tbody>
									{filtered.map(fornecedor => (
										<tr key={fornecedor.id} style={{ opacity: fornecedor.status === 'ATIVO' ? 1 : 0.65 }}>
											<td className="member-info">
												<div className="member-name">{fornecedor.nome}</div>
												<div className="member-email mono-text">{fornecedor.cnpj}</div>
											</td>
											<td>
												<CategoryBadge category={fornecedor.categoriaServico} />
											</td>
											<td>
												<div className="contact-stack">
													<span>{fornecedor.pessoaContato}</span>
													<span className="contact-secondary">{fornecedor.email}</span>
													<span className="contact-secondary">{fornecedor.telefone}</span>
												</div>
											</td>
											<td>
												<StatusBadge status={fornecedor.status} />
											</td>
											<td className="date-cell">
												<span>{new Date(fornecedor.atualizadoEm).toLocaleDateString('pt-BR')}</span>
												<span className="contact-secondary">
													{new Date(fornecedor.atualizadoEm).toLocaleTimeString('pt-BR', {
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
														onClick={() => onEdit(fornecedor.id)}
													>
														Editar
													</button>
													{fornecedor.status === 'ATIVO' && (
														<button
															type="button"
															className="edit-link"
															style={{ marginLeft: '0.75rem', color: '#ea580c' }}
															onClick={() => setToDeactivate(fornecedor)}
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
						</div>
						<div className="table-footer">
							{filtered.length} de {fornecedores.length} fornecedor(es)
						</div>
					</>
				)}
			</div>

			{toDeactivate && (
				<ConfirmModal
					title="Desativar Fornecedor"
					description={
						<>
							<p>Você está prestes a desativar o fornecedor:</p>
							<div className="highlight-box">
								<strong>{toDeactivate.nome}</strong>
								<span className="mono-text">{toDeactivate.cnpj}</span>
							</div>
							<p>Fornecedores inativos não podem ser vinculados a novos contratos.</p>
						</>
					}
					confirmLabel={actionLoading ? 'Desativando...' : 'Desativar Fornecedor'}
					onConfirm={() => void handleDeactivate()}
					onCancel={() => setToDeactivate(null)}
				/>
			)}
		</div>
	);
}
