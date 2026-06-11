package presentationbackend.controller;

import java.time.LocalDateTime;

/**
 * Corpo padrão de resposta de erro da API.
 */
public record ErrorResponse(int status, String erro, LocalDateTime timestamp) {

    public static ErrorResponse de(int status, String mensagem) {
        return new ErrorResponse(status, mensagem, LocalDateTime.now());
    }
}
