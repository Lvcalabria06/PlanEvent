package application.equipe.usecase;

import application.equipe.dto.CriarEquipeRequest;
import application.equipe.dto.EditarEquipeRequest;
import application.equipe.dto.EquipeResponse;
import application.equipe.dto.MembroEquipeResponse;

import java.util.List;

/**
 * Casos de uso de gerenciamento de equipes, orquestrando o serviço de domínio
 * e expondo DTOs à camada de apresentação.
 */
public interface EquipeUseCase {

    EquipeResponse criar(CriarEquipeRequest request);

    EquipeResponse editar(String equipeId, EditarEquipeRequest request);

    void remover(String equipeId);

    EquipeResponse buscar(String equipeId);

    List<EquipeResponse> listarPorEvento(String eventoId);

    List<MembroEquipeResponse> filtrarMembros(String equipeId, String expressao);
}
