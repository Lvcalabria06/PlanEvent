package domain.conciliacao.valueobject;

public record ItemRelatorioConciliacao(
        String despesaId,
        String contratoId,
        StatusConciliacao status,
        MetodoConciliacao metodo
) {
    public ItemRelatorioConciliacao {
        if (despesaId == null || despesaId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID da despesa é obrigatório no item do relatório.");
        }
        if (status == null) {
            throw new IllegalArgumentException("Status da conciliação é obrigatório no item do relatório.");
        }
        if (status == StatusConciliacao.COBERTA && contratoId == null) {
            throw new IllegalArgumentException("Item coberto deve informar o contrato vinculado.");
        }
        if (status == StatusConciliacao.COBERTA && metodo == null) {
            throw new IllegalArgumentException("Item coberto deve informar o método de conciliação.");
        }
    }
}
