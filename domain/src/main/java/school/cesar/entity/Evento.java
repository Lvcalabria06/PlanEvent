package school.cesar.entity;

import school.cesar.entity.enums.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

public class Evento {
    private String id;
    private String nome;
    private String tipo;
    private String objetivo;
    private Integer quantidadeParticipantes;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private StatusEvento status;
    private BigDecimal orcamentoTotal;
    
    // Associacoes de 1:N que fazem sentido no Agregado Raiz (Evento)
    private List<Tarefa> tarefas = new ArrayList<>();
    private List<Despesa> despesas = new ArrayList<>();
    private List<Receita> receitas = new ArrayList<>();
    private List<Contrato> contratos = new ArrayList<>();
    
    // Outros relacionamentos listados no mapping 1:N:
    private List<ReservaItem> reservaItens = new ArrayList<>();
    private List<AlocacaoFuncionario> alocacoesEquipe = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Evento() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getObjetivo() { return objetivo; }
    public void setObjetivo(String objetivo) { this.objetivo = objetivo; }

    public Integer getQuantidadeParticipantes() { return quantidadeParticipantes; }
    public void setQuantidadeParticipantes(Integer quantidadeParticipantes) { this.quantidadeParticipantes = quantidadeParticipantes; }

    public LocalDateTime getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }

    public LocalDateTime getDataFim() { return dataFim; }
    public void setDataFim(LocalDateTime dataFim) { this.dataFim = dataFim; }

    public StatusEvento getStatus() { return status; }
    public void setStatus(StatusEvento status) { this.status = status; }

    public BigDecimal getOrcamentoTotal() { return orcamentoTotal; }
    public void setOrcamentoTotal(BigDecimal orcamentoTotal) { this.orcamentoTotal = orcamentoTotal; }

    public List<Tarefa> getTarefas() { return tarefas; }
    public void setTarefas(List<Tarefa> tarefas) { this.tarefas = tarefas; }

    public List<Despesa> getDespesas() { return despesas; }
    public void setDespesas(List<Despesa> despesas) { this.despesas = despesas; }

    public List<Receita> getReceitas() { return receitas; }
    public void setReceitas(List<Receita> receitas) { this.receitas = receitas; }

    public List<Contrato> getContratos() { return contratos; }
    public void setContratos(List<Contrato> contratos) { this.contratos = contratos; }

    public List<ReservaItem> getReservaItens() { return reservaItens; }
    public void setReservaItens(List<ReservaItem> reservaItens) { this.reservaItens = reservaItens; }

    public List<AlocacaoFuncionario> getAlocacoesEquipe() { return alocacoesEquipe; }
    public void setAlocacoesEquipe(List<AlocacaoFuncionario> alocacoesEquipe) { this.alocacoesEquipe = alocacoesEquipe; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
