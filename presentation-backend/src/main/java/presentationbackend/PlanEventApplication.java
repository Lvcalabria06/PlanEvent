package presentationbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import presentationbackend.config.DotEnvLoader;

/**
 * Ponto de entrada da aplicação PlanEvent. Varre os pacotes da apresentação e da
 * infraestrutura (onde estão os adapters JPA e a {@code PersistenceConfig}). Os
 * casos de uso e serviços de domínio são registrados manualmente em
 * {@link presentationbackend.config.BeanConfig}, mantendo as camadas internas
 * livres de anotações de framework.
 */
@SpringBootApplication(scanBasePackages = {"presentationbackend", "infrastructure", "domain"})
public class PlanEventApplication {

    public static void main(String[] args) {
        DotEnvLoader.load();
        SpringApplication.run(PlanEventApplication.class, args);
    }
}
