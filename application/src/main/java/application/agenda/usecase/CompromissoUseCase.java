package application.agenda.usecase;

import application.agenda.dto.CompromissoResponse;
import application.agenda.dto.CriarCompromissoRequest;
import application.agenda.dto.EditarCompromissoRequest;

import java.util.List;

public interface CompromissoUseCase {

    CompromissoResponse criar(CriarCompromissoRequest request);

    CompromissoResponse editar(String id, EditarCompromissoRequest request);

    void remover(String id);

    CompromissoResponse buscar(String id);

    List<CompromissoResponse> listarPorGestor(String gestorId);

    List<CompromissoResponse> listarTodos();

    CompromissoResponse iniciar(String id);

    CompromissoResponse concluir(String id);

    CompromissoResponse cancelar(String id);
}
