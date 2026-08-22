"""Visitor: add an operation to a class hierarchy without editing its classes.

Three node types, three operations, and the node classes contain nothing but
accept(). Adding a fourth operation adds one class and edits none.

Run: python visitor.py
"""

from dataclasses import dataclass
from typing import Protocol


class Visitor(Protocol):
    def visit_paragraph(self, node: "Paragraph") -> None: ...
    def visit_image(self, node: "ImageNode") -> None: ...
    def visit_code(self, node: "CodeBlock") -> None: ...


# Each node class has exactly one line of dispatch and nothing else.
@dataclass(frozen=True)
class Paragraph:
    text: str

    def accept(self, visitor: Visitor) -> None:
        visitor.visit_paragraph(self)


@dataclass(frozen=True)
class ImageNode:
    src: str
    alt: str

    def accept(self, visitor: Visitor) -> None:
        visitor.visit_image(self)


@dataclass(frozen=True)
class CodeBlock:
    language: str
    source: str

    def accept(self, visitor: Visitor) -> None:
        visitor.visit_code(self)


# One operation, one class. This is the payoff.
class WordCounter:
    def __init__(self) -> None:
        self.total = 0

    def visit_paragraph(self, node: Paragraph) -> None:
        self.total += len(node.text.split())

    def visit_image(self, node: ImageNode) -> None:
        pass  # images have no words

    def visit_code(self, node: CodeBlock) -> None:
        pass  # code is not prose


class HtmlExporter:
    def __init__(self) -> None:
        self.lines: list[str] = []

    def visit_paragraph(self, node: Paragraph) -> None:
        self.lines.append(f"<p>{node.text}</p>")

    def visit_image(self, node: ImageNode) -> None:
        self.lines.append(f'<img src="{node.src}" alt="{node.alt}">')

    def visit_code(self, node: CodeBlock) -> None:
        self.lines.append(f'<pre lang="{node.language}">{node.source}</pre>')

    def output(self) -> str:
        return "\n".join(self.lines)


class PlainTextExporter:
    def __init__(self) -> None:
        self.lines: list[str] = []

    def visit_paragraph(self, node: Paragraph) -> None:
        self.lines.append(node.text)

    def visit_image(self, node: ImageNode) -> None:
        self.lines.append(f"[image: {node.alt}]")

    def visit_code(self, node: CodeBlock) -> None:
        self.lines.append("\n".join("    " + line for line in node.source.splitlines()))

    def output(self) -> str:
        return "\n".join(self.lines)


def main() -> None:
    document = [
        Paragraph("Design patterns are named solutions to recurring problems."),
        ImageNode("diagram.png", "a class diagram"),
        CodeBlock("python", "x = 1\ny = 2"),
        Paragraph("Each one has a cost."),
    ]

    words, html, text = WordCounter(), HtmlExporter(), PlainTextExporter()
    for node in document:
        node.accept(words)
        node.accept(html)
        node.accept(text)

    print("word count =", words.total)
    print()
    print("--- html ---")
    print(html.output())
    print()
    print("--- plain text ---")
    print(text.output())


if __name__ == "__main__":
    main()
