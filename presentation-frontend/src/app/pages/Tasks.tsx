import { useState } from "react";
import { useNavigate } from "react-router";
import { DndProvider, useDrag, useDrop } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import {
  Plus,
  LayoutList,
  Kanban,
  CheckCircle,
  Circle,
  Clock,
  AlertTriangle,
  GitBranch,
  Play,
  CheckCheck,
  Trash2,
  Calendar,
  Network,
} from "lucide-react";
import { useTasks, type TaskStatus, type Task } from "../contexts/TasksContext";
import { toast } from "sonner";
import { TaskFlowchart } from "../components/TaskFlowchart";

const statusConfig = {
  completed: { label: "Concluída", icon: CheckCircle, color: "text-green-600", bg: "bg-green-50", border: "border-green-200" },
  in_progress: { label: "Em Andamento", icon: Clock, color: "text-blue-600", bg: "bg-blue-50", border: "border-blue-200" },
  pending: { label: "Pendente", icon: Circle, color: "text-gray-600", bg: "bg-gray-50", border: "border-gray-200" },
  overdue: { label: "Atrasada", icon: AlertTriangle, color: "text-red-600", bg: "bg-red-50", border: "border-red-200" },
  blocked: { label: "Bloqueada", icon: AlertTriangle, color: "text-orange-600", bg: "bg-orange-50", border: "border-orange-200" },
};

