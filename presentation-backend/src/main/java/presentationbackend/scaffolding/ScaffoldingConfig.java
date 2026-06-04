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
            // Roda enquanto Evento e Funcionário ainda forem stubs (módulos não plugados).
            // A Equipe pode já ser persistência real (módulo da colega) — usamos o
            // repositório injetado de qualquer forma.
            boolean apoioStubsAtivo = eventoRepository instanceof InMemoryEventoRepository
                    && funcionarioRepository instanceof InMemoryFuncionarioRepository;
            if (!apoioStubsAtivo) {
                return;
            }

            Evento evento = eventoRepository.salvar(new Evento());
            Funcionario funcionario = funcionarioRepository.salvar(
                    new Funcionario("Funcionario Demo", "ANALISTA", "INTEGRAL"));
            Equipe equipe = equipeRepository.salvar(
                    new Equipe(evento.getId(), "Equipe Demo", funcionario.getId()));

            boolean equipePersistida = !(equipeRepository instanceof InMemoryEquipeRepository);
            log.info("==================== DADOS DEMO ====================");
            log.info("eventoId      = {}  (stub in-memory)", evento.getId());
            log.info("funcionarioId = {}  (stub in-memory; membro da equipe; use como responsavel)", funcionario.getId());
            log.info("equipeId      = {}  ({}; use ao criar tarefas)",
                    equipe.getId(), equipePersistida ? "persistida no banco" : "stub in-memory");
            log.info("===================================================");
        };
    }
}
