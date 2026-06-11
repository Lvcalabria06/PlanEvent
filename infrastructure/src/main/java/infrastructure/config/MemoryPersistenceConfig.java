package infrastructure.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = "infrastructure.persistence.memory")
@Import(DomainServicesConfig.class)
public class MemoryPersistenceConfig {
}
