package domain.tarefa.iterator;

import domain.tarefa.entity.Tarefa;
import domain.tarefa.repository.TarefaRepository;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

/**
 * Iterator (GoF) que percorre, em profundidade, todas as tarefas predecessoras
 * (transitivas) de uma tarefa raiz, resolvendo cada nó pelo repositório.
 * A própria raiz não é incluída na iteração e ciclos são tratados via conjunto
 * de visitados, garantindo término.
 */
public class IteradorDependencias implements Iterator<Tarefa> {

    private final TarefaRepository tarefaRepository;
    private final Deque<String> pilha = new ArrayDeque<>();
    private final Set<String> visitados = new HashSet<>();
    private Tarefa proxima;

    public IteradorDependencias(TarefaRepository tarefaRepository, String tarefaRaizId) {
        this.tarefaRepository = tarefaRepository;
        tarefaRepository.buscarPorId(tarefaRaizId)
                .ifPresent(raiz -> empilhar(raiz.listarDependencias()));
        avancar();
    }

    private void empilhar(List<String> ids) {
        for (String id : ids) {
            if (!visitados.contains(id)) {
                pilha.push(id);
            }
        }
    }

    private void avancar() {
        proxima = null;
        while (!pilha.isEmpty()) {
            String id = pilha.pop();
            if (visitados.contains(id)) {
                continue;
            }
            visitados.add(id);
            Optional<Tarefa> tarefaOpt = tarefaRepository.buscarPorId(id);
            if (tarefaOpt.isPresent()) {
                proxima = tarefaOpt.get();
                empilhar(proxima.listarDependencias());
                return;
            }
        }
    }

    @Override
    public boolean hasNext() {
        return proxima != null;
    }

    @Override
    public Tarefa next() {
        if (proxima == null) {
            throw new NoSuchElementException("Não há mais tarefas predecessoras para iterar.");
        }
        Tarefa atual = proxima;
        avancar();
        return atual;
    }
}
