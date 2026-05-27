import type {
  CreateTodoRequest,
  Todo,
  UpdateTodoRequest,
} from "../types/todo";

const BASE = "/api/todos";

async function parseOrThrow<T>(response: Response): Promise<T> {
  if (!response.ok) {
    const body = await response.text();
    throw new Error(
      `Request failed with ${response.status}${body ? `: ${body}` : ""}`,
    );
  }
  if (response.status === 204) {
    return undefined as T;
  }
  return (await response.json()) as T;
}

export const todosApi = {
  list(): Promise<Todo[]> {
    return fetch(BASE).then((r) => parseOrThrow<Todo[]>(r));
  },

  create(request: CreateTodoRequest): Promise<Todo> {
    return fetch(BASE, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(request),
    }).then((r) => parseOrThrow<Todo>(r));
  },

  update(id: string, request: UpdateTodoRequest): Promise<Todo> {
    return fetch(`${BASE}/${id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(request),
    }).then((r) => parseOrThrow<Todo>(r));
  },

  remove(id: string): Promise<void> {
    return fetch(`${BASE}/${id}`, { method: "DELETE" }).then((r) =>
      parseOrThrow<void>(r),
    );
  },
};
