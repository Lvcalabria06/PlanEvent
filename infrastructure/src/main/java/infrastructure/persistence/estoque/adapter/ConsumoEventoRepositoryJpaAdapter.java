package infrastructure.persistence.estoque.adapter;

import domain.estoque.entity.ConsumoEvento;
import domain.estoque.repository.ConsumoEventoRepository;
import infrastructure.persistence.estoque.mapper.ConsumoEventoMapper;
import infrastructure.persistence.estoque.repository.ConsumoEventoJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ConsumoEventoRepositoryJpaAdapter implements ConsumoEventoRepository {

    private final ConsumoEventoJpaRepository jpaRepository;

    public ConsumoEventoRepositoryJpaAdapter(ConsumoEventoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ConsumoEvento salvar(ConsumoEvento consumoEvento) {
        return ConsumoEventoMapper.paraDominio(jpaRepository.save(ConsumoEventoMapper.paraJpa(consumoEvento)));
    }

    @Override
    public Optional<ConsumoEvento> buscarPorId(String id) {
        return jpaRepository.findById(id).map(ConsumoEventoMapper::paraDominio);
    }

    @Override
    public List<ConsumoEvento> listarTodos() {
        return jpaRepository.findAll().stream()
                .map(ConsumoEventoMapper::paraDominio)
                .toList();
    }

    @Override
    public List<ConsumoEvento> listarPorEvento(String eventoId) {
        return jpaRepository.findByEventoId(eventoId).stream()
                .map(ConsumoEventoMapper::paraDominio)
                .toList();
    }
}
