package domain.contrato.steps;

import domain.contrato.entity.Contrato;
import domain.contrato.repository.ContratoRepository;
import domain.contrato.service.ContratoService;
import domain.contrato.service.ContratoServiceImpl;
import domain.contrato.valueobject.DadosParteContrato;
import domain.contrato.valueobject.StatusContrato;
import domain.contrato.valueobject.TipoContrato;
import domain.evento.entity.Evento;
import domain.evento.repository.EventoRepository;

import io.cucumber.java.Before;
import io.cucumber.java.en.But;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

public class ContratoSteps {

    private static final String ID_EVENTO = "evento-contrato-1";
    private static final String OBJETO_PADRAO = "Fornecimento de equipamentos para o evento";

    private ContratoRepository contratoRepository;
    private EventoRepository eventoRepository;
    private ContratoService contratoService;

    private Exception excecaoLancada;
    private Contrato contratoEmContexto;
    private Contrato contratoRetornadoBusca;
    private List<Contrato> listaContratosRetornada;

    @Before
    public void setup() {
        contratoRepository = Mockito.mock(ContratoRepository.class);
        eventoRepository = Mockito.mock(EventoRepository.class);
        contratoService = new ContratoServiceImpl(contratoRepository, eventoRepository);

        excecaoLancada = null;
        contratoEmContexto = null;
        contratoRetornadoBusca = null;
        listaContratosRetornada = null;

        when(contratoRepository.salvar(any(Contrato.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Given("existe um evento válido para contrato")
    public void existe_um_evento_valido_para_contrato() {
        when(eventoRepository.buscarPorId(ID_EVENTO)).thenReturn(Optional.of(Mockito.mock(Evento.class)));
    }

    @Given("não existe evento válido para contrato")
    public void nao_existe_evento_valido_para_contrato() {
        when(eventoRepository.buscarPorId(any())).thenReturn(Optional.empty());
    }

    @Given("existe um contrato cadastrado nesse evento")
    public void existe_um_contrato_cadastrado_nesse_evento() {
        contratoEmContexto = novoContratoValido(ID_EVENTO);
        when(contratoRepository.buscarPorId(eq(contratoEmContexto.getId())))
                .thenReturn(Optional.of(contratoEmContexto));
    }

    @Given("existe um contrato encerrado nesse evento")
    public void existe_um_contrato_encerrado_nesse_evento() {
        existe_um_contrato_cadastrado_nesse_evento();
        contratoEmContexto.encerrar();
    }

    @Given("não existe contrato com o id informado")
    public void nao_existe_contrato_com_o_id_informado() {
        when(contratoRepository.buscarPorId(any())).thenReturn(Optional.empty());
    }

    @But("o contrato é considerado incompleto para encerramento")
    public void o_contrato_e_considerado_incompleto_para_encerramento() {
        Contrato spy = Mockito.spy(contratoEmContexto);
        doReturn(false).when(spy).estaCompleto();
        when(contratoRepository.buscarPorId(eq(contratoEmContexto.getId()))).thenReturn(Optional.of(spy));
        contratoEmContexto = spy;
    }

    @When("eu cadastrar um contrato completo para esse evento")
    public void eu_cadastrar_um_contrato_completo_para_esse_evento() {
        tentarCadastrar(novoContratoValido(ID_EVENTO));
    }

    @When("eu tentar cadastrar um contrato completo")
    public void eu_tentar_cadastrar_um_contrato_completo() {
        eu_cadastrar_um_contrato_completo_para_esse_evento();
    }

    @When("eu tentar cadastrar contrato sem objeto")
    public void eu_tentar_cadastrar_contrato_sem_objeto() {
        tentarConstruirContratoInvalido(null, new BigDecimal("1.00"));
    }

    @When("eu tentar cadastrar contrato sem valor")
    public void eu_tentar_cadastrar_contrato_sem_valor() {
        tentarConstruirContratoInvalido(OBJETO_PADRAO, null);
    }

    @When("eu tentar cadastrar contrato com data de término não posterior à de início")
    public void eu_tentar_cadastrar_contrato_com_vigencia_invalida() {
        LocalDateTime inicio = LocalDateTime.now();
        LocalDateTime fim = inicio;
        tentarConstruirContratoComDatas(inicio, fim);
    }

    @When("eu tentar cadastrar contrato com apenas uma parte")
    public void eu_tentar_cadastrar_contrato_com_apenas_uma_parte() {
        LocalDateTime inicio = LocalDateTime.now();
        LocalDateTime fim = inicio.plusDays(1);
        List<DadosParteContrato> uma = List.of(new DadosParteContrato("Única Parte", "Tipo"));
        try {
            new Contrato(ID_EVENTO, TipoContrato.OUTRO, OBJETO_PADRAO, new BigDecimal("1.00"), inicio, fim, uma);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu editar o objeto desse contrato para {string}")
    public void eu_editar_o_objeto_desse_contrato_para(String novoObjeto) {
        try {
            List<DadosParteContrato> dados = contratoEmContexto.getPartes().stream()
                    .map(p -> new DadosParteContrato(p.getNomeParte(), p.getTipoParte()))
                    .toList();
            contratoEmContexto.atualizarDetalhes(
                    contratoEmContexto.getTipo(),
                    novoObjeto,
                    contratoEmContexto.getValor(),
                    contratoEmContexto.getDataInicio(),
                    contratoEmContexto.getDataFim(),
                    dados);
            contratoService.editarContrato(contratoEmContexto);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu tentar editar um contrato inexistente")
    public void eu_tentar_editar_um_contrato_inexistente() {
        Contrato orphan = novoContratoValido(ID_EVENTO);
        when(contratoRepository.buscarPorId(orphan.getId())).thenReturn(Optional.empty());
        try {
            contratoService.editarContrato(orphan);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu tentar editar o contrato com valor zero")
    public void eu_tentar_editar_o_contrato_com_valor_zero() {
        try {
            List<DadosParteContrato> dados = contratoEmContexto.getPartes().stream()
                    .map(p -> new DadosParteContrato(p.getNomeParte(), p.getTipoParte()))
                    .toList();
            contratoEmContexto.atualizarDetalhes(
                    contratoEmContexto.getTipo(),
                    contratoEmContexto.getObjeto(),
                    BigDecimal.ZERO,
                    contratoEmContexto.getDataInicio(),
                    contratoEmContexto.getDataFim(),
                    dados);
            contratoService.editarContrato(contratoEmContexto);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu tentar editar esse contrato encerrado")
    public void eu_tentar_editar_esse_contrato_encerrado() {
        try {
            List<DadosParteContrato> dados = contratoEmContexto.getPartes().stream()
                    .map(p -> new DadosParteContrato(p.getNomeParte(), p.getTipoParte()))
                    .toList();
            contratoEmContexto.atualizarDetalhes(
                    contratoEmContexto.getTipo(),
                    "Tentativa de alteração",
                    contratoEmContexto.getValor(),
                    contratoEmContexto.getDataInicio(),
                    contratoEmContexto.getDataFim(),
                    dados);
            contratoService.editarContrato(contratoEmContexto);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu buscar esse contrato pelo id")
    public void eu_buscar_esse_contrato_pelo_id() {
        try {
            contratoRetornadoBusca = contratoService.buscarContrato(contratoEmContexto.getId());
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu listar contratos desse evento")
    public void eu_listar_contratos_desse_evento() {
        try {
            when(contratoRepository.listarPorEventoId(ID_EVENTO)).thenReturn(List.of(contratoEmContexto));
            listaContratosRetornada = contratoService.listarContratosPorEvento(ID_EVENTO);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu tentar buscar contrato por id inexistente")
    public void eu_tentar_buscar_contrato_por_id_inexistente() {
        try {
            contratoService.buscarContrato("id-inexistente");
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu encerrar esse contrato")
    public void eu_encerrar_esse_contrato() {
        try {
            contratoService.encerrarContrato(contratoEmContexto.getId());
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu tentar encerrar esse contrato")
    public void eu_tentar_encerrar_esse_contrato() {
        eu_encerrar_esse_contrato();
    }

    @When("eu tentar encerrar novamente esse contrato")
    public void eu_tentar_encerrar_novamente_esse_contrato() {
        eu_encerrar_esse_contrato();
    }

    @Then("o contrato é salvo com sucesso")
    @Then("o contrato é atualizado com sucesso")
    public void operacao_contrato_sucesso() {
        assertNull(excecaoLancada, () -> "Não deveria ter lançado exceção: "
                + (excecaoLancada != null ? excecaoLancada.getMessage() : ""));
    }

    @Then("o sistema deve impedir o cadastro do contrato")
    @Then("o sistema deve impedir a edição do contrato")
    @Then("o sistema deve impedir a visualização do contrato")
    @Then("o sistema deve impedir o encerramento do contrato")
    public void operacao_contrato_impedida() {
        assertNotNull(excecaoLancada, "Era esperada uma exceção.");
    }

    @Then("o contrato retornado contém o objeto esperado")
    public void o_contrato_retornado_contem_o_objeto_esperado() {
        assertNull(excecaoLancada);
        assertNotNull(contratoRetornadoBusca);
        assertEquals(OBJETO_PADRAO, contratoRetornadoBusca.getObjeto());
    }

    @Then("a lista contém ao menos um contrato")
    public void a_lista_contem_ao_menos_um_contrato() {
        assertNull(excecaoLancada);
        assertNotNull(listaContratosRetornada);
        assertFalse(listaContratosRetornada.isEmpty());
    }

    @Then("o contrato passa ao status ENCERRADO")
    public void o_contrato_passa_ao_status_encerrado() {
        assertNull(excecaoLancada);
        assertEquals(StatusContrato.ENCERRADO, contratoEmContexto.getStatus());
    }

    private void tentarCadastrar(Contrato contrato) {
        try {
            contratoService.criarContrato(contrato);
            contratoEmContexto = contrato;
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    private void tentarConstruirContratoInvalido(String objeto, BigDecimal valor) {
        LocalDateTime inicio = LocalDateTime.now();
        LocalDateTime fim = inicio.plusDays(10);
        List<DadosParteContrato> partes = duasPartesPadrao();
        try {
            new Contrato(ID_EVENTO, TipoContrato.FORNECEDOR, objeto, valor, inicio, fim, partes);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    private void tentarConstruirContratoComDatas(LocalDateTime inicio, LocalDateTime fim) {
        List<DadosParteContrato> partes = duasPartesPadrao();
        try {
            new Contrato(ID_EVENTO, TipoContrato.FORNECEDOR, OBJETO_PADRAO, new BigDecimal("5000.00"), inicio, fim,
                    partes);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    private Contrato novoContratoValido(String eventoId) {
        LocalDateTime inicio = LocalDateTime.now();
        LocalDateTime fim = inicio.plusDays(30);
        return new Contrato(eventoId, TipoContrato.FORNECEDOR, OBJETO_PADRAO, new BigDecimal("10000.00"), inicio, fim,
                duasPartesPadrao());
    }

    private static List<DadosParteContrato> duasPartesPadrao() {
        List<DadosParteContrato> partes = new ArrayList<>();
        partes.add(new DadosParteContrato("Organização Evento", "Contratante"));
        partes.add(new DadosParteContrato("Fornecedor XYZ", "Fornecedor"));
        return partes;
    }
}
