package domain.evento.strategy;

import domain.evento.planejamento.CandidatoAnaliseLocal;

/**
 * Padrao Strategy aplicado a classificacao de candidatos a local principal.
 * Cada estrategia decide se e aplicavel ao contexto e produz o
 * {@link CandidatoAnaliseLocal} correspondente, permitindo evoluir criterios
 * (agenda, custo, infraestrutura) sem alterar o servico coordenador.
 */
public interface EstrategiaCriterioAlocacaoLocal {

    boolean aplicavel(ContextoAlocacaoLocal contexto);

    CandidatoAnaliseLocal avaliar(ContextoAlocacaoLocal contexto);
}
