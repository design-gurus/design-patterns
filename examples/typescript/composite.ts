/**
 * Composite: treat a single object and a tree of objects the same way.
 *
 * This file shows both forms. The class version is the classic pattern. The
 * discriminated union below is what you would usually write in TypeScript,
 * because the compiler then checks that every node kind is handled.
 *
 * Run: npx tsx composite.ts
 */

interface TreeNode {
  size(): number;
  count(): number;
  render(depth?: number): string;
}

class FileNode implements TreeNode {
  constructor(private readonly name: string, private readonly bytes: number) {}
  size(): number { return this.bytes; }
  count(): number { return 1; }
  render(depth = 0): string { return `${"  ".repeat(depth)}${this.name} (${this.bytes})`; }
}

class FolderNode implements TreeNode {
  private children: TreeNode[] = [];
  constructor(private readonly name: string) {}

  // add() lives only on the composite, so a file never gets a method that throws.
  add(child: TreeNode): this {
    this.children.push(child);
    return this;
  }

  size(): number { return this.children.reduce((t, c) => t + c.size(), 0); }
  count(): number { return this.children.reduce((t, c) => t + c.count(), 0); }
  render(depth = 0): string {
    return [`${"  ".repeat(depth)}${this.name}/`, ...this.children.map((c) => c.render(depth + 1))].join("\n");
  }
}

const tree = new FolderNode("project")
  .add(new FileNode("README.md", 2048))
  .add(
    new FolderNode("src")
      .add(new FileNode("main.ts", 12000))
      .add(new FolderNode("util").add(new FileNode("strings.ts", 3500)).add(new FileNode("dates.ts", 4100))),
  )
  .add(new FolderNode("docs").add(new FileNode("guide.md", 8800)));

console.log(tree.render());
console.log("total bytes =", tree.size());
console.log("file count  =", tree.count());

// The idiomatic TypeScript version: data plus a function, with exhaustiveness
// checked at compile time.
type Entry =
  | { kind: "file"; name: string; bytes: number }
  | { kind: "folder"; name: string; children: Entry[] };

function totalBytes(entry: Entry): number {
  switch (entry.kind) {
    case "file":
      return entry.bytes;
    case "folder":
      return entry.children.reduce((t, c) => t + totalBytes(c), 0);
  }
}

const asData: Entry = {
  kind: "folder",
  name: "project",
  children: [
    { kind: "file", name: "README.md", bytes: 2048 },
    { kind: "folder", name: "docs", children: [{ kind: "file", name: "guide.md", bytes: 8800 }] },
  ],
};

console.log("union version total =", totalBytes(asData));
