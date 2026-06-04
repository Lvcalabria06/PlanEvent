package domain.financeiro.template;

import domain.evento.entity.Evento;
import domain.evento.repository.EventoRepository;
import domain.financeiro.entity.Despesa;
import domain.financeiro.repository.DespesaRepository;
import domain.fornecedor.entity.Fornecedor;
import domain.fornecedor.repository.FornecedorRepository;

import java.math.BigDecimal;

/**
 * Template Method do fluxo de processamento de despesa (registro e atualização).
 * Define a sequência de validações; subclasses customizam etapas específicas.
 */
public abstract class ProcessadorDespesaTemplateMethod {

    protected final EventoRepository eventoRepository;
    protected final FornecedorRepository fornecedorRepository;
    protected final ValidadorLimiteOrcamentarioTemplateMethod validadorLimite;

    protected ProcessadorDespesaTemplateMethod(EventoRepository eventoRepository,
                                                FornecedorRepository fornecedorRepository,
                                                ValidadorLimiteOrcamentarioTemplateMethod validadorLimite) {
        this.eventoRepository = eventoRepository;
        this.fornecedorRepository = fornecedorRepository;
        this.validadorLimite = validadorLimite;
    }

    public final Despesa executar(Despesa despesa,
                                    BigDecimal valorAtualNoTotal,
                                    BigDecimal valorConsiderado) {
        validarEventoAtivo(despesa.getEventoId());
        validarFornecedor(despesa);
        antesDaValidacaoOrcamento(despesa, valorConsiderado);
        validadorLimite.validar(despesa, valorAtualNoTotal, valorConsiderado);
        return finalizar(despesa, valorConsiderado);
    }

    protected void validarEventoAtivo(String eventoId) {
        Evento evento = eventoRepository.buscarPorId(eventoId)
                .orElseThrow(() -> new IllegalArgumentException("Evento inválido ou não encontrado."));
        if (evento.isConcluido()) {
            throw new IllegalArgumentException("Não é permitido operar despesas em evento concluído.");
        }
    }

    protected void validarFornecedor(Despesa despesa) {
        Fornecedor fornecedor = fornecedorRepository.buscarPorId(despesa.getFornecedorId())
                .orElseThrow(() -> new IllegalArgumentException("Fornecedor inválido ou não encontrado."));
        if (!fornecedor.isAtivo()) {
            throw new IllegalArgumentException("Fornecedor inativo não pode ser vinculado a despesas.");
        }
    }

    protected void antesDaValidacaoOrcamento(Despesa despesa, BigDecimal valorConsiderado) {
        // hook opcional para subclasses
    }

    protected abstract Despesa finalizar(Despesa despesa, BigDecimal valorConsiderado);
}
