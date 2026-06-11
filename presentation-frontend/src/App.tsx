import { useState } from 'react'
import { Toaster } from 'sonner'
import './App.css'
import { FornecedoresSection } from './features/fornecedores/FornecedoresSection'
import { ContratosSection } from './features/contratos/ContratosSection'
import { PlanningDataProvider } from './modules/planning/PlanningDataContext'
import './agenda/agenda.css'
import AgendaModule from './agenda/AgendaModule'
import { AgendaProvider, useAgenda } from './agenda/AgendaContext'
import LembretesNotificacaoPopup from './agenda/LembretesNotificacaoPopup'
import TarefasApp from './app/TarefasApp'

interface Funcionario {
	id: string;
	nome: string;
	email: string;
	cargo: string;
	competencias: string[];
	disponibilidade: string;
	status: 'Disponível' | 'Em equipe' | 'Inativo';
	ativo: boolean;
	lider: boolean;
	criadoEm: string;
	atualizadoEm: string;
}

interface Equipe {
	id: string;
	eventoId: string;
	nome: string;
	membros: { funcionarioId: string; lider: boolean }[];
}

function TopbarBell({ onVerAgenda }: { onVerAgenda: () => void }) {
  const { lembretes, compromissos, eventos, lembretesPendentes } = useAgenda()
  const [aberto, setAberto] = useState(false)
  const total = lembretesPendentes.length

  return (
    <div className="topbar-bell-wrap">
      <button
        type="button"
        className="notification-bell-btn"
        onClick={() => setAberto((v) => !v)}
        aria-expanded={aberto}
        aria-label={`Lembretes, ${total} pendentes`}
      >
        <svg width="20" height="20" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
          <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9" />
          <path d="M13.73 21a2 2 0 0 1-3.46 0" />
        </svg>
        {total > 0 && <span className="notification-badge">{total > 9 ? '9+' : total}</span>}
      </button>
      {aberto && (
        <LembretesNotificacaoPopup
          lembretes={lembretes}
          compromissos={compromissos}
          eventos={eventos}
          onClose={() => setAberto(false)}
          onVerAgenda={() => {
            onVerAgenda()
            setAberto(false)
          }}
        />
      )}
    </div>
  )
}

