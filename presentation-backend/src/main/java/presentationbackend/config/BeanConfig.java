package presentationbackend.config;

import application.agenda.usecase.CompromissoUseCase;
import application.agenda.usecase.CompromissoUseCaseImpl;
import application.agenda.usecase.LembreteUseCase;
import application.agenda.usecase.LembreteUseCaseImpl;
import application.conciliacao.usecase.ConciliacaoUseCase;
import application.conciliacao.usecase.ConciliacaoUseCaseImpl;
import application.financeiro.usecase.AcaoPosRelatorioUseCase;
import application.financeiro.usecase.AcaoPosRelatorioUseCaseImpl;
import application.financeiro.usecase.DespesaUseCase;
import application.financeiro.usecase.DespesaUseCaseImpl;
import application.financeiro.usecase.OrcamentoEventoUseCase;
import application.financeiro.usecase.OrcamentoEventoUseCaseImpl;
import application.financeiro.usecase.RelatorioFinanceiroUseCase;
import application.financeiro.usecase.RelatorioFinanceiroUseCaseImpl;
import application.contrato.usecase.ContratoUseCase;
import application.contrato.usecase.ContratoUseCaseImpl;
import application.dependencia.usecase.DependenciaUseCase;
import application.dependencia.usecase.DependenciaUseCaseImpl;
import application.estoque.usecase.ConsumoEventoUseCase;
import application.estoque.usecase.ConsumoEventoUseCaseImpl;
import application.estoque.usecase.ItemEstoqueUseCase;
import application.estoque.usecase.ItemEstoqueUseCaseImpl;
import application.estoque.usecase.PrevisaoConsumoUseCase;
import application.estoque.usecase.PrevisaoConsumoUseCaseImpl;
import application.estoque.usecase.RedistribuicaoEstoqueUseCase;
import application.estoque.usecase.RedistribuicaoEstoqueUseCaseImpl;
import application.estoque.usecase.ReservaEstoqueUseCase;
import application.estoque.usecase.ReservaEstoqueUseCaseImpl;
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

