package domain.financeiro.template;

import domain.evento.repository.EventoRepository;
import domain.financeiro.entity.Despesa;
import domain.financeiro.repository.DespesaRepository;
import domain.fornecedor.repository.FornecedorRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProcessadorAtualizacaoDespesaTemplateMethod extends ProcessadorDespesaTemplateMethod {

    private final DespesaRepository despesaRepository;
    private final LocalDateTime novaData;

    public ProcessadorAtualizacaoDespesaTemplateMethod(EventoRepository eventoRepository,
                                                        FornecedorRepository fornecedorRepository,
                                                        ValidadorLimiteOrcamentarioTemplateMethod validadorLimite,
                                                        DespesaRepository despesaRepository,
                                                        LocalDateTime novaData) {
        super(eventoRepository, fornecedorRepository, validadorLimite);
        this.despesaRepository = despesaRepository;
        this.novaData = novaData;
    }

    @Override
    protected void validarFornecedor(Despesa despesa) {
        // RN2 na atualização: fornecedor não muda; validação já ocorreu no registro.
    }

    @Override
    protected void antesDaValidacaoOrcamento(Despesa despesa, BigDecimal valorConsiderado) {
        despesa.garantirPodeSerAlterada();
    }

    @Override
    protected Despesa finalizar(Despesa despesa, BigDecimal valorConsiderado) {
        despesa.corrigirValor(valorConsiderado);
        if (novaData != null) {
            despesa.corrigirData(novaData);
        }
        return despesaRepository.salvar(despesa);
    }
}
