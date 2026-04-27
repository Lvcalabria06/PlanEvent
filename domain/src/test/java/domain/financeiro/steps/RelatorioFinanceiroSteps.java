package domain.financeiro.steps;

import domain.evento.entity.Evento;
import domain.evento.repository.EventoRepository;
import domain.financeiro.entity.CategoriaOrcamento;
import domain.financeiro.entity.OrcamentoEvento;
import domain.financeiro.entity.RelatorioFinanceiro;
import domain.financeiro.repository.CategoriaOrcamentoRepository;
import domain.financeiro.repository.DespesaRepository;
import domain.financeiro.repository.OrcamentoEventoRepository;
import domain.financeiro.repository.RelatorioFinanceiroRepository;
import domain.financeiro.service.RelatorioFinanceiroService;
import domain.financeiro.service.RelatorioFinanceiroServiceImpl;
import domain.financeiro.valueobject.CategoriaDespesa;
import domain.financeiro.valueobject.ItemRelatorioCategoria;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class RelatorioFinanceiroSteps {

    private static final String ID_EVENTO    = "evento-rel-1";
    private static final String ID_ORCAMENTO = "orc-rel-1";
    private static final String ID_USUARIO   = "controller-1";

    private EventoRepository              eventoRepository;
    private OrcamentoEventoRepository     orcamentoEventoRepository;
    private CategoriaOrcamentoRepository  categoriaOrcamentoRepository;
    private DespesaRepository             despesaRepository;
    private RelatorioFinanceiroRepository relatorioRepository;
    private RelatorioFinanceiroService    relatorioService;

    private Exception                excecaoLancada;
    private RelatorioFinanceiro      relatorioEmContexto;
    private RelatorioFinanceiro      relatorioRetornadoBusca;
    private RelatorioFinanceiro      segundoRelatorio;
    private List<RelatorioFinanceiro> listaRelatoriosRetornada;

    private OrcamentoEvento           orcamentoEmContexto;
    private List<CategoriaOrcamento>  categoriasOrcamento = new ArrayList<>();

    @Before
    public void setup() {
        eventoRepository             = Mockito.mock(EventoRepository.class);
        orcamentoEventoRepository    = Mockito.mock(OrcamentoEventoRepository.class);
        categoriaOrcamentoRepository = Mockito.mock(CategoriaOrcamentoRepository.class);
        despesaRepository            = Mockito.mock(DespesaRepository.class);
        relatorioRepository          = Mockito.mock(RelatorioFinanceiroRepository.class);

        relatorioService = new RelatorioFinanceiroServiceImpl(
                relatorioRepository,
                orcamentoEventoRepository,
                categoriaOrcamentoRepository,
                despesaRepository,
                eventoRepository);

        excecaoLancada           = null;
        relatorioEmContexto      = null;
        relatorioRetornadoBusca  = null;
        segundoRelatorio         = null;
        listaRelatoriosRetornada = null;
        orcamentoEmContexto      = null;
        categoriasOrcamento      = new ArrayList<>();

        when(relatorioRepository.salvar(any(RelatorioFinanceiro.class)))
                .thenAnswer(inv -> inv.getArgument(0));
    }



    @Given("existe um evento válido para relatório")
    public void existe_um_evento_valido_para_relatorio() {
        when(eventoRepository.buscarPorId(ID_EVENTO))
                .thenReturn(Optional.of(Mockito.mock(Evento.class)));
    }

    @Given("não existe evento válido para relatório")
    public void nao_existe_evento_valido_para_relatorio() {
        when(eventoRepository.buscarPorId(any())).thenReturn(Optional.empty());
    }

    @Given("existe um orçamento cadastrado para o evento do relatório com categorias")
    public void existe_um_orcamento_cadastrado_para_o_evento_do_relatorio_com_categorias() {
        orcamentoEmContexto = new OrcamentoEvento(ID_EVENTO, new BigDecimal("10000.00"));
        when(orcamentoEventoRepository.buscarPorEventoId(ID_EVENTO))
                .thenReturn(Optional.of(orcamentoEmContexto));
        when(categoriaOrcamentoRepository.listarPorOrcamentoId(any()))
                .thenAnswer(inv -> new ArrayList<>(categoriasOrcamento));
    }

    @Given("não existe orçamento cadastrado para o evento do relatório")
    public void nao_existe_orcamento_cadastrado_para_o_evento_do_relatorio() {
        when(orcamentoEventoRepository.buscarPorEventoId(any())).thenReturn(Optional.empty());
    }

    @Given("existe um orçamento cadastrado para o relatório sem categorias")
    public void existe_um_orcamento_cadastrado_para_o_relatorio_sem_categorias() {
        orcamentoEmContexto = new OrcamentoEvento(ID_EVENTO, new BigDecimal("10000.00"));
        when(orcamentoEventoRepository.buscarPorEventoId(ID_EVENTO))
                .thenReturn(Optional.of(orcamentoEmContexto));
        when(categoriaOrcamentoRepository.listarPorOrcamentoId(any()))
                .thenReturn(new ArrayList<>());
    }

    @Given("o orçamento da categoria {string} é de {double} para o relatório")
    public void o_orcamento_da_categoria_e_de_para_o_relatorio(String catStr, double valorPrevisto) {
        CategoriaDespesa cat = CategoriaDespesa.valueOf(catStr);
        CategoriaOrcamento co = new CategoriaOrcamento(
                ID_ORCAMENTO, cat, BigDecimal.valueOf(valorPrevisto));
        categoriasOrcamento.add(co);
        when(despesaRepository.somarValoresPorEventoECategoria(eq(ID_EVENTO), eq(cat)))
                .thenReturn(BigDecimal.ZERO);
    }

    @Given("foram registradas despesas de {double} na categoria {string} para o relatório")
    public void foram_registradas_despesas_de_na_categoria_para_o_relatorio(double total, String catStr) {
        CategoriaDespesa cat = CategoriaDespesa.valueOf(catStr);
        when(despesaRepository.somarValoresPorEventoECategoria(eq(ID_EVENTO), eq(cat)))
                .thenReturn(BigDecimal.valueOf(total));
    }

    @Given("existe um relatório financeiro já gerado")
    public void existe_um_relatorio_financeiro_ja_gerado() {
        List<ItemRelatorioCategoria> itens = List.of(
                new ItemRelatorioCategoria(
                        CategoriaDespesa.ALIMENTACAO,
                        new BigDecimal("1000.00"),
                        new BigDecimal("800.00")));

        relatorioEmContexto = new RelatorioFinanceiro(
                ID_EVENTO, ID_USUARIO,
                new BigDecimal("1000.00"),
                new BigDecimal("800.00"),
                itens,
                "Conteúdo do relatório");

        when(relatorioRepository.buscarPorId(relatorioEmContexto.getId()))
                .thenReturn(Optional.of(relatorioEmContexto));
    }

    @Given("existem dois relatórios gerados para o evento")
    public void existem_dois_relatorios_gerados_para_o_evento() {
        List<ItemRelatorioCategoria> itens = List.of(
                new ItemRelatorioCategoria(
                        CategoriaDespesa.ALIMENTACAO,
                        new BigDecimal("1000.00"),
                        new BigDecimal("900.00")));

        RelatorioFinanceiro r1 = new RelatorioFinanceiro(
                ID_EVENTO, ID_USUARIO,
                new BigDecimal("1000.00"), new BigDecimal("900.00"), itens, "Rel 1");

        RelatorioFinanceiro r2 = new RelatorioFinanceiro(
                ID_EVENTO, ID_USUARIO,
                new BigDecimal("1000.00"), new BigDecimal("950.00"), itens, "Rel 2");

        when(relatorioRepository.listarPorEventoId(ID_EVENTO))
                .thenReturn(List.of(r1, r2));
    }

    @Given("não existe relatório com o id informado")
    public void nao_existe_relatorio_com_o_id_informado() {
        when(relatorioRepository.buscarPorId(any())).thenReturn(Optional.empty());
    }



    @When("eu gerar o relatório financeiro do evento")
    public void eu_gerar_o_relatorio_financeiro_do_evento() {
        try {
            relatorioEmContexto = relatorioService.gerarRelatorio(ID_EVENTO, ID_USUARIO);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu tentar gerar o relatório sem orçamento")
    public void eu_tentar_gerar_sem_orcamento() {
        eu_gerar_o_relatorio_financeiro_do_evento();
    }

    @When("eu tentar gerar o relatório sem categorias no orçamento")
    public void eu_tentar_gerar_sem_categorias() {
        eu_gerar_o_relatorio_financeiro_do_evento();
    }

    @When("eu tentar gerar o relatório com evento inválido")
    public void eu_tentar_gerar_com_evento_invalido() {
        try {
            relatorioService.gerarRelatorio(ID_EVENTO, ID_USUARIO);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu tentar modificar o conteúdo do relatório")
    public void eu_tentar_modificar_o_conteudo_do_relatorio() {
        try {
            relatorioEmContexto.getClass().getMethod("setConteudo", String.class);
            excecaoLancada = new IllegalStateException(
                    "RelatorioFinanceiro não deveria expor setters.");
        } catch (NoSuchMethodException e) {
            excecaoLancada = null;
        }
    }

    @When("eu gerar dois relatórios do mesmo evento em momentos distintos")
    public void eu_gerar_dois_relatorios_do_mesmo_evento() {
        try {
            relatorioEmContexto = relatorioService.gerarRelatorio(ID_EVENTO, ID_USUARIO);
            Thread.sleep(10);
            segundoRelatorio = relatorioService.gerarRelatorio(ID_EVENTO, ID_USUARIO);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu buscar o relatório pelo id")
    public void eu_buscar_o_relatorio_pelo_id() {
        try {
            relatorioRetornadoBusca = relatorioService.buscarRelatorio(relatorioEmContexto.getId());
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu listar os relatórios do evento")
    public void eu_listar_os_relatorios_do_evento() {
        try {
            listaRelatoriosRetornada = relatorioService.listarRelatoriosPorEvento(ID_EVENTO);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu tentar buscar o relatório por id inexistente")
    public void eu_tentar_buscar_relatorio_inexistente() {
        try {
            relatorioService.buscarRelatorio("id-inexistente");
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }



    @Then("o relatório é gerado e persistido com sucesso")
    public void o_relatorio_e_gerado_com_sucesso() {
        assertNull(excecaoLancada,
                () -> "Não deveria ter lançado exceção: " + excecaoLancada.getMessage());
        assertNotNull(relatorioEmContexto);
        assertNotNull(relatorioEmContexto.getId());
    }

    @Then("o relatório deve conter o total geral previsto e realizado")
    public void o_relatorio_deve_conter_total_geral() {
        assertNull(excecaoLancada);
        assertNotNull(relatorioEmContexto.getTotalGeralPrevisto());
        assertNotNull(relatorioEmContexto.getTotalGeralRealizado());
        assertTrue(relatorioEmContexto.getTotalGeralPrevisto()
                .compareTo(BigDecimal.ZERO) > 0);
    }

    @Then("o relatório deve conter itens por categoria")
    public void o_relatorio_deve_conter_itens_por_categoria() {
        assertNull(excecaoLancada);
        assertNotNull(relatorioEmContexto.getItensPorCategoria());
        assertFalse(relatorioEmContexto.getItensPorCategoria().isEmpty());
    }

    @And("o percentual de variação da categoria {string} deve ser de {double} porcento")
    public void o_percentual_de_variacao_deve_ser(String catStr, double esperado) {
        CategoriaDespesa cat = CategoriaDespesa.valueOf(catStr);
        ItemRelatorioCategoria item = relatorioEmContexto.getItensPorCategoria()
                .stream()
                .filter(i -> i.getCategoria() == cat)
                .findFirst()
                .orElseThrow(() -> new AssertionError(
                        "Categoria " + cat + " não encontrada no relatório."));
        assertEquals(esperado, item.getPercentualVariacao(), 0.001);
    }

    @Then("o sistema deve impedir a geração do relatório")
    public void o_sistema_deve_impedir_a_geracao() {
        assertNotNull(excecaoLancada, "Era esperada uma exceção, mas nenhuma foi lançada.");
    }

    @Then("o relatório deve conter a data de geração e o usuário responsável")
    public void o_relatorio_deve_conter_data_e_usuario() {
        assertNull(excecaoLancada);
        assertNotNull(relatorioEmContexto.getDataGeracao(),
                "dataGeracao deve ser preenchida automaticamente.");
        assertNotNull(relatorioEmContexto.getGeradoPorUsuarioId(),
                "geradoPorUsuarioId deve estar preenchido.");
        assertEquals(ID_USUARIO, relatorioEmContexto.getGeradoPorUsuarioId());
    }

    @Then("o sistema deve impedir a edição do relatório")
    public void o_sistema_deve_impedir_a_edicao() {
        assertNull(excecaoLancada,
                "RelatorioFinanceiro expõe setters, o que viola RN7.");
        assertThrows(UnsupportedOperationException.class, () ->
                relatorioEmContexto.getItensPorCategoria().clear());
    }

    @Then("os dois relatórios devem ter identificadores diferentes")
    public void os_dois_relatorios_devem_ter_ids_diferentes() {
        assertNull(excecaoLancada);
        assertNotNull(relatorioEmContexto);
        assertNotNull(segundoRelatorio);
        assertNotEquals(relatorioEmContexto.getId(), segundoRelatorio.getId());
    }

    @And("os dois relatórios devem ter datas de geração registradas")
    public void os_dois_relatorios_devem_ter_datas_registradas() {
        assertNotNull(relatorioEmContexto.getDataGeracao());
        assertNotNull(segundoRelatorio.getDataGeracao());
    }

    @Then("o relatório retornado deve conter os dados corretos")
    public void o_relatorio_retornado_deve_conter_os_dados_corretos() {
        assertNull(excecaoLancada);
        assertNotNull(relatorioRetornadoBusca);
        assertEquals(relatorioEmContexto.getId(), relatorioRetornadoBusca.getId());
        assertEquals(relatorioEmContexto.getEventoId(), relatorioRetornadoBusca.getEventoId());
    }

    @Then("a lista deve conter dois relatórios")
    public void a_lista_deve_conter_dois_relatorios() {
        assertNull(excecaoLancada);
        assertNotNull(listaRelatoriosRetornada);
        assertEquals(2, listaRelatoriosRetornada.size());
    }

    @Then("o sistema deve impedir a visualização do relatório")
    public void o_sistema_deve_impedir_a_visualizacao() {
        assertNotNull(excecaoLancada,
                "Era esperada uma exceção ao buscar relatório inexistente.");
    }
}