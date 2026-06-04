import { CONTRATANTE_PADRAO } from '../constants';

export function buildPartes(contratante: string, fornecedorNome: string): string[] {
	return [contratante.trim() || CONTRATANTE_PADRAO, fornecedorNome.trim()].filter(Boolean);
}
