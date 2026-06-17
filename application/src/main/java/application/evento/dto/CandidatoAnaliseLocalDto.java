package application.evento.dto;

import domain.evento.valueobject.ClassificacaoAlocacaoLocal;

import java.math.BigDecimal;

public record CandidatoAnaliseLocalDto(
        String localId,
        String nomeLocal,
        ClassificacaoAlocacaoLocal classificacao,
        String justificativa,
        BigDecimal custo,
        int capacidade,
        boolean acimaDoTeto,
        boolean capacidadeOk,
        boolean agendaOk,
        boolean podeSerPrincipal
) {}
