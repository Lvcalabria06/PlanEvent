import { createContext, useContext, useEffect, useState, type ReactNode, useCallback } from "react";
import { toast } from "sonner";

export type TaskStatus = "pending" | "in_progress" | "completed" | "blocked" | "overdue";

export type Task = {
  id: string;
  title: string;
  description?: string;
  team: string;
  assignees: string[];
  dueDate: string;
  startDate?: string;
  status: TaskStatus;
  dependencies: string[];
  createdAt: string;
  updatedAt: string;
};

export type TeamMember = { funcionarioId: string; nome: string; lider: boolean };
export type TeamRef = { id: string; nome: string; eventoId: string; membros: TeamMember[] };

type TasksContextType = {
  tasks: Task[];
  teams: TeamRef[];
  loading: boolean;
  refresh: () => Promise<void>;
  createTask: (data: Omit<Task, "id" | "createdAt" | "updatedAt">) => Promise<void>;
  updateTask: (id: string, data: Partial<Task>) => Promise<void>;
  deleteTask: (id: string) => Promise<void>;
  getTask: (id: string) => Task | undefined;
  updateStatus: (id: string, newStatus: TaskStatus) => Promise<void>;
};

const TasksContext = createContext<TasksContextType | undefined>(undefined);

// Base da API: em dev o Vite roda na 5173 e o backend na 3000 (CORS liberado).
// Quando o backend serve o front (mesmo origin), VITE_API_URL pode ser "/api".
const API: string =
  (import.meta as any).env?.VITE_API_URL ?? "http://localhost:3000/api";

// ---- Tipos crus da API (backend) ----
type BackendTarefa = {
  id: string;
  equipeId: string;
  titulo: string;
  descricao?: string;
  dataInicio?: string;
  dataFim?: string;
  status: "PENDENTE" | "EM_ANDAMENTO" | "CONCLUIDA" | "CANCELADA";
  dependencias: string[];
  responsaveis: string[];
  dataCriacao?: string;
  dataAtualizacao?: string;
};
type BackendEquipe = { id: string; nome: string; eventoId: string; membros: TeamMember[] };
type BackendFuncionario = { id: string; nome: string; cargo: string };

// ---- HTTP helper: lança Error com a mensagem do backend ----
async function api<T>(path: string, options?: RequestInit): Promise<T> {
  const res = await fetch(`${API}${path}`, {
    headers: { "Content-Type": "application/json" },
    ...options,
  });
  if (!res.ok) {
    let msg = `Erro ${res.status}`;
    try {
      const body = await res.json();
      msg = body?.erro ?? msg;
    } catch {
      /* corpo vazio */
    }
    throw new Error(msg);
  }
  if (res.status === 204) return undefined as T;
  const text = await res.text();
  return (text ? JSON.parse(text) : undefined) as T;
}

const toDateOnly = (iso?: string) => (iso ? iso.slice(0, 10) : "");
const toBackendDate = (d?: string) => (d ? `${d}T00:00:00` : null);

const statusFromBackend = (s: BackendTarefa["status"]): TaskStatus => {
  switch (s) {
    case "EM_ANDAMENTO":
      return "in_progress";
    case "CONCLUIDA":
      return "completed";
    default:
      return "pending"; // PENDENTE e CANCELADA tratados como pendente na UI
  }
};

