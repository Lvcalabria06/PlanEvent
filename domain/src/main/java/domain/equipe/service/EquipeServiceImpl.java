package domain.equipe.service;

import domain.equipe.entity.Equipe;
import domain.equipe.entity.MembroEquipe;
import domain.equipe.repository.EquipeRepository;
import domain.evento.repository.EventoRepository;
import domain.funcionario.repository.FuncionarioRepository;
import domain.tarefa.repository.TarefaRepository;

import java.util.List;

public class EquipeServiceImpl implements EquipeService {

    private final EquipeRepository equipeRepository;
    private final EventoRepository eventoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final TarefaRepository tarefaRepository;

    public EquipeServiceImpl(
            EquipeRepository equipeRepository,
            EventoRepository eventoRepository,
            FuncionarioRepository funcionarioRepository,
            TarefaRepository tarefaRepository) {
        this.equipeRepository = equipeRepository;
        this.eventoRepository = eventoRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.tarefaRepository = tarefaRepository;
    }

    @Override
    public Equipe criarEquipe(Equipe equipe) {
        validarEvento(equipe.getEventoId());
        validarNomeUnico(equipe);
        validarMembros(equipe);
        validarQuantidadeDeLideres(equipe);
        validarFuncionarios(equipe);
        validarFuncionariosNaoAlocados(equipe);

        return equipeRepository.salvar(equipe);
    }

    @Override
    public Equipe editarEquipe(Equipe equipeEditada) {
        Equipe equipeAtual = buscarEquipe(equipeEditada.getId());

        if (!equipeAtual.getNome().equals(equipeEditada.getNome())) {
            validarNomeUnico(equipeEditada);
        }

        validarMembros(equipeEditada);
        validarQuantidadeDeLideres(equipeEditada);
        validarFuncionarios(equipeEditada);

        return equipeRepository.salvar(equipeEditada);
    }

    @Override
    public Equipe buscarEquipe(String id) {
        return equipeRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Equipe não encontrada."));
    }

    @Override
    public List<Equipe> listarEquipesPorEvento(String eventoId) {
        validarEvento(eventoId);
        return equipeRepository.listarPorEventoId(eventoId);
    }

    @Override
    public void removerEquipe(String id) {
        Equipe equipe = buscarEquipe(id);

        if (!tarefaRepository.listarPorEquipeId(equipe.getId()).isEmpty()) {
            throw new IllegalStateException("Não é permitido excluir equipe com tarefas ou atividades em andamento.");
        }

        equipeRepository.remover(id);
    }

    private void validarEvento(String eventoId) {
        eventoRepository.buscarPorId(eventoId)
                .orElseThrow(() -> new IllegalArgumentException("Evento inválido ou não encontrado."));
    }

    private void validarNomeUnico(Equipe equipe) {
        if (equipeRepository.existeEquipeComNomeNoEvento(equipe.getEventoId(), equipe.getNome())) {
            throw new IllegalStateException("Já existe uma equipe com esse nome no evento.");
        }
    }

    private void validarMembros(Equipe equipe) {
        if (equipe.getMembros() == null || equipe.getMembros().isEmpty()) {
            throw new IllegalStateException("Equipe deve possuir pelo menos um funcionário.");
        }
    }

    private void validarQuantidadeDeLideres(Equipe equipe) {
        long qtd = equipe.getMembros().stream()
                .filter(MembroEquipe::isLider)
                .count();

        if (qtd > 1) {
            throw new IllegalStateException("Equipe deve possuir no máximo um líder.");
        }
    }

    private void validarFuncionarios(Equipe equipe) {
        for (MembroEquipe membro : equipe.getMembros()) {
            funcionarioRepository.buscarPorId(membro.getFuncionarioId())
                    .orElseThrow(() -> new IllegalArgumentException("Funcionário inválido ou não encontrado."));
        }
    }

    private void validarFuncionariosNaoAlocados(Equipe equipe) {
        for (MembroEquipe membro : equipe.getMembros()) {
            if (equipeRepository.funcionarioJaEstaEmEquipeNoEvento(
                    membro.getFuncionarioId(),
                    equipe.getEventoId())) {
                throw new IllegalStateException("Funcionário já está alocado em outra equipe deste evento.");
            }
        }
    }
}