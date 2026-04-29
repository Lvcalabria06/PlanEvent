package domain.agenda.service;

import domain.agenda.entity.Compromisso;
import domain.agenda.entity.Lembrete;
import domain.agenda.repository.CompromissoRepository;
import domain.agenda.repository.LembreteRepository;

import java.time.LocalDateTime;
import java.util.List;

public class LembreteServiceImpl implements LembreteService {

    private final LembreteRepository lembreteRepository;
    private final CompromissoRepository compromissoRepository;

    public LembreteServiceImpl(LembreteRepository lembreteRepository,
                               CompromissoRepository compromissoRepository) {
        this.lembreteRepository = lembreteRepository;
        this.compromissoRepository = compromissoRepository;
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
    public void removerLembrete(String id) {
        lembreteRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Lembrete não encontrado."));

        lembreteRepository.remover(id);
    }
}
