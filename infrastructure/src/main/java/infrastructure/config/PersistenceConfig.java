package infrastructure.config;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.config.BootstrapMode;

/**
 * Habilita a camada de persistência do módulo de infraestrutura: varredura das
 * entidades JPA, dos repositórios Spring Data e dos adapters de repositório.
 *
 * BootstrapMode.DEFERRED garante que os repositórios Spring Data só sejam
 * inicializados após o entityManagerFactory estar disponível — necessário no
 * Spring Boot 4.x / Spring Framework 7.x onde a ordem de inicialização mudou.
 */
@Configuration
@EntityScan(basePackages = "infrastructure.persistence")
@EnableJpaRepositories(basePackages = "infrastructure.persistence", bootstrapMode = BootstrapMode.DEFERRED)
@ComponentScan(basePackages = "infrastructure.persistence")
public class PersistenceConfig {
}
