package domain.financeiro.template;

import domain.evento.repository.EventoRepository;
import domain.financeiro.entity.Despesa;
import domain.financeiro.repository.DespesaRepository;
import domain.fornecedor.repository.FornecedorRepository;

import java.math.BigDecimal;

public class ProcessadorRegistroDespesaTemplateMethod extends ProcessadorDespesaTemplateMethod {

    private final DespesaRepository despesaRepository;

    public ProcessadorRegistroDespesaTemplateMethod(EventoRepository eventoRepository,
                                                      FornecedorRepository fornecedorRepository,
                                                      ValidadorLimiteOrcamentarioTemplateMethod validadorLimite,
                                                      DespesaRepository despesaRepository) {
        super(eventoRepository, fornecedorRepository, validadorLimite);
        this.despesaRepository = despesaRepository;
    }

    @Override
    protected Despesa finalizar(Despesa despesa, BigDecimal valorConsiderado) {
        return despesaRepository.salvar(despesa);
    }
}
