package presentationbackend.scaffolding;

import domain.equipe.entity.Equipe;
import domain.equipe.repository.EquipeRepository;
import domain.evento.entity.Evento;
import domain.evento.repository.EventoRepository;
import domain.funcionario.entity.Funcionario;
import domain.funcionario.repository.FuncionarioRepository;
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

    /**
     * Semeia um evento, um funcionário e uma equipe (com o funcionário como membro)
     * apenas quando os stubs in-memory estão ativos, registrando os IDs no log para
     * uso nas chamadas da API.
     */
    @Bean
    public CommandLineRunner seedDadosDemo(EventoRepository eventoRepository,
            FuncionarioRepository funcionarioRepository,
            EquipeRepository equipeRepository) {
        return args -> {
            boolean usandoStubs = eventoRepository instanceof InMemoryEventoRepository
                    && funcionarioRepository instanceof InMemoryFuncionarioRepository
                    && equipeRepository instanceof InMemoryEquipeRepository;
            if (!usandoStubs) {
                return;
            }

            Evento evento = eventoRepository.salvar(new Evento());
            Funcionario funcionario = funcionarioRepository.salvar(
                    new Funcionario("Funcionario Demo", "ANALISTA", "INTEGRAL"));
            Equipe equipe = equipeRepository.salvar(
                    new Equipe(evento.getId(), "Equipe Demo", funcionario.getId()));

            log.info("==================== DADOS DEMO (stubs in-memory) ====================");
            log.info("eventoId      = {}", evento.getId());
            log.info("equipeId      = {}  (use ao criar tarefas)", equipe.getId());
            log.info("funcionarioId = {}  (membro da equipe; use como responsavel)", funcionario.getId());
            log.info("=====================================================================");
        };
    }
}
