package application.evento.dto;



import domain.evento.valueobject.PorteEvento;

import domain.evento.valueobject.TipoEvento;



import java.math.BigDecimal;

import java.time.LocalDateTime;

import java.util.List;



public record EventoResponse(

        String id,

        String nome,

        TipoEvento tipo,

        PorteEvento porte,

        int quantidadeEstimadaParticipantes,

        String objetivo,

        String localId,

        String nomeLocalPrincipal,

        BigDecimal custoLocalPrincipal,

        Integer capacidadeLocalPrincipal,

        boolean planejamentoConfirmado,

        boolean concluido,

        boolean cancelado,

        LocalDateTime dataInicio,

        LocalDateTime dataFim,

        BigDecimal tetoCustoEspacoInformado,

        String requisitosInfraestrutura,

        List<String> locaisContingenciaOrdenados,

        List<String> nomesLocaisContingencia,

        StatusAlocacaoEvento statusAlocacao,

        boolean podePlanejarLocal,

        List<TrocaLocalPlanejamentoDto> historicoTrocasLocal,

        LocalDateTime dataCriacao,

        LocalDateTime dataAtualizacao

) {}


