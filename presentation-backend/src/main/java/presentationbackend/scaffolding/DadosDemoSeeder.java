package presentationbackend.scaffolding;

import domain.equipe.entity.Equipe;
import domain.equipe.repository.EquipeRepository;
import domain.evento.entity.Evento;
import domain.evento.repository.EventoRepository;
import domain.funcionario.entity.Funcionario;
import domain.funcionario.repository.FuncionarioRepository;
import domain.tarefa.entity.Tarefa;
import domain.tarefa.repository.ResponsavelTarefaRepository;
import domain.tarefa.repository.TarefaRepository;
import domain.tarefa.service.DependenciaService;
import domain.tarefa.service.TarefaService;
import org.slf4j.Logger;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Semeia um conjunto realista de dados de demonstração (evento + funcionários +
 * equipes + tarefas com dependências e status variados) para permitir testar as
 * telas e a persistência fim-a-fim.
 *
 * <p>Como Evento e Funcionário ainda são stubs em memória (recriados a cada
 * inicialização), as tarefas/equipes persistidas são LIMPAS e recriadas a cada
 * start, garantindo que as referências fiquem sempre consistentes.</p>
 */
class DadosDemoSeeder {

    private final EventoRepository eventoRepo;
    private final FuncionarioRepository funcRepo;
    private final EquipeRepository equipeRepo;
    private final TarefaRepository tarefaRepo;
    private final ResponsavelTarefaRepository responsavelRepo;
    private final TarefaService tarefaService;
    private final DependenciaService dependenciaService;
    private final Logger log;

    private final Map<String, String> funcIdPorNome = new LinkedHashMap<>();
    private final Map<String, String> equipeIdPorNome = new LinkedHashMap<>();
    private final Map<String, String> taskIdPorChave = new LinkedHashMap<>();

    DadosDemoSeeder(EventoRepository eventoRepo, FuncionarioRepository funcRepo, EquipeRepository equipeRepo,
            TarefaRepository tarefaRepo, ResponsavelTarefaRepository responsavelRepo,
            TarefaService tarefaService, DependenciaService dependenciaService, Logger log) {
        this.eventoRepo = eventoRepo;
        this.funcRepo = funcRepo;
        this.equipeRepo = equipeRepo;
        this.tarefaRepo = tarefaRepo;
        this.responsavelRepo = responsavelRepo;
        this.tarefaService = tarefaService;
        this.dependenciaService = dependenciaService;
        this.log = log;
    }

    void semear() {
        limparPersistidos();

        String eventoId = eventoRepo.salvar(new Evento()).getId();
        criarFuncionarios();
        criarEquipes(eventoId);
        criarTarefas();

        log.info("==================== SEED DE DEMONSTRAÇÃO ====================");
        log.info("Evento, {} funcionários, {} equipes e {} tarefas criadas.",
                funcIdPorNome.size(), equipeIdPorNome.size(), taskIdPorChave.size());
        log.info("Equipes: {}", equipeIdPorNome.keySet());
        log.info("Abra a aba 'Tarefas' no front — os dados já aparecem populados.");
        log.info("=============================================================");
    }

    private void limparPersistidos() {
        for (Tarefa t : tarefaRepo.listarTodos()) {
            responsavelRepo.listarPorTarefa(t.getId()).forEach(r -> responsavelRepo.remover(r.getId()));
            tarefaRepo.remover(t.getId());
        }
        equipeRepo.listarTodos().forEach(e -> equipeRepo.remover(e.getId()));
    }

    // ---- Funcionários ----
    private void func(String nome, String cargo, String disponibilidade) {
        Funcionario f = funcRepo.salvar(new Funcionario(nome, cargo, disponibilidade));
        funcIdPorNome.put(nome, f.getId());
    }

    private void criarFuncionarios() {
        func("Maria Silva", "GERENTE", "INTEGRAL");
        func("João Silva", "GERENTE", "INTEGRAL");
        func("Ana Costa", "ANALISTA", "MANHA");
        func("Carlos Dias", "ASSISTENTE", "TARDE");
        func("João Santos", "TECNICO", "TARDE");
        func("Marcos Paulo", "TECNICO", "INTEGRAL");
        func("Pedro Oliveira", "ASSISTENTE", "INTEGRAL");
        func("Carla Mendes", "ASSISTENTE", "TARDE");
        func("Ricardo Alves", "TECNICO", "NOITE");
        func("Bruno Lima", "ASSISTENTE", "MANHA");
    }

