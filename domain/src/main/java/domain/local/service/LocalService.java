package domain.local.service;

import domain.local.entity.Local;
import java.math.BigDecimal;
import java.util.List;

public interface LocalService {
    Local cadastrarLocal(String nome, int capacidade, String endereco, String tipo, String infraestrutura, BigDecimal custo);
    Local editarLocal(String id, String nome, int capacidade, String endereco, String tipo, String infraestrutura, BigDecimal custo);
    void desativarLocal(String id);
    List<Local> listarLocais();

    /**
     * Filtra os locais cadastrados usando uma expressão textual interpretada
     * pelo padrão Interpreter (GoF).
     *
     * <p>Campos suportados: {@code status}, {@code tipo},
     * {@code capacidade_min}, {@code capacidade_max}.<br>
     * Operadores lógicos: {@code AND}, {@code OR}.<br>
     * Agrupamento com parênteses é suportado.</p>
     *
     * <p>Exemplos:</p>
     * <pre>
     *   status = ATIVO
     *   tipo = Salão AND capacidade_min = 100
     *   (status = ATIVO OR status = EM_MANUTENCAO) AND capacidade_max = 300
     * </pre>
     *
     * @param expressao expressão de filtro textual (não nula e não vazia)
     * @return lista de locais que satisfazem a expressão
     * @throws IllegalArgumentException se a expressão for inválida
     */
    List<Local> filtrarLocais(String expressao);
}
