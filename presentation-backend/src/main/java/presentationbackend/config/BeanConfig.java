package presentationbackend.config;

import application.agenda.usecase.CompromissoUseCase;
import application.agenda.usecase.CompromissoUseCaseImpl;
import application.agenda.usecase.LembreteUseCase;
import application.agenda.usecase.LembreteUseCaseImpl;
import application.contrato.usecase.ContratoUseCase;
import application.contrato.usecase.ContratoUseCaseImpl;
import application.dependencia.usecase.DependenciaUseCase;
import application.dependencia.usecase.DependenciaUseCaseImpl;
import application.fornecedor.usecase.FornecedorUseCase;
import application.fornecedor.usecase.FornecedorUseCaseImpl;
import application.tarefa.usecase.TarefaUseCase;
import application.tarefa.usecase.TarefaUseCaseImpl;
import domain.agenda.observer.LembreteNotificacaoSubject;
import domain.agenda.port.AlertaLembretePort;
import domain.agenda.repository.CompromissoRepository;
import domain.agenda.repository.LembreteRepository;
import domain.agenda.service.CompromissoService;
import domain.agenda.service.CompromissoServiceImpl;
import domain.agenda.service.LembreteService;
import domain.agenda.service.LembreteServiceImpl;
import domain.contrato.repository.ContratoRepository;
import domain.contrato.service.ContratoService;
import domain.contrato.service.ContratoServiceImpl;
import domain.equipe.repository.EquipeRepository;
import domain.evento.repository.EventoRepository;
import domain.financeiro.repository.DespesaRepository;
import domain.fornecedor.repository.FornecedorRepository;
import domain.fornecedor.service.FornecedorService;
import domain.fornecedor.service.FornecedorServiceImpl;
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

    @Bean
    public CompromissoService compromissoService(CompromissoRepository compromissoRepository,
            LembreteRepository lembreteRepository) {
        return new CompromissoServiceImpl(compromissoRepository, lembreteRepository);
    }

    @Bean
    public LembreteNotificacaoSubject lembreteNotificacaoSubject(LembreteRepository lembreteRepository,
            AlertaLembretePort alertaLembretePort) {
        return LembreteServiceImpl.criarSubject(lembreteRepository, alertaLembretePort);
    }

    @Bean
    public LembreteService lembreteService(LembreteRepository lembreteRepository,
            CompromissoRepository compromissoRepository,
            LembreteNotificacaoSubject lembreteNotificacaoSubject) {
        return new LembreteServiceImpl(lembreteRepository, compromissoRepository, lembreteNotificacaoSubject);
    }

    @Bean
    public CompromissoUseCase compromissoUseCase(CompromissoService compromissoService) {
        return new CompromissoUseCaseImpl(compromissoService);
    }

    @Bean
    public LembreteUseCase lembreteUseCase(LembreteService lembreteService,
            CompromissoService compromissoService) {
        return new LembreteUseCaseImpl(lembreteService, compromissoService);
    }

    @Bean
    public FornecedorService fornecedorService(FornecedorRepository fornecedorRepository,
            ContratoRepository contratoRepository,
            DespesaRepository despesaRepository,
            EventoRepository eventoRepository) {
        return new FornecedorServiceImpl(fornecedorRepository, contratoRepository,
                despesaRepository, eventoRepository);
    }

    @Bean
    public FornecedorUseCase fornecedorUseCase(FornecedorService fornecedorService) {
        return new FornecedorUseCaseImpl(fornecedorService);
    }

    @Bean
    public ContratoService contratoService(ContratoRepository contratoRepository,
            EventoRepository eventoRepository,
            FornecedorRepository fornecedorRepository) {
        return new ContratoServiceImpl(contratoRepository, eventoRepository, fornecedorRepository);
    }

    @Bean
    public ContratoUseCase contratoUseCase(ContratoService contratoService) {
        return new ContratoUseCaseImpl(contratoService);
    }
}
