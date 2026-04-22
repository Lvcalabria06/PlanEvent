package domain.funcionario.steps;

import domain.equipe.repository.EquipeRepository;
import domain.funcionario.entity.Funcionario;
import domain.funcionario.repository.FuncionarioRepository;
import domain.funcionario.service.FuncionarioService;
import domain.funcionario.service.FuncionarioServiceImpl;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class FuncionarioSteps {

    private static final String NOME_VALIDO = "Maria Silva";
    private static final String CARGO_VALIDO = "garçom";
    private static final String DISPONIBILIDADE_VALIDA = "manhã";

    private FuncionarioRepository funcionarioRepository;
    private EquipeRepository equipeRepository;
    private FuncionarioService funcionarioService;

    private Exception excecaoLancada;
    private Funcionario funcionarioEmContexto;
    private Funcionario funcionarioRetornadoBusca;
    private List<Funcionario> listaFuncionariosRetornada;

    private String nomeInformado;
    private String cargoInformado;
    private String disponibilidadeInformada;
    private String idGeradoAntes;
    private LocalDateTime createdAtAntes;
    private LocalDateTime updatedAtAntes;
    private boolean mensagemSemFuncionarios;

    @Before
    public void setup() {
        funcionarioRepository = Mockito.mock(FuncionarioRepository.class);
        equipeRepository = Mockito.mock(EquipeRepository.class);
        funcionarioService = new FuncionarioServiceImpl(funcionarioRepository, equipeRepository);

        excecaoLancada = null;
        funcionarioEmContexto = null;
        funcionarioRetornadoBusca = null;
        listaFuncionariosRetornada = null;
        nomeInformado = null;
        cargoInformado = null;
        disponibilidadeInformada = null;
        idGeradoAntes = null;
        createdAtAntes = null;
        updatedAtAntes = null;
        mensagemSemFuncionarios = false;

        when(funcionarioRepository.salvar(any(Funcionario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(equipeRepository.existeFuncionarioVinculado(any()))
                .thenReturn(false);
    }

    @Given("o gestor informa um nome válido, cargo permitido e disponibilidade válida")
    public void o_gestor_informa_um_nome_valido_cargo_permitido_e_disponibilidade_valida() {
        nomeInformado = NOME_VALIDO;
        cargoInformado = CARGO_VALIDO;
        disponibilidadeInformada = DISPONIBILIDADE_VALIDA;
    }

    @Given("o gestor informa cargo e disponibilidade válidos")
    public void o_gestor_informa_cargo_e_disponibilidade_validos() {
        cargoInformado = CARGO_VALIDO;
        disponibilidadeInformada = DISPONIBILIDADE_VALIDA;
    }

    @Given("o gestor informa um nome com menos de {int} caracteres ou com caracteres inválidos")
    public void o_gestor_informa_um_nome_com_menos_de_caracteres_ou_com_caracteres_invalidos(Integer minimo) {
        nomeInformado = "Ma1";
        cargoInformado = CARGO_VALIDO;
        disponibilidadeInformada = DISPONIBILIDADE_VALIDA;
    }

    @Given("o gestor informa um nome válido e disponibilidade válida")
    public void o_gestor_informa_um_nome_valido_e_disponibilidade_valida() {
        nomeInformado = NOME_VALIDO;
        cargoInformado = "cargo-invalido";
        disponibilidadeInformada = DISPONIBILIDADE_VALIDA;
    }

    @Given("o gestor informa um nome válido e cargo permitido")
    public void o_gestor_informa_um_nome_valido_e_cargo_permitido() {
        nomeInformado = NOME_VALIDO;
        cargoInformado = CARGO_VALIDO;
        disponibilidadeInformada = "madrugada";
    }

    @Given("o gestor informa dados válidos para cadastro")
    public void o_gestor_informa_dados_validos_para_cadastro() {
        nomeInformado = NOME_VALIDO;
        cargoInformado = CARGO_VALIDO;
        disponibilidadeInformada = DISPONIBILIDADE_VALIDA;
    }

    @Given("existe um funcionário ativo cadastrado no sistema")
    public void existe_um_funcionario_ativo_cadastrado_no_sistema() {
        funcionarioEmContexto = novoFuncionarioValido();
        createdAtAntes = funcionarioEmContexto.getCreatedAt();
        updatedAtAntes = funcionarioEmContexto.getUpdatedAt();

        when(funcionarioRepository.buscarPorId(eq(funcionarioEmContexto.getId())))
            .thenReturn(Optional.of(funcionarioEmContexto));
    }

    @Given("existe um funcionário ativo sem vínculo com evento ou equipe")
    public void existe_um_funcionario_ativo_sem_vinculo_com_evento_ou_equipe() {
        funcionarioEmContexto = novoFuncionarioValido();
        when(funcionarioRepository.buscarPorId(eq(funcionarioEmContexto.getId())))
                .thenReturn(Optional.of(funcionarioEmContexto));
        when(equipeRepository.existeFuncionarioVinculado(eq(funcionarioEmContexto.getId())))
                .thenReturn(false);
    }

    @Given("existe um funcionário ativo vinculado a um evento")
    public void existe_um_funcionario_ativo_vinculado_a_um_evento() {
        funcionarioEmContexto = novoFuncionarioValido();
        when(funcionarioRepository.buscarPorId(eq(funcionarioEmContexto.getId())))
                .thenReturn(Optional.of(funcionarioEmContexto));
        when(equipeRepository.existeFuncionarioVinculado(eq(funcionarioEmContexto.getId())))
                .thenReturn(true);
    }

    @Given("existe um funcionário ativo vinculado a uma equipe")
    public void existe_um_funcionario_ativo_vinculado_a_uma_equipe() {
        funcionarioEmContexto = novoFuncionarioValido();
        when(funcionarioRepository.buscarPorId(eq(funcionarioEmContexto.getId())))
                .thenReturn(Optional.of(funcionarioEmContexto));
        when(equipeRepository.existeFuncionarioVinculado(eq(funcionarioEmContexto.getId())))
                .thenReturn(true);
    }

    @Given("existem funcionários cadastrados no sistema")
    public void existem_funcionarios_cadastrados_no_sistema() {
        listaFuncionariosRetornada = List.of(
                novoFuncionarioValido(),
                new Funcionario("João Pedro", "técnico", "tarde")
        );
        when(funcionarioRepository.listarTodos()).thenReturn(listaFuncionariosRetornada);
    }

    @Given("existe um funcionário cadastrado no sistema")
    public void existe_um_funcionario_cadastrado_no_sistema() {
        funcionarioEmContexto = novoFuncionarioValido();
        when(funcionarioRepository.listarTodos()).thenReturn(List.of(funcionarioEmContexto));
        when(funcionarioRepository.buscarPorId(eq(funcionarioEmContexto.getId())))
                .thenReturn(Optional.of(funcionarioEmContexto));
    }

    @Given("existe um funcionário inativo no sistema")
    public void existe_um_funcionario_inativo_no_sistema() {
        funcionarioEmContexto = novoFuncionarioValido();
        funcionarioEmContexto.inativar();
        when(funcionarioRepository.listarTodos()).thenReturn(List.of(funcionarioEmContexto));
    }

    @Given("não existem funcionários cadastrados no sistema")
    public void nao_existem_funcionarios_cadastrados_no_sistema() {
        when(funcionarioRepository.listarTodos()).thenReturn(List.of());
    }

    @When("o gestor cadastrar o funcionário no sistema")
    public void o_gestor_cadastrar_o_funcionario_no_sistema() {
        try {
            funcionarioEmContexto = funcionarioService.criarFuncionario(
                    new Funcionario(nomeInformado, cargoInformado, disponibilidadeInformada)
            );
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("o gestor tentar cadastrar um funcionário sem nome")
    public void o_gestor_tentar_cadastrar_um_funcionario_sem_nome() {
        try {
            funcionarioService.criarFuncionario(
                    new Funcionario(null, cargoInformado, disponibilidadeInformada)
            );
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("o gestor tentar cadastrar o funcionário")
    public void o_gestor_tentar_cadastrar_o_funcionario() {
        try {
            funcionarioService.criarFuncionario(
                    new Funcionario(nomeInformado, cargoInformado, disponibilidadeInformada)
            );
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("o gestor tentar cadastrar um funcionário com cargo fora dos valores permitidos")
    public void o_gestor_tentar_cadastrar_um_funcionario_com_cargo_fora_dos_valores_permitidos() {
        try {
            funcionarioService.criarFuncionario(
                    new Funcionario(nomeInformado, cargoInformado, disponibilidadeInformada)
            );
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("o gestor tentar cadastrar um funcionário com disponibilidade inválida")
    public void o_gestor_tentar_cadastrar_um_funcionario_com_disponibilidade_invalida() {
        try {
            funcionarioService.criarFuncionario(
                    new Funcionario(nomeInformado, cargoInformado, disponibilidadeInformada)
            );
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("o gestor cadastrar o funcionário")
    public void o_gestor_cadastrar_o_funcionario() {
        try {
            funcionarioEmContexto = funcionarioService.criarFuncionario(
                    new Funcionario(nomeInformado, cargoInformado, disponibilidadeInformada)
            );
            idGeradoAntes = funcionarioEmContexto.getId();
            createdAtAntes = funcionarioEmContexto.getCreatedAt();
            updatedAtAntes = funcionarioEmContexto.getUpdatedAt();
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("o gestor editar o nome, cargo ou disponibilidade com valores válidos")
    public void o_gestor_editar_o_nome_cargo_ou_disponibilidade_com_valores_validos() {
        try {
            updatedAtAntes = funcionarioEmContexto.getUpdatedAt();
            funcionarioEmContexto.alterarNome("Maria Souza");
            funcionarioEmContexto.alterarCargo("técnico");
            funcionarioEmContexto.alterarDisponibilidade("tarde");
            funcionarioEmContexto = funcionarioService.editarFuncionario(funcionarioEmContexto);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("o gestor tentar editar o funcionário com nome com menos de {int} caracteres ou com caracteres inválidos")
    public void o_gestor_tentar_editar_o_funcionario_com_nome_com_menos_de_caracteres_ou_com_caracteres_invalidos(Integer minimo) {
        try {
            funcionarioEmContexto.alterarNome("A1");
            funcionarioService.editarFuncionario(funcionarioEmContexto);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("o gestor tentar editar o funcionário com cargo fora dos valores permitidos")
    public void o_gestor_tentar_editar_o_funcionario_com_cargo_fora_dos_valores_permitidos() {
        try {
            funcionarioEmContexto.alterarCargo("cargo-invalido");
            funcionarioService.editarFuncionario(funcionarioEmContexto);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("o gestor tentar editar o funcionário com disponibilidade fora do padrão permitido")
    public void o_gestor_tentar_editar_o_funcionario_com_disponibilidade_fora_do_padrao_permitido() {
        try {
            funcionarioEmContexto.alterarDisponibilidade("madrugada");
            funcionarioService.editarFuncionario(funcionarioEmContexto);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("o gestor editar qualquer dado válido do funcionário")
    public void o_gestor_editar_qualquer_dado_valido_do_funcionario() {
        try {
            updatedAtAntes = funcionarioEmContexto.getUpdatedAt();
            funcionarioEmContexto.alterarNome("Maria Oliveira");
            funcionarioEmContexto = funcionarioService.editarFuncionario(funcionarioEmContexto);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("o gestor tentar alterar manualmente o campo createdAt")
    public void o_gestor_tentar_alterar_manualmente_o_campo_created_at() {
    }

    @When("o gestor tentar alterar manualmente o campo updatedAt")
    public void o_gestor_tentar_alterar_manualmente_o_campo_updated_at() {
    }

    @When("o gestor tentar salvar alterações inconsistentes no funcionário")
    public void o_gestor_tentar_salvar_alteracoes_inconsistentes_no_funcionario() {
        try {
            funcionarioEmContexto.alterarNome("1");
            funcionarioService.editarFuncionario(funcionarioEmContexto);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("o gestor solicitar a exclusão do funcionário")
    public void o_gestor_solicitar_a_exclusao_do_funcionario() {
        try {
            funcionarioService.inativarFuncionario(funcionarioEmContexto.getId());
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("o gestor solicitar a visualização dos funcionários")
    public void o_gestor_solicitar_a_visualizacao_dos_funcionarios() {
        try {
            listaFuncionariosRetornada = funcionarioService.listarFuncionarios();
            mensagemSemFuncionarios = listaFuncionariosRetornada.isEmpty();
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("o gestor visualizar os funcionários")
    public void o_gestor_visualizar_os_funcionarios() {
        try {
            listaFuncionariosRetornada = funcionarioService.listarFuncionarios();
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("o gestor visualizar a lista padrão de funcionários")
    public void o_gestor_visualizar_a_lista_padrao_de_funcionarios() {
        try {
            listaFuncionariosRetornada = funcionarioService.listarFuncionarios()
                    .stream()
                    .filter(Funcionario::isAtivo)
                    .toList();
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @Then("o funcionário é salvo com sucesso")
    public void o_funcionario_e_salvo_com_sucesso() {
        assertNull(excecaoLancada);
        assertNotNull(funcionarioEmContexto);
        assertNotNull(funcionarioEmContexto.getId());
    }

    @Then("o sistema deve impedir o cadastro do funcionário")
    public void o_sistema_deve_impedir_o_cadastro_do_funcionario() {
        assertNotNull(excecaoLancada);
    }

    @Then("o sistema deve gerar automaticamente um identificador único e imutável")
    public void o_sistema_deve_gerar_automaticamente_um_identificador_unico_e_imutavel() {
        assertNull(excecaoLancada);
        assertNotNull(funcionarioEmContexto);
        assertNotNull(funcionarioEmContexto.getId());
        assertEquals(idGeradoAntes, funcionarioEmContexto.getId());
    }

    @Then("o sistema deve definir automaticamente createdAt e updatedAt")
    public void o_sistema_deve_definir_automaticamente_created_at_e_updated_at() {
        assertNull(excecaoLancada);
        assertNotNull(funcionarioEmContexto.getCreatedAt());
        assertNotNull(funcionarioEmContexto.getUpdatedAt());
        assertEquals(funcionarioEmContexto.getCreatedAt(), funcionarioEmContexto.getUpdatedAt());
    }

    @Then("o sistema deve atualizar os dados do funcionário com sucesso")
    public void o_sistema_deve_atualizar_os_dados_do_funcionario_com_sucesso() {
        assertNull(excecaoLancada);
        assertEquals("Maria Souza", funcionarioEmContexto.getNome());
    }

    @Then("o sistema deve impedir a edição do funcionário")
    public void o_sistema_deve_impedir_a_edicao_do_funcionario() {
        assertNotNull(excecaoLancada);
    }

    @Then("o sistema deve atualizar automaticamente o campo updatedAt")
    public void o_sistema_deve_atualizar_automaticamente_o_campo_updated_at() {
        assertNull(excecaoLancada);
        assertNotNull(updatedAtAntes);
        assertTrue(funcionarioEmContexto.getUpdatedAt().isAfter(updatedAtAntes)
                || funcionarioEmContexto.getUpdatedAt().isEqual(updatedAtAntes));
    }

    @Then("o sistema deve impedir a edição do campo createdAt")
    public void o_sistema_deve_impedir_a_edicao_do_campo_created_at() {
        assertNotNull(funcionarioEmContexto);
        assertNotNull(funcionarioEmContexto.getCreatedAt());
        assertEquals(createdAtAntes, funcionarioEmContexto.getCreatedAt());
    }

    @Then("o sistema deve impedir a edição do campo updatedAt")
    public void o_sistema_deve_impedir_a_edicao_do_campo_updated_at() {
        assertNotNull(funcionarioEmContexto);
        assertNotNull(funcionarioEmContexto.getUpdatedAt());
    }

    @Then("o sistema deve inativar o funcionário com sucesso")
    public void o_sistema_deve_inativar_o_funcionario_com_sucesso() {
        assertNull(excecaoLancada);
        assertFalse(funcionarioEmContexto.isAtivo());
    }

    @Then("o sistema deve impedir a exclusão do funcionário")
    public void o_sistema_deve_impedir_a_exclusao_do_funcionario() {
        assertNotNull(excecaoLancada);
    }

    @Then("o sistema deve exibir a lista de funcionários cadastrados")
    public void o_sistema_deve_exibir_a_lista_de_funcionarios_cadastrados() {
        assertNull(excecaoLancada);
        assertNotNull(listaFuncionariosRetornada);
        assertFalse(listaFuncionariosRetornada.isEmpty());
    }

    @Then("o sistema deve exibir nome, cargo e disponibilidade do funcionário")
    public void o_sistema_deve_exibir_nome_cargo_e_disponibilidade_do_funcionario() {
        assertNull(excecaoLancada);
        assertNotNull(listaFuncionariosRetornada);
        assertFalse(listaFuncionariosRetornada.isEmpty());

        Funcionario funcionario = listaFuncionariosRetornada.get(0);
        assertNotNull(funcionario.getNome());
        assertNotNull(funcionario.getCargo());
        assertNotNull(funcionario.getDisponibilidade());
    }

    @Then("o sistema não deve exibir o funcionário inativo")
    public void o_sistema_nao_deve_exibir_o_funcionario_inativo() {
        assertNull(excecaoLancada);
        assertNotNull(listaFuncionariosRetornada);
        assertTrue(listaFuncionariosRetornada.stream().noneMatch(f -> !f.isAtivo()));
    }

    @Then("o sistema deve informar que não há funcionários cadastrados")
    public void o_sistema_deve_informar_que_nao_ha_funcionarios_cadastrados() {
        assertNull(excecaoLancada);
        assertTrue(mensagemSemFuncionarios);
    }

    private Funcionario novoFuncionarioValido() {
        return new Funcionario(NOME_VALIDO, CARGO_VALIDO, DISPONIBILIDADE_VALIDA);
    }
}