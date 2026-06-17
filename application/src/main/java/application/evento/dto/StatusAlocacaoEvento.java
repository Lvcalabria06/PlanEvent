package application.evento.dto;

/**
 * Estado resumido da alocação de local para orientar o fluxo no front
 * (planejar local antes de confirmar preparação; risco após confirmar).
 */
public enum StatusAlocacaoEvento {
    SEM_LOCAL_DEFINIDO,
    LOCAL_DEFINIDO_PREPARACAO_PENDENTE,
    PREPARACAO_CONFIRMADA_AGUARDANDO_RISCO,
    EVENTO_CONCLUIDO
}
