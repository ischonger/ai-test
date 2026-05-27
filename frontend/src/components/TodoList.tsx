import type { Todo } from "../types/todo";
import { TodoItem } from "./TodoItem";

interface TodoListProps {
  todos: Todo[];
  onUpdate: (id: string, title: string, completed: boolean) => Promise<void>;
  onRemove: (id: string) => Promise<void>;
}

export function TodoList({ todos, onUpdate, onRemove }: TodoListProps) {
  if (todos.length === 0) {
    return <p className="empty">No todos yet — add your first one above.</p>;
  }

  return (
    <ul className="todo-list">
      {todos.map((todo) => (
        <TodoItem
          key={todo.id}
          todo={todo}
          onUpdate={onUpdate}
          onRemove={onRemove}
        />
      ))}
    </ul>
  );
}
