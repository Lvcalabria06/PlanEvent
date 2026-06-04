import { useState, type FormEvent } from 'react';
import { CONTRACT_CATEGORIES } from '../../../modules/planning/constants';
import { usePlanningData } from '../../../modules/planning/PlanningDataContext';
import type { Fornecedor, FornecedorInput } from '../../../modules/planning/types';
import { IntegrationPendingBanner } from '../../../shared/components/IntegrationPendingBanner';
import { formatCnpj, isCnpjValid } from '../../../shared/utils/cnpj';

interface FornecedorFormPageProps {
	fornecedor?: Fornecedor;
	onBack: () => void;
	onSaved: () => void;
}

type FormState = FornecedorInput;

const emptyForm = (): FormState => ({
	nome: '',
	cnpj: '',
	categoriaServico: 'Buffet/Alimentação',
	contato: '',
	pessoaContato: '',
	email: '',
	telefone: '',
	endereco: '',
});

export function FornecedorFormPage({ fornecedor, onBack, onSaved }: FornecedorFormPageProps) {
	const isEditing = !!fornecedor;
	const { criarFornecedor, atualizarFornecedor, integrationPending } = usePlanningData();

	const [form, setForm] = useState<FormState>(
		fornecedor
			? {
					nome: fornecedor.nome,
					cnpj: fornecedor.cnpj,
					categoriaServico: fornecedor.categoriaServico,
					contato: fornecedor.contato,
					pessoaContato: fornecedor.pessoaContato,
					email: fornecedor.email,
					telefone: fornecedor.telefone,
					endereco: fornecedor.endereco,
				}
			: emptyForm()
	);
	const [errors, setErrors] = useState<Partial<Record<keyof FormState, string>>>({});
	const [submitError, setSubmitError] = useState<string | null>(null);
	const [submitting, setSubmitting] = useState(false);

	const update = <K extends keyof FormState>(key: K, value: FormState[K]) => {
		setForm(prev => ({ ...prev, [key]: value }));
		setErrors(prev => ({ ...prev, [key]: undefined }));
		setSubmitError(null);
	};

	const validate = (): boolean => {
		const next: Partial<Record<keyof FormState, string>> = {};
		if (!form.nome.trim() || form.nome.trim().length < 3) {
			next.nome = 'Informe o nome (mínimo 3 caracteres).';
		}
		if (!isCnpjValid(form.cnpj)) {
			next.cnpj = 'CNPJ inválido (14 dígitos).';
		}
		if (!form.pessoaContato.trim() || form.pessoaContato.trim().length < 3) {
			next.pessoaContato = 'Informe a pessoa de contato.';
		}
		if (!form.email.trim() || !/\S+@\S+\.\S+/.test(form.email)) {
			next.email = 'E-mail inválido.';
		}
		if (!form.telefone.trim() || form.telefone.trim().length < 8) {
			next.telefone = 'Informe um telefone válido.';
		}
		if (!form.contato.trim()) {
			next.contato = 'Informe o contato principal.';
		}
		setErrors(next);
		return Object.keys(next).length === 0;
	};

	const handleSubmit = async (e: FormEvent) => {
		e.preventDefault();
		if (!validate()) return;

		const payload: FornecedorInput = {
			...form,
			contato: form.email.trim() || form.contato.trim(),
		};

		setSubmitting(true);
		setSubmitError(null);

		try {
			if (isEditing && fornecedor) {
				const ok = await atualizarFornecedor(fornecedor.id, payload);
				if (!ok) {
					setSubmitError(
						integrationPending
							? 'Integração com backend pendente. Implemente fornecedoresApi.editarFornecedor.'
							: 'Não foi possível salvar. Verifique se o fornecedor está ativo e se o CNPJ não está duplicado.'
					);
					return;
				}
			} else {
				const id = await criarFornecedor(payload);
				if (!id) {
					setSubmitError(
						integrationPending
							? 'Integração com backend pendente. Implemente fornecedoresApi.cadastrarFornecedor.'
							: 'Não foi possível cadastrar. Verifique os dados e se o CNPJ já existe.'
					);
					return;
				}
			}
			onSaved();
		} finally {
			setSubmitting(false);
		}
	};

	const locked = isEditing && fornecedor?.status === 'INATIVO';

	return (
		<div className="module-page module-page-narrow">
			<button type="button" className="back-link" onClick={onBack}>
				<svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
					<line x1="19" y1="12" x2="5" y2="12" />
					<polyline points="12 19 5 12 12 5" />
				</svg>
				Voltar para Fornecedores
			</button>

			<div className="content-card">
				<h2 style={{ fontSize: '1.5rem', fontWeight: 700, marginBottom: '0.25rem' }}>
					{isEditing ? 'Editar Fornecedor' : 'Novo Fornecedor'}
				</h2>
				<p style={{ color: '#6b7280', fontSize: '0.9rem', marginBottom: '1.5rem' }}>
					{isEditing
						? `Editando ${fornecedor?.nome}`
						: 'Preencha os dados para cadastrar um novo fornecedor'}
				</p>

				{integrationPending && <IntegrationPendingBanner />}

				{locked && (
					<div className="alert-box yellow" style={{ marginBottom: '1rem' }}>
						<div className="alert-content">
							<p style={{ fontSize: '0.85rem', margin: 0 }}>
								Fornecedores inativos não podem ser editados. Reative o cadastro na listagem.
							</p>
						</div>
					</div>
				)}

				{submitError && (
					<div className="error-message" style={{ padding: '0.75rem', marginBottom: '1rem' }}>
						{submitError}
					</div>
				)}

				<form onSubmit={handleSubmit}>
					<div className="form-grid">
						<div className="form-group">
							<label>Nome / Razão Social *</label>
							<input
								type="text"
								className="form-input"
								value={form.nome}
								disabled={locked}
								onChange={e => update('nome', e.target.value)}
								placeholder="Ex: Buffet Premium Eventos"
							/>
							{errors.nome && <span className="error-message">{errors.nome}</span>}
						</div>
						<div className="form-group">
							<label>Categoria *</label>
							<select
								className="form-select"
								value={form.categoriaServico}
								disabled={locked}
								onChange={e =>
									update('categoriaServico', e.target.value as FormState['categoriaServico'])
								}
							>
								{CONTRACT_CATEGORIES.map(c => (
									<option key={c} value={c}>{c}</option>
								))}
							</select>
						</div>
						<div className="form-group full-width">
							<label>CNPJ *</label>
							<input
								type="text"
								className="form-input mono-text"
								value={form.cnpj}
								disabled={locked}
								maxLength={18}
								placeholder="00.000.000/0000-00"
								onChange={e => update('cnpj', formatCnpj(e.target.value))}
							/>
							{errors.cnpj && <span className="error-message">{errors.cnpj}</span>}
						</div>
						<div className="form-group full-width">
							<label>Pessoa de Contato *</label>
							<input
								type="text"
								className="form-input"
								value={form.pessoaContato}
								disabled={locked}
								onChange={e => update('pessoaContato', e.target.value)}
							/>
							{errors.pessoaContato && (
								<span className="error-message">{errors.pessoaContato}</span>
							)}
						</div>
						<div className="form-group">
							<label>E-mail *</label>
							<input
								type="email"
								className="form-input"
								value={form.email}
								disabled={locked}
								onChange={e => update('email', e.target.value)}
							/>
							{errors.email && <span className="error-message">{errors.email}</span>}
						</div>
						<div className="form-group">
							<label>Telefone *</label>
							<input
								type="text"
								className="form-input"
								value={form.telefone}
								disabled={locked}
								onChange={e => update('telefone', e.target.value)}
							/>
							{errors.telefone && <span className="error-message">{errors.telefone}</span>}
						</div>
						<div className="form-group full-width">
							<label>Endereço</label>
							<input
								type="text"
								className="form-input"
								value={form.endereco}
								disabled={locked}
								onChange={e => update('endereco', e.target.value)}
								placeholder="Rua, número — Cidade, UF"
							/>
						</div>
					</div>

					<div className="alert-box blue" style={{ marginTop: '1rem' }}>
						<div className="alert-content">
							<h4>Regras de validação</h4>
							<ul>
								<li>CNPJ deve ser válido e único no cadastro</li>
								<li>Fornecedor inativo não pode ser editado</li>
								<li>Não é possível desativar fornecedor com contrato ativo</li>
							</ul>
						</div>
					</div>

					<div className="form-actions">
						<button type="button" className="btn-outline" onClick={onBack}>
							Cancelar
						</button>
						{!locked && (
							<button type="submit" className="action-btn" disabled={submitting}>
								{submitting
									? 'Salvando...'
									: isEditing
										? 'Salvar Alterações'
										: 'Cadastrar Fornecedor'}
							</button>
						)}
					</div>
				</form>
			</div>
		</div>
	);
}
