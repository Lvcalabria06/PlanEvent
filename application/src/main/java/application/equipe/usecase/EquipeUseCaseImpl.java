package application.equipe.usecase;

import application.equipe.dto.CriarEquipeRequest;
import application.equipe.dto.EditarEquipeRequest;
import application.equipe.dto.EquipeResponse;
import application.equipe.dto.MembroEquipeResponse;
import application.equipe.mapper.EquipeDtoMapper;
import domain.equipe.entity.Equipe;
import domain.equipe.entity.MembroEquipe;
import domain.equipe.service.EquipeService;

import java.util.List;

public class EquipeUseCaseImpl implements EquipeUseCase {

    private final EquipeService equipeService;

    public EquipeUseCaseImpl(EquipeService equipeService) {
        this.equipeService = equipeService;
    }

    @Override
    public EquipeResponse criar(CriarEquipeRequest request) {
        List<MembroEquipe> membros = request.membros().stream()
                .map(m -> new MembroEquipe(m.funcionarioId(), m.lider()))
                .toList();

        Equipe equipe = new Equipe(request.eventoId(), request.nome(), membros);
        return EquipeDtoMapper.paraResposta(equipeService.criarEquipe(equipe));
    }

    @Override
    public EquipeResponse editar(String equipeId, EditarEquipeRequest request) {
        Equipe equipeAtual = equipeService.buscarEquipe(equipeId);

        equipeAtual.alterarNome(request.nome());

        List<String> novasMembros = request.membros().stream()
                .map(m -> m.funcionarioId())
                .toList();

        List<String> membrosAtuais = equipeAtual.getMembros().stream()
                .map(MembroEquipe::getFuncionarioId)
                .toList();

        // Remover membros que não estão na nova lista
        for (String funcId : membrosAtuais) {
            if (!novasMembros.contains(funcId)) {
                String novoLiderId = request.membros().stream()
                        .filter(m -> m.lider())
                        .map(m -> m.funcionarioId())
                        .findFirst()
                        .orElse(null);
                equipeAtual.removerMembro(funcId, novoLiderId);
            }
        }

        // Adicionar membros novos
        for (var membroReq : request.membros()) {
            if (!equipeAtual.possuiMembro(membroReq.funcionarioId())) {
                equipeAtual.adicionarMembro(membroReq.funcionarioId());
            }
        }

        // Definir líder se especificado
        request.membros().stream()
                .filter(m -> m.lider())
                .findFirst()
                .ifPresent(m -> equipeAtual.definirLider(m.funcionarioId()));

        return EquipeDtoMapper.paraResposta(equipeService.editarEquipe(equipeAtual));
    }

    @Override
    public void remover(String equipeId) {
        equipeService.removerEquipe(equipeId);
    }

    @Override
    public EquipeResponse buscar(String equipeId) {
        return EquipeDtoMapper.paraResposta(equipeService.buscarEquipe(equipeId));
    }

    @Override
    public List<EquipeResponse> listarPorEvento(String eventoId) {
        return equipeService.listarEquipesPorEvento(eventoId).stream()
                .map(EquipeDtoMapper::paraResposta)
                .toList();
    }

    @Override
    public List<MembroEquipeResponse> filtrarMembros(String equipeId, String expressao) {
        return equipeService.filtrarMembros(equipeId, expressao).stream()
                .map(m -> new MembroEquipeResponse(
                        m.getId(),
                        m.getFuncionarioId(),
                        m.isLider(),
                        m.getDataEntrada()))
                .toList();
    }
}
