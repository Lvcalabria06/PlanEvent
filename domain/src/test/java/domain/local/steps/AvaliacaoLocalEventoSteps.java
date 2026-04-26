package domain.local.steps;

import domain.evento.entity.Evento;
import domain.evento.service.EventoService;
import domain.evento.service.EventoServiceImpl;
import domain.evento.valueobject.PorteEvento;
import domain.evento.valueobject.TipoEvento;
import domain.local.entity.AvaliacaoLocalEvento;
import domain.local.entity.Local;
import domain.local.repository.LocalRepository;
import domain.local.service.AvaliacaoLocalEventoService;
import domain.local.service.AvaliacaoLocalEventoServiceImpl;
import domain.local.valueobject.NivelAdequacao;
import domain.local.support.InMemoryAvaliacaoLocalEventoRepository;
import domain.local.support.InMemoryEventoRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AvaliacaoLocalEventoSteps {

    private InMemoryEventoRepository memoriaEvento;
    private InMemoryAvaliacaoLocalEventoRepository memoriaAval;
    private Evento evento;
    private Local local;
    private EventoService eventoService;
    private AvaliacaoLocalEventoService avaliacaoService;
    private Exception excecao;
    private AvaliacaoLocalEvento avaliacaoSalva;
    private List<AvaliacaoLocalEvento> listaAval;
    private AvaliacaoLocalEvento registroVisto;
    private String outroId;

    @Given("cenario de avaliacao com evento vinculado a um local")
    public void cenarioBase() {
        excecao = null;
        avaliacaoSalva = null;
        memoriaEvento = new InMemoryEventoRepository();
        memoriaAval = new InMemoryAvaliacaoLocalEventoRepository();
        local = new Local("Espaço Aval", 200, "R. B", "Sala", "AC", BigDecimal.TEN);
        outroId = "local-outro-id-999";
        evento = new Evento(
                "Conferencia X",
                TipoEvento.CORPORATIVO,
                PorteEvento.MEDIO,
                150,
                "Networking",
                local.getId()
        );
        memoriaEvento.salvar(evento);
        LocalRepository localRepository = mock(LocalRepository.class);
        when(localRepository.buscarPorId(local.getId())).thenReturn(java.util.Optional.of(local));
        eventoService = new EventoServiceImpl(memoriaEvento, localRepository);
        avaliacaoService = new AvaliacaoLocalEventoServiceImpl(memoriaEvento, memoriaAval);
    }

    @And("o evento do contexto de avaliação foi concluído")
    public void eventoConcluido() {
        try {
            eventoService.concluirEvento(evento.getId());
            evento = memoriaEvento.buscarPorId(evento.getId()).orElseThrow();
        } catch (Exception e) {
            excecao = e;
        }
    }

    @When("o gestor registra avaliação {string} com justificativa {string} como usuário {string}")
    public void registraOk(String nivel, String j, String u) {
        excecao = null;
        avaliacaoSalva = null;
        try {
            avaliacaoSalva = avaliacaoService.registrarAvaliacao(
                    evento.getId(),
                    local.getId(),
                    NivelAdequacao.valueOf(nivel),
                    j,
                    u);
        } catch (Exception e) {
            excecao = e;
        }
    }

    @When("o gestor tenta registrar avaliação {string} com justificativa {string} como usuário {string}")
    public void registraFalha(String nivel, String j, String u) {
        excecao = null;
        avaliacaoSalva = null;
        try {
            avaliacaoService.registrarAvaliacao(
                    evento.getId(),
                    local.getId(),
                    NivelAdequacao.valueOf(nivel),
                    j,
                    u);
        } catch (Exception e) {
            excecao = e;
        }
    }

    @When("o gestor tenta avaliar o local {string} em vez do vinculado")
    public void outroLocal(String outro) {
        excecao = null;
        String id = "outro-local".equals(outro) ? outroId : outro;
        try {
            avaliacaoService.registrarAvaliacao(
                    evento.getId(),
                    id,
                    NivelAdequacao.ADEQUADO,
                    "teste",
                    "g");
        } catch (Exception e) {
            excecao = e;
        }
    }

    @And("uma avaliação {string} com justificativa {string} já foi registrada por {string}")
    public void jaAvaliado(String nivel, String j, String u) {
        avaliacaoService.registrarAvaliacao(
                evento.getId(),
                local.getId(),
                NivelAdequacao.valueOf(nivel),
                j,
                u);
    }

    @When("o gestor lista avaliações do local")
    public void lista() {
        excecao = null;
        try {
            listaAval = avaliacaoService.listarAvaliacoesDoLocal(local.getId());
            if (!listaAval.isEmpty()) {
                registroVisto = listaAval.get(0);
            }
        } catch (Exception e) {
            excecao = e;
        }
    }

    @Then("a avaliação fica salva")
    public void salva() {
        assertNull(excecao);
        assertNotNull(avaliacaoSalva);
    }

    @And("o registro traz o usuário {string}")
    public void usuarioId(String u) {
        assertEquals(u, avaliacaoSalva.getRegistradoPorUsuarioId());
    }

    @Then("a avaliação é rejeitada")
    public void rejeit() {
        assertNotNull(excecao);
    }

    @And("a mensagem fala de evento concluir")
    public void msg1() {
        assertTrue(excecao.getMessage().toLowerCase().contains("conclu"));
    }

    @And("a mensagem fala de vinculado")
    public void msg2() {
        assertTrue(excecao.getMessage().toLowerCase().contains("vinculad"));
    }

    @And("a mensagem cita que já existe avaliação")
    public void msg3() {
        assertTrue(excecao.getMessage().toLowerCase().contains("já")
                || excecao.getMessage().toLowerCase().contains("existe"));
    }

    @Then("a lista traz ao menos 1 registro")
    public void lista1() {
        assertNull(excecao);
        assertNotNull(listaAval);
        assertTrue(listaAval.size() >= 1);
    }

    @And("o registro visto tem nível {string}")
    public void nivelVisto(String n) {
        assertNotNull(registroVisto);
        assertEquals(NivelAdequacao.valueOf(n), registroVisto.getNivelAdequacao());
    }
}
