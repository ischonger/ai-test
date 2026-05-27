import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { describe, expect, it, vi } from "vitest";
import { TodoForm } from "./TodoForm";

describe("TodoForm", () => {
  it("submits trimmed title and clears the input", async () => {
    const user = userEvent.setup();
    const onCreate = vi.fn().mockResolvedValue(undefined);

    render(<TodoForm onCreate={onCreate} />);

    const input = screen.getByLabelText(/new todo title/i);
    await user.type(input, "  buy milk  ");
    await user.click(screen.getByRole("button", { name: /add/i }));

    expect(onCreate).toHaveBeenCalledWith("buy milk");
    expect(input).toHaveValue("");
  });

  it("does not submit when title is empty or whitespace", async () => {
    const user = userEvent.setup();
    const onCreate = vi.fn();

    render(<TodoForm onCreate={onCreate} />);

    const button = screen.getByRole("button", { name: /add/i });
    expect(button).toBeDisabled();

    await user.type(screen.getByLabelText(/new todo title/i), "   ");
    expect(button).toBeDisabled();
    expect(onCreate).not.toHaveBeenCalled();
  });

  it("keeps the input value when create fails", async () => {
    const user = userEvent.setup();
    const onCreate = vi.fn().mockRejectedValue(new Error("boom"));

    render(<TodoForm onCreate={onCreate} />);

    const input = screen.getByLabelText(/new todo title/i);
    await user.type(input, "thing");
    await user.click(screen.getByRole("button", { name: /add/i }));

    expect(onCreate).toHaveBeenCalledWith("thing");
    expect(input).toHaveValue("thing");
  });
});