import application.equipe.usecase.EquipeUseCase;
import application.equipe.usecase.EquipeUseCaseImpl;
import application.funcionario.usecase.FuncionarioUseCase;
import application.funcionario.usecase.FuncionarioUseCaseImpl;
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
import domain.conciliacao.service.ConciliacaoService;
import domain.contrato.repository.ContratoRepository;
import domain.contrato.service.ContratoService;
import domain.contrato.service.ContratoServiceImpl;
import domain.equipe.repository.EquipeRepository;
import domain.equipe.service.EquipeService;
import domain.equipe.service.EquipeServiceImpl;
import domain.funcionario.service.FuncionarioService;
import domain.funcionario.service.FuncionarioServiceImpl;
import domain.estoque.repository.CenarioRedistribuicaoRepository;
import domain.estoque.repository.ConsumoEventoRepository;
import domain.estoque.repository.ItemEstoqueRepository;
import domain.estoque.repository.PrevisaoConsumoRepository;
import domain.estoque.repository.ReservaEstoqueRepository;
import domain.estoque.service.ConsumoEventoService;
import domain.estoque.service.ConsumoEventoServiceImpl;
import domain.estoque.service.ItemEstoqueService;
import domain.estoque.service.ItemEstoqueServiceImpl;
import domain.estoque.service.PrevisaoConsumoService;
import domain.estoque.service.PrevisaoConsumoServiceImpl;
import domain.estoque.service.RedistribuicaoEstoqueService;
import domain.estoque.service.RedistribuicaoEstoqueServiceImpl;
import domain.estoque.service.ReservaEstoqueService;
import domain.estoque.service.ReservaEstoqueServiceImpl;
import domain.estoque.strategy.PrioridadePadraoEventoStrategy;
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
import domain.local.repository.AvaliacaoContextualLocalRepository;
import domain.local.repository.IndisponibilidadeLocalRepository;
import domain.local.repository.LocalRepository;
import domain.local.repository.ManutencaoRepository;
import domain.local.repository.ReservaLocalRepository;
import domain.local.service.AvaliacaoContextualLocalService;
import domain.local.service.AvaliacaoContextualLocalServiceImpl;
import domain.local.service.LocalService;
import domain.local.service.LocalServiceImpl;
import domain.local.service.ManutencaoService;
import domain.local.service.ManutencaoServiceImpl;
import domain.local.turno.repository.TurnoOperacionalRepository;
import domain.local.turno.service.TurnoOperacionalService;
import domain.local.turno.service.TurnoOperacionalServiceImpl;
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
    public FuncionarioService funcionarioService(FuncionarioRepository funcionarioRepository,
            EquipeRepository equipeRepository) {
        return new FuncionarioServiceImpl(funcionarioRepository, equipeRepository);
    }

    @Bean
    public FuncionarioUseCase funcionarioUseCase(FuncionarioService funcionarioService) {
        return new FuncionarioUseCaseImpl(funcionarioService);
    }

    @Bean
    public EquipeService equipeService(EquipeRepository equipeRepository,
            EventoRepository eventoRepository,
            FuncionarioRepository funcionarioRepository,
            TarefaRepository tarefaRepository) {
        return new EquipeServiceImpl(equipeRepository, eventoRepository, funcionarioRepository, tarefaRepository);
    }

    @Bean
    public EquipeUseCase equipeUseCase(EquipeService equipeService) {
        return new EquipeUseCaseImpl(equipeService);
    }

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
    public ConciliacaoUseCase conciliacaoUseCase(ConciliacaoService conciliacaoService) {
        return new ConciliacaoUseCaseImpl(conciliacaoService);
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
    public EventoUseCase eventoUseCase(EventoService eventoService, LocalRepository localRepository,
            PrevisaoConsumoService previsaoConsumoService) {
        return new EventoUseCaseImpl(eventoService, localRepository, previsaoConsumoService);
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

    @Bean
    public AvaliacaoContextualLocalService avaliacaoContextualLocalService(
            EventoRepository eventoRepository,
            LocalRepository localRepository,
            AvaliacaoContextualLocalRepository avaliacaoContextualLocalRepository) {
        return new AvaliacaoContextualLocalServiceImpl(eventoRepository, localRepository, avaliacaoContextualLocalRepository);
    }

    @Bean
    public TurnoOperacionalService turnoOperacionalService(
            TurnoOperacionalRepository turnoOperacionalRepository,
            LocalRepository localRepository) {
        return new TurnoOperacionalServiceImpl(turnoOperacionalRepository, localRepository);
    }

    @Bean
    public ItemEstoqueService itemEstoqueService(ItemEstoqueRepository itemEstoqueRepository) {
        return new ItemEstoqueServiceImpl(itemEstoqueRepository);
    }

    @Bean
    public ReservaEstoqueService reservaEstoqueService(ReservaEstoqueRepository reservaEstoqueRepository,
            EventoRepository eventoRepository,
            ItemEstoqueRepository itemEstoqueRepository) {
        return new ReservaEstoqueServiceImpl(reservaEstoqueRepository, eventoRepository, itemEstoqueRepository);
    }

    @Bean
    public ConsumoEventoService consumoEventoService(ConsumoEventoRepository consumoEventoRepository,
            EventoRepository eventoRepository) {
        return new ConsumoEventoServiceImpl(consumoEventoRepository, eventoRepository);
    }

    @Bean
    public PrevisaoConsumoService previsaoConsumoService(EventoRepository eventoRepository,
            ConsumoEventoRepository consumoEventoRepository,
            PrevisaoConsumoRepository previsaoConsumoRepository) {
        return new PrevisaoConsumoServiceImpl(eventoRepository, consumoEventoRepository, previsaoConsumoRepository);
    }

    @Bean
    public RedistribuicaoEstoqueService redistribuicaoEstoqueService(
            ReservaEstoqueRepository reservaEstoqueRepository,
            ItemEstoqueRepository itemEstoqueRepository,
            EventoRepository eventoRepository,
            CenarioRedistribuicaoRepository cenarioRedistribuicaoRepository,
            PrevisaoConsumoRepository previsaoConsumoRepository) {
        return new RedistribuicaoEstoqueServiceImpl(
                reservaEstoqueRepository,
                itemEstoqueRepository,
                eventoRepository,
                cenarioRedistribuicaoRepository,
                previsaoConsumoRepository,
                new PrioridadePadraoEventoStrategy());
    }

    @Bean
    public ItemEstoqueUseCase itemEstoqueUseCase(ItemEstoqueService itemEstoqueService) {
        return new ItemEstoqueUseCaseImpl(itemEstoqueService);
    }

    @Bean
    public ReservaEstoqueUseCase reservaEstoqueUseCase(ReservaEstoqueService reservaEstoqueService) {
        return new ReservaEstoqueUseCaseImpl(reservaEstoqueService);
    }

    @Bean
    public ConsumoEventoUseCase consumoEventoUseCase(ConsumoEventoService consumoEventoService) {
        return new ConsumoEventoUseCaseImpl(consumoEventoService);
    }

    @Bean
    public PrevisaoConsumoUseCase previsaoConsumoUseCase(PrevisaoConsumoService previsaoConsumoService) {
        return new PrevisaoConsumoUseCaseImpl(previsaoConsumoService);
    }

    @Bean
    public RedistribuicaoEstoqueUseCase redistribuicaoEstoqueUseCase(
            RedistribuicaoEstoqueService redistribuicaoEstoqueService) {
        return new RedistribuicaoEstoqueUseCaseImpl(redistribuicaoEstoqueService);
    }
}
