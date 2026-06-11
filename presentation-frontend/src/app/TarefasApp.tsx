import { MemoryRouter, Routes, Route } from "react-router";
import { Toaster } from "sonner";
import { TasksProvider } from "./contexts/TasksContext";
import { Tasks } from "./pages/Tasks";
import { TaskForm } from "./pages/tasks/TaskForm";
import "./tarefas.css";

/**
 * Módulo de Tarefas embutido na aba "Tarefas" do app. Usa um MemoryRouter
 * próprio (rotas /tasks, /tasks/new, /tasks/:id) para não interferir na
 * navegação por abas do restante da aplicação.
 */
export default function TarefasApp() {
  return (
    <TasksProvider>
      <div className="tarefas-root">
        <MemoryRouter initialEntries={["/tasks"]}>
          <Routes>
            <Route path="/tasks" element={<Tasks />} />
            <Route path="/tasks/new" element={<TaskForm />} />
            <Route path="/tasks/:id" element={<TaskForm />} />
            <Route path="*" element={<Tasks />} />
          </Routes>
        </MemoryRouter>
        <Toaster position="top-right" richColors />
      </div>
    </TasksProvider>
  );
}
