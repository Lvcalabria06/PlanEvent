package domain.evento.service;

import domain.evento.entity.Evento;
import domain.evento.planejamento.AlertaRiscoAlocacao;
import domain.evento.planejamento.CandidatoAnaliseLocal;
import domain.evento.planejamento.ResultadoAnaliseAlocacao;
import domain.evento.repository.EventoRepository;
import domain.evento.strategy.ClassificadorAlocacaoLocal;
import domain.evento.strategy.ConflitoAgendaLocalEvaluator;
import domain.evento.strategy.CriterioAgendaLocalStrategy;
import domain.evento.strategy.CriterioCapacidadeLocalStrategy;
import domain.evento.strategy.CriterioCustoTetoLocalStrategy;
import domain.evento.strategy.CriterioInfraestruturaLocalStrategy;
import domain.evento.strategy.CriterioLocalInativoStrategy;
import domain.evento.strategy.CriterioRecomendadoLocalStrategy;
import domain.evento.strategy.FabricaContextoAlocacaoLocal;
import domain.evento.valueobject.ClassificacaoAlocacaoLocal;
import domain.evento.valueobject.MotivoAlertaAlocacao;
import domain.local.entity.Local;
import domain.local.repository.IndisponibilidadeLocalRepository;
import domain.local.repository.LocalRepository;
import domain.local.repository.ManutencaoRepository;
import domain.local.repository.ReservaLocalRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

public class PlanejamentoAlocacaoLocalServiceImpl implements PlanejamentoAlocacaoLocalService {

    private final EventoRepository eventoRepository;
    private final LocalRepository localRepository;
    private final ConflitoAgendaLocalEvaluator conflitoAgendaEvaluator;
    private final ClassificadorAlocacaoLocal classificador;

    public PlanejamentoAlocacaoLocalServiceImpl(
            EventoRepository eventoRepository,
            LocalRepository localRepository,
            ReservaLocalRepository reservaLocalRepository,
            IndisponibilidadeLocalRepository indisponibilidadeLocalRepository,
            ManutencaoRepository manutencaoRepository) {
        this.eventoRepository = eventoRepository;
        this.localRepository = localRepository;
        this.conflitoAgendaEvaluator = new ConflitoAgendaLocalEvaluator(
                reservaLocalRepository,
                indisponibilidadeLocalRepository,
                manutencaoRepository);
        this.classificador = new ClassificadorAlocacaoLocal(List.of(
                new CriterioLocalInativoStrategy(),
                new CriterioAgendaLocalStrategy(),
                new CriterioCapacidadeLocalStrategy(),
                new CriterioCustoTetoLocalStrategy(),
                new CriterioInfraestruturaLocalStrategy(),
                new CriterioRecomendadoLocalStrategy()));
    }

    @Override
    public ResultadoAnaliseAlocacao analisarLocaisParaEvento(String eventoId, BigDecimal tetoCusto) {
        Evento evento = buscarEvento(eventoId);
        BigDecimal teto = validarTeto(tetoCusto);
        List<CandidatoAnaliseLocal> candidatos = new ArrayList<>();
        for (Local local : localRepository.listarTodos()) {
            candidatos.add(avaliarLocal(evento, local, teto));
        }
        return new ResultadoAnaliseAlocacao(eventoId, candidatos);
    }

    @Override
    public Evento registrarParametrosPlanejamento(
            String eventoId,
            BigDecimal tetoCusto,
            LocalDateTime janelaInicio,
            LocalDateTime janelaFim) {
        Evento evento = buscarEvento(eventoId);
        if (evento.isPlanejamentoConfirmado()) {
            throw new IllegalStateException("Não é possível alterar parâmetros após confirmar o planejamento.");
        }
        validarTeto(tetoCusto);
        evento.definirTetoCustoEspacoInformado(tetoCusto);
        if (janelaInicio != null && janelaFim != null) {
            evento.definirJanelaPlanejamento(janelaInicio, janelaFim);
        } else if (janelaInicio != null || janelaFim != null) {
            throw new IllegalArgumentException("Informe início e fim da janela ou omita ambos.");
        }
        return eventoRepository.salvar(evento);
    }

