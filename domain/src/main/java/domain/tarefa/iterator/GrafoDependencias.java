package domain.tarefa.iterator;

import domain.tarefa.entity.Tarefa;
import domain.tarefa.repository.TarefaRepository;

import java.util.Iterator;

/**
 * Coleção agregada que representa o grafo de dependências (predecessoras
 * transitivas) de uma tarefa raiz. Expõe um {@link IteradorDependencias}
 * próprio, aplicando o padrão Iterator (GoF) sobre o grafo navegado pelo
 * repositório.
 */
public class GrafoDependencias implements Iterable<Tarefa> {

    private final TarefaRepository tarefaRepository;
    private final String tarefaRaizId;

    public GrafoDependencias(TarefaRepository tarefaRepository, String tarefaRaizId) {
        if (tarefaRepository == null || tarefaRaizId == null) {
            throw new IllegalArgumentException("Repositório e tarefa raiz são obrigatórios.");
        }
        this.tarefaRepository = tarefaRepository;
        this.tarefaRaizId = tarefaRaizId;
    }

    @Override
    public Iterator<Tarefa> iterator() {
        return new IteradorDependencias(tarefaRepository, tarefaRaizId);
    }

    /**
     * Indica se a tarefa informada é uma predecessora (direta ou indireta) da raiz.
     */
    public boolean contem(String tarefaId) {
        for (Tarefa tarefa : this) {
            if (tarefa.getId().equals(tarefaId)) {
                return true;
            }
        }
        return false;
    }
}
