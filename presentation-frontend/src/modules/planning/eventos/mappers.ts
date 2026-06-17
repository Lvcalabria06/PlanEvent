import type { EventoDto, EventoResumo } from './dto';

export function fromEventoDto(dto: EventoDto): EventoResumo {
	return { id: dto.id, name: dto.nome };
}

export function toApiDateTime(date: string, hour = '09:00:00'): string {
	if (!date) return '';
	return date.includes('T') ? date : `${date}T${hour}`;
}

export function fromApiDateInput(value: string | null | undefined): string {
	if (!value) return '';
	return value.slice(0, 10);
}

export function formatPeriodo(dataInicio?: string | null, dataFim?: string | null): string {
	const ini = fromApiDateInput(dataInicio);
	const fim = fromApiDateInput(dataFim);
	if (!ini && !fim) return 'Período não informado';
	if (ini && fim) return `${ini} a ${fim}`;
	return ini || fim || '';
}
