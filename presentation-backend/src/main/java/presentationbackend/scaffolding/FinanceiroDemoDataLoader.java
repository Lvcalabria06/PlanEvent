package presentationbackend.scaffolding;

import domain.evento.entity.Evento;
import domain.evento.repository.EventoRepository;
import domain.financeiro.entity.CategoriaOrcamento;
import domain.financeiro.entity.OrcamentoEvento;
import domain.financeiro.repository.CategoriaOrcamentoRepository;
import domain.financeiro.repository.OrcamentoEventoRepository;
import domain.financeiro.valueobject.CategoriaDespesa;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Semeia fornecedores e orçamento para o evento de demonstração já criado pelo
 * {@link DadosDemoSeeder}. Executa após o seed
 * de tarefas ({@code @Order(100)}).
 */
@Component
@Order(100)
public class FinanceiroDemoDataLoader implements CommandLineRunner {

    private final EventoRepository eventoRepository;
    private final OrcamentoEventoRepository orcamentoEventoRepository;
    private final CategoriaOrcamentoRepository categoriaOrcamentoRepository;

    public FinanceiroDemoDataLoader(EventoRepository eventoRepository,
                                   OrcamentoEventoRepository orcamentoEventoRepository,
                                   CategoriaOrcamentoRepository categoriaOrcamentoRepository) {
        this.eventoRepository = eventoRepository;
        this.orcamentoEventoRepository = orcamentoEventoRepository;
        this.categoriaOrcamentoRepository = categoriaOrcamentoRepository;
    }

    @Override
    public void run(String... args) {
        List<Evento> eventos = eventoRepository.listarTodos();
        if (eventos.isEmpty()) {
            return;
        }

        Evento evento = eventos.get(0);
        if (orcamentoEventoRepository.buscarPorEventoId(evento.getId()).isPresent()) {
            return;
        }

        OrcamentoEvento orcamento = new OrcamentoEvento(evento.getId(), new BigDecimal("125000.00"));
        orcamentoEventoRepository.salvar(orcamento);

        salvarCategoria(orcamento.getId(), CategoriaDespesa.ALIMENTACAO, "45000.00");
        salvarCategoria(orcamento.getId(), CategoriaDespesa.DECORACAO, "20000.00");
        salvarCategoria(orcamento.getId(), CategoriaDespesa.EQUIPAMENTO, "25000.00");
        salvarCategoria(orcamento.getId(), CategoriaDespesa.MARKETING, "15000.00");
        salvarCategoria(orcamento.getId(), CategoriaDespesa.SERVICO, "10000.00");
        salvarCategoria(orcamento.getId(), CategoriaDespesa.LOGISTICA, "10000.00");
    }

    private void salvarCategoria(String orcamentoId, CategoriaDespesa cat, String valor) {
        categoriaOrcamentoRepository.salvar(
                new CategoriaOrcamento(orcamentoId, cat, new BigDecimal(valor)));
    }
}
