package domain.equipe.service;

import domain.equipe.entity.Equipe;
import domain.equipe.entity.MembroEquipe;

import java.util.List;

public interface EquipeService {
    Equipe criarEquipe(Equipe equipe);
    Equipe editarEquipe(Equipe equipe);
    Equipe buscarEquipe(String id);
    List<Equipe> listarEquipesPorEvento(String eventoId);
    void removerEquipe(String id);
    List<MembroEquipe> filtrarMembros(String equipeId, String expressaoFiltragem);
}