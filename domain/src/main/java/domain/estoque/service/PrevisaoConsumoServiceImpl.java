package domain.estoque.service;

import domain.estoque.entity.ConsumoEvento;
import domain.estoque.entity.ItemConsumoEvento;
import domain.estoque.entity.ItemPrevisao;
import domain.estoque.entity.PrevisaoConsumo;
import domain.estoque.repository.ConsumoEventoRepository;
import domain.estoque.repository.PrevisaoConsumoRepository;
import domain.estoque.valueobject.StatusHistoricoPrevisao;
import domain.evento.entity.Evento;
import domain.evento.repository.EventoRepository;
import domain.evento.valueobject.PorteEvento;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PrevisaoConsumoServiceImpl implements PrevisaoConsumoService {

    private final EventoRepository eventoRepository;
    private final ConsumoEventoRepository consumoEventoRepository;
    private final PrevisaoConsumoRepository previsaoConsumoRepository;

    public PrevisaoConsumoServiceImpl(EventoRepository eventoRepository,
                                      ConsumoEventoRepository consumoEventoRepository,
                                      PrevisaoConsumoRepository previsaoConsumoRepository) {
        this.eventoRepository = eventoRepository;
        this.consumoEventoRepository = consumoEventoRepository;
        this.previsaoConsumoRepository = previsaoConsumoRepository;
    }

    @Override
    public PrevisaoConsumo gerarPrevisao(String eventoId, String usuarioId) {
        Evento evento = buscarEventoExistente(eventoId);
        BaseCalculo baseCalculo = montarBaseCalculo(evento);

        PrevisaoConsumo previsao = new PrevisaoConsumo(
                evento,
                usuarioId,
                baseCalculo.statusHistorico(),
                baseCalculo.totalEventosBase(),
                criarItensPrevisao(baseCalculo.quantidadesEstimadas(), null));

        return previsaoConsumoRepository.salvar(previsao);
    }

    @Override
    public PrevisaoConsumo ajustarPrevisao(String previsaoId, Map<String, Integer> quantidadesAjustadas, String usuarioId) {
        PrevisaoConsumo previsao = buscarPrevisaoExistente(previsaoId);
        previsao.ajustarQuantidades(quantidadesAjustadas, usuarioId);
        return previsaoConsumoRepository.salvar(previsao);
    }

    @Override
    public PrevisaoConsumo recalcularPrevisao(String previsaoId, String usuarioId) {
        PrevisaoConsumo previsao = buscarPrevisaoExistente(previsaoId);
        Evento evento = buscarEventoExistente(previsao.getEventoId());
        BaseCalculo baseCalculo = montarBaseCalculo(evento);

        previsao.recalcular(
                evento,
                usuarioId,
                baseCalculo.statusHistorico(),
                baseCalculo.totalEventosBase(),
                criarItensPrevisao(baseCalculo.quantidadesEstimadas(), previsao.getId()));

        return previsaoConsumoRepository.salvar(previsao);
    }

    @Override
    public PrevisaoConsumo buscarPorEvento(String eventoId) {
        return previsaoConsumoRepository.buscarPorEventoId(eventoId)
                .orElseThrow(() -> new IllegalArgumentException("Previsao nao encontrada para o evento informado."));
    }

    private BaseCalculo montarBaseCalculo(Evento eventoAtual) {
        List<ConsumoEvento> consumosCompativeis = consumoEventoRepository.listarTodos().stream()
                .filter(ConsumoEvento::isValido)
                .filter(consumo -> consumo.getItensConsumidos() != null && !consumo.getItensConsumidos().isEmpty())
                .filter(consumo -> eventoRepository.buscarPorId(consumo.getEventoId())
                        .filter(Evento::isConcluido)
                        .filter(eventoHistorico -> eventoHistorico.getTipo() == eventoAtual.getTipo())
                        .isPresent())
                .collect(Collectors.toList());

        if (consumosCompativeis.isEmpty()) {
            return new BaseCalculo(StatusHistoricoPrevisao.INEXISTENTE, 0, new LinkedHashMap<>());
        }

        StatusHistoricoPrevisao statusHistorico = consumosCompativeis.size() >= 2
                ? StatusHistoricoPrevisao.SUFICIENTE
                : StatusHistoricoPrevisao.INSUFICIENTE;

        Map<String, Double> somaPonderada = new LinkedHashMap<>();
        Map<String, Double> somaPesos = new LinkedHashMap<>();

        for (ConsumoEvento consumo : consumosCompativeis) {
            Evento eventoHistorico = eventoRepository.buscarPorId(consumo.getEventoId()).orElseThrow();
            double pesoPorte = calcularPesoPorte(eventoAtual.getPorte(), eventoHistorico.getPorte());
            double fatorParticipantes = (double) eventoAtual.getQuantidadeEstimadaParticipantes()
                    / eventoHistorico.getQuantidadeEstimadaParticipantes();

            for (ItemConsumoEvento item : consumo.getItensConsumidos()) {
                double quantidadeProjetada = item.getQuantidadeConsumida() * fatorParticipantes;
                somaPonderada.merge(item.getItemEstoqueId(), quantidadeProjetada * pesoPorte, Double::sum);
                somaPesos.merge(item.getItemEstoqueId(), pesoPorte, Double::sum);
            }
        }

        Map<String, Integer> quantidadesEstimadas = somaPonderada.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.naturalOrder()))
                .collect(LinkedHashMap::new, (mapa, entry) -> {
                    double peso = somaPesos.getOrDefault(entry.getKey(), 1.0);
                    int quantidade = (int) Math.round(entry.getValue() / peso);
                    mapa.put(entry.getKey(), Math.max(0, quantidade));
                }, LinkedHashMap::putAll);

        return new BaseCalculo(statusHistorico, consumosCompativeis.size(), quantidadesEstimadas);
    }

    private List<ItemPrevisao> criarItensPrevisao(Map<String, Integer> quantidadesEstimadas, String previsaoId) {
        String idPrevisao = previsaoId == null ? "previsao-em-geracao" : previsaoId;
        List<ItemPrevisao> itens = new ArrayList<>();
        for (Map.Entry<String, Integer> item : quantidadesEstimadas.entrySet()) {
            itens.add(new ItemPrevisao(idPrevisao, item.getKey(), item.getValue()));
        }
        return itens;
    }

    private double calcularPesoPorte(PorteEvento porteAtual, PorteEvento porteHistorico) {
        int distancia = Math.abs(porteAtual.ordinal() - porteHistorico.ordinal());
        if (distancia == 0) {
            return 1.0;
        }
        if (distancia == 1) {
            return 0.75;
        }
        if (distancia == 2) {
            return 0.5;
        }
        return 0.25;
    }

    private Evento buscarEventoExistente(String eventoId) {
        return eventoRepository.buscarPorId(eventoId)
                .orElseThrow(() -> new IllegalArgumentException("Evento nao encontrado."));
    }

    private PrevisaoConsumo buscarPrevisaoExistente(String previsaoId) {
        return previsaoConsumoRepository.buscarPorId(previsaoId)
                .orElseThrow(() -> new IllegalArgumentException("Previsao nao encontrada."));
    }

    private static class BaseCalculo {
        private final StatusHistoricoPrevisao statusHistorico;
        private final int totalEventosBase;
        private final Map<String, Integer> quantidadesEstimadas;

        private BaseCalculo(StatusHistoricoPrevisao statusHistorico,
                            int totalEventosBase,
                            Map<String, Integer> quantidadesEstimadas) {
            this.statusHistorico = statusHistorico;
            this.totalEventosBase = totalEventosBase;
            this.quantidadesEstimadas = quantidadesEstimadas;
        }

        private StatusHistoricoPrevisao statusHistorico() {
            return statusHistorico;
        }

        private int totalEventosBase() {
            return totalEventosBase;
        }

        private Map<String, Integer> quantidadesEstimadas() {
            return quantidadesEstimadas;
        }
    }
}
