package domain.tarefa.service;

import domain.equipe.entity.Equipe;
import domain.equipe.repository.EquipeRepository;
import domain.evento.repository.EventoRepository;
import domain.funcionario.repository.FuncionarioRepository;
import domain.tarefa.entity.ResponsavelTarefa;
import domain.tarefa.entity.Tarefa;
import domain.tarefa.repository.ResponsavelTarefaRepository;
import domain.tarefa.repository.TarefaRepository;
import domain.tarefa.valueobject.StatusTarefa;
import java.time.LocalDateTime;
import java.util.List;

public class TarefaServiceImpl implements TarefaService {

    private final TarefaRepository tarefaRepository;
    private final EquipeRepository equipeRepository;
    private final EventoRepository eventoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final ResponsavelTarefaRepository responsavelTarefaRepository;

    public TarefaServiceImpl(TarefaRepository tarefaRepository,
            EquipeRepository equipeRepository,
            EventoRepository eventoRepository,
            FuncionarioRepository funcionarioRepository,
            ResponsavelTarefaRepository responsavelTarefaRepository) {
        this.tarefaRepository = tarefaRepository;
        this.equipeRepository = equipeRepository;
        this.eventoRepository = eventoRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.responsavelTarefaRepository = responsavelTarefaRepository;
    }

    @Override
    public Tarefa criarTarefa(Tarefa tarefa) {
        // RN1: A equipe da tarefa deve ser válida e pertencer a um evento válido
        Equipe equipe = equipeRepository.buscarPorId(tarefa.getEquipeId())
                .orElseThrow(() -> new IllegalArgumentException("Equipe inválida ou não encontrada."));

        eventoRepository.buscarPorId(equipe.getEventoId())
                .orElseThrow(() -> new IllegalArgumentException("A equipe fornecida não pertence a um evento válido."));

        // RN2: O título deve ser obrigatório e único dentro da mesma equipe
        if (tarefaRepository.existePorTituloEEquipe(tarefa.getTitulo(), tarefa.getEquipeId())) {
            throw new IllegalStateException("Já existe uma tarefa com esse título na equipe.");
        }

        // RN3: Data inicio e fim não sobrepostas manipulada pelos construtores da
        // entidade

        return tarefaRepository.salvar(tarefa);
    }

    @Override
    public Tarefa editarTarefa(Tarefa tarefaEditada) {
        return editarTarefa(
                tarefaEditada.getId(),
                tarefaEditada.getTitulo(),
                tarefaEditada.getDescricao(),
                tarefaEditada.getDataInicio(),
                tarefaEditada.getDataFim());
    }

    @Override
    public Tarefa editarTarefa(String tarefaId, String titulo, String descricao,
            LocalDateTime dataInicio, LocalDateTime dataFim) {
        Tarefa tarefaAtual = tarefaRepository.buscarPorId(tarefaId)
                .orElseThrow(() -> new IllegalArgumentException("Tarefa não encontrada."));

        // RN2/CA4: título único dentro da equipe (apenas quando o título muda)
        if (!tarefaAtual.getTitulo().equals(titulo)) {
            if (tarefaRepository.existePorTituloEEquipe(titulo, tarefaAtual.getEquipeId())) {
                throw new IllegalStateException("Já existe uma tarefa com esse título na equipe.");
            }
        }

        // RN8 (lança exceção se estiver concluída) e RN3 (datas válidas) validadas na entidade
        tarefaAtual.atualizarDetalhes(titulo, descricao, dataInicio, dataFim);

        // Impacto nas dependentes: se as novas datas comprometem dependentes, sinalizamos erro.
        if (tarefaAtual.getDataFim() != null) {
            List<Tarefa> dependentes = tarefaRepository.listarDependentes(tarefaAtual.getId());
            for (Tarefa dependente : dependentes) {
                if (dependente.getDataInicio() != null && dependente.getDataInicio().isBefore(tarefaAtual.getDataFim())) {
                    throw new IllegalStateException("Atraso de dependência! A alteração compromete tarefas dependentes (potencialmente atrasada).");
                }
            }
        }

        return tarefaRepository.salvar(tarefaAtual);
    }

    @Override
    public void removerTarefa(String tarefaId) {
        Tarefa tarefa = tarefaRepository.buscarPorId(tarefaId)
                .orElseThrow(() -> new IllegalArgumentException("Tarefa não encontrada."));

        // RN7: Não é permitido excluir tarefa em andamento ou concluída
        if (tarefa.getStatus() == StatusTarefa.EM_ANDAMENTO || tarefa.getStatus() == StatusTarefa.CONCLUIDA) {
            throw new IllegalStateException("Não é permitido excluir uma tarefa em andamento ou concluída.");
        }

        // RN de Dependência: Uma tarefa não pode ser removida se houver outras funções dependendo dela.
        if (!tarefaRepository.listarDependentes(tarefaId).isEmpty()) {
            throw new IllegalStateException("Não é possível remover a tarefa pois existem outras tarefas que dependem dela.");
        }

        tarefaRepository.remover(tarefaId);
    }

