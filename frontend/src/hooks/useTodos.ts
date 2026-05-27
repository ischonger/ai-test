import { useCallback, useEffect, useState } from "react";
import { todosApi } from "../api/todos";
import type { Todo } from "../types/todo";

export interface UseTodos {
  todos: Todo[];
  loading: boolean;
  error: string | null;
  reload: () => Promise<void>;
  create: (title: string) => Promise<void>;
  update: (id: string, title: string, completed: boolean) => Promise<void>;
  remove: (id: string) => Promise<void>;
}

export function useTodos(): UseTodos {
  const [todos, setTodos] = useState<Todo[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const reload = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      setTodos(await todosApi.list());
    } catch (e) {
      setError(e instanceof Error ? e.message : String(e));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    void reload();
  }, [reload]);

  const create = useCallback(async (title: string) => {
    try {
      const created = await todosApi.create({ title });
      setTodos((prev) => [created, ...prev]);
      setError(null);
    } catch (e) {
      setError(e instanceof Error ? e.message : String(e));
      throw e;
    }
  }, []);

  const update = useCallback(
    async (id: string, title: string, completed: boolean) => {
      try {
        const updated = await todosApi.update(id, { title, completed });
        setTodos((prev) => prev.map((t) => (t.id === id ? updated : t)));
        setError(null);
      } catch (e) {
        setError(e instanceof Error ? e.message : String(e));
        throw e;
      }
    },
    [],
  );

  const remove = useCallback(async (id: string) => {
    try {
      await todosApi.remove(id);
      setTodos((prev) => prev.filter((t) => t.id !== id));
      setError(null);
    } catch (e) {
      setError(e instanceof Error ? e.message : String(e));
      throw e;
    }
  }, []);

  return { todos, loading, error, reload, create, update, remove };
}
