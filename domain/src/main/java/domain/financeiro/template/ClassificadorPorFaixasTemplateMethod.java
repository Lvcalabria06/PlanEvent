package domain.financeiro.template;

public class ClassificadorPorFaixasTemplateMethod extends ClassificadorDesvioTemplateMethod {

    private static final double LIMIAR_ATENCAO = 10.0;
    private static final double LIMIAR_CRITICO = 20.0;

    @Override
    protected double limiteAtencao() {
        return LIMIAR_ATENCAO;
    }

    @Override
    protected double limiteCritico() {
        return LIMIAR_CRITICO;
    }
}
