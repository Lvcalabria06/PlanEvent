package application.estoque.dto;

import java.time.LocalDateTime;

public record ItemEstoqueResponse(
        String id,
        String nome,
        int quantidadeTotal,
        int quantidadeDisponivel,
        boolean ativo,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao) {
}
