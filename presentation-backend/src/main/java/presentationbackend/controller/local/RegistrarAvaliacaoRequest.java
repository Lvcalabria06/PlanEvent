package presentationbackend.controller.local;

import java.util.Map;

public record RegistrarAvaliacaoRequest(
        String eventoId,
        Map<String, Integer> notasPorCriterio,
        String justificativa,
        String usuarioResponsavel
) {}
