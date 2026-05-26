package domain.estoque.strategy;

import domain.estoque.valueobject.CriticidadeEvento;
import domain.evento.entity.Evento;
import domain.evento.valueobject.PorteEvento;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Politica padrao de priorizacao: combina criticidade (peso 0.5), proximidade
 * da data do evento (peso 0.3) e porte (peso 0.2). Eventos com planejamento
 * confirmado sao considerados mais criticos.
 */
public class PrioridadePadraoEventoStrategy implements EstrategiaPrioridadeEvento {

    @Override
    public double calcular(Evento evento, LocalDateTime referencia) {
        double pesoCriticidade = calcularPesoCriticidade(determinarCriticidade(evento));
        double pesoProximidade = calcularPesoProximidade(evento, referencia);
        double pesoPorte = calcularPesoPorte(evento.getPorte());
        return pesoCriticidade * 0.5 + pesoProximidade * 0.3 + pesoPorte * 0.2;
    }

    private CriticidadeEvento determinarCriticidade(Evento evento) {
        if (evento.isPlanejamentoConfirmado()) {
            return evento.getPorte() == PorteEvento.MEGA || evento.getPorte() == PorteEvento.GRANDE
                    ? CriticidadeEvento.CRITICA : CriticidadeEvento.ALTA;
        }
        return evento.getPorte() == PorteEvento.GRANDE || evento.getPorte() == PorteEvento.MEGA
                ? CriticidadeEvento.MEDIA : CriticidadeEvento.BAIXA;
    }

    private double calcularPesoCriticidade(CriticidadeEvento criticidade) {
        return switch (criticidade) {
            case CRITICA -> 1.0;
            case ALTA -> 0.75;
            case MEDIA -> 0.5;
            case BAIXA -> 0.25;
        };
    }

    private double calcularPesoProximidade(Evento evento, LocalDateTime referencia) {
        if (evento.getJanelaInicioPlanejamento() == null) {
            return 0.5;
        }
        long diasAteEvento = Duration.between(referencia, evento.getJanelaInicioPlanejamento()).toDays();
        if (diasAteEvento <= 0) return 1.0;
        if (diasAteEvento <= 7) return 0.9;
        if (diasAteEvento <= 30) return 0.6;
        return 0.3;
    }

    private double calcularPesoPorte(PorteEvento porte) {
        return switch (porte) {
            case MEGA -> 1.0;
            case GRANDE -> 0.75;
            case MEDIO -> 0.5;
            case PEQUENO -> 0.25;
        };
    }
}
