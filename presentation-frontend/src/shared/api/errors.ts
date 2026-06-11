export class ApiNotImplementedError extends Error {
	constructor(operation: string) {
		super(`${operation}: integração com backend pendente`);
		this.name = 'ApiNotImplementedError';
	}
}

export class ApiError extends Error {
	readonly status: number;
	readonly body: unknown;

	constructor(message: string, status: number, body?: unknown) {
		super(message);
		this.name = 'ApiError';
		this.status = status;
		this.body = body;
	}
}

export function isApiNotImplementedError(error: unknown): error is ApiNotImplementedError {
	return error instanceof ApiNotImplementedError;
}