export function TasksProvider({ children }: { children: ReactNode }) {
  const [tasks, setTasks] = useState<Task[]>([]);
  const [teams, setTeams] = useState<TeamRef[]>([]);
  const [funcionarios, setFuncionarios] = useState<BackendFuncionario[]>([]);
  const [loading, setLoading] = useState(true);

  // ---- Resolução nome <-> id ----
  const teamNameById = useCallback(
    (id: string) => teams.find((t) => t.id === id)?.nome ?? id,
    [teams]
  );
  const teamIdByName = useCallback(
    (nome: string) => teams.find((t) => t.nome === nome)?.id,
    [teams]
  );
  const funcNameById = useCallback(
    (id: string) => funcionarios.find((f) => f.id === id)?.nome ?? id,
    [funcionarios]
  );
  const funcIdByName = useCallback(
    (nome: string) => funcionarios.find((f) => f.nome === nome)?.id,
    [funcionarios]
  );

  // ---- Mapeia tarefas cruas para o modelo da UI, derivando blocked/overdue ----
  const mapTasks = useCallback(
    (raw: BackendTarefa[]): Task[] => {
      const byId = new Map(raw.map((t) => [t.id, t]));
      const hoje = new Date();
      hoje.setHours(0, 0, 0, 0);

      return raw.map((t) => {
        const base = statusFromBackend(t.status);
        const dueDate = toDateOnly(t.dataFim);
        let status: TaskStatus = base;

        if (base === "pending") {
          const atrasada = dueDate && new Date(dueDate + "T00:00:00") < hoje;
          const bloqueada = t.dependencias.some((depId) => {
            const dep = byId.get(depId);
            return dep && dep.status !== "CONCLUIDA";
          });
          if (atrasada) status = "overdue";
          else if (bloqueada) status = "blocked";
        }

        return {
          id: t.id,
          title: t.titulo,
          description: t.descricao,
          team: teamNameById(t.equipeId),
          assignees: (t.responsaveis ?? []).map(funcNameById),
          dueDate,
          startDate: toDateOnly(t.dataInicio),
          status,
          dependencies: t.dependencias ?? [],
          createdAt: t.dataCriacao ?? "",
          updatedAt: t.dataAtualizacao ?? "",
        };
      });
    },
    [teamNameById, funcNameById]
  );

  const refresh = useCallback(async () => {
    const raw = await api<BackendTarefa[]>("/tarefas");
    setTasks(mapTasks(raw));
  }, [mapTasks]);

  // Carga inicial: equipes + funcionários e depois tarefas (dependem dos mapas).
  useEffect(() => {
    let ativo = true;
    (async () => {
      try {
        const [eqs, fns] = await Promise.all([
          api<BackendEquipe[]>("/equipes"),
          api<BackendFuncionario[]>("/funcionarios"),
        ]);
        if (!ativo) return;
        setTeams(eqs);
        setFuncionarios(fns);
      } catch (e: any) {
        toast.error("Falha ao carregar equipes/funcionários", { description: e.message });
      } finally {
        if (ativo) setLoading(false);
      }
    })();
    return () => {
      ativo = false;
    };
  }, []);

  // Quando os mapas (teams/funcionarios) carregarem, busca as tarefas.
  useEffect(() => {
    if (loading) return;
    refresh().catch((e: any) =>
      toast.error("Falha ao carregar tarefas", { description: e.message })
    );
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [loading, teams, funcionarios]);

  const getTask = (id: string) => tasks.find((t) => t.id === id);

  // ========== CRIAR ==========
  const createTask = async (data: Omit<Task, "id" | "createdAt" | "updatedAt">) => {
    const equipeId = teamIdByName(data.team);
    if (!equipeId) {
      toast.error("Equipe inválida", { description: "Selecione uma equipe válida." });
      throw new Error("Equipe inválida");
    }
    try {
      const criada = await api<BackendTarefa>("/tarefas", {
        method: "POST",
        body: JSON.stringify({
          equipeId,
          titulo: data.title,
          descricao: data.description ?? "",
          dataInicio: toBackendDate(data.startDate),
          dataFim: toBackendDate(data.dueDate),
        }),
      });

      await sincronizarResponsaveis(criada.id, [], data.assignees);
      await adicionarDependencias(criada.id, data.dependencies);
      await aplicarStatusInicial(criada.id, data.status);

      await refresh();
      toast.success("Tarefa criada", { description: "A tarefa foi cadastrada com sucesso." });
    } catch (e: any) {
      toast.error("Não foi possível criar a tarefa", { description: e.message });
      throw e;
    }
  };

  // ========== EDITAR ==========
  const updateTask = async (id: string, data: Partial<Task>) => {
    const atual = tasks.find((t) => t.id === id);
    if (!atual) throw new Error("Tarefa inexistente");
    try {
      await api<BackendTarefa>(`/tarefas/${id}`, {
        method: "PUT",
        body: JSON.stringify({
          titulo: data.title ?? atual.title,
          descricao: data.description ?? atual.description ?? "",
          dataInicio: toBackendDate(data.startDate ?? atual.startDate),
          dataFim: toBackendDate(data.dueDate ?? atual.dueDate),
        }),
      });

      if (data.assignees) {
        await sincronizarResponsaveis(id, atual.assignees, data.assignees);
      }
      if (data.dependencies) {
        await sincronizarDependencias(id, atual.dependencies, data.dependencies);
      }

      await refresh();
      toast.success("Tarefa atualizada", { description: "As alterações foram salvas." });
    } catch (e: any) {
      toast.error("Não foi possível salvar", { description: e.message });
      throw e;
    }
  };

  // ========== EXCLUIR ==========
  const deleteTask = async (id: string) => {
    try {
      await api<void>(`/tarefas/${id}`, { method: "DELETE" });
      await refresh();
      toast.success("Tarefa removida", { description: "A tarefa foi excluída com sucesso." });
    } catch (e: any) {
      toast.error("Não foi possível excluir", { description: e.message });
      throw e;
    }
  };

  // ========== STATUS (iniciar / concluir) ==========
  const updateStatus = async (id: string, newStatus: TaskStatus) => {
    try {
      if (newStatus === "in_progress") {
        await api<void>(`/tarefas/${id}/iniciar`, { method: "POST" });
      } else if (newStatus === "completed") {
        await api<void>(`/tarefas/${id}/concluir`, { method: "POST" });
      } else {
        throw new Error("Transição de status não suportada pelo fluxo controlado.");
      }
      await refresh();
      toast.success("Status atualizado", { description: "A tarefa foi atualizada com sucesso." });
    } catch (e: any) {
      toast.error("Transição não permitida", { description: e.message });
      throw e;
    }
  };

  // ---- Auxiliares de orquestração ----
  async function sincronizarResponsaveis(taskId: string, antigos: string[], novos: string[]) {
    const idsAntigos = antigos.map(funcIdByName).filter(Boolean) as string[];
    const idsNovos = novos.map(funcIdByName).filter(Boolean) as string[];
    for (const fid of idsNovos.filter((f) => !idsAntigos.includes(f))) {
      await api<void>(`/tarefas/${taskId}/responsaveis`, {
        method: "POST",
        body: JSON.stringify({ funcionarioId: fid }),
      });
    }
    for (const fid of idsAntigos.filter((f) => !idsNovos.includes(f))) {
      await api<void>(`/tarefas/${taskId}/responsaveis/${fid}`, { method: "DELETE" });
    }
  }

  async function adicionarDependencias(taskId: string, deps: string[]) {
    for (const depId of deps) {
      await api<void>(`/tarefas/${taskId}/dependencias`, {
        method: "POST",
        body: JSON.stringify({ tarefaPredecessoraId: depId }),
      });
    }
  }

  async function sincronizarDependencias(taskId: string, antigas: string[], novas: string[]) {
    for (const depId of novas.filter((d) => !antigas.includes(d))) {
      await api<void>(`/tarefas/${taskId}/dependencias`, {
        method: "POST",
        body: JSON.stringify({ tarefaPredecessoraId: depId }),
      });
    }
    for (const depId of antigas.filter((d) => !novas.includes(d))) {
      await api<void>(`/tarefas/${taskId}/dependencias/${depId}`, { method: "DELETE" });
    }
  }

  async function aplicarStatusInicial(taskId: string, status: TaskStatus) {
    // Backend cria como PENDENTE; honra in_progress/completed em melhor esforço.
    if (status === "in_progress" || status === "completed") {
      await api<void>(`/tarefas/${taskId}/iniciar`, { method: "POST" });
    }
    if (status === "completed") {
      await api<void>(`/tarefas/${taskId}/concluir`, { method: "POST" });
    }
  }

  return (
    <TasksContext.Provider
      value={{ tasks, teams, loading, refresh, createTask, updateTask, deleteTask, getTask, updateStatus }}
    >
      {children}
    </TasksContext.Provider>
  );
}

export function useTasks() {
  const context = useContext(TasksContext);
  if (!context) {
    throw new Error("useTasks must be used within TasksProvider");
  }
  return context;
}
