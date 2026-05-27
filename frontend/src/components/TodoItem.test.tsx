import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { describe, expect, it, vi } from "vitest";
import { TodoItem } from "./TodoItem";
import type { Todo } from "../types/todo";

const sample: Todo = {
  id: "abc",
  title: "buy milk",
  completed: false,
  createdAt: "2026-05-27T10:00:00Z",
  updatedAt: "2026-05-27T10:00:00Z",
};

describe("TodoItem", () => {
  it("toggles completion via the checkbox", async () => {
    const user = userEvent.setup();
    const onUpdate = vi.fn().mockResolvedValue(undefined);
    const onRemove = vi.fn();

    render(<TodoItem todo={sample} onUpdate={onUpdate} onRemove={onRemove} />);

    await user.click(screen.getByRole("checkbox"));
    expect(onUpdate).toHaveBeenCalledWith("abc", "buy milk", true);
  });

  it("edits the title and saves trimmed value", async () => {
    const user = userEvent.setup();
    const onUpdate = vi.fn().mockResolvedValue(undefined);
    const onRemove = vi.fn();

    render(<TodoItem todo={sample} onUpdate={onUpdate} onRemove={onRemove} />);

    await user.click(screen.getByRole("button", { name: /edit/i }));

    const input = screen.getByLabelText(/edit todo title/i);
    await user.clear(input);
    await user.type(input, "  walk dog  ");
    await user.click(screen.getByRole("button", { name: /save/i }));

    expect(onUpdate).toHaveBeenCalledWith("abc", "walk dog", false);
  });

  it("cancel restores the original title without calling onUpdate", async () => {
    const user = userEvent.setup();
    const onUpdate = vi.fn();
    const onRemove = vi.fn();

    render(<TodoItem todo={sample} onUpdate={onUpdate} onRemove={onRemove} />);

    await user.click(screen.getByRole("button", { name: /edit/i }));
    const input = screen.getByLabelText(/edit todo title/i);
    await user.clear(input);
    await user.type(input, "something else");
    await user.click(screen.getByRole("button", { name: /cancel/i }));

    expect(onUpdate).not.toHaveBeenCalled();
    expect(screen.getByText("buy milk")).toBeInTheDocument();
  });

  it("calls onRemove with the todo id", async () => {
    const user = userEvent.setup();
    const onUpdate = vi.fn();
    const onRemove = vi.fn().mockResolvedValue(undefined);

    render(<TodoItem todo={sample} onUpdate={onUpdate} onRemove={onRemove} />);

    await user.click(screen.getByRole("button", { name: /delete/i }));
    expect(onRemove).toHaveBeenCalledWith("abc");
  });
});
