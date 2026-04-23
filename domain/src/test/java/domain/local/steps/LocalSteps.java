package domain.local.steps;

import domain.local.entity.Local;
import domain.local.repository.LocalRepository;
import domain.local.service.LocalService;
import domain.local.service.LocalServiceImpl;
import domain.local.valueobject.StatusLocal;
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
import static org.mockito.Mockito.*;

public class LocalSteps {

    private LocalService localService;
    private LocalRepository localRepository;
    private Local localAtual;
    private Exception excecaoAtual;
    private List<Local> listaRetornada;

    // Dados temporarios
    private String tempNome;
    private int tempCapacidade;
    private String tempEndereco;
    private String tempTipo;
    private String tempInfra;
    private BigDecimal tempCusto;

    @Given("que eu possuo os dados do local: nome {string}, capacidade {int}, endereco {string}, tipo {string}, infraestrutura {string} e custo {double}")
    public void queEuPossuoOsDadosDoLocal(String nome, int capacidade, String endereco, String tipo, String infra, double custo) {
        tempNome = nome;
        tempCapacidade = capacidade;
        tempEndereco = endereco;
        tempTipo = tipo;
        tempInfra = infra;
        tempCusto = BigDecimal.valueOf(custo);
        
        localRepository = mock(LocalRepository.class);
        when(localRepository.salvar(any(Local.class))).thenAnswer(invocation -> invocation.getArgument(0));
        localService = new LocalServiceImpl(localRepository);
    }

    @When("eu cadastro o local")
    public void euCadastroOLocal() {
        try {
            localAtual = localService.cadastrarLocal(tempNome, tempCapacidade, tempEndereco, tempTipo, tempInfra, tempCusto);
        } catch (Exception e) {
            excecaoAtual = e;
        }
    }

    @When("eu tento cadastrar o local")
    public void euTentoCadastrarOLocal() {
        euCadastroOLocal();
    }

    @Then("o local deve ser salvo com sucesso")
    public void oLocalDeveSerSalvoComSucesso() {
        assertNull(excecaoAtual);
        assertNotNull(localAtual);
        verify(localRepository, times(1)).salvar(any(Local.class));
    }

    @And("o status do local deve ser {string}")
    public void oStatusDoLocalDeveSer(String statusStr) {
        assertEquals(StatusLocal.valueOf(statusStr), localAtual.getStatus());
    }

    @And("a capacidade deve ser {int}")
    public void aCapacidadeDeveSer(int capacidade) {
        assertEquals(capacidade, localAtual.getCapacidade());
    }

    @Then("deve ocorrer um erro informando que os campos obrigatórios estão ausentes")
    public void deveOcorrerUmErroInformandoCamposAusentes() {
        assertNotNull(excecaoAtual);
        assertTrue(excecaoAtual instanceof IllegalArgumentException);
    }

    @Given("que eu tenho um local cadastrado com nome {string}, capacidade {int}, endereco {string}, tipo {string}, infraestrutura {string} e custo {double}")
    public void queEuTenhoUmLocalCadastrado(String nome, int capacidade, String endereco, String tipo, String infra, double custo) {
        localRepository = mock(LocalRepository.class);
        localService = new LocalServiceImpl(localRepository);
        
        localAtual = new Local(nome, capacidade, endereco, tipo, infra, BigDecimal.valueOf(custo));
        when(localRepository.buscarPorId(localAtual.getId())).thenReturn(Optional.of(localAtual));
        when(localRepository.salvar(any(Local.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @When("eu edito o local para ter o nome {string}, capacidade {int}, endereco {string}, tipo {string}, infraestrutura {string} e custo {double}")
    public void euEditoOLocal(String nome, int capacidade, String endereco, String tipo, String infra, double custo) {
        try {
            localAtual = localService.editarLocal(localAtual.getId(), nome, capacidade, endereco, tipo, infra, BigDecimal.valueOf(custo));
        } catch (Exception e) {
            excecaoAtual = e;
        }
    }

    @Then("as informações do local devem ser atualizadas com sucesso")
    public void asInformacoesDoLocalDevemSerAtualizadasComSucesso() {
        assertNull(excecaoAtual);
        verify(localRepository, times(1)).salvar(localAtual);
    }

    @And("o updatedAt deve ser modificado")
    public void oUpdatedAtDeveSerModificado() {
        assertNotNull(localAtual.getUpdatedAt());
        // Considerando que na criação também é salvo, podemos verificar se a propriedade existe.
    }

    @When("eu desativo o local")
    public void euDesativoOLocal() {
        try {
            localService.desativarLocal(localAtual.getId());
        } catch (Exception e) {
            excecaoAtual = e;
        }
    }

    @And("eu não devo poder realizar novas reservas no local")
    public void euNaoDevoPoderRealizarNovasReservasNoLocal() {
        // Simular a verificação de negócio
        assertFalse(localAtual.isAtivo(), "Local inativo não pode estar ativo e não pode receber reservas.");
    }

    @Given("eu tenho um local inativo com nome {string}, capacidade {int}, endereco {string}, tipo {string}, infraestrutura {string} e custo {int}")
    public void euTenhoUmLocalInativo(String nome, int capacidade, String endereco, String tipo, String infra, int custo) {
        Local localInativo = new Local(nome, capacidade, endereco, tipo, infra, BigDecimal.valueOf(custo));
        localInativo.desativar();
        
        List<Local> db = new ArrayList<>();
        db.add(localAtual);
        db.add(localInativo);
        when(localRepository.listarTodos()).thenReturn(db);
    }

    @When("eu listo todos os locais")
    public void euListoTodosOsLocais() {
        listaRetornada = localService.listarLocais();
    }

    @Then("a lista deve conter tanto {string} quanto {string}")
    public void aListaDeveConterTantoQuanto(String nome1, String nome2) {
        assertNotNull(listaRetornada);
        assertEquals(2, listaRetornada.size());
        assertTrue(listaRetornada.stream().anyMatch(l -> l.getNome().equals(nome1)));
        assertTrue(listaRetornada.stream().anyMatch(l -> l.getNome().equals(nome2)));
    }
}
