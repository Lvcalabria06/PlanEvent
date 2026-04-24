package domain.equipe.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Equipe {
    private final String id;
    private final String eventoId;
    private String nome;
    private final List<MembroEquipe> membros;
    private final LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    public Equipe(String eventoId, String nome, String liderId) {
        validarEventoId(eventoId);
        validarNome(nome);

        this.id = UUID.randomUUID().toString();
        this.eventoId = eventoId;
        this.nome = nome.trim();
        this.membros = new ArrayList<>();

        if (liderId != null && !liderId.trim().isEmpty()) {
            this.membros.add(new MembroEquipe(liderId, true));
        }

        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = this.dataCriacao;
    }

    public Equipe(String eventoId, String nome, List<MembroEquipe> membros) {
        validarEventoId(eventoId);
        validarNome(nome);
        validarMembros(membros);
        validarQuantidadeDeLideres(membros);

        this.id = UUID.randomUUID().toString();
        this.eventoId = eventoId;
        this.nome = nome.trim();
        this.membros = new ArrayList<>(membros);
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = this.dataCriacao;
    }

    public void alterarNome(String novoNome) {
        validarNome(novoNome);
        this.nome = novoNome.trim();
        atualizarData();
    }

    public void adicionarMembro(String funcionarioId) {
        validarFuncionarioId(funcionarioId);

        if (possuiMembro(funcionarioId)) {
            throw new IllegalArgumentException("Funcionário já pertence à equipe.");
        }

        this.membros.add(new MembroEquipe(funcionarioId, false));
        atualizarData();
    }

    public void removerMembro(String funcionarioId, String novoLiderId) {
        validarFuncionarioId(funcionarioId);

        MembroEquipe membro = buscarMembro(funcionarioId);

        if (this.membros.size() == 1) {
            throw new IllegalStateException("Equipe deve possuir pelo menos um funcionário.");
        }

        if (membro.isLider()) {
            if (novoLiderId == null || novoLiderId.trim().isEmpty()) {
                throw new IllegalStateException("Novo líder deve ser definido antes de remover o líder atual.");
            }

            validarFuncionarioId(novoLiderId);

            if (funcionarioId.equals(novoLiderId)) {
                throw new IllegalArgumentException("Novo líder deve ser diferente do líder removido.");
            }

            if (!possuiMembro(novoLiderId)) {
                throw new IllegalArgumentException("Novo líder deve pertencer à equipe.");
            }
        }

        this.membros.remove(membro);

        if (membro.isLider()) {
            definirLider(novoLiderId);
        }

        atualizarData();
    }

    public void definirLider(String funcionarioId) {
        validarFuncionarioId(funcionarioId);

        if (!possuiMembro(funcionarioId)) {
            throw new IllegalArgumentException("Líder deve pertencer à equipe.");
        }

        for (MembroEquipe membro : membros) {
            membro.removerLideranca();
        }

        buscarMembro(funcionarioId).definirComoLider();
        atualizarData();
    }

    public boolean possuiMembro(String funcionarioId) {
        if (funcionarioId == null) return false;

        return membros.stream()
                .anyMatch(m -> m.getFuncionarioId().equals(funcionarioId));
    }

    private MembroEquipe buscarMembro(String funcionarioId) {
        return membros.stream()
                .filter(m -> m.getFuncionarioId().equals(funcionarioId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não pertence à equipe."));
    }

    private void validarEventoId(String eventoId) {
        if (eventoId == null || eventoId.trim().isEmpty()) {
            throw new IllegalArgumentException("Evento é obrigatório.");
        }
    }

    private void validarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da equipe é obrigatório.");
        }
    }

    private void validarFuncionarioId(String funcionarioId) {
        if (funcionarioId == null || funcionarioId.trim().isEmpty()) {
            throw new IllegalArgumentException("Funcionário é obrigatório.");
        }
    }

    private void validarMembros(List<MembroEquipe> membros) {
        if (membros == null || membros.isEmpty()) {
            throw new IllegalArgumentException("Equipe deve possuir pelo menos um funcionário.");
        }
    }

    private void validarQuantidadeDeLideres(List<MembroEquipe> membros) {
        long qtd = membros.stream().filter(MembroEquipe::isLider).count();

        if (qtd > 1) {
            throw new IllegalArgumentException("Equipe deve possuir no máximo um líder.");
        }
    }

    private void atualizarData() {
        this.dataAtualizacao = LocalDateTime.now();
    }

    public String getId() { return id; }

    public String getEventoId() { return eventoId; }

    public String getNome() { return nome; }

    public String getLiderId() {
        return membros.stream()
                .filter(MembroEquipe::isLider)
                .map(MembroEquipe::getFuncionarioId)
                .findFirst()
                .orElse(null);
    }

    public List<MembroEquipe> getMembros() {
        return Collections.unmodifiableList(membros);
    }

    public LocalDateTime getDataCriacao() { return dataCriacao; }

    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
}