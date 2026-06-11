interface IntegrationPendingBannerProps {
	message?: string;
}

export function IntegrationPendingBanner({
	message = 'Integração com o backend Spring ainda não está ativa. As telas estão prontas — implemente os endpoints em modules/planning/*/api.ts.',
}: IntegrationPendingBannerProps) {
	return (
		<div className="alert-box yellow" style={{ marginBottom: '1rem' }}>
			<div className="alert-content">
				<h4 style={{ margin: '0 0 0.35rem', fontSize: '0.9rem' }}>Backend pendente</h4>
				<p style={{ fontSize: '0.85rem', margin: 0 }}>{message}</p>
			</div>
		</div>
	);
}
