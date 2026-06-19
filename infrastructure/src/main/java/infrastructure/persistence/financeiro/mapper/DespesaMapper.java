package infrastructure.persistence.financeiro.mapper;

import domain.financeiro.entity.Despesa;
import infrastructure.persistence.financeiro.entity.DespesaJpaEntity;

public class DespesaMapper {

    public static DespesaJpaEntity paraJpa(Despesa dominio) {
        if (dominio == null) return null;
        return new DespesaJpaEntity(
                dominio.getId(),
                dominio.getEventoId(),
                dominio.getCategoria(),
                dominio.getFornecedorId(),
                dominio.getValor(),
                dominio.getData(),
                dominio.getLancadoPorUsuarioId(),
                dominio.getDataHoraLancamento(),
                dominio.getStatus(),
                dominio.getAprovadorId(),
                dominio.getMotivoRejeicao()
        );
    }

    public static Despesa paraDominio(DespesaJpaEntity jpa) {
        if (jpa == null) return null;
        return Despesa.reconstruir(
                jpa.getId(),
                jpa.getEventoId(),
                jpa.getCategoria(),
                jpa.getFornecedorId(),
                jpa.getValor(),
                jpa.getData(),
                jpa.getLancadoPorUsuarioId(),
                jpa.getDataHoraLancamento(),
                jpa.getStatus(),
                jpa.getAprovadorId(),
                jpa.getMotivoRejeicao()
        );
    }
}
