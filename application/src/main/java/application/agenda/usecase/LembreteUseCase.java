package application.agenda.usecase;

import application.agenda.dto.AlertaLembreteResponse;
import application.agenda.dto.CriarLembreteRequest;
import application.agenda.dto.EditarLembreteRequest;
import application.agenda.dto.LembreteResponse;

import java.util.List;

public interface LembreteUseCase {

    LembreteResponse criar(CriarLembreteRequest request);

    LembreteResponse editar(String id, EditarLembreteRequest request);

    void remover(String id);

    LembreteResponse buscar(String id);

    List<LembreteResponse> listarPorCompromisso(String compromissoId);

    List<LembreteResponse> listarPorEvento(String eventoId);

    List<LembreteResponse> listarPorGestor(String gestorId);

    List<LembreteResponse> listarTodos();

    LembreteResponse dispararNotificacao(String id);

    List<AlertaLembreteResponse> processarVencidos();
}
