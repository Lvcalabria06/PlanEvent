package presentationbackend.scaffolding;

import domain.estoque.entity.ItemConsumoEvento;
import domain.estoque.entity.ItemReserva;
import domain.estoque.repository.ItemEstoqueRepository;
import domain.estoque.service.ConsumoEventoService;
import domain.estoque.service.ItemEstoqueService;
import domain.estoque.service.ReservaEstoqueService;
import domain.evento.entity.Evento;
import domain.evento.repository.EventoRepository;
import domain.evento.valueobject.PorteEvento;
import domain.evento.valueobject.TipoEvento;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Semeia itens de estoque, consumo histórico, substituições e reservas concorrentes
 * para demonstrar previsão e redistribuição na interface.
 */
@Component
@Order(101)
public class EstoqueDemoDataLoader implements CommandLineRunner {

    private static final String USUARIO_DEMO = "gestor@empresa.com";

    private final EventoRepository eventoRepository;
    private final ItemEstoqueRepository itemEstoqueRepository;
    private final ItemEstoqueService itemEstoqueService;
    private final ConsumoEventoService consumoEventoService;
    private final ReservaEstoqueService reservaEstoqueService;

    public EstoqueDemoDataLoader(EventoRepository eventoRepository,
            ItemEstoqueRepository itemEstoqueRepository,
            ItemEstoqueService itemEstoqueService,
            ConsumoEventoService consumoEventoService,
            ReservaEstoqueService reservaEstoqueService) {
        this.eventoRepository = eventoRepository;
        this.itemEstoqueRepository = itemEstoqueRepository;
        this.itemEstoqueService = itemEstoqueService;
        this.consumoEventoService = consumoEventoService;
        this.reservaEstoqueService = reservaEstoqueService;
    }

    @Override
    public void run(String... args) {
        if (!itemEstoqueRepository.listarTodos().isEmpty()) {
            return;
        }

        List<Evento> eventos = eventoRepository.listarTodos();
        if (eventos.isEmpty()) {
            return;
        }

        Evento eventoPrincipal = eventos.stream()
                .filter(e -> DemoEventoBasicoSeeder.NOME_EVENTO_DEMO.equals(e.getNome()))
                .findFirst()
                .orElse(eventos.get(0));

        String localId = eventoPrincipal.getLocalId();
        if (localId == null || localId.isBlank()) {
            return;
        }

        var cadeiras = itemEstoqueService.cadastrar("Cadeiras", 500);
        var mesas = itemEstoqueService.cadastrar("Mesas", 200);
        var agua = itemEstoqueService.cadastrar("Agua mineral", 1000);
        var bancos = itemEstoqueService.cadastrar("Bancos", 150);

        itemEstoqueService.registrarSubstituicao(cadeiras.getId(), bancos.getId(), 1.0);

        semearHistoricoPrevisao(localId, agua.getId(), cadeiras.getId());

        Evento eventoConcorrente = criarEventoAtivoConcorrente(localId);

        LocalDateTime inicio = LocalDateTime.now().plusDays(7).withHour(8).withMinute(0).withSecond(0);
        LocalDateTime fim = inicio.plusHours(10);

        var reservaPrincipal = reservaEstoqueService.criarReserva(
                eventoPrincipal.getId(),
                inicio,
                fim,
                List.of(
                        new ItemReserva("temp", cadeiras.getId(), 350),
                        new ItemReserva("temp", mesas.getId(), 80)));
        reservaEstoqueService.confirmar(reservaPrincipal.getId());

        var reservaConcorrente = reservaEstoqueService.criarReserva(
                eventoConcorrente.getId(),
                inicio.plusHours(2),
                fim.plusHours(4),
                List.of(
                        new ItemReserva("temp", cadeiras.getId(), 280),
                        new ItemReserva("temp", mesas.getId(), 60)));
        reservaEstoqueService.confirmar(reservaConcorrente.getId());
    }

    private void semearHistoricoPrevisao(String localId, String itemAguaId, String itemCadeiraId) {
        criarEventoHistorico(localId, "Summit Corporativo 2025", 300, itemAguaId, 350, itemCadeiraId, 280);
        criarEventoHistorico(localId, "Forum Executivo 2025", 400, itemAguaId, 420, itemCadeiraId, 350);
        criarEventoHistorico(localId, "Workshop Interno 2025", 320, itemAguaId, 380, itemCadeiraId, 300);
    }

    private void criarEventoHistorico(String localId, String nome, int participantes,
            String itemAguaId, int qtdAgua, String itemCadeiraId, int qtdCadeiras) {
        Evento eventoPassado = new Evento(
                nome,
                TipoEvento.CORPORATIVO,
                PorteEvento.GRANDE,
                participantes,
                "Evento historico para base de previsao de consumo.",
                localId);
        eventoPassado.concluirEvento();
        eventoRepository.salvar(eventoPassado);

        consumoEventoService.registrar(
                eventoPassado.getId(),
                USUARIO_DEMO,
                List.of(
                        new ItemConsumoEvento(itemAguaId, "bebida", qtdAgua),
                        new ItemConsumoEvento(itemCadeiraId, "mobiliario", qtdCadeiras)));
    }

    private Evento criarEventoAtivoConcorrente(String localId) {
        Evento evento = new Evento(
                "Feira de Startups 2026",
                TipoEvento.CORPORATIVO,
                PorteEvento.GRANDE,
                280,
                "Feira paralela com demanda concorrente de estoque.",
                localId);
        eventoRepository.salvar(evento);
        return evento;
    }
}
