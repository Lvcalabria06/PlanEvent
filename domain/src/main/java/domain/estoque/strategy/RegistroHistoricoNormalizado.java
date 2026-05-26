package domain.estoque.strategy;

public class RegistroHistoricoNormalizado {

    private final String eventoId;
    private final String itemId;
    private final String categoria;
    private final double quantidadeNormalizada;
    private final double pesoSimilaridade;
    private final double pesoRecencia;
    private final double fatorNormalizacao;

    public RegistroHistoricoNormalizado(String eventoId,
                                        String itemId,
                                        String categoria,
                                        double quantidadeNormalizada,
                                        double pesoSimilaridade,
                                        double pesoRecencia,
                                        double fatorNormalizacao) {
        this.eventoId = eventoId;
        this.itemId = itemId;
        this.categoria = categoria;
        this.quantidadeNormalizada = quantidadeNormalizada;
        this.pesoSimilaridade = pesoSimilaridade;
        this.pesoRecencia = pesoRecencia;
        this.fatorNormalizacao = fatorNormalizacao;
    }

    public double pesoTotal() {
        return pesoSimilaridade + pesoRecencia;
    }

    public String getEventoId() { return eventoId; }
    public String getItemId() { return itemId; }
    public String getCategoria() { return categoria; }
    public double getQuantidadeNormalizada() { return quantidadeNormalizada; }
    public double getPesoSimilaridade() { return pesoSimilaridade; }
    public double getPesoRecencia() { return pesoRecencia; }
    public double getFatorNormalizacao() { return fatorNormalizacao; }
}
