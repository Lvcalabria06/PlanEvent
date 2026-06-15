package dev.proj.planevent.web;

import dev.proj.planevent.web.dto.FinanceiroDtos;
import domain.fornecedor.repository.FornecedorRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("fornecedorFinanceiroController")
@RequestMapping("/api/fornecedores")
public class FornecedorController {

    private final FornecedorRepository fornecedorRepository;

    public FornecedorController(FornecedorRepository fornecedorRepository) {
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
