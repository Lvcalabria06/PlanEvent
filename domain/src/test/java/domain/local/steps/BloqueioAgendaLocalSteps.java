package domain.local.steps;

import domain.local.entity.Local;
import domain.local.entity.ReservaLocal;
import domain.local.repository.LocalRepository;
import domain.local.service.IndisponibilidadeLocalService;
import domain.local.service.IndisponibilidadeLocalServiceImpl;
import domain.local.service.ReservaLocalService;
import domain.local.service.ReservaLocalServiceImpl;
import domain.local.support.InMemoryAgendaLocalRepository;
import domain.local.support.InMemoryIndisponibilidadeLocalRepository;
import domain.local.support.InMemoryReservaLocalRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BloqueioAgendaLocalSteps {

    private InMemoryAgendaLocalRepository memoriaAgenda;
    private InMemoryReservaLocalRepository memoriaReservas;
    private InMemoryIndisponibilidadeLocalRepository memoriaIndis;
    private Local localAtivo;
    private LocalRepository localRepository;
    private ReservaLocalService reservaLocalService;
    private IndisponibilidadeLocalService indisponibilidadeLocalService;
    private ReservaLocal reservaCriada;
    private Exception excecao;
    @Given("a preparação de bloqueio de agenda com um local ativo")
    public void preparacaoBloqueio() {
        memoriaAgenda = new InMemoryAgendaLocalRepository();
        memoriaReservas = new InMemoryReservaLocalRepository(memoriaAgenda);
        memoriaIndis = new InMemoryIndisponibilidadeLocalRepository();
        localAtivo = new Local("Espaço Teste", 100, "Rua A", "Sala", "Projetor", BigDecimal.ONE);
        localRepository = mock(LocalRepository.class);
        when(localRepository.buscarPorId(localAtivo.getId())).thenReturn(java.util.Optional.of(localAtivo));
        reservaLocalService = new ReservaLocalServiceImpl(
                memoriaReservas,
                memoriaIndis,
                memoriaAgenda,
                localRepository);
        indisponibilidadeLocalService = new IndisponibilidadeLocalServiceImpl(memoriaIndis, localRepository);
        excecao = null;
        reservaCriada = null;
    }

    @And("existe indisponibilidade no local de {string} a {string} com motivo {string}")
    public void indisponibilidade(String sInicio, String sFim, String motivo) {
        indisponibilidadeLocalService.registrarIndisponibilidade(
                localAtivo.getId(),
                LocalDateTime.parse(sInicio),
                LocalDateTime.parse(sFim),
                motivo);
    }

    @And("já existe reserva no local de {string} a {string} para o evento {string}")
    public void reservaExistente(String sInicio, String sFim, String idEvento) {
        reservaLocalService.reservar(
                localAtivo.getId(),
                idEvento,
                LocalDateTime.parse(sInicio),
                LocalDateTime.parse(sFim));
    }

    @And("eu registro reserva com sucesso de {string} a {string} para o evento {string}")
    public void reservaComSucesso(String sInicio, String sFim, String idEvento) {
        reservaCriada = reservaLocalService.reservar(
                localAtivo.getId(),
                idEvento,
                LocalDateTime.parse(sInicio),
                LocalDateTime.parse(sFim));
    }

    @When("o gestor bloqueia o local de {string} a {string} com motivo {string}")
    public void gestorBloqueia(String sInicio, String sFim, String motivo) {
        indisponibilidadeLocalService.registrarIndisponibilidade(
                localAtivo.getId(),
                LocalDateTime.parse(sInicio),
                LocalDateTime.parse(sFim),
                motivo);
    }

    @And("tenta reservar o local de {string} a {string} para o evento {string}")
    public void reservaNovo(String sInicio, String sFim, String idEvento) {
        tentoReservar(sInicio, sFim, idEvento);
    }

    @When("eu tento reservar o local de {string} a {string} para o evento {string}")
    public void tentoReservar(String sInicio, String sFim, String idEvento) {
        excecao = null;
        reservaCriada = null;
        try {
            reservaCriada = reservaLocalService.reservar(
                    localAtivo.getId(),
                    idEvento,
                    LocalDateTime.parse(sInicio),
                    LocalDateTime.parse(sFim));
        } catch (Exception e) {
            excecao = e;
        }
    }

    @Then("a reserva é aceita")
    public void reservaAceita() {
        assertNull(excecao);
        assertNotNull(reservaCriada);
    }

    @Then("a reserva é rejeitada")
    public void reservaRejeitada() {
        assertNotNull(excecao);
        assertNull(reservaCriada);
    }

    @And("a mensagem explica bloqueio por indisponibilidade")
    public void msgIndis() {
        assertNotNull(excecao);
        assertTrue(excecao.getMessage().contains("indisponível")
                || excecao.getMessage().contains("Motivo:"));
    }

    @And("a mensagem explica conflito com reserva")
    public void msgConflito() {
        assertNotNull(excecao);
        assertTrue(excecao.getMessage().toLowerCase().contains("conflit"));
    }
}
