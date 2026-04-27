package domain.evento.steps;

import domain.evento.entity.Evento;
import domain.evento.repository.EventoRepository;
import domain.evento.service.EventoService;
import domain.evento.service.EventoServiceImpl;
import domain.evento.valueobject.PorteEvento;
import domain.evento.valueobject.TipoEvento;
import domain.local.entity.Local;
import domain.local.repository.LocalRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EventoSteps {

    private EventoRepository eventoRepository;
    private LocalRepository localRepository;
    private EventoService eventoService;

    private Evento eventoAtual;
    private Evento eventoCadastrado;
    private Local localCompativel;
    private Exception excecaoAtual;
    private List<Local> locaisFiltrados;

    @Before
    public void setup() {
        eventoRepository = mock(EventoRepository.class);
        localRepository = mock(LocalRepository.class);
        eventoService = new EventoServiceImpl(eventoRepository, localRepository);

        when(eventoRepository.salvar(any(Evento.class))).thenAnswer(invocation -> invocation.getArgument(0));

        eventoAtual = null;
        eventoCadastrado = null;
        localCompativel = null;
        excecaoAtual = null;
        locaisFiltrados = null;
    }

    @Given("que eu possuo dados válidos para cadastro de evento")
    public void queEuPossuoDadosValidosParaCadastroDeEvento() {
        // Dados usados no passo de cadastro
    }

    @Given("que eu possuo dados inválidos de nome para evento")
    public void queEuPossuoDadosInvalidosDeNomeParaEvento() {
        // O nome inválido é aplicado no passo de tentativa de cadastro
    }

    @Given("que existe um evento cadastrado e não confirmado")
    public void queExisteUmEventoCadastradoENaoConfirmado() {
        eventoAtual = new Evento(
                "Feira de Tecnologia",
                TipoEvento.CORPORATIVO,
                PorteEvento.MEDIO,
                200,
                "Conectar expositores e clientes",
                null
        );
        when(eventoRepository.buscarPorId(eventoAtual.getId())).thenReturn(Optional.of(eventoAtual));
    }

    @Given("que existe um evento não confirmado para escolha de local")
    public void queExisteUmEventoNaoConfirmadoParaEscolhaDeLocal() {
        queExisteUmEventoCadastradoENaoConfirmado();
    }

    @Given("que existe um evento já confirmado")
    public void queExisteUmEventoJaConfirmado() {
        eventoAtual = new Evento(
                "Feira de Tecnologia",
                TipoEvento.CORPORATIVO,
                PorteEvento.MEDIO,
                200,
                "Conectar expositores e clientes",
                null
        );
        eventoAtual.confirmarPlanejamento();
        when(eventoRepository.buscarPorId(eventoAtual.getId())).thenReturn(Optional.of(eventoAtual));
    }

    @Given("existem locais com custos, capacidades e status diferentes")
    public void existemLocaisComCustosCapacidadesEStatusDiferentes() {
        Local compativel = new Local("Centro A", 300, "Rua 1", "Auditório", "Som", BigDecimal.valueOf(1000));
        Local caro = new Local("Centro B", 300, "Rua 2", "Auditório", "Som", BigDecimal.valueOf(2500));
        Local pequeno = new Local("Centro C", 100, "Rua 3", "Auditório", "Som", BigDecimal.valueOf(900));
        Local inativo = new Local("Centro D", 400, "Rua 4", "Auditório", "Som", BigDecimal.valueOf(1200));
        inativo.desativar();

        when(localRepository.listarTodos()).thenReturn(List.of(compativel, caro, pequeno, inativo));
    }

    @Given("existe um local compatível para vínculo")
    public void existeUmLocalCompativelParaVinculo() {
        localCompativel = new Local("Centro A", 300, "Rua 1", "Auditório", "Som", BigDecimal.valueOf(1000));
        when(localRepository.buscarPorId(localCompativel.getId())).thenReturn(Optional.of(localCompativel));
    }

    @When("eu cadastro o evento")
    public void euCadastroOEvento() {
        try {
            eventoCadastrado = eventoService.cadastrarEvento(
                    "Feira de Tecnologia",
                    TipoEvento.CORPORATIVO,
                    PorteEvento.MEDIO,
                    200,
                    "Conectar expositores e clientes"
            );
        } catch (Exception e) {
            excecaoAtual = e;
        }
    }

    @When("eu tento cadastrar o evento")
    public void euTentoCadastrarOEvento() {
        try {
            eventoService.cadastrarEvento(
                    "   ",
                    TipoEvento.CORPORATIVO,
                    PorteEvento.MEDIO,
                    200,
                    "Conectar expositores e clientes"
            );
        } catch (Exception e) {
            excecaoAtual = e;
        }
    }

    @When("eu edito os dados do evento com valores válidos")
    public void euEditoOsDadosDoEventoComValoresValidos() {
        try {
            eventoAtual = eventoService.editarEvento(
                    eventoAtual.getId(),
                    "Feira de Tecnologia 2026",
                    TipoEvento.ACADEMICO,
                    PorteEvento.GRANDE,
                    350,
                    "Capacitação técnica"
            );
        } catch (Exception e) {
            excecaoAtual = e;
        }
    }

    @When("eu confirmo a preparação inicial do evento")
    public void euConfirmoAPreparacaoInicialDoEvento() {
        try {
            eventoAtual = eventoService.confirmarPreparacaoInicial(eventoAtual.getId());
        } catch (Exception e) {
            excecaoAtual = e;
        }
    }

    @When("eu tento confirmar novamente a preparação inicial do evento")
    public void euTentoConfirmarNovamenteAPreparacaoInicialDoEvento() {
        try {
            eventoService.confirmarPreparacaoInicial(eventoAtual.getId());
        } catch (Exception e) {
            excecaoAtual = e;
        }
    }

    @When("eu busco locais compatíveis com teto de custo {double}")
    public void euBuscoLocaisCompativeisComTetoDeCusto(Double teto) {
        try {
            locaisFiltrados = eventoService.listarLocaisCompativeis(eventoAtual.getId(), BigDecimal.valueOf(teto));
        } catch (Exception e) {
            excecaoAtual = e;
        }
    }

    @When("eu vinculo o local ao evento com teto de custo {double}")
    public void euVinculoOLocalAoEventoComTetoDeCusto(Double teto) {
        try {
            eventoAtual = eventoService.vincularLocalAoEvento(
                    eventoAtual.getId(),
                    localCompativel.getId(),
                    BigDecimal.valueOf(teto)
            );
        } catch (Exception e) {
            excecaoAtual = e;
        }
    }

    @When("eu tento vincular um local ao evento com teto de custo {double}")
    public void euTentoVincularUmLocalAoEventoComTetoDeCusto(Double teto) {
        try {
            eventoService.vincularLocalAoEvento(
                    eventoAtual.getId(),
                    "local-invalido",
                    BigDecimal.valueOf(teto)
            );
        } catch (Exception e) {
            excecaoAtual = e;
        }
    }

    @When("eu tento vincular o local ao evento confirmado com teto de custo {double}")
    public void euTentoVincularOLocalAoEventoConfirmadoComTetoDeCusto(Double teto) {
        try {
            eventoService.vincularLocalAoEvento(
                    eventoAtual.getId(),
                    localCompativel.getId(),
                    BigDecimal.valueOf(teto)
            );
        } catch (Exception e) {
            excecaoAtual = e;
        }
    }

    @Then("o evento deve ser salvo com sucesso")
    public void oEventoDeveSerSalvoComSucesso() {
        assertNull(excecaoAtual);
        assertNotNull(eventoCadastrado);
        verify(eventoRepository, times(1)).salvar(any(Evento.class));
    }

    @And("o evento deve iniciar com preparação não confirmada")
    public void oEventoDeveIniciarComPreparacaoNaoConfirmada() {
        assertNotNull(eventoCadastrado);
        assertFalse(eventoCadastrado.isPlanejamentoConfirmado());
    }

    @Then("deve ocorrer erro de validação de evento")
    public void deveOcorrerErroDeValidacaoDeEvento() {
        assertNotNull(excecaoAtual);
        assertTrue(excecaoAtual instanceof IllegalArgumentException);
    }

    @Then("os dados do evento devem ser atualizados")
    public void osDadosDoEventoDevemSerAtualizados() {
        assertNull(excecaoAtual);
        assertEquals("Feira de Tecnologia 2026", eventoAtual.getNome());
        assertEquals(TipoEvento.ACADEMICO, eventoAtual.getTipo());
        assertEquals(PorteEvento.GRANDE, eventoAtual.getPorte());
        assertEquals(350, eventoAtual.getQuantidadeEstimadaParticipantes());
    }

    @Then("o evento deve ficar confirmado")
    public void oEventoDeveFicarConfirmado() {
        assertNull(excecaoAtual);
        assertTrue(eventoAtual.isPlanejamentoConfirmado());
    }

    @Then("deve ocorrer erro de confirmação duplicada")
    public void deveOcorrerErroDeConfirmacaoDuplicada() {
        assertNotNull(excecaoAtual);
        assertTrue(excecaoAtual instanceof IllegalStateException);
    }

    @Then("a lista deve retornar apenas locais compatíveis")
    public void aListaDeveRetornarApenasLocaisCompativeis() {
        assertNull(excecaoAtual);
        assertNotNull(locaisFiltrados);
        assertEquals(1, locaisFiltrados.size());
        assertEquals("Centro A", locaisFiltrados.get(0).getNome());
    }

    @Then("o local deve ficar vinculado ao evento")
    public void oLocalDeveFicarVinculadoAoEvento() {
        assertNull(excecaoAtual);
        assertEquals(localCompativel.getId(), eventoAtual.getLocalId());
    }

    @Then("deve ocorrer erro de teto de custo inválido")
    public void deveOcorrerErroDeTetoDeCustoInvalido() {
        assertNotNull(excecaoAtual);
        assertTrue(excecaoAtual instanceof IllegalArgumentException);
        assertTrue(excecaoAtual.getMessage().contains("Teto de custo"));
    }

    @Then("deve ocorrer erro por evento confirmado")
    public void deveOcorrerErroPorEventoConfirmado() {
        assertNotNull(excecaoAtual);
        assertTrue(excecaoAtual instanceof IllegalStateException);
    }
}
