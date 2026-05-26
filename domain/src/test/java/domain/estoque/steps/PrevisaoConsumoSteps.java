package domain.estoque.steps;

import domain.estoque.entity.ConsumoEvento;
import domain.estoque.entity.ItemConsumoEvento;
import domain.estoque.entity.ItemPrevisao;
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

public class PrevisaoConsumoSteps {

    private static final String ID_EVENTO = "evento-previsao-1";
    private static final String ID_USUARIO = "gestor-estoque";
    private static final String ID_USUARIO_AJUSTE = "gestor-ajuste";

    private EventoRepository eventoRepository;
    private ConsumoEventoRepository consumoEventoRepository;
    private PrevisaoConsumoRepository previsaoConsumoRepository;
    private PrevisaoConsumoService previsaoConsumoService;

    private Evento eventoAtual;
    private final List<ConsumoEvento> consumos = new ArrayList<>();
    private PrevisaoConsumo previsaoEmContexto;
    private Exception excecaoLancada;

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

        consumos.clear();
        eventoAtual = null;
        previsaoEmContexto = null;
        excecaoLancada = null;

        when(consumoEventoRepository.listarTodos()).thenAnswer(invocation -> List.copyOf(consumos));
        when(previsaoConsumoRepository.salvar(any(PrevisaoConsumo.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Given("existe um evento valido para previsao de consumo")
    public void existeUmEventoValidoParaPrevisaoDeConsumo() {
        eventoAtual = new Evento("Feira de Negocios", TipoEvento.CORPORATIVO, PorteEvento.MEDIO, 200, "Planejamento", "local-1");
        eventoAtual.definirJanelaPlanejamento(LocalDateTime.of(2026, 6, 10, 8, 0), LocalDateTime.of(2026, 6, 10, 18, 0));
        when(eventoRepository.buscarPorId(eq(ID_EVENTO))).thenReturn(Optional.of(eventoAtual));
        when(eventoRepository.buscarPorId(eq(eventoAtual.getId()))).thenReturn(Optional.of(eventoAtual));
    }

    @Given("existem eventos concluidos similares do mesmo tipo, porte e categoria {string}")
    public void existemEventosConcluidosSimilaresDoMesmoTipoPorteECategoria(String categoria) {
        adicionarHistoricoValido("Historico 1", PorteEvento.MEDIO, 100, categoria, 100);
        adicionarHistoricoValido("Historico 2", PorteEvento.MEDIO, 150, categoria, 150);
    }

    @Given("existe um registro inconsistente ou incompleto no historico da categoria {string}")
    public void existeUmRegistroInconsistenteOuIncompletoNoHistoricoDaCategoria(String categoria) {
        Evento historicoInvalido = new Evento("Historico Invalido", TipoEvento.CORPORATIVO, PorteEvento.MEDIO, 120, "Planejamento", "local-2");
        when(eventoRepository.buscarPorId(eq(historicoInvalido.getId()))).thenReturn(Optional.of(historicoInvalido));
        ConsumoEvento consumoInvalido = new ConsumoEvento(historicoInvalido.getId(), ID_USUARIO, List.of(
                new ItemConsumoEvento("agua", categoria, 90)
        ));
        consumoInvalido.invalidar();
        consumos.add(consumoInvalido);
    }

    @Given("existe um outlier historico na categoria {string}")
    public void existeUmOutlierHistoricoNaCategoria(String categoria) {
        adicionarHistoricoValido("Historico 3", PorteEvento.MEDIO, 100, categoria, 110);
        adicionarHistoricoValido("Historico 4", PorteEvento.MEDIO, 100, categoria, 1000);
    }

    @Given("nao existe historico suficiente para a categoria {string}")
    public void naoExisteHistoricoSuficienteParaACategoria(String categoria) {
        adicionarHistoricoValido("Historico Unico", PorteEvento.MEDIO, 100, categoria, 100);
    }

    @When("eu gero a previsao de estoque para o evento")
    public void euGeroAPrevisaoDeEstoqueParaOEvento() {
        try {
            previsaoEmContexto = previsaoConsumoService.gerarPrevisao(ID_EVENTO, ID_USUARIO);
            when(previsaoConsumoRepository.buscarPorId(eq(previsaoEmContexto.getId()))).thenReturn(Optional.of(previsaoEmContexto));
            when(previsaoConsumoRepository.buscarPorEventoId(eq(ID_EVENTO))).thenReturn(Optional.of(previsaoEmContexto));
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu ajusto manualmente o item {string} para {int} unidades com justificativa {string}")
    public void euAjustoManualmenteOItemParaUnidadesComJustificativa(String itemId, int quantidade, String justificativa) {
        try {
            previsaoEmContexto = previsaoConsumoService.ajustarPrevisao(
                    previsaoEmContexto.getId(),
                    java.util.Map.of(itemId, quantidade),
                    ID_USUARIO_AJUSTE,
                    justificativa
            );
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu altero dados relevantes do evento para invalidar a previsao")
    public void euAlteroDadosRelevantesDoEventoParaInvalidarAPrevisao() {
        eventoAtual.atualizarDados(
                eventoAtual.getNome(),
                eventoAtual.getTipo(),
                PorteEvento.GRANDE,
                eventoAtual.getQuantidadeEstimadaParticipantes(),
                eventoAtual.getObjetivo()
        );
        when(eventoRepository.buscarPorId(eq(ID_EVENTO))).thenReturn(Optional.of(eventoAtual));
        when(eventoRepository.buscarPorId(eq(eventoAtual.getId()))).thenReturn(Optional.of(eventoAtual));
        try {
            previsaoEmContexto = previsaoConsumoService.invalidarPrevisaoPorAlteracaoEvento(ID_EVENTO, ID_USUARIO);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu recalculo a previsao de estoque")
    public void euRecalculoAPrevisaoDeEstoque() {
        try {
            previsaoEmContexto = previsaoConsumoService.recalcularPrevisao(previsaoEmContexto.getId(), ID_USUARIO);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @Then("a previsao deve ser gerada com media ponderada baseada em eventos similares")
    public void aPrevisaoDeveSerGeradaComMediaPonderadaBaseadaEmEventosSimilares() {
        assertNull(excecaoLancada);
        assertNotNull(previsaoEmContexto);
        assertEquals(StatusHistoricoPrevisao.SUFICIENTE, previsaoEmContexto.getStatusHistorico());
        assertTrue(previsaoEmContexto.getTotalEventosBase() >= 2);
    }

    @Then("o sistema deve ignorar registros historicos invalidos")
    public void oSistemaDeveIgnorarRegistrosHistoricosInvalidos() {
        assertNull(excecaoLancada);
        assertEquals(4, previsaoEmContexto.getTotalEventosBase());
    }

    @Then("a previsao deve estar normalizada ao contexto do evento atual")
    public void aPrevisaoDeveEstarNormalizadaAoContextoDoEventoAtual() {
        assertNull(excecaoLancada);
        ItemPrevisao item = previsaoEmContexto.getItens().get(0);
        assertTrue(item.getQuantidadeEstimada() > 150);
    }

    @Then("o sistema deve desconsiderar outliers automaticamente")
    public void oSistemaDeveDesconsiderarOutliersAutomaticamente() {
        assertNull(excecaoLancada);
        ItemPrevisao item = previsaoEmContexto.getItens().get(0);
        assertTrue(item.getQuantidadeEstimada() < 500);
    }

    @Then("o sistema deve aplicar fallback com indicacao explicita")
    public void oSistemaDeveAplicarFallbackComIndicacaoExplicita() {
        assertNull(excecaoLancada);
        assertEquals(StatusHistoricoPrevisao.FALLBACK, previsaoEmContexto.getStatusHistorico());
        assertTrue(previsaoEmContexto.isFallbackUtilizado());
    }

    @Then("cada item previsto deve possuir quantidade estimada e intervalo minimo maximo")
    public void cadaItemPrevistoDevePossuirQuantidadeEstimadaEIntervaloMinimoMaximo() {
        assertNull(excecaoLancada);
        ItemPrevisao item = previsaoEmContexto.getItens().get(0);
        assertTrue(item.getQuantidadeMinima() <= item.getQuantidadeEstimada());
        assertTrue(item.getQuantidadeMaxima() >= item.getQuantidadeEstimada());
    }

    @Then("cada item previsto deve apresentar explicacao detalhada com eventos pesos e ajustes")
    public void cadaItemPrevistoDeveApresentarExplicacaoDetalhadaComEventosPesosEAjustes() {
        assertNull(excecaoLancada);
        String explicacao = previsaoEmContexto.getItens().get(0).getExplicacaoCalculo();
        assertTrue(explicacao.contains("Eventos usados"),
                "Explicacao deve listar os eventos usados: " + explicacao);
        assertTrue(explicacao.contains("Pesos"),
                "Explicacao deve detalhar os pesos aplicados: " + explicacao);
        assertTrue(explicacao.contains("Ajustes"),
                "Explicacao deve descrever os ajustes aplicados: " + explicacao);
    }

    @Then("o ajuste manual deve sobrescrever a previsao com usuario data hora e justificativa")
    public void oAjusteManualDeveSobrescreverAPrevisaoComUsuarioDataHoraEJustificativa() {
        assertNull(excecaoLancada);
        assertEquals(180, previsaoEmContexto.getItens().get(0).getQuantidadeFinal());
        assertEquals(TipoRegistroPrevisao.AJUSTE_MANUAL,
                previsaoEmContexto.getHistoricoRegistros().get(previsaoEmContexto.getHistoricoRegistros().size() - 1).getTipoRegistro());
        assertEquals(ID_USUARIO_AJUSTE,
                previsaoEmContexto.getHistoricoRegistros().get(previsaoEmContexto.getHistoricoRegistros().size() - 1).getUsuarioResponsavelId());
        assertEquals("Ajuste operacional",
                previsaoEmContexto.getHistoricoRegistros().get(previsaoEmContexto.getHistoricoRegistros().size() - 1).getJustificativa());
        assertNotNull(previsaoEmContexto.getHistoricoRegistros().get(previsaoEmContexto.getHistoricoRegistros().size() - 1).getDataHora());
    }

    @Then("a previsao deve ficar vinculada ao evento com metadados de geracao")
    public void aPrevisaoDeveFicarVinculadaAoEventoComMetadadosDeGeracao() {
        assertNull(excecaoLancada);
        assertEquals(eventoAtual.getId(), previsaoEmContexto.getEventoId());
        assertEquals(ID_USUARIO, previsaoEmContexto.getGeradoPorUsuarioId());
        assertNotNull(previsaoEmContexto.getDataGeracao());
    }

    @Then("a previsao deve ser invalidada automaticamente por alteracao relevante")
    public void aPrevisaoDeveSerInvalidadaAutomaticamentePorAlteracaoRelevante() {
        assertNull(excecaoLancada);
        assertTrue(previsaoEmContexto.isInvalidada());
        assertEquals(StatusHistoricoPrevisao.INVALIDADA, previsaoEmContexto.getStatusHistorico());
    }

    @Then("o sistema deve manter historico da versao original e das recalculadas")
    public void oSistemaDeveManterHistoricoDaVersaoOriginalEDasRecalculadas() {
        assertNull(excecaoLancada);
        assertTrue(previsaoEmContexto.getVersaoAtual() >= 2);
        assertEquals(TipoRegistroPrevisao.RECALCULO,
                previsaoEmContexto.getHistoricoRegistros().get(previsaoEmContexto.getHistoricoRegistros().size() - 1).getTipoRegistro());
        assertFalse(previsaoEmContexto.getHistoricoRegistros().isEmpty());
    }

    private void adicionarHistoricoValido(String nomeEvento,
                                          PorteEvento porte,
                                          int participantes,
                                          String categoria,
                                          int consumo) {
        Evento historico = new Evento(nomeEvento, TipoEvento.CORPORATIVO, porte, participantes, "Planejamento", "local-h");
        historico.definirJanelaPlanejamento(LocalDateTime.of(2026, 5, 1, 8, 0), LocalDateTime.of(2026, 5, 1, 18, 0));
        historico.concluirEvento();
        when(eventoRepository.buscarPorId(eq(historico.getId()))).thenReturn(Optional.of(historico));
        consumos.add(new ConsumoEvento(historico.getId(), ID_USUARIO, List.of(
                new ItemConsumoEvento("agua", categoria, consumo)
        )));
    }
}
