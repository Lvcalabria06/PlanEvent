package presentationbackend.controller;

import application.financeiro.dto.FinanceiroDtos;
import application.financeiro.mapper.FinanceiroMapper;
import domain.fornecedor.repository.FornecedorRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Expõe a listagem de fornecedores ativos para o módulo financeiro
 * (endpoint {@code /api/fornecedores}).
 * Renomeado para evitar conflito com {@link FornecedorController} ({@code /api/v1/fornecedores}).
 */
@RestController
@RequestMapping("/api/fornecedores")
public class FornecedorFinanceiroController {

    private final FornecedorRepository fornecedorRepository;

    public FornecedorFinanceiroController(FornecedorRepository fornecedorRepository) {
        this.fornecedorRepository = fornecedorRepository;
    }

    @GetMapping
    public List<FinanceiroDtos.FornecedorResumoDto> listarAtivos() {
        return fornecedorRepository.listarTodos().stream()
                .filter(f -> f.isAtivo())
                .map(FinanceiroMapper::toFornecedorDto)
                .toList();
    }
}
