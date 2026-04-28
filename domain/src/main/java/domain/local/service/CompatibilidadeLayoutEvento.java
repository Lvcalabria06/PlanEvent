package domain.local.service;

public class CompatibilidadeLayoutEvento {
    private final String layoutId;
    private final String nomeLayout;
    private final int capacidadeMaxima;
    private final boolean compativel;
    private final String justificativa;

    public CompatibilidadeLayoutEvento(
            String layoutId,
            String nomeLayout,
            int capacidadeMaxima,
            boolean compativel,
            String justificativa) {
        this.layoutId = layoutId;
        this.nomeLayout = nomeLayout;
        this.capacidadeMaxima = capacidadeMaxima;
        this.compativel = compativel;
        this.justificativa = justificativa;
    }

    public String getLayoutId() {
        return layoutId;
    }

    public String getNomeLayout() {
        return nomeLayout;
    }

    public int getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    public boolean isCompativel() {
        return compativel;
    }

    public String getJustificativa() {
        return justificativa;
    }
}
