package domain.equipe.service;

import domain.equipe.entity.Equipe;

import java.util.List;

public interface EquipeService {
    Equipe criarEquipe(Equipe equipe);
    Equipe editarEquipe(Equipe equipe);
    Equipe buscarEquipe(String id);
    List<Equipe> listarEquipesPorEvento(String eventoId);
    void removerEquipe(String id);
}