package domain.financeiro.service;

import domain.conciliacao.service.ConciliacaoService;
import domain.evento.entity.Evento;
import domain.evento.repository.EventoRepository;
import domain.financeiro.entity.RelatorioFinanceiro;
import domain.financeiro.entity.SimulacaoRelatorioFinanceiro;
import domain.financeiro.repository.CategoriaOrcamentoRepository;
import domain.financeiro.repository.DespesaRepository;
import domain.financeiro.repository.OrcamentoEventoRepository;
import domain.financeiro.repository.RelatorioFinanceiroRepository;
import domain.financeiro.repository.SimulacaoRelatorioRepository;
import domain.financeiro.template.GeradorRelatorioPadraoTemplateMethod;
import domain.financeiro.template.GeradorRelatorioFinanceiroTemplateMethod;
import domain.financeiro.valueobject.ResultadoGeracaoRelatorio;
import domain.financeiro.valueobject.TipoRelatorio;

import java.util.List;

public class RelatorioFinanceiroServiceImpl implements RelatorioFinanceiroService {

    private final RelatorioFinanceiroRepository relatorioRepository;
    private final SimulacaoRelatorioRepository simulacaoRepository;
    private final OrcamentoEventoRepository orcamentoEventoRepository;
    private final CategoriaOrcamentoRepository categoriaOrcamentoRepository;
    private final DespesaRepository despesaRepository;
    private final EventoRepository eventoRepository;
    private final ConciliacaoService conciliacaoService;

    public RelatorioFinanceiroServiceImpl(
            RelatorioFinanceiroRepository relatorioRepository,
            SimulacaoRelatorioRepository simulacaoRepository,
            OrcamentoEventoRepository orcamentoEventoRepository,
            CategoriaOrcamentoRepository categoriaOrcamentoRepository,
            DespesaRepository despesaRepository,
            EventoRepository eventoRepository,
            ConciliacaoService conciliacaoService) {
        this.relatorioRepository = relatorioRepository;
        this.simulacaoRepository = simulacaoRepository;
        this.orcamentoEventoRepository = orcamentoEventoRepository;
        this.categoriaOrcamentoRepository = categoriaOrcamentoRepository;
        this.despesaRepository = despesaRepository;
        this.eventoRepository = eventoRepository;
        this.conciliacaoService = conciliacaoService;
    }

    public RelatorioFinanceiroServiceImpl(
            RelatorioFinanceiroRepository relatorioRepository,
            OrcamentoEventoRepository orcamentoEventoRepository,
            CategoriaOrcamentoRepository categoriaOrcamentoRepository,
            DespesaRepository despesaRepository,
            EventoRepository eventoRepository) {
        this(relatorioRepository, new SimulacaoRelatorioRepositoryNulo(),
                orcamentoEventoRepository, categoriaOrcamentoRepository,
                despesaRepository, eventoRepository, new ConciliacaoServiceNulo());
    }

    @Override
    public RelatorioFinanceiro gerarRelatorio(String eventoId, String usuarioId) {
        return gerarRelatorio(eventoId, usuarioId, TipoRelatorio.PRELIMINAR);
    }

    @Override
    public RelatorioFinanceiro gerarRelatorio(String eventoId, String usuarioId, TipoRelatorio tipo) {
        if (tipo == TipoRelatorio.OFICIAL) {
            return gerarRelatorioOficial(eventoId, usuarioId, null);
        }
        ResultadoGeracaoRelatorio resultado = criarGerador().executar(eventoId, usuarioId);
        RelatorioFinanceiro relatorio = new RelatorioFinanceiro(resultado, TipoRelatorio.PRELIMINAR, null);
        return relatorioRepository.salvar(relatorio);
    }

    @Override
    public RelatorioFinanceiro gerarRelatorioOficial(String eventoId,
                                                      String usuarioId,
                                                      String motivoNovaVersaoOficial) {
        validarEmissaoOficial(eventoId, motivoNovaVersaoOficial);
        ResultadoGeracaoRelatorio resultado = criarGerador().executar(eventoId, usuarioId);
        RelatorioFinanceiro relatorio = new RelatorioFinanceiro(
                resultado, TipoRelatorio.OFICIAL, motivoNovaVersaoOficial);
        return relatorioRepository.salvar(relatorio);
    }

