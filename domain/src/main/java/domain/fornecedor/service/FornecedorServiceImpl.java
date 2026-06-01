package domain.fornecedor.service;

import domain.contrato.repository.ContratoRepository;
import domain.evento.entity.Evento;
import domain.evento.repository.EventoRepository;
import domain.financeiro.entity.Despesa;
import domain.financeiro.repository.DespesaRepository;
import domain.fornecedor.entity.Fornecedor;
import domain.fornecedor.repository.FornecedorRepository;
import domain.fornecedor.util.CnpjValidator;

import java.util.List;

public class FornecedorServiceImpl implements FornecedorService {

    private final FornecedorRepository fornecedorRepository;
    private final ContratoRepository contratoRepository;
    private final DespesaRepository despesaRepository;
    private final EventoRepository eventoRepository;

    public FornecedorServiceImpl(FornecedorRepository fornecedorRepository,
                                 ContratoRepository contratoRepository,
                                 DespesaRepository despesaRepository,
                                 EventoRepository eventoRepository) {
        this.fornecedorRepository = fornecedorRepository;
        this.contratoRepository = contratoRepository;
        this.despesaRepository = despesaRepository;
        this.eventoRepository = eventoRepository;
    }

    @Override
    public Fornecedor cadastrarFornecedor(String nome, String cnpj, String categoriaServico, String contato) {
        validarCnpjUnico(cnpj, null);
        Fornecedor fornecedor = new Fornecedor(nome, cnpj, categoriaServico, contato);
        return fornecedorRepository.salvar(fornecedor);
    }

    @Override
    public Fornecedor editarFornecedor(String id, String nome, String cnpj, String categoriaServico, String contato) {
        Fornecedor fornecedor = fornecedorRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Fornecedor não encontrado."));
        validarCnpjUnico(cnpj, id);
        fornecedor.atualizarDados(nome, cnpj, categoriaServico, contato);
        return fornecedorRepository.salvar(fornecedor);
    }

    @Override
    public Fornecedor buscarFornecedor(String id) {
        return fornecedorRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Fornecedor não encontrado."));
    }

    @Override
    public List<Fornecedor> listarFornecedores() {
        return fornecedorRepository.listarTodos();
    }

    @Override
    public void desativarFornecedor(String id) {
        Fornecedor fornecedor = fornecedorRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Fornecedor não encontrado."));

        if (contratoRepository.possuiContratoAtivoPorFornecedorId(id)) {
            throw new IllegalStateException(
                    "Não é possível desativar fornecedor com contratos ativos vinculados.");
        }

        for (Despesa despesa : despesaRepository.listarPorFornecedorId(id)) {
            Evento evento = eventoRepository.buscarPorId(despesa.getEventoId())
                    .orElseThrow(() -> new IllegalStateException(
                            "Evento vinculado à despesa não encontrado."));
            if (!evento.isConcluido()) {
                throw new IllegalStateException(
                        "Não é possível desativar fornecedor com despesas em eventos em andamento.");
            }
        }

        fornecedor.desativar();
        fornecedorRepository.salvar(fornecedor);
    }

    private void validarCnpjUnico(String cnpj, String idIgnorado) {
        String cnpjNormalizado = CnpjValidator.normalizar(cnpj);
        fornecedorRepository.buscarPorCnpj(cnpjNormalizado).ifPresent(existente -> {
            if (idIgnorado == null || !existente.getId().equals(idIgnorado)) {
                throw new IllegalArgumentException("CNPJ já cadastrado no sistema.");
            }
        });
    }
}
