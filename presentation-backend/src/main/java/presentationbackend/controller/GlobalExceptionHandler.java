package presentationbackend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Traduz as exceções de domínio em respostas HTTP:
 * <ul>
 *   <li>{@link IllegalArgumentException} (dados inválidos/inexistentes) -> 400</li>
 *   <li>{@link IllegalStateException} (violação de regra de negócio) -> 409</li>
 *   <li>demais erros -> 500</li>
 * </ul>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> tratarArgumentoInvalido(IllegalArgumentException ex) {
        return resposta(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> tratarRegraDeNegocio(IllegalStateException ex) {
        return resposta(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> tratarErroInesperado(Exception ex) {
        return resposta(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    private ResponseEntity<ErrorResponse> resposta(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status).body(ErrorResponse.de(status.value(), mensagem));
    }
}
