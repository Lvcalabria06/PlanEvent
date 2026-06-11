import { useEffect, useState } from 'react';
import { financeiroApi, type EventoResumo } from '../../api/financeiroApi';

export function useEventosFinanceiro() {
  const [eventos, setEventos] = useState<EventoResumo[]>([]);
  const [eventoId, setEventoId] = useState('');
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState(false);

  useEffect(() => {
    setCarregando(true);
    setErro(false);
    financeiroApi
      .listarEventos()
      .then((lista) => {
        setEventos(lista);
        if (lista.length > 0) {
          setEventoId((atual) => atual || lista[0].id);
        }
      })
      .catch(() => {
        setEventos([]);
        setErro(true);
      })
      .finally(() => setCarregando(false));
  }, []);

  const eventoNome =
    eventos.find((e) => e.id === eventoId)?.nome ?? 'Selecione um evento';

  return { eventos, eventoId, setEventoId, eventoNome, carregando, erro };
}
