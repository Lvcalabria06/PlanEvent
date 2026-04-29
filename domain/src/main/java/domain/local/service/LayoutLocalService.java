package domain.local.service;

import domain.local.entity.LayoutLocal;

import java.util.List;

public interface LayoutLocalService {
    LayoutLocal cadastrarLayout(String localId, String nome, String descricao, int capacidadeMaxima, String usuarioResponsavel);

    LayoutLocal atualizarLayout(String localId, String layoutId, String nome, String descricao, int capacidadeMaxima, String usuarioResponsavel);

    List<LayoutLocal> listarLayouts(String localId);

    List<CompatibilidadeLayoutEvento> analisarCompatibilidadeParaEvento(String eventoId, String localId);

    void associarLayoutAoEvento(String eventoId, String localId, String layoutId, String justificativaExcecao, String usuarioResponsavel);
}
