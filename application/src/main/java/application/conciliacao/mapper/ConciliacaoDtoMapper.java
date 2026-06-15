package application.conciliacao.mapper;

import application.conciliacao.dto.DespesaResumoResponse;
import application.conciliacao.dto.ItemRelatorioResponse;
import application.conciliacao.dto.RelatorioConciliacaoResponse;
import application.conciliacao.dto.VinculoConciliacaoResponse;
import domain.conciliacao.entity.RelatorioConciliacao;
import domain.conciliacao.entity.VinculoConciliacao;
import domain.financeiro.entity.Despesa;

public final class ConciliacaoDtoMapper {

    private ConciliacaoDtoMapper() {}

    public static VinculoConciliacaoResponse paraResposta(VinculoConciliacao vinculo) {
        return new VinculoConciliacaoResponse(
                vinculo.getId(),
                vinculo.getEventoId(),
                vinculo.getDespesaId(),
                vinculo.getContratoId(),
                vinculo.getMetodo(),
                vinculo.getResponsavelId(),
                vinculo.getDataConciliacao(),
                vinculo.getCreatedAt(),
                vinculo.getUpdatedAt()
        );
    }

    public static RelatorioConciliacaoResponse paraResposta(RelatorioConciliacao relatorio) {
        var itens = relatorio.getItens().stream()
                .map(i -> new ItemRelatorioResponse(i.despesaId(), i.contratoId(), i.status(), i.metodo()))
                .toList();
        return new RelatorioConciliacaoResponse(
                relatorio.getId(),
                relatorio.getEventoId(),
                relatorio.getResponsavelId(),
                relatorio.getDataGeracao(),
                itens
        );
    }

    public static DespesaResumoResponse paraResumo(Despesa despesa) {
        return new DespesaResumoResponse(
                despesa.getId(),
                despesa.getEventoId(),
                despesa.getCategoria(),
                despesa.getFornecedorId(),
                despesa.getValor(),
                despesa.getData(),
                despesa.getStatus()
        );
    }
}
