import type { ReactNode } from 'react';

interface ConfirmModalProps {
	title: string;
	description: ReactNode;
	confirmLabel: string;
	cancelLabel?: string;
	variant?: 'danger' | 'default';
	isLoading?: boolean;
	onConfirm: () => void;
	onCancel: () => void;
}

export function ConfirmModal({
	title,
	description,
	confirmLabel,
	cancelLabel = 'Cancelar',
	variant = 'danger',
	isLoading = false,
	onConfirm,
	onCancel,
}: ConfirmModalProps) {
	return (
		<div className="modal-overlay" role="dialog" aria-modal="true">
			<div className="modal-container">
				<div className="modal-body">
					<div
						className={`modal-icon-circle ${variant === 'danger' ? 'modal-icon-danger' : ''}`}
					>
						<svg width="24" height="24" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
							<circle cx="12" cy="12" r="10" />
							<line x1="12" y1="8" x2="12" y2="12" />
							<line x1="12" y1="16" x2="12.01" y2="16" />
						</svg>
					</div>
					<div className="modal-content-text">
						<h3 className="modal-title">{title}</h3>
						<div className="modal-description">{description}</div>
					</div>
				</div>
				<div className="modal-actions">
					<button
						type="button"
						className="modal-btn-cancelar"
						onClick={onCancel}
						disabled={isLoading}
					>
						{cancelLabel}
					</button>
					<button
						type="button"
						className={`modal-btn-confirm ${variant === 'danger' ? 'modal-btn-danger' : ''}`}
						onClick={onConfirm}
						disabled={isLoading}
					>
						{confirmLabel}
					</button>
				</div>
			</div>
		</div>
	);
}
