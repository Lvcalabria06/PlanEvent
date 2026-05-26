package domain.estoque.service;

import domain.estoque.entity.ConsumoEvento;
import domain.estoque.entity.ItemConsumoEvento;
import domain.estoque.repository.ConsumoEventoRepository;
import domain.evento.entity.Evento;
import domain.evento.repository.EventoRepository;

import java.util.List;
import java.util.Optional;

public class ConsumoEventoServiceImpl implements ConsumoEventoService {

    private final ConsumoEventoRepository consumoEventoRepository;
    private final EventoRepository eventoRepository;

    public ConsumoEventoServiceImpl(ConsumoEventoRepository consumoEventoRepository,
                                    EventoRepository eventoRepository) {
        this.consumoEventoRepository = consumoEventoRepository;
        this.eventoRepository = eventoRepository;
    }

    @Override
    public ConsumoEvento registrar(String eventoId,
                                   String usuarioId,
                                   List<ItemConsumoEvento> itensConsumidos) {
        Evento evento = eventoRepository.buscarPorId(eventoId)
                .orElseThrow(() -> new IllegalArgumentException("Evento nao encontrado para registro de consumo."));
        if (!evento.isConcluido()) {
            throw new IllegalStateException("Consumo so pode ser registrado apos a conclusao do evento.");
        }

        ConsumoEvento consumo = new ConsumoEvento(eventoId, usuarioId, itensConsumidos);
        return consumoEventoRepository.salvar(consumo);
    }

    @Override
    public void invalidar(String consumoEventoId) {
        ConsumoEvento consumo = consumoEventoRepository.buscarPorId(consumoEventoId)
                .orElseThrow(() -> new IllegalArgumentException("Registro de consumo nao encontrado."));
        consumo.invalidar();
        consumoEventoRepository.salvar(consumo);
    }

    @Override
    public Optional<ConsumoEvento> buscarPorId(String id) {
        return consumoEventoRepository.buscarPorId(id);
    }

    @Override
    public List<ConsumoEvento> listarPorEvento(String eventoId) {
        return consumoEventoRepository.listarPorEvento(eventoId);
    }

    @Override
    public List<ConsumoEvento> listarTodos() {
        return consumoEventoRepository.listarTodos();
    }
}
