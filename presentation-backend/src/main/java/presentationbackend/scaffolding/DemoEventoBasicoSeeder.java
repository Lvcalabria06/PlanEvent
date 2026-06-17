package presentationbackend.scaffolding;

import domain.evento.entity.Evento;
import domain.evento.repository.EventoRepository;
import domain.evento.valueobject.PorteEvento;
import domain.evento.valueobject.TipoEvento;
import domain.local.repository.LocalRepository;
import org.slf4j.Logger;

/**
 * Cria um evento de demonstração mínimo quando o seed completo de tarefas não roda
 * (repositório real de funcionário) ou quando a base ainda não possui eventos.
 */
public final class DemoEventoBasicoSeeder {

    public static final String NOME_EVENTO_DEMO = "Congresso Tech 2026";

    private DemoEventoBasicoSeeder() {
    }

    public static String semearSeVazio(EventoRepository eventoRepository,
            LocalRepository localRepository,
            Logger log) {
        if (!eventoRepository.listarTodos().isEmpty()) {
            return eventoRepository.listarTodos().get(0).getId();
        }

        String localId = localRepository.listarTodos().stream()
                .findFirst()
                .map(l -> l.getId())
                .orElse(null);

        Evento evento = new Evento(
                NOME_EVENTO_DEMO,
                TipoEvento.CORPORATIVO,
                PorteEvento.GRANDE,
                350,
                "Conferencia anual de tecnologia e inovacao.",
                localId);

        eventoRepository.salvar(evento);
        log.info("Evento demo criado: {} (id={})", evento.getNome(), evento.getId());
        return evento.getId();
    }

    public static Evento criarEventoDemo(LocalRepository localRepository) {
        String localId = localRepository.listarTodos().stream()
                .findFirst()
                .map(l -> l.getId())
                .orElse(null);

        return new Evento(
                NOME_EVENTO_DEMO,
                TipoEvento.CORPORATIVO,
                PorteEvento.GRANDE,
                350,
                "Conferencia anual de tecnologia e inovacao.",
                localId);
    }
}
