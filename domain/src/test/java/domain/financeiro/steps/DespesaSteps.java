package domain.financeiro.steps;

import domain.evento.entity.Evento;
import domain.evento.repository.EventoRepository;
import domain.financeiro.entity.CategoriaOrcamento;
import domain.financeiro.entity.Despesa;
import domain.financeiro.entity.OrcamentoEvento;
import domain.financeiro.repository.CategoriaOrcamentoRepository;
import domain.financeiro.repository.DespesaRepository;
import domain.financeiro.repository.OrcamentoEventoRepository;
import domain.financeiro.service.DespesaService;
import domain.financeiro.service.DespesaServiceImpl;
import domain.financeiro.valueobject.CategoriaDespesa;
import domain.financeiro.valueobject.ClassificacaoDesvio;
import domain.financeiro.valueobject.DesvioOrcamentario;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class DespesaSteps {

    private static final String ID_EVENTO      = "evento-fin-1";
    private static final String ID_ORCAMENTO   = "orc-1";
    private static final String ID_FORNECEDOR  = "fornecedor-1";
    private static final String ID_USUARIO     = "usuario-gestor-1";

    private EventoRepository            eventoRepository;
    private OrcamentoEventoRepository   orcamentoEventoRepository;
    private CategoriaOrcamentoRepository categoriaOrcamentoRepository;
    private DespesaRepository           despesaRepository;
    private DespesaService              despesaService;

    private Exception          excecaoLancada;
    private Despesa            despesaEmContexto;
    private Despesa            despesaRetornadaBusca;
    private List<Despesa>      listaDespesasRetornada;
    private DesvioOrcamentario desvioRetornado;
    private List<DesvioOrcamentario> listaDesviosRetornada;

    private OrcamentoEvento    orcamentoEmContexto;
    private List<CategoriaOrcamento> categoriasOrcamento = new ArrayList<>();

    @Before
    public void setup() {
        eventoRepository             = Mockito.mock(EventoRepository.class);
        orcamentoEventoRepository    = Mockito.mock(OrcamentoEventoRepository.class);
        categoriaOrcamentoRepository = Mockito.mock(CategoriaOrcamentoRepository.class);
        despesaRepository            = Mockito.mock(DespesaRepository.class);

        despesaService = new DespesaServiceImpl(
                despesaRepository,
                orcamentoEventoRepository,
                categoriaOrcamentoRepository,
                eventoRepository);

        excecaoLancada          = null;
        despesaEmContexto       = null;
        despesaRetornadaBusca   = null;
        listaDespesasRetornada  = null;
        desvioRetornado         = null;
        listaDesviosRetornada   = null;
        orcamentoEmContexto     = null;
        categoriasOrcamento     = new ArrayList<>();

        when(despesaRepository.salvar(any(Despesa.class)))
                .thenAnswer(inv -> inv.getArgument(0));
    }


    @Given("existe um evento válido para despesas")
    public void existe_um_evento_valido_para_despesas() {
        when(eventoRepository.buscarPorId(ID_EVENTO))
                .thenReturn(Optional.of(Mockito.mock(Evento.class)));
    }

    @Given("não existe evento válido para despesas")
    public void nao_existe_evento_valido_para_despesas() {
        when(eventoRepository.buscarPorId(any())).thenReturn(Optional.empty());
    }

    @Given("existe um orçamento cadastrado para o evento")
    public void existe_um_orcamento_cadastrado_para_o_evento() {
        orcamentoEmContexto = new OrcamentoEvento(ID_EVENTO, new BigDecimal("10000.00"));
        when(orcamentoEventoRepository.buscarPorEventoId(ID_EVENTO))
                .thenReturn(Optional.of(orcamentoEmContexto));
        when(categoriaOrcamentoRepository.buscarPorOrcamentoECategoria(any(), any()))
                .thenReturn(Optional.empty());
        when(categoriaOrcamentoRepository.listarPorOrcamentoId(any()))
                .thenAnswer(inv -> new ArrayList<>(categoriasOrcamento));
    }

    @Given("não existe orçamento cadastrado para o evento")
    public void nao_existe_orcamento_cadastrado_para_o_evento() {
        when(orcamentoEventoRepository.buscarPorEventoId(any())).thenReturn(Optional.empty());
    }

    @Given("a categoria {string} possui orçamento previsto de {double}")
    public void a_categoria_possui_orcamento_previsto_de(String catStr, double valorPrevisto) {
        CategoriaDespesa cat = CategoriaDespesa.valueOf(catStr);
        CategoriaOrcamento co = new CategoriaOrcamento(
                ID_ORCAMENTO, cat, BigDecimal.valueOf(valorPrevisto));

        categoriasOrcamento.add(co);

        when(categoriaOrcamentoRepository.buscarPorOrcamentoECategoria(any(), eq(cat)))
                .thenReturn(Optional.of(co));
        when(despesaRepository.somarValoresPorEventoECategoria(eq(ID_EVENTO), eq(cat)))
                .thenReturn(BigDecimal.ZERO);
    }

    @Given("já foram registradas despesas de {double} na categoria {string}")
    public void ja_foram_registradas_despesas_de_na_categoria(double total, String catStr) {
        CategoriaDespesa cat = CategoriaDespesa.valueOf(catStr);
        when(despesaRepository.somarValoresPorEventoECategoria(eq(ID_EVENTO), eq(cat)))
                .thenReturn(BigDecimal.valueOf(total));
    }

    @Given("o total acumulado de despesas da categoria {string} é de {double}")
    public void o_total_acumulado_de_despesas_da_categoria_e_de(String catStr, double total) {
        ja_foram_registradas_despesas_de_na_categoria(total, catStr);
    }

    @Given("existe uma despesa registrada de {double} na categoria {string}")
    public void existe_uma_despesa_registrada_de_na_categoria(double valor, String catStr) {
        CategoriaDespesa cat = CategoriaDespesa.valueOf(catStr);
        despesaEmContexto = new Despesa(ID_EVENTO, cat, ID_FORNECEDOR,
                BigDecimal.valueOf(valor), LocalDateTime.now(), ID_USUARIO);
        when(despesaRepository.buscarPorId(despesaEmContexto.getId()))
                .thenReturn(Optional.of(despesaEmContexto));
        when(despesaRepository.listarPorEventoId(ID_EVENTO))
                .thenReturn(List.of(despesaEmContexto));
    }

    @Given("não existe despesa com o id informado")
    public void nao_existe_despesa_com_o_id_informado() {
        when(despesaRepository.buscarPorId(any())).thenReturn(Optional.empty());
    }



    @When("eu registrar uma despesa de {double} na categoria {string} com fornecedor e usuário válidos")
    public void eu_registrar_uma_despesa_de_na_categoria(double valor, String catStr) {
        try {
            Despesa d = new Despesa(ID_EVENTO, CategoriaDespesa.valueOf(catStr),
                    ID_FORNECEDOR, BigDecimal.valueOf(valor), LocalDateTime.now(), ID_USUARIO);
            despesaEmContexto = despesaService.registrarDespesa(d);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu tentar registrar uma despesa sem evento válido")
    public void eu_tentar_registrar_uma_despesa_sem_evento_valido() {
        try {
            Despesa d = new Despesa("evento-inexistente", CategoriaDespesa.ALIMENTACAO,
                    ID_FORNECEDOR, new BigDecimal("100.00"), LocalDateTime.now(), ID_USUARIO);
            despesaService.registrarDespesa(d);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu tentar registrar uma despesa sem orçamento")
    public void eu_tentar_registrar_uma_despesa_sem_orcamento() {
        try {
            Despesa d = new Despesa(ID_EVENTO, CategoriaDespesa.ALIMENTACAO,
                    ID_FORNECEDOR, new BigDecimal("100.00"), LocalDateTime.now(), ID_USUARIO);
            despesaService.registrarDespesa(d);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu tentar registrar uma despesa na categoria {string} sem orçamento previsto")
    public void eu_tentar_registrar_uma_despesa_na_categoria_sem_orcamento_previsto(String catStr) {
        try {
            Despesa d = new Despesa(ID_EVENTO, CategoriaDespesa.valueOf(catStr),
                    ID_FORNECEDOR, new BigDecimal("100.00"), LocalDateTime.now(), ID_USUARIO);
            despesaService.registrarDespesa(d);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu tentar registrar uma despesa com valor zero")
    public void eu_tentar_registrar_uma_despesa_com_valor_zero() {
        try {
            new Despesa(ID_EVENTO, CategoriaDespesa.ALIMENTACAO,
                    ID_FORNECEDOR, BigDecimal.ZERO, LocalDateTime.now(), ID_USUARIO);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu tentar registrar uma despesa com valor negativo")
    public void eu_tentar_registrar_uma_despesa_com_valor_negativo() {
        try {
            new Despesa(ID_EVENTO, CategoriaDespesa.ALIMENTACAO,
                    ID_FORNECEDOR, new BigDecimal("-50.00"), LocalDateTime.now(), ID_USUARIO);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu tentar registrar uma despesa sem categoria")
    public void eu_tentar_registrar_uma_despesa_sem_categoria() {
        try {
            new Despesa(ID_EVENTO, null, ID_FORNECEDOR,
                    new BigDecimal("100.00"), LocalDateTime.now(), ID_USUARIO);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu tentar registrar uma despesa sem fornecedor")
    public void eu_tentar_registrar_uma_despesa_sem_fornecedor() {
        try {
            new Despesa(ID_EVENTO, CategoriaDespesa.ALIMENTACAO,
                    null, new BigDecimal("100.00"), LocalDateTime.now(), ID_USUARIO);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu tentar registrar uma despesa sem data")
    public void eu_tentar_registrar_uma_despesa_sem_data() {
        try {
            new Despesa(ID_EVENTO, CategoriaDespesa.ALIMENTACAO,
                    ID_FORNECEDOR, new BigDecimal("100.00"), null, ID_USUARIO);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }


    @When("eu calcular o desvio da categoria {string}")
    public void eu_calcular_o_desvio_da_categoria(String catStr) {
        try {
            desvioRetornado = despesaService.calcularDesvio(ID_EVENTO,
                    CategoriaDespesa.valueOf(catStr));
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu tentar calcular o desvio sem orçamento cadastrado")
    public void eu_tentar_calcular_o_desvio_sem_orcamento_cadastrado() {
        try {
            despesaService.calcularDesvio(ID_EVENTO, CategoriaDespesa.ALIMENTACAO);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu calcular os desvios de todas as categorias do evento")
    public void eu_calcular_os_desvios_de_todas_as_categorias_do_evento() {
        try {
            for (CategoriaOrcamento co : categoriasOrcamento) {
                when(despesaRepository.somarValoresPorEventoECategoria(
                        eq(ID_EVENTO), eq(co.getNome())))
                        .thenReturn(BigDecimal.ZERO);
            }
            listaDesviosRetornada = despesaService.calcularDesviosPorEvento(ID_EVENTO);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }


    @When("eu buscar essa despesa pelo id")
    public void eu_buscar_essa_despesa_pelo_id() {
        try {
            despesaRetornadaBusca = despesaService.buscarDespesa(despesaEmContexto.getId());
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu listar as despesas do evento")
    public void eu_listar_as_despesas_do_evento() {
        try {
            listaDespesasRetornada = despesaService.listarDespesasPorEvento(ID_EVENTO);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu tentar buscar a despesa por id inexistente")
    public void eu_tentar_buscar_a_despesa_por_id_inexistente() {
        try {
            despesaService.buscarDespesa("id-inexistente");
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @Then("a despesa é salva com sucesso")
    public void a_despesa_e_salva_com_sucesso() {
        assertNull(excecaoLancada,
                () -> "Não deveria ter lançado exceção: " + excecaoLancada.getMessage());
        assertNotNull(despesaEmContexto);
    }

    @Then("o sistema deve impedir o registro da despesa")
    public void o_sistema_deve_impedir_o_registro_da_despesa() {
        assertNotNull(excecaoLancada, "Era esperada uma exceção, mas nenhuma foi lançada.");
    }

    @Then("a despesa deve conter data, hora e usuário responsável pelo lançamento")
    public void a_despesa_deve_conter_data_hora_e_usuario() {
        assertNull(excecaoLancada);
        assertNotNull(despesaEmContexto);
        assertNotNull(despesaEmContexto.getDataHoraLancamento(), "dataHoraLancamento deve ser preenchida automaticamente.");
        assertNotNull(despesaEmContexto.getLancadoPorUsuarioId(), "lancadoPorUsuarioId deve estar preenchido.");
        assertEquals(ID_USUARIO, despesaEmContexto.getLancadoPorUsuarioId());
    }

    @Then("o desvio percentual calculado deve ser de {double} porcento")
    public void o_desvio_percentual_calculado_deve_ser_de(double esperado) {
        assertNull(excecaoLancada);
        assertNotNull(desvioRetornado);
        assertEquals(esperado, desvioRetornado.getDesvioPercentual(), 0.001);
    }

    @Then("a classificação deve ser {string}")
    public void a_classificacao_deve_ser(String esperado) {
        assertNull(excecaoLancada);
        assertNotNull(desvioRetornado);
        assertEquals(ClassificacaoDesvio.valueOf(esperado), desvioRetornado.getClassificacao());
    }

    @Then("o sistema deve impedir o cálculo do desvio")
    public void o_sistema_deve_impedir_o_calculo_do_desvio() {
        assertNotNull(excecaoLancada, "Era esperada uma exceção ao calcular desvio sem orçamento.");
    }

    @Then("a lista de desvios deve conter ao menos uma entrada")
    public void a_lista_de_desvios_deve_conter_ao_menos_uma_entrada() {
        assertNull(excecaoLancada);
        assertNotNull(listaDesviosRetornada);
        assertFalse(listaDesviosRetornada.isEmpty());
    }

    @Then("a despesa retornada deve conter os dados corretos")
    public void a_despesa_retornada_deve_conter_os_dados_corretos() {
        assertNull(excecaoLancada);
        assertNotNull(despesaRetornadaBusca);
        assertEquals(despesaEmContexto.getId(), despesaRetornadaBusca.getId());
        assertEquals(despesaEmContexto.getValor(), despesaRetornadaBusca.getValor());
    }

    @Then("a lista deve conter ao menos uma despesa")
    public void a_lista_deve_conter_ao_menos_uma_despesa() {
        assertNull(excecaoLancada);
        assertNotNull(listaDespesasRetornada);
        assertFalse(listaDespesasRetornada.isEmpty());
    }

    @Then("o sistema deve impedir a visualização da despesa")
    public void o_sistema_deve_impedir_a_visualizacao_da_despesa() {
        assertNotNull(excecaoLancada, "Era esperada uma exceção ao buscar despesa inexistente.");
    }
}