export default function App() {
	// Shared state for navigation
	const [currentTab, setCurrentTab] = useState<'dashboard' | 'equipe' | 'eventos' | 'locais' | 'estoque' | 'financeiro' | 'aprovacoes' | 'tarefas' | 'agenda' | 'contratos' | 'fornecedores' | 'conciliacao'>('dashboard');

	// Shared state for team management
	const [funcionarios, setFuncionarios] = useState<Funcionario[]>([
		{
			id: '1',
			nome: 'Maria Silva',
			email: 'maria.silva@empresa.com',
			cargo: 'Coordenador',
			competencias: ['Gestão', 'Eventos Corporativos', 'Liderança'],
			disponibilidade: 'INTEGRAL',
			status: 'Em equipe',
			ativo: true,
			lider: true,
			criadoEm: '01/04/2026, 10:00:00',
			atualizadoEm: '01/04/2026, 10:00:00'
		},
		{
			id: '2',
			nome: 'João Santos',
			email: 'joao.santos@empresa.com',
			cargo: 'Técnico A/V',
			competencias: ['Áudio', 'Vídeo', 'Iluminação'],
			disponibilidade: 'TARDE',
			status: 'Em equipe',
			ativo: true,
			lider: false,
			criadoEm: '01/04/2026, 10:00:00',
			atualizadoEm: '01/04/2026, 10:00:00'
		},
		{
			id: '3',
			nome: 'Ana Costa',
			email: 'ana.costa@empresa.com',
			cargo: 'Logística',
			competencias: ['Logística', 'Transporte', 'Estoque'],
			disponibilidade: 'MANHÃ',
			status: 'Disponível',
			ativo: true,
			lider: false,
			criadoEm: '01/04/2026, 10:00:00',
			atualizadoEm: '01/04/2026, 10:00:00'
		}
	]);

	const [equipes, setEquipes] = useState<Equipe[]>([
		{
			id: 'equipe-1',
			eventoId: 'evento-1',
			nome: 'Equipe Alpha',
			membros: [
				{ funcionarioId: '1', lider: true },
				{ funcionarioId: '2', lider: false }
			]
		}
	]);

	// Team management navigation and form states
	const [currentView, setCurrentView] = useState<'list' | 'create' | 'edit' | 'create-team' | 'edit-team'>('list');
	const [activeTab, setActiveTab] = useState<'funcionarios' | 'equipes'>('funcionarios');
	const [selectedFuncId, setSelectedFuncId] = useState<string | null>(null);
	const [selectedTeamId, setSelectedTeamId] = useState<string | null>(null);
	const [showInativos, setShowInativos] = useState<boolean>(false);
	const [searchExpression, setSearchExpression] = useState<string>('');

	const [formNome, setFormNome] = useState('');
	const [formEmail, setFormEmail] = useState('');
	const [formCargo, setFormCargo] = useState('');
	const [formDisponibilidade, setFormDisponibilidade] = useState('INTEGRAL');
	const [formCompetencias, setFormCompetencias] = useState('');
	const [formErrors, setFormErrors] = useState<{ [key: string]: string }>({});

	const [teamNome, setTeamNome] = useState('');
	const [teamEventoId, setTeamEventoId] = useState<string>('');
	const [selectedMembros, setSelectedMembros] = useState<string[]>([]);
	const [teamLiderId, setTeamLiderId] = useState<string>('');
	const [teamErrors, setTeamErrors] = useState<string>('');

	// Confirmation modal states
	const [showInativarModal, setShowInativarModal] = useState<boolean>(false);
	const [funcToInativar, setFuncToInativar] = useState<Funcionario | null>(null);

	// List of events for the team selection
	const [eventosList] = useState<{ id: string; nome: string; dataEvento?: string }[]>([
		{ id: 'evento-1', nome: 'Conferência Anual de TI 2026', dataEvento: '2026-05-15' },
		{ id: 'evento-2', nome: 'Workshop de Liderança Q2', dataEvento: '2026-06-10' },
		{ id: 'evento-3', nome: 'Convenção Anual 2026', dataEvento: '2026-04-20' },
	]);

	// JS expression evaluator representing the Interpreter pattern
	const evaluateInterpreterExpression = (func: Funcionario, expr: string): boolean => {
		if (!expr || expr.trim() === '') return true;
		try {
			const normalizedExpr = expr.trim();
			if (normalizedExpr.toUpperCase().includes(' OR ')) {
				const parts = normalizedExpr.split(/\s+OR\s+/i);
				return parts.some(p => evaluateInterpreterExpression(func, p));
			}
			if (normalizedExpr.toUpperCase().includes(' AND ')) {
				const parts = normalizedExpr.split(/\s+AND\s+/i);
				return parts.every(p => evaluateInterpreterExpression(func, p));
			}

			if (normalizedExpr.includes('=')) {
				let [field, value] = normalizedExpr.split('=');
				field = field.trim().toLowerCase();
				value = value.trim().replace(/['"]/g, '').toLowerCase();

				if (field === 'lider') {
					return func.lider === (value === 'true');
				}
				if (field === 'cargo') {
					return func.cargo.toLowerCase() === value;
				}
				if (field === 'disponibilidade') {
					return func.disponibilidade.toLowerCase() === value;
				}
				if (field === 'status') {
					return func.status.toLowerCase() === value;
				}
				if (field === 'nome') {
					return func.nome.toLowerCase().includes(value);
				}
				return false;
			}

			const query = normalizedExpr.toLowerCase();
			return func.nome.toLowerCase().includes(query) || func.cargo.toLowerCase().includes(query);
		} catch {
			return true;
		}
	};

	const validateForm = () => {
		const errors: { [key: string]: string } = {};
		if (!formNome.trim()) {
			errors.nome = 'Nome é obrigatório.';
		} else if (formNome.trim().length < 3) {
			errors.nome = 'Nome deve ter no mínimo 3 caracteres.';
		} else if (/\d/.test(formNome)) {
			errors.nome = 'Nome não pode conter números.';
		}
		if (!formEmail.trim()) {
			errors.email = 'Email é obrigatório.';
		} else if (!/\S+@\S+\.\S+/.test(formEmail)) {
			errors.email = 'Email inválido.';
		}
		if (!formCargo) {
			errors.cargo = 'Cargo é obrigatório.';
		}
		setFormErrors(errors);
		return Object.keys(errors).length === 0;
	};

	const handleCreateFuncionario = (e: React.FormEvent) => {
		e.preventDefault();
		if (!validateForm()) return;

		const newFunc: Funcionario = {
			id: String(funcionarios.length + 1),
			nome: formNome.trim(),
			email: formEmail.trim(),
			cargo: formCargo,
			competencias: formCompetencias.split(',').map(s => s.trim()).filter(Boolean),
			disponibilidade: formDisponibilidade.toUpperCase(),
			status: 'Disponível',
			ativo: true,
			lider: false,
			criadoEm: new Date().toLocaleString('pt-BR'),
			atualizadoEm: new Date().toLocaleString('pt-BR')
		};

		setFuncionarios([...funcionarios, newFunc]);
		resetForm();
		setCurrentView('list');
	};

	const handleEditFuncionario = (e: React.FormEvent) => {
		e.preventDefault();
		if (!validateForm() || !selectedFuncId) return;

		setFuncionarios(
			funcionarios.map(f => {
				if (f.id === selectedFuncId) {
					return {
						...f,
						nome: formNome.trim(),
						email: formEmail.trim(),
						cargo: formCargo,
						competencias: formCompetencias.split(',').map(s => s.trim()).filter(Boolean),
						disponibilidade: formDisponibilidade.toUpperCase(),
						atualizadoEm: new Date().toLocaleString('pt-BR')
					};
				}
				return f;
			})
		);

		resetForm();
		setCurrentView('list');
	};

	const triggerInativarConfirmation = (id: string) => {
		const isLinkedToTeam = equipes.some(eq => eq.membros.some(m => m.funcionarioId === id));
		if (isLinkedToTeam) {
			alert('Não é possível inativar funcionário vinculado a uma equipe.');
			return;
		}
		const func = funcionarios.find(f => f.id === id);
		if (func) {
			setFuncToInativar(func);
			setShowInativarModal(true);
		}
	};

	const confirmInativarFuncionario = () => {
		if (funcToInativar) {
			setFuncionarios(
				funcionarios.map(f => {
					if (f.id === funcToInativar.id) {
						return { ...f, ativo: false, status: 'Inativo', atualizadoEm: new Date().toLocaleString('pt-BR') };
					}
					return f;
				})
			);
			setShowInativarModal(false);
			setFuncToInativar(null);
			resetForm();
			setCurrentView('list');
		}
	};

	const handleAtivarFuncionario = (id: string) => {
		setFuncionarios(
			funcionarios.map(f => {
				if (f.id === id) {
					return { ...f, ativo: true, status: 'Disponível', atualizadoEm: new Date().toLocaleString('pt-BR') };
				}
				return f;
			})
		);
	};

	const handleCreateEquipe = (e: React.FormEvent) => {
		e.preventDefault();
		if (!teamEventoId) {
			setTeamErrors('A equipe deve estar associada a um evento válido.');
			return;
		}
		if (!teamNome.trim()) {
			setTeamErrors('Nome da equipe é obrigatório.');
			return;
		}
		if (selectedMembros.length === 0) {
			setTeamErrors('Equipe deve possuir pelo menos um funcionário.');
			return;
		}

		// Nome deve ser único no evento
		const nomeDuplicado = equipes.some(eq => eq.eventoId === teamEventoId && eq.nome.toLowerCase() === teamNome.trim().toLowerCase());
		if (nomeDuplicado) {
			setTeamErrors('Já existe uma equipe com esse nome no evento.');
			return;
		}

		const alocado = selectedMembros.some(mId =>
			equipes.some(eq => eq.eventoId === teamEventoId && eq.membros.some(m => m.funcionarioId === mId))
		);
		if (alocado) {
			setTeamErrors('Um ou mais funcionários já pertencem a outra equipe neste evento.');
			return;
		}

		const newTeam: Equipe = {
			id: 'equipe-' + (equipes.length + 1),
			eventoId: teamEventoId,
			nome: teamNome.trim(),
			membros: selectedMembros.map(mId => ({
				funcionarioId: mId,
				lider: mId === teamLiderId
			}))
		};

		setFuncionarios(
			funcionarios.map(f => {
				if (selectedMembros.includes(f.id)) {
					return {
						...f,
						status: 'Em equipe',
						lider: f.id === teamLiderId
					};
				}
				return f;
			})
		);

		setEquipes([...equipes, newTeam]);
		setTeamNome('');
		setTeamEventoId('');
		setSelectedMembros([]);
		setTeamLiderId('');
		setTeamErrors('');
		setCurrentView('list');
	};

	const handleEditEquipe = (e: React.FormEvent) => {
		e.preventDefault();
		if (!selectedTeamId) return;

		if (!teamEventoId) {
			setTeamErrors('A equipe deve estar associada a um evento válido.');
			return;
		}
		if (!teamNome.trim()) {
			setTeamErrors('Nome da equipe é obrigatório.');
			return;
		}
		if (selectedMembros.length === 0) {
			setTeamErrors('Equipe deve possuir pelo menos um funcionário.');
			return;
		}

		// Nome único no evento (excluindo a própria equipe)
		const nomeDuplicado = equipes.some(eq => eq.id !== selectedTeamId && eq.eventoId === teamEventoId && eq.nome.toLowerCase() === teamNome.trim().toLowerCase());
		if (nomeDuplicado) {
			setTeamErrors('Já existe uma equipe com esse nome no evento.');
			return;
		}

		// Funcionário alocado em outra equipe do mesmo evento
		const alocado = selectedMembros.some(mId =>
			equipes.some(eq => eq.id !== selectedTeamId && eq.eventoId === teamEventoId && eq.membros.some(m => m.funcionarioId === mId))
		);
		if (alocado) {
			setTeamErrors('Um ou mais funcionários já pertencem a outra equipe neste evento.');
			return;
		}

		const prevTeam = equipes.find(eq => eq.id === selectedTeamId);
		const prevMemberIds = prevTeam ? prevTeam.membros.map(m => m.funcionarioId) : [];

		// Reset status of old members to "Disponível"
		let updatedFuncionarios = funcionarios.map(f => {
			if (prevMemberIds.includes(f.id)) {
				return { ...f, status: 'Disponível' as const, lider: false };
			}
			return f;
		});

		// Update status of current selected members to "Em equipe"
		updatedFuncionarios = updatedFuncionarios.map(f => {
			if (selectedMembros.includes(f.id)) {
				return {
					...f,
					status: 'Em equipe' as const,
					lider: f.id === teamLiderId
				};
			}
			return f;
		});

		setFuncionarios(updatedFuncionarios);

		setEquipes(
			equipes.map(eq => {
				if (eq.id === selectedTeamId) {
					return {
						...eq,
						eventoId: teamEventoId,
						nome: teamNome.trim(),
						membros: selectedMembros.map(mId => ({
							funcionarioId: mId,
							lider: mId === teamLiderId
						}))
					};
				}
				return eq;
			})
		);

		setTeamNome('');
		setTeamEventoId('');
		setSelectedMembros([]);
		setTeamLiderId('');
		setTeamErrors('');
		setCurrentView('list');
	};

	const handleRemoveEquipe = (teamId: string) => {
		const team = equipes.find(eq => eq.id === teamId);
		if (!team) return;

		const teamMemberIds = team.membros.map(m => m.funcionarioId);
		setFuncionarios(
			funcionarios.map(f => {
				if (teamMemberIds.includes(f.id)) {
					return { ...f, status: 'Disponível', lider: false };
				}
				return f;
			})
		);

		setEquipes(equipes.filter(eq => eq.id !== teamId));
		setCurrentView('list');
	};

	const navigateToEditTeam = (team: Equipe) => {
		setSelectedTeamId(team.id);
		setTeamNome(team.nome);
		setTeamEventoId(team.eventoId);
		setSelectedMembros(team.membros.map(m => m.funcionarioId));
		const liderMember = team.membros.find(m => m.lider);
		setTeamLiderId(liderMember ? liderMember.funcionarioId : '');
		setTeamErrors('');
		setCurrentView('edit-team');
	};

	const navigateToCreateTeam = () => {
		setTeamNome('');
		setTeamEventoId('');
		setSelectedMembros([]);
		setTeamLiderId('');
		setTeamErrors('');
		setCurrentView('create-team');
	};

	const resetForm = () => {
		setFormNome('');
		setFormEmail('');
		setFormCargo('');
		setFormDisponibilidade('INTEGRAL');
		setFormCompetencias('');
		setFormErrors({});
		setSelectedFuncId(null);
	};

	const navigateToEdit = (func: Funcionario) => {
		setSelectedFuncId(func.id);
		setFormNome(func.nome);
		setFormEmail(func.email);
		setFormCargo(func.cargo);
		setFormDisponibilidade(func.disponibilidade);
		setFormCompetencias(func.competencias.join(', '));
		setFormErrors({});
		setCurrentView('edit');
	};

	const totalAtivos = funcionarios.filter(f => f.ativo).length;
	const totalInativos = funcionarios.filter(f => !f.ativo).length;
	const totalDisponiveis = funcionarios.filter(f => f.ativo && f.status === 'Disponível').length;
	const totalAlocados = funcionarios.filter(f => f.ativo && f.status === 'Em equipe').length;
	const totalEquipes = equipes.length;

	const filteredFuncionarios = funcionarios.filter(f => {
		if (!showInativos && !f.ativo) return false;
		return evaluateInterpreterExpression(f, searchExpression);
	});

	const eventosAgenda = eventosList.map((e) => ({
		id: e.id,
		nome: e.nome,
		dataEvento: e.dataEvento ?? '2026-05-15',
	}));

	return (
		<AgendaProvider eventos={eventosAgenda}>
		<PlanningDataProvider>
		<Toaster position="top-right" richColors />
		<div className="app-layout">
			{/* Menu Lateral (Sidebar) */}
			<aside className="sidebar">
				<div>
					<div className="brand-section">
						<div className="brand-title">EventOS</div>
						<div className="brand-subtitle">Gestão de Eventos</div>
					</div>

					<nav className="nav-links">
						<button
							className={`nav-item ${currentTab === 'dashboard' ? 'active' : ''}`}
							onClick={() => setCurrentTab('dashboard')}
						>
							<svg className="nav-icon" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
								<rect x="3" y="3" width="7" height="9" rx="1" />
								<rect x="14" y="3" width="7" height="5" rx="1" />
								<rect x="14" y="12" width="7" height="9" rx="1" />
								<rect x="3" y="16" width="7" height="5" rx="1" />
							</svg>
							Dashboard
						</button>

						<button
							className={`nav-item ${currentTab === 'eventos' ? 'active' : ''}`}
							onClick={() => setCurrentTab('eventos')}
						>
							<svg className="nav-icon" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
								<path d="M12 2v20M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6" />
							</svg>
							Eventos
						</button>

						<button
							className={`nav-item ${currentTab === 'locais' ? 'active' : ''}`}
							onClick={() => setCurrentTab('locais')}
						>
							<svg className="nav-icon" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
								<path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z" />
								<circle cx="12" cy="10" r="3" />
							</svg>
							Locais
						</button>

						<button
							className={`nav-item ${currentTab === 'estoque' ? 'active' : ''}`}
							onClick={() => setCurrentTab('estoque')}
						>
							<svg className="nav-icon" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
								<path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z" />
								<polyline points="3.27 6.96 12 12.01 20.73 6.96" />
								<line x1="12" y1="22.08" x2="12" y2="12" />
							</svg>
							Estoque
						</button>

						<button
							className={`nav-item ${currentTab === 'financeiro' ? 'active' : ''}`}
							onClick={() => setCurrentTab('financeiro')}
						>
							<svg className="nav-icon" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
								<line x1="12" y1="1" x2="12" y2="23" />
								<path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6" />
							</svg>
							Financeiro
						</button>

						<button
							className={`nav-item ${currentTab === 'aprovacoes' ? 'active' : ''}`}
							onClick={() => setCurrentTab('aprovacoes')}
						>
							<svg className="nav-icon" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
								<polyline points="9 11 12 14 22 4" />
								<path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11" />
							</svg>
							Aprovações
						</button>

						<button
							className={`nav-item ${currentTab === 'equipe' ? 'active' : ''}`}
							onClick={() => { setCurrentTab('equipe'); setCurrentView('list'); }}
						>
							<svg className="nav-icon" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
								<path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
								<circle cx="9" cy="7" r="4" />
								<path d="M23 21v-2a4 4 0 0 0-3-3.87" />
								<path d="M16 3.13a4 4 0 0 1 0 7.75" />
							</svg>
							Equipe
						</button>

						<button
							className={`nav-item ${currentTab === 'tarefas' ? 'active' : ''}`}
							onClick={() => setCurrentTab('tarefas')}
						>
							<svg className="nav-icon" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
								<polyline points="9 11 12 14 22 4" />
								<path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11" />
							</svg>
							Tarefas
						</button>

						<button
							className={`nav-item ${currentTab === 'agenda' ? 'active' : ''}`}
							onClick={() => setCurrentTab('agenda')}
						>
							<svg className="nav-icon" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
								<rect x="3" y="4" width="18" height="18" rx="2" ry="2" />
								<line x1="16" y1="2" x2="16" y2="6" />
								<line x1="8" y1="2" x2="8" y2="6" />
								<line x1="3" y1="10" x2="21" y2="10" />
							</svg>
							Agenda
						</button>

						<button
							className={`nav-item ${currentTab === 'contratos' ? 'active' : ''}`}
							onClick={() => setCurrentTab('contratos')}
						>
							<svg className="nav-icon" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
								<path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
								<polyline points="14 2 14 8 20 8" />
								<line x1="16" y1="13" x2="8" y2="13" />
								<line x1="16" y1="17" x2="8" y2="17" />
								<polyline points="10 9 9 9 8 9" />
							</svg>
							Contratos
						</button>

						<button
							className={`nav-item ${currentTab === 'fornecedores' ? 'active' : ''}`}
							onClick={() => setCurrentTab('fornecedores')}
						>
							<svg className="nav-icon" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
								<rect x="2" y="7" width="20" height="14" rx="2" ry="2" />
								<path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16" />
							</svg>
							Fornecedores
						</button>

						<button
							className={`nav-item ${currentTab === 'conciliacao' ? 'active' : ''}`}
							onClick={() => setCurrentTab('conciliacao')}
						>
							<svg className="nav-icon" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
								<circle cx="18" cy="18" r="3" />
								<circle cx="6" cy="6" r="3" />
								<path d="M13 6h3a2 2 0 0 1 2 2v7" />
								<path d="M11 18H8a2 2 0 0 1-2-2V9" />
							</svg>
							Conciliação
						</button>
					</nav>
				</div>

				{/* User profile card at the bottom */}
				<div className="user-profile-section">
					<div className="user-avatar">GE</div>
					<div className="user-info">
						<span className="user-name">Gestor de Eventos</span>
						<span className="user-email">gestor@empresa.com</span>
					</div>
				</div>
			</aside>

			{/* Main panel */}
			<main className="main-panel">
				<header className="topbar">
					<TopbarBell onVerAgenda={() => setCurrentTab('agenda')} />
				</header>

				<div className="main-content">
					{currentTab === 'dashboard' ? (
						/* Dashboard view matching landing page mockup */
						<div>
							<div className="dashboard-header">
								<h2>Dashboard</h2>
								<p>Visão geral dos seus eventos</p>
							</div>

							{/* Stats Cards Row */}
							<div className="stats-grid">
								<div className="stat-card">
									<div className="stat-info">
										<div className="stat-label">Eventos Ativos</div>
										<div className="stat-value">8</div>
										<div className="stat-sub">
											<span className="stat-trend positive">↑ +12%</span> vs mês anterior
										</div>
									</div>
									<div className="stat-icon ativos">
										<svg width="20" height="20" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
											<polyline points="20 6 9 17 4 12" />
										</svg>
									</div>
								</div>

								<div className="stat-card">
									<div className="stat-info">
										<div className="stat-label">Orçamento Total</div>
										<div className="stat-value" style={{ fontSize: '1.65rem', whiteSpace: 'nowrap' }}>R$ 1.2M</div>
										<div className="stat-sub">Utilizado: <strong>R$ 896K (75%)</strong></div>
									</div>
									<div className="stat-icon disponiveis">
										<svg width="20" height="20" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
											<polyline points="23 6 13.5 15.5 8.5 10.5 1 18" />
											<polyline points="17 6 23 6 23 12" />
										</svg>
									</div>
								</div>

								<div className="stat-card">
									<div className="stat-info">
										<div className="stat-label">Participantes</div>
										<div className="stat-value">1,130</div>
										<div className="stat-sub">
											<span className="stat-trend positive">↑ +8%</span> vs previsão
										</div>
									</div>
									<div className="stat-icon alocados">
										<svg width="20" height="20" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
											<path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
											<circle cx="9" cy="7" r="4" />
											<path d="M23 21v-2a4 4 0 0 0-3-3.87" />
											<path d="M16 3.13a4 4 0 0 1 0 7.75" />
										</svg>
									</div>
								</div>

								<div className="stat-card">
									<div className="stat-info">
										<div className="stat-label">Tarefas Pendentes</div>
										<div className="stat-value">23</div>
										<div className="stat-sub">
											<span className="stat-trend warning">⚠ 3 atrasadas</span>
										</div>
									</div>
									<div className="stat-icon alocados" style={{ backgroundColor: '#fff7ed', color: '#ea580c' }}>
										<svg width="20" height="20" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
											<circle cx="12" cy="12" r="10" />
											<polyline points="12 6 12 12 16 14" />
										</svg>
									</div>
								</div>
							</div>

							{/* Row 1 Widgets: Chart & Task Status */}
							<div className="dashboard-grid-row-1">
								<div className="content-card" style={{ marginBottom: 0 }}>
									<h3 className="widget-title">Orçamento: Previsto vs Realizado</h3>

									<div className="chart-container">
										<div className="chart-y-axis">
											<span>80000</span>
											<span>60000</span>
											<span>40000</span>
											<span>20000</span>
											<span>0</span>
										</div>

										<div className="chart-grid-lines">
											<div className="chart-grid-line" />
											<div className="chart-grid-line" />
											<div className="chart-grid-line" />
											<div className="chart-grid-line" />
											<div className="chart-grid-line" />
										</div>

										<div className="chart-bars-area">
											<div className="chart-month-group">
												<div className="chart-bars-pair">
													<div className="chart-bar previsto" style={{ height: '56%' }} />
													<div className="chart-bar realizado" style={{ height: '52%' }} />
												</div>
												<span className="chart-month-label">Jan</span>
											</div>

											<div className="chart-month-group">
												<div className="chart-bars-pair">
													<div className="chart-bar previsto" style={{ height: '65%' }} />
													<div className="chart-bar realizado" style={{ height: '62%' }} />
												</div>
												<span className="chart-month-label">Fev</span>
											</div>

											<div className="chart-month-group">
												<div className="chart-bars-pair">
													<div className="chart-bar previsto" style={{ height: '60%' }} />
													<div className="chart-bar realizado" style={{ height: '61%' }} />
												</div>
												<span className="chart-month-label">Mar</span>
											</div>

											<div className="chart-month-group">
												<div className="chart-bars-pair">
													<div className="chart-bar previsto" style={{ height: '76%' }} />
													<div className="chart-bar realizado" style={{ height: '72%' }} />
												</div>
												<span className="chart-month-label">Abr</span>
											</div>
										</div>
									</div>
								</div>

								<div className="content-card" style={{ marginBottom: 0 }}>
									<h3 className="widget-title">Status das Tarefas</h3>

									<div className="progress-list">
										<div>
											<div className="progress-item-header">
												<span className="progress-item-label">Concluídas</span>
												<span className="progress-item-value">34 tarefas</span>
											</div>
											<div className="progress-bar-bg">
												<div className="progress-bar-fill concluidas" style={{ width: '60%' }} />
											</div>
										</div>

										<div>
											<div className="progress-item-header">
												<span className="progress-item-label">Em Andamento</span>
												<span className="progress-item-value">12 tarefas</span>
											</div>
											<div className="progress-bar-bg">
												<div className="progress-bar-fill andamento" style={{ width: '22%' }} />
											</div>
										</div>

										<div>
											<div className="progress-item-header">
												<span className="progress-item-label">Pendentes</span>
												<span className="progress-item-value">8 tarefas</span>
											</div>
											<div className="progress-bar-bg">
												<div className="progress-bar-fill pendentes" style={{ width: '14%' }} />
											</div>
										</div>

										<div>
											<div className="progress-item-header">
												<span className="progress-item-label">Atrasadas</span>
												<span className="progress-item-value">3 tarefas</span>
											</div>
											<div className="progress-bar-bg">
												<div className="progress-bar-fill atrasadas" style={{ width: '6%' }} />
											</div>
										</div>
									</div>
								</div>
							</div>

							{/* Row 2 Widgets: Upcoming Events & Critical Alerts */}
							<div className="dashboard-grid-row-2">
								<div className="content-card" style={{ marginBottom: 0 }}>
									<h3 className="widget-title">Próximos Eventos</h3>

									<div className="event-card">
										<div className="event-card-header">
											<div className="event-card-title">Convenção Anual 2026</div>
											<span className="status-badge planejamento">Planejamento</span>
										</div>

										<div className="event-meta-line">
											<div className="event-meta-item">
												<svg className="event-meta-icon" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
													<rect x="3" y="4" width="18" height="18" rx="2" ry="2" />
													<line x1="16" y1="2" x2="16" y2="6" />
													<line x1="8" y1="2" x2="8" y2="6" />
													<line x1="3" y1="10" x2="21" y2="10" />
												</svg>
												14/05/2026
											</div>
											<div className="event-meta-item">
												<svg className="event-meta-icon" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
													<path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
													<circle cx="9" cy="7" r="4" />
												</svg>
												150 participantes
											</div>
											<div className="event-meta-item">
												<svg className="event-meta-icon" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
													<line x1="12" y1="1" x2="12" y2="23" />
													<path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6" />
												</svg>
												R$ 125K
											</div>
										</div>
									</div>
								</div>

								<div className="content-card" style={{ marginBottom: 0 }}>
									<h3 className="widget-title">Alertas Críticos</h3>

									<div className="alert-box yellow" style={{ border: '1px solid #fee2e2', backgroundColor: '#fef2f2', color: '#991b1b', marginBottom: 0 }}>
										<div className="alert-icon" style={{ color: '#ef4444' }}>
											<svg viewBox="0 0 20 20" fill="currentColor">
												<path fillRule="evenodd" d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" clipRule="evenodd" />
											</svg>
										</div>
										<div className="alert-content">
											<p style={{ fontSize: '0.85rem', fontWeight: '500' }}>
												Orçamento do evento 'Workshop de Liderança' ultrapassou 5%
											</p>
										</div>
									</div>
								</div>
							</div>

							{/* Float Help Button */}
							<button className="help-floating-btn">?</button>
						</div>
					) : currentTab === 'equipe' ? (
						/* Teams Tab View (previously implemented) */
						<div className="container" style={{ padding: 0 }}>
							<div className="header-section" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
								<div className="title-area">
									<h1 style={{ fontSize: '2rem', fontWeight: 700, color: '#111827', margin: 0 }}>Gestão de Equipe</h1>
									<p style={{ color: '#6b7280', fontSize: '0.95rem', margin: '0.25rem 0 0 0' }}>Gerencie os membros da equipe e suas alocações</p>
								</div>
								<div className="tab-nav">
									<button
										className={`tab-button ${activeTab === 'funcionarios' ? 'active' : ''}`}
										onClick={() => setActiveTab('funcionarios')}
									>
										Funcionários
									</button>
									<button
										className={`tab-button ${activeTab === 'equipes' ? 'active' : ''}`}
										onClick={() => setActiveTab('equipes')}
									>
										Equipes
									</button>
								</div>
							</div>

							{currentView === 'list' && (
								<div className="stats-grid">
									<div className="stat-card">
										<div className="stat-info">
											<div className="stat-label">Funcionários Ativos</div>
											<div className="stat-value">{totalAtivos}</div>
											<div className="stat-sub">Inativos: {totalInativos}</div>
										</div>
										<div className="stat-icon ativos">
											<svg width="20" height="20" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
												<path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
												<circle cx="9" cy="7" r="4" />
											</svg>
										</div>
									</div>

									<div className="stat-card">
										<div className="stat-info">
											<div className="stat-label">Disponíveis</div>
											<div className="stat-value">{totalDisponiveis}</div>
											<div className="stat-sub">Prontos para alocação</div>
										</div>
										<div className="stat-icon disponiveis">
											<svg width="20" height="20" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
												<path d="M22 11.08V12a10 10 0 1 1-5.93-9.14" />
												<polyline points="22 4 12 14.01 9 11.01" />
											</svg>
										</div>
									</div>

									<div className="stat-card">
										<div className="stat-info">
											<div className="stat-label">Alocados</div>
											<div className="stat-value">{totalAlocados}</div>
											<div className="stat-sub">Em equipe no evento</div>
										</div>
										<div className="stat-icon alocados">
											<svg width="20" height="20" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
												<path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
												<circle cx="9" cy="7" r="4" />
												<path d="M23 21v-2a4 4 0 0 0-3-3.87" />
												<path d="M16 3.13a4 4 0 0 1 0 7.75" />
											</svg>
										</div>
									</div>

									<div className="stat-card">
										<div className="stat-info">
											<div className="stat-label">Equipes Ativas</div>
											<div className="stat-value">{totalEquipes}</div>
											<div className="stat-sub">No evento corrente</div>
										</div>
										<div className="stat-icon equipes">
											<svg width="20" height="20" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
												<rect x="3" y="3" width="7" height="7" />
												<rect x="14" y="3" width="7" height="7" />
												<rect x="14" y="14" width="7" height="7" />
												<rect x="3" y="14" width="7" height="7" />
											</svg>
										</div>
									</div>
								</div>
							)}

							{currentView === 'list' ? (
								activeTab === 'funcionarios' ? (
									<div className="content-card">
										<div className="card-header">
											<div className="card-header-left">
												<h2 className="card-title">Cadastro de Funcionários</h2>
												<button
													className={`toggle-button ${showInativos ? 'active' : ''}`}
													onClick={() => setShowInativos(!showInativos)}
												>
													<svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2" style={{ marginRight: '4px' }}>
														<polygon points="22 3 2 3 10 12.46 10 19 14 21 14 12.46 22 3" />
													</svg>
													Mostrar inativos
												</button>
											</div>
											<button className="action-btn" onClick={() => { resetForm(); setCurrentView('create'); }}>
												<svg width="18" height="18" fill="none" stroke="currentColor" strokeWidth="2">
													<path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
													<circle cx="9" cy="7" r="4" />
													<line x1="19" y1="8" x2="19" y2="14" />
													<line x1="16" y1="11" x2="22" y2="11" />
												</svg>
												Adicionar Funcionário
											</button>
										</div>

										{/* Interpreter Search Box */}
										<div className="search-box-container">
											<div className="search-box">
												<span className="interpreter-badge">Interpreter DSL</span>
												<input
													type="text"
													className="search-input"
													placeholder="Filtro, ex: cargo = coordenador AND lider = true"
													value={searchExpression}
													onChange={(e) => setSearchExpression(e.target.value)}
												/>
												{searchExpression && (
													<button className="btn-outline" style={{ padding: '0.35rem 0.75rem' }} onClick={() => setSearchExpression('')}>
														Limpar
													</button>
												)}
											</div>
											<div className="search-help-text">
												<strong>Busca de Expressão (GoF Interpreter):</strong> Suporta operadores <code>AND</code>, <code>OR</code> e igualdade <code>=</code>. Campos válidos: <code>lider</code> (true/false), <code>cargo</code> (ex: coordenador, técnico a/v, logística), <code>disponibilidade</code> (ex: manhã, integral) e <code>status</code>.
											</div>
										</div>

										<div className="table-container">
											<table className="data-table">
												<thead>
													<tr>
														<th>Nome</th>
														<th>Cargo</th>
														<th>Competências</th>
														<th>Disponibilidade</th>
														<th>Status</th>
														<th>Ações</th>
													</tr>
												</thead>
												<tbody>
													{filteredFuncionarios.length === 0 ? (
														<tr>
															<td colSpan={6} style={{ textAlign: 'center', color: '#6b7280', padding: '2rem' }}>
																Nenhum funcionário encontrado para os filtros selecionados.
															</td>
														</tr>
													) : (
														filteredFuncionarios.map(func => (
															<tr key={func.id} style={{ opacity: func.ativo ? 1 : 0.6 }}>
																<td className="member-info">
																	<div className="member-name">{func.nome}</div>
																	<div className="member-email">{func.email}</div>
																</td>
																<td>{func.cargo}</td>
																<td>
																	{func.competencias.map((comp, idx) => (
																		<span key={idx} className="competency-tag">
																			{comp}
																		</span>
																	))}
																</td>
																<td>
																	<span className="badge availability">{func.disponibilidade}</span>
																</td>
																<td>
																	<span className={`badge ${func.status.toLowerCase().replace(' ', '-')}`}>
																		{func.status}
																	</span>
																</td>
																<td>
																	<button className="edit-link" onClick={() => navigateToEdit(func)}>
																		Editar
																	</button>
																	{!func.ativo && (
																		<button
																			className="edit-link"
																			style={{ marginLeft: '12px', color: '#10b981' }}
																			onClick={() => handleAtivarFuncionario(func.id)}
																		>
																			Reativar
																		</button>
																	)}
																</td>
															</tr>
														))
													)}
												</tbody>
											</table>
										</div>
									</div>
								) : (
									/* Teams Tab View (Screenshot 3) */
									<div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
										{/* Rules Box */}
										<div className="info-box">
											<div className="info-box-title">
												<svg width="18" height="18" fill="none" stroke="currentColor" strokeWidth="2.5" viewBox="0 0 24 24">
													<circle cx="12" cy="12" r="10" />
													<line x1="12" y1="8" x2="12" y2="12" />
													<line x1="12" y1="16" x2="12.01" y2="16" />
												</svg>
												Regras de Equipe:
											</div>
											<ul className="info-box-list">
												<li>Cada equipe deve ter pelo menos um funcionário</li>
												<li>Um funcionário não pode estar em mais de uma equipe do mesmo evento</li>
												<li>Cada equipe pode ter no máximo um líder</li>
												<li>Nome da equipe deve ser único dentro do evento</li>
												<li>Equipes com tarefas ativas não podem ser excluídas</li>
											</ul>
										</div>

										{/* Teams Grid */}
										<div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
											{equipes.length === 0 ? (
												<div style={{ textAlign: 'center', color: '#6b7280', padding: '3rem', border: '1px solid #e5e7eb', borderRadius: '0.75rem', backgroundColor: 'white' }}>
													Nenhuma equipe criada para este evento.
												</div>
											) : (
												equipes.map(eq => {
													const eventName = eventosList.find(ev => ev.id === eq.eventoId)?.nome || eq.eventoId;
													const liderMembro = eq.membros.find(m => m.lider);
													const liderFunc = liderMembro ? funcionarios.find(f => f.id === liderMembro.funcionarioId) : null;
													const liderNome = liderFunc ? liderFunc.nome : 'Sem líder';

													return (
														<div key={eq.id} className="team-list-card">
															<div className="team-list-card-header">
																<h3 className="team-list-card-title">{eq.nome}</h3>
																<span className="team-list-card-membros-badge">
																	{eq.membros.length} {eq.membros.length === 1 ? 'membro' : 'membros'}
																</span>
															</div>
															<div className="team-list-card-subtitle">
																Evento: {eventName}
															</div>
															<div className="team-list-card-leader-line">
																<svg width="16" height="16" fill="#fbbf24" stroke="#d97706" strokeWidth="1.5" viewBox="0 0 24 24">
																	<polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2" />
																</svg>
																<strong>Líder:</strong> {liderNome}
															</div>

															<div className="team-list-card-members-grid">
																{eq.membros.map(m => {
																	const func = funcionarios.find(f => f.id === m.funcionarioId);
																	if (!func) return null;
																	const initials = func.nome.split(/\s+/).map(n => n[0]).join('').substring(0, 2).toUpperCase();

																	return (
																		<div key={m.funcionarioId} className={`member-profile-card ${m.lider ? 'is-leader' : ''}`}>
																			<div className="member-avatar-circle">
																				{initials}
																			</div>
																			<div className="member-profile-info">
																				<span className="member-profile-name">
																					{func.nome}
																					{m.lider && (
																						<svg width="12" height="12" fill="#fbbf24" stroke="#d97706" viewBox="0 0 24 24" style={{ marginLeft: '2px' }}>
																							<polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2" />
																						</svg>
																					)}
																				</span>
																				<span className="member-profile-role">{func.cargo}</span>
																			</div>
																		</div>
																	);
																})}
															</div>

															<div className="team-card-action-btn-container">
																<button className="team-card-action-btn" onClick={() => navigateToEditTeam(eq)}>
																	Gerenciar Equipe
																</button>
															</div>
														</div>
													);
												})
											)}

											{/* Dotted Create Team Card */}
											<div className="create-team-dotted-card" onClick={navigateToCreateTeam}>
												<svg className="create-team-dotted-card-icon" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
													<path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
													<circle cx="9" cy="7" r="4" />
													<line x1="19" y1="8" x2="19" y2="14" />
													<line x1="16" y1="11" x2="22" y2="11" />
												</svg>
												<span>Criar Nova Equipe</span>
											</div>
										</div>
									</div>
								)
							) : currentView === 'create-team' ? (
								/* Create Team View (Screenshot 4) */
								<div className="content-card">
									<button className="back-link" onClick={() => setCurrentView('list')}>
										<svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2" style={{ marginRight: '6px' }}>
											<line x1="19" y1="12" x2="5" y2="12" />
											<polyline points="12 19 5 12 12 5" />
										</svg>
										Voltar para Equipe
									</button>

									<h2 style={{ fontSize: '1.5rem', fontWeight: 700, color: '#111827', marginBottom: '1.5rem' }}>Nova Equipe</h2>

									{teamErrors && (
										<div className="error-message" style={{ padding: '0.75rem', backgroundColor: '#fee2e2', borderRadius: '0.375rem', marginBottom: '1.25rem', border: '1px solid #fca5a5' }}>
											{teamErrors}
										</div>
									)}

									<form onSubmit={handleCreateEquipe}>
										<div className="form-grid" style={{ gridTemplateColumns: '1fr 1fr', marginBottom: '1.5rem' }}>
											<div className="form-group">
												<label>Evento *</label>
												<select
													className="form-select"
													value={teamEventoId}
													onChange={(e) => setTeamEventoId(e.target.value)}
												>
													<option value="">Selecione um evento...</option>
													{eventosList.map(ev => (
														<option key={ev.id} value={ev.id}>{ev.nome}</option>
													))}
												</select>
											</div>

											<div className="form-group">
												<label>Nome da Equipe *</label>
												<input
													type="text"
													className="form-input"
													placeholder="Ex: Equipe de Logística"
													value={teamNome}
													onChange={(e) => setTeamNome(e.target.value)}
												/>
												<span className="form-help-text">Nome deve ser único dentro do evento</span>
											</div>
										</div>

										{/* Member Selection Section */}
										<div style={{ marginBottom: '1.5rem' }}>
											<div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.5rem' }}>
												<h3 style={{ fontSize: '1rem', fontWeight: 700, color: '#374151', margin: 0 }}>Membros da Equipe</h3>
												<span className="team-list-card-membros-badge">
													{selectedMembros.length} selecionados
												</span>
											</div>
											<p style={{ fontSize: '0.85rem', color: '#6b7280', margin: '0 0 0.75rem 0' }}>
												Selecione os funcionários que farão parte desta equipe
											</p>

											<div className="member-selection-grid">
												{funcionarios
													.filter(f => f.ativo)
													.map(f => {
														const isSelected = selectedMembros.includes(f.id);
														const initials = f.nome.split(/\s+/).map(n => n[0]).join('').substring(0, 2).toUpperCase();

														return (
															<div
																key={f.id}
																className={`member-select-card ${isSelected ? 'selected' : ''}`}
																onClick={() => {
																	if (selectedMembros.includes(f.id)) {
																		setSelectedMembros(selectedMembros.filter(id => id !== f.id));
																		if (teamLiderId === f.id) setTeamLiderId('');
																	} else {
																		setSelectedMembros([...selectedMembros, f.id]);
																	}
																}}
															>
																<input
																	type="checkbox"
																	className="member-select-checkbox"
																	checked={isSelected}
																	onChange={() => {}} // handled by click on card
																/>
																<div className="member-avatar-circle" style={{ marginLeft: '0.5rem', marginRight: '0.5rem' }}>
																	{initials}
																</div>
																<div className="member-profile-info">
																	<span className="member-profile-name">{f.nome}</span>
																	<span className="member-profile-role">{f.cargo} ({f.status})</span>
																</div>
															</div>
														);
													})}
											</div>
										</div>

										{/* Leader Selection Section */}
										<div style={{ marginBottom: '1.5rem' }}>
											<h3 style={{ fontSize: '1rem', fontWeight: 700, color: '#374151', margin: '0 0 0.25rem 0' }}>Líder da Equipe (opcional)</h3>
											<p style={{ fontSize: '0.85rem', color: '#6b7280', margin: '0 0 0.75rem 0' }}>
												O líder deve ser um dos membros selecionados
											</p>

											<div className="leader-selection-container">
												<button
													type="button"
													className={`leader-select-btn ${!teamLiderId ? 'selected' : ''}`}
													onClick={() => setTeamLiderId('')}
												>
													Sem líder
												</button>
												{funcionarios
													.filter(f => f.ativo && selectedMembros.includes(f.id))
													.map(f => {
														const isLider = teamLiderId === f.id;
														return (
															<button
																key={f.id}
																type="button"
																className={`leader-select-btn ${isLider ? 'selected' : ''}`}
																onClick={() => setTeamLiderId(f.id)}
															>
																{isLider && (
																	<svg width="14" height="14" fill="#fbbf24" stroke="#d97706" viewBox="0 0 24 24" style={{ marginRight: '2px' }}>
																		<polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2" />
																	</svg>
																)}
																{f.nome}
															</button>
														);
													})}
											</div>
										</div>

										{/* Rules Box inside new team card */}
										<div className="alert-box blue" style={{ marginBottom: '1.5rem' }}>
											<div className="alert-icon">
												<svg viewBox="0 0 20 20" fill="currentColor">
													<path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clipRule="evenodd" />
												</svg>
											</div>
											<div className="alert-content">
												<h4>Regras de Validação:</h4>
												<ul>
													<li>A equipe deve estar associada a um evento válido</li>
													<li>Nome da equipe deve ser único dentro do evento</li>
													<li>Pelo menos um funcionário deve ser selecionado</li>
													<li>Funcionários não podem estar em mais de uma equipe do mesmo evento</li>
													<li>O líder deve ser um membro da equipe</li>
													<li>Equipes com tarefas ativas não podem ser excluídas</li>
													<li>Se remover o líder atual dos membros, outro líder deve ser definido</li>
												</ul>
											</div>
										</div>

										<div className="form-actions">
											<button type="button" className="btn-outline" onClick={() => setCurrentView('list')}>
												Cancelar
											</button>
											<button type="submit" className="action-btn">
												Criar Equipe
											</button>
										</div>
									</form>
								</div>
							) : currentView === 'edit-team' ? (
								/* Edit Team View (Screenshot 5) */
								<div className="content-card">
									<div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
										<button className="back-link" style={{ margin: 0 }} onClick={() => setCurrentView('list')}>
											<svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2" style={{ marginRight: '6px' }}>
												<line x1="19" y1="12" x2="5" y2="12" />
												<polyline points="12 19 5 12 12 5" />
											</svg>
											Voltar para Equipe
										</button>

										{selectedTeamId && (
											<button
												type="button"
												className="btn-outline"
												style={{ color: '#ef4444', borderColor: '#fee2e2', display: 'flex', alignItems: 'center', gap: '0.35rem' }}
												onClick={() => {
													if (window.confirm('Tem certeza que deseja excluir esta equipe?')) {
														handleRemoveEquipe(selectedTeamId);
													}
												}}
											>
												<svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2">
													<polyline points="3 6 5 6 21 6" />
													<path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" />
													<line x1="10" y1="11" x2="10" y2="17" />
													<line x1="14" y1="11" x2="14" y2="17" />
												</svg>
												Excluir Equipe
											</button>
										)}
									</div>

									<h2 style={{ fontSize: '1.5rem', fontWeight: 700, color: '#111827', marginBottom: '1.5rem' }}>Editar Equipe</h2>

									{teamErrors && (
										<div className="error-message" style={{ padding: '0.75rem', backgroundColor: '#fee2e2', borderRadius: '0.375rem', marginBottom: '1.25rem', border: '1px solid #fca5a5' }}>
											{teamErrors}
										</div>
									)}

									<form onSubmit={handleEditEquipe}>
										<div className="form-grid" style={{ gridTemplateColumns: '1fr 1fr', marginBottom: '1.5rem' }}>
											<div className="form-group">
												<label>Evento *</label>
												<select
													className="form-select"
													value={teamEventoId}
													onChange={(e) => setTeamEventoId(e.target.value)}
												>
													<option value="">Selecione um evento...</option>
													{eventosList.map(ev => (
														<option key={ev.id} value={ev.id}>{ev.nome}</option>
													))}
												</select>
											</div>

											<div className="form-group">
												<label>Nome da Equipe *</label>
												<input
													type="text"
													className="form-input"
													value={teamNome}
													onChange={(e) => setTeamNome(e.target.value)}
												/>
												<span className="form-help-text">Nome deve ser único dentro do evento</span>
											</div>
										</div>

										{/* Member Selection Section */}
										<div style={{ marginBottom: '1.5rem' }}>
											<div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.5rem' }}>
												<h3 style={{ fontSize: '1rem', fontWeight: 700, color: '#374151', margin: 0 }}>Membros da Equipe</h3>
												<span className="team-list-card-membros-badge">
													{selectedMembros.length} selecionados
												</span>
											</div>
											<p style={{ fontSize: '0.85rem', color: '#6b7280', margin: '0 0 0.75rem 0' }}>
												Selecione os funcionários que farão parte desta equipe
											</p>

											<div className="member-selection-grid">
												{funcionarios
													.filter(f => f.ativo)
													.map(f => {
														const isSelected = selectedMembros.includes(f.id);
														const initials = f.nome.split(/\s+/).map(n => n[0]).join('').substring(0, 2).toUpperCase();

														return (
															<div
																key={f.id}
																className={`member-select-card ${isSelected ? 'selected' : ''}`}
																onClick={() => {
																	if (selectedMembros.includes(f.id)) {
																		setSelectedMembros(selectedMembros.filter(id => id !== f.id));
																		if (teamLiderId === f.id) setTeamLiderId('');
																	} else {
																		setSelectedMembros([...selectedMembros, f.id]);
																	}
																}}
															>
																<input
																	type="checkbox"
																	className="member-select-checkbox"
																	checked={isSelected}
																	onChange={() => {}} // handled by click on card
																/>
																<div className="member-avatar-circle" style={{ marginLeft: '0.5rem', marginRight: '0.5rem' }}>
																	{initials}
																</div>
																<div className="member-profile-info">
																	<span className="member-profile-name">{f.nome}</span>
																	<span className="member-profile-role">{f.cargo} ({f.status})</span>
																</div>
															</div>
														);
													})}
											</div>
										</div>

										{/* Leader Selection Section */}
										<div style={{ marginBottom: '1.5rem' }}>
											<h3 style={{ fontSize: '1rem', fontWeight: 700, color: '#374151', margin: '0 0 0.25rem 0' }}>Líder da Equipe (opcional)</h3>
											<p style={{ fontSize: '0.85rem', color: '#6b7280', margin: '0 0 0.75rem 0' }}>
												O líder deve ser um dos membros selecionados
											</p>

											<div className="leader-selection-container">
												<button
													type="button"
													className={`leader-select-btn ${!teamLiderId ? 'selected' : ''}`}
													onClick={() => setTeamLiderId('')}
												>
													Sem líder
												</button>
												{funcionarios
													.filter(f => f.ativo && selectedMembros.includes(f.id))
													.map(f => {
														const isLider = teamLiderId === f.id;
														return (
															<button
																key={f.id}
																type="button"
																className={`leader-select-btn ${isLider ? 'selected' : ''}`}
																onClick={() => setTeamLiderId(f.id)}
															>
																{isLider && (
																	<svg width="14" height="14" fill="#fbbf24" stroke="#d97706" viewBox="0 0 24 24" style={{ marginRight: '2px' }}>
																		<polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2" />
																	</svg>
																)}
																{f.nome}
															</button>
														);
													})}
											</div>
										</div>

										{/* Rules Box inside edit team card */}
										<div className="alert-box blue" style={{ marginBottom: '1.5rem' }}>
											<div className="alert-icon">
												<svg viewBox="0 0 20 20" fill="currentColor">
													<path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clipRule="evenodd" />
												</svg>
											</div>
											<div className="alert-content">
												<h4>Regras de Validação:</h4>
												<ul>
													<li>A equipe deve estar associada a um evento válido</li>
													<li>Nome da equipe deve ser único dentro do evento</li>
													<li>Pelo menos um funcionário deve ser selecionado</li>
													<li>Funcionários não podem estar em mais de uma equipe do mesmo evento</li>
													<li>O líder deve ser um membro da equipe</li>
													<li>Equipes com tarefas ativas não podem ser excluídas</li>
													<li>Se remover o líder atual dos membros, outro líder deve ser definido</li>
												</ul>
											</div>
										</div>

										<div className="form-actions">
											<button type="button" className="btn-outline" onClick={() => setCurrentView('list')}>
												Cancelar
											</button>
											<button type="submit" className="action-btn">
												Salvar Alterações
											</button>
										</div>
									</form>
								</div>
							) : currentView === 'create' ? (
								/* Create Employee view */
								<div className="content-card">
									<button className="back-link" onClick={() => setCurrentView('list')}>
										<svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2">
											<line x1="19" y1="12" x2="5" y2="12" />
											<polyline points="12 19 5 12 12 5" />
										</svg>
										Voltar para Equipe
									</button>

									<h2 style={{ fontSize: '1.5rem', fontWeight: 700, color: '#111827', marginBottom: '1.5rem' }}>Novo Funcionário</h2>

									<form onSubmit={handleCreateFuncionario}>
										<div className="form-grid">
											<div className="form-group full-width">
												<label>Nome Completo *</label>
												<input
													type="text"
													className="form-input"
													placeholder="Ex: Maria da Silva"
													value={formNome}
													onChange={(e) => setFormNome(e.target.value)}
												/>
												{formErrors.nome && <span className="error-message">{formErrors.nome}</span>}
											</div>

											<div className="form-group">
												<label>Email *</label>
												<input
													type="email"
													className="form-input"
													placeholder="funcionario@empresa.com"
													value={formEmail}
													onChange={(e) => setFormEmail(e.target.value)}
												/>
												{formErrors.email && <span className="error-message">{formErrors.email}</span>}
											</div>

											<div className="form-group">
												<label>Cargo *</label>
												<select className="form-select" value={formCargo} onChange={(e) => setFormCargo(e.target.value)}>
													<option value="">Selecione...</option>
													<option value="Coordenador">Coordenador</option>
													<option value="Técnico A/V">Técnico A/V</option>
													<option value="Logística">Logística</option>
													<option value="Gerente">Gerente</option>
													<option value="Analista">Analista</option>
													<option value="Assistente">Assistente</option>
													<option value="Técnico">Técnico</option>
													<option value="Garçom">Garçom</option>
												</select>
												{formErrors.cargo && <span className="error-message">{formErrors.cargo}</span>}
											</div>

											<div className="form-group full-width">
												<label>Disponibilidade *</label>
												<select className="form-select" value={formDisponibilidade} onChange={(e) => setFormDisponibilidade(e.target.value)}>
													<option value="INTEGRAL">Integral</option>
													<option value="MANHÃ">Manhã</option>
													<option value="TARDE">Tarde</option>
													<option value="NOITE">Noite</option>
												</select>
											</div>

											<div className="form-group full-width">
												<label>Competências (separadas por vírgula)</label>
												<input
													type="text"
													className="form-input"
													placeholder="Ex: Gestão, Liderança, Eventos Corporativos"
													value={formCompetencias}
													onChange={(e) => setFormCompetencias(e.target.value)}
												/>
												<span className="form-help-text">Digite as habilidades separadas por vírgula</span>
											</div>
										</div>

										<div className="alert-box blue">
											<div className="alert-icon">
												<svg viewBox="0 0 20 20" fill="currentColor">
													<path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clipRule="evenodd" />
												</svg>
											</div>
											<div className="alert-content">
												<h4>Regras de Validação:</h4>
												<ul>
													<li>Nome deve ter no mínimo 3 caracteres, sem números</li>
													<li>Cargo deve ser selecionado da lista predefinida</li>
													<li>Disponibilidade segue o padrão: MANHÃ, TARDE, NOITE ou INTEGRAL</li>
													<li>Funcionários vinculados a equipes não podem ser inativados</li>
													<li>A exclusão é lógica (soft delete), mantendo histórico</li>
													<li>Funcionários inativos não podem ser editados</li>
												</ul>
											</div>
										</div>

										<div className="form-actions">
											<button type="button" className="btn-outline" onClick={() => setCurrentView('list')}>
												Cancelar
											</button>
											<button type="submit" className="action-btn">
												<svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2" style={{ marginRight: '4px' }}>
													<path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2z" />
													<polyline points="17 21 17 13 7 13 7 21" />
													<polyline points="7 3 7 8 15 8" />
												</svg>
												Cadastrar Funcionário
											</button>
										</div>
									</form>
								</div>
							) : (
								/* Edit Employee view */
								<div className="content-card">
									<div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
										<button className="back-link" onClick={() => setCurrentView('list')}>
											<svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2">
												<line x1="19" y1="12" x2="5" y2="12" />
												<polyline points="12 19 5 12 12 5" />
											</svg>
											Voltar para Equipe
										</button>

										{selectedFuncId &&
											funcionarios.find(f => f.id === selectedFuncId)?.ativo &&
											!equipes.some(eq => eq.membros.some(m => m.funcionarioId === selectedFuncId)) && (
												<button
													type="button"
													className="btn-outline"
													style={{ color: '#ef4444', borderColor: '#fee2e2', display: 'flex', alignItems: 'center', gap: '0.25rem' }}
													onClick={() => triggerInativarConfirmation(selectedFuncId)}
												>
													<svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2">
														<polyline points="3 6 5 6 21 6" />
														<path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" />
														<line x1="10" y1="11" x2="10" y2="17" />
														<line x1="14" y1="11" x2="14" y2="17" />
													</svg>
													Inativar
												</button>
											)}
									</div>

									<h2 style={{ fontSize: '1.5rem', fontWeight: 700, color: '#111827', marginBottom: '1.5rem' }}>Editar Funcionário</h2>

									{selectedFuncId && equipes.some(eq => eq.membros.some(m => m.funcionarioId === selectedFuncId)) && (
										<div className="alert-box yellow">
											<div className="alert-icon">
												<svg viewBox="0 0 20 20" fill="currentColor">
													<path fillRule="evenodd" d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" clipRule="evenodd" />
												</svg>
											</div>
											<div className="alert-content">
												<h4>Funcionário Vinculado a Equipe</h4>
												<p style={{ fontSize: '0.85rem' }}>
													Este funcionário está vinculado a uma ou mais equipes. Para inativá-lo, remova-o de todas as equipes primeiro.
												</p>
											</div>
										</div>
									)}

									<form onSubmit={handleEditFuncionario}>
										<div className="form-grid">
											<div className="form-group full-width">
												<label>Nome Completo *</label>
												<input
													type="text"
													className="form-input"
													value={formNome}
													onChange={(e) => setFormNome(e.target.value)}
													disabled={selectedFuncId ? !funcionarios.find(f => f.id === selectedFuncId)?.ativo : false}
												/>
												{formErrors.nome && <span className="error-message">{formErrors.nome}</span>}
											</div>

											<div className="form-group">
												<label>Email *</label>
												<input
													type="email"
													className="form-input"
													value={formEmail}
													onChange={(e) => setFormEmail(e.target.value)}
													disabled={selectedFuncId ? !funcionarios.find(f => f.id === selectedFuncId)?.ativo : false}
												/>
												{formErrors.email && <span className="error-message">{formErrors.email}</span>}
											</div>

											<div className="form-group">
												<label>Cargo *</label>
												<select
													className="form-select"
													value={formCargo}
													onChange={(e) => setFormCargo(e.target.value)}
													disabled={selectedFuncId ? !funcionarios.find(f => f.id === selectedFuncId)?.ativo : false}
												>
													<option value="Coordenador">Coordenador</option>
													<option value="Técnico A/V">Técnico A/V</option>
													<option value="Logística">Logística</option>
													<option value="Gerente">Gerente</option>
													<option value="Analista">Analista</option>
													<option value="Assistente">Assistente</option>
													<option value="Técnico">Técnico</option>
													<option value="Garçom">Garçom</option>
												</select>
												{formErrors.cargo && <span className="error-message">{formErrors.cargo}</span>}
											</div>

											<div className="form-group full-width">
												<label>Disponibilidade *</label>
												<select
													className="form-select"
													value={formDisponibilidade}
													onChange={(e) => setFormDisponibilidade(e.target.value)}
													disabled={selectedFuncId ? !funcionarios.find(f => f.id === selectedFuncId)?.ativo : false}
												>
													<option value="INTEGRAL">Integral</option>
													<option value="MANHÃ">Manhã</option>
													<option value="TARDE">Tarde</option>
													<option value="NOITE">Noite</option>
												</select>
											</div>

											<div className="form-group full-width">
												<label>Competências (separadas por vírgula)</label>
												<input
													type="text"
													className="form-input"
													value={formCompetencias}
													onChange={(e) => setFormCompetencias(e.target.value)}
													disabled={selectedFuncId ? !funcionarios.find(f => f.id === selectedFuncId)?.ativo : false}
												/>
												<span className="form-help-text">Digite as habilidades separadas por vírgula</span>
											</div>
										</div>

										<div className="alert-box blue">
											<div className="alert-icon">
												<svg viewBox="0 0 20 20" fill="currentColor">
													<path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clipRule="evenodd" />
												</svg>
											</div>
											<div className="alert-content">
												<h4>Regras de Validação:</h4>
												<ul>
													<li>Nome deve ter no mínimo 3 caracteres, sem números</li>
													<li>Cargo deve ser selecionado da lista predefinida</li>
													<li>Disponibilidade segue o padrão: MANHÃ, TARDE, NOITE ou INTEGRAL</li>
													<li>Funcionários vinculados a equipes não podem ser inativados</li>
													<li>A exclusão é lógica (soft delete), mantendo histórico</li>
													<li>Funcionários inativos não podem ser editados</li>
												</ul>
											</div>
										</div>

										{selectedFuncId && (
											<div className="metadata-block">
												<div className="metadata-item">
													<div className="meta-label">ID do Funcionário</div>
													<div className="meta-value">{selectedFuncId}</div>
												</div>
												<div className="metadata-item">
													<div className="meta-label">Status</div>
													<div className="meta-value" style={{ display: 'flex', alignItems: 'center', gap: '0.2rem' }}>
														{funcionarios.find(f => f.id === selectedFuncId)?.ativo ? (
															<>
																<span style={{ color: '#10b981' }}>✓</span> Ativo
															</>
														) : (
															<>
																<span style={{ color: '#ef4444' }}>✗</span> Inativo
															</>
														)}
													</div>
												</div>
												<div className="metadata-item">
													<div className="meta-label">Criado em</div>
													<div className="meta-value">
														{funcionarios.find(f => f.id === selectedFuncId)?.criadoEm}
													</div>
												</div>
												<div className="metadata-item">
													<div className="meta-label">Última atualização</div>
													<div className="meta-value">
														{funcionarios.find(f => f.id === selectedFuncId)?.atualizadoEm}
													</div>
												</div>
											</div>
										)}

										<div className="form-actions">
											<button type="button" className="btn-outline" onClick={() => setCurrentView('list')}>
												Cancelar
											</button>
											{selectedFuncId && funcionarios.find(f => f.id === selectedFuncId)?.ativo && (
												<button type="submit" className="action-btn">
													<svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2" style={{ marginRight: '4px' }}>
														<path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2z" />
														<polyline points="17 21 17 13 7 13 7 21" />
														<polyline points="7 3 7 8 15 8" />
													</svg>
													Salvar Alterações
												</button>
											)}
										</div>
									</form>
								</div>
							)}
						</div>
					) : currentTab === 'tarefas' ? (
						<TarefasApp />
					) : currentTab === 'agenda' ? (
						<AgendaModule />
					) : currentTab === 'fornecedores' ? (
						<FornecedoresSection />
					) : currentTab === 'contratos' ? (
						<ContratosSection />
					) : (
						<div className="content-card" style={{ textAlign: 'center', padding: '3rem', color: '#6b7280' }}>
							<svg width="64" height="64" fill="none" stroke="currentColor" strokeWidth="1.5" viewBox="0 0 24 24" style={{ marginBottom: '1.5rem', color: '#9ca3af' }}>
								<circle cx="12" cy="12" r="10" />
								<line x1="12" y1="8" x2="12" y2="12" />
								<line x1="12" y1="16" x2="12.01" y2="16" />
							</svg>
							<h2 style={{ fontSize: '1.25rem', fontWeight: 700, color: '#111827', marginBottom: '0.5rem' }}>Módulo em Desenvolvimento</h2>
							<p style={{ fontSize: '0.9rem' }}>Esta área de apresentação está temporariamente sob construção. Escolha &quot;Dashboard&quot;, &quot;Equipe&quot;, &quot;Tarefas&quot;, &quot;Agenda&quot;, &quot;Fornecedores&quot; ou &quot;Contratos&quot; no menu lateral.</p>
						</div>
					)}
				</div>
			</main>

			{/* Confirmation Modal */}
			{showInativarModal && (
				<div className="modal-overlay">
					<div className="modal-container">
						<div className="modal-body">
							<div className="modal-icon-circle">
								<svg width="24" height="24" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
									<circle cx="12" cy="12" r="10" />
									<line x1="4.93" y1="4.93" x2="19.07" y2="19.07" />
								</svg>
							</div>
							<div className="modal-content-text">
								<h3 className="modal-title">Inativar Funcionário</h3>
								<p className="modal-description">
									Tem certeza que deseja inativar este funcionário? A exclusão é lógica (soft delete), então o registro permanecerá no sistema, mas não poderá mais ser editado ou alocado em equipes.
								</p>
							</div>
						</div>
						<div className="modal-actions">
							<button className="modal-btn-cancelar" onClick={() => { setShowInativarModal(false); setFuncToInativar(null); }}>
								Cancelar
							</button>
							<button className="modal-btn-confirm" onClick={confirmInativarFuncionario}>
								Inativar
							</button>
						</div>
					</div>
				</div>
			)}
		</div>
		</PlanningDataProvider>
		</AgendaProvider>
	);
}
