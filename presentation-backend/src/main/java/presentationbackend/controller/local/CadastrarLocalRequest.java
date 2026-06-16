package presentationbackend.controller.local;

import java.math.BigDecimal;

public record CadastrarLocalRequest(
        String nome,
        int capacidade,
        String endereco,
        String tipo,
        String infraestrutura,
        String restricoes,
        BigDecimal custo
) {}
