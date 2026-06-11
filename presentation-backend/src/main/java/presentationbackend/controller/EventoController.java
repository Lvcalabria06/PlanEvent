package presentationbackend.controller;

import domain.evento.entity.Evento;
import domain.evento.repository.EventoRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Expõe a listagem de eventos para uso em seletores do frontend
 * (ex.: associar um contrato a um evento).
 */
@RestController
@RequestMapping("/api/v1/eventos")
public class EventoController {

    private final EventoRepository eventoRepository;

    public EventoController(EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    @GetMapping
    public List<Map<String, String>> listar() {
        return eventoRepository.listarTodos().stream()
                .map(evento -> Map.of(
                        "id", evento.getId(),
                        "nome", resolverNome(evento)))
                .toList();
    }

    private static String resolverNome(Evento evento) {
        return evento.getNome() != null ? evento.getNome() : "Evento " + evento.getId().substring(0, 8);
    }
}
