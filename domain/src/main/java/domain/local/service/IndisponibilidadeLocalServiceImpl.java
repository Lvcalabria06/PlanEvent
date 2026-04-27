package domain.local.service;

import domain.local.entity.IndisponibilidadeLocal;
import domain.local.repository.IndisponibilidadeLocalRepository;
import domain.local.repository.LocalRepository;
import domain.local.util.IntervaloAgenda;

import java.time.LocalDateTime;

public class IndisponibilidadeLocalServiceImpl implements IndisponibilidadeLocalService {

    private final IndisponibilidadeLocalRepository indisponibilidadeLocalRepository;
    private final LocalRepository localRepository;

    public IndisponibilidadeLocalServiceImpl(
            IndisponibilidadeLocalRepository indisponibilidadeLocalRepository,
            LocalRepository localRepository) {
        this.indisponibilidadeLocalRepository = indisponibilidadeLocalRepository;
        this.localRepository = localRepository;
    }

    @Override
    public IndisponibilidadeLocal registrarIndisponibilidade(String localId, LocalDateTime inicio, LocalDateTime fim, String motivo) {
        localRepository.buscarPorId(localId)
                .orElseThrow(() -> new IllegalArgumentException("Local não encontrado."));

        IntervaloAgenda.validarFimAposInicio(inicio, fim);

        IndisponibilidadeLocal entidade = new IndisponibilidadeLocal(localId, inicio, fim, motivo);
        return indisponibilidadeLocalRepository.salvar(entidade);
    }
}
