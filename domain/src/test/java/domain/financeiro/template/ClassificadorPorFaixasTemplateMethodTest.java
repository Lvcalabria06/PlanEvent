package domain.financeiro.template;

import domain.financeiro.valueobject.ClassificacaoDesvio;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClassificadorPorFaixasTemplateMethodTest {

    private final ClassificadorPorFaixasTemplateMethod classificador =
            new ClassificadorPorFaixasTemplateMethod();

    @Test
    void classificaDesvioAbaixoDeDezPorcentoComoNormal() {
        assertEquals(ClassificacaoDesvio.NORMAL, classificador.classificar(-10.0));
        assertEquals(ClassificacaoDesvio.NORMAL, classificador.classificar(9.9));
    }

    @Test
    void classificaDesvioEntreDezEVintePorcentoComoAtencao() {
        assertEquals(ClassificacaoDesvio.ATENCAO, classificador.classificar(10.0));
        assertEquals(ClassificacaoDesvio.ATENCAO, classificador.classificar(20.0));
    }

    @Test
    void classificaDesvioAcimaDeVintePorcentoComoCritico() {
        assertEquals(ClassificacaoDesvio.CRITICO, classificador.classificar(20.1));
        assertEquals(ClassificacaoDesvio.CRITICO, classificador.classificar(30.0));
    }
}
