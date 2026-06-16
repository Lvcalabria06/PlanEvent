package domain.agenda.observer;

import domain.agenda.entity.Compromisso;
import domain.agenda.entity.Lembrete;
import domain.agenda.port.AlertaLembretePort;
import domain.agenda.repository.CompromissoRepository;
import domain.agenda.repository.LembreteRepository;
import domain.agenda.service.LembreteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LembreteNotificacaoObserverTest {

    private static final String ID_GESTOR = "gestor-1";
    private static final String ID_EVENTO = "evento-1";

    private LembreteRepository lembreteRepository;
    private CompromissoRepository compromissoRepository;
    private AlertaLembretePort alertaPort;
    private LembreteNotificacaoSubject subject;
    private LembreteServiceImpl lembreteService;

    @BeforeEach
    void setUp() {
        lembreteRepository = mock(LembreteRepository.class);
        compromissoRepository = mock(CompromissoRepository.class);
        alertaPort = mock(AlertaLembretePort.class);
        subject = LembreteServiceImpl.criarSubject(lembreteRepository, alertaPort);
        lembreteService = new LembreteServiceImpl(lembreteRepository, compromissoRepository, subject);

        when(lembreteRepository.salvar(any(Lembrete.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    @DisplayName("dispararNotificacao marca lembrete como notificado via Observer")
    void dispararNotificacaoMarcaLembreteComoNotificado() {
        Compromisso compromisso = novoCompromisso();
        LocalDateTime horario = compromisso.getDataInicio().minusMinutes(30);
        Lembrete lembrete = new Lembrete(compromisso.getId(), ID_EVENTO, horario, compromisso.getDataInicio());

        when(lembreteRepository.buscarPorId(lembrete.getId())).thenReturn(Optional.of(lembrete));

        Lembrete resultado = lembreteService.dispararNotificacao(lembrete.getId());

        assertTrue(resultado.isNotificado());
        verify(alertaPort).enviar(lembrete);
        verify(lembreteRepository).salvar(lembrete);
    }

    @Test
    @DisplayName("dispararNotificacao impede reenvio para lembrete ja notificado")
    void dispararNotificacaoImpedeReenvio() {
        Compromisso compromisso = novoCompromisso();
        LocalDateTime horario = compromisso.getDataInicio().minusMinutes(30);
        Lembrete lembrete = new Lembrete(compromisso.getId(), ID_EVENTO, horario, compromisso.getDataInicio());
        lembrete.marcarComoNotificado();

        when(lembreteRepository.buscarPorId(lembrete.getId())).thenReturn(Optional.of(lembrete));

        assertThrows(IllegalStateException.class,
                () -> lembreteService.dispararNotificacao(lembrete.getId()));
        assertTrue(lembrete.isNotificado());
        Mockito.verify(lembreteRepository, Mockito.never()).salvar(any());
    }

    @Test
    @DisplayName("Subject notifica todos os observadores registrados")
    void subjectNotificaTodosObservadores() {
        LembreteObserver extra = mock(LembreteObserver.class);
        subject.registrar(extra);

        Compromisso compromisso = novoCompromisso();
        Lembrete lembrete = new Lembrete(compromisso.getId(), ID_EVENTO,
                compromisso.getDataInicio().minusMinutes(15), compromisso.getDataInicio());

        subject.notificar(lembrete);

        assertTrue(lembrete.isNotificado());
        verify(extra).onLembreteDisparado(lembrete);
        verify(lembreteRepository).salvar(lembrete);
    }

    @Test
    @DisplayName("EnviarAlertaLembreteObserver ignora lembrete ja notificado")
    void enviarAlertaIgnoraLembreteJaNotificado() {
        AlertaLembretePort port = mock(AlertaLembretePort.class);
        Compromisso compromisso = novoCompromisso();
        Lembrete lembrete = new Lembrete(compromisso.getId(), ID_EVENTO,
                compromisso.getDataInicio().minusMinutes(15), compromisso.getDataInicio());
        lembrete.marcarComoNotificado();

        new EnviarAlertaLembreteObserver(port).onLembreteDisparado(lembrete);

        assertTrue(lembrete.isNotificado());
        assertFalse(lembrete.getUpdatedAt().isBefore(lembrete.getCreatedAt()));
        Mockito.verify(port, Mockito.never()).enviar(any());
    }

    @Test
    @DisplayName("processarLembretesVencidos dispara alerta e marca como notificado")
    void processarLembretesVencidos() {
        Compromisso compromisso = novoCompromisso();
        LocalDateTime horario = LocalDateTime.now().minusMinutes(5);
        Lembrete lembrete = Lembrete.reconstituir(
                "lem-vencido",
                compromisso.getId(),
                ID_EVENTO,
                horario,
                false,
                horario.minusHours(1),
                horario.minusHours(1));

        when(lembreteRepository.listarPendentesComHorarioAte(any())).thenReturn(List.of(lembrete));
        when(lembreteRepository.buscarPorId(lembrete.getId())).thenReturn(Optional.of(lembrete));
        when(compromissoRepository.buscarPorId(compromisso.getId())).thenReturn(Optional.of(compromisso));

        List<Lembrete> processados = lembreteService.processarLembretesVencidos();

        assertEquals(1, processados.size());
        assertTrue(processados.get(0).isNotificado());
        verify(alertaPort).enviar(lembrete);
    }

    private static Compromisso novoCompromisso() {
        LocalDateTime inicio = LocalDateTime.now().plusDays(2);
        return new Compromisso(ID_GESTOR, ID_EVENTO, "Reuniao", "Descricao", inicio, inicio.plusHours(1));
    }
}
