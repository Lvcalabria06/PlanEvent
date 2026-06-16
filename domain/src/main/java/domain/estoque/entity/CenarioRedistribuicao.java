package domain.estoque.entity;

import domain.estoque.valueobject.StatusRedistribuicao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class CenarioRedistribuicao {
    private final String id;
    private final LocalDateTime dataCriacao;
    private final String geradoPorUsuarioId;
    private final LocalDateTime periodoInicio;
    private final LocalDateTime periodoFim;
    private StatusRedistribuicao status;
    private final List<AlocacaoRedistribuicao> alocacoesAtuais;
    private final List<AlocacaoRedistribuicao> alocacoesOtimizadas;
    private final List<ImpactoEvento> impactosPorEvento;
    private final List<RegistroHistorico> historico;
    private LocalDateTime dataAplicacao;
    private String aplicadoPorUsuarioId;

    public CenarioRedistribuicao(String geradoPorUsuarioId,
                                  LocalDateTime periodoInicio,
                                  LocalDateTime periodoFim,
                                  List<AlocacaoRedistribuicao> alocacoesAtuais,
                                  List<AlocacaoRedistribuicao> alocacoesOtimizadas) {
        if (geradoPorUsuarioId == null || geradoPorUsuarioId.isBlank()) {
            throw new IllegalArgumentException("Usuario responsavel pela geracao e obrigatorio.");
        }
        if (periodoInicio == null || periodoFim == null || periodoInicio.isAfter(periodoFim)) {
            throw new IllegalArgumentException("Periodo de redistribuicao invalido.");
        }
        if (alocacoesAtuais == null || alocacoesAtuais.isEmpty()) {
            throw new IllegalArgumentException("Alocacoes atuais sao obrigatorias.");
        }
        if (alocacoesOtimizadas == null || alocacoesOtimizadas.isEmpty()) {
            throw new IllegalArgumentException("Alocacoes otimizadas sao obrigatorias.");
        }
        this.id = UUID.randomUUID().toString();
        this.dataCriacao = LocalDateTime.now();
        this.geradoPorUsuarioId = geradoPorUsuarioId;
        this.periodoInicio = periodoInicio;
        this.periodoFim = periodoFim;
        this.status = StatusRedistribuicao.PENDENTE;
        this.alocacoesAtuais = new ArrayList<>(alocacoesAtuais);
        this.alocacoesOtimizadas = new ArrayList<>(alocacoesOtimizadas);
        this.impactosPorEvento = calcularImpactos();
        this.historico = new ArrayList<>();
    }

    public void aplicar(String usuarioId) {
        if (this.status != StatusRedistribuicao.PENDENTE) {
            throw new IllegalStateException("Apenas cenarios pendentes podem ser aplicados.");
        }
        if (usuarioId == null || usuarioId.isBlank()) {
            throw new IllegalArgumentException("Usuario responsavel pela aplicacao e obrigatorio.");
        }
        this.status = StatusRedistribuicao.APLICADA;
        this.dataAplicacao = LocalDateTime.now();
        this.aplicadoPorUsuarioId = usuarioId;
        registrarHistorico(usuarioId, "Redistribuicao aplicada pelo gestor.");
    }

    public void invalidar(String usuarioId, String motivo) {
        if (this.status == StatusRedistribuicao.APLICADA) {
            throw new IllegalStateException("Nao e possivel invalidar cenario ja aplicado.");
        }
        this.status = StatusRedistribuicao.INVALIDADA;
        registrarHistorico(usuarioId, "Cenario invalidado: " + motivo);
    }

    public boolean isPendente() {
        return this.status == StatusRedistribuicao.PENDENTE;
    }

    public boolean isAplicada() {
        return this.status == StatusRedistribuicao.APLICADA;
    }

    public boolean isInvalidada() {
        return this.status == StatusRedistribuicao.INVALIDADA;
    }

    private List<ImpactoEvento> calcularImpactos() {
        Map<String, List<AlocacaoRedistribuicao>> atuaisPorEvento = alocacoesAtuais.stream()
                .collect(Collectors.groupingBy(AlocacaoRedistribuicao::getEventoId));

        Map<String, List<AlocacaoRedistribuicao>> otimizadasPorEvento = alocacoesOtimizadas.stream()
                .collect(Collectors.groupingBy(AlocacaoRedistribuicao::getEventoId));

        List<ImpactoEvento> impactos = new ArrayList<>();

        for (String eventoId : otimizadasPorEvento.keySet()) {
            List<AlocacaoRedistribuicao> otimizadas = otimizadasPorEvento.get(eventoId);
            List<AlocacaoRedistribuicao> atuais = atuaisPorEvento.getOrDefault(eventoId, List.of());

            List<ImpactoEvento.ImpactoItem> itensImpactados = new ArrayList<>();
            for (AlocacaoRedistribuicao otimizada : otimizadas) {
                int quantidadeAnterior = atuais.stream()
                        .filter(a -> a.getItemEstoqueId().equals(otimizada.getItemEstoqueId()))
                        .mapToInt(AlocacaoRedistribuicao::getQuantidadeAnterior)
                        .sum();

                itensImpactados.add(new ImpactoEvento.ImpactoItem(
                        otimizada.getItemEstoqueId(),
                        quantidadeAnterior,
                        otimizada.getQuantidadeRedistribuida()
                ));
            }
            impactos.add(new ImpactoEvento(eventoId, itensImpactados));
        }

        return impactos;
    }

    private void registrarHistorico(String usuarioId, String descricao) {
        historico.add(new RegistroHistorico(this.id, usuarioId, descricao, new ArrayList<>(alocacoesOtimizadas)));
    }

    public String getId() { return id; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public String getGeradoPorUsuarioId() { return geradoPorUsuarioId; }
    public LocalDateTime getPeriodoInicio() { return periodoInicio; }
    public LocalDateTime getPeriodoFim() { return periodoFim; }
    public StatusRedistribuicao getStatus() { return status; }
    public List<AlocacaoRedistribuicao> getAlocacoesAtuais() { return Collections.unmodifiableList(alocacoesAtuais); }
    public List<AlocacaoRedistribuicao> getAlocacoesOtimizadas() { return Collections.unmodifiableList(alocacoesOtimizadas); }
    public List<ImpactoEvento> getImpactosPorEvento() { return Collections.unmodifiableList(impactosPorEvento); }
    public List<RegistroHistorico> getHistorico() { return Collections.unmodifiableList(historico); }
    public LocalDateTime getDataAplicacao() { return dataAplicacao; }
    public String getAplicadoPorUsuarioId() { return aplicadoPorUsuarioId; }

    public static class ImpactoEvento {
        private final String eventoId;
        private final List<ImpactoItem> itensImpactados;

        public ImpactoEvento(String eventoId, List<ImpactoItem> itensImpactados) {
            this.eventoId = eventoId;
            this.itensImpactados = Collections.unmodifiableList(itensImpactados);
        }

        public String getEventoId() { return eventoId; }
        public List<ImpactoItem> getItensImpactados() { return itensImpactados; }

        public boolean possuiDeficit() {
            return itensImpactados.stream().anyMatch(i -> i.getDeficit() > 0);
        }

        public boolean possuiExcesso() {
            return itensImpactados.stream().anyMatch(i -> i.getExcesso() > 0);
        }

        public static class ImpactoItem {
            private final String itemEstoqueId;
            private final int quantidadeAnterior;
            private final int quantidadeRedistribuida;
            private final int deficit;
            private final int excesso;

            public ImpactoItem(String itemEstoqueId, int quantidadeAnterior, int quantidadeRedistribuida) {
                this.itemEstoqueId = itemEstoqueId;
                this.quantidadeAnterior = quantidadeAnterior;
                this.quantidadeRedistribuida = quantidadeRedistribuida;
                int diferenca = quantidadeRedistribuida - quantidadeAnterior;
                this.deficit = diferenca < 0 ? Math.abs(diferenca) : 0;
                this.excesso = diferenca > 0 ? diferenca : 0;
            }

            public String getItemEstoqueId() { return itemEstoqueId; }
            public int getQuantidadeAnterior() { return quantidadeAnterior; }
            public int getQuantidadeRedistribuida() { return quantidadeRedistribuida; }
            public int getDeficit() { return deficit; }
            public int getExcesso() { return excesso; }
        }
    }

    public static class RegistroHistorico {
        private final String id;
        private final String cenarioRedistribuicaoId;
        private final String usuarioResponsavelId;
        private final LocalDateTime dataHora;
        private final String descricao;
        private final List<AlocacaoRedistribuicao> alocacoesSnapshot;

        public RegistroHistorico(String cenarioRedistribuicaoId,
                                  String usuarioResponsavelId,
                                  String descricao,
                                  List<AlocacaoRedistribuicao> alocacoesSnapshot) {
            this.id = UUID.randomUUID().toString();
            this.cenarioRedistribuicaoId = cenarioRedistribuicaoId;
            this.usuarioResponsavelId = usuarioResponsavelId;
            this.dataHora = LocalDateTime.now();
            this.descricao = descricao;
            this.alocacoesSnapshot = Collections.unmodifiableList(new ArrayList<>(alocacoesSnapshot));
        }

        public String getId() { return id; }
        public String getCenarioRedistribuicaoId() { return cenarioRedistribuicaoId; }
        public String getUsuarioResponsavelId() { return usuarioResponsavelId; }
        public LocalDateTime getDataHora() { return dataHora; }
        public String getDescricao() { return descricao; }
        public List<AlocacaoRedistribuicao> getAlocacoesSnapshot() { return alocacoesSnapshot; }
    }
}
