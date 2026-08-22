"""Command: turn a request into an object, so it can be queued, logged, or undone.

Each command records what it needs to reverse itself during execute(). That is
the part people skip, and it is what makes undo correct.

Run: python command.py
"""

from typing import Protocol


class Document:
    def __init__(self) -> None:
        self.text = ""

    def append(self, s: str) -> None:
        self.text += s

    def delete_from(self, position: int, count: int) -> None:
        self.text = self.text[:position] + self.text[position + count :]

    def __str__(self) -> str:
        return self.text


class Command(Protocol):
    name: str
    def execute(self) -> None: ...
    def undo(self) -> None: ...


class AppendText:
    def __init__(self, doc: Document, text: str):
        self.doc, self.text = doc, text
        self.position = 0  # recorded during execute, needed by undo
        self.name = f'append("{text}")'

    def execute(self) -> None:
        self.position = len(self.doc.text)
        self.doc.append(self.text)

    def undo(self) -> None:
        self.doc.delete_from(self.position, len(self.text))


class UpperCaseAll:
    """Some operations have no cheap inverse. Those record the previous value
    instead, which is the Memento pattern doing the work inside a Command."""

    def __init__(self, doc: Document):
        self.doc = doc
        self.before = ""
        self.name = "upper_case_all()"

    def execute(self) -> None:
        self.before = self.doc.text
        self.doc.text = self.before.upper()

    def undo(self) -> None:
        self.doc.text = self.before


class Editor:
    def __init__(self) -> None:
        self._history: list[Command] = []

    def run(self, command: Command) -> None:
        command.execute()
        self._history.append(command)

    def undo_last(self) -> None:
        if not self._history:
            print("nothing to undo")
            return
        self._history.pop().undo()

    def history(self) -> list[str]:
        return [c.name for c in reversed(self._history)]


def main() -> None:
    doc = Document()
    editor = Editor()

    editor.run(AppendText(doc, "Hello"))
    editor.run(AppendText(doc, ", world"))
    editor.run(UpperCaseAll(doc))

    print("after 3 commands :", doc)
    print("history          :", editor.history())

    editor.undo_last()
    print("after 1 undo     :", doc)

    editor.undo_last()
    editor.undo_last()
    print(f'after 3 undos    : "{doc}"')


if __name__ == "__main__":
    main()
