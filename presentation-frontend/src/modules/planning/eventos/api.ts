import { apiRequest } from '../../../shared/api/client';
import type { EventoDto } from './dto';

export const eventosApiPaths = {
	list: '/eventos',
} as const;

export function listarEventosApi(): Promise<EventoDto[]> {
	return apiRequest<EventoDto[]>(eventosApiPaths.list);
}
