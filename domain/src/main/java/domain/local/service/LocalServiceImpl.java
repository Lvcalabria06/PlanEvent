package domain.local.service;

import domain.local.entity.Local;
import domain.local.repository.LocalRepository;
import java.math.BigDecimal;
import java.util.List;

public class LocalServiceImpl implements LocalService {

    private final LocalRepository localRepository;

    public LocalServiceImpl(LocalRepository localRepository) {
        this.localRepository = localRepository;
    }

    @Override
    public Local cadastrarLocal(String nome, int capacidade, String endereco, String tipo, String infraestrutura, BigDecimal custo) {
        Local local = new Local(nome, capacidade, endereco, tipo, infraestrutura, custo);
        return localRepository.salvar(local);
    }

    @Override
    public Local editarLocal(String id, String nome, int capacidade, String endereco, String tipo, String infraestrutura, BigDecimal custo) {
        Local local = localRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Local não encontrado."));
        
        local.atualizarDados(nome, capacidade, endereco, tipo, infraestrutura, custo);
        return localRepository.salvar(local);
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
        return localRepository.listarTodos();
    }
}
