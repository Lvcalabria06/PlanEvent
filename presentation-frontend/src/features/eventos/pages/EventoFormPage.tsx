import { useState, useRef, type FormEvent } from 'react';
import {
	PORTE_EVENTO_OPTIONS,
	TIPO_EVENTO_OPTIONS,
} from '../../../modules/planning/eventos/constants';
import type { CriarEventoDto, EventoDto } from '../../../modules/planning/eventos/dto';
import { fromApiDateInput, toApiDateTime } from '../../../modules/planning/eventos/mappers';
import {
	persistirOrcamentoEvento,
} from '../../../modules/planning/eventos/orcamentoApi';
import { ConfirmModal } from '../../../shared/components/ConfirmModal';
import {
	EventoOrcamentoSection,
	extrairValoresCategoria,
	validarOrcamentoForm,
	type OrcamentoFormState,
} from '../components/EventoOrcamentoSection';
import { useEventos } from '../EventosContext';

interface EventoFormPageProps {
	evento?: EventoDto;
	onBack: () => void;
	onSaved: () => void;
}

type FormState = {
	nome: string;
	tipo: CriarEventoDto['tipo'];
	porte: CriarEventoDto['porte'];
	quantidadeEstimadaParticipantes: string;
	objetivo: string;
	dataInicio: string;
	dataFim: string;
	requisitosInfraestrutura: string;
};