    @Override
    public void iniciarTarefa(String tarefaId) {
        Tarefa tarefa = tarefaRepository.buscarPorId(tarefaId)
                .orElseThrow(() -> new IllegalArgumentException("Tarefa não encontrada."));

        // RN5: Só pode ser iniciada se houver pelo menos um responsável atribuído
        List<ResponsavelTarefa> responsaveis = responsavelTarefaRepository.listarPorTarefa(tarefaId);
        if (responsaveis == null || responsaveis.isEmpty()) {
            throw new IllegalStateException(
                    "A tarefa só pode ser iniciada se houver pelo menos um responsável atribuído.");
        }

        // RN de Dependências: Uma tarefa só pode ser iniciada se todas as suas dependências estiverem concluídas.
        List<String> dependenciasIds = tarefa.listarDependencias();
        if (!dependenciasIds.isEmpty()) {
            List<Tarefa> dependencias = tarefaRepository.listarPorIds(dependenciasIds);
            for (Tarefa d : dependencias) {
                if (d.getStatus() != StatusTarefa.CONCLUIDA) {
                    throw new IllegalStateException("A tarefa não pode ser iniciada até que todas as dependências estejam concluídas.");
                }
            }
        }

        // Inicia a tarefa aplicando as regras de RN6 (fluxo controlado interno da
        // entidade)
        tarefa.iniciar();

        tarefaRepository.salvar(tarefa);
    }

    @Override
    public void concluirTarefa(String tarefaId) {
        Tarefa tarefa = tarefaRepository.buscarPorId(tarefaId)
                .orElseThrow(() -> new IllegalArgumentException("Tarefa não encontrada."));

        // RN6: fluxo controlado (EM_ANDAMENTO -> CONCLUIDA) validado na entidade
        tarefa.concluir();

        tarefaRepository.salvar(tarefa);
    }

    @Override
    public void atribuirResponsavel(String tarefaId, String funcionarioId) {
        Tarefa tarefa = tarefaRepository.buscarPorId(tarefaId)
                .orElseThrow(() -> new IllegalArgumentException("Tarefa não encontrada."));

        funcionarioRepository.buscarPorId(funcionarioId)
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado."));

        // RN4: Apenas funcionários da equipe da tarefa podem ser atribuídos como
        // responsáveis
        if (!isFuncionarioNaEquipe(tarefa.getEquipeId(), funcionarioId)) {
            throw new IllegalStateException(
                    "Apenas funcionários pertencentes à equipe da tarefa podem ser atribuídos.");
        }

        if (responsavelTarefaRepository.existePorTarefaEFuncionario(tarefaId, funcionarioId)) {
            throw new IllegalStateException("Este funcionário já é responsável por esta tarefa.");
        }

        ResponsavelTarefa responsavel = new ResponsavelTarefa(tarefaId, funcionarioId);
        responsavelTarefaRepository.salvar(responsavel);
    }

    @Override
    public List<String> listarResponsaveis(String tarefaId) {
        return responsavelTarefaRepository.listarPorTarefa(tarefaId).stream()
                .map(ResponsavelTarefa::getFuncionarioId)
                .toList();
    }

    @Override
    public List<Tarefa> listarPorEquipe(String equipeId) {
        return tarefaRepository.listarPorEquipeId(equipeId);
    }

    @Override
    public List<Tarefa> listarPorEvento(String eventoId) {
        // CA17: tarefas do evento, resolvidas pelas equipes que pertencem a ele.
        // Mantém o repositório de Tarefa restrito ao seu próprio agregado (DDD),
        // delegando a relação evento->equipe ao EquipeRepository.
        return equipeRepository.listarPorEventoId(eventoId).stream()
                .map(Equipe::getId)
                .flatMap(equipeId -> tarefaRepository.listarPorEquipeId(equipeId).stream())
                .toList();
    }

    private boolean isFuncionarioNaEquipe(String equipeId, String funcionarioId) {
        Equipe equipe = equipeRepository.buscarPorId(equipeId)
                .orElseThrow(() -> new IllegalArgumentException("Equipe não encontrada."));

        return equipe.possuiMembro(funcionarioId);
    }
}
