package presentationbackend.config;

import application.agenda.usecase.CompromissoUseCase;
import application.agenda.usecase.CompromissoUseCaseImpl;
import application.agenda.usecase.LembreteUseCase;
import application.agenda.usecase.LembreteUseCaseImpl;
import application.contrato.usecase.ContratoUseCase;
import application.contrato.usecase.ContratoUseCaseImpl;
import application.dependencia.usecase.DependenciaUseCase;
import application.dependencia.usecase.DependenciaUseCaseImpl;
import application.evento.usecase.AlocacaoLocalUseCase;
import application.evento.usecase.AlocacaoLocalUseCaseImpl;
import application.evento.usecase.EventoUseCase;
import application.evento.usecase.EventoUseCaseImpl;
import application.financeiro.usecase.AcaoPosRelatorioUseCase;
import application.financeiro.usecase.AcaoPosRelatorioUseCaseImpl;
import application.financeiro.usecase.DespesaUseCase;
import application.financeiro.usecase.DespesaUseCaseImpl;
import application.financeiro.usecase.OrcamentoEventoUseCase;
import application.financeiro.usecase.OrcamentoEventoUseCaseImpl;
import application.financeiro.usecase.RelatorioFinanceiroUseCase;
import application.financeiro.usecase.RelatorioFinanceiroUseCaseImpl;
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
import domain.evento.service.EventoService;
import domain.evento.service.EventoServiceImpl;
import domain.evento.service.PlanejamentoAlocacaoLocalService;
import domain.evento.service.PlanejamentoAlocacaoLocalServiceImpl;
import domain.financeiro.repository.DespesaRepository;
import domain.financeiro.service.AcaoPosRelatorioService;
import domain.financeiro.service.DespesaService;
import domain.financeiro.service.OrcamentoEventoService;
import domain.financeiro.service.RelatorioFinanceiroService;
import domain.fornecedor.repository.FornecedorRepository;
import domain.fornecedor.service.FornecedorService;
import domain.fornecedor.service.FornecedorServiceImpl;
import domain.funcionario.repository.FuncionarioRepository;
import domain.local.repository.IndisponibilidadeLocalRepository;
import domain.local.repository.LocalRepository;
import domain.local.repository.ManutencaoRepository;
import domain.local.repository.ReservaLocalRepository;
import domain.local.service.LocalService;
import domain.local.service.LocalServiceImpl;
import domain.local.service.ManutencaoService;
import domain.local.service.ManutencaoServiceImpl;
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

    @Bean
    public EventoService eventoService(EventoRepository eventoRepository, LocalRepository localRepository,
            ContratoRepository contratoRepository) {
        return new EventoServiceImpl(eventoRepository, localRepository, contratoRepository);
    }

    @Bean
    public PlanejamentoAlocacaoLocalService planejamentoAlocacaoLocalService(
            EventoRepository eventoRepository,
            LocalRepository localRepository,
            ReservaLocalRepository reservaLocalRepository,
            IndisponibilidadeLocalRepository indisponibilidadeLocalRepository,
            ManutencaoRepository manutencaoRepository) {
        return new PlanejamentoAlocacaoLocalServiceImpl(
                eventoRepository,
                localRepository,
                reservaLocalRepository,
                indisponibilidadeLocalRepository,
                manutencaoRepository);
    }

    @Bean
    public EventoUseCase eventoUseCase(EventoService eventoService, LocalRepository localRepository) {
        return new EventoUseCaseImpl(eventoService, localRepository);
    }

    @Bean
    public AlocacaoLocalUseCase alocacaoLocalUseCase(
            PlanejamentoAlocacaoLocalService planejamentoAlocacaoLocalService,
            LocalRepository localRepository) {
        return new AlocacaoLocalUseCaseImpl(planejamentoAlocacaoLocalService, localRepository);
    }

    @Bean
    public DespesaUseCase despesaUseCase(DespesaService despesaService) {
        return new DespesaUseCaseImpl(despesaService);
    }

    @Bean
    public RelatorioFinanceiroUseCase relatorioFinanceiroUseCase(RelatorioFinanceiroService relatorioFinanceiroService) {
        return new RelatorioFinanceiroUseCaseImpl(relatorioFinanceiroService);
    }

    @Bean
    public OrcamentoEventoUseCase orcamentoEventoUseCase(OrcamentoEventoService orcamentoEventoService) {
        return new OrcamentoEventoUseCaseImpl(orcamentoEventoService);
    }

    @Bean
    public AcaoPosRelatorioUseCase acaoPosRelatorioUseCase(AcaoPosRelatorioService acaoPosRelatorioService) {
        return new AcaoPosRelatorioUseCaseImpl(acaoPosRelatorioService);
    }

    @Bean
    public LocalService localService(LocalRepository localRepository, ManutencaoRepository manutencaoRepository) {
        return new LocalServiceImpl(localRepository, manutencaoRepository);
    }

    @Bean
    public ManutencaoService manutencaoService(ManutencaoRepository manutencaoRepository,
            LocalRepository localRepository,
            ReservaLocalRepository reservaLocalRepository) {
        return new ManutencaoServiceImpl(manutencaoRepository, localRepository, reservaLocalRepository);
    }
}