    // ---- Equipes ----
    private void equipe(String eventoId, String nome, String liderNome, String... outrosNomes) {
        Equipe eq = new Equipe(eventoId, nome, funcIdPorNome.get(liderNome));
        for (String n : outrosNomes) {
            eq.adicionarMembro(funcIdPorNome.get(n));
        }
        equipeIdPorNome.put(nome, equipeRepo.salvar(eq).getId());
    }

    private void criarEquipes(String eventoId) {
        equipe(eventoId, "Coordenação", "Maria Silva", "João Silva");
        equipe(eventoId, "Logística", "Ana Costa", "Carlos Dias");
        equipe(eventoId, "Técnico", "João Santos", "Marcos Paulo");
        equipe(eventoId, "Atendimento", "Pedro Oliveira", "Carla Mendes");
        equipe(eventoId, "Segurança", "Ricardo Alves", "Bruno Lima");
    }

    // ---- Tarefas ----
    private LocalDateTime d(int mes, int dia) {
        return LocalDate.of(2026, mes, dia).atStartOfDay();
    }

    private void tarefa(String chave, String equipeNome, String titulo, String descricao,
            LocalDateTime inicio, LocalDateTime fim, String... responsaveisNomes) {
        Tarefa t = tarefaService.criarTarefa(
                new Tarefa(equipeIdPorNome.get(equipeNome), titulo, descricao, inicio, fim));
        for (String r : responsaveisNomes) {
            tarefaService.atribuirResponsavel(t.getId(), funcIdPorNome.get(r));
        }
        taskIdPorChave.put(chave, t.getId());
    }

    private void dep(String chave, String predChave) {
        dependenciaService.adicionarDependencia(taskIdPorChave.get(chave), taskIdPorChave.get(predChave));
    }

    private void criarTarefas() {
        tarefa("t1", "Coordenação", "Confirmar contratação do local",
                "Assinar contrato com o Centro de Convenções e confirmar a data.", d(4, 1), d(4, 22), "Maria Silva");
        tarefa("t2", "Técnico", "Configurar sistema de som",
                "Instalar e testar todos os equipamentos de áudio do auditório.", d(4, 23), d(5, 2), "João Santos");
        tarefa("t3", "Logística", "Finalizar menu com buffet",
                "Definir cardápio completo com a empresa de catering.", d(4, 23), d(4, 30), "Ana Costa");
        tarefa("t4", "Técnico", "Testar iluminação do auditório",
                "Verificar e calibrar a iluminação cênica antes do ensaio.", d(5, 3), d(5, 5), "João Santos");
        tarefa("t5", "Atendimento", "Enviar convites aos participantes",
                "Disparar e-mails de convite com confirmação de presença.", d(4, 1), d(4, 20), "Pedro Oliveira");
        tarefa("t6", "Atendimento", "Preparar material de recepção",
                "Montar kits de boas-vindas, crachás e pastas.", d(5, 8), d(5, 10), "Pedro Oliveira", "Carla Mendes");
        tarefa("t7", "Segurança", "Revisar plano de segurança",
                "Atualizar e aprovar o plano de segurança e emergência.", d(4, 23), d(5, 8), "Ricardo Alves");
        tarefa("t8", "Coordenação", "Credenciar equipe de apoio",
                "Registrar e emitir credenciais para a equipe de apoio.", d(5, 13), d(5, 15), "Maria Silva", "João Silva");

        dep("t2", "t1");
        dep("t3", "t1");
        dep("t4", "t2");
        dep("t6", "t5");
        dep("t7", "t1");
        dep("t8", "t6");
        dep("t8", "t7");

        // Status variados: t1 concluída; t2, t3 e t7 em andamento; o resto pendente
        // (t5 vence no passado -> "atrasada"; t4/t6/t8 ficam "bloqueadas" pela UI).
        String t1 = taskIdPorChave.get("t1");
        tarefaService.iniciarTarefa(t1);
        tarefaService.concluirTarefa(t1);
        tarefaService.iniciarTarefa(taskIdPorChave.get("t2"));
        tarefaService.iniciarTarefa(taskIdPorChave.get("t3"));
        tarefaService.iniciarTarefa(taskIdPorChave.get("t7"));
    }
}