    @Override
    public Evento fixarLocalPrincipal(String eventoId, String localId, BigDecimal tetoCusto) {
        Evento evento = buscarEvento(eventoId);
        if (evento.isPlanejamentoConfirmado()) {
            throw new IllegalStateException("Não é possível alterar o local livremente após confirmar o planejamento.");
        }
        BigDecimal teto = validarTeto(tetoCusto);
        Local local = buscarLocal(localId);
        CandidatoAnaliseLocal candidato = avaliarLocal(evento, local, teto);
        if (!candidato.podeSerPrincipal()) {
            throw new IllegalArgumentException(
                    "Local não pode ser principal com os dados atuais: " + candidato.getJustificativa());
        }
        evento.definirTetoCustoEspacoInformado(teto);
        evento.alterarLocal(localId);
        return eventoRepository.salvar(evento);
    }

    @Override
    public Evento registrarAlternativasContingencia(String eventoId, List<String> localIdsOrdenados) {
        Evento evento = buscarEvento(eventoId);
        BigDecimal teto = evento.getTetoCustoEspacoInformado();
        if (teto == null) {
            throw new IllegalStateException("Informe o teto de custo do espaço antes de registrar alternativas.");
        }
        LinkedHashSet<String> vistos = new LinkedHashSet<>();
        for (String id : localIdsOrdenados) {
            if (!vistos.add(id)) {
                throw new IllegalArgumentException("Lista de alternativas não pode conter duplicados.");
            }
            Local local = buscarLocal(id);
            CandidatoAnaliseLocal c = avaliarLocal(evento, local, teto);
            if (!c.podeSerPrincipal()) {
                throw new IllegalArgumentException(
                        "Alternativa inválida para contingência (" + id + "): " + c.getJustificativa());
            }
        }
        evento.definirAlternativasContingenciaOrdenadas(localIdsOrdenados);
        return eventoRepository.salvar(evento);
    }

    @Override
    public Optional<AlertaRiscoAlocacao> avaliarRiscoAlocacaoPrincipal(String eventoId) {
        Evento evento = buscarEvento(eventoId);
        if (evento.getLocalId() == null || evento.getLocalId().isBlank()) {
            return Optional.empty();
        }
        BigDecimal teto = evento.getTetoCustoEspacoInformado();
        if (teto == null) {
            throw new IllegalStateException("Registre o teto de custo do espaço para o evento antes de avaliar riscos.");
        }
        Local principal = localRepository.buscarPorId(evento.getLocalId()).orElse(null);
        if (principal == null) {
            return Optional.of(new AlertaRiscoAlocacao(
                    eventoId,
                    evento.getLocalId(),
                    "Local principal não está mais cadastrado.",
                    List.of(MotivoAlertaAlocacao.PRINCIPAL_NAO_LOCALIZADO),
                    sugerirSubstituto(evento, teto)));
        }
        CandidatoAnaliseLocal candidato = avaliarLocal(evento, principal, teto);
        if (candidato.podeSerPrincipal()) {
            return Optional.empty();
        }
        List<MotivoAlertaAlocacao> motivos = extrairMotivos(candidato);
        String descricao = candidato.getJustificativa();
        return Optional.of(new AlertaRiscoAlocacao(
                eventoId,
                evento.getLocalId(),
                descricao,
                motivos,
                sugerirSubstituto(evento, teto)));
    }

