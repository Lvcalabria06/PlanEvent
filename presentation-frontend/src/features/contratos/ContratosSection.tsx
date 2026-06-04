import { useState } from 'react';
import { usePlanningData } from '../../modules/planning/PlanningDataContext';
import { ContratoDetailPage } from './pages/ContratoDetailPage';
import { ContratoFormPage } from './pages/ContratoFormPage';
import { ContratosListPage } from './pages/ContratosListPage';

export type ContratosView = 'list' | 'create' | 'edit' | 'detail';

export function ContratosSection() {
	const [view, setView] = useState<ContratosView>('list');
	const [selectedId, setSelectedId] = useState<string | null>(null);
	const { obterContrato } = usePlanningData();

	const goToList = () => {
		setView('list');
		setSelectedId(null);
	};

	const goToCreate = () => {
		setSelectedId(null);
		setView('create');
	};

	const goToEdit = (id: string) => {
		setSelectedId(id);
		setView('edit');
	};

	const goToDetail = (id: string) => {
		setSelectedId(id);
		setView('detail');
	};

	if (view === 'create') {
		return <ContratoFormPage onBack={goToList} onSaved={goToList} />;
	}

	if (view === 'edit' && selectedId) {
		const contrato = obterContrato(selectedId);
		if (!contrato) {
			goToList();
			return null;
		}
		return (
			<ContratoFormPage
				contrato={contrato}
				onBack={() => goToDetail(selectedId)}
				onSaved={() => goToDetail(selectedId)}
			/>
		);
	}

	if (view === 'detail' && selectedId) {
		const contrato = obterContrato(selectedId);
		if (!contrato) {
			goToList();
			return null;
		}
		return (
			<ContratoDetailPage
				contrato={contrato}
				onBack={goToList}
				onEdit={() => goToEdit(selectedId)}
			/>
		);
	}

	return (
		<ContratosListPage
			onCreate={goToCreate}
			onEdit={goToEdit}
			onView={goToDetail}
		/>
	);
}
