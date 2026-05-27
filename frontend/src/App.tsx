import { TodoForm } from "./components/TodoForm";
import { TodoList } from "./components/TodoList";
import { useTodos } from "./hooks/useTodos";
import "./App.css";

export default function App() {
  const { todos, loading, error, create, update, remove } = useTodos();

  return (
    <main className="app">
      <h1>Todos</h1>
      <TodoForm onCreate={create} />

      {error && (
        <div role="alert" className="error">
          {error}
        </div>
      )}

      {loading ? (
        <p className="loading">Loading…</p>
      ) : (
        <TodoList todos={todos} onUpdate={update} onRemove={remove} />
      )}
    </main>
  );
}
