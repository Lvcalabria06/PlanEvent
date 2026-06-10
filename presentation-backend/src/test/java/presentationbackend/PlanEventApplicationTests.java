package presentationbackend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Smoke test: garante que o contexto Spring sobe por inteiro (wiring dos beans
 * de domínio/casos de uso, controllers, adapters JPA e stubs de scaffolding),
 * usando H2 em memória no lugar do PostgreSQL.
 */
@SpringBootTest
class PlanEventApplicationTests {

    @Test
    void contextLoads() {
        // Sucesso = contexto carregou e o seed de dados demo executou sem erros.
    }
}
