export class ApiError extends Error {
  status: number;

  constructor(message: string, status: number) {
    super(message);
    this.status = status;
    this.name = 'ApiError';
  }
}

export async function apiFetch<T>(path: string, options: RequestInit = {}): Promise<T> {
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    'X-Usuario-Id': 'gestor@empresa.com',
    ...(options.headers as Record<string, string> | undefined),
  };

  const response = await fetch(`/api${path}`, { ...options, headers });

  if (!response.ok) {
    let mensagem = response.statusText;
    try {
      const body = await response.json();
      if (body.mensagem) mensagem = body.mensagem;
    } catch {
      /* ignore */
    }
    throw new ApiError(mensagem, response.status);
  }

  if (response.status === 204) {
    return undefined as T;
  }

  return response.json() as Promise<T>;
}
