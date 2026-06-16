package domain.local.service;

import domain.local.entity.Local;
import domain.local.entity.ManutencaoLocal;
import domain.local.repository.LocalRepository;
import domain.local.repository.ManutencaoRepository;
import domain.local.valueobject.StatusLocal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class LocalServiceImpl implements LocalService {

    private final LocalRepository localRepository;
    private final ManutencaoRepository manutencaoRepository;

    public LocalServiceImpl(LocalRepository localRepository, ManutencaoRepository manutencaoRepository) {
        this.localRepository = localRepository;
        this.manutencaoRepository = manutencaoRepository;
    }

    public LocalServiceImpl(LocalRepository localRepository) {
        this.localRepository = localRepository;
        this.manutencaoRepository = null;
    }

    private Local atualizarStatusDinamico(Local local) {
        if (local == null) return null;
        if (local.getStatus() == StatusLocal.INATIVO) {
            return local;
        }
        if (manutencaoRepository != null) {
            List<ManutencaoLocal> manutencoes = manutencaoRepository.buscarPorLocalId(local.getId());
            LocalDateTime now = LocalDateTime.now();
            boolean emManutencao = manutencoes.stream()
                    .anyMatch(m -> !now.isBefore(m.getDataInicio()) && !now.isAfter(m.getDataFim()));
            if (emManutencao) {
                if (local.getStatus() != StatusLocal.EM_MANUTENCAO) {
                    local.marcarEmManutencao();
                    localRepository.salvar(local);
                }
            } else {
                if (local.getStatus() == StatusLocal.EM_MANUTENCAO) {
                    local.marcarAtivo();
                    localRepository.salvar(local);
                }
            }
        }
        return local;
    }

    @Override
    public Local cadastrarLocal(String nome, int capacidade, String endereco, String tipo, String infraestrutura, BigDecimal custo) {
        Local local = new Local(nome, capacidade, endereco, tipo, infraestrutura, custo);
        return atualizarStatusDinamico(localRepository.salvar(local));
    }

    @Override
    public Local editarLocal(String id, String nome, int capacidade, String endereco, String tipo, String infraestrutura, BigDecimal custo) {
        Local local = localRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Local não encontrado."));
        
        local.atualizarDados(nome, capacidade, endereco, tipo, infraestrutura, custo);
        return atualizarStatusDinamico(localRepository.salvar(local));
    }

    @Override
    public void desativarLocal(String id) {
        Local local = localRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Local não encontrado."));
        
        local.desativar();
        localRepository.salvar(local);
    }

    @Override
    public List<Local> listarLocais() {
        return localRepository.listarTodos().stream()
                .map(this::atualizarStatusDinamico)
                .toList();
    }
}
