/**
 * Contratos de API para listagem de eventos (select de contratos).
 */

import { apiRequest } from '../../../shared/api/client';
import { ApiNotImplementedError } from '../../../shared/api/errors';
import type { EventoDto } from './dto';

export const eventosApiPaths = {
	list: '/eventos',
} as const;

export async function listarEventosApi(): Promise<EventoDto[]> {
	throw new ApiNotImplementedError('eventosApi.listarEventos');
	// return apiRequest<EventoDto[]>(eventosApiPaths.list);
}

void apiRequest;
