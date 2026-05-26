package domain.estoque.service;

import domain.estoque.entity.ItemEstoque;
import domain.estoque.support.InMemoryItemEstoqueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemEstoqueServiceImplTest {

    private InMemoryItemEstoqueRepository repository;
    private ItemEstoqueService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryItemEstoqueRepository();
        service = new ItemEstoqueServiceImpl(repository);
    }

    @Test
    @DisplayName("Cadastrar item gera id imutavel, ativo e com timestamps")
    void cadastrarGeraItemAtivoComMetadados() {
        ItemEstoque item = service.cadastrar("Cadeira", 50);

        assertNotNull(item.getId());
        assertEquals("Cadeira", item.getNome());
        assertEquals(50, item.getQuantidadeTotal());
        assertEquals(50, item.getQuantidadeDisponivel());
        assertTrue(item.isAtivo());
        assertNotNull(item.getDataCriacao());
        assertEquals(item.getDataCriacao(), item.getDataAtualizacao());

        assertTrue(repository.buscarPorId(item.getId()).isPresent());
    }

    @Test
    @DisplayName("Cadastrar deve rejeitar nome em branco ou quantidade negativa")
    void cadastrarRejeitaDadosInvalidos() {
        assertThrows(IllegalArgumentException.class, () -> service.cadastrar("", 10));
        assertThrows(IllegalArgumentException.class, () -> service.cadastrar("Cadeira", -1));
    }

    @Test
    @DisplayName("Editar atualiza nome e quantidade total, ajusta disponivel e timestamp")
    void editarAtualizaItemERefletiNoTimestamp() throws Exception {
        ItemEstoque item = service.cadastrar("Cadeira", 50);
        Thread.sleep(2);

        ItemEstoque editado = service.editar(item.getId(), "Cadeira Plus", 80);

        assertEquals("Cadeira Plus", editado.getNome());
        assertEquals(80, editado.getQuantidadeTotal());
        assertTrue(editado.getDataAtualizacao().isAfter(editado.getDataCriacao()));
    }

    @Test
    @DisplayName("Editar item inexistente lanca excecao")
    void editarItemInexistenteLancaExcecao() {
        assertThrows(IllegalArgumentException.class, () -> service.editar("nao-existe", "Mesa", 10));
    }

    @Test
    @DisplayName("Desativar realiza soft delete e bloqueia novas operacoes")
    void desativarBloqueiaOperacoesPosteriores() {
        ItemEstoque item = service.cadastrar("Cadeira", 50);

        service.desativar(item.getId());

        ItemEstoque desativado = service.buscarPorId(item.getId()).orElseThrow();
        assertFalse(desativado.isAtivo());

        assertThrows(IllegalStateException.class,
                () -> service.editar(item.getId(), "Cadeira", 30));
        assertThrows(IllegalStateException.class,
                () -> service.adicionarEstoque(item.getId(), 10));
    }

    @Test
    @DisplayName("Reativar restaura item desativado")
    void reativarRestauraItemDesativado() {
        ItemEstoque item = service.cadastrar("Cadeira", 50);
        service.desativar(item.getId());

        service.reativar(item.getId());

        ItemEstoque reativado = service.buscarPorId(item.getId()).orElseThrow();
        assertTrue(reativado.isAtivo());
    }

    @Test
    @DisplayName("Listar todos e listar ativos respeitam soft delete")
    void listarTodosEAtivosRespeitamSoftDelete() {
        ItemEstoque cadeira = service.cadastrar("Cadeira", 50);
        ItemEstoque mesa = service.cadastrar("Mesa", 20);
        service.desativar(mesa.getId());

        List<ItemEstoque> todos = service.listarTodos();
        List<ItemEstoque> ativos = service.listarAtivos();

        assertEquals(2, todos.size());
        assertEquals(1, ativos.size());
        assertEquals(cadeira.getId(), ativos.get(0).getId());
    }

    @Test
    @DisplayName("Adicionar estoque incrementa total e disponivel")
    void adicionarEstoqueIncrementaQuantidades() {
        ItemEstoque item = service.cadastrar("Cadeira", 50);

        ItemEstoque atualizado = service.adicionarEstoque(item.getId(), 25);

        assertEquals(75, atualizado.getQuantidadeTotal());
        assertEquals(75, atualizado.getQuantidadeDisponivel());
    }

    @Test
    @DisplayName("Buscar por id retorna vazio quando inexistente")
    void buscarPorIdInexistenteRetornaVazio() {
        Optional<ItemEstoque> resultado = service.buscarPorId("nao-existe");
        assertTrue(resultado.isEmpty());
    }
}
