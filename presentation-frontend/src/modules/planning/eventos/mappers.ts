import type { EventoDto, EventoResumo } from './dto';

export function fromEventoDto(dto: EventoDto): EventoResumo {
	return { id: dto.id, name: dto.nome };
}
