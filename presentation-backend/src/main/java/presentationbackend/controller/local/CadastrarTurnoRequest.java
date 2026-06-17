package presentationbackend.controller.local;

public record CadastrarTurnoRequest(
        String nome,
        String horaInicio,
        String horaFim,
        String diasDaSemana,
        Integer capacidade,
        String observacoes
) {}
