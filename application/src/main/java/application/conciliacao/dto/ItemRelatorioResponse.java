package application.conciliacao.dto;

import domain.conciliacao.valueobject.MetodoConciliacao;
import domain.conciliacao.valueobject.StatusConciliacao;

public record ItemRelatorioResponse(
        String despesaId,
        String contratoId,
        StatusConciliacao status,
        MetodoConciliacao metodo
) {}
