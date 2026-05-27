import { useState, type FormEvent } from "react";

interface TodoFormProps {
  onCreate: (title: string) => Promise<void>;
}

export function TodoForm({ onCreate }: TodoFormProps) {
  const [title, setTitle] = useState("");
  const [submitting, setSubmitting] = useState(false);

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    const trimmed = title.trim();
    if (!trimmed || submitting) return;
    setSubmitting(true);
    try {
      await onCreate(trimmed);
      setTitle("");
    } catch {
      // Error is surfaced via the parent hook.
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <form className="todo-form" onSubmit={submit}>
      <input
        type="text"
        aria-label="New todo title"
        placeholder="What needs to be done?"
        value={title}
        onChange={(e) => setTitle(e.target.value)}
        disabled={submitting}
        maxLength={200}
      />
      <button type="submit" disabled={submitting || !title.trim()}>
        Add
      </button>
    </form>
  );
}
