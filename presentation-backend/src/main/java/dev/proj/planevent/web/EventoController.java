package dev.proj.planevent.web;

import dev.proj.planevent.web.dto.FinanceiroDtos;
import domain.evento.repository.EventoRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    private final EventoRepository eventoRepository;

    public EventoController(EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    @GetMapping
    public List<FinanceiroDtos.EventoResumoDto> listar() {
        return eventoRepository.listarTodos().stream()
                .map(FinanceiroMapper::toEventoDto)
                .toList();
    }
}
