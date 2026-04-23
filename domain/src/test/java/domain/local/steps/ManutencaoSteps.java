package domain.local.steps;

import domain.local.entity.Local;
import domain.local.entity.ManutencaoLocal;
import domain.local.entity.ReservaLocal;
import domain.local.repository.LocalRepository;
import domain.local.repository.ManutencaoRepository;
import domain.local.repository.ReservaLocalRepository;
import domain.local.service.ManutencaoService;
import domain.local.service.ManutencaoServiceImpl;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ManutencaoSteps {

    private LocalRepository localRepository;
    private ManutencaoRepository manutencaoRepository;
    private ReservaLocalRepository reservaLocalRepository;
    private ManutencaoService manutencaoService;
    
    private Local localAtual;
    private ManutencaoLocal manutencaoAtual;
    private Exception excecaoAtual;
    private List<ManutencaoLocal> listaRetornada;
    private List<ManutencaoLocal> bancoManutencao;

    @Given("que existe um local cadastrado")
    public void queExisteUmLocalCadastrado() {
        localAtual = new Local("Local Teste", 100, "Rua X", "Sala", "Wifi", BigDecimal.ZERO);
        
        localRepository = mock(LocalRepository.class);
        manutencaoRepository = mock(ManutencaoRepository.class);
        reservaLocalRepository = mock(ReservaLocalRepository.class);
        
        when(localRepository.buscarPorId(localAtual.getId())).thenReturn(Optional.of(localAtual));
        
        bancoManutencao = new ArrayList<>();
        
        // Mock do repositório para simular salvar
        when(manutencaoRepository.salvar(any(ManutencaoLocal.class))).thenAnswer(invocation -> {
            ManutencaoLocal m = invocation.getArgument(0);
            bancoManutencao.removeIf(existente -> existente.getId().equals(m.getId()));
            bancoManutencao.add(m);
            return m;
        });
        
        when(manutencaoRepository.buscarPorLocalId(anyString())).thenReturn(bancoManutencao);
        
        doAnswer(invocation -> {
            String id = invocation.getArgument(0);
            bancoManutencao.removeIf(m -> m.getId().equals(id));
            return null;
        }).when(manutencaoRepository).remover(anyString());
        
        manutencaoService = new ManutencaoServiceImpl(manutencaoRepository, localRepository, reservaLocalRepository);
        excecaoAtual = null;
    }

    @And("não existem reservas conflitantes para o período de {string} até {string}")
    public void naoExistemReservasConflitantesParaOPeriodo(String inicioStr, String fimStr) {
        LocalDateTime inicio = LocalDateTime.parse(inicioStr);
        LocalDateTime fim = LocalDateTime.parse(fimStr);
        when(reservaLocalRepository.buscarReservasPorLocalEPeriodo(localAtual.getId(), inicio, fim))
            .thenReturn(new ArrayList<>());
    }
    
    @And("não existem reservas conflitantes para o novo período")
    public void naoExistemReservasConflitantesParaONovoPeriodo() {
        when(reservaLocalRepository.buscarReservasPorLocalEPeriodo(eq(localAtual.getId()), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(new ArrayList<>());
    }

    @When("eu cadastro uma manutenção com início {string}, fim {string} e responsável {string}")
    public void euCadastroUmaManutencaoComInicioFimEResponsavel(String inicioStr, String fimStr, String responsavel) {
        LocalDateTime inicio = LocalDateTime.parse(inicioStr);
        LocalDateTime fim = LocalDateTime.parse(fimStr);
        try {
            manutencaoAtual = manutencaoService.cadastrarManutencao(localAtual.getId(), inicio, fim, responsavel);
        } catch (Exception e) {
            excecaoAtual = e;
        }
    }

    @Then("a manutenção deve ser salva com sucesso")
    public void aManutencaoDeveSerSalvaComSucesso() {
        assertNull(excecaoAtual, "Não deveria ocorrer exceção: " + (excecaoAtual != null ? excecaoAtual.getMessage() : ""));
        assertNotNull(manutencaoAtual);
        assertTrue(bancoManutencao.contains(manutencaoAtual));
    }

    @And("deve estar vinculada ao local")
    public void deveEstarVinculadaAoLocal() {
        assertEquals(localAtual.getId(), manutencaoAtual.getLocalId());
    }

    @And("deve registrar o responsável {string} e a data da operação")
    public void deveRegistrarOResponsavelEADataDaOperacao(String responsavel) {
        assertEquals(responsavel, manutencaoAtual.getResponsavel());
        assertNotNull(manutencaoAtual.getCreatedAt());
    }

    @When("eu tento cadastrar uma manutenção com início {string}, fim {string} e responsável {string}")
    public void euTentoCadastrarUmaManutencaoComInicioFimEResponsavel(String inicioStr, String fimStr, String responsavel) {
        euCadastroUmaManutencaoComInicioFimEResponsavel(inicioStr, fimStr, responsavel);
    }

    @Then("deve ocorrer um erro informando que as datas são inválidas")
    public void deveOcorrerUmErroInformandoQueAsDatasSaoInvalidas() {
        assertNotNull(excecaoAtual);
        assertTrue(excecaoAtual instanceof IllegalArgumentException);
        assertTrue(excecaoAtual.getMessage().contains("anterior") || excecaoAtual.getMessage().contains("inválida"));
    }

    @When("eu tento cadastrar uma manutenção sem informar os campos obrigatórios")
    public void euTentoCadastrarUmaManutencaoSemInformarOsCamposObrigatorios() {
        try {
            manutencaoService.cadastrarManutencao(localAtual.getId(), null, null, "");
        } catch (Exception e) {
            excecaoAtual = e;
        }
    }

    @Then("deve ocorrer um erro na manutenção informando que os campos obrigatórios estão ausentes")
    public void deveOcorrerUmErroInformandoQueOsCamposObrigatoriosEstaoAusentes() {
        assertNotNull(excecaoAtual);
        assertTrue(excecaoAtual instanceof IllegalArgumentException);
        assertTrue(excecaoAtual.getMessage().contains("obrigatóri"));
    }

    @And("existe uma reserva aprovada para o local no período de {string} até {string}")
    public void existeUmaReservaAprovadaParaOLocalNoPeriodo(String inicioStr, String fimStr) {
        LocalDateTime inicio = LocalDateTime.parse(inicioStr);
        LocalDateTime fim = LocalDateTime.parse(fimStr);
        
        ReservaLocal reservaMock = new ReservaLocal("agendaId", "eventoId", inicio, fim);
        List<ReservaLocal> reservas = new ArrayList<>();
        reservas.add(reservaMock);
        
        when(reservaLocalRepository.buscarReservasPorLocalEPeriodo(anyString(), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(reservas);
    }

    @Then("deve ocorrer um erro informando conflito com reservas")
    public void deveOcorrerUmErroInformandoConflitoComReservas() {
        assertNotNull(excecaoAtual);
        assertTrue(excecaoAtual instanceof IllegalArgumentException);
        assertTrue(excecaoAtual.getMessage().contains("conflita"));
    }

    @And("eu tenho uma manutenção cadastrada com início {string} e fim {string}")
    public void euTenhoUmaManutencaoCadastrada(String inicioStr, String fimStr) {
        LocalDateTime inicio = LocalDateTime.parse(inicioStr);
        LocalDateTime fim = LocalDateTime.parse(fimStr);
        ManutencaoLocal m = new ManutencaoLocal(localAtual.getId(), inicio, fim, "Gestor Base");
        bancoManutencao.add(m);
        manutencaoAtual = m;
        when(manutencaoRepository.buscarPorId(m.getId())).thenReturn(Optional.of(m));
    }

    @And("eu tenho outra manutenção cadastrada com início {string} e fim {string}")
    public void euTenhoOutraManutencaoCadastrada(String inicioStr, String fimStr) {
        LocalDateTime inicio = LocalDateTime.parse(inicioStr);
        LocalDateTime fim = LocalDateTime.parse(fimStr);
        ManutencaoLocal m = new ManutencaoLocal(localAtual.getId(), inicio, fim, "Gestor Base");
        bancoManutencao.add(m);
    }

    @When("eu edito a manutenção para ter início {string} e fim {string}")
    public void euEditoAManutencao(String inicioStr, String fimStr) {
        LocalDateTime inicio = LocalDateTime.parse(inicioStr);
        LocalDateTime fim = LocalDateTime.parse(fimStr);
        try {
            manutencaoAtual = manutencaoService.editarManutencao(manutencaoAtual.getId(), inicio, fim, manutencaoAtual.getResponsavel());
        } catch (Exception e) {
            excecaoAtual = e;
        }
    }

    @Then("as informações da manutenção devem ser atualizadas com sucesso")
    public void asInformacoesDaManutencaoDevemSerAtualizadasComSucesso() {
        assertNull(excecaoAtual);
        assertNotNull(manutencaoAtual.getUpdatedAt());
    }

    @When("eu tento editar a manutenção para ter início {string} e fim {string}")
    public void euTentoEditarAManutencao(String inicioStr, String fimStr) {
        euEditoAManutencao(inicioStr, fimStr);
    }

    @When("eu listo as manutenções do local")
    public void euListoAsManutencoesDoLocal() {
        listaRetornada = manutencaoService.listarManutencoesPorLocal(localAtual.getId());
    }

    @Then("a lista deve conter as duas manutenções cadastradas")
    public void aListaDeveConterAsDuasManutencoesCadastradas() {
        assertNotNull(listaRetornada);
        assertEquals(2, listaRetornada.size());
    }

    @When("eu removo a manutenção")
    public void euRemovoAManutencao() {
        try {
            manutencaoService.removerManutencao(manutencaoAtual.getId());
        } catch (Exception e) {
            excecaoAtual = e;
        }
    }

    @Then("a manutenção deve ser excluída com sucesso")
    public void aManutencaoDeveSerExcluidaComSucesso() {
        assertNull(excecaoAtual);
        verify(manutencaoRepository, times(1)).remover(manutencaoAtual.getId());
        assertFalse(bancoManutencao.contains(manutencaoAtual));
    }
}
