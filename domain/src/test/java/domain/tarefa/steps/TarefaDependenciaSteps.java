package domain.tarefa.steps;

import domain.equipe.entity.Equipe;
import domain.equipe.repository.EquipeRepository;
import domain.funcionario.repository.FuncionarioRepository;
import domain.tarefa.entity.ResponsavelTarefa;
import domain.tarefa.entity.Tarefa;
import domain.tarefa.repository.ResponsavelTarefaRepository;
import domain.tarefa.repository.TarefaRepository;
import domain.tarefa.service.DependenciaService;
import domain.tarefa.service.DependenciaServiceImpl;
import domain.tarefa.service.TarefaService;
import domain.tarefa.service.TarefaServiceImpl;
import domain.evento.repository.EventoRepository;
import domain.tarefa.valueobject.StatusTarefa;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import org.mockito.Mockito;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class TarefaDependenciaSteps {

    private TarefaRepository tarefaRepository;
    private EquipeRepository equipeRepository;
    private ResponsavelTarefaRepository responsavelTarefaRepository;

    private DependenciaService dependenciaService;
    private TarefaService tarefaService;

    private Exception excecaoLancada;

    private Tarefa tarefaA;
    private Tarefa tarefaB;
    private Tarefa tarefaC;

    private List<Tarefa> resultadoBuscaDependencias;

    @Before
    public void setup() {
        tarefaRepository = Mockito.mock(TarefaRepository.class);
        equipeRepository = Mockito.mock(EquipeRepository.class);
        responsavelTarefaRepository = Mockito.mock(ResponsavelTarefaRepository.class);

        dependenciaService = new DependenciaServiceImpl(tarefaRepository, equipeRepository);
        tarefaService = new TarefaServiceImpl(
            tarefaRepository, 
            equipeRepository, 
            Mockito.mock(EventoRepository.class), 
            Mockito.mock(FuncionarioRepository.class), 
            responsavelTarefaRepository
        );

        TarefaSteps.excecaoLancada = null;
        resultadoBuscaDependencias = new ArrayList<>();

        when(tarefaRepository.salvar(any(Tarefa.class))).thenAnswer(i -> i.getArgument(0));

        when(responsavelTarefaRepository.listarPorTarefa(any())).thenReturn(
            Collections.singletonList(new ResponsavelTarefa("t", "f"))
        );
    }

    @Given("existem duas tarefas do mesmo evento")
    public void existem_duas_tarefas_do_mesmo_evento() {
        Equipe eq1 = new Equipe("evento-1", "EQ", (String) null);
        when(equipeRepository.buscarPorId("eq-1")).thenReturn(Optional.of(eq1));

        tarefaA = new Tarefa("eq-1", "A", "Desc", null, null);
        tarefaB = new Tarefa("eq-1", "B", "Desc", null, null);

        mockTarefas(tarefaA, tarefaB);
    }

    @Given("existem duas tarefas de eventos diferentes")
    public void existem_duas_tarefas_de_eventos_diferentes() {
        Equipe eq1 = new Equipe("evento-1", "EQ", (String) null);
        Equipe eq2 = new Equipe("evento-2", "EQ", (String) null);
        when(equipeRepository.buscarPorId("eq-1")).thenReturn(Optional.of(eq1));
        when(equipeRepository.buscarPorId("eq-2")).thenReturn(Optional.of(eq2));

        tarefaA = new Tarefa("eq-1", "A", "Desc", null, null);
        tarefaB = new Tarefa("eq-2", "B", "Desc", null, null);

        mockTarefas(tarefaA, tarefaB);
    }

    @Given("existe uma tarefa A que depende da tarefa B")
    public void existe_uma_tarefa_A_que_depende_da_tarefa_B() {
        existem_duas_tarefas_do_mesmo_evento();
        tarefaA.adicionarDependencia(tarefaB.getId());
    }

    @Given("A depende de B")
    public void a_depende_de_b() {
        existem_duas_tarefas_do_mesmo_evento();
        tarefaA.adicionarDependencia(tarefaB.getId());
    }

    @Given("B depende de C")
    public void b_depende_de_c() {
        tarefaC = new Tarefa("eq-1", "C", "Desc", null, null);
        when(tarefaRepository.buscarPorId(tarefaC.getId())).thenReturn(Optional.of(tarefaC));
        tarefaB.adicionarDependencia(tarefaC.getId());
    }

    @Given("existem tarefas A, B e C do mesmo evento")
    public void existem_tarefas_A_B_e_C_do_mesmo_evento() {
        existem_duas_tarefas_do_mesmo_evento();
        tarefaC = new Tarefa("eq-1", "C", "Desc", null, null);
        mockTarefas(tarefaA, tarefaB, tarefaC);
    }

    @Given("a tarefa A termina após a tarefa B iniciar")
    public void a_tarefa_A_termina_apos_a_tarefa_B_iniciar() {
        existem_duas_tarefas_do_mesmo_evento();
        tarefaA.atualizarDetalhes("A", "D", LocalDateTime.now(), LocalDateTime.now().plusDays(5));
        tarefaB.atualizarDetalhes("B", "D", LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(10));
    }

    @Given("existe uma tarefa A")
    public void existe_uma_tarefa_A() {
        existem_duas_tarefas_do_mesmo_evento(); 
    }

    @Given("a tarefa B depende da tarefa A")
    public void a_tarefa_b_depende_da_tarefa_a() {
        existem_duas_tarefas_do_mesmo_evento();
        tarefaB.adicionarDependencia(tarefaA.getId());
        when(tarefaRepository.listarDependentes(tarefaA.getId())).thenReturn(Arrays.asList(tarefaB));
    }

    @Given("a tarefa A tem sua data de conclusão alterada para depois do início de B")
    public void a_tarefa_A_tem_sua_data_de_conclusao_alterada_para_depois_do_inicio_de_B() {
        tarefaB.atualizarDetalhes("B", "D", LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(5));
        tarefaA.atualizarDetalhes("A", "D", LocalDateTime.now(), LocalDateTime.now().plusDays(10));
    }

    @Given("a tarefa A está concluída")
    public void a_tarefa_a_esta_concluida() {
        tarefaA.iniciar();
        tarefaA.concluir();
        when(tarefaRepository.listarPorIds(any())).thenReturn(Collections.singletonList(tarefaA));
    }

    @Given("a tarefa A não está concluída")
    public void a_tarefa_a_nao_esta_concluida() {
        when(tarefaRepository.listarPorIds(any())).thenReturn(Collections.singletonList(tarefaA));
    }

    @Given("existe uma dependência entre tarefa A e tarefa B")
    public void existe_uma_dependencia_entre_tarefa_A_e_tarefa_B() {
        a_tarefa_b_depende_da_tarefa_a();
    }

    @Given("a dependência foi removida")
    public void a_dependencia_foi_removida() {
        tarefaB.removerDependencia(tarefaA.getId());
        when(tarefaRepository.listarDependentes(tarefaA.getId())).thenReturn(new ArrayList<>());
    }

    @Given("a tarefa C depende das tarefas A e B")
    public void a_tarefa_c_depende_das_tarefas_a_e_b() {
        existem_tarefas_A_B_e_C_do_mesmo_evento();
        tarefaC.adicionarDependencia(tarefaA.getId());
        tarefaC.adicionarDependencia(tarefaB.getId());
        when(tarefaRepository.listarPorIds(tarefaC.listarDependencias())).thenReturn(Arrays.asList(tarefaA, tarefaB));
    }

    @Given("existe uma tarefa sem dependências")
    public void existe_uma_tarefa_sem_dependencias() {
        existem_duas_tarefas_do_mesmo_evento();
    }

    @Given("as tarefas B e C dependem da tarefa A")
    public void as_tarefas_b_e_c_dependem_da_tarefa_a() {
        existem_tarefas_A_B_e_C_do_mesmo_evento();
        tarefaB.adicionarDependencia(tarefaA.getId());
        tarefaC.adicionarDependencia(tarefaA.getId());
        when(tarefaRepository.listarDependentes(tarefaA.getId())).thenReturn(Arrays.asList(tarefaB, tarefaC));
    }

    @Given("existe uma tarefa sem dependentes")
    public void existe_uma_tarefa_sem_dependentes() {
        existem_duas_tarefas_do_mesmo_evento();
        when(tarefaRepository.listarDependentes(tarefaA.getId())).thenReturn(new ArrayList<>());
    }


    @When("eu definir que a tarefa B depende da tarefa A")
    @When("eu tentar definir uma dependência entre elas")
    @When("eu tentar fazer a tarefa B depender da tarefa A")
    @When("eu tentar fazer B depender de A")
    public void eu_definir_que_a_tarefa_B_depende_da_tarefa_A() {
        try {
            dependenciaService.adicionarDependencia(tarefaB.getId(), tarefaA.getId());
        } catch (Exception e) {
            TarefaSteps.excecaoLancada = e;
        }
    }

    @When("eu tentar fazer C depender de A")
    public void eu_tentar_fazer_C_depender_de_A() {
        try {
            dependenciaService.adicionarDependencia(tarefaC.getId(), tarefaA.getId());
        } catch (Exception e) {
            TarefaSteps.excecaoLancada = e;
        }
    }

    @When("eu definir que a tarefa C depende de A e B")
    public void eu_definir_que_a_tarefa_C_depende_de_A_e_B() {
        try {
            dependenciaService.adicionarDependencia(tarefaC.getId(), tarefaA.getId());
            dependenciaService.adicionarDependencia(tarefaC.getId(), tarefaB.getId());
        } catch (Exception e) {
            TarefaSteps.excecaoLancada = e;
        }
    }

    @When("eu tentar fazer A depender de A")
    public void eu_tentar_fazer_A_depender_de_A() {
        try {
            dependenciaService.adicionarDependencia(tarefaA.getId(), tarefaA.getId());
        } catch (Exception e) {
            TarefaSteps.excecaoLancada = e;
        }
    }

    @When("a alteração é realizada")
    public void a_alteracao_e_realizada() {
        try {
            tarefaService.editarTarefa(tarefaA);
        } catch (Exception e) {
            TarefaSteps.excecaoLancada = e;
        }
    }

    @When("eu iniciar a tarefa B")
    @When("eu tentar iniciar a tarefa B")
    public void eu_iniciar_a_tarefa_B() {
        try {
            tarefaService.iniciarTarefa(tarefaB.getId());
        } catch (Exception e) {
            TarefaSteps.excecaoLancada = e;
        }
    }

    @When("eu remover essa dependência")
    public void eu_remover_essa_dependencia() {
        try {
            dependenciaService.removerDependencia(tarefaB.getId(), tarefaA.getId());
        } catch (Exception e) {
            TarefaSteps.excecaoLancada = e;
        }
    }

    @When("eu tentar remover a tarefa A")
    @When("eu remover a tarefa A")
    public void eu_tentar_remover_a_tarefa_A() {
        try {
            tarefaService.removerTarefa(tarefaA.getId());
        } catch (Exception e) {
            TarefaSteps.excecaoLancada = e;
        }
    }

    @When("eu visualizar as dependências da tarefa C")
    @When("eu visualizar suas dependências")
    public void eu_visualizar_as_dependencias_da_tarefa_C() {
        try {
            resultadoBuscaDependencias = dependenciaService.listarDependencias(tarefaC != null ? tarefaC.getId() : tarefaA.getId());
        } catch (Exception e) {
            TarefaSteps.excecaoLancada = e;
        }
    }

    @When("eu visualizar tarefas dependentes de A")
    @When("eu visualizar tarefas dependentes dela")
    public void eu_visualizar_tarefas_dependentes_de_A() {
        try {
            resultadoBuscaDependencias = dependenciaService.listarDependentes(tarefaA.getId());
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }


    @Then("a dependência é criada com sucesso")
    @Then("as dependências são criadas com sucesso")
    @Then("a tarefa B é iniciada com sucesso")
    @Then("a dependência é removida com sucesso")
    public void a_dependencia_e_criada_com_sucesso() {
        assertNull(TarefaSteps.excecaoLancada, "Não era esperado erro, mas ocorreu: " + (TarefaSteps.excecaoLancada!=null ? TarefaSteps.excecaoLancada.getMessage() : ""));
    }

    @Then("a tarefa B deve ser marcada como potencialmente atrasada")
    public void a_tarefa_b_deve_ser_marcada_como_potencialmente_atrasada() {
        assertNotNull(TarefaSteps.excecaoLancada, "Como não temos um sistema de flag na tela, nossa forma de marcar é barrar edições que causem impacto sem verificação!");
        assertTrue(TarefaSteps.excecaoLancada.getMessage().contains("potencialmente atrasada"));
    }

    @Then("o sistema deve retornar A e B")
    public void o_sistema_deve_retornar_A_e_B() {
        assertEquals(2, resultadoBuscaDependencias.size());
        assertTrue(resultadoBuscaDependencias.contains(tarefaA));
        assertTrue(resultadoBuscaDependencias.contains(tarefaB));
    }

    @Then("o sistema deve retornar B e C")
    public void o_sistema_deve_retornar_B_e_C() {
        assertEquals(2, resultadoBuscaDependencias.size());
        assertTrue(resultadoBuscaDependencias.contains(tarefaB));
        assertTrue(resultadoBuscaDependencias.contains(tarefaC));
    }

    @Then("o sistema deve informar que não há dependências")
    @Then("o sistema deve informar que não há dependentes")
    public void o_sistema_deve_informar_que_nao_ha_dependencias() {
        assertTrue(resultadoBuscaDependencias.isEmpty());
    }


    private void mockTarefas(Tarefa... tarefas) {
        for (Tarefa t : tarefas) {
            when(tarefaRepository.buscarPorId(t.getId())).thenReturn(Optional.of(t));
        }
    }
}
