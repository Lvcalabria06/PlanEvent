package domain.agenda.service;

import domain.agenda.entity.Compromisso;
import domain.agenda.repository.CompromissoRepository;
import domain.agenda.repository.LembreteRepository;

import java.util.List;

public class CompromissoServiceImpl implements CompromissoService {

    private final CompromissoRepository compromissoRepository;
    private final LembreteRepository lembreteRepository;

    public CompromissoServiceImpl(CompromissoRepository compromissoRepository,
                                  LembreteRepository lembreteRepository) {
        this.compromissoRepository = compromissoRepository;
        this.lembreteRepository = lembreteRepository;
    }

    @Override
    public Compromisso criarCompromisso(Compromisso compromisso) {
        List<Compromisso> existentes = compromissoRepository.listarPorGestorId(compromisso.getGestorId());
        for (Compromisso existente : existentes) {
            if (!existente.estaFinalizado() && compromisso.temSobreposicao(existente)) {
                throw new IllegalArgumentException("Já existe um compromisso nesse horário para o gestor.");
            }
        }
        return compromissoRepository.salvar(compromisso);
    }

    @Override
    public Compromisso editarCompromisso(Compromisso compromissoEditado) {
        Compromisso atual = compromissoRepository.buscarPorId(compromissoEditado.getId())
                .orElseThrow(() -> new IllegalArgumentException("Compromisso não encontrado."));

        atual.editar(
                compromissoEditado.getTitulo(),
                compromissoEditado.getDescricao(),
                compromissoEditado.getDataInicio(),
                compromissoEditado.getDataFim());

        List<Compromisso> existentes = compromissoRepository.listarPorGestorId(atual.getGestorId());
        for (Compromisso existente : existentes) {
            if (!existente.estaFinalizado() && atual.temSobreposicao(existente)) {
                throw new IllegalArgumentException("A edição geraria sobreposição com outro compromisso.");
            }
        }

        return compromissoRepository.salvar(atual);
    }

    @Override
    public Compromisso buscarCompromisso(String id) {
        return compromissoRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Compromisso não encontrado."));
    }

    @Override
    public List<Compromisso> listarCompromissosPorGestor(String gestorId) {
        return compromissoRepository.listarPorGestorId(gestorId);
    }

    @Override
    public void removerCompromisso(String id) {
        Compromisso compromisso = compromissoRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Compromisso não encontrado."));

        compromisso.validarExclusao();

        lembreteRepository.removerPorCompromissoId(id);

        compromissoRepository.remover(id);
    }
}
