package infrastructure.persistence.estoque.adapter;

import domain.estoque.entity.ReservaEstoque;
import domain.estoque.repository.ReservaEstoqueRepository;
import infrastructure.persistence.estoque.mapper.ReservaEstoqueMapper;
import infrastructure.persistence.estoque.repository.ReservaEstoqueJpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class ReservaEstoqueRepositoryJpaAdapter implements ReservaEstoqueRepository {

    private final ReservaEstoqueJpaRepository jpaRepository;

    public ReservaEstoqueRepositoryJpaAdapter(ReservaEstoqueJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ReservaEstoque salvar(ReservaEstoque reservaEstoque) {
        return ReservaEstoqueMapper.paraDominio(jpaRepository.save(ReservaEstoqueMapper.paraJpa(reservaEstoque)));
    }

    @Override
    public Optional<ReservaEstoque> buscarPorId(String id) {
        return jpaRepository.findById(id).map(ReservaEstoqueMapper::paraDominio);
    }

    @Override
    public List<ReservaEstoque> listarTodas() {
        return jpaRepository.findAll().stream()
                .map(ReservaEstoqueMapper::paraDominio)
                .toList();
    }

    @Override
    public List<ReservaEstoque> listarPorEvento(String eventoId) {
        return jpaRepository.findByEventoId(eventoId).stream()
                .map(ReservaEstoqueMapper::paraDominio)
                .toList();
    }

    @Override
    public List<ReservaEstoque> listarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return jpaRepository.findByPeriodoOverlap(inicio, fim).stream()
                .map(ReservaEstoqueMapper::paraDominio)
                .toList();
    }
}
