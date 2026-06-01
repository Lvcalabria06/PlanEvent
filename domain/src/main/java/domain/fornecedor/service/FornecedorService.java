package domain.fornecedor.service;

import domain.fornecedor.entity.Fornecedor;

import java.util.List;

public interface FornecedorService {

    Fornecedor cadastrarFornecedor(String nome, String cnpj, String categoriaServico, String contato);

    Fornecedor editarFornecedor(String id, String nome, String cnpj, String categoriaServico, String contato);

    Fornecedor buscarFornecedor(String id);

    List<Fornecedor> listarFornecedores();

    void desativarFornecedor(String id);
}
