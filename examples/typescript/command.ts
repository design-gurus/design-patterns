/**
 * Command: turn a request into an object, so it can be queued, logged, or undone.
 *
 * Each command records what it needs to reverse itself during execute(). That
 * is the part people skip, and it is what makes undo correct.
 *
 * Run: npx tsx command.ts
 */

class Document {
  text = "";

  append(s: string): void {
    this.text += s;
  }

  deleteFrom(position: number, count: number): void {
    this.text = this.text.slice(0, position) + this.text.slice(position + count);
  }
}

interface Command {
  readonly name: string;
  execute(): void;
  undo(): void;
}

class AppendText implements Command {
  readonly name: string;
  private position = 0; // recorded during execute, needed by undo

  constructor(private readonly doc: Document, private readonly text: string) {
    this.name = `append("${text}")`;
  }

  execute(): void {
    this.position = this.doc.text.length;
    this.doc.append(this.text);
  }

  undo(): void {
    this.doc.deleteFrom(this.position, this.text.length);
  }
}

/**
 * Some operations have no cheap inverse. Those record the previous value
 * instead, which is the Memento pattern doing the work inside a Command.
 */
class UpperCaseAll implements Command {
  readonly name = "upperCaseAll()";
  private before = "";

  constructor(private readonly doc: Document) {}

  execute(): void {
    this.before = this.doc.text;
    this.doc.text = this.before.toUpperCase();
  }

  undo(): void {
    this.doc.text = this.before;
  }
}

class Editor {
  private history: Command[] = [];

  run(command: Command): void {
    command.execute();
    this.history.push(command);
  }

  undoLast(): void {
    const last = this.history.pop();
    if (last === undefined) {
      console.log("nothing to undo");
      return;
    }
    last.undo();
  }

  names(): string[] {
    return [...this.history].reverse().map((c) => c.name);
  }
}

const doc = new Document();
const editor = new Editor();

editor.run(new AppendText(doc, "Hello"));
editor.run(new AppendText(doc, ", world"));
editor.run(new UpperCaseAll(doc));

console.log("after 3 commands :", doc.text);
console.log("history          :", editor.names());

editor.undoLast();
console.log("after 1 undo     :", doc.text);

editor.undoLast();
editor.undoLast();
console.log(`after 3 undos    : "${doc.text}"`);
