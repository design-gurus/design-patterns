"""Composite: treat a single object and a tree of objects the same way.

size() is written once. It does not care whether it is holding a file or a
folder of folders, and no caller writes an isinstance check.

Run: python composite.py
"""

from typing import Protocol


class Node(Protocol):
    def size(self) -> int: ...
    def count(self) -> int: ...
    def render(self, depth: int = 0) -> str: ...


class FileNode:
    def __init__(self, name: str, size_bytes: int):
        self.name = name
        self._bytes = size_bytes

    def size(self) -> int:
        return self._bytes

    def count(self) -> int:
        return 1

    def render(self, depth: int = 0) -> str:
        return f"{'  ' * depth}{self.name} ({self._bytes})"


class FolderNode:
    def __init__(self, name: str):
        self.name = name
        self.children: list[Node] = []

    # add() lives only on the composite, so a file never gets a method that raises.
    def add(self, child: Node) -> "FolderNode":
        self.children.append(child)
        return self

    def size(self) -> int:
        return sum(child.size() for child in self.children)

    def count(self) -> int:
        return sum(child.count() for child in self.children)

    def render(self, depth: int = 0) -> str:
        lines = [f"{'  ' * depth}{self.name}/"]
        lines.extend(child.render(depth + 1) for child in self.children)
        return "\n".join(lines)


def main() -> None:
    tree = (
        FolderNode("project")
        .add(FileNode("README.md", 2048))
        .add(
            FolderNode("src")
            .add(FileNode("main.py", 12000))
            .add(FolderNode("util").add(FileNode("strings.py", 3500)).add(FileNode("dates.py", 4100)))
        )
        .add(FolderNode("docs").add(FileNode("guide.md", 8800)))
    )

    print(tree.render())
    print("total bytes =", tree.size())
    print("file count  =", tree.count())

    one: Node = FileNode("notes.txt", 120)
    print(f"one file    = {one.size()} bytes, {one.count()} file")


if __name__ == "__main__":
    main()
