package infrastructure.config;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Habilita a camada de persistência do módulo de infraestrutura: varredura das
 * entidades JPA, dos repositórios Spring Data e dos adapters de repositório.
 * A camada de apresentação precisa apenas importar esta configuração (ou incluir
 * o pacote {@code infrastructure} no component scan).
 */
@Configuration
@EntityScan(basePackages = "infrastructure.persistence")
@EnableJpaRepositories(basePackages = "infrastructure.persistence")
@ComponentScan(basePackages = "infrastructure.persistence")
public class PersistenceConfig {
}