function TaskCard({ task, onClick, selected, tasks }: { task: Task; onClick: () => void; selected: boolean; tasks: Task[] }) {
  const config = statusConfig[task.status];
  const Icon = config.icon;
  const [{ isDragging }, drag] = useDrag(() => ({
    type: "TASK",
    item: { id: task.id, status: task.status },
    collect: (monitor) => ({ isDragging: !!monitor.isDragging() }),
  }));

  const hasDependencies = task.dependencies.length > 0;

  // Calcular se é potencialmente atrasada: alguma predecessora tem data final posterior ao início (ou final) desta
  const potentiallyDelayed = task.dependencies.some(depId => {
    const dep = tasks.find(t => t.id === depId);
    if (!dep) return false;
    if (task.startDate && dep.dueDate && new Date(dep.dueDate) > new Date(task.startDate)) return true;
    if (task.dueDate && dep.dueDate && new Date(dep.dueDate) > new Date(task.dueDate)) return true;
    return false;
  });

  return (
    <div
      ref={(node) => { drag(node); }}
      onClick={onClick}
      className={`bg-white rounded-lg border-2 p-4 cursor-pointer transition-all ${
        isDragging ? "opacity-50" : "opacity-100"
      } ${selected ? "border-blue-500 shadow-md" : "border-gray-200 hover:border-gray-300"}`}
    >
      <div className="flex items-start gap-3">
        <Icon className={`w-5 h-5 ${config.color} flex-shrink-0 mt-0.5`} />
        <div className="flex-1 min-w-0">
          <h4 className="font-medium text-gray-900 line-clamp-2">{task.title}</h4>

          <div className="flex flex-wrap gap-2 mt-2">
             <span className={`px-2 py-0.5 rounded text-xs font-medium ${config.bg} ${config.color}`}>
              {config.label}
            </span>
            <span className="flex items-center gap-1 text-xs text-gray-500 bg-gray-100 px-2 py-0.5 rounded">
              <Calendar className="w-3 h-3" />
              {task.dueDate ? new Date(task.dueDate + "T00:00:00").toLocaleDateString("pt-BR") : "—"}
            </span>
          </div>

          <div className="mt-3 flex items-center gap-2">
            <div className="flex -space-x-2">
              {task.assignees.map((assignee, idx) => (
                <div key={idx} title={assignee} className="w-6 h-6 rounded-full bg-blue-100 border-2 border-white flex items-center justify-center text-blue-700 text-xs font-bold">
                  {assignee.charAt(0)}
                </div>
              ))}
            </div>
            {task.assignees.length === 0 && (
               <span className="text-xs text-red-500 font-medium">Sem responsável</span>
            )}
          </div>

          {hasDependencies && (
            <div className="flex items-center gap-1.5 mt-3 text-xs text-gray-600 bg-gray-50 p-1.5 rounded">
              <GitBranch className="w-3 h-3 text-gray-400" />
              <span>Depende de {task.dependencies.length} tarefa(s)</span>
            </div>
          )}

          {potentiallyDelayed && task.status !== "completed" && (
            <div className="mt-2 flex items-center gap-1.5 text-xs text-amber-700 bg-amber-50 p-1.5 rounded border border-amber-200">
              <Clock className="w-3 h-3" />
              <span>Potencialmente atrasada (Predecessora impacta datas)</span>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

function KanbanColumn({ title, statusGroup, tasks, allTasks, onTaskClick, selectedTaskId }: {
  title: string;
  statusGroup: TaskStatus[];
  tasks: Task[];
  allTasks: Task[];
  onTaskClick: (id: string) => void;
  selectedTaskId: string | null;
}) {
  const { updateStatus } = useTasks();

  const [{ isOver }, drop] = useDrop(() => ({
    accept: "TASK",
    drop: async (item: { id: string; status: TaskStatus }) => {
      if (statusGroup.includes(item.status)) return;

      const targetStatus = statusGroup[0];

      try {
        await updateStatus(item.id, targetStatus);
      } catch (err: any) {
        // toast já é disparado pelo contexto, o catch só previne crash
      }
    },
    collect: (monitor) => ({
      isOver: !!monitor.isOver(),
    }),
  }));

  const columnTasks = tasks.filter(t => statusGroup.includes(t.status));

  return (
    <div ref={(node) => { drop(node); }} className={`bg-gray-50 rounded-xl p-4 flex flex-col h-full border-2 border-dashed transition-colors ${isOver ? "border-blue-400 bg-blue-50/50" : "border-transparent"}`}>
      <div className="flex items-center justify-between mb-4 px-1">
        <h3 className="font-semibold text-gray-700">{title}</h3>
        <span className="bg-gray-200 text-gray-700 text-xs font-bold px-2 py-1 rounded-full">
          {columnTasks.length}
        </span>
      </div>
      <div className="flex-1 space-y-3 overflow-y-auto">
        {columnTasks.map(task => (
          <TaskCard
            key={task.id}
            task={task}
            tasks={allTasks}
            onClick={() => onTaskClick(task.id)}
            selected={selectedTaskId === task.id}
          />
        ))}
        {columnTasks.length === 0 && (
          <div className="text-center py-8 text-gray-400 text-sm border-2 border-dashed border-gray-200 rounded-lg">
            Solte tarefas aqui
          </div>
        )}
      </div>
    </div>
  );
}

export function Tasks() {
  const navigate = useNavigate();
  const { tasks, deleteTask, updateStatus } = useTasks();
  const [view, setView] = useState<"list" | "kanban" | "flowchart">("kanban");
  const [selectedTaskId, setSelectedTaskId] = useState<string | null>(null);
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);

  const selectedTask = tasks.find((t) => t.id === selectedTaskId);
  const getTaskById = (id: string) => tasks.find((t) => t.id === id);

  const handleStatusAction = async (id: string, newStatus: TaskStatus) => {
    try {
      const task = getTaskById(id);
      if (newStatus === "in_progress" && task?.assignees.length === 0) {
        toast.error("Sem responsável", { description: "Atribua um funcionário antes de iniciar a tarefa." });
        return;
      }
      await updateStatus(id, newStatus);
    } catch {}
  };

  const handleDelete = async () => {
    if (!selectedTaskId) return;
    try {
      await deleteTask(selectedTaskId);
      setSelectedTaskId(null);
      setShowDeleteConfirm(false);
    } catch {
      setShowDeleteConfirm(false);
    }
  };

  return (
    <DndProvider backend={HTML5Backend}>
      <div className="tarefas-page max-w-7xl mx-auto w-full flex flex-col">
        <div className="mb-6 flex items-center justify-between shrink-0">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Gestão de Tarefas</h1>
            <p className="text-gray-600 mt-1">
              Acompanhe tarefas com dependências e controle de fluxo
            </p>
          </div>
          <div className="flex items-center gap-4">
            <div className="flex bg-gray-100 p-1 rounded-lg">
              <button
                onClick={() => setView("list")}
                className={`p-2 rounded-md transition-colors ${view === "list" ? "bg-white shadow-sm text-blue-600" : "text-gray-500 hover:text-gray-700"}`}
                title="Visualização em Lista"
              >
                <LayoutList className="w-5 h-5" />
              </button>
              <button
                onClick={() => setView("kanban")}
                className={`p-2 rounded-md transition-colors ${view === "kanban" ? "bg-white shadow-sm text-blue-600" : "text-gray-500 hover:text-gray-700"}`}
                title="Visualização Kanban"
              >
                <Kanban className="w-5 h-5" />
              </button>
              <button
                onClick={() => setView("flowchart")}
                className={`p-2 rounded-md transition-colors ${view === "flowchart" ? "bg-white shadow-sm text-blue-600" : "text-gray-500 hover:text-gray-700"}`}
                title="Visualização em Fluxograma"
              >
                <Network className="w-5 h-5" />
              </button>
            </div>
            <button
              onClick={() => navigate("/tasks/new")}
              className="flex items-center gap-2 bg-blue-600 text-white px-4 py-2.5 rounded-lg font-medium hover:bg-blue-700 shadow-sm"
            >
              <Plus className="w-5 h-5" />
              Nova Tarefa
            </button>
          </div>
        </div>

        <div className="flex-1 min-h-0 flex gap-6">
          {/* Main Content Area */}
          <div className="flex-1 overflow-hidden flex flex-col">
            {view === "kanban" ? (
              <div className="grid grid-cols-1 md:grid-cols-3 gap-6 h-full overflow-hidden">
                <KanbanColumn
                  title="Pendente"
                  statusGroup={["pending", "blocked", "overdue"]}
                  tasks={tasks}
                  allTasks={tasks}
                  onTaskClick={setSelectedTaskId}
                  selectedTaskId={selectedTaskId}
                />
                <KanbanColumn
                  title="Em Andamento"
                  statusGroup={["in_progress"]}
                  tasks={tasks}
                  allTasks={tasks}
                  onTaskClick={setSelectedTaskId}
                  selectedTaskId={selectedTaskId}
                />
                <KanbanColumn
                  title="Concluída"
                  statusGroup={["completed"]}
                  tasks={tasks}
                  allTasks={tasks}
                  onTaskClick={setSelectedTaskId}
                  selectedTaskId={selectedTaskId}
                />
              </div>
            ) : view === "flowchart" ? (
              <div className="w-full h-full">
                <TaskFlowchart tasks={tasks} onTaskClick={setSelectedTaskId} />
              </div>
            ) : (
              <div className="space-y-3 overflow-y-auto pr-2 pb-4">
                {tasks.map((task) => (
                  <TaskCard
                    key={task.id}
                    task={task}
                    tasks={tasks}
                    onClick={() => setSelectedTaskId(task.id)}
                    selected={selectedTaskId === task.id}
                  />
                ))}
                {tasks.length === 0 && (
                  <div className="text-center py-16 text-gray-500 bg-white rounded-lg border border-gray-200">
                    <Circle className="w-12 h-12 mx-auto mb-3 text-gray-300" />
                    <p>Nenhuma tarefa cadastrada ainda.</p>
                  </div>
                )}
              </div>
            )}
          </div>

          {/* Painel de detalhes Lateral */}
          <div className="w-80 shrink-0 overflow-y-auto bg-white rounded-xl border border-gray-200 shadow-sm flex flex-col">
            {selectedTask ? (
              <div className="p-6 flex flex-col h-full">
                <div className="flex items-center justify-between mb-6">
                  <h3 className="text-lg font-bold text-gray-900">Detalhes</h3>
                  <button
                    onClick={() => navigate(`/tasks/${selectedTask.id}`)}
                    className="text-sm px-3 py-1.5 bg-gray-100 hover:bg-gray-200 text-gray-700 font-medium rounded-md transition-colors"
                  >
                    Editar
                  </button>
                </div>

                <div className="space-y-5 flex-1">
                  <div>
                    <h4 className="text-xl font-semibold text-gray-900 leading-tight">{selectedTask.title}</h4>
                    <div className="mt-3 flex flex-wrap gap-2">
                      <span className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-md text-xs font-medium ${statusConfig[selectedTask.status].bg} ${statusConfig[selectedTask.status].color}`}>
                        {(() => {
                          const StatusIcon = statusConfig[selectedTask.status].icon;
                          return <StatusIcon className="w-3.5 h-3.5" />;
                        })()}
                        {statusConfig[selectedTask.status].label}
                      </span>
                    </div>
                  </div>

                  {selectedTask.description && (
                    <div>
                      <p className="text-sm font-medium text-gray-700 mb-1">Descrição</p>
                      <p className="text-sm text-gray-600 leading-relaxed bg-gray-50 p-3 rounded-lg border border-gray-100">{selectedTask.description}</p>
                    </div>
                  )}

                  <div className="grid grid-cols-2 gap-4">
                    <div>
                      <p className="text-xs font-medium text-gray-500 mb-1 uppercase tracking-wider">Equipe</p>
                      <p className="text-sm text-gray-900 font-medium">{selectedTask.team}</p>
                    </div>
                    <div>
                      <p className="text-xs font-medium text-gray-500 mb-1 uppercase tracking-wider">Prazo</p>
                      <p className="text-sm text-gray-900 font-medium">
                        {selectedTask.dueDate ? new Date(selectedTask.dueDate + "T00:00:00").toLocaleDateString("pt-BR") : "—"}
                      </p>
                    </div>
                  </div>

                  <div>
                    <p className="text-xs font-medium text-gray-500 mb-2 uppercase tracking-wider">Responsáveis</p>
                    {selectedTask.assignees.length > 0 ? (
                      <div className="space-y-2">
                        {selectedTask.assignees.map((assignee, idx) => (
                          <div key={idx} className="flex items-center gap-2.5 text-sm text-gray-700 bg-gray-50 p-2 rounded-lg border border-gray-100">
                            <div className="w-6 h-6 rounded-full bg-blue-100 flex items-center justify-center text-blue-700 text-xs font-bold">
                              {assignee.charAt(0)}
                            </div>
                            <span className="font-medium">{assignee}</span>
                          </div>
                        ))}
                      </div>
                    ) : (
                      <p className="text-sm text-red-500 font-medium bg-red-50 p-2 rounded border border-red-100">Sem responsável alocado</p>
                    )}
                  </div>

                  {selectedTask.dependencies.length > 0 && (
                    <div>
                      <p className="text-xs font-medium text-gray-500 mb-2 uppercase tracking-wider">Predecessoras (Bloqueiam esta)</p>
                      <div className="space-y-2">
                        {selectedTask.dependencies.map((depId) => {
                          const depTask = getTaskById(depId);
                          if (!depTask) return null;
                          const depConfig = statusConfig[depTask.status];
                          return (
                            <div key={depId} className="flex items-center justify-between p-2.5 border border-gray-200 rounded-lg bg-gray-50">
                              <p className="text-sm text-gray-700 font-medium truncate pr-2" title={depTask.title}>
                                {depTask.title}
                              </p>
                              <span className={`text-[10px] font-bold px-1.5 py-0.5 rounded whitespace-nowrap ${depConfig.bg} ${depConfig.color}`}>
                                {depConfig.label}
                              </span>
                            </div>
                          );
                        })}
                      </div>
                    </div>
                  )}

                  {(() => {
                    const dependentes = tasks.filter(t => t.dependencies.includes(selectedTask.id));
                    if (dependentes.length === 0) return null;
                    return (
                      <div>
                        <p className="text-xs font-medium text-gray-500 mb-2 uppercase tracking-wider">Dependentes (Aguardam esta)</p>
                        <div className="space-y-2">
                           {dependentes.map((depTask) => {
                            const depConfig = statusConfig[depTask.status];
                            return (
                              <div key={depTask.id} className="flex items-center justify-between p-2.5 border border-gray-200 rounded-lg bg-gray-50">
                                <p className="text-sm text-gray-700 font-medium truncate pr-2" title={depTask.title}>
                                  {depTask.title}
                                </p>
                                <span className={`text-[10px] font-bold px-1.5 py-0.5 rounded whitespace-nowrap ${depConfig.bg} ${depConfig.color}`}>
                                  {depConfig.label}
                                </span>
                              </div>
                            );
                          })}
                        </div>
                      </div>
                    );
                  })()}
                </div>

                {/* Ações de status fixadas no rodapé */}
                <div className="pt-6 mt-6 border-t border-gray-100 space-y-3 shrink-0">
                  {(selectedTask.status === "pending" || selectedTask.status === "overdue") && (
                    <button
                      onClick={() => handleStatusAction(selectedTask.id, "in_progress")}
                      className="w-full flex items-center justify-center gap-2 bg-blue-600 text-white py-2.5 rounded-lg font-medium hover:bg-blue-700 transition-colors shadow-sm"
                    >
                      <Play className="w-4 h-4" />
                      Iniciar Tarefa
                    </button>
                  )}

                  {selectedTask.status === "blocked" && (
                    <>
                      <div className="text-xs font-medium text-orange-700 bg-orange-50 border border-orange-200 rounded-lg p-3 text-center">
                        Tarefa bloqueada por dependências pendentes
                      </div>
                      <button
                        onClick={() => handleStatusAction(selectedTask.id, "in_progress")}
                        className="w-full flex items-center justify-center gap-2 bg-blue-600/50 text-white py-2.5 rounded-lg font-medium hover:bg-blue-600 transition-colors shadow-sm"
                      >
                        <Play className="w-4 h-4" />
                        Forçar Início
                      </button>
                    </>
                  )}

                  {selectedTask.status === "in_progress" && (
                    <button
                      onClick={() => handleStatusAction(selectedTask.id, "completed")}
                      className="w-full flex items-center justify-center gap-2 bg-green-600 text-white py-2.5 rounded-lg font-medium hover:bg-green-700 transition-colors shadow-sm"
                    >
                      <CheckCheck className="w-4 h-4" />
                      Marcar como Concluída
                    </button>
                  )}

                  {selectedTask.status !== "completed" && selectedTask.status !== "in_progress" && (
                    <button
                      onClick={() => setShowDeleteConfirm(true)}
                      className="w-full flex items-center justify-center gap-2 border-2 border-red-100 text-red-600 py-2 rounded-lg font-medium hover:bg-red-50 transition-colors"
                    >
                      <Trash2 className="w-4 h-4" />
                      Excluir Tarefa
                    </button>
                  )}
                  {selectedTask.status === "in_progress" && (
                     <div className="text-center text-xs text-gray-500 py-1">
                       Tarefas em andamento não podem ser excluídas.
                     </div>
                  )}
                  {selectedTask.status === "completed" && (
                     <div className="text-center text-xs text-green-600 font-medium py-1 bg-green-50 rounded-lg border border-green-100 p-2">
                       Tarefa finalizada com sucesso.
                     </div>
                  )}
                </div>
              </div>
            ) : (
              <div className="flex flex-col items-center justify-center h-full text-center p-8 text-gray-500">
                <div className="w-16 h-16 rounded-full bg-gray-50 flex items-center justify-center mb-4 border border-gray-100">
                  <Kanban className="w-8 h-8 text-gray-300" />
                </div>
                <p className="font-medium text-gray-600 mb-1">Nenhuma tarefa selecionada</p>
                <p className="text-sm text-gray-400">Clique em um card para visualizar seus detalhes e dependências</p>
              </div>
            )}
          </div>
        </div>

        {showDeleteConfirm && selectedTask && (
          <div className="fixed inset-0 bg-black/60 flex items-center justify-center z-50 p-4 backdrop-blur-sm">
            <div className="bg-white rounded-xl max-w-md w-full p-6 shadow-xl">
              <div className="flex items-start gap-4">
                <div className="w-12 h-12 rounded-full bg-red-100 flex items-center justify-center flex-shrink-0">
                  <Trash2 className="w-6 h-6 text-red-600" />
                </div>
                <div className="flex-1 pt-1">
                  <h3 className="text-xl font-bold text-gray-900 mb-2">Excluir Tarefa</h3>
                  <p className="text-sm text-gray-600 mb-6 leading-relaxed">
                    Tem certeza que deseja excluir <strong>"{selectedTask.title}"</strong>? Esta ação é irreversível.
                  </p>
                  <div className="flex gap-3">
                    <button
                      onClick={() => setShowDeleteConfirm(false)}
                      className="flex-1 px-4 py-2.5 border-2 border-gray-200 text-gray-700 rounded-lg hover:bg-gray-50 hover:border-gray-300 font-medium transition-all"
                    >
                      Cancelar
                    </button>
                    <button
                      onClick={handleDelete}
                      className="flex-1 px-4 py-2.5 bg-red-600 text-white rounded-lg hover:bg-red-700 font-medium transition-all shadow-sm"
                    >
                      Sim, Excluir
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </DndProvider>
  );
}
