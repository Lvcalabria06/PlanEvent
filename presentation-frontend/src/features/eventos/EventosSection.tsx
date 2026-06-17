import { useEffect, useState } from 'react';
import type { ReactNode } from 'react';
import './eventos.css';
import { useEventos } from './EventosContext';
import { EventoAlocacaoPage } from './pages/EventoAlocacaoPage';
import { EventoDetailPage } from './pages/EventoDetailPage';
import { EventoFormPage } from './pages/EventoFormPage';
import { EventosListPage } from './pages/EventosListPage';

export type EventosView = 'list' | 'create' | 'edit' | 'detail' | 'alocacao';

export function EventosSection() {
	const [view, setView] = useState<EventosView>('list');
	const [selectedId, setSelectedId] = useState<string | null>(null);
	const { obterEvento } = useEventos();
	const selectedEvento = selectedId ? obterEvento(selectedId) : null;

	useEffect(() => {
		if (view !== 'list' && view !== 'create' && selectedId && !selectedEvento) {
			setView('list');
			setSelectedId(null);
		}
	}, [view, selectedId, selectedEvento]);

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

	const goToAlocacao = (id: string) => {
		setSelectedId(id);
		setView('alocacao');
	};

	let content: ReactNode;

	if (view === 'create') {
		content = <EventoFormPage onBack={goToList} onSaved={goToList} />;
	} else if (view === 'edit' && selectedId && selectedEvento) {
		content = (
			<EventoFormPage
				evento={selectedEvento}
				onBack={() => goToDetail(selectedId)}
				onSaved={() => goToDetail(selectedId)}
			/>
		);
	} else if (view === 'alocacao' && selectedId && selectedEvento) {
		content = (
			<EventoAlocacaoPage
				evento={selectedEvento}
				onBack={() => goToDetail(selectedId)}
				onSaved={() => goToDetail(selectedId)}
			/>
		);
	} else if (view === 'detail' && selectedId && selectedEvento) {
		content = (
			<EventoDetailPage
				evento={selectedEvento}
				onBack={goToList}
				onEdit={() => goToEdit(selectedId)}
				onPlanejarLocal={() => goToAlocacao(selectedId)}
			/>
		);
	} else {
		content = (
			<EventosListPage
				onCreate={goToCreate}
				onEdit={goToEdit}
				onView={goToDetail}
				onPlanejarLocal={goToAlocacao}
			/>
		);
	}

	return (
		<div className="eventos-module">
			<div className="eventos-view" key={view + (selectedId ?? '')}>
				{content}
			</div>
		</div>
	);
}
