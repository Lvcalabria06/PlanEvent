package presentationbackend.controller;

import application.financeiro.dto.FinanceiroDtos;
import application.financeiro.mapper.FinanceiroMapper;
import domain.evento.repository.EventoRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Expõe a listagem de eventos para o módulo financeiro (endpoint {@code /api/eventos}).
 * Renomeado para evitar conflito com {@link EventoController} ({@code /api/v1/eventos}).
 */
@RestController
@RequestMapping("/api/eventos")
public class EventoFinanceiroController {

    private final EventoRepository eventoRepository;

    public EventoFinanceiroController(EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    @GetMapping
    public List<FinanceiroDtos.EventoResumoDto> listar() {
        return eventoRepository.listarTodos().stream()
                .map(FinanceiroMapper::toEventoDto)
                .toList();
    }
}
