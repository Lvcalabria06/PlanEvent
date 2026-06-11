import { ApiError } from './errors';

export const API_BASE = '/api/v1';

export async function apiRequest<T>(path: string, options?: RequestInit): Promise<T> {
	const response = await fetch(`${API_BASE}${path}`, {
		headers: {
			'Content-Type': 'application/json',
			...options?.headers,
		},
		...options,
	});

	if (!response.ok) {
		const body = await response.json().catch(() => undefined);
		const message =
			typeof body === 'object' &&
			body !== null &&
			'message' in body &&
			typeof body.message === 'string'
				? body.message
				: `Erro HTTP ${response.status}`;
		throw new ApiError(message, response.status, body);
	}

	if (response.status === 204) {
		return undefined as T;
	}

	return response.json() as Promise<T>;
}
