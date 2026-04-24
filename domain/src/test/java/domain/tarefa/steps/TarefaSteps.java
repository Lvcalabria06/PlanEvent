package domain.tarefa.steps;

import domain.equipe.entity.Equipe;
import domain.equipe.repository.EquipeRepository;
import domain.evento.entity.Evento;
import domain.evento.repository.EventoRepository;
import domain.funcionario.entity.Funcionario;
import domain.funcionario.repository.FuncionarioRepository;
import domain.tarefa.entity.ResponsavelTarefa;
import domain.tarefa.entity.Tarefa;
import domain.tarefa.repository.ResponsavelTarefaRepository;
import domain.tarefa.repository.TarefaRepository;
import domain.tarefa.service.TarefaService;
import domain.tarefa.service.TarefaServiceImpl;
import domain.tarefa.valueobject.StatusTarefa;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import org.mockito.Mockito;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TarefaSteps {

    private TarefaRepository tarefaRepository;
    private EquipeRepository equipeRepository;
    private EventoRepository eventoRepository;
    private FuncionarioRepository funcionarioRepository;
    private ResponsavelTarefaRepository responsavelTarefaRepository;

    private TarefaService tarefaService;

    public static Exception excecaoLancada;
    private Tarefa tarefaEmContexto;
    private Equipe equipeEmContexto;
    private String idEquipeValida = "equipe-1";
    private String idEventoValido = "evento-1";
    private String idTarefaValida = "tarefa-1";
    private String idFuncionarioValido = "func-1";

    @Before
    public void setup() {
        tarefaRepository = Mockito.mock(TarefaRepository.class);
        equipeRepository = Mockito.mock(EquipeRepository.class);
        eventoRepository = Mockito.mock(EventoRepository.class);
        funcionarioRepository = Mockito.mock(FuncionarioRepository.class);
        responsavelTarefaRepository = Mockito.mock(ResponsavelTarefaRepository.class);

        tarefaService = new TarefaServiceImpl(
                tarefaRepository,
                equipeRepository,
                eventoRepository,
                funcionarioRepository,
                responsavelTarefaRepository
        );

        excecaoLancada = null;
        tarefaEmContexto = null;
        equipeEmContexto = null;
        
        when(tarefaRepository.salvar(any(Tarefa.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Given("existe um evento válido")
    public void existe_um_evento_valido() {
        when(eventoRepository.buscarPorId(idEventoValido)).thenReturn(Optional.of(Mockito.mock(Evento.class)));
    }

    @Given("existe uma equipe válida associada ao evento")
    @Given("existe uma equipe válida")
    public void existe_uma_equipe_valida_associada_ao_evento() {
        equipeEmContexto = new Equipe(idEventoValido, "Equipe Alpha", (String) null);
        when(equipeRepository.buscarPorId(any())).thenReturn(Optional.of(equipeEmContexto));
        when(eventoRepository.buscarPorId(any())).thenReturn(Optional.of(Mockito.mock(Evento.class)));
    }

    @Given("não existe equipe válida")
    public void nao_existe_equipe_valida() {
        when(equipeRepository.buscarPorId(any())).thenReturn(Optional.empty());
    }

    @Given("não existe tarefa com título {string} na equipe")
    public void nao_existe_tarefa_com_titulo_na_equipe(String titulo) {
        when(tarefaRepository.existePorTituloEEquipe(eq(titulo), any())).thenReturn(false);
    }
    
    @Given("já existe uma tarefa com título {string}")
    public void ja_existe_uma_tarefa_com_titulo(String titulo) {
        when(tarefaRepository.existePorTituloEEquipe(eq(titulo), any())).thenReturn(true);
    }

    @Given("existe uma tarefa pendente")
    @Given("existe uma tarefa")
    @Given("existe uma tarefa dessa equipe")
    public void existe_uma_tarefa_pendente() {
        tarefaEmContexto = new Tarefa(idEquipeValida, "Pendente", "Desc", LocalDateTime.now(), LocalDateTime.now().plusDays(2));
        when(tarefaRepository.buscarPorId(any())).thenReturn(Optional.of(tarefaEmContexto));
    }
    
    @Given("existe uma tarefa concluída")
    public void existe_uma_tarefa_concluida() {
        tarefaEmContexto = new Tarefa(idEquipeValida, "Concluída", "Desc", LocalDateTime.now(), LocalDateTime.now().plusDays(2));
        tarefaEmContexto.iniciar();
        tarefaEmContexto.concluir();
        when(tarefaRepository.buscarPorId(any())).thenReturn(Optional.of(tarefaEmContexto));
    }

    @Given("existe uma tarefa em andamento")
    public void existe_uma_tarefa_em_andamento() {
        tarefaEmContexto = new Tarefa(idEquipeValida, "Em Andamento", "Desc", LocalDateTime.now(), LocalDateTime.now().plusDays(2));
        tarefaEmContexto.iniciar();
        when(tarefaRepository.buscarPorId(any())).thenReturn(Optional.of(tarefaEmContexto));
    }

    @Given("existe uma tarefa com título {string}")
    public void existe_uma_tarefa_com_titulo(String titulo) {
        tarefaEmContexto = new Tarefa(idEquipeValida, titulo, "Desc", LocalDateTime.now(), LocalDateTime.now().plusDays(2));
        when(tarefaRepository.buscarPorId("tarefa-a")).thenReturn(Optional.of(tarefaEmContexto));
    }

    @Given("existe outra tarefa com título {string}")
    public void existe_outra_tarefa_com_titulo(String titulo) {
        Tarefa tarefaB = new Tarefa(idEquipeValida, titulo, "Desc", LocalDateTime.now(), LocalDateTime.now().plusDays(2));
        when(tarefaRepository.buscarPorId("tarefa-b")).thenReturn(Optional.of(tarefaB));
        when(tarefaRepository.existePorTituloEEquipe(eq("A"), any())).thenReturn(true); 
    }

    @Given("existe um funcionário atribuído à tarefa")
    public void existe_um_funcionario_atribuido_a_tarefa() {
        List<ResponsavelTarefa> lista = new ArrayList<>();
        lista.add(new ResponsavelTarefa(idTarefaValida, idFuncionarioValido));
        when(responsavelTarefaRepository.listarPorTarefa(any())).thenReturn(lista);
    }
    
    @Given("não há responsáveis atribuídos")
    public void nao_ha_responsaveis_atribuidos() {
        when(responsavelTarefaRepository.listarPorTarefa(any())).thenReturn(new ArrayList<>());
    }

    @Given("existe um funcionário que não pertence à equipe")
    public void existe_um_funcionario_que_nao_pertence_a_equipe() {
        when(funcionarioRepository.buscarPorId(any())).thenReturn(Optional.of(Mockito.mock(Funcionario.class)));
        Equipe equipeDesvinculada = new Equipe("evento-2", "Outra Equipe", (String) null);
        when(equipeRepository.buscarPorId(any())).thenReturn(Optional.of(equipeDesvinculada));
    }

    @Given("existe uma equipe com um funcionário")
    public void existe_uma_equipe_com_um_funcionario() {
        Equipe equipeComFunc = new Equipe(idEventoValido, "Equipe", idFuncionarioValido);
        when(equipeRepository.buscarPorId(any())).thenReturn(Optional.of(equipeComFunc));
        when(funcionarioRepository.buscarPorId(any())).thenReturn(Optional.of(Mockito.mock(Funcionario.class)));
    }

    @When("eu criar uma tarefa com título {string}")
    public void eu_criar_uma_tarefa_com_titulo(String titulo) {
        Tarefa novaTarefa = new Tarefa(idEquipeValida, titulo, "Descrição básica", LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        try {
            tarefaService.criarTarefa(novaTarefa);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu tentar criar uma tarefa")
    public void eu_tentar_criar_uma_tarefa() {
        eu_criar_uma_tarefa_com_titulo("Montar palco");
    }

    @When("eu tentar criar outra tarefa com título {string}")
    public void eu_tentar_criar_outra_tarefa_com_titulo(String titulo) {
        eu_criar_uma_tarefa_com_titulo(titulo);
    }
    
    @When("eu tentar criar uma tarefa sem título")
    public void eu_tentar_criar_uma_tarefa_sem_titulo() {
        try {
            Tarefa t = new Tarefa(idEquipeValida, null, "Descrição", LocalDateTime.now(), LocalDateTime.now().plusDays(1));
            tarefaService.criarTarefa(t);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu tentar criar uma tarefa com data de fim anterior à data de início")
    public void eu_tentar_criar_uma_tarefa_com_data_de_fim_anterior_a_data_de_inicio() {
        try {
            Tarefa t = new Tarefa(idEquipeValida, "Titulo", "Desc", LocalDateTime.now().plusDays(2), LocalDateTime.now());
            tarefaService.criarTarefa(t);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu editar o título da tarefa")
    public void eu_editar_o_titulo_da_tarefa() {
        try {
            tarefaEmContexto.atualizarDetalhes("Novo Título", "Desc", LocalDateTime.now(), LocalDateTime.now().plusDays(1));
            tarefaService.editarTarefa(tarefaEmContexto);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }
    
    @When("eu tentar alterar o título da tarefa {string} para {string}")
    public void eu_tentar_alterar_o_titulo_da_tarefa_para(String tituloDe, String tituloPara) {
        try {
            Tarefa editada = new Tarefa(idEquipeValida, tituloPara, "Desc", LocalDateTime.now(), LocalDateTime.now().plusDays(1));
            when(tarefaRepository.buscarPorId(any())).thenReturn(Optional.of(new Tarefa(idEquipeValida, tituloDe, "", LocalDateTime.now(), LocalDateTime.now().plusDays(1))));
            tarefaService.editarTarefa(editada);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu tentar editar com data de fim anterior à de início")
    public void eu_tentar_editar_com_data_de_fim_anterior_a_de_inicio() {
        try {
            tarefaEmContexto.atualizarDetalhes("Tit", "Desc", LocalDateTime.now().plusDays(2), LocalDateTime.now());
            tarefaService.editarTarefa(tarefaEmContexto);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu tentar editar a tarefa")
    public void eu_tentar_editar_a_tarefa() {
        eu_editar_o_titulo_da_tarefa();
    }

    @When("eu remover a tarefa")
    @When("eu tentar remover a tarefa")
    public void eu_remover_a_tarefa() {
        try {
            tarefaService.removerTarefa(idTarefaValida);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }
    
    @When("eu iniciar a tarefa")
    @When("eu tentar iniciar a tarefa")
    public void eu_iniciar_a_tarefa() {
        try {
            tarefaService.iniciarTarefa(idTarefaValida);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu concluir a tarefa")
    @When("eu tentar marcar como concluída diretamente")
    public void eu_concluir_a_tarefa() {
        try {
            tarefaEmContexto.concluir();
            tarefaRepository.salvar(tarefaEmContexto); 
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("eu atribuir o funcionário à tarefa")
    @When("eu tentar atribuir esse funcionário")
    public void eu_atribuir_o_funcionario_a_tarefa() {
        try {
            tarefaService.atribuirResponsavel(idTarefaValida, idFuncionarioValido);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @Then("a tarefa é criada com sucesso")
    @Then("a tarefa é atualizada com sucesso")
    @Then("a tarefa é removida com sucesso")
    @Then("o responsável é adicionado com sucesso")
    public void sucesso() {
        assertNull(excecaoLancada, "Não deveria ter lançado exceção, mas lançou: " + 
                  (excecaoLancada != null ? excecaoLancada.getMessage() : ""));
    }

    @Then("o sistema deve impedir a criação")
    @Then("o sistema deve impedir a edição")
    @Then("o sistema deve impedir a remoção")
    @Then("o sistema deve impedir a ação")
    public void o_sistema_deve_impedir_a_acao() {
        assertNotNull(excecaoLancada, "O sistema deveria ter impedido a ação lançando uma exceção.");
    }

    @Then("o status deve ser {string}")
    public void o_status_deve_ser(String statusEsperado) {
        assertNull(excecaoLancada);
        assertEquals(StatusTarefa.valueOf(statusEsperado), tarefaEmContexto.getStatus());
    }
}