    @Override
    public Evento executarTrocaPrincipalPorContingencia(String eventoId, String novoLocalId, String usuarioId, String motivo) {
        Evento evento = buscarEvento(eventoId);
        if (!evento.isPlanejamentoConfirmado()) {
            throw new IllegalStateException(
                    "Troca documentada por contingência só após confirmação da preparação inicial.");
        }
        BigDecimal teto = evento.getTetoCustoEspacoInformado();
        if (teto == null) {
            throw new IllegalStateException("Teto de custo do espaço não registrado para o evento.");
        }
        Local novo = buscarLocal(novoLocalId);
        CandidatoAnaliseLocal candidato = avaliarLocal(evento, novo, teto);
        if (!candidato.podeSerPrincipal()) {
            throw new IllegalArgumentException(
                    "Novo local inválido como substituto: " + candidato.getJustificativa());
        }
        evento.substituirLocalPrincipalPorContingenciaDocumentada(novoLocalId, usuarioId, motivo);
        return eventoRepository.salvar(evento);
    }

    private String sugerirSubstituto(Evento evento, BigDecimal teto) {
        ResultadoAnaliseAlocacao resultado = analisarLocaisParaEvento(evento.getId(), teto);
        Set<String> ordem = new LinkedHashSet<>(evento.getLocaisContingenciaOrdenados());
        for (String localId : ordem) {
            Optional<CandidatoAnaliseLocal> c = resultado.getCandidatos().stream()
                    .filter(x -> x.getLocalId().equals(localId))
                    .findFirst();
            if (c.isPresent() && c.get().podeSerPrincipal()) {
                return localId;
            }
        }
        return resultado.getCandidatos().stream()
                .filter(CandidatoAnaliseLocal::podeSerPrincipal)
                .filter(c -> !c.getLocalId().equals(evento.getLocalId()))
                .map(CandidatoAnaliseLocal::getLocalId)
                .findFirst()
                .orElse(null);
    }

    private List<MotivoAlertaAlocacao> extrairMotivos(CandidatoAnaliseLocal c) {
        List<MotivoAlertaAlocacao> m = new ArrayList<>();
        String j = c.getJustificativa() != null ? c.getJustificativa() : "";
        if (j.contains("inativo")) {
            m.add(MotivoAlertaAlocacao.LOCAL_INATIVO);
        }
        if (!c.isAgendaOk()) {
            if (j.toLowerCase(Locale.ROOT).contains("manuten")) {
                m.add(MotivoAlertaAlocacao.MANUTENCAO);
            } else if (j.contains("reserva")) {
                m.add(MotivoAlertaAlocacao.RESERVA_EXISTENTE);
            } else if (j.contains("bloqueio") || j.contains("indisponibilidade")) {
                m.add(MotivoAlertaAlocacao.INDISPONIBILIDADE_OU_BLOQUEIO);
            } else {
                m.add(MotivoAlertaAlocacao.CONFLITO_AGENDA);
            }
        }
        if (c.isAcimaDoTeto()) {
            m.add(MotivoAlertaAlocacao.CUSTO_ACIMA_DO_TETO);
        }
        if (!c.isCapacidadeOk()) {
            m.add(MotivoAlertaAlocacao.CAPACIDADE_INSUFICIENTE);
        }
        if (c.getClassificacao() == ClassificacaoAlocacaoLocal.INADEQUADO
                && m.isEmpty()) {
            m.add(MotivoAlertaAlocacao.INFRAESTRUTURA_INSUFICIENTE);
        }
        if (m.isEmpty()) {
            m.add(MotivoAlertaAlocacao.CONFLITO_AGENDA);
        }
        return m;
    }

    private CandidatoAnaliseLocal avaliarLocal(Evento evento, Local local, BigDecimal teto) {
        var contexto = FabricaContextoAlocacaoLocal.criar(evento, local, teto, conflitoAgendaEvaluator);
        return classificador.classificar(contexto);
    }

    private Evento buscarEvento(String eventoId) {
        return eventoRepository.buscarPorId(eventoId)
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado."));
    }

    private Local buscarLocal(String localId) {
        return localRepository.buscarPorId(localId)
                .orElseThrow(() -> new IllegalArgumentException("Local não encontrado."));
    }

    private BigDecimal validarTeto(BigDecimal tetoCusto) {
        if (tetoCusto == null || tetoCusto.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Teto de custo deve ser maior ou igual a zero.");
        }
        return tetoCusto;
    }
}
