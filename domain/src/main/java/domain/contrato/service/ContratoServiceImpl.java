package domain.contrato.service;

import domain.contrato.entity.Contrato;
import domain.contrato.repository.ContratoRepository;
import domain.contrato.valueobject.DadosParteContrato;
import domain.evento.repository.EventoRepository;

import java.util.List;

public class ContratoServiceImpl implements ContratoService {

    private final ContratoRepository contratoRepository;
    private final EventoRepository eventoRepository;

    public ContratoServiceImpl(ContratoRepository contratoRepository, EventoRepository eventoRepository) {
        this.contratoRepository = contratoRepository;
        this.eventoRepository = eventoRepository;
    }

    @Override
    public Contrato criarContrato(Contrato contrato) {
        eventoRepository.buscarPorId(contrato.getEventoId())
                .orElseThrow(() -> new IllegalArgumentException("Evento inválido ou não encontrado."));
        return contratoRepository.salvar(contrato);
    }

    @Override
    public Contrato editarContrato(Contrato contratoEditado) {
        Contrato atual = contratoRepository.buscarPorId(contratoEditado.getId())
                .orElseThrow(() -> new IllegalArgumentException("Contrato não encontrado."));

        List<DadosParteContrato> dadosPartes = contratoEditado.getPartes().stream()
                .map(p -> new DadosParteContrato(p.getNomeParte(), p.getTipoParte()))
                .toList();

        atual.atualizarDetalhes(
                contratoEditado.getTipo(),
                contratoEditado.getObjeto(),
                contratoEditado.getValor(),
                contratoEditado.getDataInicio(),
                contratoEditado.getDataFim(),
                dadosPartes);

        return contratoRepository.salvar(atual);
    }

    @Override
    public Contrato buscarContrato(String id) {
        return contratoRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Contrato não encontrado."));
    }

    @Override
    public List<Contrato> listarContratosPorEvento(String eventoId) {
        return contratoRepository.listarPorEventoId(eventoId);
    }

    @Override
    public void encerrarContrato(String id) {
        Contrato contrato = contratoRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Contrato não encontrado."));
        contrato.encerrar();
        contratoRepository.salvar(contrato);
    }
}
