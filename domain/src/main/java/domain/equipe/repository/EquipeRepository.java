package domain.equipe.repository;

import domain.equipe.entity.Equipe;

import java.util.List;
import java.util.Optional;

public interface EquipeRepository {
    Equipe salvar(Equipe equipe);
    Optional<Equipe> buscarPorId(String id);
    List<Equipe> listarPorEventoId(String eventoId);
    void remover(String id);

    boolean existeEquipeComNomeNoEvento(String eventoId, String nome);
    boolean funcionarioJaEstaEmEquipeNoEvento(String funcionarioId, String eventoId);
    boolean existeFuncionarioVinculado(String funcionarioId);
}