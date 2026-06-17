package infrastructure.persistence.financeiro.adapter;

import domain.financeiro.entity.Despesa;
import domain.financeiro.repository.DespesaRepository;
import domain.financeiro.valueobject.StatusDespesa;
import infrastructure.persistence.financeiro.mapper.DespesaMapper;
import infrastructure.persistence.financeiro.repository.DespesaJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DespesaRepositoryJpaAdapter implements DespesaRepository {

    private final DespesaJpaRepository jpaRepository;

    public DespesaRepositoryJpaAdapter(DespesaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Despesa salvar(Despesa despesa) {
        return DespesaMapper.paraDominio(jpaRepository.save(DespesaMapper.paraJpa(despesa)));
    }

    @Override
    public Optional<Despesa> buscarPorId(String id) {
        return jpaRepository.findById(id).map(DespesaMapper::paraDominio);
    }

    @Override
    public void excluir(String id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<Despesa> listarPorEventoId(String eventoId) {
        return jpaRepository.findByEventoId(eventoId).stream()
                .map(DespesaMapper::paraDominio)
                .toList();
    }

    @Override
    public List<Despesa> listarPorEventoECategoria(String eventoId, domain.financeiro.valueobject.CategoriaDespesa categoria) {
        return jpaRepository.findByEventoIdAndCategoria(eventoId, categoria).stream()
                .map(DespesaMapper::paraDominio)
                .toList();
    }

    @Override
    public List<Despesa> listarPorEventoEFornecedor(String eventoId, String fornecedorId) {
        return jpaRepository.findByEventoIdAndFornecedorId(eventoId, fornecedorId).stream()
                .map(DespesaMapper::paraDominio)
                .toList();
    }

    @Override
    public List<Despesa> listarPorFornecedorId(String fornecedorId) {
        return jpaRepository.findByFornecedorId(fornecedorId).stream()
                .map(DespesaMapper::paraDominio)
                .toList();
    }

    @Override
    public java.math.BigDecimal somarValoresPorEventoECategoria(String eventoId, domain.financeiro.valueobject.CategoriaDespesa categoria) {
        return listarPorEventoECategoria(eventoId, categoria).stream()
                .map(Despesa::getValor)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }

    @Override
    public java.math.BigDecimal somarValoresAtivosPorEventoECategoria(String eventoId, domain.financeiro.valueobject.CategoriaDespesa categoria) {
        return listarPorEventoECategoria(eventoId, categoria).stream()
                .filter(d -> d.getStatus() != StatusDespesa.REJEITADA)
                .map(Despesa::getValor)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }
}
