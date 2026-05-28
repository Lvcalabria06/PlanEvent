package domain.agenda.observer;

import domain.agenda.entity.Compromisso;
import domain.agenda.entity.Lembrete;
import domain.agenda.repository.CompromissoRepository;
import domain.agenda.repository.LembreteRepository;
import domain.agenda.service.LembreteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Optional;

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
    private LembreteNotificacaoSubject subject;
    private LembreteServiceImpl lembreteService;

    @BeforeEach
    void setUp() {
        lembreteRepository = mock(LembreteRepository.class);
        compromissoRepository = mock(CompromissoRepository.class);
        subject = new LembreteNotificacaoSubject();
        subject.registrar(new EnviarAlertaLembreteObserver());
        subject.registrar(new MarcarLembreteNotificadoObserver(lembreteRepository));
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
        Compromisso compromisso = novoCompromisso();
        Lembrete lembrete = new Lembrete(compromisso.getId(), ID_EVENTO,
                compromisso.getDataInicio().minusMinutes(15), compromisso.getDataInicio());
        lembrete.marcarComoNotificado();

        new EnviarAlertaLembreteObserver().onLembreteDisparado(lembrete);

        assertTrue(lembrete.isNotificado());
        assertFalse(lembrete.getUpdatedAt().isBefore(lembrete.getCreatedAt()));
    }

    private static Compromisso novoCompromisso() {
        LocalDateTime inicio = LocalDateTime.now().plusDays(2);
        return new Compromisso(ID_GESTOR, ID_EVENTO, "Reuniao", "Descricao", inicio, inicio.plusHours(1));
    }
}