    private void validarEmissaoOficial(String eventoId, String motivoNovaVersaoOficial) {
        Evento evento = eventoRepository.buscarPorId(eventoId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Evento inválido ou não encontrado."));
        if (evento.isConcluido()) {
            throw new IllegalStateException(
                    "Não é permitido emitir relatório oficial para evento concluído.");
        }

        boolean jaExisteOficial = relatorioRepository.listarPorEventoId(eventoId).stream()
                .anyMatch(r -> r.getTipo() == TipoRelatorio.OFICIAL);
        if (jaExisteOficial && (motivoNovaVersaoOficial == null || motivoNovaVersaoOficial.isBlank())) {
            throw new IllegalArgumentException(
                    "Nova versão oficial exige motivo documentado.");
        }
    }

    @Override
    public SimulacaoRelatorioFinanceiro simularRelatorio(String eventoId, String usuarioId) {
        ResultadoGeracaoRelatorio resultado = criarGerador().executar(eventoId, usuarioId);
        SimulacaoRelatorioFinanceiro simulacao = new SimulacaoRelatorioFinanceiro(resultado);
        return simulacaoRepository.salvar(simulacao);
    }

    @Override
    public RelatorioFinanceiro confirmarGeracao(String simulacaoId,
                                                 TipoRelatorio tipo,
                                                 String motivoNovaVersaoOficial) {
        SimulacaoRelatorioFinanceiro simulacao = simulacaoRepository.buscarPorId(simulacaoId)
                .orElseThrow(() -> new IllegalArgumentException("Simulação não encontrada ou expirada."));

        ResultadoGeracaoRelatorio resultado = simulacao.getResultado();

        if (tipo == TipoRelatorio.OFICIAL) {
            validarEmissaoOficial(resultado.getEventoId(), motivoNovaVersaoOficial);
        }

        RelatorioFinanceiro relatorio = new RelatorioFinanceiro(resultado, tipo, motivoNovaVersaoOficial);
        simulacaoRepository.remover(simulacaoId);
        return relatorioRepository.salvar(relatorio);
    }

    @Override
    public RelatorioFinanceiro buscarRelatorio(String id) {
        return relatorioRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Relatório financeiro não encontrado."));
    }

    @Override
    public List<RelatorioFinanceiro> listarRelatoriosPorEvento(String eventoId) {
        return relatorioRepository.listarPorEventoId(eventoId);
    }

    private GeradorRelatorioFinanceiroTemplateMethod criarGerador() {
        return new GeradorRelatorioPadraoTemplateMethod(
                relatorioRepository,
                orcamentoEventoRepository,
                categoriaOrcamentoRepository,
                despesaRepository,
                eventoRepository,
                conciliacaoService);
    }

    /**
     * Permite testes legados sem simulação nem conciliação.
     */
    private static final class SimulacaoRelatorioRepositoryNulo implements SimulacaoRelatorioRepository {
        @Override
        public SimulacaoRelatorioFinanceiro salvar(SimulacaoRelatorioFinanceiro simulacao) {
            return simulacao;
        }

        @Override
        public java.util.Optional<SimulacaoRelatorioFinanceiro> buscarPorId(String id) {
            return java.util.Optional.empty();
        }

        @Override
        public void remover(String id) {
        }
    }

    private static final class ConciliacaoServiceNulo implements ConciliacaoService {
        @Override
        public void executarConciliacaoAutomatica(String eventoId, String responsavelId) {
        }

        @Override
        public List<domain.financeiro.entity.Despesa> listarDespesasDescobertasPorEvento(String eventoId) {
            return List.of();
        }

        @Override
        public List<domain.contrato.entity.Contrato> listarContratosExtrapoladosPorEvento(String eventoId) {
            return List.of();
        }

        @Override
        public domain.conciliacao.entity.VinculoConciliacao vincularManualmente(
                String despesaId, String contratoId, String responsavelId) {
            return null;
        }

        @Override
        public domain.conciliacao.entity.RelatorioConciliacao gerarRelatorio(
                String eventoId, String responsavelId) {
            return null;
        }
    }
}
