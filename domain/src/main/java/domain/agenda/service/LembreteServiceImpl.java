package domain.agenda.service;

import domain.agenda.entity.Compromisso;
import domain.agenda.entity.Lembrete;
import domain.agenda.observer.EnviarAlertaLembreteObserver;
import domain.agenda.observer.LembreteNotificacaoSubject;
import domain.agenda.observer.MarcarLembreteNotificadoObserver;
import domain.agenda.port.AlertaLembretePort;
import domain.agenda.repository.CompromissoRepository;
import domain.agenda.repository.LembreteRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LembreteServiceImpl implements LembreteService {

    private final LembreteRepository lembreteRepository;
    private final CompromissoRepository compromissoRepository;
    private final LembreteNotificacaoSubject notificacaoSubject;

    public LembreteServiceImpl(LembreteRepository lembreteRepository,
                               CompromissoRepository compromissoRepository) {
        this(lembreteRepository, compromissoRepository, criarSubjectPadrao(lembreteRepository));
    }

    public LembreteServiceImpl(LembreteRepository lembreteRepository,
                               CompromissoRepository compromissoRepository,
                               LembreteNotificacaoSubject notificacaoSubject) {
        this.lembreteRepository = lembreteRepository;
        this.compromissoRepository = compromissoRepository;
        this.notificacaoSubject = notificacaoSubject;
    }

    private static LembreteNotificacaoSubject criarSubjectPadrao(LembreteRepository lembreteRepository) {
        LembreteNotificacaoSubject subject = new LembreteNotificacaoSubject();
        subject.registrar(new EnviarAlertaLembreteObserver(lembrete -> { /* noop em testes sem porta */ }));
        subject.registrar(new MarcarLembreteNotificadoObserver(lembreteRepository));
        return subject;
    }

    public static LembreteNotificacaoSubject criarSubject(LembreteRepository lembreteRepository,
                                                          AlertaLembretePort alertaPort) {
        LembreteNotificacaoSubject subject = new LembreteNotificacaoSubject();
        subject.registrar(new EnviarAlertaLembreteObserver(alertaPort));
        subject.registrar(new MarcarLembreteNotificadoObserver(lembreteRepository));
        return subject;
    }

    @Override
    public Lembrete criarLembrete(Lembrete lembrete) {
        if (lembrete.getCompromissoId() != null) {
            Compromisso compromisso = compromissoRepository.buscarPorId(lembrete.getCompromissoId())
                    .orElseThrow(() -> new IllegalArgumentException("Compromisso não encontrado."));

            if (compromisso.estaFinalizado()) {
                throw new IllegalStateException("Não é permitido criar lembretes para compromissos finalizados.");
            }
        }

        List<Lembrete> existentes = lembrete.getCompromissoId() != null
                ? lembreteRepository.listarPorCompromissoId(lembrete.getCompromissoId())
                : lembreteRepository.listarPorEventoId(lembrete.getEventoId());

        for (Lembrete existente : existentes) {
            if (lembrete.temMesmoHorario(existente)) {
                throw new IllegalArgumentException("Já existe um lembrete com esse horário para este vínculo.");
            }
        }

        return lembreteRepository.salvar(lembrete);
    }

    @Override
    public Lembrete editarLembrete(Lembrete lembreteEditado) {
        Lembrete atual = lembreteRepository.buscarPorId(lembreteEditado.getId())
                .orElseThrow(() -> new IllegalArgumentException("Lembrete não encontrado."));

        LocalDateTime inicioReferencia = null;
        if (atual.getCompromissoId() != null) {
            Compromisso compromisso = compromissoRepository.buscarPorId(atual.getCompromissoId())
                    .orElseThrow(() -> new IllegalArgumentException("Compromisso não encontrado."));

            if (compromisso.estaFinalizado()) {
                throw new IllegalStateException("Não é permitido editar lembretes de compromissos finalizados.");
            }
            inicioReferencia = compromisso.getDataInicio();
        }

        atual.editar(lembreteEditado.getHorario(), inicioReferencia);

        List<Lembrete> existentes = atual.getCompromissoId() != null
                ? lembreteRepository.listarPorCompromissoId(atual.getCompromissoId())
                : lembreteRepository.listarPorEventoId(atual.getEventoId());

        for (Lembrete existente : existentes) {
            if (atual.temMesmoHorario(existente)) {
                throw new IllegalArgumentException("Já existe um lembrete com esse horário para este vínculo.");
            }
        }

        return lembreteRepository.salvar(atual);
    }

    @Override
    public Lembrete buscarLembrete(String id) {
        return lembreteRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Lembrete não encontrado."));
    }

    @Override
    public List<Lembrete> listarLembretesPorCompromisso(String compromissoId) {
        compromissoRepository.buscarPorId(compromissoId)
                .orElseThrow(() -> new IllegalArgumentException("Compromisso não encontrado."));

        return lembreteRepository.listarPorCompromissoId(compromissoId);
    }

    @Override
    public List<Lembrete> listarLembretesPorEvento(String eventoId) {
        return lembreteRepository.listarPorEventoId(eventoId);
    }

    @Override
    public List<Lembrete> listarLembretesPorGestor(String gestorId) {
        List<Compromisso> compromissos = compromissoRepository.listarPorGestorId(gestorId);
        List<Lembrete> resultado = new ArrayList<>();
        Set<String> idsIncluidos = new HashSet<>();
        Set<String> eventoIds = new HashSet<>();

        for (Compromisso compromisso : compromissos) {
            eventoIds.add(compromisso.getEventoId());
            for (Lembrete lembrete : lembreteRepository.listarPorCompromissoId(compromisso.getId())) {
                if (idsIncluidos.add(lembrete.getId())) {
                    resultado.add(lembrete);
                }
            }
        }

        for (String eventoId : eventoIds) {
            for (Lembrete lembrete : lembreteRepository.listarPorEventoId(eventoId)) {
                if (lembrete.getCompromissoId() == null && idsIncluidos.add(lembrete.getId())) {
                    resultado.add(lembrete);
                }
            }
        }

        return resultado;
    }

    @Override
    public List<Lembrete> listarTodosLembretes() {
        return lembreteRepository.listarTodos();
    }

    @Override
    public void removerLembrete(String id) {
        lembreteRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Lembrete não encontrado."));

        lembreteRepository.remover(id);
    }

    @Override
    public Lembrete dispararNotificacao(String lembreteId) {
        Lembrete lembrete = lembreteRepository.buscarPorId(lembreteId)
                .orElseThrow(() -> new IllegalArgumentException("Lembrete não encontrado."));

        if (lembrete.isNotificado()) {
            throw new IllegalStateException("Lembrete já foi notificado.");
        }

        notificacaoSubject.notificar(lembrete);
        return lembrete;
    }

    @Override
    public List<Lembrete> processarLembretesVencidos() {
        LocalDateTime agora = LocalDateTime.now();
        List<Lembrete> vencidos = lembreteRepository.listarPendentesComHorarioAte(agora);
        List<Lembrete> processados = new ArrayList<>();

        for (Lembrete lembrete : vencidos) {
            if (lembrete.getCompromissoId() != null) {
                Compromisso compromisso = compromissoRepository.buscarPorId(lembrete.getCompromissoId())
                        .orElse(null);
                if (compromisso != null && compromisso.estaFinalizado()) {
                    continue;
                }
            }
            try {
                processados.add(dispararNotificacao(lembrete.getId()));
            } catch (IllegalStateException ignored) {
                // concorrência ou já notificado
            }
        }

        return processados;
    }
}
