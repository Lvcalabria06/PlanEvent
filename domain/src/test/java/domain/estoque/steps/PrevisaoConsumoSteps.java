package domain.estoque.steps;

import domain.estoque.entity.ConsumoEvento;
import domain.estoque.entity.ItemConsumoEvento;
import domain.estoque.entity.PrevisaoConsumo;
import domain.estoque.repository.ConsumoEventoRepository;
import domain.estoque.repository.PrevisaoConsumoRepository;
import domain.estoque.service.PrevisaoConsumoService;
import domain.estoque.service.PrevisaoConsumoServiceImpl;
import domain.estoque.valueobject.StatusHistoricoPrevisao;
import domain.estoque.valueobject.TipoRegistroPrevisao;
import domain.evento.entity.Evento;
import domain.evento.repository.EventoRepository;
import domain.evento.valueobject.PorteEvento;
import domain.evento.valueobject.TipoEvento;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.Mockito;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class PrevisaoConsumoSteps {

    private static final String EVENTO_ATUAL_ID = "evento-atual";
    private static final String EVENTO_HISTORICO_1_ID = "evento-historico-1";
    private static final String EVENTO_HISTORICO_2_ID = "evento-historico-2";
    private static final String PREVISAO_ID = "previsao-1";
    private static final String USUARIO_GESTOR_ID = "gestor-1";
    private static final String USUARIO_AJUSTE_ID = "gestor-ajuste";
    private static final String USUARIO_RECALCULO_ID = "gestor-recalculo";

    private EventoRepository eventoRepository;
    private ConsumoEventoRepository consumoEventoRepository;
    private PrevisaoConsumoRepository previsaoConsumoRepository;
    private PrevisaoConsumoService previsaoConsumoService;

    private Evento eventoAtual;
    private Evento eventoHistorico1;
    private Evento eventoHistorico2;
    private PrevisaoConsumo previsaoEmContexto;
    private Exception excecaoLancada;
    private Map<String, Evento> eventosCadastrados;

    @Before
    public void setup() {
        eventoRepository = Mockito.mock(EventoRepository.class);
        consumoEventoRepository = Mockito.mock(ConsumoEventoRepository.class);
        previsaoConsumoRepository = Mockito.mock(PrevisaoConsumoRepository.class);

        previsaoConsumoService = new PrevisaoConsumoServiceImpl(
                eventoRepository,
                consumoEventoRepository,
                previsaoConsumoRepository
        );

        eventoAtual = null;
        eventoHistorico1 = null;
        eventoHistorico2 = null;
        previsaoEmContexto = null;
        excecaoLancada = null;
        eventosCadastrados = new HashMap<>();

        when(previsaoConsumoRepository.salvar(any(PrevisaoConsumo.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(eventoRepository.buscarPorId(any()))
                .thenAnswer(invocation -> Optional.ofNullable(eventosCadastrados.get(invocation.getArgument(0))));
    }

    @Given("que existe um evento corporativo medio com estimativa de {int} participantes")
    public void queExisteUmEventoCorporativoMedioComEstimativaDeParticipantes(int participantes) {
        eventoAtual = novoEvento("Forum de Inovacao", TipoEvento.CORPORATIVO, PorteEvento.MEDIO, participantes, "Networking");
        mockEvento(EVENTO_ATUAL_ID, eventoAtual);
    }

    @Given("que existe um evento academico medio com estimativa de {int} participantes")
    public void queExisteUmEventoAcademicoMedioComEstimativaDeParticipantes(int participantes) {
        eventoAtual = novoEvento("Congresso Academico", TipoEvento.ACADEMICO, PorteEvento.MEDIO, participantes, "Pesquisa");
        mockEvento(EVENTO_ATUAL_ID, eventoAtual);
    }

    @Given("que existe um evento corporativo pequeno com estimativa de {int} participantes")
    public void queExisteUmEventoCorporativoPequenoComEstimativaDeParticipantes(int participantes) {
        eventoAtual = novoEvento("Expo Negocios", TipoEvento.CORPORATIVO, PorteEvento.PEQUENO, participantes, "Lancamento");
        mockEvento(EVENTO_ATUAL_ID, eventoAtual);
    }

    @Given("que existem {int} eventos historicos concluidos do mesmo tipo com consumos validos")
    public void queExistemEventosHistoricosConcluidosDoMesmoTipoComConsumosValidos(int quantidadeEventos) {
        eventoHistorico1 = novoEvento("Historico 1", eventoAtual.getTipo(), PorteEvento.MEDIO, 100, eventoAtual.getObjetivo());
        eventoHistorico1.concluirEvento();
        mockEvento(EVENTO_HISTORICO_1_ID, eventoHistorico1);

        eventoHistorico2 = novoEvento("Historico 2", eventoAtual.getTipo(), PorteEvento.GRANDE, 400, eventoAtual.getObjetivo());
        eventoHistorico2.concluirEvento();
        mockEvento(EVENTO_HISTORICO_2_ID, eventoHistorico2);

        if (quantidadeEventos == 2) {
            when(consumoEventoRepository.listarTodos()).thenReturn(List.of(
                    novoConsumo(EVENTO_HISTORICO_1_ID,
                            new ItemConsumoEvento("cadeira", 100),
                            new ItemConsumoEvento("agua", 200)),
                    novoConsumo(EVENTO_HISTORICO_2_ID,
                            new ItemConsumoEvento("cadeira", 360),
                            new ItemConsumoEvento("agua", 600))
            ));
        }
    }

    @Given("que existe um unico evento historico concluido com consumo valido de agua igual a {int}")
    public void queExisteUmUnicoEventoHistoricoConcluidoComConsumoValidoDeAguaIgualA(int quantidadeAgua) {
        eventoHistorico1 = novoEvento("Historico Agua", eventoAtual.getTipo(), eventoAtual.getPorte(),
                eventoAtual.getQuantidadeEstimadaParticipantes(), eventoAtual.getObjetivo());
        eventoHistorico1.concluirEvento();
        mockEvento(EVENTO_HISTORICO_1_ID, eventoHistorico1);

        when(consumoEventoRepository.listarTodos()).thenReturn(List.of(
                novoConsumo(EVENTO_HISTORICO_1_ID, new ItemConsumoEvento("agua", quantidadeAgua))
        ));
    }

    @Given("que existe um unico evento historico concluido com consumo valido de copos igual a {int}")
    public void queExisteUmUnicoEventoHistoricoConcluidoComConsumoValidoDeCoposIgualA(int quantidadeCopos) {
        eventoHistorico1 = novoEvento("Historico Copo", eventoAtual.getTipo(), PorteEvento.PEQUENO, 100, "Lancamento");
        eventoHistorico1.concluirEvento();
        mockEvento(EVENTO_HISTORICO_1_ID, eventoHistorico1);

        when(consumoEventoRepository.listarTodos()).thenReturn(List.of(
                novoConsumo(EVENTO_HISTORICO_1_ID, new ItemConsumoEvento("copo", quantidadeCopos))
        ));
    }

    @Given("que existe outro evento historico concluido de porte grande com consumo valido de copos igual a {int}")
    public void queExisteOutroEventoHistoricoConcluidoDePorteGrandeComConsumoValidoDeCoposIgualA(int quantidadeCopos) {
        eventoHistorico2 = novoEvento("Historico Copo Grande", eventoAtual.getTipo(), PorteEvento.GRANDE, 200, "Lancamento");
        eventoHistorico2.concluirEvento();
        mockEvento(EVENTO_HISTORICO_2_ID, eventoHistorico2);

        when(consumoEventoRepository.listarTodos()).thenReturn(List.of(
                novoConsumo(EVENTO_HISTORICO_1_ID, new ItemConsumoEvento("copo", 100)),
                novoConsumo(EVENTO_HISTORICO_2_ID, new ItemConsumoEvento("copo", quantidadeCopos))
        ));
    }

    @When("eu gero a previsao de consumo para esse evento")
    public void euGeroAPrevisaoDeConsumoParaEsseEvento() {
        try {
            previsaoEmContexto = previsaoConsumoService.gerarPrevisao(EVENTO_ATUAL_ID, USUARIO_GESTOR_ID);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu ajusto manualmente a quantidade prevista do item {string} para {int}")
    public void euAjustoManualmenteAQuantidadePrevistaDoItemPara(String itemId, int quantidade) {
        try {
            mockPrevisaoAtual();
            previsaoEmContexto = previsaoConsumoService.ajustarPrevisao(
                    previsaoEmContexto.getId(),
                    Map.of(itemId, quantidade),
                    USUARIO_AJUSTE_ID
            );
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu altero o porte do evento para grande e a estimativa para {int} participantes")
    public void euAlteroOPorteDoEventoParaGrandeEAEstimativaParaParticipantes(int participantes) {
        eventoAtual.atualizarDados(
                eventoAtual.getNome(),
                eventoAtual.getTipo(),
                PorteEvento.GRANDE,
                participantes,
                "Lancamento ampliado"
        );
        mockEvento(EVENTO_ATUAL_ID, eventoAtual);
    }

    @When("eu recalculo a previsao desse evento")
    public void euRecalculoAPrevisaoDesseEvento() {
        try {
            mockPrevisaoAtual();
            previsaoEmContexto = previsaoConsumoService.recalcularPrevisao(previsaoEmContexto.getId(), USUARIO_RECALCULO_ID);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @Then("a previsao deve ser gerada com sucesso")
    public void aPrevisaoDeveSerGeradaComSucesso() {
        assertNull(excecaoLancada, "Nao deveria lancar excecao.");
        assertNotNull(previsaoEmContexto);
    }

    @Then("a previsao deve indicar historico suficiente")
    public void aPrevisaoDeveIndicarHistoricoSuficiente() {
        assertNull(excecaoLancada);
        assertEquals(StatusHistoricoPrevisao.SUFICIENTE, previsaoEmContexto.getStatusHistorico());
        assertEquals(2, previsaoEmContexto.getTotalEventosBase());
    }

    @Then("a previsao deve indicar historico inexistente")
    public void aPrevisaoDeveIndicarHistoricoInexistente() {
        assertNull(excecaoLancada);
        assertEquals(StatusHistoricoPrevisao.INEXISTENTE, previsaoEmContexto.getStatusHistorico());
        assertEquals(0, previsaoEmContexto.getTotalEventosBase());
        assertTrue(previsaoEmContexto.getItens().isEmpty());
    }

    @Then("a previsao deve indicar historico insuficiente")
    public void aPrevisaoDeveIndicarHistoricoInsuficiente() {
        assertNull(excecaoLancada);
        assertEquals(StatusHistoricoPrevisao.INSUFICIENTE, previsaoEmContexto.getStatusHistorico());
        assertEquals(1, previsaoEmContexto.getTotalEventosBase());
    }

    @Then("a previsao deve conter o item {string} com quantidade ajustada igual a {int}")
    public void aPrevisaoDeveConterOItemComQuantidadeAjustadaIgualA(String itemId, int quantidadeEsperada) {
        assertNull(excecaoLancada);
        assertEquals(quantidadeEsperada, buscarQuantidadeAjustada(itemId));
    }

    @Then("a previsao deve registrar a geracao inicial vinculada ao evento e ao usuario")
    public void aPrevisaoDeveRegistrarAGeracaoInicialVinculadaAoEventoEAoUsuario() {
        assertNull(excecaoLancada);
        assertEquals(eventoAtual.getId(), previsaoEmContexto.getEventoId());
        assertEquals(USUARIO_GESTOR_ID, previsaoEmContexto.getGeradoPorUsuarioId());
        assertNotNull(previsaoEmContexto.getDataGeracao());
        assertFalse(previsaoEmContexto.getHistoricoRegistros().isEmpty());
        assertEquals(TipoRegistroPrevisao.GERACAO_INICIAL, previsaoEmContexto.getHistoricoRegistros().get(0).getTipoRegistro());
    }

    @Then("o historico da previsao deve preservar o valor original de {int} para o item {string}")
    public void oHistoricoDaPrevisaoDevePreservarOValorOriginalDeParaOItem(int quantidadeOriginal, String itemId) {
        assertNull(excecaoLancada);
        int quantidadeRegistrada = previsaoEmContexto.getHistoricoRegistros().get(0).getItens().stream()
                .filter(item -> item.getItemEstoqueId().equals(itemId))
                .findFirst()
                .orElseThrow()
                .getQuantidadeAjustada();
        assertEquals(quantidadeOriginal, quantidadeRegistrada);
    }

    @Then("o historico da previsao deve registrar um ajuste manual")
    public void oHistoricoDaPrevisaoDeveRegistrarUmAjusteManual() {
        assertNull(excecaoLancada);
        assertEquals(TipoRegistroPrevisao.AJUSTE_MANUAL, previsaoEmContexto.getHistoricoRegistros().get(1).getTipoRegistro());
    }

    @Then("o historico da previsao deve registrar um recalculo")
    public void oHistoricoDaPrevisaoDeveRegistrarUmRecalculo() {
        assertNull(excecaoLancada);
        assertEquals(TipoRegistroPrevisao.RECALCULO, previsaoEmContexto.getHistoricoRegistros().get(1).getTipoRegistro());
    }

    @Then("o sistema deve impedir o recalculo da previsao")
    public void oSistemaDeveImpedirORecalculoDaPrevisao() {
        assertNotNull(excecaoLancada, "Era esperada uma excecao.");
        assertTrue(excecaoLancada.getMessage().contains("alteracoes relevantes"));
    }

    private void mockEvento(String eventoId, Evento evento) {
        eventosCadastrados.put(eventoId, evento);
        eventosCadastrados.put(evento.getId(), evento);
    }

    private void mockPrevisaoAtual() {
        when(previsaoConsumoRepository.buscarPorId(eq(previsaoEmContexto.getId())))
                .thenReturn(Optional.of(previsaoEmContexto));
        when(previsaoConsumoRepository.buscarPorEventoId(eq(EVENTO_ATUAL_ID)))
                .thenReturn(Optional.of(previsaoEmContexto));
    }

    private Evento novoEvento(String nome, TipoEvento tipo, PorteEvento porte, int participantes, String objetivo) {
        return new Evento(nome, tipo, porte, participantes, objetivo, "local-1");
    }

    private ConsumoEvento novoConsumo(String eventoId, ItemConsumoEvento... itens) {
        return new ConsumoEvento(eventoId, USUARIO_GESTOR_ID, List.of(itens));
    }

    private int buscarQuantidadeAjustada(String itemId) {
        return previsaoEmContexto.getItens().stream()
                .filter(item -> item.getItemEstoqueId().equals(itemId))
                .findFirst()
                .orElseThrow()
                .getQuantidadeAjustada();
    }
}
