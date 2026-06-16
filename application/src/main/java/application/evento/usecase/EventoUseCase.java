package application.evento.usecase;

import application.evento.dto.CriarEventoRequest;
import application.evento.dto.EditarEventoRequest;
import application.evento.dto.EventoResponse;

import java.util.List;

public interface EventoUseCase {

    EventoResponse criar(CriarEventoRequest request);

    EventoResponse editar(String id, EditarEventoRequest request);

    EventoResponse buscar(String id);

    List<EventoResponse> listar();

    EventoResponse confirmarPreparacao(String id);

    void cancelar(String id);
}
