import { useEffect, useState } from 'react';
import { CATEGORIAS_ORCAMENTO } from '../../../modules/planning/eventos/constants';
import {
	buscarOrcamentoApi,
	listarCategoriasOrcamentoApi,
	type CategoriaOrcamentoDto,
	type OrcamentoEventoDto,
} from '../../../modules/planning/eventos/orcamentoApi';

export type OrcamentoFormState = {
	valorTotal: string;
	categorias: Record<string, string>;
	orcamentoExistente: OrcamentoEventoDto | null;
	categoriasExistentes: CategoriaOrcamentoDto[];
};

interface EventoOrcamentoSectionProps {
	eventoId?: string;
	disabled?: boolean;
	onChange: (state: OrcamentoFormState) => void;
}

function emptyCategorias(): Record<string, string> {
	return Object.fromEntries(CATEGORIAS_ORCAMENTO.map(c => [c.value, ''));
}

export function EventoOrcamentoSection({
	eventoId,
	disabled = false,
	onChange,
}: EventoOrcamentoSectionProps) {
	const [valorTotal, setValorTotal] = useState('');
	const [categorias, setCategorias] = useState<Record<string, string>>(emptyCategorias);
	const [orcamentoExistente, setOrcamentoExistente] = useState<OrcamentoEventoDto | null>(null);
	const [categoriasExistentes, setCategoriasExistentes] = useState<CategoriaOrcamentoDto[]>([]);
	const [loading, setLoading] = useState(Boolean(eventoId));
	const [loadError, setLoadError] = useState<string | null>(null);

	useEffect(() => {
		if (!eventoId) {
			setLoading(false);
			return;
		}

		let cancelled = false;
		setLoading(true);
		setLoadError(null);

		void (async () => {
			try {
				const orcamento = await buscarOrcamentoApi(eventoId);
				if (cancelled) return;

				if (!orcamento) {
					setOrcamentoExistente(null);
					setCategoriasExistentes([]);
					setValorTotal('');
					setCategorias(emptyCategorias());
					return;
				}

				const cats = await listarCategoriasOrcamentoApi(eventoId);
				if (cancelled) return;

				const valores = emptyCategorias();
				for (const cat of cats) {
					valores[cat.categoria] = String(cat.valorPrevisto);
				}

				setOrcamentoExistente(orcamento);
				setCategoriasExistentes(cats);
				setValorTotal(String(orcamento.valorTotal));
				setCategorias(valores);
			} catch {
				if (!cancelled) {
					setLoadError('Não foi possível carregar o orçamento deste evento.');
				}
			} finally {
				if (!cancelled) setLoading(false);
			}
		})();

		return () => {
			cancelled = true;
		};
	}, [eventoId]);

	useEffect(() => {
		onChange({
			valorTotal,
			categorias,
			orcamentoExistente,
			categoriasExistentes,
		});
	}, [valorTotal, categorias, orcamentoExistente, categoriasExistentes, onChange]);

	const somaCategorias = CATEGORIAS_ORCAMENTO.reduce((acc, cat) => {
		const v = parseFloat(categorias[cat.value] || '');
		return acc + (Number.isFinite(v) && v > 0 ? v : 0);
	}, 0);

	return (
		<div className="evento-orcamento-section">
			<div className="evento-orcamento-header">
				<h3 className="evento-section-title">Orçamento previsto do evento</h3>
				<p className="evento-section-hint">
					Orçamento financeiro (despesas e relatórios). Diferente do{' '}
					<strong>teto de custo do espaço</strong>, definido depois em Planejar local.
				</p>
			</div>

			{loading && <p className="evento-muted">Carregando orçamento...</p>}
			{loadError && <div className="error-message">{loadError}</div>}

			{!loading && (
				<div className="form-grid">
					<div className="form-group full-width">
						<label>Valor total previsto (R$) *</label>
						<input
							type="number"
							min="0"
							step="0.01"
							className="form-input"
							placeholder="Ex.: 125000"
							value={valorTotal}
							disabled={disabled || Boolean(orcamentoExistente)}
							onChange={e => setValorTotal(e.target.value)}
						/>
						{orcamentoExistente && (
							<span className="form-help-text">
								O valor total não pode ser alterado após o cadastro. Ajuste os valores por
								categoria abaixo.
							</span>
						)}
					</div>

					<div className="form-group full-width">
						<label>Previsto por categoria</label>
						<p className="form-help-text" style={{ marginTop: 0 }}>
							Informe o orçamento previsto em cada categoria para comparar com despesas no módulo
							Financeiro.
						</p>
						<div className="evento-orcamento-grid">
							{CATEGORIAS_ORCAMENTO.map(cat => (
								<div key={cat.value} className="evento-orcamento-row">
									<span className="evento-orcamento-cat-label">{cat.label}</span>
									<input
										type="number"
										min="0"
										step="0.01"
										className="form-input"
										placeholder="0,00"
										value={categorias[cat.value]}
										disabled={disabled}
										onChange={e =>
											setCategorias(prev => ({ ...prev, [cat.value]: e.target.value }))
										}
									/>
								</div>
							))}
						</div>
						{somaCategorias > 0 && (
							<p className="form-help-text">
								Soma das categorias:{' '}
								<strong>
									{somaCategorias.toLocaleString('pt-BR', {
										style: 'currency',
										currency: 'BRL',
									})}
								</strong>
							</p>
						)}
					</div>
				</div>
			)}
		</div>
	);
}

export function validarOrcamentoForm(state: OrcamentoFormState): string | null {
	const total = parseFloat(state.valorTotal);
	if (!state.valorTotal.trim() || !Number.isFinite(total) || total <= 0) {
		return 'Informe o valor total previsto do orçamento (maior que zero).';
	}

	const valores: Record<string, number> = {};
	for (const [cat, raw] of Object.entries(state.categorias)) {
		if (!raw.trim()) continue;
		const v = parseFloat(raw);
		if (!Number.isFinite(v) || v < 0) {
			return `Valor inválido na categoria ${cat}.`;
		}
		if (v > 0) valores[cat] = v;
	}

	const soma = Object.values(valores).reduce((a, b) => a + b, 0);
	if (soma > total) {
		return 'A soma das categorias não pode ultrapassar o valor total previsto.';
	}

	return null;
}

export function extrairValoresCategoria(state: OrcamentoFormState): Record<string, number> {
	const valores: Record<string, number> = {};
	for (const [cat, raw] of Object.entries(state.categorias)) {
		if (!raw.trim()) continue;
		const v = parseFloat(raw);
		if (Number.isFinite(v) && v > 0) valores[cat] = v;
	}
	return valores;
}
