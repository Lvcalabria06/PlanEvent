package domain.contrato.iterator;

import domain.contrato.entity.Contrato;
import domain.contrato.repository.ContratoRepository;
import domain.contrato.valueobject.StatusContrato;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Iterator (GoF) que percorre apenas os contratos ativos (não encerrados e
 * não cancelados) de um fornecedor. Utiliza o repositório para carregar a
 * lista uma única vez e filtra os registros inelegíveis durante a iteração.
 */
public class IteradorContratosAtivos implements Iterator<Contrato> {

    private final List<Contrato> todos;
    private int indice;
    private Contrato proxima;

    public IteradorContratosAtivos(ContratoRepository contratoRepository, String fornecedorId) {
        this.todos = contratoRepository.listarPorFornecedorId(fornecedorId);
        this.indice = 0;
        avancar();
    }

    private void avancar() {
        proxima = null;
        while (indice < todos.size()) {
            Contrato candidato = todos.get(indice++);
            if (isAtivo(candidato)) {
                proxima = candidato;
                return;
            }
        }
    }

    private boolean isAtivo(Contrato contrato) {
        StatusContrato status = contrato.getStatus();
        return status != StatusContrato.ENCERRADO && status != StatusContrato.CANCELADO;
    }

    @Override
    public boolean hasNext() {
        return proxima != null;
    }

    @Override
    public Contrato next() {
        if (proxima == null) {
            throw new NoSuchElementException("Não há mais contratos ativos para iterar.");
        }
        Contrato atual = proxima;
        avancar();
        return atual;
    }
}
