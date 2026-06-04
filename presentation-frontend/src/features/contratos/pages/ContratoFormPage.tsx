import { useState, type FormEvent } from 'react';
import {
	CONTRATANTE_PADRAO,
	CONTRACT_CATEGORIES,
	CONTRACT_EVENTS,
	CONTRACT_TYPES,
} from '../../../modules/planning/constants';
import {
	buildPartes,
	usePlanningData,
} from '../../../modules/planning/PlanningDataContext';
import type { Contrato, ContratoInput } from '../../../modules/planning/types';

interface ContratoFormPageProps {
	contrato?: Contrato;
	onBack: () => void;
	onSaved: (id?: string) => void;
}

type FormState = {
	tipo: string;
	contratante: string;
	fornecedorId: string;
	objeto: string;
	valor: string;
	dataInicio: string;
	dataFim: string;
	eventoId: string;
	categoria: ContratoInput['categoria'];
};

export function ContratoFormPage({ contrato, onBack, onSaved }: ContratoFormPageProps) {
	const isEditing = !!contrato;
	const locked = isEditing && contrato?.status === 'ENCERRADO';
	const { criarContrato, atualizarContrato, fornecedoresAtivos, obterFornecedor } =
		usePlanningData();

	const [form, setForm] = useState<FormState>(() => ({
		tipo: contrato?.tipo ?? 'Prestação de Serviços',
		contratante: contrato?.partes[0] ?? CONTRATANTE_PADRAO,
		fornecedorId: contrato?.fornecedorId ?? fornecedoresAtivos[0]?.id ?? '',
		objeto: contrato?.objeto ?? '',
		valor: contrato?.valor?.toString() ?? '',
		dataInicio: contrato?.dataInicio ?? '',
		dataFim: contrato?.dataFim ?? '',
		eventoId: contrato?.eventoId ?? CONTRACT_EVENTS[0].id,
		categoria: contrato?.categoria ?? 'Buffet/Alimentação',
	}));
	const [errors, setErrors] = useState<Partial<Record<keyof FormState, string>>>({});
	const [submitError, setSubmitError] = useState<string | null>(null);
	const [savedId, setSavedId] = useState<string | null>(null);

	const selectedSupplier = obterFornecedor(form.fornecedorId);

	const update = <K extends keyof FormState>(key: K, value: FormState[K]) => {
		setForm(prev => ({ ...prev, [key]: value }));
		setErrors(prev => ({ ...prev, [key]: undefined }));
		setSubmitError(null);
	};

	const validate = (): boolean => {
		const next: Partial<Record<keyof FormState, string>> = {};
		if (!form.tipo) next.tipo = 'Selecione o tipo.';
		if (!form.contratante.trim()) next.contratante = 'Informe o contratante.';
		if (!form.fornecedorId) next.fornecedorId = 'Selecione um fornecedor ativo.';
		if (!form.objeto.trim() || form.objeto.trim().length < 20) {
			next.objeto = 'Descreva o objeto com pelo menos 20 caracteres.';
		}
		const valor = parseFloat(form.valor);
		if (isNaN(valor) || valor <= 0) next.valor = 'Informe um valor maior que zero.';
		if (!form.dataInicio) next.dataInicio = 'Informe a data de início.';
		if (!form.dataFim) next.dataFim = 'Informe a data de término.';
		if (form.dataInicio && form.dataFim && form.dataFim <= form.dataInicio) {
			next.dataFim = 'A data de término deve ser posterior à de início.';
		}
		setErrors(next);
		return Object.keys(next).length === 0;
	};

	const buildPayload = (): ContratoInput | null => {
		const fornecedor = obterFornecedor(form.fornecedorId);
		if (!fornecedor || fornecedor.status !== 'ATIVO') return null;
		const valor = parseFloat(form.valor);
		return {
			tipo: form.tipo,
			partes: buildPartes(form.contratante, fornecedor.nome),
			objeto: form.objeto.trim(),
			valor,
			dataInicio: form.dataInicio,
			dataFim: form.dataFim,
			eventoId: form.eventoId,
			fornecedorId: form.fornecedorId,
			categoria: form.categoria,
		};
	};

	const handleSubmit = (e: FormEvent) => {
		e.preventDefault();
		if (!validate()) return;
		const payload = buildPayload();
		if (!payload) {
			setSubmitError('Selecione um fornecedor ativo para vincular ao contrato.');
			return;
		}

		if (isEditing && contrato) {
			const ok = atualizarContrato(contrato.id, payload);
			if (!ok) {
				setSubmitError('Não foi possível salvar. Verifique se o contrato está ativo.');
				return;
			}
			onSaved(contrato.id);
		} else {
			const id = criarContrato(payload);
			if (!id) {
				setSubmitError('Não foi possível cadastrar o contrato. Verifique os dados.');
				return;
			}
			setSavedId(id);
			onSaved(id);
		}
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

			<div className="content-card">
				<div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
					<div>
						<h2 style={{ fontSize: '1.5rem', fontWeight: 700, marginBottom: '0.25rem' }}>
							{isEditing ? 'Editar Contrato' : 'Novo Contrato'}
						</h2>
						<p style={{ color: '#6b7280', fontSize: '0.9rem', marginBottom: '1.5rem' }}>
							{isEditing
								? `Editando ${contrato?.id}`
								: 'Preencha os dados para cadastrar um novo contrato'}
						</p>
					</div>
					{isEditing && (
						<div style={{ textAlign: 'right' }}>
							<p style={{ fontSize: '0.75rem', color: '#9ca3af', margin: 0 }}>ID do Contrato</p>
							<p className="mono-text" style={{ color: '#2563eb', fontWeight: 700, margin: 0 }}>
								{contrato?.id}
							</p>
						</div>
					)}
				</div>

				{locked && (
					<div className="alert-box yellow" style={{ marginBottom: '1rem' }}>
						<div className="alert-content">
							<p style={{ fontSize: '0.85rem', margin: 0 }}>
								Este contrato está encerrado e não pode ser editado.
							</p>
						</div>
					</div>
				)}

				{savedId && (
					<div className="alert-box blue" style={{ marginBottom: '1rem' }}>
						<div className="alert-content">
							<p style={{ fontSize: '0.85rem', margin: 0 }}>
								Contrato salvo com sucesso. ID: <strong className="mono-text">{savedId}</strong>
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
							<label>Tipo de Contrato *</label>
							<select
								className="form-select"
								value={form.tipo}
								disabled={locked}
								onChange={e => update('tipo', e.target.value)}
							>
								{CONTRACT_TYPES.map(t => (
									<option key={t} value={t}>{t}</option>
								))}
							</select>
							{errors.tipo && <span className="error-message">{errors.tipo}</span>}
						</div>
						<div className="form-group">
							<label>Categoria *</label>
							<select
								className="form-select"
								value={form.categoria}
								disabled={locked}
								onChange={e =>
									update('categoria', e.target.value as FormState['categoria'])
								}
							>
								{CONTRACT_CATEGORIES.map(c => (
									<option key={c} value={c}>{c}</option>
								))}
							</select>
						</div>
						<div className="form-group full-width">
							<label>Evento Vinculado *</label>
							<select
								className="form-select"
								value={form.eventoId}
								disabled={locked}
								onChange={e => update('eventoId', e.target.value)}
							>
								{CONTRACT_EVENTS.map(e => (
									<option key={e.id} value={e.id}>
										{e.id} — {e.name}
									</option>
								))}
							</select>
						</div>
						<div className="form-group full-width">
							<label>Contratante (Parte 1) *</label>
							<input
								type="text"
								className="form-input"
								value={form.contratante}
								disabled={locked}
								onChange={e => update('contratante', e.target.value)}
							/>
							{errors.contratante && (
								<span className="error-message">{errors.contratante}</span>
							)}
						</div>
						<div className="form-group full-width">
							<label>Fornecedor (Parte 2) *</label>
							{fornecedoresAtivos.length === 0 ? (
								<div className="alert-box yellow">
									<div className="alert-content">
										<p style={{ fontSize: '0.85rem', margin: 0 }}>
											Nenhum fornecedor ativo cadastrado. Cadastre um fornecedor antes de criar o contrato.
										</p>
									</div>
								</div>
							) : (
								<>
									<select
										className="form-select"
										value={form.fornecedorId}
										disabled={locked}
										onChange={e => update('fornecedorId', e.target.value)}
									>
										<option value="">— Selecione um fornecedor —</option>
										{fornecedoresAtivos.map(s => (
											<option key={s.id} value={s.id}>
												{s.nome} · {s.categoriaServico}
											</option>
										))}
									</select>
									{errors.fornecedorId && (
										<span className="error-message">{errors.fornecedorId}</span>
									)}
									{selectedSupplier && (
										<div className="supplier-preview">
											<strong>{selectedSupplier.nome}</strong>
											<span>
												{selectedSupplier.pessoaContato} · {selectedSupplier.telefone} ·{' '}
												{selectedSupplier.email}
											</span>
										</div>
									)}
								</>
							)}
						</div>
						<div className="form-group full-width">
							<label>Objeto do Contrato *</label>
							<textarea
								className="form-input"
								rows={3}
								value={form.objeto}
								disabled={locked}
								onChange={e => update('objeto', e.target.value)}
								style={{ resize: 'vertical' }}
							/>
							{errors.objeto && <span className="error-message">{errors.objeto}</span>}
						</div>
						<div className="form-group">
							<label>Valor Global (R$) *</label>
							<input
								type="number"
								min="0.01"
								step="0.01"
								className="form-input"
								value={form.valor}
								disabled={locked}
								onChange={e => update('valor', e.target.value)}
							/>
							{errors.valor && <span className="error-message">{errors.valor}</span>}
						</div>
						<div className="form-group">
							<label>Data de Início *</label>
							<input
								type="date"
								className="form-input"
								value={form.dataInicio}
								disabled={locked}
								onChange={e => update('dataInicio', e.target.value)}
							/>
							{errors.dataInicio && (
								<span className="error-message">{errors.dataInicio}</span>
							)}
						</div>
						<div className="form-group">
							<label>Data de Término *</label>
							<input
								type="date"
								className="form-input"
								value={form.dataFim}
								disabled={locked}
								onChange={e => update('dataFim', e.target.value)}
							/>
							{errors.dataFim && <span className="error-message">{errors.dataFim}</span>}
						</div>
					</div>

					<div className="alert-box blue" style={{ marginTop: '1rem' }}>
						<div className="alert-content">
							<h4>Regras de validação</h4>
							<ul>
								<li>Contrato exige fornecedor ativo e pelo menos duas partes</li>
								<li>Vigência: data de término posterior à de início</li>
								<li>Contratos encerrados não podem ser editados</li>
							</ul>
						</div>
					</div>

					<div className="form-actions">
						<button type="button" className="btn-outline" onClick={onBack}>
							Cancelar
						</button>
						{!locked && (
							<button type="submit" className="action-btn">
								{isEditing ? 'Salvar Alterações' : 'Cadastrar Contrato'}
							</button>
						)}
					</div>
				</form>
			</div>
		</div>
	);
}
