package infrastructure.persistence.contrato.adapter;

import domain.contrato.entity.Contrato;
import domain.contrato.repository.ContratoRepository;
import domain.contrato.valueobject.StatusContrato;
import infrastructure.persistence.contrato.mapper.ContratoMapper;
import infrastructure.persistence.contrato.repository.ContratoJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class ContratoRepositoryJpaAdapter implements ContratoRepository {

    private final ContratoJpaRepository jpaRepository;

    public ContratoRepositoryJpaAdapter(ContratoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Contrato salvar(Contrato contrato) {
        return ContratoMapper.paraDominio(jpaRepository.save(ContratoMapper.paraJpa(contrato)));
    }

    @Override
    public Optional<Contrato> buscarPorId(String id) {
        return jpaRepository.findById(id).map(ContratoMapper::paraDominio);
    }

    @Override
    public List<Contrato> listarTodos() {
        return jpaRepository.findAll().stream()
                .map(ContratoMapper::paraDominio)
                .toList();
    }

    @Override
    public List<Contrato> listarPorEventoId(String eventoId) {
        return jpaRepository.findByEventoId(eventoId).stream()
                .map(ContratoMapper::paraDominio)
                .toList();
    }

    @Override
    public boolean possuiContratoAtivoPorFornecedorId(String fornecedorId) {
        var statusInativos = List.of(StatusContrato.ENCERRADO, StatusContrato.CANCELADO);
        return jpaRepository.existsByFornecedorIdAndStatusNotIn(fornecedorId, statusInativos);
    }

    @Override
    @Transactional
    public void remover(String id) {
        jpaRepository.deleteById(id);
    }
}
