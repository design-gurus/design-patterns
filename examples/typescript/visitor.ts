/**
 * Visitor: add an operation to a class hierarchy without editing its classes.
 *
 * The classic double-dispatch version is first. The discriminated union at the
 * end is what you would usually write in TypeScript: the same separation, with
 * the compiler checking that every node kind is handled.
 *
 * Run: npx tsx visitor.ts
 */

interface Visitor {
  visitParagraph(node: Paragraph): void;
  visitImage(node: ImageNode): void;
  visitCode(node: CodeBlock): void;
}

interface DocNode {
  accept(visitor: Visitor): void;
}

// Each node class has exactly one line of dispatch and nothing else.
class Paragraph implements DocNode {
  constructor(readonly text: string) {}
  accept(v: Visitor): void { v.visitParagraph(this); }
}

class ImageNode implements DocNode {
  constructor(readonly src: string, readonly alt: string) {}
  accept(v: Visitor): void { v.visitImage(this); }
}

class CodeBlock implements DocNode {
  constructor(readonly language: string, readonly source: string) {}
  accept(v: Visitor): void { v.visitCode(this); }
}

// One operation, one class. This is the payoff.
class WordCounter implements Visitor {
  total = 0;
  visitParagraph(node: Paragraph): void { this.total += node.text.split(/\s+/).length; }
  visitImage(_node: ImageNode): void {}
  visitCode(_node: CodeBlock): void {}
}

class HtmlExporter implements Visitor {
  private lines: string[] = [];
  visitParagraph(node: Paragraph): void { this.lines.push(`<p>${node.text}</p>`); }
  visitImage(node: ImageNode): void { this.lines.push(`<img src="${node.src}" alt="${node.alt}">`); }
  visitCode(node: CodeBlock): void { this.lines.push(`<pre lang="${node.language}">${node.source}</pre>`); }
  output(): string { return this.lines.join("\n"); }
}

const document: DocNode[] = [
  new Paragraph("Design patterns are named solutions to recurring problems."),
  new ImageNode("diagram.png", "a class diagram"),
  new CodeBlock("ts", "const x = 1;"),
  new Paragraph("Each one has a cost."),
];

const words = new WordCounter();
const html = new HtmlExporter();
for (const node of document) {
  node.accept(words);
  node.accept(html);
}

console.log("word count =", words.total);
console.log();
console.log("--- html ---");
console.log(html.output());

// The idiomatic version. No accept() methods, and the compiler errors if a new
// node kind is added and this switch is not updated.
type Node =
  | { kind: "paragraph"; text: string }
  | { kind: "image"; src: string; alt: string }
  | { kind: "code"; language: string; source: string };

function toPlainText(node: Node): string {
  switch (node.kind) {
    case "paragraph": return node.text;
    case "image": return `[image: ${node.alt}]`;
    case "code": return node.source.split("\n").map((l) => "    " + l).join("\n");
  }
}

const asData: Node[] = [
  { kind: "paragraph", text: "Design patterns are named solutions." },
  { kind: "image", src: "diagram.png", alt: "a class diagram" },
  { kind: "code", language: "ts", source: "const x = 1;" },
];

console.log();
console.log("--- plain text, via a union and a switch ---");
console.log(asData.map(toPlainText).join("\n"));
