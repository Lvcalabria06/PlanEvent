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
        Tarefa tarefaAtual = tarefaRepository.buscarPorId(tarefaEditada.getId())
                .orElseThrow(() -> new IllegalArgumentException("Tarefa não encontrada."));

        // RN8: Não editável após conclusão (a própria entidade Tarefa já validará no
        // update)
        if (!tarefaAtual.getTitulo().equals(tarefaEditada.getTitulo())) {
            if (tarefaRepository.existePorTituloEEquipe(tarefaEditada.getTitulo(), tarefaAtual.getEquipeId())) {
                throw new IllegalStateException("Já existe uma tarefa com esse título na equipe.");
            }
        }

        // Aplicamos RN8 (lança exception se estiver concluída) e RN3 (datas válidas)
        tarefaAtual.atualizarDetalhes(
                tarefaEditada.getTitulo(),
                tarefaEditada.getDescricao(),
                tarefaEditada.getDataInicio(),
                tarefaEditada.getDataFim());

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

        // Inicia a tarefa aplicando as regras de RN6 (fluxo controlado interno da
        // entidade)
        tarefa.iniciar();

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
    public List<Tarefa> listarPorEquipe(String equipeId) {
        return tarefaRepository.listarPorEquipeId(equipeId);
    }

    /**
     * Lógica incorporada no serviço para verificar se o funcionário pertence à
     * equipe
     * (Implementado diretamente no service para compensar a ausência do
     * MembroEquipeRepository)
     */
    private boolean isFuncionarioNaEquipe(String equipeId, String funcionarioId) {
        Equipe equipe = equipeRepository.buscarPorId(equipeId)
                .orElseThrow(() -> new IllegalArgumentException("Equipe não encontrada."));

        // Simulação da regra de negócio incorporada:
        // Como o repositório de associação DB não existe, validamos usando os dados da
        // raiz (Ex: o Lider)
        // ou outra premissa de domínio, ao invés de buscar membros por junção SQL.
        return equipe.getLiderId() != null && equipe.getLiderId().equals(funcionarioId);
    }
}
