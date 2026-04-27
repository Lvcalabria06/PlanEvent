package domain.conciliacao.steps;

import domain.conciliacao.entity.RelatorioConciliacao;
import domain.conciliacao.entity.VinculoConciliacao;
import domain.conciliacao.repository.RelatorioConciliacaoRepository;
import domain.conciliacao.repository.VinculoConciliacaoRepository;
import domain.conciliacao.service.ConciliacaoService;
import domain.conciliacao.service.ConciliacaoServiceImpl;
import domain.conciliacao.valueobject.ItemRelatorioConciliacao;
import domain.conciliacao.valueobject.MetodoConciliacao;
import domain.conciliacao.valueobject.StatusConciliacao;
import domain.contrato.entity.Contrato;
import domain.contrato.repository.ContratoRepository;
import domain.contrato.valueobject.DadosParteContrato;
import domain.contrato.valueobject.TipoContrato;
import domain.financeiro.entity.Despesa;
import domain.financeiro.repository.DespesaRepository;
import domain.financeiro.valueobject.CategoriaDespesa;

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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ConciliacaoSteps {

    private static final String ID_EVENTO = "evento-conciliacao-1";
    private static final String ID_OUTRO_EVENTO = "evento-outro-99";
    private static final LocalDateTime VIGENCIA_INICIO = LocalDateTime.now().minusDays(10);
    private static final LocalDateTime VIGENCIA_FIM = LocalDateTime.now().plusDays(20);
    private static final LocalDateTime DATA_DENTRO_VIGENCIA = LocalDateTime.now();
    private static final LocalDateTime DATA_FORA_VIGENCIA = LocalDateTime.now().plusDays(30);

    private ContratoRepository contratoRepository;
    private DespesaRepository despesaRepository;
    private VinculoConciliacaoRepository vinculoRepository;
    private RelatorioConciliacaoRepository relatorioRepository;
    private ConciliacaoService conciliacaoService;

    private Exception excecaoLancada;
    private Contrato contratoEmContexto;
    private Despesa despesaEmContexto;
    private Despesa despesaDescobertaEmContexto;
    private Despesa despesaConjuntoFora;
    private VinculoConciliacao vinculoRetornado;
    private RelatorioConciliacao relatorioRetornado;
    private List<Despesa> despesasDescobertasRetornadas;
    private List<Contrato> contratosExtrapoladosRetornados;

    @Before
    public void setup() {
        contratoRepository = Mockito.mock(ContratoRepository.class);
        despesaRepository = Mockito.mock(DespesaRepository.class);
        vinculoRepository = Mockito.mock(VinculoConciliacaoRepository.class);
        relatorioRepository = Mockito.mock(RelatorioConciliacaoRepository.class);

        conciliacaoService = new ConciliacaoServiceImpl(
                contratoRepository, despesaRepository, vinculoRepository, relatorioRepository);

        excecaoLancada = null;
        contratoEmContexto = null;
        despesaEmContexto = null;
        despesaDescobertaEmContexto = null;
        despesaConjuntoFora = null;
        vinculoRetornado = null;
        relatorioRetornado = null;
        despesasDescobertasRetornadas = null;
        contratosExtrapoladosRetornados = null;

        when(vinculoRepository.salvar(any(VinculoConciliacao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(relatorioRepository.salvar(any(RelatorioConciliacao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    // --- Givens comuns ---

    @Given("existe um evento para conciliação")
    public void existe_um_evento_para_conciliacao() {
        // evento implícito via ID_EVENTO
    }

    @Given("existe um contrato ativo e vigente nesse evento para o fornecedor {string}")
    public void existe_um_contrato_ativo_e_vigente(String fornecedorId) {
        contratoEmContexto = novoContrato(ID_EVENTO, fornecedorId, VIGENCIA_INICIO, VIGENCIA_FIM);
        when(contratoRepository.listarPorEventoId(ID_EVENTO)).thenReturn(List.of(contratoEmContexto));
        when(contratoRepository.buscarPorId(contratoEmContexto.getId()))
                .thenReturn(Optional.of(contratoEmContexto));
        when(vinculoRepository.listarPorContratoId(contratoEmContexto.getId()))
                .thenReturn(new ArrayList<>());
    }

    @Given("existe um contrato encerrado nesse evento para o fornecedor {string}")
    public void existe_um_contrato_encerrado(String fornecedorId) {
        contratoEmContexto = novoContrato(ID_EVENTO, fornecedorId, VIGENCIA_INICIO, VIGENCIA_FIM);
        contratoEmContexto.encerrar();
        when(contratoRepository.listarPorEventoId(ID_EVENTO)).thenReturn(List.of(contratoEmContexto));
        when(contratoRepository.buscarPorId(contratoEmContexto.getId()))
                .thenReturn(Optional.of(contratoEmContexto));
    }

    @Given("não existe contrato compatível com a despesa nesse evento")
    public void nao_existe_contrato_compativel() {
        when(contratoRepository.listarPorEventoId(ID_EVENTO)).thenReturn(new ArrayList<>());
    }

    @Given("existe uma despesa válida do fornecedor {string} nesse evento dentro da vigência")
    public void existe_despesa_valida_dentro_vigencia(String fornecedorId) {
        despesaEmContexto = new Despesa(ID_EVENTO, CategoriaDespesa.SERVICO, fornecedorId,
                new BigDecimal("500.00"), DATA_DENTRO_VIGENCIA, "gestor-sistema");
        when(despesaRepository.listarPorEventoId(ID_EVENTO)).thenReturn(List.of(despesaEmContexto));
        when(despesaRepository.buscarPorId(despesaEmContexto.getId()))
                .thenReturn(Optional.of(despesaEmContexto));
        when(vinculoRepository.buscarPorDespesaId(despesaEmContexto.getId()))
                .thenReturn(Optional.empty());
        when(vinculoRepository.listarPorEventoId(ID_EVENTO)).thenReturn(new ArrayList<>());
    }

    @Given("existe uma despesa do fornecedor {string} com data fora da vigência do contrato")
    public void existe_despesa_fora_vigencia(String fornecedorId) {
        despesaEmContexto = new Despesa(ID_EVENTO, CategoriaDespesa.SERVICO, fornecedorId,
                new BigDecimal("500.00"), DATA_FORA_VIGENCIA, "gestor-sistema");
        when(despesaRepository.listarPorEventoId(ID_EVENTO)).thenReturn(List.of(despesaEmContexto));
        when(despesaRepository.buscarPorId(despesaEmContexto.getId()))
                .thenReturn(Optional.of(despesaEmContexto));
        when(vinculoRepository.buscarPorDespesaId(despesaEmContexto.getId()))
                .thenReturn(Optional.empty());
        when(vinculoRepository.listarPorEventoId(ID_EVENTO)).thenReturn(new ArrayList<>());
    }

    @Given("existe uma despesa válida sem vínculo de conciliação nesse evento")
    public void existe_despesa_sem_vinculo() {
        despesaDescobertaEmContexto = new Despesa(ID_EVENTO, CategoriaDespesa.OUTRO, "forn-x",
                new BigDecimal("200.00"), DATA_DENTRO_VIGENCIA, "gestor-sistema");
        List<Despesa> todas = new ArrayList<>();
        if (despesaEmContexto != null) todas.add(despesaEmContexto);
        todas.add(despesaDescobertaEmContexto);
        when(despesaRepository.listarPorEventoId(ID_EVENTO)).thenReturn(todas);
        when(despesaRepository.buscarPorId(despesaDescobertaEmContexto.getId()))
                .thenReturn(Optional.of(despesaDescobertaEmContexto));
    }

    @Given("existe uma despesa válida já vinculada a um contrato nesse evento")
    public void existe_despesa_vinculada() {
        despesaEmContexto = new Despesa(ID_EVENTO, CategoriaDespesa.SERVICO, "forn-1",
                new BigDecimal("500.00"), DATA_DENTRO_VIGENCIA, "gestor-sistema");
        if (contratoEmContexto == null) {
            contratoEmContexto = novoContrato(ID_EVENTO, "forn-1", VIGENCIA_INICIO, VIGENCIA_FIM);
            when(contratoRepository.listarPorEventoId(ID_EVENTO)).thenReturn(List.of(contratoEmContexto));
            when(contratoRepository.buscarPorId(contratoEmContexto.getId()))
                    .thenReturn(Optional.of(contratoEmContexto));
        }
        VinculoConciliacao vinculo = new VinculoConciliacao(
                ID_EVENTO, despesaEmContexto.getId(), contratoEmContexto.getId(),
                MetodoConciliacao.AUTOMATICO, "sistema");
        when(despesaRepository.listarPorEventoId(ID_EVENTO)).thenAnswer(inv -> {
            List<Despesa> lista = new ArrayList<>();
            lista.add(despesaEmContexto);
            if (despesaDescobertaEmContexto != null) lista.add(despesaDescobertaEmContexto);
            return lista;
        });
        when(despesaRepository.buscarPorId(despesaEmContexto.getId()))
                .thenReturn(Optional.of(despesaEmContexto));
        when(vinculoRepository.buscarPorDespesaId(despesaEmContexto.getId()))
                .thenReturn(Optional.of(vinculo));
        when(vinculoRepository.listarPorEventoId(ID_EVENTO)).thenReturn(List.of(vinculo));
    }

    @Given("existe uma despesa já vinculada automaticamente nesse evento")
    public void existe_despesa_ja_vinculada_automaticamente() {
        despesaEmContexto = new Despesa(ID_EVENTO, CategoriaDespesa.SERVICO, "forn-1",
                new BigDecimal("500.00"), DATA_DENTRO_VIGENCIA, "gestor-sistema");
        VinculoConciliacao vinculoExistente = new VinculoConciliacao(
                ID_EVENTO, despesaEmContexto.getId(), contratoEmContexto.getId(),
                MetodoConciliacao.AUTOMATICO, "sistema");
        when(despesaRepository.buscarPorId(despesaEmContexto.getId()))
                .thenReturn(Optional.of(despesaEmContexto));
        when(vinculoRepository.buscarPorDespesaId(despesaEmContexto.getId()))
                .thenReturn(Optional.of(vinculoExistente));
        when(vinculoRepository.listarPorEventoId(ID_EVENTO)).thenReturn(List.of(vinculoExistente));
    }

    @Given("existe um contrato ativo pertencente a outro evento")
    public void existe_contrato_outro_evento() {
        contratoEmContexto = novoContrato(ID_OUTRO_EVENTO, "forn-1", VIGENCIA_INICIO, VIGENCIA_FIM);
        when(contratoRepository.buscarPorId(contratoEmContexto.getId()))
                .thenReturn(Optional.of(contratoEmContexto));
    }

    @Given("existe um contrato cujo total de despesas conciliadas ultrapassa seu valor contratado")
    public void contrato_extrapolado() {
        contratoEmContexto = novoContrato(ID_EVENTO, "forn-1", VIGENCIA_INICIO, VIGENCIA_FIM);
        despesaEmContexto = new Despesa(ID_EVENTO, CategoriaDespesa.SERVICO, "forn-1",
                new BigDecimal("99999.00"), DATA_DENTRO_VIGENCIA, "gestor-sistema");
        VinculoConciliacao vinculo = new VinculoConciliacao(
                ID_EVENTO, despesaEmContexto.getId(), contratoEmContexto.getId(),
                MetodoConciliacao.AUTOMATICO, "sistema");
        when(contratoRepository.listarPorEventoId(ID_EVENTO)).thenReturn(List.of(contratoEmContexto));
        when(despesaRepository.listarPorEventoId(ID_EVENTO)).thenReturn(List.of(despesaEmContexto));
        when(vinculoRepository.listarPorEventoId(ID_EVENTO)).thenReturn(List.of(vinculo));
    }

    @Given("existe um contrato cujo total de despesas conciliadas não ultrapassa seu valor contratado")
    public void contrato_dentro_do_limite() {
        contratoEmContexto = novoContrato(ID_EVENTO, "forn-1", VIGENCIA_INICIO, VIGENCIA_FIM);
        despesaEmContexto = new Despesa(ID_EVENTO, CategoriaDespesa.SERVICO, "forn-1",
                new BigDecimal("1.00"), DATA_DENTRO_VIGENCIA, "gestor-sistema");
        VinculoConciliacao vinculo = new VinculoConciliacao(
                ID_EVENTO, despesaEmContexto.getId(), contratoEmContexto.getId(),
                MetodoConciliacao.AUTOMATICO, "sistema");
        when(contratoRepository.listarPorEventoId(ID_EVENTO)).thenReturn(List.of(contratoEmContexto));
        when(despesaRepository.listarPorEventoId(ID_EVENTO)).thenReturn(List.of(despesaEmContexto));
        when(vinculoRepository.listarPorEventoId(ID_EVENTO)).thenReturn(List.of(vinculo));
    }

    @Given("não há despesas elegíveis para conciliação nesse evento")
    public void nao_ha_despesas_elegiveis() {
        when(despesaRepository.listarPorEventoId(ID_EVENTO)).thenReturn(new ArrayList<>());
    }

    // --- Whens ---

    @When("o gestor executa a conciliação automática")
    public void gestor_executa_conciliacao_automatica() {
        try {
            conciliacaoService.executarConciliacaoAutomatica(ID_EVENTO, "gestor-1");
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("o gestor lista as despesas descobertas do evento")
    public void gestor_lista_despesas_descobertas() {
        try {
            despesasDescobertasRetornadas = conciliacaoService.listarDespesasDescobertasPorEvento(ID_EVENTO);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("o gestor lista os contratos extrapolados do evento")
    public void gestor_lista_contratos_extrapolados() {
        try {
            contratosExtrapoladosRetornados = conciliacaoService.listarContratosExtrapoladosPorEvento(ID_EVENTO);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("o gestor vincula manualmente a despesa ao contrato com responsável {string}")
    public void gestor_vincula_manualmente(String responsavel) {
        try {
            vinculoRetornado = conciliacaoService.vincularManualmente(
                    despesaEmContexto.getId(), contratoEmContexto.getId(), responsavel);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("o gestor tentar vincular manualmente a despesa ao contrato encerrado")
    public void gestor_tenta_vincular_encerrado() {
        gestor_vincula_manualmente("gestor-1");
    }

    @When("o gestor tentar vincular manualmente a despesa ao contrato de outro evento")
    public void gestor_tenta_vincular_outro_evento() {
        gestor_vincula_manualmente("gestor-1");
    }

    @When("o gestor tentar vincular manualmente essa despesa ao contrato")
    public void gestor_tenta_vincular_fora_vigencia() {
        gestor_vincula_manualmente("gestor-1");
    }

    @When("o gestor substitui o vínculo manualmente com responsável {string}")
    public void gestor_substitui_vinculo(String responsavel) {
        gestor_vincula_manualmente(responsavel);
    }

    @When("o gestor gera o relatório de conciliação do evento com responsável {string}")
    public void gestor_gera_relatorio(String responsavel) {
        try {
            relatorioRetornado = conciliacaoService.gerarRelatorio(ID_EVENTO, responsavel);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("o gestor tentar gerar o relatório do evento")
    public void gestor_tenta_gerar_relatorio() {
        gestor_gera_relatorio("gestor-1");
    }

    // --- Thens ---

    @Then("a despesa é vinculada ao contrato pelo método automático")
    public void despesa_vinculada_automaticamente() {
        assertNull(excecaoLancada);
        verify(vinculoRepository).salvar(any(VinculoConciliacao.class));
    }

    @Then("a despesa não possui cobertura contratual")
    public void despesa_sem_cobertura() {
        assertNull(excecaoLancada);
        verify(vinculoRepository).removerPorDespesaId(despesaEmContexto.getId());
    }

    @Then("a lista contém a despesa sem cobertura")
    public void lista_contem_despesa_sem_cobertura() {
        assertNull(excecaoLancada);
        assertNotNull(despesasDescobertasRetornadas);
        assertTrue(despesasDescobertasRetornadas.stream()
                .anyMatch(d -> d.getId().equals(despesaDescobertaEmContexto.getId())));
    }

    @Then("a lista de despesas descobertas está vazia")
    public void lista_despesas_descobertas_vazia() {
        assertNull(excecaoLancada);
        assertNotNull(despesasDescobertasRetornadas);
        assertTrue(despesasDescobertasRetornadas.isEmpty());
    }

    @Then("o contrato aparece na lista de extrapolados")
    public void contrato_aparece_extrapolados() {
        assertNull(excecaoLancada);
        assertNotNull(contratosExtrapoladosRetornados);
        assertTrue(contratosExtrapoladosRetornados.stream()
                .anyMatch(c -> c.getId().equals(contratoEmContexto.getId())));
    }

    @Then("a lista de contratos extrapolados está vazia")
    public void lista_extrapolados_vazia() {
        assertNull(excecaoLancada);
        assertNotNull(contratosExtrapoladosRetornados);
        assertTrue(contratosExtrapoladosRetornados.isEmpty());
    }

    @Then("o vínculo é registrado como manual com o responsável {string}")
    public void vinculo_registrado_manual(String responsavel) {
        assertNull(excecaoLancada);
        assertNotNull(vinculoRetornado);
        assertEquals(MetodoConciliacao.MANUAL, vinculoRetornado.getMetodo());
        assertEquals(responsavel, vinculoRetornado.getResponsavelId());
    }

    @Then("o vínculo é atualizado para manual com o responsável {string}")
    public void vinculo_atualizado_manual(String responsavel) {
        vinculo_registrado_manual(responsavel);
    }

    @Then("o sistema deve impedir o vínculo manual")
    public void sistema_impede_vinculo_manual() {
        assertNotNull(excecaoLancada);
    }

    @Then("o relatório contém itens cobertos e descobertos")
    public void relatorio_contem_cobertos_e_descobertos() {
        assertNull(excecaoLancada);
        assertNotNull(relatorioRetornado);
        List<ItemRelatorioConciliacao> itens = relatorioRetornado.getItens();
        assertTrue(itens.stream().anyMatch(i -> i.status() == StatusConciliacao.COBERTA));
        assertTrue(itens.stream().anyMatch(i -> i.status() == StatusConciliacao.DESCOBERTA));
    }

    @Then("o relatório registra o responsável {string} e a data de geração")
    public void relatorio_registra_responsavel(String responsavel) {
        assertNotNull(relatorioRetornado);
        assertEquals(responsavel, relatorioRetornado.getResponsavelId());
        assertNotNull(relatorioRetornado.getDataGeracao());
    }

    @Then("o relatório é salvo pelo repositório")
    public void relatorio_salvo() {
        assertNull(excecaoLancada);
        verify(relatorioRepository).salvar(any(RelatorioConciliacao.class));
    }

    @Then("o sistema deve impedir a geração do relatório")
    public void sistema_impede_geracao_relatorio() {
        assertNotNull(excecaoLancada);
    }

    // --- Helpers ---

    private Contrato novoContrato(String eventoId, String fornecedorId,
            LocalDateTime inicio, LocalDateTime fim) {
        List<DadosParteContrato> partes = List.of(
                new DadosParteContrato("Organização Evento", "Contratante"),
                new DadosParteContrato("Fornecedor Parceiro", "Fornecedor"));
        Contrato contrato = new Contrato(eventoId, TipoContrato.FORNECEDOR,
                "Objeto do contrato de teste", new BigDecimal("10000.00"), inicio, fim, partes);
        contrato.definirFornecedor(fornecedorId);
        return contrato;
    }
}