export function EventoFormPage({ evento, onBack, onSaved }: EventoFormPageProps) {
	const isEditing = !!evento;
	const locked = isEditing && (evento?.planejamentoConfirmado || evento?.concluido);
	const podeCancelar = isEditing && !locked && !evento?.cancelado;
	const { criarEvento, editarEvento, cancelarEvento } = useEventos();

	const [form, setForm] = useState<FormState>(() => ({
		nome: evento?.nome ?? '',
		tipo: evento?.tipo ?? 'CORPORATIVO',
		porte: evento?.porte ?? 'MEDIO',
		quantidadeEstimadaParticipantes:
			evento?.quantidadeEstimadaParticipantes?.toString() ?? '',
		objetivo: evento?.objetivo ?? '',
		dataInicio: fromApiDateInput(evento?.dataInicio),
		dataFim: fromApiDateInput(evento?.dataFim),
		requisitosInfraestrutura: evento?.requisitosInfraestrutura ?? '',
	}));
	const [errors, setErrors] = useState<Partial<Record<keyof FormState, string>>>({});
	const [submitError, setSubmitError] = useState<string | null>(null);
	const [submitting, setSubmitting] = useState(false);
	const [showCancelModal, setShowCancelModal] = useState(false);
	const [canceling, setCanceling] = useState(false);
	const [cancelError, setCancelError] = useState<string | null>(null);
	const orcamentoRef = useRef<OrcamentoFormState>({
		valorTotal: '',
		categorias: {},
		orcamentoExistente: null,
		categoriasExistentes: [],
	});
	const [orcamentoError, setOrcamentoError] = useState<string | null>(null);

	const update = <K extends keyof FormState>(key: K, value: FormState[K]) => {
		setForm(prev => ({ ...prev, [key]: value }));
		setErrors(prev => ({ ...prev, [key]: undefined }));
		setSubmitError(null);
	};

	const validate = (): boolean => {
		const next: Partial<Record<keyof FormState, string>> = {};
		if (!form.nome.trim() || form.nome.trim().length < 3) {
			next.nome = 'Informe um nome com pelo menos 3 caracteres.';
		}
		const participantes = parseInt(form.quantidadeEstimadaParticipantes, 10);
		if (isNaN(participantes) || participantes <= 0) {
			next.quantidadeEstimadaParticipantes = 'Informe a quantidade estimada de participantes.';
		}
		if (!form.objetivo.trim() || form.objetivo.trim().length < 10) {
			next.objetivo = 'Descreva o objetivo com pelo menos 10 caracteres.';
		}
		if (!form.dataInicio) next.dataInicio = 'Informe a data de início do período.';
		if (!form.dataFim) next.dataFim = 'Informe a data de término do período.';
		if (form.dataInicio && form.dataFim && form.dataFim < form.dataInicio) {
			next.dataFim = 'A data de término deve ser igual ou posterior à de início.';
		}
		setErrors(next);
		return Object.keys(next).length === 0;
	};

	const buildPayload = (): CriarEventoDto => ({
		nome: form.nome.trim(),
		tipo: form.tipo,
		porte: form.porte,
		quantidadeEstimadaParticipantes: parseInt(form.quantidadeEstimadaParticipantes, 10),
		objetivo: form.objetivo.trim(),
		dataInicio: toApiDateTime(form.dataInicio, '09:00:00'),
		dataFim: toApiDateTime(form.dataFim, '18:00:00'),
		requisitosInfraestrutura: form.requisitosInfraestrutura.trim() || null,
	});

	const handleSubmit = async (e: FormEvent) => {
		e.preventDefault();
		if (!validate()) return;

		const orcamentoErr = validarOrcamentoForm(orcamentoRef.current);
		if (orcamentoErr) {
			setOrcamentoError(orcamentoErr);
			return;
		}
		setOrcamentoError(null);

		setSubmitting(true);
		setSubmitError(null);
		const payload = buildPayload();
		const orcamentoState = orcamentoRef.current;
		const valorTotal = parseFloat(orcamentoState.valorTotal);

		try {
			let eventoId = evento?.id;

			if (isEditing && evento) {
				const ok = await editarEvento(evento.id, payload);
				if (!ok) {
					setSubmitError('Não foi possível salvar. Verifique os dados e o status do evento.');
					return;
				}
			} else {
				const id = await criarEvento(payload);
				if (!id) {
					setSubmitError('Não foi possível cadastrar o evento. Verifique os dados.');
					return;
				}
				eventoId = id;
			}

			if (eventoId) {
				try {
					await persistirOrcamentoEvento(
						eventoId,
						valorTotal,
						extrairValoresCategoria(orcamentoState),
						orcamentoState.orcamentoExistente,
						orcamentoState.categoriasExistentes,
					);
				} catch (orcErr) {
					const detalhe = orcErr instanceof Error ? orcErr.message : 'erro desconhecido';
					setOrcamentoError(
						isEditing
							? `Evento atualizado, mas o orçamento não foi salvo: ${detalhe}`
							: `Evento criado, mas o orçamento não foi salvo: ${detalhe}`,
					);
					return;
				}
			}

			onSaved();
		} catch (err) {
			const msg = err instanceof Error ? err.message : 'Erro ao salvar.';
			setSubmitError(msg);
		} finally {
			setSubmitting(false);
		}
	};

	const handleCancelEvento = async () => {
		if (!evento) return;
		setCanceling(true);
		setCancelError(null);
		const erro = await cancelarEvento(evento.id);
		setCanceling(false);
		if (erro) {
			setCancelError(erro);
			return;
		}
		setShowCancelModal(false);
		onBack();
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

			<div className="content-card evento-card-interactive">
				<div className="evento-form-header">
					<div>
						<h2 className="evento-form-title">
							{isEditing ? 'Editar Evento' : 'Novo Evento'}
						</h2>
						<p className="evento-form-subtitle">
							{isEditing
								? `Editando ${evento?.nome}`
								: 'Cadastre o evento antes de planejar o local'}
						</p>
					</div>
					{isEditing && (
						<div className="evento-form-id">
							<p className="evento-form-id-label">ID</p>
							<p className="mono-text evento-form-id-value">{evento?.id}</p>
						</div>
					)}
				</div>

				{locked && (
					<div className="alert-box yellow" style={{ marginBottom: '1rem' }}>
						<div className="alert-content">
							<p style={{ fontSize: '0.85rem', margin: 0 }}>
								Evento com preparação confirmada ou concluído não pode ser editado nesta etapa.
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
						<div className="form-group full-width">
							<label>Nome do Evento *</label>
							<input
								type="text"
								className="form-input"
								value={form.nome}
								disabled={locked}
								onChange={e => update('nome', e.target.value)}
							/>
							{errors.nome && <span className="error-message">{errors.nome}</span>}
						</div>
						<div className="form-group">
							<label>Tipo *</label>
							<select
								className="form-select"
								value={form.tipo}
								disabled={locked}
								onChange={e => update('tipo', e.target.value as FormState['tipo'])}
							>
								{TIPO_EVENTO_OPTIONS.map(t => (
									<option key={t.value} value={t.value}>{t.label}</option>
								))}
							</select>
						</div>
						<div className="form-group">
							<label>Porte *</label>
							<select
								className="form-select"
								value={form.porte}
								disabled={locked}
								onChange={e => update('porte', e.target.value as FormState['porte'])}
							>
								{PORTE_EVENTO_OPTIONS.map(p => (
									<option key={p.value} value={p.value}>{p.label}</option>
								))}
							</select>
						</div>
						<div className="form-group">
							<label>Participantes estimados *</label>
							<input
								type="number"
								min="1"
								className="form-input"
								value={form.quantidadeEstimadaParticipantes}
								disabled={locked}
								onChange={e => update('quantidadeEstimadaParticipantes', e.target.value)}
							/>
							{errors.quantidadeEstimadaParticipantes && (
								<span className="error-message">{errors.quantidadeEstimadaParticipantes}</span>
							)}
						</div>
						<div className="form-group">
							<label>Início do período *</label>
							<input
								type="date"
								className="form-input"
								value={form.dataInicio}
								disabled={locked}
								onChange={e => update('dataInicio', e.target.value)}
							/>
							{errors.dataInicio && <span className="error-message">{errors.dataInicio}</span>}
						</div>
						<div className="form-group">
							<label>Término do período *</label>
							<input
								type="date"
								className="form-input"
								value={form.dataFim}
								disabled={locked}
								onChange={e => update('dataFim', e.target.value)}
							/>
							{errors.dataFim && <span className="error-message">{errors.dataFim}</span>}
						</div>
						<div className="form-group full-width">
							<label>Objetivo *</label>
							<textarea
								className="form-input"
								rows={3}
								value={form.objetivo}
								disabled={locked}
								onChange={e => update('objetivo', e.target.value)}
								style={{ resize: 'vertical' }}
							/>
							{errors.objetivo && <span className="error-message">{errors.objetivo}</span>}
						</div>
						<div className="form-group full-width">
							<label>Requisitos de infraestrutura</label>
							<textarea
								className="form-input"
								rows={2}
								placeholder="Ex.: auditório com 200 lugares, estacionamento, acessibilidade..."
								value={form.requisitosInfraestrutura}
								disabled={locked}
								onChange={e => update('requisitosInfraestrutura', e.target.value)}
								style={{ resize: 'vertical' }}
							/>
						</div>
					</div>

					{orcamentoError && (
						<div className="error-message" style={{ padding: '0.75rem', marginTop: '1rem' }}>
							{orcamentoError}
						</div>
					)}

					<EventoOrcamentoSection
						eventoId={evento?.id}
						disabled={locked}
						onChange={state => {
							orcamentoRef.current = state;
						}}
					/>

					<div className="alert-box blue" style={{ marginTop: '1rem' }}>
						<div className="alert-content">
							<h4>Próximo passo</h4>
							<p style={{ fontSize: '0.85rem', margin: 0 }}>
								Após salvar, use &quot;Planejar local&quot; para definir o local principal,
								contingências e o <strong>teto de custo do espaço</strong> antes de confirmar a
								preparação.
							</p>
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
										: 'Cadastrar Evento'}
							</button>
						)}
					</div>
				</form>

				{podeCancelar && (
					<div className="evento-danger-zone">
						<h3 className="evento-danger-title">Cancelar evento</h3>
						<p className="evento-danger-text">
							A exclusão é lógica: o registro permanece no sistema, mas o evento deixa a listagem
							e não pode ser editado. Não é possível cancelar com contratos ativos ou após confirmar
							a preparação.
						</p>
						{cancelError && (
							<div className="error-message" style={{ padding: '0.75rem', marginBottom: '1rem' }}>
								{cancelError}
							</div>
						)}
						<button
							type="button"
							className="action-btn danger"
							onClick={() => {
								setCancelError(null);
								setShowCancelModal(true);
							}}
						>
							Cancelar evento
						</button>
					</div>
				)}
			</div>

			{showCancelModal && evento && (
				<ConfirmModal
					title="Cancelar Evento"
					description={
						<>
							<p>Você está prestes a cancelar o evento:</p>
							<div className="highlight-box">
								<strong>{evento.nome}</strong>
								<span className="mono-text">{evento.id}</span>
							</div>
							<p>
								O evento será removido da listagem, mas o histórico permanece no sistema.
								Esta ação não pode ser desfeita nesta interface.
							</p>
						</>
					}
					confirmLabel={canceling ? 'Cancelando...' : 'Confirmar cancelamento'}
					isLoading={canceling}
					onConfirm={() => void handleCancelEvento()}
					onCancel={() => setShowCancelModal(false)}
				/>
			)}
		</div>
	);
}
