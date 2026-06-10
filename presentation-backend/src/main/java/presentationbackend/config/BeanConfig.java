package presentationbackend.config;

import application.dependencia.usecase.DependenciaUseCase;
import application.dependencia.usecase.DependenciaUseCaseImpl;
import application.tarefa.usecase.TarefaUseCase;
import application.tarefa.usecase.TarefaUseCaseImpl;
import domain.equipe.repository.EquipeRepository;
import domain.evento.repository.EventoRepository;
import domain.funcionario.repository.FuncionarioRepository;
import domain.tarefa.repository.ResponsavelTarefaRepository;
import domain.tarefa.repository.TarefaRepository;
import domain.tarefa.service.DependenciaService;
import domain.tarefa.service.DependenciaServiceImpl;
import domain.tarefa.service.TarefaService;
import domain.tarefa.service.TarefaServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registra como beans os serviços de domínio e casos de uso (classes puras, sem
 * anotações Spring), injetando os adapters de repositório descobertos na
 * infraestrutura.
 */
@Configuration
public class BeanConfig {

    @Bean
    public TarefaService tarefaService(TarefaRepository tarefaRepository,
            EquipeRepository equipeRepository,
            EventoRepository eventoRepository,
            FuncionarioRepository funcionarioRepository,
            ResponsavelTarefaRepository responsavelTarefaRepository) {
        return new TarefaServiceImpl(tarefaRepository, equipeRepository, eventoRepository,
                funcionarioRepository, responsavelTarefaRepository);
    }

    @Bean
    public DependenciaService dependenciaService(TarefaRepository tarefaRepository,
            EquipeRepository equipeRepository) {
        return new DependenciaServiceImpl(tarefaRepository, equipeRepository);
    }

    @Bean
    public TarefaUseCase tarefaUseCase(TarefaService tarefaService) {
        return new TarefaUseCaseImpl(tarefaService);
    }

    @Bean
    public DependenciaUseCase dependenciaUseCase(DependenciaService dependenciaService,
            TarefaService tarefaService) {
        return new DependenciaUseCaseImpl(dependenciaService, tarefaService);
    }
}
