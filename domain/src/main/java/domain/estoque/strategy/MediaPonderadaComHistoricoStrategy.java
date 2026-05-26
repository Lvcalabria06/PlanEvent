package domain.estoque.strategy;

import domain.estoque.entity.ItemPrevisao;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Estrategia principal: usa media ponderada por similaridade + recencia,
 * descartando outliers via IQR (RN1, RN3, RN4, RN5, RN7).
 */
public class MediaPonderadaComHistoricoStrategy implements EstrategiaCalculoItemPrevisao {

    private static final int MIN_HISTORICOS = 2;

    @Override
    public boolean aplicavel(ContextoCalculoItem contexto) {
        List<RegistroHistoricoNormalizado> base = removerOutliers(contexto.getHistoricosDoItem());
        return base.size() >= MIN_HISTORICOS;
    }

    @Override
    public ResultadoCalculoItem calcular(ContextoCalculoItem contexto) {
        List<RegistroHistoricoNormalizado> base = removerOutliers(contexto.getHistoricosDoItem());

        base.sort(Comparator.comparingDouble(RegistroHistoricoNormalizado::pesoTotal).reversed());

        double somaPesos = base.stream().mapToDouble(RegistroHistoricoNormalizado::pesoTotal).sum();
        double somaPonderada = base.stream()
                .mapToDouble(h -> h.getQuantidadeNormalizada() * h.pesoTotal())
                .sum();
        int estimada = (int) Math.round(somaPonderada / somaPesos);

        List<Double> valores = base.stream()
                .map(RegistroHistoricoNormalizado::getQuantidadeNormalizada)
                .sorted()
                .collect(Collectors.toList());
        int minimo = (int) Math.floor(percentil(valores, 0.25));
        int maximo = (int) Math.ceil(percentil(valores, 0.75));

        String explicacao = montarExplicacao(base);

        ItemPrevisao item = new ItemPrevisao(
                contexto.getPrevisaoId(),
                contexto.getItemId(),
                contexto.getCategoria(),
                estimada,
                Math.max(0, minimo),
                Math.max(estimada, maximo),
                explicacao
        );

        return new ResultadoCalculoItem(item, false);
    }

    private String montarExplicacao(List<RegistroHistoricoNormalizado> base) {
        String eventos = base.stream()
                .map(RegistroHistoricoNormalizado::getEventoId)
                .collect(Collectors.joining(", "));

        String pesos = base.stream()
                .map(h -> String.format(
                        "%s[similaridade=%.2f, recencia=%.2f, total=%.2f]",
                        h.getEventoId(), h.getPesoSimilaridade(), h.getPesoRecencia(), h.pesoTotal()))
                .collect(Collectors.joining("; "));

        double fatorMedio = base.stream()
                .mapToDouble(RegistroHistoricoNormalizado::getFatorNormalizacao)
                .average()
                .orElse(1.0);

        return "Eventos usados: " + eventos
                + ". Pesos aplicados (por evento): " + pesos
                + ". Ajustes: normalizacao por porte e duracao (fator medio="
                + String.format("%.2f", fatorMedio)
                + "); outliers descartados via IQR.";
    }

    private List<RegistroHistoricoNormalizado> removerOutliers(List<RegistroHistoricoNormalizado> base) {
        if (base == null || base.isEmpty()) {
            return new ArrayList<>();
        }
        if (base.size() < 4) {
            return new ArrayList<>(base);
        }
        List<Double> valores = base.stream()
                .map(RegistroHistoricoNormalizado::getQuantidadeNormalizada)
                .sorted()
                .collect(Collectors.toList());
        double q1 = percentil(valores, 0.25);
        double q3 = percentil(valores, 0.75);
        double iqr = q3 - q1;
        double limiteSuperior = q3 + (1.5 * iqr);
        return base.stream()
                .filter(h -> h.getQuantidadeNormalizada() <= limiteSuperior)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private double percentil(List<Double> valores, double percentil) {
        if (valores.isEmpty()) {
            return 0.0;
        }
        int indice = (int) Math.floor((valores.size() - 1) * percentil);
        return valores.get(indice);
    }
}
