package domain.estoque.steps;

import domain.estoque.entity.AlocacaoRedistribuicao;
import domain.estoque.entity.CenarioRedistribuicao;
import domain.estoque.entity.ItemEstoque;
import domain.estoque.entity.ItemReserva;
import domain.estoque.entity.ItemSubstituicao;
import domain.estoque.entity.ReservaEstoque;
import domain.estoque.repository.CenarioRedistribuicaoRepository;
import domain.estoque.repository.ItemEstoqueRepository;
import domain.estoque.repository.ReservaEstoqueRepository;
import domain.estoque.service.RedistribuicaoEstoqueService;
import domain.estoque.service.RedistribuicaoEstoqueServiceImpl;
import domain.estoque.valueobject.StatusRedistribuicao;
import domain.evento.entity.Evento;
import domain.evento.repository.EventoRepository;
import domain.evento.valueobject.PorteEvento;
import domain.evento.valueobject.TipoEvento;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class RedistribuicaoEstoqueSteps {

    private static final String EVENTO_PRIORITARIO_ID = "evento-prioritario";
    private static final String EVENTO_SECUNDARIO_ID = "evento-secundario";
    private static final String USUARIO_ID = "gestor-001";

    private ReservaEstoqueRepository reservaEstoqueRepository;
    private ItemEstoqueRepository itemEstoqueRepository;
    private EventoRepository eventoRepository;
    private CenarioRedistribuicaoRepository cenarioRedistribuicaoRepository;
    private RedistribuicaoEstoqueService redistribuicaoService;

    private List<ReservaEstoque> reservasExistentes;
    private List<ItemEstoque> itensEstoque;
    private List<ItemSubstituicao> substituicoes;
    private List<CenarioRedistribuicao> cenariosSalvos;

    private CenarioRedistribuicao cenarioGerado;
    private CenarioRedistribuicao cenarioPendente;
    private Exception excecaoLancada;

    private LocalDateTime periodoInicio;
    private LocalDateTime periodoFim;

    private Evento eventoPrioritario;
    private Evento eventoSecundario;

    @Before
    public void setup() {
        reservaEstoqueRepository = Mockito.mock(ReservaEstoqueRepository.class);
        itemEstoqueRepository = Mockito.mock(ItemEstoqueRepository.class);
        eventoRepository = Mockito.mock(EventoRepository.class);
        cenarioRedistribuicaoRepository = Mockito.mock(CenarioRedistribuicaoRepository.class);

        redistribuicaoService = new RedistribuicaoEstoqueServiceImpl(
                reservaEstoqueRepository,
                itemEstoqueRepository,
                eventoRepository,
                cenarioRedistribuicaoRepository
        );

        reservasExistentes = new ArrayList<>();
        itensEstoque = new ArrayList<>();
        substituicoes = new ArrayList<>();
        cenariosSalvos = new ArrayList<>();

        cenarioGerado = null;
        cenarioPendente = null;
        excecaoLancada = null;

        periodoInicio = LocalDateTime.of(2026, 6, 1, 8, 0);
        periodoFim = LocalDateTime.of(2026, 6, 1, 22, 0);

        when(reservaEstoqueRepository.listarTodas())
                .thenAnswer(invocation -> List.copyOf(reservasExistentes));
        when(reservaEstoqueRepository.salvar(any(ReservaEstoque.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(cenarioRedistribuicaoRepository.salvar(any(CenarioRedistribuicao.class)))
                .thenAnswer(invocation -> {
                    CenarioRedistribuicao c = invocation.getArgument(0);
                    cenariosSalvos.add(c);
                    return c;
                });
        when(cenarioRedistribuicaoRepository.listarPendentes())
                .thenAnswer(invocation -> cenariosSalvos.stream()
                        .filter(CenarioRedistribuicao::isPendente)
                        .toList());
        when(itemEstoqueRepository.listarTodos())
                .thenAnswer(invocation -> List.copyOf(itensEstoque));
        when(itemEstoqueRepository.buscarSubstituicoesPorItem(any()))
                .thenAnswer(invocation -> {
                    String itemId = invocation.getArgument(0);
                    return substituicoes.stream()
                            .filter(s -> s.getItemOriginalId().equals(itemId))
                            .toList();
                });
    }

    @Given("existem dois eventos ativos no mesmo periodo com demanda concorrente")
    public void existemDoisEventosAtivosNoMesmoPeriodoComDemandaConcorrente() {
        eventoPrioritario = new Evento("Congresso Nacional", TipoEvento.CORPORATIVO, PorteEvento.GRANDE, 500, "Congresso corporativo", "local-1");
        eventoPrioritario.definirJanelaPlanejamento(periodoInicio, periodoFim);
        eventoPrioritario.confirmarPlanejamento();

        eventoSecundario = new Evento("Workshop Interno", TipoEvento.ACADEMICO, PorteEvento.PEQUENO, 50, "Treinamento interno", "local-2");
        eventoSecundario.definirJanelaPlanejamento(periodoInicio.plusDays(15), periodoFim.plusDays(15));

        when(eventoRepository.buscarPorId(eq(EVENTO_PRIORITARIO_ID)))
                .thenReturn(Optional.of(eventoPrioritario));
        when(eventoRepository.buscarPorId(eq(EVENTO_SECUNDARIO_ID)))
                .thenReturn(Optional.of(eventoSecundario));
    }

    @Given("o item {string} possui estoque total de {int} unidades para redistribuicao")
    public void oItemPossuiEstoqueTotalDeUnidadesParaRedistribuicao(String itemId, int quantidade) {
        ItemEstoque item = new ItemEstoque(itemId, quantidade);
        itensEstoque.add(item);
        when(itemEstoqueRepository.buscarPorId(eq(itemId)))
                .thenReturn(Optional.of(item));
    }

    @Given("o evento {string} possui reserva de {int} unidades de {string}")
    public void oEventoPossuiReservaDeUnidadesDe(String eventoId, int quantidade, String itemId) {
        ReservaEstoque reserva = new ReservaEstoque(
                eventoId, periodoInicio, periodoFim,
                List.of(new ItemReserva("solicitacao-" + eventoId, itemId, quantidade))
        );
        reserva.confirmar();
        reservasExistentes.add(reserva);
    }

    @Given("o evento {string} possui reserva em uso de {int} unidades de {string}")
    public void oEventoPossuiReservaEmUsoDeUnidadesDe(String eventoId, int quantidade, String itemId) {
        ReservaEstoque reserva = new ReservaEstoque(
                eventoId, periodoInicio, periodoFim,
                List.of(new ItemReserva("solicitacao-" + eventoId, itemId, quantidade))
        );
        reserva.confirmar();
        reserva.iniciarUso();
        reservasExistentes.add(reserva);
    }

    @Given("o evento {string} possui maior prioridade por proximidade e porte")
    public void oEventoPossuiMaiorPrioridadePorProximidadeEPorte(String eventoId) {
    }

    @Given("existe um cenario de redistribuicao pendente gerado anteriormente")
    public void existeUmCenarioDeRedistribuicaoPendenteGeradoAnteriormente() {
        try {
            cenarioPendente = redistribuicaoService.gerarCenarioRedistribuicao(USUARIO_ID, periodoInicio, periodoFim);
            when(cenarioRedistribuicaoRepository.buscarPorId(any()))
                    .thenReturn(Optional.of(cenarioPendente));
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @Given("existe substituicao de {string} por {string} com fator {double}")
    public void existeSubstituicaoDePorComFator(String itemOriginal, String itemSubstituto, double fator) {
        substituicoes.add(new ItemSubstituicao(itemOriginal, itemSubstituto, fator));
    }

    @When("o gestor solicitar a geracao do cenario de redistribuicao")
    public void oGestorSolicitarAGeracaoDoCenarioDeRedistribuicao() {
        try {
            cenarioGerado = redistribuicaoService.gerarCenarioRedistribuicao(USUARIO_ID, periodoInicio, periodoFim);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("o gestor aplicar a redistribuicao com confirmacao manual")
    public void oGestorAplicarARedistribuicaoComConfirmacaoManual() {
        try {
            cenarioGerado = redistribuicaoService.aplicarRedistribuicao(cenarioPendente.getId(), USUARIO_ID);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("o gestor solicitar novo calculo de redistribuicao")
    public void oGestorSolicitarNovoCalculoDeRedistribuicao() {
        try {
            cenarioGerado = redistribuicaoService.gerarCenarioRedistribuicao(USUARIO_ID, periodoInicio, periodoFim);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @Then("o sistema deve identificar escassez global do item {string}")
    public void oSistemaDeveIdentificarEscassezGlobalDoItem(String itemId) {
        assertNull(excecaoLancada, msgErro());
        assertNotNull(cenarioGerado);

        int demandaTotal = cenarioGerado.getAlocacoesAtuais().stream()
                .filter(a -> a.getItemEstoqueId().equals(itemId))
                .mapToInt(AlocacaoRedistribuicao::getQuantidadeAnterior)
                .sum();

        ItemEstoque item = itensEstoque.stream()
                .filter(i -> i.getNome().equals(itemId))
                .findFirst()
                .orElse(null);
        assertNotNull(item);
        assertTrue(demandaTotal > item.getQuantidadeTotal(),
                "A demanda total (" + demandaTotal + ") deve exceder o estoque (" + item.getQuantidadeTotal() + ")");
    }

    @Then("o cenario gerado deve possuir status pendente")
    public void oCenarioGeradoDevePossuirStatusPendente() {
        assertNull(excecaoLancada, msgErro());
        assertNotNull(cenarioGerado);
        assertEquals(StatusRedistribuicao.PENDENTE, cenarioGerado.getStatus());
    }

    @Then("o evento {string} deve receber alocacao igual ou superior ao evento {string}")
    public void oEventoDeveReceberAlocacaoIgualOuSuperiorAoEvento(String eventoPrioritarioId, String eventoSecundarioId) {
        assertNull(excecaoLancada, msgErro());
        assertNotNull(cenarioGerado);

        int alocadoPrioritario = cenarioGerado.getAlocacoesOtimizadas().stream()
                .filter(a -> a.getEventoId().equals(eventoPrioritarioId))
                .mapToInt(AlocacaoRedistribuicao::getQuantidadeRedistribuida)
                .sum();

        int alocadoSecundario = cenarioGerado.getAlocacoesOtimizadas().stream()
                .filter(a -> a.getEventoId().equals(eventoSecundarioId))
                .mapToInt(AlocacaoRedistribuicao::getQuantidadeRedistribuida)
                .sum();

        assertTrue(alocadoPrioritario >= alocadoSecundario,
                "Evento prioritario (" + alocadoPrioritario + ") deve ter alocacao >= secundario (" + alocadoSecundario + ")");
    }

    @Then("o cenario deve apresentar alocacoes atuais e otimizadas")
    public void oCenarioDeveApresentarAlocacoesAtuaisEOtimizadas() {
        assertNull(excecaoLancada, msgErro());
        assertNotNull(cenarioGerado);
        assertFalse(cenarioGerado.getAlocacoesAtuais().isEmpty());
        assertFalse(cenarioGerado.getAlocacoesOtimizadas().isEmpty());
    }

    @Then("o cenario deve apresentar impacto para o evento {string}")
    public void oCenarioDeveApresentarImpactoParaOEvento(String eventoId) {
        assertNull(excecaoLancada, msgErro());
        assertNotNull(cenarioGerado);
        boolean temImpacto = cenarioGerado.getImpactosPorEvento().stream()
                .anyMatch(i -> i.getEventoId().equals(eventoId));
        assertTrue(temImpacto, "Deve existir impacto para o evento " + eventoId);
    }

    @Then("pelo menos um evento deve apresentar deficit apos redistribuicao")
    public void peloMenosUmEventoDeveApresentarDeficitAposRedistribuicao() {
        assertNull(excecaoLancada, msgErro());
        assertNotNull(cenarioGerado);
        boolean temDeficit = cenarioGerado.getImpactosPorEvento().stream()
                .anyMatch(CenarioRedistribuicao.ImpactoEvento::possuiDeficit);
        assertTrue(temDeficit, "Pelo menos um evento deve apresentar deficit");
    }

    @Then("o cenario deve ser marcado como aplicado")
    public void oCenarioDeveSerMarcadoComoAplicado() {
        assertNull(excecaoLancada, msgErro());
        assertNotNull(cenarioGerado);
        assertEquals(StatusRedistribuicao.APLICADA, cenarioGerado.getStatus());
    }

    @Then("as reservas dos eventos devem ser atualizadas conforme alocacoes otimizadas")
    public void asReservasDosEventosDevemSerAtualizadasConformeAlocacoesOtimizadas() {
        assertNull(excecaoLancada, msgErro());
        assertNotNull(cenarioGerado);
        assertTrue(cenarioGerado.isAplicada());
    }

    @Then("a reserva em uso do evento {string} nao deve ser alterada na redistribuicao")
    public void aReservaEmUsoDoEventoNaoDeveSerAlteradaNaRedistribuicao(String eventoId) {
        assertNull(excecaoLancada, msgErro());
        assertNotNull(cenarioGerado);

        List<AlocacaoRedistribuicao> alocacoesEvento = cenarioGerado.getAlocacoesOtimizadas().stream()
                .filter(a -> a.getEventoId().equals(eventoId))
                .toList();

        for (AlocacaoRedistribuicao alocacao : alocacoesEvento) {
            assertEquals(alocacao.getQuantidadeAnterior(), alocacao.getQuantidadeRedistribuida(),
                    "Itens em uso nao devem ser alterados na redistribuicao");
        }
    }

    @Then("o cenario anterior deve ser invalidado")
    public void oCenarioAnteriorDeveSerInvalidado() {
        assertNull(excecaoLancada, msgErro());
        assertTrue(cenarioPendente.isInvalidada(), "Cenario anterior deve estar invalidado");
    }

    @Then("um novo cenario pendente deve ser gerado")
    public void umNovoCenarioPendenteDeveSerGerado() {
        assertNull(excecaoLancada, msgErro());
        assertNotNull(cenarioGerado);
        assertTrue(cenarioGerado.isPendente());
        assertFalse(cenarioGerado.getId().equals(cenarioPendente.getId()));
    }

    @Then("o cenario deve possuir historico com usuario data e descricao da redistribuicao")
    public void oCenarioDevePossuirHistoricoComUsuarioDataEDescricaoDaRedistribuicao() {
        assertNull(excecaoLancada, msgErro());
        assertNotNull(cenarioGerado);
        assertFalse(cenarioGerado.getHistorico().isEmpty(), "Historico de redistribuicao nao pode estar vazio");
        assertNotNull(cenarioGerado.getHistorico().get(0).getUsuarioResponsavelId());
        assertNotNull(cenarioGerado.getHistorico().get(0).getDataHora());
        assertNotNull(cenarioGerado.getHistorico().get(0).getDescricao());
    }

    @Then("o cenario deve considerar substituicao de {string} por {string} para eventos com deficit")
    public void oCenarioDeveConsiderarSubstituicaoDePorParaEventosComDeficit(String itemOriginal, String itemSubstituto) {
        assertNull(excecaoLancada, msgErro());
        assertNotNull(cenarioGerado);

        boolean temSubstituicao = cenarioGerado.getAlocacoesOtimizadas().stream()
                .anyMatch(a -> a.possuiSubstituicao()
                        && a.getItemSubstitutoId().equals(itemSubstituto));

        assertTrue(temSubstituicao,
                "Deve existir pelo menos uma alocacao com substituicao de " + itemOriginal + " por " + itemSubstituto);
    }

    private String msgErro() {
        return excecaoLancada != null ? excecaoLancada.getMessage() : "";
    }
}
