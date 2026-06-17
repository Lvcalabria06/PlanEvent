package presentationbackend.scaffolding;

import domain.local.entity.Local;
import domain.local.repository.LocalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * Semeia locais de demonstração para o módulo de eventos/alocação enquanto
 * persistência real de locais não estiver disponível.
 */
public final class EventoLocaisSeeder {

    private static final Logger log = LoggerFactory.getLogger(EventoLocaisSeeder.class);

    private EventoLocaisSeeder() {}

    public static void semearSeVazio(LocalRepository localRepository) {
        if (!localRepository.listarTodos().isEmpty()) {
            return;
        }

        localRepository.salvar(new Local(
                "Auditório Central",
                500,
                "Av. Principal, 100",
                "Auditório",
                "som, projetor, wifi",
                BigDecimal.valueOf(2500)));
        localRepository.salvar(new Local(
                "Sala Executiva",
                120,
                "Rua das Flores, 45",
                "Sala",
                "wifi, projetor",
                BigDecimal.valueOf(800)));
        localRepository.salvar(new Local(
                "Centro de Convenções Norte",
                800,
                "Rod. Norte, km 12",
                "Centro de eventos",
                "som, projetor, wifi, estacionamento",
                BigDecimal.valueOf(4500)));
        localRepository.salvar(new Local(
                "Espaço Garden",
                300,
                "Alameda Verde, 22",
                "Área externa",
                "wifi, som",
                BigDecimal.valueOf(1800)));

        log.info("Locais de demonstração semeados para planejamento de alocação.");
    }
}
