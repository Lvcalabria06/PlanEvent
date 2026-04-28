package domain.evento.steps;

import domain.evento.entity.Evento;
import domain.evento.planejamento.AlertaRiscoAlocacao;
import domain.evento.planejamento.CandidatoAnaliseLocal;
import domain.evento.planejamento.ResultadoAnaliseAlocacao;
import domain.evento.service.PlanejamentoAlocacaoLocalService;
import domain.evento.service.PlanejamentoAlocacaoLocalServiceImpl;
import domain.evento.valueobject.ClassificacaoAlocacaoLocal;
import domain.evento.valueobject.PorteEvento;
import domain.evento.valueobject.TipoEvento;
import domain.local.entity.IndisponibilidadeLocal;
import domain.local.entity.Local;
import domain.local.support.InMemoryAgendaLocalRepository;
import domain.local.support.InMemoryEventoRepository;
import domain.local.support.InMemoryIndisponibilidadeLocalRepository;
import domain.local.support.InMemoryLocalRepository;
import domain.local.support.InMemoryManutencaoRepository;
import domain.local.support.InMemoryReservaLocalRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PlanejamentoAlocacaoLocalSteps {

    private InMemoryEventoRepository eventoRepository;
    private InMemoryLocalRepository localRepository;
    private InMemoryAgendaLocalRepository agendaRepository;
    private InMemoryReservaLocalRepository reservaRepository;
    private InMemoryIndisponibilidadeLocalRepository indisponibilidadeRepository;
    private InMemoryManutencaoRepository manutencaoRepository;

    private PlanejamentoAlocacaoLocalService planejamentoService;

    private Evento evento;
    private Local localInativo;
    private Local localSubstituto;

    private ResultadoAnaliseAlocacao resultado;
    private AlertaRiscoAlocacao alerta;
    private Exception excecao;

    private static final LocalDateTime JI = LocalDateTime.parse("2026-06-01T09:00");
    private static final LocalDateTime JF = LocalDateTime.parse("2026-06-01T18:00");

    @Before
    public void preparacaoGlobal() {
        eventoRepository = new InMemoryEventoRepository();
        localRepository = new InMemoryLocalRepository();
        agendaRepository = new InMemoryAgendaLocalRepository();
        reservaRepository = new InMemoryReservaLocalRepository(agendaRepository);
        indisponibilidadeRepository = new InMemoryIndisponibilidadeLocalRepository();
        manutencaoRepository = new InMemoryManutencaoRepository();
        planejamentoService = new PlanejamentoAlocacaoLocalServiceImpl(
                eventoRepository,
                localRepository,
                reservaRepository,
                indisponibilidadeRepository,
                manutencaoRepository
        );
        excecao = null;
        resultado = null;
        alerta = null;
        evento = null;
    }

    @Given("planning allocation setup with a registered event")
    public void preparacaoBasica() {
        Local local = new Local("Sala Alfa", 400, "Rua 1", "Auditório", "som, projetor", BigDecimal.valueOf(1500));
        localRepository.salvar(local);
        evento = new Evento(
                "Forum",
                TipoEvento.CORPORATIVO,
                PorteEvento.MEDIO,
                200,
                "Networking",
                null);
        eventoRepository.salvar(evento);
    }

    @Given("planning setup with two venues one inactive")
    public void doisLocaisUmInativo() {
        Local localAtivo = new Local("Ativo", 300, "R1", "Tipo", "wifi", BigDecimal.valueOf(500));
        localInativo = new Local("Inativo", 300, "R2", "Tipo", "wifi", BigDecimal.valueOf(600));
        localInativo.desativar();
        localRepository.salvar(localAtivo);
        localRepository.salvar(localInativo);
        evento = new Evento(
                "Evt",
                TipoEvento.CORPORATIVO,
                PorteEvento.MEDIO,
                100,
                "Obj",
                null);
        eventoRepository.salvar(evento);
    }

    @Given("planning with event window and two compatible venues")
    public void preparacaoJanelaDoisLocais() {
        Local a = new Local("Principal", 500, "R1", "Tipo", "som", BigDecimal.valueOf(2000));
        Local b = new Local("Backup", 500, "R2", "Tipo", "som", BigDecimal.valueOf(2200));
        localRepository.salvar(a);
        localRepository.salvar(b);
        evento = new Evento(
                "Evt Janela",
                TipoEvento.CORPORATIVO,
                PorteEvento.MEDIO,
                150,
                "Obj",
                null);
        eventoRepository.salvar(evento);
        planejamentoService.registrarParametrosPlanejamento(
                evento.getId(),
                BigDecimal.valueOf(5000),
                JI,
                JF);
        evento = eventoRepository.buscarPorId(evento.getId()).orElseThrow();
    }

    @And("unavailability on the principal venue during the window")
    public void indisponibilidadeNoPrincipal() {
        Local principal = localRepository.listarTodos().stream()
                .filter(l -> "Principal".equals(l.getNome()))
                .findFirst()
                .orElseThrow();
        IndisponibilidadeLocal ind = new IndisponibilidadeLocal(
                principal.getId(),
                JI.plusHours(1),
                JF.minusHours(1),
                "Reforma");
        indisponibilidadeRepository.salvar(ind);
    }

    @Given("an unconfirmed event with a sufficient large venue")
    public void eventoSemConfirmacaoComLocal() {
        Local grande = new Local("Grande", 500, "R1", "Tipo", "som", BigDecimal.valueOf(2500));
        localRepository.salvar(grande);
        evento = new Evento(
                "Evt Cap",
                TipoEvento.CORPORATIVO,
                PorteEvento.MEDIO,
                100,
                "Obj",
                null);
        eventoRepository.salvar(evento);
    }

    @Given("a confirmed event with principal and a viable substitute venue")
    public void eventoConfirmadoComSubstituto() {
        Local principal = new Local("Principal", 300, "R1", "Tipo", "som", BigDecimal.valueOf(1000));
        localSubstituto = new Local("Substituto", 300, "R2", "Tipo", "som", BigDecimal.valueOf(1100));
        localRepository.salvar(principal);
        localRepository.salvar(localSubstituto);
        evento = new Evento(
                "Evt Conf",
                TipoEvento.CORPORATIVO,
                PorteEvento.MEDIO,
                100,
                "Obj",
                null);
        eventoRepository.salvar(evento);
        planejamentoService.registrarParametrosPlanejamento(evento.getId(), BigDecimal.valueOf(5000), null, null);
        planejamentoService.fixarLocalPrincipal(evento.getId(), principal.getId(), BigDecimal.valueOf(5000));
        evento = eventoRepository.buscarPorId(evento.getId()).orElseThrow();
        evento.confirmarPlanejamento();
        eventoRepository.salvar(evento);
    }

    @Given("a confirmed event with a principal venue")
    public void eventoConfirmadoSoPrincipal() {
        Local principal = new Local("Unico", 300, "R1", "Tipo", "som", BigDecimal.valueOf(800));
        localSubstituto = new Local("Outro", 300, "R2", "Tipo", "som", BigDecimal.valueOf(900));
        localRepository.salvar(principal);
        localRepository.salvar(localSubstituto);
        evento = new Evento(
                "Evt Block",
                TipoEvento.CORPORATIVO,
                PorteEvento.MEDIO,
                100,
                "Obj",
                null);
        eventoRepository.salvar(evento);
        planejamentoService.registrarParametrosPlanejamento(evento.getId(), BigDecimal.valueOf(5000), null, null);
        planejamentoService.fixarLocalPrincipal(evento.getId(), principal.getId(), BigDecimal.valueOf(5000));
        evento = eventoRepository.buscarPorId(evento.getId()).orElseThrow();
        evento.confirmarPlanejamento();
        eventoRepository.salvar(evento);
    }

    @When("I attempt to analyse venues with ceiling -50")
    public void tentoAnalisarTetoNegativo() {
        try {
            resultado = planejamentoService.analisarLocaisParaEvento(evento.getId(), BigDecimal.valueOf(-50));
        } catch (Exception e) {
            excecao = e;
        }
    }

    @When("I analyse venues with ceiling {int}")
    public void analisoLocais(int teto) {
        resultado = planejamentoService.analisarLocaisParaEvento(evento.getId(), BigDecimal.valueOf(teto));
    }

    @When("I register ceiling {int} and fix principal from analysis")
    public void registroEFixoPrincipal(int teto) {
        Local grande = localRepository.listarTodos().get(0);
        planejamentoService.registrarParametrosPlanejamento(
                evento.getId(),
                BigDecimal.valueOf(teto),
                null,
                null);
        evento = planejamentoService.fixarLocalPrincipal(evento.getId(), grande.getId(), BigDecimal.valueOf(teto));
    }

    @And("I raise estimated participants above principal capacity")
    public void aumentoParticipantesDemais() {
        evento = eventoRepository.buscarPorId(evento.getId()).orElseThrow();
        evento.atualizarDados(
                evento.getNome(),
                evento.getTipo(),
                evento.getPorte(),
                600,
                evento.getObjetivo());
        eventoRepository.salvar(evento);
    }

    @And("I evaluate principal risk")
    public void avalioRisco() {
        alerta = planejamentoService.avaliarRiscoAlocacaoPrincipal(evento.getId()).orElse(null);
    }

    @When("I execute documented swap to substitute by user {word} with reason {string}")
    public void trocaDocumentada(String usuario, String motivo) {
        evento = planejamentoService.executarTrocaPrincipalPorContingencia(
                evento.getId(),
                localSubstituto.getId(),
                usuario,
                motivo);
    }

    @When("I attempt to freely fix another principal venue")
    public void tentoFixarOutroPrincipal() {
        try {
            planejamentoService.fixarLocalPrincipal(evento.getId(), localSubstituto.getId(), BigDecimal.valueOf(5000));
        } catch (Exception e) {
            excecao = e;
        }
    }

    @Then("a planning invalid ceiling error occurs")
    public void erroTeto() {
        assertNotNull(excecao);
        assertTrue(excecao.getMessage().contains("Teto"));
    }

    @Then("the inactive venue candidate has classification INADEQUADO")
    public void candidatoInativoInadequado() {
        Optional<CandidatoAnaliseLocal> c = resultado.getCandidatos().stream()
                .filter(x -> x.getLocalId().equals(localInativo.getId()))
                .findFirst();
        assertTrue(c.isPresent());
        assertEquals(ClassificacaoAlocacaoLocal.INADEQUADO, c.get().getClassificacao());
    }

    @Then("the principal venue is INDISPONIVEL in the analysis")
    public void principalIndisponivel() {
        Local principal = localRepository.listarTodos().stream()
                .filter(l -> "Principal".equals(l.getNome()))
                .findFirst()
                .orElseThrow();
        Optional<CandidatoAnaliseLocal> c = resultado.getCandidatos().stream()
                .filter(x -> x.getLocalId().equals(principal.getId()))
                .findFirst();
        assertTrue(c.isPresent());
        assertEquals(ClassificacaoAlocacaoLocal.INDISPONIVEL, c.get().getClassificacao());
    }

    @Then("a risk alert exists for the principal")
    public void existeAlerta() {
        assertNotNull(alerta);
        assertTrue(alerta.getDescricao().length() > 5);
    }

    @Then("history contains one swap with user {word} and reason {string}")
    public void historicoTroca(String usuario, String motivo) {
        List<domain.evento.planejamento.TrocaLocalPlanejamento> h = evento.getHistoricoTrocasLocal();
        assertEquals(1, h.size());
        assertEquals(usuario, h.get(0).getUsuarioId());
        assertEquals(motivo, h.get(0).getMotivo());
        assertNotNull(h.get(0).getDataHora());
    }

    @Then("a planning already confirmed error occurs")
    public void erroConfirmado() {
        assertNotNull(excecao);
        assertTrue(excecao instanceof IllegalStateException);
    }
}
