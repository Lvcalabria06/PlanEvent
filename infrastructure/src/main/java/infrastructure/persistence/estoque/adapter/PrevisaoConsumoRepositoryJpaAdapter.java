package infrastructure.persistence.estoque.adapter;

import domain.estoque.entity.PrevisaoConsumo;
import domain.estoque.repository.PrevisaoConsumoRepository;
import infrastructure.persistence.estoque.mapper.PrevisaoConsumoMapper;
import infrastructure.persistence.estoque.repository.PrevisaoConsumoJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PrevisaoConsumoRepositoryJpaAdapter implements PrevisaoConsumoRepository {

    private final PrevisaoConsumoJpaRepository jpaRepository;

    public PrevisaoConsumoRepositoryJpaAdapter(PrevisaoConsumoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public PrevisaoConsumo salvar(PrevisaoConsumo previsaoConsumo) {
        return PrevisaoConsumoMapper.paraDominio(jpaRepository.save(PrevisaoConsumoMapper.paraJpa(previsaoConsumo)));
    }

    @Override
    public Optional<PrevisaoConsumo> buscarPorId(String id) {
        return jpaRepository.findById(id).map(PrevisaoConsumoMapper::paraDominio);
    }

    @Override
    public Optional<PrevisaoConsumo> buscarPorEventoId(String eventoId) {
        return jpaRepository.findByEventoId(eventoId).map(PrevisaoConsumoMapper::paraDominio);
    }

    @Override
    public List<PrevisaoConsumo> listarTodas() {
        return jpaRepository.findAll().stream()
                .map(PrevisaoConsumoMapper::paraDominio)
                .toList();
    }
}
