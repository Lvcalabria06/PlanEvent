package domain.local.util;

import java.time.LocalDateTime;

public final class IntervaloAgenda {

    private IntervaloAgenda() {
    }

    public static void validarFimAposInicio(LocalDateTime inicio, LocalDateTime fim) {
        if (inicio == null || fim == null) {
            throw new IllegalArgumentException("Início e fim são obrigatórios.");
        }
        if (!inicio.isBefore(fim)) {
            throw new IllegalArgumentException("O horário de início deve ser anterior ao fim da reserva.");
        }
    }

    public static boolean seSobrepoe(LocalDateTime aInicio, LocalDateTime aFim, LocalDateTime bInicio, LocalDateTime bFim) {
        if (aInicio == null || aFim == null || bInicio == null || bFim == null) {
            return false;
        }
        if (!aInicio.isBefore(aFim) || !bInicio.isBefore(bFim)) {
            return false;
        }
        return aInicio.isBefore(bFim) && bInicio.isBefore(aFim);
    }
}
