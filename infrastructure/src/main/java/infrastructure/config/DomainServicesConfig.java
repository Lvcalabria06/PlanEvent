package infrastructure.config;

import domain.conciliacao.repository.RelatorioConciliacaoRepository;
import domain.conciliacao.repository.VinculoConciliacaoRepository;
import domain.conciliacao.service.ConciliacaoService;
import domain.conciliacao.service.ConciliacaoServiceImpl;
import domain.contrato.repository.ContratoRepository;
import domain.evento.repository.EventoRepository;
import domain.financeiro.repository.CategoriaOrcamentoRepository;
import domain.financeiro.repository.DespesaRepository;
import domain.financeiro.repository.OrcamentoEventoRepository;
import domain.financeiro.repository.RelatorioFinanceiroRepository;
import domain.financeiro.repository.SimulacaoRelatorioRepository;
import domain.financeiro.service.DespesaService;
import domain.financeiro.service.DespesaServiceImpl;
import domain.financeiro.service.RelatorioFinanceiroService;
import domain.financeiro.service.RelatorioFinanceiroServiceImpl;
import domain.fornecedor.repository.FornecedorRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainServicesConfig {

    @Bean
    public ConciliacaoService conciliacaoService(ContratoRepository contratoRepository,
                                                  DespesaRepository despesaRepository,
                                                  VinculoConciliacaoRepository vinculoRepository,
                                                  RelatorioConciliacaoRepository relatorioRepository) {
        return new ConciliacaoServiceImpl(contratoRepository, despesaRepository,
                vinculoRepository, relatorioRepository);
    }

    @Bean
    public DespesaService despesaService(DespesaRepository despesaRepository,
                                         OrcamentoEventoRepository orcamentoEventoRepository,
                                         CategoriaOrcamentoRepository categoriaOrcamentoRepository,
                                         EventoRepository eventoRepository,
                                         FornecedorRepository fornecedorRepository) {
        return new DespesaServiceImpl(
                despesaRepository,
                orcamentoEventoRepository,
                categoriaOrcamentoRepository,
                eventoRepository,
                fornecedorRepository);
    }

    @Bean
    public RelatorioFinanceiroService relatorioFinanceiroService(
            RelatorioFinanceiroRepository relatorioRepository,
            SimulacaoRelatorioRepository simulacaoRepository,
            OrcamentoEventoRepository orcamentoEventoRepository,
            CategoriaOrcamentoRepository categoriaOrcamentoRepository,
            DespesaRepository despesaRepository,
            EventoRepository eventoRepository,
            ConciliacaoService conciliacaoService) {
        return new RelatorioFinanceiroServiceImpl(
                relatorioRepository,
                simulacaoRepository,
                orcamentoEventoRepository,
                categoriaOrcamentoRepository,
                despesaRepository,
                eventoRepository,
                conciliacaoService);
    }
}
