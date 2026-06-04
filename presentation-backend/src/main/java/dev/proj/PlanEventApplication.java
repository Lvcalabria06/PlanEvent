package dev.proj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
    "dev.proj",
    "infrastructure",
    "domain"
})
public class PlanEventApplication {
    public static void main(String[] args) {
        SpringApplication.run(PlanEventApplication.class, args);
    }
}
