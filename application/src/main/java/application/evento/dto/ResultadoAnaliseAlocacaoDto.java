package application.evento.dto;

import java.util.List;

public record ResultadoAnaliseAlocacaoDto(
        String eventoId,
        List<CandidatoAnaliseLocalDto> candidatos
) {}
