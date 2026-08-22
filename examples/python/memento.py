"""Memento: capture state so it can be restored, without exposing internals.

History stores mementos and never reads them. That is the whole point: the
caretaker manages history without knowing what state looks like.

Run: python memento.py
"""

from dataclasses import dataclass


@dataclass(frozen=True)
class Memento:
    # Frozen, and the caretaker below never touches these fields.
    _content: str
    _cursor: int
    _tags: tuple[str, ...]


class Editor:
    def __init__(self) -> None:
        self._content = ""
        self._cursor = 0
        self._tags: list[str] = []

    def type(self, text: str) -> None:
        self._content += text
        self._cursor = len(self._content)

    def add_tag(self, tag: str) -> None:
        self._tags.append(tag)

    def save(self) -> Memento:
        # The list becomes a tuple. A shallow snapshot would share the list with
        # the live object, and restoring would put back the current state.
        return Memento(self._content, self._cursor, tuple(self._tags))

    def restore(self, m: Memento) -> None:
        self._content = m._content
        self._cursor = m._cursor
        self._tags = list(m._tags)

    def __str__(self) -> str:
        return f'"{self._content}" cursor={self._cursor} tags={self._tags}'


class History:
    """The caretaker. It holds mementos and never looks inside one."""

    def __init__(self, max_depth: int = 3):
        self._stack: list[Memento] = []
        self._max_depth = max_depth

    def backup(self, editor: Editor) -> None:
        if len(self._stack) == self._max_depth:
            self._stack.pop(0)  # drop the oldest, so history is not a leak
        self._stack.append(editor.save())

    def undo(self, editor: Editor) -> None:
        if not self._stack:
            print("nothing left to undo")
            return
        editor.restore(self._stack.pop())


def main() -> None:
    editor = Editor()
    history = History(max_depth=3)

    history.backup(editor)
    editor.type("Hello")

    history.backup(editor)
    editor.type(", world")
    editor.add_tag("draft")

    history.backup(editor)
    editor.type("!")

    print("now        :", editor)
    for _ in range(4):
        history.undo(editor)
        print("after undo :", editor)


if __name__ == "__main__":
    main()
