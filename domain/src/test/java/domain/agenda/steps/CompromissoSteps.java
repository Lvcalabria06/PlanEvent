package domain.agenda.steps;

import domain.agenda.entity.Compromisso;
import domain.agenda.entity.Lembrete;
import domain.agenda.repository.CompromissoRepository;
import domain.agenda.repository.LembreteRepository;
import domain.agenda.service.CompromissoService;
import domain.agenda.service.CompromissoServiceImpl;
import domain.agenda.service.LembreteService;
import domain.agenda.service.LembreteServiceImpl;
import domain.agenda.valueobject.StatusCompromisso;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CompromissoSteps {

    private static final String ID_GESTOR = "gestor-artur-1";
    private static final String TITULO_PADRAO = "Reunião de planejamento do evento";

    private CompromissoRepository compromissoRepository;
    private LembreteRepository lembreteRepository;
    private CompromissoService compromissoService;
    private LembreteService lembreteService;

    private Exception excecaoLancada;
    private Compromisso compromissoEmContexto;
    private Compromisso segundoCompromisso;
    private Compromisso compromissoRetornadoBusca;
    private List<Compromisso> listaCompromissosRetornada;

    private Lembrete lembreteEmContexto;
    private Lembrete lembreteRetornadoBusca;
    private List<Lembrete> listaLembretesRetornada;

    @Before
    public void setup() {
        compromissoRepository = Mockito.mock(CompromissoRepository.class);
        lembreteRepository = Mockito.mock(LembreteRepository.class);
        compromissoService = new CompromissoServiceImpl(compromissoRepository, lembreteRepository);
        lembreteService = new LembreteServiceImpl(lembreteRepository, compromissoRepository);

        excecaoLancada = null;
        compromissoEmContexto = null;
        segundoCompromisso = null;
        compromissoRetornadoBusca = null;
        listaCompromissosRetornada = null;
        lembreteEmContexto = null;
        lembreteRetornadoBusca = null;
        listaLembretesRetornada = null;

        when(compromissoRepository.salvar(any(Compromisso.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(lembreteRepository.salvar(any(Lembrete.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }



    @Given("existe um gestor válido para agenda")
    public void existe_um_gestor_valido_para_agenda() {
        when(compromissoRepository.listarPorGestorId(ID_GESTOR)).thenReturn(new ArrayList<>());
    }

    @Given("existe um compromisso cadastrado para esse gestor")
    public void existe_um_compromisso_cadastrado_para_esse_gestor() {
        compromissoEmContexto = novoCompromissoValido(ID_GESTOR);
        when(compromissoRepository.buscarPorId(eq(compromissoEmContexto.getId())))
                .thenReturn(Optional.of(compromissoEmContexto));

        List<Compromisso> lista = new ArrayList<>();
        lista.add(compromissoEmContexto);
        when(compromissoRepository.listarPorGestorId(ID_GESTOR)).thenReturn(lista);
        when(lembreteRepository.listarPorCompromissoId(compromissoEmContexto.getId()))
                .thenReturn(new ArrayList<>());
    }

    @Given("existe um compromisso concluído para esse gestor")
    public void existe_um_compromisso_concluido_para_esse_gestor() {
        existe_um_compromisso_cadastrado_para_esse_gestor();
        compromissoEmContexto.concluir();
    }

    @Given("existe um compromisso em andamento para esse gestor")
    public void existe_um_compromisso_em_andamento_para_esse_gestor() {
        existe_um_compromisso_cadastrado_para_esse_gestor();
        compromissoEmContexto.iniciar();
    }

    @Given("existem dois compromissos cadastrados para esse gestor")
    public void existem_dois_compromissos_cadastrados_para_esse_gestor() {
        compromissoEmContexto = novoCompromissoValido(ID_GESTOR);
        LocalDateTime inicio2 = LocalDateTime.now().plusDays(5);
        LocalDateTime fim2 = inicio2.plusHours(2);
        segundoCompromisso = new Compromisso(ID_GESTOR, "Segunda reunião", "Desc", inicio2, fim2);

        when(compromissoRepository.buscarPorId(eq(compromissoEmContexto.getId())))
                .thenReturn(Optional.of(compromissoEmContexto));
        when(compromissoRepository.buscarPorId(eq(segundoCompromisso.getId())))
                .thenReturn(Optional.of(segundoCompromisso));

        List<Compromisso> lista = new ArrayList<>();
        lista.add(compromissoEmContexto);
        lista.add(segundoCompromisso);
        when(compromissoRepository.listarPorGestorId(ID_GESTOR)).thenReturn(lista);
    }

    @Given("existe um compromisso com lembretes cadastrado para esse gestor")
    public void existe_um_compromisso_com_lembretes_cadastrado_para_esse_gestor() {
        existe_um_compromisso_cadastrado_para_esse_gestor();
        LocalDateTime horarioLembrete = compromissoEmContexto.getDataInicio().minusMinutes(30);
        lembreteEmContexto = new Lembrete(compromissoEmContexto.getId(), horarioLembrete,
                compromissoEmContexto.getDataInicio());

        List<Lembrete> lembretes = new ArrayList<>();
        lembretes.add(lembreteEmContexto);
        when(lembreteRepository.listarPorCompromissoId(compromissoEmContexto.getId())).thenReturn(lembretes);
    }



    @Given("existe um lembrete cadastrado para esse compromisso")
    public void existe_um_lembrete_cadastrado_para_esse_compromisso() {
        LocalDateTime horarioLembrete = compromissoEmContexto.getDataInicio().minusMinutes(30);
        lembreteEmContexto = new Lembrete(compromissoEmContexto.getId(), horarioLembrete,
                compromissoEmContexto.getDataInicio());

        when(lembreteRepository.buscarPorId(eq(lembreteEmContexto.getId())))
                .thenReturn(Optional.of(lembreteEmContexto));

        List<Lembrete> lembretes = new ArrayList<>();
        lembretes.add(lembreteEmContexto);
        when(lembreteRepository.listarPorCompromissoId(compromissoEmContexto.getId())).thenReturn(lembretes);
        when(compromissoRepository.buscarPorId(eq(compromissoEmContexto.getId())))
                .thenReturn(Optional.of(compromissoEmContexto));
    }

    @Given("existe um lembrete notificado para esse compromisso")
    public void existe_um_lembrete_notificado_para_esse_compromisso() {
        existe_um_lembrete_cadastrado_para_esse_compromisso();
        lembreteEmContexto.marcarComoNotificado();
    }

    @Given("existe um lembrete cadastrado para esse compromisso finalizado")
    public void existe_um_lembrete_cadastrado_para_esse_compromisso_finalizado() {
        LocalDateTime horarioLembrete = compromissoEmContexto.getDataInicio().minusMinutes(30);
        lembreteEmContexto = new Lembrete(compromissoEmContexto.getId(), horarioLembrete,
                compromissoEmContexto.getDataInicio());

        when(lembreteRepository.buscarPorId(eq(lembreteEmContexto.getId())))
                .thenReturn(Optional.of(lembreteEmContexto));
        when(compromissoRepository.buscarPorId(eq(compromissoEmContexto.getId())))
                .thenReturn(Optional.of(compromissoEmContexto));
    }



    @When("eu cadastrar um compromisso completo para esse gestor")
    public void eu_cadastrar_um_compromisso_completo_para_esse_gestor() {
        try {
            Compromisso novo = novoCompromissoValido(ID_GESTOR);
            when(compromissoRepository.listarPorGestorId(ID_GESTOR)).thenReturn(new ArrayList<>());
            compromissoEmContexto = compromissoService.criarCompromisso(novo);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu tentar cadastrar compromisso sem gestor")
    public void eu_tentar_cadastrar_compromisso_sem_gestor() {
        try {
            LocalDateTime inicio = LocalDateTime.now().plusDays(1);
            LocalDateTime fim = inicio.plusHours(2);
            new Compromisso(null, TITULO_PADRAO, "Descrição", inicio, fim);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu tentar cadastrar compromisso sem título")
    public void eu_tentar_cadastrar_compromisso_sem_titulo() {
        try {
            LocalDateTime inicio = LocalDateTime.now().plusDays(1);
            LocalDateTime fim = inicio.plusHours(2);
            new Compromisso(ID_GESTOR, null, "Descrição", inicio, fim);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu tentar cadastrar compromisso com horário de fim anterior ao início")
    public void eu_tentar_cadastrar_compromisso_com_horario_fim_anterior_inicio() {
        try {
            LocalDateTime inicio = LocalDateTime.now().plusDays(1);
            LocalDateTime fim = inicio.minusHours(1);
            new Compromisso(ID_GESTOR, TITULO_PADRAO, "Descrição", inicio, fim);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu tentar cadastrar compromisso em data passada")
    public void eu_tentar_cadastrar_compromisso_em_data_passada() {
        try {
            LocalDateTime inicio = LocalDateTime.now().minusDays(1);
            LocalDateTime fim = inicio.plusHours(2);
            new Compromisso(ID_GESTOR, TITULO_PADRAO, "Descrição", inicio, fim);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu tentar cadastrar compromisso com sobreposição de horário")
    public void eu_tentar_cadastrar_compromisso_com_sobreposicao_de_horario() {
        try {
            LocalDateTime inicio = compromissoEmContexto.getDataInicio().plusMinutes(30);
            LocalDateTime fim = compromissoEmContexto.getDataFim().plusMinutes(30);
            Compromisso sobreposto = new Compromisso(ID_GESTOR, "Sobreposto", "Desc", inicio, fim);
            compromissoService.criarCompromisso(sobreposto);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }



    @When("eu editar o título desse compromisso para {string}")
    public void eu_editar_o_titulo_desse_compromisso_para(String novoTitulo) {
        try {
            compromissoEmContexto.editar(
                    novoTitulo,
                    compromissoEmContexto.getDescricao(),
                    compromissoEmContexto.getDataInicio(),
                    compromissoEmContexto.getDataFim());
            compromissoService.editarCompromisso(compromissoEmContexto);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu tentar editar um compromisso inexistente")
    public void eu_tentar_editar_um_compromisso_inexistente() {
        try {
            Compromisso orphan = novoCompromissoValido(ID_GESTOR);
            when(compromissoRepository.buscarPorId(orphan.getId())).thenReturn(Optional.empty());
            compromissoService.editarCompromisso(orphan);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu tentar editar esse compromisso concluído")
    public void eu_tentar_editar_esse_compromisso_concluido() {
        try {
            compromissoEmContexto.editar(
                    "Tentativa de edição",
                    compromissoEmContexto.getDescricao(),
                    compromissoEmContexto.getDataInicio(),
                    compromissoEmContexto.getDataFim());
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu tentar editar o segundo compromisso para o horário do primeiro")
    public void eu_tentar_editar_o_segundo_compromisso_para_horario_do_primeiro() {
        try {
            segundoCompromisso.editar(
                    segundoCompromisso.getTitulo(),
                    segundoCompromisso.getDescricao(),
                    compromissoEmContexto.getDataInicio(),
                    compromissoEmContexto.getDataFim());
            compromissoService.editarCompromisso(segundoCompromisso);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }



    @When("eu remover esse compromisso")
    public void eu_remover_esse_compromisso() {
        try {
            compromissoService.removerCompromisso(compromissoEmContexto.getId());
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu tentar remover esse compromisso em andamento")
    public void eu_tentar_remover_esse_compromisso_em_andamento() {
        try {
            compromissoService.removerCompromisso(compromissoEmContexto.getId());
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu tentar remover um compromisso inexistente")
    public void eu_tentar_remover_um_compromisso_inexistente() {
        try {
            when(compromissoRepository.buscarPorId(any())).thenReturn(Optional.empty());
            compromissoService.removerCompromisso("id-inexistente");
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }



    @When("eu buscar esse compromisso pelo id")
    public void eu_buscar_esse_compromisso_pelo_id() {
        try {
            compromissoRetornadoBusca = compromissoService.buscarCompromisso(compromissoEmContexto.getId());
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu listar compromissos desse gestor")
    public void eu_listar_compromissos_desse_gestor() {
        try {
            listaCompromissosRetornada = compromissoService.listarCompromissosPorGestor(ID_GESTOR);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu tentar buscar compromisso por id inexistente")
    public void eu_tentar_buscar_compromisso_por_id_inexistente() {
        try {
            when(compromissoRepository.buscarPorId(any())).thenReturn(Optional.empty());
            compromissoService.buscarCompromisso("id-inexistente");
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }



    @When("eu criar um lembrete para esse compromisso")
    public void eu_criar_um_lembrete_para_esse_compromisso() {
        try {
            LocalDateTime horario = compromissoEmContexto.getDataInicio().minusMinutes(15);
            Lembrete lembrete = new Lembrete(compromissoEmContexto.getId(), horario,
                    compromissoEmContexto.getDataInicio());
            lembreteEmContexto = lembreteService.criarLembrete(lembrete);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu tentar criar lembrete para compromisso inexistente")
    public void eu_tentar_criar_lembrete_para_compromisso_inexistente() {
        try {
            when(compromissoRepository.buscarPorId(any())).thenReturn(Optional.empty());
            LocalDateTime horario = LocalDateTime.now().plusDays(1);
            Lembrete lembrete = new Lembrete("inexistente-id", horario, horario.plusHours(1));
            lembreteService.criarLembrete(lembrete);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu tentar criar lembrete com horário posterior ao compromisso")
    public void eu_tentar_criar_lembrete_com_horario_posterior_ao_compromisso() {
        try {
            LocalDateTime horarioDepois = compromissoEmContexto.getDataInicio().plusHours(1);
            new Lembrete(compromissoEmContexto.getId(), horarioDepois,
                    compromissoEmContexto.getDataInicio());
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu tentar criar lembrete com o mesmo horário")
    public void eu_tentar_criar_lembrete_com_o_mesmo_horario() {
        try {
            Lembrete duplicado = new Lembrete(compromissoEmContexto.getId(),
                    lembreteEmContexto.getHorario(), compromissoEmContexto.getDataInicio());
            lembreteService.criarLembrete(duplicado);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu tentar criar lembrete para compromisso finalizado")
    public void eu_tentar_criar_lembrete_para_compromisso_finalizado() {
        try {
            when(compromissoRepository.buscarPorId(eq(compromissoEmContexto.getId())))
                    .thenReturn(Optional.of(compromissoEmContexto));

            LocalDateTime horario = compromissoEmContexto.getDataInicio().minusMinutes(15);
            Lembrete lembrete = new Lembrete(compromissoEmContexto.getId(), horario,
                    compromissoEmContexto.getDataInicio());
            lembreteService.criarLembrete(lembrete);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }



    @When("eu editar o horário desse lembrete")
    public void eu_editar_o_horario_desse_lembrete() {
        try {
            LocalDateTime novoHorario = compromissoEmContexto.getDataInicio().minusMinutes(45);
            Lembrete editado = new Lembrete(compromissoEmContexto.getId(), novoHorario,
                    compromissoEmContexto.getDataInicio());
            when(lembreteRepository.buscarPorId(eq(lembreteEmContexto.getId())))
                    .thenReturn(Optional.of(lembreteEmContexto));
            lembreteEmContexto.editar(novoHorario, compromissoEmContexto.getDataInicio());
            lembreteRepository.salvar(lembreteEmContexto);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu tentar editar esse lembrete notificado")
    public void eu_tentar_editar_esse_lembrete_notificado() {
        try {
            LocalDateTime novoHorario = compromissoEmContexto.getDataInicio().minusMinutes(45);
            lembreteEmContexto.editar(novoHorario, compromissoEmContexto.getDataInicio());
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu tentar editar lembrete de compromisso finalizado")
    public void eu_tentar_editar_lembrete_de_compromisso_finalizado() {
        try {
            LocalDateTime novoHorario = compromissoEmContexto.getDataInicio().minusMinutes(45);
            Lembrete editado = new Lembrete(compromissoEmContexto.getId(), novoHorario,
                    compromissoEmContexto.getDataInicio());

            when(lembreteRepository.buscarPorId(eq(lembreteEmContexto.getId())))
                    .thenReturn(Optional.of(lembreteEmContexto));

            lembreteService.editarLembrete(lembreteEmContexto);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }



    @When("eu listar lembretes desse compromisso")
    public void eu_listar_lembretes_desse_compromisso() {
        try {
            when(compromissoRepository.buscarPorId(eq(compromissoEmContexto.getId())))
                    .thenReturn(Optional.of(compromissoEmContexto));
            listaLembretesRetornada = lembreteService.listarLembretesPorCompromisso(compromissoEmContexto.getId());
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu tentar listar lembretes de compromisso inexistente")
    public void eu_tentar_listar_lembretes_de_compromisso_inexistente() {
        try {
            when(compromissoRepository.buscarPorId(any())).thenReturn(Optional.empty());
            lembreteService.listarLembretesPorCompromisso("inexistente-id");
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }



    @When("eu excluir esse lembrete")
    public void eu_excluir_esse_lembrete() {
        try {
            lembreteService.removerLembrete(lembreteEmContexto.getId());
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu tentar excluir lembrete inexistente")
    public void eu_tentar_excluir_lembrete_inexistente() {
        try {
            when(lembreteRepository.buscarPorId(any())).thenReturn(Optional.empty());
            lembreteService.removerLembrete("id-inexistente");
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }



    @Then("o compromisso é salvo com sucesso")
    @Then("o compromisso é atualizado com sucesso")
    public void operacao_compromisso_sucesso() {
        assertNull(excecaoLancada, () -> "Não deveria ter lançado exceção: "
                + (excecaoLancada != null ? excecaoLancada.getMessage() : ""));
    }

    @Then("o sistema deve impedir o cadastro do compromisso")
    @Then("o sistema deve impedir a edição do compromisso")
    @Then("o sistema deve impedir a remoção do compromisso")
    @Then("o sistema deve impedir a visualização do compromisso")
    public void operacao_compromisso_impedida() {
        assertNotNull(excecaoLancada, "Era esperada uma exceção.");
    }

    @Then("o compromisso retornado contém o título esperado")
    public void o_compromisso_retornado_contem_o_titulo_esperado() {
        assertNull(excecaoLancada);
        assertNotNull(compromissoRetornadoBusca);
        assertEquals(TITULO_PADRAO, compromissoRetornadoBusca.getTitulo());
    }

    @Then("a lista contém ao menos um compromisso")
    public void a_lista_contem_ao_menos_um_compromisso() {
        assertNull(excecaoLancada);
        assertNotNull(listaCompromissosRetornada);
        assertFalse(listaCompromissosRetornada.isEmpty());
    }

    @Then("o compromisso é removido com sucesso")
    public void o_compromisso_e_removido_com_sucesso() {
        assertNull(excecaoLancada, () -> "Não deveria ter lançado exceção: "
                + (excecaoLancada != null ? excecaoLancada.getMessage() : ""));
    }

    @Then("os lembretes vinculados são removidos")
    public void os_lembretes_vinculados_sao_removidos() {
        verify(lembreteRepository, times(1)).removerPorCompromissoId(compromissoEmContexto.getId());
    }



    @Then("o lembrete é salvo com sucesso")
    @Then("o lembrete é atualizado com sucesso")
    public void operacao_lembrete_sucesso() {
        assertNull(excecaoLancada, () -> "Não deveria ter lançado exceção: "
                + (excecaoLancada != null ? excecaoLancada.getMessage() : ""));
    }

    @Then("o sistema deve impedir o cadastro do lembrete")
    @Then("o sistema deve impedir a edição do lembrete")
    @Then("o sistema deve impedir a exclusão do lembrete")
    @Then("o sistema deve impedir a visualização dos lembretes")
    public void operacao_lembrete_impedida() {
        assertNotNull(excecaoLancada, "Era esperada uma exceção.");
    }

    @Then("a lista contém ao menos um lembrete")
    public void a_lista_contem_ao_menos_um_lembrete() {
        assertNull(excecaoLancada);
        assertNotNull(listaLembretesRetornada);
        assertFalse(listaLembretesRetornada.isEmpty());
    }

    @Then("o lembrete é removido com sucesso")
    public void o_lembrete_e_removido_com_sucesso() {
        assertNull(excecaoLancada, () -> "Não deveria ter lançado exceção: "
                + (excecaoLancada != null ? excecaoLancada.getMessage() : ""));
    }



    private Compromisso novoCompromissoValido(String gestorId) {
        LocalDateTime inicio = LocalDateTime.now().plusDays(1);
        LocalDateTime fim = inicio.plusHours(2);
        return new Compromisso(gestorId, TITULO_PADRAO, "Descrição do compromisso", inicio, fim);
    }
}
