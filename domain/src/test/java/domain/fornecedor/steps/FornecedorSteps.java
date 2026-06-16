package domain.fornecedor.steps;

import domain.contrato.entity.Contrato;
import domain.contrato.repository.ContratoRepository;
import domain.contrato.service.ContratoService;
import domain.contrato.service.ContratoServiceImpl;
import domain.contrato.valueobject.DadosParteContrato;
import domain.contrato.valueobject.StatusContrato;
import domain.contrato.valueobject.TipoContrato;
import domain.evento.entity.Evento;
import domain.evento.repository.EventoRepository;
import domain.financeiro.entity.Despesa;
import domain.financeiro.repository.DespesaRepository;
import domain.fornecedor.entity.Fornecedor;
import domain.fornecedor.repository.FornecedorRepository;
import domain.fornecedor.service.FornecedorService;
import domain.fornecedor.service.FornecedorServiceImpl;
import domain.fornecedor.valueobject.StatusFornecedor;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class FornecedorSteps {

    private static final String ID_EVENTO = "evento-contrato-forn-1";
    private static final String OBJETO_PADRAO = "Fornecimento de equipamentos para o evento";

    private FornecedorRepository fornecedorRepository;
    private ContratoRepository contratoRepository;
    private DespesaRepository despesaRepository;
    private EventoRepository eventoRepository;
    private FornecedorService fornecedorService;
    private ContratoService contratoService;

    private final Map<String, Fornecedor> fornecedoresDb = new HashMap<>();

    private String tempNome;
    private String tempCnpj;
    private String tempCategoria;
    private String tempContato;

    private Fornecedor fornecedorAtual;
    private Fornecedor fornecedorRetornado;
    private List<Fornecedor> listaFornecedores;
    private Exception excecaoAtual;

    private void inicializarRepositorios() {
        fornecedorRepository = mock(FornecedorRepository.class);
        contratoRepository = mock(ContratoRepository.class);
        despesaRepository = mock(DespesaRepository.class);
        eventoRepository = mock(EventoRepository.class);

        when(fornecedorRepository.salvar(any(Fornecedor.class))).thenAnswer(inv -> {
            Fornecedor f = inv.getArgument(0);
            fornecedoresDb.put(f.getId(), f);
            return f;
        });
        when(fornecedorRepository.buscarPorId(any())).thenAnswer(inv ->
                Optional.ofNullable(fornecedoresDb.get(inv.getArgument(0))));
        when(fornecedorRepository.buscarPorCnpj(any())).thenAnswer(inv -> {
            String cnpj = inv.getArgument(0);
            return fornecedoresDb.values().stream()
                    .filter(f -> f.getCnpj().equals(cnpj))
                    .findFirst();
        });
        when(fornecedorRepository.listarTodos()).thenAnswer(inv -> new ArrayList<>(fornecedoresDb.values()));
        when(contratoRepository.listarPorFornecedorId(any())).thenReturn(List.of());
        when(despesaRepository.listarPorFornecedorId(any())).thenReturn(List.of());

        fornecedorService = new FornecedorServiceImpl(
                fornecedorRepository, contratoRepository, despesaRepository, eventoRepository);
        contratoService = new ContratoServiceImpl(contratoRepository, eventoRepository, fornecedorRepository);
    }

    @Given("que eu possuo os dados do fornecedor: nome {string}, cnpj {string}, categoria {string} e contato {string}")
    public void queEuPossuoOsDadosDoFornecedor(String nome, String cnpj, String categoria, String contato) {
        inicializarRepositorios();
        tempNome = nome;
        tempCnpj = cnpj;
        tempCategoria = categoria;
        tempContato = contato;
        excecaoAtual = null;
    }

    @Given("que existe um fornecedor cadastrado com cnpj {string}")
    public void queExisteUmFornecedorCadastradoComCnpj(String cnpj) {
        inicializarRepositorios();
        Fornecedor existente = new Fornecedor("Fornecedor Existente", cnpj, "Categoria", "contato@existente.com");
        fornecedoresDb.put(existente.getId(), existente);
    }

    @Given("que eu tenho um fornecedor ativo cadastrado com nome {string}, cnpj {string}, categoria {string} e contato {string}")
    public void queEuTenhoUmFornecedorAtivoCadastrado(String nome, String cnpj, String categoria, String contato) {
        inicializarRepositorios();
        fornecedorAtual = new Fornecedor(nome, cnpj, categoria, contato);
        fornecedoresDb.put(fornecedorAtual.getId(), fornecedorAtual);
    }

    @Given("que eu tenho um fornecedor inativo cadastrado com nome {string}, cnpj {string}, categoria {string} e contato {string}")
    public void queEuTenhoUmFornecedorInativoCadastrado(String nome, String cnpj, String categoria, String contato) {
        queEuTenhoUmFornecedorAtivoCadastrado(nome, cnpj, categoria, contato);
        fornecedorAtual.desativar();
    }

    @Given("que não existe fornecedor com o id informado")
    public void queNaoExisteFornecedorComOIdInformado() {
        inicializarRepositorios();
    }

    @And("o fornecedor possui contrato ativo vinculado")
    public void oFornecedorPossuiContratoAtivoVinculado() {
        Contrato contratoAtivo = Mockito.mock(Contrato.class);
        when(contratoAtivo.getStatus()).thenReturn(StatusContrato.ASSINADO);
        when(contratoRepository.listarPorFornecedorId(eq(fornecedorAtual.getId()))).thenReturn(List.of(contratoAtivo));
    }

    @And("o fornecedor possui despesa em evento em andamento")
    public void oFornecedorPossuiDespesaEmEventoEmAndamento() {
        Despesa despesa = Mockito.mock(Despesa.class);
        when(despesa.getEventoId()).thenReturn(ID_EVENTO);
        when(despesaRepository.listarPorFornecedorId(fornecedorAtual.getId())).thenReturn(List.of(despesa));

        Evento evento = Mockito.mock(Evento.class);
        when(evento.isConcluido()).thenReturn(false);
        when(eventoRepository.buscarPorId(ID_EVENTO)).thenReturn(Optional.of(evento));
    }

    @And("existe um evento válido para contrato com fornecedor inativo")
    public void existeUmEventoValidoParaContratoComFornecedorInativo() {
        when(eventoRepository.buscarPorId(ID_EVENTO)).thenReturn(Optional.of(Mockito.mock(Evento.class)));
    }

    @When("eu cadastro o fornecedor")
    public void euCadastroOFornecedor() {
        try {
            fornecedorAtual = fornecedorService.cadastrarFornecedor(tempNome, tempCnpj, tempCategoria, tempContato);
        } catch (Exception e) {
            excecaoAtual = e;
        }
    }

    @When("eu tento cadastrar o fornecedor")
    public void euTentoCadastrarOFornecedor() {
        euCadastroOFornecedor();
    }

    @When("eu edito o fornecedor para ter o nome {string}, cnpj {string}, categoria {string} e contato {string}")
    public void euEditoOFornecedor(String nome, String cnpj, String categoria, String contato) {
        try {
            fornecedorAtual = fornecedorService.editarFornecedor(
                    fornecedorAtual.getId(), nome, cnpj, categoria, contato);
        } catch (Exception e) {
            excecaoAtual = e;
        }
    }

    @When("eu tento editar o fornecedor para ter o nome {string}, cnpj {string}, categoria {string} e contato {string}")
    public void euTentoEditarOFornecedor(String nome, String cnpj, String categoria, String contato) {
        euEditoOFornecedor(nome, cnpj, categoria, contato);
    }

    @When("eu busco o fornecedor pelo id")
    public void euBuscoOFornecedorPeloId() {
        try {
            fornecedorRetornado = fornecedorService.buscarFornecedor(fornecedorAtual.getId());
        } catch (Exception e) {
            excecaoAtual = e;
        }
    }

    @And("eu listo todos os fornecedores")
    public void euListoTodosOsFornecedores() {
        listaFornecedores = fornecedorService.listarFornecedores();
    }

    @When("eu tento buscar fornecedor por id inexistente")
    public void euTentoBuscarFornecedorPorIdInexistente() {
        try {
            fornecedorService.buscarFornecedor("id-inexistente");
        } catch (Exception e) {
            excecaoAtual = e;
        }
    }

    @When("eu desativo o fornecedor")
    public void euDesativoOFornecedor() {
        try {
            fornecedorService.desativarFornecedor(fornecedorAtual.getId());
            fornecedorAtual = fornecedoresDb.get(fornecedorAtual.getId());
        } catch (Exception e) {
            excecaoAtual = e;
        }
    }

    @When("eu tento desativar o fornecedor")
    public void euTentoDesativarOFornecedor() {
        euDesativoOFornecedor();
    }

    @When("eu tento cadastrar um contrato completo para esse evento")
    public void euTentoCadastrarUmContratoCompletoParaEsseEvento() {
        try {
            LocalDateTime inicio = LocalDateTime.now();
            LocalDateTime fim = inicio.plusDays(30);
            List<DadosParteContrato> partes = List.of(
                    new DadosParteContrato("Organização Evento", "Contratante"),
                    new DadosParteContrato("Fornecedor XYZ", "Fornecedor"));
            Contrato contrato = new Contrato(
                    ID_EVENTO, fornecedorAtual.getId(), TipoContrato.FORNECEDOR,
                    OBJETO_PADRAO, new BigDecimal("10000.00"), inicio, fim, partes);
            contratoService.criarContrato(contrato);
        } catch (Exception e) {
            excecaoAtual = e;
        }
    }

    @Then("o fornecedor deve ser salvo com sucesso")
    public void oFornecedorDeveSerSalvoComSucesso() {
        assertNull(excecaoAtual);
        assertNotNull(fornecedorAtual);
        verify(fornecedorRepository, times(1)).salvar(any(Fornecedor.class));
    }

    @And("o status do fornecedor deve ser {string}")
    public void oStatusDoFornecedorDeveSer(String statusStr) {
        assertEquals(StatusFornecedor.valueOf(statusStr), fornecedorAtual.getStatus());
    }

    @Then("deve ocorrer um erro ao cadastrar o fornecedor")
    @Then("deve ocorrer um erro ao editar o fornecedor")
    @Then("deve ocorrer um erro ao buscar o fornecedor")
    @Then("deve ocorrer um erro ao desativar o fornecedor")
    public void deveOcorrerUmErro() {
        assertNotNull(excecaoAtual);
    }

    @Then("as informações do fornecedor devem ser atualizadas com sucesso")
    public void asInformacoesDoFornecedorDevemSerAtualizadasComSucesso() {
        assertNull(excecaoAtual);
        verify(fornecedorRepository, atLeastOnce()).salvar(fornecedorAtual);
    }

    @Then("o fornecedor retornado contém o nome {string}")
    public void oFornecedorRetornadoContemONome(String nome) {
        assertNull(excecaoAtual);
        assertNotNull(fornecedorRetornado);
        assertEquals(nome, fornecedorRetornado.getNome());
    }

    @And("a lista de fornecedores contém ao menos um item")
    public void aListaDeFornecedoresContemAoMenosUmItem() {
        assertNotNull(listaFornecedores);
        assertFalse(listaFornecedores.isEmpty());
    }

    @Then("o sistema deve impedir o cadastro do contrato")
    public void oSistemaDeveImpedirOCadastroDoContrato() {
        assertNotNull(excecaoAtual);
    }
}
