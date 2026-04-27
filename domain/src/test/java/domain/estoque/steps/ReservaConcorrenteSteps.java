package domain.estoque.steps;

import domain.estoque.entity.ItemEstoque;
import domain.estoque.entity.ItemReserva;
import domain.estoque.entity.ReservaEstoque;
import domain.estoque.repository.ItemEstoqueRepository;
import domain.estoque.repository.ReservaEstoqueRepository;
import domain.estoque.service.ReservaEstoqueService;
import domain.estoque.service.ReservaEstoqueServiceImpl;
import domain.estoque.valueobject.ResultadoDisponibilidadeReserva;
import domain.estoque.valueobject.StatusReservaEstoque;
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

public class ReservaConcorrenteSteps {

    private static final String EVENTO_ATUAL_ID = "evento-reserva-atual";
    private static final String EVENTO_CONFLITO_ID = "evento-reserva-conflito";
    private static final String ITEM_CADEIRA_ID = "item-cadeira";
    private static final String ITEM_PROJETOR_ID = "item-projetor";
    private static final String RESERVA_ATUAL_ID = "reserva-atual";

    private ReservaEstoqueRepository reservaEstoqueRepository;
    private ItemEstoqueRepository itemEstoqueRepository;
    private EventoRepository eventoRepository;
    private ReservaEstoqueService reservaEstoqueService;

    private Evento eventoAtual;
    private ResultadoDisponibilidadeReserva resultadoDisponibilidade;
    private ReservaEstoque reservaEmContexto;
    private Exception excecaoLancada;
    private LocalDateTime dataInicioSolicitada;
    private LocalDateTime dataFimSolicitada;
    private List<ItemReserva> itensSolicitados;
    private List<ReservaEstoque> reservasExistentes;
    private ResultadoDisponibilidadeReserva resultadoDisponibilidadeAnterior;

