import { useState } from 'react';
import { usePlanningData } from '../../modules/planning/PlanningDataContext';
import { FornecedoresListPage } from './pages/FornecedoresListPage';
import { FornecedorFormPage } from './pages/FornecedorFormPage';

export type FornecedoresView = 'list' | 'create' | 'edit';

export function FornecedoresSection() {
	const [view, setView] = useState<FornecedoresView>('list');
	const [selectedId, setSelectedId] = useState<string | null>(null);
	const { obterFornecedor } = usePlanningData();

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

	if (view === 'create') {
		return <FornecedorFormPage onBack={goToList} onSaved={goToList} />;
	}

	if (view === 'edit' && selectedId) {
		const fornecedor = obterFornecedor(selectedId);
		if (!fornecedor) {
			goToList();
			return null;
		}
		return (
			<FornecedorFormPage
				fornecedor={fornecedor}
				onBack={goToList}
				onSaved={goToList}
			/>
		);
	}

	return (
		<FornecedoresListPage
			onCreate={goToCreate}
			onEdit={goToEdit}
		/>
	);
}
