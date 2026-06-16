package application.evento.dto;

import java.util.List;

public record RegistrarAlternativasContingenciaRequest(
        List<String> localIdsOrdenados
) {}
