package domain.financeiro.valueobject;

public class RecomendacaoFinanceira {

    private final TipoRecomendacaoFinanceira tipo;
    private final String mensagem;
    private final CategoriaDespesa categoriaRelacionada;

    public RecomendacaoFinanceira(TipoRecomendacaoFinanceira tipo,
                                   String mensagem,
                                   CategoriaDespesa categoriaRelacionada) {
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo da recomendação é obrigatório.");
        }
        if (mensagem == null || mensagem.isBlank()) {
            throw new IllegalArgumentException("Mensagem da recomendação é obrigatória.");
        }
        this.tipo = tipo;
        this.mensagem = mensagem.trim();
        this.categoriaRelacionada = categoriaRelacionada;
    }

    public RecomendacaoFinanceira(TipoRecomendacaoFinanceira tipo, String mensagem) {
        this(tipo, mensagem, null);
    }

    public TipoRecomendacaoFinanceira getTipo() {
        return tipo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public CategoriaDespesa getCategoriaRelacionada() {
        return categoriaRelacionada;
    }
}
