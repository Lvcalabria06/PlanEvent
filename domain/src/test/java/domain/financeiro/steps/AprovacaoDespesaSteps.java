package domain.financeiro.steps;

import domain.financeiro.entity.Despesa;
import domain.financeiro.repository.DespesaRepository;
import domain.financeiro.service.DespesaService;
import domain.financeiro.service.DespesaServiceImpl;
import domain.financeiro.repository.OrcamentoEventoRepository;
import domain.financeiro.repository.CategoriaOrcamentoRepository;
import domain.evento.repository.EventoRepository;
import domain.fornecedor.entity.Fornecedor;
import domain.fornecedor.repository.FornecedorRepository;
import domain.financeiro.valueobject.CategoriaDespesa;
import domain.financeiro.valueobject.StatusDespesa;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class AprovacaoDespesaSteps {

    private static final String ID_EVENTO     = "evento-apr-1";
    private static final String ID_APROVADOR  = "aprovador-gestor-1";
    private static final String ID_USUARIO    = "usuario-lanc-1";
    private static final String ID_FORNECEDOR = "fornecedor-1";

    private DespesaRepository            despesaRepository;
    private OrcamentoEventoRepository    orcamentoEventoRepository;
    private CategoriaOrcamentoRepository categoriaOrcamentoRepository;
    private EventoRepository             eventoRepository;
    private FornecedorRepository         fornecedorRepository;
    private DespesaService               despesaService;

    private Despesa   despesaEmContexto;
    private Despesa   despesaRetornada;
    private Exception excecaoLancada;

    @Before
    public void setup() {
        eventoRepository             = Mockito.mock(EventoRepository.class);
        orcamentoEventoRepository    = Mockito.mock(OrcamentoEventoRepository.class);
        categoriaOrcamentoRepository = Mockito.mock(CategoriaOrcamentoRepository.class);
        despesaRepository            = Mockito.mock(DespesaRepository.class);
        fornecedorRepository         = Mockito.mock(FornecedorRepository.class);

        Fornecedor fornecedor = Mockito.mock(Fornecedor.class);
        when(fornecedor.isAtivo()).thenReturn(true);
        when(fornecedorRepository.buscarPorId(ID_FORNECEDOR)).thenReturn(Optional.of(fornecedor));
        when(fornecedorRepository.buscarPorId(any())).thenReturn(Optional.of(fornecedor));

        despesaService = new DespesaServiceImpl(
                despesaRepository,
                orcamentoEventoRepository,
                categoriaOrcamentoRepository,
                eventoRepository,
                fornecedorRepository);

        despesaEmContexto = null;
        despesaRetornada  = null;
        excecaoLancada    = null;

        when(despesaRepository.salvar(any(Despesa.class)))
                .thenAnswer(inv -> inv.getArgument(0));
    }

    // ──── Givens ──────────────────────────────────────────────────────────

    @Given("existe uma despesa pendente de aprovação de {double} na categoria {string}")
    public void existe_uma_despesa_pendente_de_aprovacao(double valor, String catStr) {
        despesaEmContexto = new Despesa(ID_EVENTO, CategoriaDespesa.valueOf(catStr),
                ID_FORNECEDOR, BigDecimal.valueOf(valor), LocalDateTime.now(), ID_USUARIO);
        despesaEmContexto.marcarPendente();

        when(despesaRepository.buscarPorId(despesaEmContexto.getId()))
                .thenReturn(Optional.of(despesaEmContexto));
    }

    @Given("existe uma despesa já aprovada")
    public void existe_uma_despesa_ja_aprovada() {
        despesaEmContexto = new Despesa(ID_EVENTO, CategoriaDespesa.SERVICO,
                ID_FORNECEDOR, new BigDecimal("100.00"), LocalDateTime.now(), ID_USUARIO);
        despesaEmContexto.marcarPendente();
        despesaEmContexto.aprovar(ID_APROVADOR);

        when(despesaRepository.buscarPorId(despesaEmContexto.getId()))
                .thenReturn(Optional.of(despesaEmContexto));
    }

    @Given("existe uma despesa já rejeitada")
    public void existe_uma_despesa_ja_rejeitada() {
        despesaEmContexto = new Despesa(ID_EVENTO, CategoriaDespesa.SERVICO,
                ID_FORNECEDOR, new BigDecimal("100.00"), LocalDateTime.now(), ID_USUARIO);
        despesaEmContexto.marcarPendente();
        despesaEmContexto.rejeitar(ID_APROVADOR, "Motivo inicial");

        when(despesaRepository.buscarPorId(despesaEmContexto.getId()))
                .thenReturn(Optional.of(despesaEmContexto));
    }

    @Given("existe uma despesa com status REGISTRADA")
    public void existe_uma_despesa_com_status_registrada() {
        despesaEmContexto = new Despesa(ID_EVENTO, CategoriaDespesa.SERVICO,
                ID_FORNECEDOR, new BigDecimal("100.00"), LocalDateTime.now(), ID_USUARIO);

        when(despesaRepository.buscarPorId(despesaEmContexto.getId()))
                .thenReturn(Optional.of(despesaEmContexto));
    }

    // ──── Whens ───────────────────────────────────────────────────────────

    @When("o aprovador aprovar a despesa pendente")
    public void o_aprovador_aprovar_a_despesa_pendente() {
        try {
            despesaRetornada = despesaService.aprovarDespesa(despesaEmContexto.getId(), ID_APROVADOR);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("o aprovador rejeitar a despesa com motivo {string}")
    public void o_aprovador_rejeitar_a_despesa_com_motivo(String motivo) {
        try {
            despesaRetornada = despesaService.rejeitarDespesa(
                    despesaEmContexto.getId(), ID_APROVADOR, motivo);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu tentar aprovar novamente a despesa já aprovada")
    public void eu_tentar_aprovar_novamente_a_despesa_ja_aprovada() {
        try {
            despesaService.aprovarDespesa(despesaEmContexto.getId(), ID_APROVADOR);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu tentar rejeitar novamente a despesa já rejeitada")
    public void eu_tentar_rejeitar_novamente_a_despesa_ja_rejeitada() {
        try {
            despesaService.rejeitarDespesa(despesaEmContexto.getId(), ID_APROVADOR, "Motivo duplo");
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu tentar aprovar uma despesa REGISTRADA diretamente")
    public void eu_tentar_aprovar_uma_despesa_registrada_diretamente() {
        try {
            despesaService.aprovarDespesa(despesaEmContexto.getId(), ID_APROVADOR);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    // ──── Thens ───────────────────────────────────────────────────────────

    @Then("o aprovador deve estar registrado na despesa")
    public void o_aprovador_deve_estar_registrado_na_despesa() {
        assertNull(excecaoLancada);
        assertNotNull(despesaRetornada);
        assertEquals(StatusDespesa.APROVADA, despesaRetornada.getStatus());
        assertEquals(ID_APROVADOR, despesaRetornada.getAprovadorId());
    }

    @Then("o motivo de rejeição deve estar registrado")
    public void o_motivo_de_rejeicao_deve_estar_registrado() {
        assertNull(excecaoLancada);
        assertNotNull(despesaRetornada);
        assertEquals(StatusDespesa.REJEITADA, despesaRetornada.getStatus());
        assertNotNull(despesaRetornada.getMotivoRejeicao());
        assertFalse(despesaRetornada.getMotivoRejeicao().isBlank());
    }

    @Then("o sistema deve impedir a transição de estado")
    public void o_sistema_deve_impedir_a_transicao_de_estado() {
        assertNotNull(excecaoLancada,
                "Era esperada IllegalStateException para transição inválida de estado.");
        assertTrue(excecaoLancada instanceof IllegalStateException,
                "Esperado IllegalStateException, mas foi: "
                        + excecaoLancada.getClass().getSimpleName());
    }
}
