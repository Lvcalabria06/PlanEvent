package presentationbackend.scaffolding;

import domain.agenda.repository.CompromissoRepository;
import domain.agenda.repository.LembreteRepository;
import domain.agenda.service.CompromissoService;
import domain.agenda.service.LembreteService;
import domain.contrato.repository.ContratoRepository;
import domain.contrato.service.ContratoService;
import domain.equipe.repository.EquipeRepository;
import domain.evento.repository.EventoRepository;
import domain.financeiro.repository.DespesaRepository;
import domain.fornecedor.repository.FornecedorRepository;
import domain.fornecedor.service.FornecedorService;
import domain.funcionario.repository.FuncionarioRepository;
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

/**
 * Andaime (scaffolding) provisório para testar a fatia de Tarefas/Dependências
 * fim-a-fim sem depender da persistência dos módulos de Equipe/Evento/Funcionário
 * (responsabilidade de outros integrantes).
 *
 * <p>Cada bean usa {@link ConditionalOnMissingBean}: assim que o módulo real
 * fornecer sua implementação, este stub deixa de ser registrado, sem conflito.</p>
 */
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
            FornecedorRepository fornecedorRepository,
            FornecedorService fornecedorService,
            ContratoRepository contratoRepository,
            ContratoService contratoService) {
        return args -> {
            boolean apoioStubsAtivo = eventoRepository instanceof InMemoryEventoRepository
                    && funcionarioRepository instanceof InMemoryFuncionarioRepository;
            if (!apoioStubsAtivo) {
                return;
            }

            new DadosDemoSeeder(eventoRepository, funcionarioRepository, equipeRepository,
                    tarefaRepository, responsavelTarefaRepository, tarefaService, dependenciaService,
                    compromissoRepository, lembreteRepository, compromissoService, lembreteService,
                    fornecedorRepository, fornecedorService, contratoRepository, contratoService, log)
                    .semear();
        };
    }
}
