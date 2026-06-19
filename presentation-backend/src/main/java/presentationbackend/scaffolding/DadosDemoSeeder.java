package presentationbackend.scaffolding;

import domain.agenda.entity.Compromisso;
import domain.agenda.entity.Lembrete;
import domain.agenda.repository.CompromissoRepository;
import domain.agenda.repository.LembreteRepository;
import domain.agenda.service.CompromissoService;
import domain.agenda.service.LembreteService;
import domain.equipe.entity.Equipe;
import domain.equipe.repository.EquipeRepository;
import domain.evento.entity.Evento;
import domain.evento.repository.EventoRepository;
import domain.funcionario.entity.Funcionario;
import domain.funcionario.repository.FuncionarioRepository;
import domain.local.repository.LocalRepository;
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
    private final CompromissoRepository compromissoRepo;
    private final LembreteRepository lembreteRepo;
    private final CompromissoService compromissoService;
    private final LembreteService lembreteService;
    private final LocalRepository localRepository;
    private final Logger log;

    private final Map<String, String> funcIdPorNome = new LinkedHashMap<>();
    private final Map<String, String> equipeIdPorNome = new LinkedHashMap<>();
    private final Map<String, String> taskIdPorChave = new LinkedHashMap<>();
    private final Map<String, String> compromissoIdPorChave = new LinkedHashMap<>();

    DadosDemoSeeder(EventoRepository eventoRepo, FuncionarioRepository funcRepo, EquipeRepository equipeRepo,
            TarefaRepository tarefaRepo, ResponsavelTarefaRepository responsavelRepo,
            TarefaService tarefaService, DependenciaService dependenciaService,
            CompromissoRepository compromissoRepo, LembreteRepository lembreteRepo,
            CompromissoService compromissoService, LembreteService lembreteService,
            LocalRepository localRepository, Logger log) {
        this.eventoRepo = eventoRepo;
        this.funcRepo = funcRepo;
        this.equipeRepo = equipeRepo;
        this.tarefaRepo = tarefaRepo;
        this.responsavelRepo = responsavelRepo;
        this.tarefaService = tarefaService;
        this.dependenciaService = dependenciaService;
        this.compromissoRepo = compromissoRepo;
        this.lembreteRepo = lembreteRepo;
        this.compromissoService = compromissoService;
        this.lembreteService = lembreteService;
        this.localRepository = localRepository;
        this.log = log;
    }

    void semear() {
        limparPersistidos();

        Evento eventoDemo = DemoEventoBasicoSeeder.criarEventoDemo(localRepository);
        String eventoId = eventoRepo.salvar(eventoDemo).getId();
        criarFuncionarios();
        criarEquipes(eventoId);
        criarTarefas();
        criarAgenda(eventoId);

        log.info("==================== SEED DE DEMONSTRAÇÃO ====================");
        log.info("Evento, {} funcionários, {} equipes, {} tarefas, {} compromissos e {} lembretes criados.",
                funcIdPorNome.size(), equipeIdPorNome.size(), taskIdPorChave.size(),
                compromissoIdPorChave.size(), lembreteRepo.listarTodos().size());
        log.info("Equipes: {}", equipeIdPorNome.keySet());
        log.info("Abra as abas 'Tarefas' e 'Agenda' no front.");
        log.info("=============================================================");
    }

    private void limparPersistidos() {
        for (Lembrete l : lembreteRepo.listarTodos()) {
            lembreteRepo.remover(l.getId());
        }
        for (Compromisso c : compromissoRepo.listarTodos()) {
            compromissoRepo.remover(c.getId());
        }
        for (Tarefa t : tarefaRepo.listarTodos()) {
            responsavelRepo.listarPorTarefa(t.getId()).forEach(r -> responsavelRepo.remover(r.getId()));
            tarefaRepo.remover(t.getId());
        }
        equipeRepo.listarTodos().forEach(e -> equipeRepo.remover(e.getId()));
    }

    // ---- Funcionários ----
    private void func(String nome, String cargo, String disponibilidade, String... comps) {
        Funcionario f = funcRepo.salvar(new Funcionario(nome, cargo, disponibilidade, java.util.List.of(comps)));
        funcIdPorNome.put(nome, f.getId());
    }

    private void criarFuncionarios() {
        func("Maria Silva", "GERENTE", "INTEGRAL", "Liderança", "Gestão", "Eventos");
        func("João Silva", "GERENTE", "INTEGRAL", "Liderança", "Orçamento", "Logística");
        func("Ana Costa", "ANALISTA", "MANHA", "Dados", "Excel", "Inglês");
        func("Carlos Dias", "ASSISTENTE", "TARDE", "Organização", "Suporte");
        func("João Santos", "TECNICO", "TARDE", "Som", "Iluminação");
        func("Marcos Paulo", "TECNICO", "INTEGRAL", "Projeção", "Equipamento A/V");
        func("Pedro Oliveira", "ASSISTENTE", "INTEGRAL", "Recepção", "Atendimento");
        func("Carla Mendes", "ASSISTENTE", "TARDE", "Credenciamento", "Comunicação");
        func("Ricardo Alves", "TECNICO", "NOITE", "Segurança", "Prevenção");
        func("Bruno Lima", "ASSISTENTE", "MANHA", "Logística", "Atendimento");
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

        String t1 = taskIdPorChave.get("t1");
        tarefaService.iniciarTarefa(t1);
        tarefaService.concluirTarefa(t1);
        tarefaService.iniciarTarefa(taskIdPorChave.get("t2"));
        tarefaService.iniciarTarefa(taskIdPorChave.get("t3"));
        tarefaService.iniciarTarefa(taskIdPorChave.get("t7"));
    }

    // ---- Agenda (compromissos e lembretes) ----
    private LocalDateTime dt(int mes, int dia, int hora, int min) {
        return LocalDate.of(2026, mes, dia).atTime(hora, min);
    }

    private LocalDateTime proximoSlot(int diasOffset, int hora, int min) {
        LocalDateTime candidato = LocalDate.now().plusDays(diasOffset).atTime(hora, min);
        LocalDateTime agora = LocalDateTime.now();
        while (!candidato.isAfter(agora)) {
            candidato = candidato.plusDays(1);
        }
        return candidato;
    }

    private LocalDateTime horarioLembreteAntes(LocalDateTime inicioCompromisso) {
        LocalDateTime candidato = inicioCompromisso.minusHours(1);
        LocalDateTime agora = LocalDateTime.now();
        if (!candidato.isAfter(agora)) {
            candidato = agora.plusMinutes(10);
        }
        if (!candidato.isBefore(inicioCompromisso)) {
            candidato = inicioCompromisso.minusMinutes(15);
        }
        return candidato;
    }

    private void compromisso(String chave, String gestorId, String eventoId, String titulo,
            LocalDateTime inicio, LocalDateTime fim) {
        Compromisso c = compromissoService.criarCompromisso(
                new Compromisso(gestorId, eventoId, titulo, "Compromisso de demonstração.", inicio, fim));
        compromissoIdPorChave.put(chave, c.getId());
    }

    private void lembreteCompromisso(String compromissoChave, LocalDateTime horario) {
        String compId = compromissoIdPorChave.get(compromissoChave);
        Compromisso comp = compromissoService.buscarCompromisso(compId);
        lembreteService.criarLembrete(new Lembrete(compId, comp.getEventoId(), horario, comp.getDataInicio()));
    }

    private void lembreteEvento(String eventoId, LocalDateTime horario) {
        lembreteService.criarLembrete(new Lembrete(null, eventoId, horario, null));
    }

    private void criarAgenda(String eventoId) {
        String gestorId = funcIdPorNome.get("Maria Silva");

        LocalDateTime c1Inicio = proximoSlot(1, 15, 0);
        LocalDateTime c2Inicio = proximoSlot(2, 10, 0);
        LocalDateTime c3Inicio = proximoSlot(3, 10, 0);
        LocalDateTime c4Inicio = proximoSlot(4, 11, 0);
        LocalDateTime c5Inicio = proximoSlot(6, 9, 0);
        LocalDateTime c6Inicio = proximoSlot(1, 18, 0);

        compromisso("c1", gestorId, eventoId, "Revisão do Plano de Segurança",
                c1Inicio, c1Inicio.plusMinutes(90));
        compromisso("c2", gestorId, eventoId, "Alinhamento com Equipe Técnica",
                c2Inicio, c2Inicio.plusHours(1));
        compromisso("c3", gestorId, eventoId, "Visita ao Local do Evento",
                c3Inicio, c3Inicio.plusHours(2));
        compromisso("c4", gestorId, eventoId, "Briefing com Fornecedores",
                c4Inicio, c4Inicio.plusMinutes(90));
        compromisso("c5", gestorId, eventoId, "Checkpoint Semanal da Agenda",
                c5Inicio, c5Inicio.plusHours(1));
        compromisso("c6", gestorId, eventoId, "Sincronização com Patrocinadores",
                c6Inicio, c6Inicio.plusHours(1));

        compromissoService.iniciarCompromisso(compromissoIdPorChave.get("c4"));
        compromissoService.iniciarCompromisso(compromissoIdPorChave.get("c6"));
        compromissoService.concluirCompromisso(compromissoIdPorChave.get("c6"));

        lembreteCompromisso("c1", horarioLembreteAntes(c1Inicio));
        lembreteCompromisso("c2", horarioLembreteAntes(c2Inicio));
        lembreteCompromisso("c3", horarioLembreteAntes(c3Inicio));
        lembreteCompromisso("c4", horarioLembreteAntes(c4Inicio));
        lembreteCompromisso("c5", horarioLembreteAntes(c5Inicio));
        lembreteEvento(eventoId, proximoSlot(5, 9, 0));
    }
}
