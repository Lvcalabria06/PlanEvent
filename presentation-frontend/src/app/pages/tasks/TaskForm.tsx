import { useState } from "react";
import { useNavigate, useParams } from "react-router";
import { ArrowLeft, Save, Trash2, GitBranch, Plus, AlertCircle, CheckCircle, Clock } from "lucide-react";
import { useForm } from "react-hook-form";
import { useTasks, type TaskStatus } from "../../contexts/TasksContext";
import { toast } from "sonner";

type TaskFormData = {
  title: string;
  description: string;
  team: string;
  assignees: string[];
  startDate: string;
  dueDate: string;
  status: TaskStatus;
  dependencies: string[]; // IDs
};

export function TaskForm() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { tasks, createTask, updateTask, getTask, teams } = useTasks();

  // Dados reais vindos do backend (substituem as listas fixas).
  const AVAILABLE_TEAMS = teams.map((t) => t.nome);
  const AVAILABLE_EMPLOYEES: Record<string, string[]> = Object.fromEntries(
    teams.map((t) => [t.nome, t.membros.map((m) => m.nome)])
  );

  const isEditing = !!id;
  const existingTask = isEditing ? getTask(id) : undefined;
  const isReadOnly = existingTask?.status === "completed";

  const [activeTab, setActiveTab] = useState<"details" | "dependencies">("details");
  const [depToRemove, setDepToRemove] = useState<{ type: "predecessor" | "successor", id: string, name: string } | null>(null);

  // Para adicionar dependências
  const [newDepId, setNewDepId] = useState("");

  const { register, handleSubmit, watch, setValue, formState: { errors } } = useForm<TaskFormData>({
    defaultValues: existingTask ? {
      title: existingTask.title,
      description: existingTask.description || "",
      team: existingTask.team,
      assignees: existingTask.assignees || [],
      startDate: existingTask.startDate || "",
      dueDate: existingTask.dueDate || "",
      status: existingTask.status,
      dependencies: existingTask.dependencies || [],
    } : {
      title: "",
      description: "",
      team: "",
      assignees: [],
      startDate: "",
      dueDate: "",
      status: "pending",
      dependencies: [],
    }
  });

  const selectedTeam = watch("team");
  const currentDependencies = watch("dependencies");
  const currentStatus = watch("status");

  // Dependentes: tarefas que dependem desta
  const dependentTasks = isEditing && existingTask ? tasks.filter(t => t.dependencies.includes(existingTask.id)) : [];

  const onSubmit = async (data: TaskFormData) => {
    if (isReadOnly) return;

    try {
      if (isEditing) {
        await updateTask(id!, {
          title: data.title,
          description: data.description,
          team: data.team,
          assignees: data.assignees,
          startDate: data.startDate,
          dueDate: data.dueDate,
          status: data.status,
          dependencies: data.dependencies,
        });
      } else {
        await createTask({
          title: data.title,
          description: data.description,
          team: data.team,
          assignees: data.assignees,
          startDate: data.startDate,
          dueDate: data.dueDate,
          status: data.status,
          dependencies: data.dependencies,
        });
      }
      navigate("/tasks");
    } catch (err) {
      // Erros já exibidos via sonner pelo contexto
    }
  };

  const handleAddDependency = () => {
    if (!newDepId) return;
    if (currentDependencies.includes(newDepId)) {
      toast.error("Dependência já existe");
      return;
    }

    if (isEditing && newDepId === existingTask?.id) {
      toast.error("Dependência inválida", { description: "Uma tarefa não pode depender de si mesma." });
      return;
    }

    setValue("dependencies", [...currentDependencies, newDepId], { shouldDirty: true });
    setNewDepId("");
  };

  const confirmRemoveDependency = () => {
    if (!depToRemove) return;

    if (depToRemove.type === "predecessor") {
      setValue("dependencies", currentDependencies.filter(depId => depId !== depToRemove.id), { shouldDirty: true });
      toast.success("Dependência removida localmente", { description: "Salve a tarefa para confirmar a alteração." });
    } else {
      toast.error("Operação não permitida", { description: "Para remover um dependente, edite a tarefa correspondente." });
    }

    setDepToRemove(null);
  };

  // Filtrar tarefas disponíveis para virarem dependências
  const availableTasksToAdd = tasks.filter(t => {
    if (isEditing && t.id === existingTask?.id) return false;
    if (currentDependencies.includes(t.id)) return false;
    if (isEditing && existingTask && t.dependencies.includes(existingTask.id)) return false;
    return true;
  });

  const getTaskStatusLabel = (status: TaskStatus) => {
    const map = {
      pending: "Pendente",
      in_progress: "Em Andamento",
      completed: "Concluída",
      blocked: "Bloqueada",
      overdue: "Atrasada",
    };
    return map[status];
  };

  return (
    <div className="max-w-4xl mx-auto py-8">
      <button
        onClick={() => navigate("/tasks")}
        className="flex items-center gap-2 text-gray-600 hover:text-gray-900 mb-6 font-medium transition-colors"
      >
        <ArrowLeft className="w-4 h-4" />
        Voltar para Gestão de Tarefas
      </button>

      {isReadOnly && (
        <div className="bg-blue-50 border border-blue-200 rounded-lg p-4 mb-6 flex items-start gap-3">
          <CheckCircle className="w-5 h-5 text-blue-600 mt-0.5" />
          <div>
            <h3 className="font-semibold text-blue-900">Tarefa Concluída</h3>
            <p className="text-sm text-blue-700 mt-1">
              Esta tarefa foi finalizada e não pode mais ser editada. O formulário está em modo apenas leitura.
            </p>
          </div>
        </div>
      )}

      <div className="bg-white rounded-xl border border-gray-200 shadow-sm overflow-hidden">
        <div className="px-6 border-b border-gray-200 bg-gray-50/50 flex flex-col pt-6">
          <div className="flex items-center justify-between mb-4">
            <h1 className="text-2xl font-bold text-gray-900">
              {isEditing ? (isReadOnly ? "Visualizar Tarefa" : "Editar Tarefa") : "Nova Tarefa"}
            </h1>
            <div className="flex items-center gap-2">
               <span className={`px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wider ${
                 currentStatus === 'completed' ? 'bg-green-100 text-green-700' :
                 currentStatus === 'in_progress' ? 'bg-blue-100 text-blue-700' :
                 currentStatus === 'blocked' ? 'bg-orange-100 text-orange-700' :
                 'bg-gray-100 text-gray-700'
               }`}>
                 {getTaskStatusLabel(currentStatus)}
               </span>
            </div>
          </div>

          <div className="flex gap-6 -mb-px">
            <button
              onClick={() => setActiveTab("details")}
              className={`pb-4 px-2 text-sm font-medium border-b-2 transition-colors ${
                activeTab === "details" ? "border-blue-600 text-blue-600" : "border-transparent text-gray-500 hover:text-gray-700"
              }`}
            >
              Detalhes da Tarefa
            </button>
            <button
              onClick={() => setActiveTab("dependencies")}
              className={`pb-4 px-2 text-sm font-medium border-b-2 transition-colors flex items-center gap-2 ${
                activeTab === "dependencies" ? "border-blue-600 text-blue-600" : "border-transparent text-gray-500 hover:text-gray-700"
              }`}
            >
              <GitBranch className="w-4 h-4" />
              Dependências
              {currentDependencies.length > 0 && (
                <span className="bg-gray-100 text-gray-600 py-0.5 px-2 rounded-full text-xs ml-1">
                  {currentDependencies.length}
                </span>
              )}
            </button>
          </div>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className="p-6">
          {activeTab === "details" ? (
            <div className="space-y-6">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className="md:col-span-2">
                  <label className="block text-sm font-semibold text-gray-900 mb-1.5">
                    Título da Tarefa <span className="text-red-500">*</span>
                  </label>
                  <input
                    disabled={isReadOnly}
                    {...register("title", { required: true, minLength: 3 })}
                    className={`w-full px-4 py-2.5 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors ${
                      errors.title ? "border-red-300 bg-red-50" : "border-gray-300"
                    } ${isReadOnly ? "bg-gray-50 text-gray-500 cursor-not-allowed" : ""}`}
                    placeholder="Ex: Montar palco principal"
                  />
                  {errors.title && <span className="text-xs text-red-600 mt-1.5 flex items-center gap-1"><AlertCircle className="w-3 h-3"/> Título inválido ou muito curto</span>}
                </div>

                <div className="md:col-span-2">
                  <label className="block text-sm font-semibold text-gray-900 mb-1.5">
                    Descrição Detalhada
                  </label>
                  <textarea
                    disabled={isReadOnly}
                    {...register("description")}
                    rows={3}
                    className={`w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors ${
                      isReadOnly ? "bg-gray-50 text-gray-500 cursor-not-allowed" : ""
                    }`}
                    placeholder="Descreva as instruções e detalhes da tarefa..."
                  />
                </div>

                <div>
                  <label className="block text-sm font-semibold text-gray-900 mb-1.5">
                    Equipe Associada <span className="text-red-500">*</span>
                  </label>
                  <select
                    disabled={isReadOnly}
                    {...register("team", { required: true })}
                    onChange={(e) => {
                      setValue("team", e.target.value);
                      setValue("assignees", []); // Limpar responsáveis se mudar de equipe
                    }}
                    className={`w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors ${
                      isReadOnly ? "bg-gray-50 text-gray-500 cursor-not-allowed" : ""
                    }`}
                  >
                    <option value="">Selecione uma equipe...</option>
                    {AVAILABLE_TEAMS.map(t => (
                      <option key={t} value={t}>{t}</option>
                    ))}
                  </select>
                </div>

                <div>
                  <label className="block text-sm font-semibold text-gray-900 mb-1.5">
                    Responsáveis pela Tarefa
                  </label>
                  <select
                    disabled={isReadOnly || !selectedTeam}
                    multiple
                    {...register("assignees")}
                    className={`w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors h-24 ${
                      (isReadOnly || !selectedTeam) ? "bg-gray-50 text-gray-500 cursor-not-allowed" : ""
                    }`}
                  >
                    {!selectedTeam && <option disabled value="">Selecione a equipe primeiro...</option>}
                    {selectedTeam && (AVAILABLE_EMPLOYEES[selectedTeam] || []).map(emp => (
                      <option key={emp} value={emp} className="p-1">{emp}</option>
                    ))}
                  </select>
                  <p className="text-xs text-gray-500 mt-1.5">
                    Segure CTRL/CMD para selecionar múltiplos. Responsáveis devem pertencer à equipe escolhida.
                  </p>
                </div>

                <div>
                  <label className="block text-sm font-semibold text-gray-900 mb-1.5">
                    Data de Início
                  </label>
                  <input
                    type="date"
                    disabled={isReadOnly}
                    {...register("startDate")}
                    className={`w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors ${
                      isReadOnly ? "bg-gray-50 text-gray-500 cursor-not-allowed" : ""
                    }`}
                  />
                </div>

                <div>
                  <label className="block text-sm font-semibold text-gray-900 mb-1.5">
                    Data de Conclusão (Prazo) <span className="text-red-500">*</span>
                  </label>
                  <input
                    type="date"
                    disabled={isReadOnly}
                    {...register("dueDate", { required: true })}
                    className={`w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors ${
                      isReadOnly ? "bg-gray-50 text-gray-500 cursor-not-allowed" : ""
                    }`}
                  />
                </div>

                {!isEditing && (
                  <div className="md:col-span-2">
                    <label className="block text-sm font-semibold text-gray-900 mb-1.5">
                      Status Inicial <span className="text-red-500">*</span>
                    </label>
                    <select
                      {...register("status", { required: true })}
                      className="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors"
                    >
                      <option value="pending">Pendente</option>
                      <option value="in_progress">Em Andamento</option>
                      <option value="completed">Concluída</option>
                    </select>
                  </div>
                )}
              </div>
            </div>
          ) : (
            <div className="space-y-8 animate-in fade-in duration-200">
              {/* Tarefas Predecessoras (Das quais esta depende) */}
              <div className="bg-gray-50 rounded-xl p-5 border border-gray-200">
                <div className="flex items-center justify-between mb-4">
                  <div>
                    <h3 className="text-lg font-bold text-gray-900">Predecessoras</h3>
                    <p className="text-sm text-gray-600 mt-1">Tarefas que precisam ser concluídas <strong>antes</strong> do início desta.</p>
                  </div>
                </div>

                <div className="space-y-3">
                  {currentDependencies.map(depId => {
                    const depTask = tasks.find(t => t.id === depId);
                    if (!depTask) return null;
                    return (
                      <div key={depId} className="flex items-center justify-between bg-white p-4 rounded-lg border border-gray-200 shadow-sm">
                        <div className="flex items-start gap-3">
                          <Clock className={`w-5 h-5 mt-0.5 ${depTask.status === 'completed' ? 'text-green-500' : 'text-orange-500'}`} />
                          <div>
                            <h4 className="font-medium text-gray-900">{depTask.title}</h4>
                            <div className="flex gap-3 mt-1 text-xs text-gray-500">
                              <span>Prazo: {depTask.dueDate ? new Date(depTask.dueDate + "T00:00:00").toLocaleDateString("pt-BR") : "—"}</span>
                              <span>•</span>
                              <span className={depTask.status === 'completed' ? 'text-green-600 font-medium' : 'text-orange-600 font-medium'}>
                                {getTaskStatusLabel(depTask.status)}
                              </span>
                            </div>
                          </div>
                        </div>
                        {!isReadOnly && (
                          <button
                            type="button"
                            onClick={() => setDepToRemove({ type: "predecessor", id: depId, name: depTask.title })}
                            className="p-2 text-gray-400 hover:text-red-600 hover:bg-red-50 rounded-md transition-colors"
                            title="Remover dependência"
                          >
                            <Trash2 className="w-4 h-4" />
                          </button>
                        )}
                      </div>
                    );
                  })}

                  {currentDependencies.length === 0 && (
                    <div className="text-center py-6 bg-white rounded-lg border border-dashed border-gray-300">
                      <p className="text-gray-500 text-sm">Esta tarefa não possui predecessoras. Ela pode ser iniciada imediatamente.</p>
                    </div>
                  )}
                </div>

                {!isReadOnly && (
                  <div className="mt-6 flex gap-3 pt-4 border-t border-gray-200">
                    <select
                      value={newDepId}
                      onChange={(e) => setNewDepId(e.target.value)}
                      className="flex-1 px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 text-sm"
                    >
                      <option value="">Selecione uma tarefa existente...</option>
                      {availableTasksToAdd.map(t => (
                        <option key={t.id} value={t.id}>{t.title} ({getTaskStatusLabel(t.status)})</option>
                      ))}
                    </select>
                    <button
                      type="button"
                      onClick={handleAddDependency}
                      disabled={!newDepId}
                      className="flex items-center gap-2 px-5 py-2.5 bg-gray-900 text-white rounded-lg font-medium hover:bg-gray-800 disabled:opacity-50 disabled:cursor-not-allowed transition-all"
                    >
                      <Plus className="w-4 h-4" />
                      Vincular
                    </button>
                  </div>
                )}
              </div>

              {/* Tarefas Dependentes (Que aguardam esta) - Apenas Exibição se isEditing */}
              {isEditing && (
                <div className="bg-white rounded-xl p-5 border border-gray-200 shadow-sm">
                  <div className="mb-4">
                    <h3 className="text-lg font-bold text-gray-900">Dependentes (Sucessoras)</h3>
                    <p className="text-sm text-gray-600 mt-1">Tarefas que estão aguardando a conclusão desta para serem iniciadas.</p>
                  </div>

                  <div className="space-y-3">
                    {dependentTasks.map(depTask => (
                      <div key={depTask.id} className="flex items-center justify-between bg-gray-50 p-4 rounded-lg border border-gray-200">
                        <div className="flex items-start gap-3">
                          <GitBranch className="w-5 h-5 mt-0.5 text-blue-500" />
                          <div>
                            <h4 className="font-medium text-gray-900">{depTask.title}</h4>
                            <div className="flex gap-3 mt-1 text-xs text-gray-500">
                              <span>Equipe: {depTask.team}</span>
                              <span>•</span>
                              <span className="text-gray-700 font-medium">
                                {getTaskStatusLabel(depTask.status)}
                              </span>
                            </div>
                          </div>
                        </div>
                        <button
                          type="button"
                          onClick={() => {
                            if (isReadOnly) return;
                            setDepToRemove({ type: "successor", id: depTask.id, name: depTask.title });
                          }}
                          className="text-xs font-medium text-red-600 hover:bg-red-50 px-3 py-1.5 rounded transition-colors"
                          disabled={isReadOnly}
                        >
                          Remover vínculo
                        </button>
                      </div>
                    ))}

                    {dependentTasks.length === 0 && (
                      <div className="text-center py-6 bg-gray-50 rounded-lg border border-dashed border-gray-300">
                        <p className="text-gray-500 text-sm">Nenhuma tarefa depende desta.</p>
                      </div>
                    )}
                  </div>
                </div>
              )}
            </div>
          )}

          {!isReadOnly && (
            <div className="flex items-center justify-end gap-4 pt-8 mt-8 border-t border-gray-200">
              <button
                type="button"
                onClick={() => navigate("/tasks")}
                className="px-6 py-2.5 text-gray-700 font-medium hover:bg-gray-100 rounded-lg transition-colors"
              >
                Cancelar
              </button>
              <button
                type="submit"
                className="flex items-center gap-2 px-6 py-2.5 bg-blue-600 text-white font-medium rounded-lg hover:bg-blue-700 shadow-sm transition-colors"
              >
                <Save className="w-4 h-4" />
                {isEditing ? "Salvar Alterações" : "Criar Tarefa"}
              </button>
            </div>
          )}
        </form>
      </div>

      {/* Modal de Confirmação de Remoção de Dependência */}
      {depToRemove && (
        <div className="fixed inset-0 bg-black/60 flex items-center justify-center z-50 p-4 backdrop-blur-sm">
          <div className="bg-white rounded-xl max-w-md w-full p-6 shadow-xl">
            <div className="flex items-start gap-4">
              <div className="w-12 h-12 rounded-full bg-orange-100 flex items-center justify-center flex-shrink-0">
                <AlertCircle className="w-6 h-6 text-orange-600" />
              </div>
              <div className="flex-1 pt-1">
                <h3 className="text-xl font-bold text-gray-900 mb-2">Remover Dependência</h3>
                <p className="text-sm text-gray-600 mb-6 leading-relaxed">
                  Deseja remover o vínculo com a tarefa <strong>"{depToRemove.name}"</strong>?
                  {depToRemove.type === "predecessor"
                    ? " Esta tarefa não precisará mais aguardar por ela."
                    : " A tarefa dependente não precisará mais aguardar por esta."}
                </p>
                <div className="flex gap-3">
                  <button
                    onClick={() => setDepToRemove(null)}
                    className="flex-1 px-4 py-2.5 border-2 border-gray-200 text-gray-700 rounded-lg hover:bg-gray-50 hover:border-gray-300 font-medium transition-all"
                  >
                    Cancelar
                  </button>
                  <button
                    onClick={confirmRemoveDependency}
                    className="flex-1 px-4 py-2.5 bg-orange-600 text-white rounded-lg hover:bg-orange-700 font-medium transition-all shadow-sm"
                  >
                    Sim, Remover
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
