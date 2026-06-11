package presentationbackend.scaffolding;

import domain.agenda.entity.Compromisso;
import domain.agenda.entity.Lembrete;
import domain.agenda.repository.CompromissoRepository;
import domain.agenda.repository.LembreteRepository;
import domain.agenda.service.CompromissoService;
import domain.agenda.service.LembreteService;
import domain.contrato.entity.Contrato;
import domain.contrato.repository.ContratoRepository;
import domain.contrato.service.ContratoService;
import domain.contrato.valueobject.DadosParteContrato;
import domain.contrato.valueobject.TipoContrato;
import domain.equipe.entity.Equipe;
import domain.equipe.repository.EquipeRepository;
import domain.evento.entity.Evento;
import domain.evento.repository.EventoRepository;
import domain.fornecedor.entity.Fornecedor;
import domain.fornecedor.repository.FornecedorRepository;
import domain.fornecedor.service.FornecedorService;
import domain.funcionario.entity.Funcionario;
import domain.funcionario.repository.FuncionarioRepository;
import domain.tarefa.entity.Tarefa;
import domain.tarefa.repository.ResponsavelTarefaRepository;
import domain.tarefa.repository.TarefaRepository;
import domain.tarefa.service.DependenciaService;
import domain.tarefa.service.TarefaService;
import org.slf4j.Logger;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
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
    private final FornecedorRepository fornecedorRepo;
    private final FornecedorService fornecedorService;
    private final ContratoRepository contratoRepo;
    private final ContratoService contratoService;
    private final Logger log;

    private final Map<String, String> funcIdPorNome = new LinkedHashMap<>();
    private final Map<String, String> equipeIdPorNome = new LinkedHashMap<>();
    private final Map<String, String> taskIdPorChave = new LinkedHashMap<>();
    private final Map<String, String> compromissoIdPorChave = new LinkedHashMap<>();
    private final Map<String, String> fornecedorIdPorNome = new LinkedHashMap<>();

    DadosDemoSeeder(EventoRepository eventoRepo, FuncionarioRepository funcRepo, EquipeRepository equipeRepo,
            TarefaRepository tarefaRepo, ResponsavelTarefaRepository responsavelRepo,
            TarefaService tarefaService, DependenciaService dependenciaService,
            CompromissoRepository compromissoRepo, LembreteRepository lembreteRepo,
            CompromissoService compromissoService, LembreteService lembreteService,
            FornecedorRepository fornecedorRepo, FornecedorService fornecedorService,
            ContratoRepository contratoRepo, ContratoService contratoService, Logger log) {
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
        this.fornecedorRepo = fornecedorRepo;
        this.fornecedorService = fornecedorService;
        this.contratoRepo = contratoRepo;
        this.contratoService = contratoService;
        this.log = log;
    }

    void semear() {
        limparPersistidos();

        String eventoId = eventoRepo.salvar(new Evento()).getId();
        criarFuncionarios();
        criarEquipes(eventoId);
        criarTarefas();
        criarAgenda(eventoId);
        criarFornecedores();
        criarContratos(eventoId);

        log.info("==================== SEED DE DEMONSTRAÇÃO ====================");
        log.info("Evento, {} funcionários, {} equipes, {} tarefas, {} compromissos, {} fornecedores e {} contratos criados.",
                funcIdPorNome.size(), equipeIdPorNome.size(), taskIdPorChave.size(),
                compromissoIdPorChave.size(), fornecedorIdPorNome.size(),
                contratoRepo.listarTodos().size());
        log.info("Equipes: {}", equipeIdPorNome.keySet());
        log.info("Abra as abas 'Tarefas', 'Fornecedores' e 'Contratos' no front.");
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
        contratoRepo.listarTodos().forEach(c -> contratoRepo.remover(c.getId()));
        fornecedorRepo.listarTodos().forEach(f -> fornecedorRepo.remover(f.getId()));
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

    // ---- Agenda (compromissos e lembretes) ----
    private LocalDateTime dt(int mes, int dia, int hora, int min) {
        return LocalDate.of(2026, mes, dia).atTime(hora, min);
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

        compromisso("c1", gestorId, eventoId, "Revisão do Plano de Segurança",
                dt(7, 10, 9, 0), dt(7, 10, 10, 30));
        compromisso("c2", gestorId, eventoId, "Alinhamento com Equipe Técnica",
                dt(7, 10, 14, 0), dt(7, 10, 15, 0));
        compromisso("c3", gestorId, eventoId, "Visita ao Local do Evento",
                dt(7, 11, 10, 0), dt(7, 11, 12, 0));
        compromisso("c4", gestorId, eventoId, "Briefing com Fornecedores",
                dt(7, 12, 11, 0), dt(7, 12, 12, 30));

        compromissoService.iniciarCompromisso(compromissoIdPorChave.get("c4"));

        lembreteCompromisso("c1", dt(7, 9, 18, 0));
        lembreteCompromisso("c2", dt(7, 10, 8, 0));
        lembreteCompromisso("c3", dt(7, 10, 19, 0));
        lembreteCompromisso("c4", dt(7, 11, 17, 0));
        lembreteEvento(eventoId, dt(7, 15, 9, 0));
    }

    // ---- Fornecedores ----
    private void fornecedor(String nome, String cnpj, String categoria, String contato) {
        Fornecedor f = fornecedorService.cadastrarFornecedor(nome, cnpj, categoria, contato);
        fornecedorIdPorNome.put(nome, f.getId());
    }

    private void criarFornecedores() {
        fornecedor("Som & Luz Produções", "11.222.333/0001-81", "Audiovisual",
                "contato@someluz.com.br");
        fornecedor("Buffet Sabor do Sul", "22.333.444/0001-97", "Alimentação",
                "contato@sabordosul.com.br");
        fornecedor("Segurança Total Ltda", "33.444.555/0001-06", "Segurança",
                "operacoes@segurancatotal.com.br");
    }

    // ---- Contratos ----
    private void contrato(String eventoId, String fornecedorNome, TipoContrato tipo,
                          String objeto, BigDecimal valor,
                          LocalDateTime inicio, LocalDateTime fim,
                          String parteContratante, String parteFornecedor) {
        String fornecedorId = fornecedorIdPorNome.get(fornecedorNome);
        var partes = List.of(
                new DadosParteContrato(parteContratante, "CONTRATANTE"),
                new DadosParteContrato(parteFornecedor, "FORNECEDOR")
        );
        contratoService.criarContrato(
                new Contrato(eventoId, fornecedorId, tipo, objeto, valor, inicio, fim, partes));
    }

    private void criarContratos(String eventoId) {
        contrato(eventoId, "Som & Luz Produções",
                TipoContrato.PRESTACAO_SERVICO,
                "Locação e operação de equipamentos de som e iluminação para o evento.",
                new BigDecimal("18500.00"),
                dt(6, 1, 0, 0), dt(8, 31, 23, 59),
                "Organizadora PlanEvent", "Som & Luz Produções");

        contrato(eventoId, "Buffet Sabor do Sul",
                TipoContrato.FORNECEDOR,
                "Fornecimento de serviço completo de buffet para 300 pessoas.",
                new BigDecimal("24000.00"),
                dt(6, 1, 0, 0), dt(8, 31, 23, 59),
                "Organizadora PlanEvent", "Buffet Sabor do Sul");
    }
}
