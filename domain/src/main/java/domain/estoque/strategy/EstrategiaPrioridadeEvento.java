package domain.estoque.strategy;

import domain.evento.entity.Evento;

import java.time.LocalDateTime;

/**
 * Strategy para calculo da prioridade de eventos no contexto da redistribuicao
 * de estoque (RN2 da feature de redistribuicao). Permite trocar a politica de
 * priorizacao sem alterar o servico de redistribuicao.
 */
public interface EstrategiaPrioridadeEvento {

    double calcular(Evento evento, LocalDateTime referencia);
}
