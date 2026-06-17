package presentationbackend.scaffolding;

import domain.agenda.repository.CompromissoRepository;
import domain.agenda.repository.LembreteRepository;
import domain.agenda.service.CompromissoService;
import domain.agenda.service.LembreteService;
import domain.equipe.repository.EquipeRepository;
import domain.evento.repository.EventoRepository;
import domain.financeiro.repository.DespesaRepository;
import domain.funcionario.repository.FuncionarioRepository;
import domain.local.repository.AgendaLocalRepository;
import domain.local.repository.AvaliacaoContextualLocalRepository;
import domain.local.repository.IndisponibilidadeLocalRepository;
import domain.local.repository.LocalRepository;
import domain.local.repository.ManutencaoRepository;
import domain.local.repository.ReservaLocalRepository;
import domain.local.turno.repository.TurnoOperacionalRepository;
import domain.tarefa.repository.ResponsavelTarefaRepository;
import domain.tarefa.repository.TarefaRepository;
import domain.tarefa.service.DependenciaService;
import domain.tarefa.service.TarefaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ScaffoldingConfig {

    private static final Logger log = LoggerFactory.getLogger(ScaffoldingConfig.class);

    @Bean
    @ConditionalOnMissingBean(EventoRepository.class)
    public EventoRepository inMemoryEventoRepository() {
        return new InMemoryEventoRepository();
    }

    @Bean
    @ConditionalOnMissingBean(FuncionarioRepository.class)
    public FuncionarioRepository inMemoryFuncionarioRepository() {
        return new InMemoryFuncionarioRepository();
    }

    @Bean
    @ConditionalOnMissingBean(EquipeRepository.class)
    public EquipeRepository inMemoryEquipeRepository() {
        return new InMemoryEquipeRepository();
    }

    @Bean
    @ConditionalOnMissingBean(DespesaRepository.class)
    public DespesaRepository inMemoryDespesaRepository() {
        return new InMemoryDespesaRepository();
    }

    @Bean
    @ConditionalOnMissingBean(LocalRepository.class)
    public LocalRepository inMemoryLocalRepository() {
        return new InMemoryLocalRepository();
    }

    @Bean
    @ConditionalOnMissingBean(AgendaLocalRepository.class)
    public AgendaLocalRepository inMemoryAgendaLocalRepository() {
        return new InMemoryAgendaLocalRepository();
    }

    @Bean
    @ConditionalOnMissingBean(ReservaLocalRepository.class)
    public ReservaLocalRepository inMemoryReservaLocalRepository(AgendaLocalRepository agendaLocalRepository) {
        return new InMemoryReservaLocalRepository(agendaLocalRepository);
    }

    @Bean
    @ConditionalOnMissingBean(IndisponibilidadeLocalRepository.class)
    public IndisponibilidadeLocalRepository inMemoryIndisponibilidadeLocalRepository() {
        return new InMemoryIndisponibilidadeLocalRepository();
    }

    @Bean
    @ConditionalOnMissingBean(ManutencaoRepository.class)
    public ManutencaoRepository inMemoryManutencaoRepository() {
        return new InMemoryManutencaoRepository();
    }

    @Bean
    @ConditionalOnMissingBean(AvaliacaoContextualLocalRepository.class)
    public AvaliacaoContextualLocalRepository inMemoryAvaliacaoContextualRepository() {
        return new InMemoryAvaliacaoContextualRepository();
    }

    @Bean
    @ConditionalOnMissingBean(TurnoOperacionalRepository.class)
    public TurnoOperacionalRepository inMemoryTurnoOperacionalRepository() {
        return new InMemoryTurnoOperacionalRepository();
    }

    /**
     * Semeia um conjunto realista de dados de demonstração (via {@link DadosDemoSeeder})
     * apenas enquanto Evento e Funcionário ainda forem stubs em memória.
     */
    @Bean
    public CommandLineRunner seedDadosDemo(EventoRepository eventoRepository,
            FuncionarioRepository funcionarioRepository,
            EquipeRepository equipeRepository,
            TarefaRepository tarefaRepository,
            ResponsavelTarefaRepository responsavelTarefaRepository,
            TarefaService tarefaService,
            DependenciaService dependenciaService,
            CompromissoRepository compromissoRepository,
            LembreteRepository lembreteRepository,
            CompromissoService compromissoService,
            LembreteService lembreteService,
            LocalRepository localRepository) {
        return args -> {
            EventoLocaisSeeder.semearSeVazio(localRepository);

            // O seed só faz sentido enquanto Funcionário ainda for um stub em memória
            // (recriado com IDs novos a cada start). Usamos instanceof em vez de comparar
            // getSimpleName(): repositórios anotados com @Repository (ex.: o EventoRepository
            // da infraestrutura) são embrulhados em proxy CGLIB pelo Spring, e o nome simples
            // da classe deixa de bater, fazendo o seed ser pulado por engano. O bean de
            // funcionário é um @Bean simples (sem proxy), então o instanceof é confiável.
            boolean funcionarioEmMemoria = funcionarioRepository instanceof InMemoryFuncionarioRepository;
            if (!funcionarioEmMemoria) {
                log.info("Seed de demonstração ignorado (repositório real de funcionário ativo).");
                return;
            }

            new DadosDemoSeeder(eventoRepository, funcionarioRepository, equipeRepository,
                    tarefaRepository, responsavelTarefaRepository, tarefaService, dependenciaService,
                    compromissoRepository, lembreteRepository, compromissoService, lembreteService, log)
                    .semear();
        };
    }
}
