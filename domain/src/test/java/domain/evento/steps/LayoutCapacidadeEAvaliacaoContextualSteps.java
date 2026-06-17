package domain.evento.steps;

import domain.evento.entity.Evento;
import domain.evento.valueobject.PorteEvento;
import domain.evento.valueobject.TipoEvento;
import domain.local.entity.AvaliacaoContextualLocal;
import domain.local.entity.LayoutLocal;
import domain.local.entity.Local;
import domain.local.service.AvaliacaoContextualLocalService;
import domain.local.service.AvaliacaoContextualLocalServiceImpl;
import domain.local.service.CompatibilidadeLayoutEvento;
import domain.local.service.LayoutLocalService;
import domain.local.service.LayoutLocalServiceImpl;
import domain.local.service.ResumoDesempenhoContextualLocal;
import domain.local.support.InMemoryEventoRepository;
import domain.local.support.InMemoryLocalRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LayoutCapacidadeEAvaliacaoContextualSteps {

    private InMemoryEventoRepository eventoRepository;
    private InMemoryLocalRepository localRepository;
    private LayoutLocalService layoutLocalService;
    private AvaliacaoContextualLocalService avaliacaoService;

    private Evento eventoAtual;
    private Local localAtual;
    private List<CompatibilidadeLayoutEvento> compatibilidades;
    private LayoutLocal layoutAtual;
    private AvaliacaoContextualLocal avaliacaoAtual;
    private ResumoDesempenhoContextualLocal resumoAtual;
    private Exception excecaoAtual;

    @Before
    public void setup() {
        eventoRepository = new InMemoryEventoRepository();
        localRepository = new InMemoryLocalRepository();
        layoutLocalService = new LayoutLocalServiceImpl(localRepository, eventoRepository);
        avaliacaoService = new AvaliacaoContextualLocalServiceImpl(eventoRepository, localRepository, new InmemoryAvaliacaoContextualRepository());
        eventoAtual = null;
        localAtual = null;
        compatibilidades = null;
        layoutAtual = null;
        avaliacaoAtual = null;
        resumoAtual = null;
        excecaoAtual = null;
    }

    @Given("um evento em planejamento com local ativo vinculado")
    public void eventoEmPlanejamentoComLocalAtivo() {
        localAtual = new Local("Centro de Convencoes", 500, "Av 1", "Multiplo", "som,acesso", BigDecimal.valueOf(2000));
        localRepository.salvar(localAtual);
        eventoAtual = new Evento(
                "Summit",
                TipoEvento.CORPORATIVO,
                PorteEvento.MEDIO,
                220,
                "Planejamento anual",
                localAtual.getId());
        eventoRepository.salvar(eventoAtual);
    }

    @When("cadastro layout {string} com capacidade {int} e usuario {string}")
    public void cadastroLayoutComCapacidade(String nome, int capacidade, String usuario) {
        layoutAtual = layoutLocalService.cadastrarLayout(localAtual.getId(), nome, "descricao " + nome, capacidade, usuario);
    }

    @When("tento cadastrar layout {string} com capacidade {int} e usuario {string}")
    public void tentoCadastrarLayoutComCapacidade(String nome, int capacidade, String usuario) {
        try {
            layoutAtual = layoutLocalService.cadastrarLayout(localAtual.getId(), nome, "descricao " + nome, capacidade, usuario);
        } catch (Exception e) {
            excecaoAtual = e;
        }
    }

    @And("associo ao evento o layout {string} com justificativa {string}")
    public void associoLayoutAoEvento(String nomeLayout, String justificativa) {
        String layoutId = layoutLocalService.listarLayouts(localAtual.getId()).stream()
                .filter(l -> l.getNome().equals(nomeLayout))
                .findFirst()
                .orElseThrow()
                .getId();
        layoutLocalService.associarLayoutAoEvento(
                eventoAtual.getId(),
                localAtual.getId(),
                layoutId,
                justificativa,
                "gestor-associacao");
        eventoAtual = eventoRepository.buscarPorId(eventoAtual.getId()).orElseThrow();
    }

    @When("atualizo layout {string} para capacidade {int} por usuario {string}")
    public void atualizoLayoutCapacidade(String nomeLayout, int novaCapacidade, String usuario) {
        String layoutId = layoutLocalService.listarLayouts(localAtual.getId()).stream()
                .filter(l -> l.getNome().equals(nomeLayout))
                .findFirst()
                .orElseThrow()
                .getId();
        layoutLocalService.atualizarLayout(localAtual.getId(), layoutId, nomeLayout, "descricao atualizada", novaCapacidade, usuario);
        eventoAtual = eventoRepository.buscarPorId(eventoAtual.getId()).orElseThrow();
    }

    @When("analiso compatibilidade dos layouts para o evento")
    public void analisoCompatibilidade() {
        compatibilidades = layoutLocalService.analisarCompatibilidadeParaEvento(eventoAtual.getId(), localAtual.getId());
    }

    @When("tento associar ao evento o layout {string} sem justificativa")
    public void tentoAssociarLayoutIncompativelSemJustificativa(String nomeLayout) {
        try {
            String layoutId = layoutLocalService.listarLayouts(localAtual.getId()).stream()
                    .filter(l -> l.getNome().equals(nomeLayout))
                    .findFirst()
                    .orElseThrow()
                    .getId();
            layoutLocalService.associarLayoutAoEvento(
                    eventoAtual.getId(),
                    localAtual.getId(),
                    layoutId,
                    null,
                    "gestor-sem-justificativa");
        } catch (Exception e) {
            excecaoAtual = e;
        }
    }

    @Then("o layout fica cadastrado para o local")
    public void layoutFicaCadastrado() {
        assertNotNull(layoutAtual);
        assertTrue(layoutLocalService.listarLayouts(localAtual.getId()).size() > 0);
    }

    @And("o layout registra usuario {string}")
    public void layoutRegistraUsuario(String usuario) {
        assertNotNull(layoutAtual);
        assertTrue(usuario.equals(layoutAtual.getUsuarioCriacao()) || usuario.equals(layoutAtual.getUsuarioAtualizacao()));
    }

    @Then("ocorre erro de layout duplicado")
    public void ocorreErroLayoutDuplicado() {
        assertNotNull(excecaoAtual);
        assertTrue(excecaoAtual.getMessage().contains("Já existe layout"));
    }

    @Then("o layout {string} fica incompativel")
    public void layoutIncompativel(String nomeLayout) {
        CompatibilidadeLayoutEvento item = compatibilidades.stream()
                .filter(c -> c.getNomeLayout().equals(nomeLayout))
                .findFirst()
                .orElseThrow();
        assertFalse(item.isCompativel());
    }

    @And("o layout {string} fica compativel")
    public void layoutCompativel(String nomeLayout) {
        CompatibilidadeLayoutEvento item = compatibilidades.stream()
                .filter(c -> c.getNomeLayout().equals(nomeLayout))
                .findFirst()
                .orElseThrow();
        assertTrue(item.isCompativel());
    }

    @Then("ocorre erro exigindo justificativa de excecao")
    public void ocorreErroExigindoJustificativa() {
        assertNotNull(excecaoAtual);
        assertTrue(excecaoAtual.getMessage().contains("justificativa"));
    }

    @Then("o evento fica com validacao de layout pendente")
    public void eventoComValidacaoPendente() {
        assertTrue(eventoAtual.isValidacaoLayoutPendente());
    }

    @Given("um evento concluido com local vinculado para avaliacao contextual")
    public void eventoConcluidoParaAvaliacao() {
        localAtual = new Local("Local Avaliado", 300, "Rua A", "Sala", "wifi,limpeza", BigDecimal.valueOf(900));
        localRepository.salvar(localAtual);
        eventoAtual = new Evento(
                "Evento Avaliado",
                TipoEvento.CORPORATIVO,
                PorteEvento.PEQUENO,
                120,
                "Workshop",
                localAtual.getId());
        eventoAtual.concluirEvento();
        eventoRepository.salvar(eventoAtual);
    }

    @Given("um evento em andamento com local vinculado para avaliacao contextual")
    public void eventoEmAndamentoParaAvaliacao() {
        localAtual = new Local("Local Em Andamento", 300, "Rua B", "Sala", "wifi,limpeza", BigDecimal.valueOf(900));
        localRepository.salvar(localAtual);
        eventoAtual = new Evento(
                "Evento Em Andamento",
                TipoEvento.CORPORATIVO,
                PorteEvento.PEQUENO,
                100,
                "Workshop",
                localAtual.getId());
        eventoRepository.salvar(eventoAtual);
    }

    @When("registro avaliacao contextual com notas validas e usuario {string}")
    public void registroAvaliacaoContextual(String usuario) {
        try {
            Map<String, Integer> notas = new LinkedHashMap<>();
            notas.put("estrutura", 4);
            notas.put("acesso", 5);
            notas.put("suporte", 4);
            notas.put("limpeza", 5);
            notas.put("conforto", 4);
            notas.put("capacidade percebida", 4);
            notas.put("adequacao geral", 4);
            avaliacaoAtual = avaliacaoService.registrarAvaliacao(
                    eventoAtual.getId(),
                    localAtual.getId(),
                    notas,
                    "Local performou bem no contexto",
                    usuario);
        } catch (Exception e) {
            excecaoAtual = e;
        }
    }

    @And("existe outro evento concluido do mesmo contexto avaliado com nota base {int}")
    public void existeOutroEventoMesmoContexto(int notaBase) {
        Evento outro = new Evento(
                "Evento Contexto " + notaBase,
                eventoAtual.getTipo(),
                eventoAtual.getPorte(),
                90,
                "Contexto",
                localAtual.getId());
        outro.concluirEvento();
        eventoRepository.salvar(outro);
        Map<String, Integer> notas = new LinkedHashMap<>();
        notas.put("estrutura", notaBase);
        notas.put("acesso", notaBase);
        notas.put("suporte", notaBase);
        notas.put("limpeza", notaBase);
        notas.put("conforto", notaBase);
        notas.put("capacidade percebida", notaBase);
        notas.put("adequacao geral", notaBase);
        avaliacaoService.registrarAvaliacao(
                outro.getId(),
                localAtual.getId(),
                notas,
                "Historico adicional",
                "gestor-historico");
    }

    @When("consulto o resumo contextual do local")
    public void consultoResumoContextual() {
        resumoAtual = avaliacaoService.consultarResumo(localAtual.getId(), eventoAtual.getTipo(), eventoAtual.getPorte());
    }

    @Then("a avaliacao contextual fica registrada")
    public void avaliacaoContextualRegistrada() {
        assertNotNull(avaliacaoAtual);
        assertTrue(avaliacaoAtual.getNotaFinal() > 0);
    }

    @And("a avaliacao registra usuario {string}")
    public void avaliacaoRegistraUsuario(String usuario) {
        assertNotNull(avaliacaoAtual);
        assertTrue(usuario.equals(avaliacaoAtual.getUsuarioResponsavel()));
    }

    @Then("ocorre erro de evento nao concluido para avaliacao")
    public void erroEventoNaoConcluido() {
        assertNotNull(excecaoAtual);
        assertTrue(excecaoAtual.getMessage().contains("conclusao") || excecaoAtual.getMessage().contains("conclu"));
    }

    @Then("ocorre erro de avaliacao duplicada do evento")
    public void erroAvaliacaoDuplicada() {
        assertNotNull(excecaoAtual);
        assertTrue(excecaoAtual.getMessage().contains("Já existe avaliação principal"));
    }

    @Then("a media contextual do local e maior que zero")
    public void mediaContextualMaiorQueZero() {
        assertNotNull(resumoAtual);
        assertTrue(resumoAtual.getNotaMediaContexto() > 0.0);
    }

    @And("nao ha baixa base historica no contexto")
    public void naoHaBaixaBaseHistorica() {
        assertNotNull(resumoAtual);
        assertFalse(resumoAtual.isBaixaBaseHistoricaContexto());
    }
}
