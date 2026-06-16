package domain.estoque.service;

import domain.estoque.entity.ConsumoEvento;
import domain.estoque.entity.ItemConsumoEvento;
import domain.estoque.support.InMemoryConsumoEventoRepository;
import domain.estoque.support.InMemoryEventoRepository;
import domain.evento.entity.Evento;
import domain.evento.valueobject.PorteEvento;
import domain.evento.valueobject.TipoEvento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsumoEventoServiceImplTest {

    private InMemoryEventoRepository eventoRepository;
    private InMemoryConsumoEventoRepository consumoRepository;
    private ConsumoEventoService service;

    @BeforeEach
    void setUp() {
        eventoRepository = new InMemoryEventoRepository();
        consumoRepository = new InMemoryConsumoEventoRepository();
        service = new ConsumoEventoServiceImpl(consumoRepository, eventoRepository);
    }

    @Test
    @DisplayName("Registrar consumo so apos conclusao do evento")
    void registrarSomenteAposEventoConcluido() {
        Evento evento = criarEventoConcluido("evt-1");

        ConsumoEvento consumo = service.registrar(
                evento.getId(),
                "gestor-1",
                List.of(new ItemConsumoEvento("agua", "bebida", 100))
        );

        assertEquals(evento.getId(), consumo.getEventoId());
        assertEquals("gestor-1", consumo.getRegistradoPorUsuarioId());
        assertEquals(1, consumo.getItensConsumidos().size());
        assertTrue(consumo.isValido());
        assertTrue(consumoRepository.buscarPorId(consumo.getId()).isPresent());
    }

    @Test
    @DisplayName("Nao pode registrar consumo para evento nao concluido")
    void naoRegistraConsumoParaEventoEmAndamento() {
        Evento evento = new Evento(
                "Workshop", TipoEvento.CORPORATIVO, PorteEvento.MEDIO, 100, "Treinamento", "local-1");
        eventoRepository.salvar(evento);

        assertThrows(IllegalStateException.class, () -> service.registrar(
                evento.getId(),
                "gestor-1",
                List.of(new ItemConsumoEvento("agua", "bebida", 50))
        ));
    }

    @Test
    @DisplayName("Nao pode registrar consumo para evento inexistente")
    void naoRegistraConsumoSemEvento() {
        assertThrows(IllegalArgumentException.class, () -> service.registrar(
                "evt-inexistente",
                "gestor-1",
                List.of(new ItemConsumoEvento("agua", "bebida", 50))
        ));
    }

    @Test
    @DisplayName("Invalidar consumo marca como invalido e preserva no historico")
    void invalidarConsumoPreservaRegistro() {
        Evento evento = criarEventoConcluido("evt-2");
        ConsumoEvento consumo = service.registrar(
                evento.getId(),
                "gestor-1",
                List.of(new ItemConsumoEvento("agua", "bebida", 100))
        );

        service.invalidar(consumo.getId());

        ConsumoEvento atualizado = service.buscarPorId(consumo.getId()).orElseThrow();
        assertFalse(atualizado.isValido());
    }

    @Test
    @DisplayName("Listagem por evento e total funciona corretamente")
    void listagemPorEventoETotal() {
        Evento eventoA = criarEventoConcluido("evt-A");
        Evento eventoB = criarEventoConcluido("evt-B");

        service.registrar(eventoA.getId(), "gestor-1",
                List.of(new ItemConsumoEvento("agua", "bebida", 100)));
        service.registrar(eventoA.getId(), "gestor-2",
                List.of(new ItemConsumoEvento("cha", "bebida", 50)));
        service.registrar(eventoB.getId(), "gestor-1",
                List.of(new ItemConsumoEvento("cafe", "bebida", 70)));

        assertEquals(2, service.listarPorEvento(eventoA.getId()).size());
        assertEquals(1, service.listarPorEvento(eventoB.getId()).size());
        assertEquals(3, service.listarTodos().size());
    }

    private Evento criarEventoConcluido(String nomeEvento) {
        Evento evento = new Evento(
                nomeEvento, TipoEvento.CORPORATIVO, PorteEvento.MEDIO, 100, "Objetivo", "local-1");
        evento.definirJanelaPlanejamento(
                LocalDateTime.of(2026, 5, 1, 8, 0),
                LocalDateTime.of(2026, 5, 1, 18, 0));
        evento.concluirEvento();
        eventoRepository.salvar(evento);
        return evento;
    }
}
