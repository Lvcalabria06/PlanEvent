package domain.estoque.service;

import domain.estoque.entity.ItemEstoque;
import domain.estoque.entity.ItemReserva;
import domain.estoque.entity.ReservaEstoque;
import domain.estoque.support.InMemoryEventoRepository;
import domain.estoque.support.InMemoryItemEstoqueRepository;
import domain.estoque.support.InMemoryReservaEstoqueRepository;
import domain.estoque.valueobject.StatusReservaEstoque;
import domain.evento.entity.Evento;
import domain.evento.valueobject.PorteEvento;
import domain.evento.valueobject.TipoEvento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReservaEstoqueServiceImplTest {

    private static final LocalDateTime INICIO = LocalDateTime.of(2026, 6, 10, 8, 0);
    private static final LocalDateTime FIM = LocalDateTime.of(2026, 6, 10, 22, 0);

    private InMemoryEventoRepository eventoRepository;
    private InMemoryItemEstoqueRepository itemEstoqueRepository;
    private InMemoryReservaEstoqueRepository reservaRepository;
    private ReservaEstoqueService service;
    private ItemEstoque itemAtivo;

    @BeforeEach
    void setUp() {
        eventoRepository = new InMemoryEventoRepository();
        itemEstoqueRepository = new InMemoryItemEstoqueRepository();
        reservaRepository = new InMemoryReservaEstoqueRepository();
        service = new ReservaEstoqueServiceImpl(reservaRepository, eventoRepository, itemEstoqueRepository);

        itemAtivo = new ItemEstoque("item-cadeira", "Cadeira", 200);
        itemEstoqueRepository.salvar(itemAtivo);
    }

    @Test
    @DisplayName("Criar reserva valida com itens ativos e evento existente")
    void criarReservaValidaComItensAtivos() {
        Evento evento = criarEventoFuturo("evt-1");

        ReservaEstoque reserva = service.criarReserva(
                evento.getId(), INICIO, FIM,
                List.of(new ItemReserva("solicitacao-1", itemAtivo.getId(), 50)));

        assertEquals(evento.getId(), reserva.getEventoId());
        assertEquals(StatusReservaEstoque.PENDENTE, reserva.getStatus());
        assertEquals(1, reserva.getItensReservados().size());
        assertTrue(reservaRepository.buscarPorId(reserva.getId()).isPresent());
    }

    @Test
    @DisplayName("Nao deve criar reserva para evento concluido")
    void naoCriaReservaParaEventoConcluido() {
        Evento evento = new Evento(
                "Show", TipoEvento.SOCIAL, PorteEvento.GRANDE, 500, "Show", "local-1");
        evento.definirJanelaPlanejamento(INICIO, FIM);
        evento.concluirEvento();
        eventoRepository.salvar(evento);

        assertThrows(IllegalStateException.class, () -> service.criarReserva(
                evento.getId(), INICIO, FIM,
                List.of(new ItemReserva("solicitacao-1", itemAtivo.getId(), 10))));
    }

    @Test
    @DisplayName("Nao deve criar reserva com item inexistente")
    void naoCriaReservaComItemInexistente() {
        Evento evento = criarEventoFuturo("evt-2");

        assertThrows(IllegalArgumentException.class, () -> service.criarReserva(
                evento.getId(), INICIO, FIM,
                List.of(new ItemReserva("solicitacao-1", "item-fantasma", 10))));
    }

    @Test
    @DisplayName("Nao deve criar reserva com item inativo")
    void naoCriaReservaComItemInativo() {
        Evento evento = criarEventoFuturo("evt-3");
        itemAtivo.desativar();
        itemEstoqueRepository.salvar(itemAtivo);

        assertThrows(IllegalStateException.class, () -> service.criarReserva(
                evento.getId(), INICIO, FIM,
                List.of(new ItemReserva("solicitacao-1", itemAtivo.getId(), 10))));
    }

    @Test
    @DisplayName("Atualizar reserva pendente substitui itens e datas")
    void atualizarReservaPendente() {
        Evento evento = criarEventoFuturo("evt-4");
        ReservaEstoque reserva = service.criarReserva(
                evento.getId(), INICIO, FIM,
                List.of(new ItemReserva("solicitacao-1", itemAtivo.getId(), 50)));

        ReservaEstoque atualizada = service.atualizarReserva(
                reserva.getId(),
                INICIO.plusHours(1),
                FIM.plusHours(1),
                List.of(new ItemReserva("solicitacao-1", itemAtivo.getId(), 80)));

        assertEquals(80, atualizada.getItensReservados().get(0).getQuantidade());
        assertEquals(INICIO.plusHours(1), atualizada.getDataInicio());
    }

    @Test
    @DisplayName("Nao deve atualizar reserva finalizada")
    void naoAtualizaReservaFinalizada() {
        Evento evento = criarEventoFuturo("evt-5");
        ReservaEstoque reserva = service.criarReserva(
                evento.getId(), INICIO, FIM,
                List.of(new ItemReserva("solicitacao-1", itemAtivo.getId(), 50)));

        service.confirmar(reserva.getId());
        service.finalizar(reserva.getId());

        assertThrows(IllegalStateException.class, () -> service.atualizarReserva(
                reserva.getId(), INICIO, FIM,
                List.of(new ItemReserva("solicitacao-1", itemAtivo.getId(), 30))));
    }

    @Test
    @DisplayName("Transicoes de status: confirmar -> iniciarUso -> finalizar")
    void transicoesDeStatusValidas() {
        Evento evento = criarEventoFuturo("evt-6");
        ReservaEstoque reserva = service.criarReserva(
                evento.getId(), INICIO, FIM,
                List.of(new ItemReserva("solicitacao-1", itemAtivo.getId(), 50)));

        ReservaEstoque confirmada = service.confirmar(reserva.getId());
        assertEquals(StatusReservaEstoque.CONFIRMADA, confirmada.getStatus());

        ReservaEstoque emUso = service.iniciarUso(reserva.getId());
        assertEquals(StatusReservaEstoque.EM_USO, emUso.getStatus());

        ReservaEstoque finalizada = service.finalizar(reserva.getId());
        assertEquals(StatusReservaEstoque.FINALIZADA, finalizada.getStatus());
    }

    @Test
    @DisplayName("Cancelar reserva pendente muda status para cancelada")
    void cancelarReservaPendente() {
        Evento evento = criarEventoFuturo("evt-7");
        ReservaEstoque reserva = service.criarReserva(
                evento.getId(), INICIO, FIM,
                List.of(new ItemReserva("solicitacao-1", itemAtivo.getId(), 50)));

        service.cancelar(reserva.getId());

        ReservaEstoque cancelada = service.buscarPorId(reserva.getId()).orElseThrow();
        assertEquals(StatusReservaEstoque.CANCELADA, cancelada.getStatus());
    }

    @Test
    @DisplayName("Listagem por evento e periodo")
    void listagemPorEventoEPorPeriodo() {
        Evento eventoA = criarEventoFuturo("evt-A");
        Evento eventoB = criarEventoFuturo("evt-B");

        service.criarReserva(eventoA.getId(), INICIO, FIM,
                List.of(new ItemReserva("solicitacao-1", itemAtivo.getId(), 10)));
        service.criarReserva(eventoB.getId(), INICIO.plusDays(5), FIM.plusDays(5),
                List.of(new ItemReserva("solicitacao-2", itemAtivo.getId(), 20)));

        assertEquals(1, service.listarPorEvento(eventoA.getId()).size());
        assertEquals(2, service.listarTodas().size());
        assertEquals(1, service.listarPorPeriodo(INICIO, FIM).size());
        assertEquals(2, service.listarPorPeriodo(INICIO, FIM.plusDays(7)).size());
    }

    private Evento criarEventoFuturo(String nomeEvento) {
        Evento evento = new Evento(
                nomeEvento, TipoEvento.CORPORATIVO, PorteEvento.MEDIO, 100, "Objetivo", "local-1");
        evento.definirJanelaPlanejamento(INICIO, FIM);
        eventoRepository.salvar(evento);
        return evento;
    }
}
