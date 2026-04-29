package domain.evento.planejamento;

import domain.evento.valueobject.ClassificacaoAlocacaoLocal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ResultadoAnaliseAlocacao {

    private static final Map<ClassificacaoAlocacaoLocal, Integer> ORDEM_CLASSIFICACAO = new EnumMap<>(ClassificacaoAlocacaoLocal.class);

    static {
        ORDEM_CLASSIFICACAO.put(ClassificacaoAlocacaoLocal.RECOMENDADO, 0);
        ORDEM_CLASSIFICACAO.put(ClassificacaoAlocacaoLocal.VIAVEL_COM_RESSALVA, 1);
        ORDEM_CLASSIFICACAO.put(ClassificacaoAlocacaoLocal.INADEQUADO, 2);
        ORDEM_CLASSIFICACAO.put(ClassificacaoAlocacaoLocal.INDISPONIVEL, 3);
    }

    private final String eventoId;
    private final List<CandidatoAnaliseLocal> candidatos;

    public ResultadoAnaliseAlocacao(String eventoId, List<CandidatoAnaliseLocal> candidatos) {
        this.eventoId = eventoId;
        List<CandidatoAnaliseLocal> copia = new ArrayList<>(candidatos);
        copia.sort(Comparator
                .comparing((CandidatoAnaliseLocal c) -> ORDEM_CLASSIFICACAO.get(c.getClassificacao()))
                .thenComparing(c -> c.getCusto() != null ? c.getCusto() : BigDecimal.ZERO));
        this.candidatos = Collections.unmodifiableList(copia);
    }

    public String getEventoId() {
        return eventoId;
    }

    public List<CandidatoAnaliseLocal> getCandidatos() {
        return candidatos;
    }
}
