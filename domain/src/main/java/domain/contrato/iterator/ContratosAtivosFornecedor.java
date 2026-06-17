package domain.contrato.iterator;

import domain.contrato.entity.Contrato;
import domain.contrato.repository.ContratoRepository;

import java.util.Iterator;

/**
 * Coleção agregada que representa os contratos ativos de um fornecedor.
 * Expõe um {@link IteradorContratosAtivos} próprio, aplicando o padrão
 * Iterator (GoF) sobre os registros navegados pelo repositório.
 */
public class ContratosAtivosFornecedor implements Iterable<Contrato> {

    private final ContratoRepository contratoRepository;
    private final String fornecedorId;

    public ContratosAtivosFornecedor(ContratoRepository contratoRepository, String fornecedorId) {
        if (contratoRepository == null || fornecedorId == null) {
            throw new IllegalArgumentException("Repositório e ID do fornecedor são obrigatórios.");
        }
        this.contratoRepository = contratoRepository;
        this.fornecedorId = fornecedorId;
    }

    @Override
    public Iterator<Contrato> iterator() {
        return new IteradorContratosAtivos(contratoRepository, fornecedorId);
    }

    /**
     * Indica se o fornecedor possui ao menos um contrato ativo.
     */
    public boolean possuiAtivos() {
        return iterator().hasNext();
    }
}
