package domain.equipe.steps;

import domain.equipe.entity.Equipe;
import domain.equipe.entity.MembroEquipe;
import domain.equipe.repository.EquipeRepository;
import domain.equipe.service.EquipeService;
import domain.equipe.service.EquipeServiceImpl;
import domain.evento.entity.Evento;
import domain.evento.repository.EventoRepository;
import domain.funcionario.entity.Funcionario;
import domain.funcionario.repository.FuncionarioRepository;
import domain.tarefa.entity.Tarefa;
import domain.tarefa.repository.TarefaRepository;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class EquipeSteps {

    private static final String EVENTO_ID = "evento-1";
    private static final String EQUIPE_ID = "equipe-1";
    private static final String FUNCIONARIO_ID = "func-1";
    private static final String FUNCIONARIO_2_ID = "func-2";
    private static final String FUNCIONARIO_INVALIDO_ID = "funcionario-invalido";

    private EquipeRepository equipeRepository;
    private EventoRepository eventoRepository;
    private FuncionarioRepository funcionarioRepository;
    private TarefaRepository tarefaRepository;
    private EquipeService equipeService;

    private Exception excecaoLancada;
    private Equipe equipeEmContexto;
    private List<Equipe> equipesRetornadas;

    @Before
    public void setup() {
        equipeRepository = Mockito.mock(EquipeRepository.class);
        eventoRepository = Mockito.mock(EventoRepository.class);
        funcionarioRepository = Mockito.mock(FuncionarioRepository.class);
        tarefaRepository = Mockito.mock(TarefaRepository.class);

        equipeService = new EquipeServiceImpl(
                equipeRepository,
                eventoRepository,
                funcionarioRepository,
                tarefaRepository
        );

        excecaoLancada = null;
        equipeEmContexto = null;
        equipesRetornadas = null;

        when(equipeRepository.salvar(any(Equipe.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Given("o gestor informa um evento válido, nome único e funcionários válidos")
    public void o_gestor_informa_um_evento_valido_nome_unico_e_funcionarios_validos() {
        existe_um_evento_valido_para_equipe();
        existem_funcionarios_validos_para_equipe();
        nao_existe_equipe_com_o_mesmo_nome_no_evento();
    }

    @Given("existe um evento válido")
    @Given("existe um evento válido para equipe")
    public void existe_um_evento_valido_para_equipe() {
        when(eventoRepository.buscarPorId(any()))
                .thenReturn(Optional.of(Mockito.mock(Evento.class)));
    }

    @Given("não existe evento válido")
    @Given("não existe evento válido para equipe")
    public void nao_existe_evento_valido_para_equipe() {
        when(eventoRepository.buscarPorId(any()))
                .thenReturn(Optional.empty());
    }

    @Given("existe um evento válido e funcionários válidos")
    public void existe_um_evento_valido_e_funcionarios_validos() {
        existe_um_evento_valido_para_equipe();
        existem_funcionarios_validos_para_equipe();
    }

    @Given("existem funcionários válidos para equipe")
    public void existem_funcionarios_validos_para_equipe() {
        when(funcionarioRepository.buscarPorId(any()))
                .thenAnswer(invocation -> {
                    String id = invocation.getArgument(0);

                    if (FUNCIONARIO_INVALIDO_ID.equals(id)) {
                        return Optional.empty();
                    }

                    if (FUNCIONARIO_2_ID.equals(id)) {
                        return Optional.of(new Funcionario("João Pedro", "técnico", "tarde"));
                    }

                    return Optional.of(new Funcionario("Maria Silva", "garçom", "manhã"));
                });
    }

    @Given("não existe equipe com o mesmo nome no evento")
    public void nao_existe_equipe_com_o_mesmo_nome_no_evento() {
        when(equipeRepository.existeEquipeComNomeNoEvento(any(), any()))
                .thenReturn(false);
    }

    @Given("já existe uma equipe com o mesmo nome no evento")
    @Given("já existe equipe com o mesmo nome no evento")
    public void ja_existe_equipe_com_o_mesmo_nome_no_evento() {
        when(equipeRepository.existeEquipeComNomeNoEvento(any(), any()))
                .thenReturn(true);
    }

    @Given("os funcionários não estão alocados em outra equipe do evento")
    public void os_funcionarios_nao_estao_alocados_em_outra_equipe_do_evento() {
        when(equipeRepository.funcionarioJaEstaEmEquipeNoEvento(any(), any()))
                .thenReturn(false);
    }

    @Given("um funcionário já está alocado em outra equipe do mesmo evento")
    @Given("um funcionário já está alocado em outra equipe do evento")
    public void um_funcionario_ja_esta_alocado_em_outra_equipe_do_evento() {
        when(equipeRepository.funcionarioJaEstaEmEquipeNoEvento(any(), any()))
                .thenReturn(true);
    }

    @Given("a equipe possui no máximo um responsável válido pertencente à equipe")
    public void a_equipe_possui_no_maximo_um_responsavel_valido_pertencente_a_equipe() {
        equipeEmContexto = novaEquipeValida();
    }

    @Given("existe uma equipe cadastrada")
    @Given("existe uma equipe cadastrada para o evento")
    public void existe_uma_equipe_cadastrada_para_o_evento() {
        existe_um_evento_valido_para_equipe();
        existem_funcionarios_validos_para_equipe();
        os_funcionarios_nao_estao_alocados_em_outra_equipe_do_evento();

        equipeEmContexto = novaEquipeValida();

        when(equipeRepository.buscarPorId(any()))
                .thenReturn(Optional.of(equipeEmContexto));

        when(equipeRepository.existeEquipeComNomeNoEvento(any(), any()))
                .thenReturn(false);
    }

    @Given("o novo nome não está sendo utilizado no evento")
    @Given("não existe equipe com o novo nome no evento")
    public void nao_existe_equipe_com_o_novo_nome_no_evento() {
        when(equipeRepository.existeEquipeComNomeNoEvento(any(), any()))
                .thenReturn(false);
    }

    @Given("existe uma equipe cadastrada com líder e outro membro")
    @Given("existe uma equipe com um líder definido")
    public void existe_uma_equipe_cadastrada_com_lider_e_outro_membro() {
        existe_um_evento_valido_para_equipe();
        existem_funcionarios_validos_para_equipe();

        equipeEmContexto = new Equipe(
                EVENTO_ID,
                "Equipe Alpha",
                List.of(
                        new MembroEquipe(FUNCIONARIO_ID, true),
                        new MembroEquipe(FUNCIONARIO_2_ID, false)
                )
        );

        when(equipeRepository.buscarPorId(any()))
                .thenReturn(Optional.of(equipeEmContexto));
    }

    @Given("a equipe não possui tarefas em andamento")
    public void a_equipe_nao_possui_tarefas_em_andamento() {
        when(tarefaRepository.listarPorEquipeId(any()))
                .thenReturn(List.of());
    }

    @Given("a equipe possui tarefas em andamento")
    public void a_equipe_possui_tarefas_em_andamento() {
        when(tarefaRepository.listarPorEquipeId(any()))
                .thenReturn(List.of(Mockito.mock(Tarefa.class)));
    }

    @Given("existem equipes cadastradas para o evento")
    public void existem_equipes_cadastradas_para_o_evento() {
        when(equipeRepository.listarPorEventoId(EVENTO_ID))
                .thenReturn(List.of(novaEquipeValida()));
    }

    @When("o gestor cadastrar a equipe no sistema")
    @When("o gestor cadastrar uma equipe válida")
    public void o_gestor_cadastrar_uma_equipe_valida() {
        tentarCriarEquipe(this::novaEquipeValida);
    }

    @When("o gestor tentar cadastrar uma equipe")
    public void o_gestor_tentar_cadastrar_uma_equipe() {
        tentarCriarEquipe(this::novaEquipeValida);
    }

    @When("o gestor tentar cadastrar uma equipe sem funcionários")
    public void o_gestor_tentar_cadastrar_uma_equipe_sem_funcionarios() {
        tentarCriarEquipe(() -> new Equipe(EVENTO_ID, "Equipe Alpha", List.of()));
    }

    @When("o gestor tentar cadastrar uma equipe com mais de um responsável")
    @When("o gestor tentar cadastrar uma equipe com mais de um líder")
    public void o_gestor_tentar_cadastrar_uma_equipe_com_mais_de_um_lider() {
        tentarCriarEquipe(() -> new Equipe(
                EVENTO_ID,
                "Equipe Alpha",
                List.of(
                        new MembroEquipe(FUNCIONARIO_ID, true),
                        new MembroEquipe(FUNCIONARIO_2_ID, true)
                )
        ));
    }

    @When("o gestor tentar cadastrar uma equipe com responsável inválido")
    public void o_gestor_tentar_cadastrar_uma_equipe_com_responsavel_invalido() {
        tentarCriarEquipe(() -> new Equipe(
                EVENTO_ID,
                "Equipe Alpha",
                List.of(new MembroEquipe(FUNCIONARIO_INVALIDO_ID, true))
        ));
    }

    @When("o gestor editar o nome da equipe")
    public void o_gestor_editar_o_nome_da_equipe() {
        try {
            equipeEmContexto.alterarNome("Equipe Beta");
            equipeEmContexto = equipeService.editarEquipe(equipeEmContexto);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("o gestor tentar remover todos os funcionários da equipe")
    @When("o gestor tentar remover o único funcionário da equipe")
    public void o_gestor_tentar_remover_todos_os_funcionarios_da_equipe() {
        try {
            equipeEmContexto.removerMembro(FUNCIONARIO_ID, null);
            equipeService.editarEquipe(equipeEmContexto);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("o gestor tentar remover o líder sem definir novo responsável")
    @When("o gestor tentar remover o líder sem definir novo líder")
    @When("o gestor tentar remover o líder sem definir outro")
    public void o_gestor_tentar_remover_o_lider_sem_definir_novo_responsavel() {
        try {
            equipeEmContexto.removerMembro(FUNCIONARIO_ID, null);
            equipeService.editarEquipe(equipeEmContexto);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("o gestor remover a equipe")
    public void o_gestor_remover_a_equipe() {
        try {
            equipeService.removerEquipe(EQUIPE_ID);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("o gestor tentar remover a equipe")
    public void o_gestor_tentar_remover_a_equipe() {
        try {
            equipeService.removerEquipe(EQUIPE_ID);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("o gestor listar as equipes")
    @When("o gestor listar as equipes do evento")
    public void o_gestor_listar_as_equipes_do_evento() {
        try {
            equipesRetornadas = equipeService.listarEquipesPorEvento(EVENTO_ID);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @Then("a equipe é salva com sucesso")
    @Then("a equipe é atualizada com sucesso")
    @Then("a equipe é removida com sucesso")
    public void operacao_equipe_sucesso() {
        assertNull(excecaoLancada, "Não deveria lançar exceção, mas lançou: "
                + (excecaoLancada != null ? excecaoLancada.getMessage() : ""));
    }

    @Then("o sistema deve impedir o cadastro da equipe")
    @Then("o sistema deve impedir a edição da equipe")
    @Then("o sistema deve impedir a remoção da equipe")
    public void operacao_equipe_impedida() {
        assertNotNull(excecaoLancada, "Era esperada uma exceção.");
    }

    @Then("o sistema deve exibir as equipes do evento")
    public void o_sistema_deve_exibir_as_equipes_do_evento() {
        assertNull(excecaoLancada);
        assertNotNull(equipesRetornadas);
        assertFalse(equipesRetornadas.isEmpty());
    }

    private void tentarCriarEquipe(Supplier<Equipe> supplier) {
        try {
            equipeEmContexto = equipeService.criarEquipe(supplier.get());
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    private Equipe novaEquipeValida() {
        return new Equipe(
                EVENTO_ID,
                "Equipe Alpha",
                List.of(new MembroEquipe(FUNCIONARIO_ID, true))
        );
    }
}