    @Before
    public void setup() {
        reservaEstoqueRepository = Mockito.mock(ReservaEstoqueRepository.class);
        itemEstoqueRepository = Mockito.mock(ItemEstoqueRepository.class);
        eventoRepository = Mockito.mock(EventoRepository.class);

        reservaEstoqueService = new ReservaEstoqueServiceImpl(
                reservaEstoqueRepository,
                itemEstoqueRepository,
                eventoRepository
        );

        eventoAtual = null;
        resultadoDisponibilidade = null;
        reservaEmContexto = null;
        excecaoLancada = null;
        dataInicioSolicitada = LocalDateTime.of(2026, 5, 10, 8, 0);
        dataFimSolicitada = LocalDateTime.of(2026, 5, 10, 18, 0);
        itensSolicitados = List.of();
        reservasExistentes = new ArrayList<>();
        resultadoDisponibilidadeAnterior = null;

        when(reservaEstoqueRepository.salvar(any(ReservaEstoque.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(reservaEstoqueRepository.listarTodas())
                .thenAnswer(invocation -> List.copyOf(reservasExistentes));
    }

    @Given("existe um evento valido para reserva de estoque")
    public void existeUmEventoValidoParaReservaDeEstoque() {
        eventoAtual = new Evento("Feira de Negocios", TipoEvento.CORPORATIVO, PorteEvento.MEDIO, 200, "Exposicao", "local-1");
        when(eventoRepository.buscarPorId(eq(EVENTO_ATUAL_ID)))
                .thenReturn(Optional.of(eventoAtual));
    }

    @Given("o item {string} possui estoque total de {int} unidades")
    public void oItemPossuiEstoqueTotalDeUnidades(String itemId, int quantidadeTotal) {
        when(itemEstoqueRepository.buscarPorId(eq(itemId)))
                .thenReturn(Optional.of(new ItemEstoque(itemId, quantidadeTotal)));
    }

    @Given("existe uma reserva {string} do item {string} com quantidade {int} para o mesmo periodo")
    public void existeUmaReservaDoItemComQuantidadeParaOMesmoPeriodo(String status, String itemId, int quantidade) {
        ReservaEstoque reservaExistente = new ReservaEstoque(
                EVENTO_CONFLITO_ID,
                dataInicioSolicitada.minusHours(1),
                dataFimSolicitada.minusHours(-1),
                List.of(new ItemReserva("solicitacao-existente", itemId, quantidade))
        );

        if ("confirmada".equals(status)) {
            reservaExistente.confirmar();
        } else if ("em uso".equals(status)) {
            reservaExistente.confirmar();
            reservaExistente.iniciarUso();
        }

        reservasExistentes.add(reservaExistente);
    }

    @Given("existe uma reserva confirmada do item {string} com quantidade {int} em periodo sem sobreposicao")
    public void existeUmaReservaConfirmadaDoItemComQuantidadeEmPeriodoSemSobreposicao(String itemId, int quantidade) {
        ReservaEstoque reservaExistente = new ReservaEstoque(
                EVENTO_CONFLITO_ID,
                dataInicioSolicitada.minusDays(3),
                dataInicioSolicitada.minusDays(2),
                List.of(new ItemReserva("solicitacao-sem-sobreposicao", itemId, quantidade))
        );
        reservaExistente.confirmar();
        reservasExistentes.add(reservaExistente);
    }

    @Given("existem reservas pendentes, confirmadas ou em uso que nao comprometem a solicitacao")
    public void existemReservasPendentesConfirmadasOuEmUsoQueNaoComprometemASolicitacao() {
        ReservaEstoque pendente = new ReservaEstoque(
                EVENTO_CONFLITO_ID,
                dataInicioSolicitada,
                dataFimSolicitada,
                List.of(new ItemReserva("item-pendente", ITEM_CADEIRA_ID, 20))
        );

        ReservaEstoque confirmada = new ReservaEstoque(
                "evento-confirmado",
                dataInicioSolicitada,
                dataFimSolicitada,
                List.of(new ItemReserva("item-confirmado", ITEM_PROJETOR_ID, 1))
        );
        confirmada.confirmar();

        ReservaEstoque emUso = new ReservaEstoque(
                "evento-em-uso",
                dataInicioSolicitada,
                dataFimSolicitada,
                List.of(new ItemReserva("item-em-uso", ITEM_CADEIRA_ID, 10))
        );
        emUso.confirmar();
        emUso.iniciarUso();

        reservasExistentes.add(pendente);
        reservasExistentes.add(confirmada);
        reservasExistentes.add(emUso);
    }

    @Given("o gestor solicita reservar {int} unidades do item {string}")
    public void oGestorSolicitaReservarUnidadesDoItem(int quantidade, String itemId) {
        itensSolicitados = List.of(new ItemReserva("solicitacao-atual", itemId, quantidade));
    }

    @Given("o gestor solicita reservar os itens abaixo")
    public void oGestorSolicitaReservarOsItensAbaixo(io.cucumber.datatable.DataTable dataTable) {
        itensSolicitados = dataTable.asMaps().stream()
                .map(linha -> new ItemReserva(
                        "solicitacao-atual",
                        linha.get("item"),
                        Integer.parseInt(linha.get("quantidade"))
                ))
                .toList();
    }

    @Given("existe uma reserva pendente vinculada ao evento atual")
    public void existeUmaReservaPendenteVinculadaAoEventoAtual() {
        reservaEmContexto = new ReservaEstoque(
                EVENTO_ATUAL_ID,
                dataInicioSolicitada,
                dataFimSolicitada,
                List.of(new ItemReserva("solicitacao-atual", ITEM_CADEIRA_ID, 30))
        );
        when(reservaEstoqueRepository.buscarPorId(any()))
                .thenReturn(Optional.of(reservaEmContexto));
        reservasExistentes.add(reservaEmContexto);
    }

    @Given("existe uma reserva pendente vinculada ao evento atual com conflito de disponibilidade")
    public void existeUmaReservaPendenteVinculadaAoEventoAtualComConflitoDeDisponibilidade() {
        existeUmaReservaPendenteVinculadaAoEventoAtual();
        ReservaEstoque reservaConcorrente = new ReservaEstoque(
                EVENTO_CONFLITO_ID,
                dataInicioSolicitada,
                dataFimSolicitada,
                List.of(new ItemReserva("reserva-concorrente", ITEM_CADEIRA_ID, 80))
        );
        reservaConcorrente.confirmar();
        reservasExistentes.add(reservaConcorrente);
    }

    @When("o gestor verificar a disponibilidade da solicitacao")
    public void oGestorVerificarADisponibilidadeDaSolicitacao() {
        try {
            resultadoDisponibilidade = reservaEstoqueService.verificarDisponibilidade(
                    EVENTO_ATUAL_ID,
                    dataInicioSolicitada,
                    dataFimSolicitada,
                    itensSolicitados
            );
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("o gestor criar a reserva de estoque")
    public void oGestorCriarAReservaDeEstoque() {
        try {
            reservaEmContexto = reservaEstoqueService.criarReserva(
                    EVENTO_ATUAL_ID,
                    dataInicioSolicitada,
                    dataFimSolicitada,
                    itensSolicitados
            );
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("o gestor confirmar a reserva sem autorizacao especial")
    public void oGestorConfirmarAReservaSemAutorizacaoEspecial() {
        try {
            reservaEmContexto = reservaEstoqueService.confirmarReserva(RESERVA_ATUAL_ID, null, false);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("o gestor confirmar a reserva com justificativa {string} e autorizacao especial")
    public void oGestorConfirmarAReservaComJustificativaEAutorizacaoEspecial(String justificativa) {
        try {
            reservaEmContexto = reservaEstoqueService.confirmarReserva(RESERVA_ATUAL_ID, justificativa, true);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("o gestor alterar a solicitacao para {int} unidades do item {string}")
    public void oGestorAlterarASolicitacaoParaUnidadesDoItem(int quantidade, String itemId) {
        try {
            resultadoDisponibilidadeAnterior = reservaEstoqueService.verificarDisponibilidade(
                    EVENTO_ATUAL_ID,
                    dataInicioSolicitada,
                    dataFimSolicitada,
                    reservaEmContexto.getItensReservados()
            );
            reservaEmContexto = reservaEstoqueService.atualizarSolicitacao(
                    RESERVA_ATUAL_ID,
                    dataInicioSolicitada,
                    dataFimSolicitada.plusHours(2),
                    List.of(new ItemReserva("solicitacao-atualizada", itemId, quantidade))
            );
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @Then("o sistema deve sinalizar conflito de disponibilidade")
    public void oSistemaDeveSinalizarConflitoDeDisponibilidade() {
        assertNull(excecaoLancada);
        assertNotNull(resultadoDisponibilidade);
        assertTrue(resultadoDisponibilidade.isPossuiConflito());
    }

    @Then("o sistema deve exibir o item {string} em conflito com o evento {string}")
    public void oSistemaDeveExibirOItemEmConflitoComOEvento(String itemId, String eventoId) {
        assertNull(excecaoLancada);
        assertTrue(resultadoDisponibilidade.getConflitos().stream()
                .anyMatch(conflito -> conflito.getItemEstoqueId().equals(itemId)
                        && conflito.getEventosEmConflito().contains(eventoId)));
    }

    @Then("o sistema deve exibir os itens {string} e {string} em conflito")
    public void oSistemaDeveExibirOsItensEEmConflito(String itemA, String itemB) {
        assertNull(excecaoLancada);
        assertEquals(2, resultadoDisponibilidade.getConflitos().size());
        assertTrue(resultadoDisponibilidade.getConflitos().stream()
                .anyMatch(conflito -> conflito.getItemEstoqueId().equals(itemA)));
        assertTrue(resultadoDisponibilidade.getConflitos().stream()
                .anyMatch(conflito -> conflito.getItemEstoqueId().equals(itemB)));
    }

    @Then("o sistema deve considerar reservas pendentes, confirmadas e em uso no calculo")
    public void oSistemaDeveConsiderarReservasPendentesConfirmadasEEmUsoNoCalculo() {
        assertNull(excecaoLancada);
        assertNotNull(resultadoDisponibilidade);
        assertFalse(resultadoDisponibilidade.isPossuiConflito());
    }

    @Then("o sistema deve indicar que nao ha conflito de disponibilidade")
    public void oSistemaDeveIndicarQueNaoHaConflitoDeDisponibilidade() {
        assertNull(excecaoLancada);
        assertNotNull(resultadoDisponibilidade);
        assertFalse(resultadoDisponibilidade.isPossuiConflito());
    }

    @Then("a reserva deve ser confirmada por autorizacao especial")
    public void aReservaDeveSerConfirmadaPorAutorizacaoEspecial() {
        assertNull(excecaoLancada);
        assertNotNull(reservaEmContexto);
        assertEquals(StatusReservaEstoque.CONFIRMADA, reservaEmContexto.getStatus());
    }

    @Then("a reserva deve ser registrada vinculada ao evento e aos itens solicitados")
    public void aReservaDeveSerRegistradaVinculadaAoEventoEAosItensSolicitados() {
        assertNull(excecaoLancada);
        assertNotNull(reservaEmContexto);
        assertEquals(EVENTO_ATUAL_ID, reservaEmContexto.getEventoId());
        assertEquals(1, reservaEmContexto.getItensReservados().size());
        assertEquals(ITEM_CADEIRA_ID, reservaEmContexto.getItensReservados().get(0).getItemEstoqueId());
    }

    @Then("o sistema deve bloquear a confirmacao da reserva")
    public void oSistemaDeveBloquearAConfirmacaoDaReserva() {
        assertNotNull(excecaoLancada);
        assertTrue(excecaoLancada.getMessage().contains("Nao e permitido confirmar reserva"));
    }

    @Then("a solicitacao deve ser atualizada para nova validacao")
    public void aSolicitacaoDeveSerAtualizadaParaNovaValidacao() {
        assertNull(excecaoLancada);
        assertNotNull(reservaEmContexto);
        assertEquals(40, reservaEmContexto.getItensReservados().get(0).getQuantidade());
        assertEquals(dataFimSolicitada.plusHours(2), reservaEmContexto.getDataFim());
    }

    @Then("o sistema deve recalcular a disponibilidade considerando a nova solicitacao")
    public void oSistemaDeveRecalcularADisponibilidadeConsiderandoANovaSolicitacao() {
        assertNull(excecaoLancada);
        assertNotNull(resultadoDisponibilidadeAnterior);
        reservasExistentes.removeIf(reserva -> reserva.getEventoId().equals(EVENTO_ATUAL_ID));
        resultadoDisponibilidade = reservaEstoqueService.verificarDisponibilidade(
                EVENTO_ATUAL_ID,
                reservaEmContexto.getDataInicio(),
                reservaEmContexto.getDataFim(),
                reservaEmContexto.getItensReservados()
        );
        assertNotNull(resultadoDisponibilidade);
        assertTrue(resultadoDisponibilidadeAnterior.isPossuiConflito());
        assertFalse(resultadoDisponibilidade.isPossuiConflito());
    }
}
