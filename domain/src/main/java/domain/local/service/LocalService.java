package domain.local.service;

import domain.local.entity.Local;
import java.math.BigDecimal;
import java.util.List;

public interface LocalService {
    Local cadastrarLocal(String nome, int capacidade, String endereco, String tipo, String infraestrutura, BigDecimal custo);
    Local editarLocal(String id, String nome, int capacidade, String endereco, String tipo, String infraestrutura, BigDecimal custo);
    void desativarLocal(String id);
    List<Local> listarLocais();
}
