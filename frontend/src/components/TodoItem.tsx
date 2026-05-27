import { useState, type FormEvent } from "react";
import type { Todo } from "../types/todo";

interface TodoItemProps {
  todo: Todo;
  onUpdate: (id: string, title: string, completed: boolean) => Promise<void>;
  onRemove: (id: string) => Promise<void>;
}

export function TodoItem({ todo, onUpdate, onRemove }: TodoItemProps) {
  const [editing, setEditing] = useState(false);
  const [draft, setDraft] = useState(todo.title);
  const [busy, setBusy] = useState(false);

  const toggleCompleted = async () => {
    setBusy(true);
    try {
      await onUpdate(todo.id, todo.title, !todo.completed);
    } finally {
      setBusy(false);
    }
  };

  const startEdit = () => {
    setDraft(todo.title);
    setEditing(true);
  };

  const cancelEdit = () => {
    setEditing(false);
    setDraft(todo.title);
  };

  const saveEdit = async (event: FormEvent) => {
    event.preventDefault();
    const trimmed = draft.trim();
    if (!trimmed) return;
    setBusy(true);
    try {
      await onUpdate(todo.id, trimmed, todo.completed);
      setEditing(false);
    } finally {
      setBusy(false);
    }
  };

  const remove = async () => {
    setBusy(true);
    try {
      await onRemove(todo.id);
    } finally {
      setBusy(false);
    }
  };

  return (
    <li className={`todo-item${todo.completed ? " completed" : ""}`}>
      <input
        type="checkbox"
        aria-label={`Mark "${todo.title}" as ${todo.completed ? "open" : "done"}`}
        checked={todo.completed}
        onChange={toggleCompleted}
        disabled={busy}
      />

      {editing ? (
        <form className="todo-edit" onSubmit={saveEdit}>
          <input
            type="text"
            aria-label="Edit todo title"
            value={draft}
            onChange={(e) => setDraft(e.target.value)}
            autoFocus
            maxLength={200}
            disabled={busy}
          />
          <button type="submit" disabled={busy || !draft.trim()}>
            Save
          </button>
          <button type="button" onClick={cancelEdit} disabled={busy}>
            Cancel
          </button>
        </form>
      ) : (
        <>
          <span className="todo-title" onDoubleClick={startEdit}>
            {todo.title}
          </span>
          <div className="todo-actions">
            <button type="button" onClick={startEdit} disabled={busy}>
              Edit
            </button>
            <button
              type="button"
              onClick={remove}
              disabled={busy}
              className="danger"
            >
              Delete
            </button>
          </div>
        </>
      )}
    </li>
  );
}
