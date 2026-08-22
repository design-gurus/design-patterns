/**
 * Memento: capture state so it can be restored, without exposing internals.
 *
 * History stores mementos and never reads them. That is the whole point: the
 * caretaker manages history without knowing what state looks like.
 *
 * Run: npx tsx memento.ts
 */

/** Opaque on purpose. Only Editor can build one or read it back. */
class Memento {
  constructor(
    private readonly content: string,
    private readonly cursor: number,
    private readonly tags: readonly string[],
  ) {}

  /** Package-private by convention: Editor calls this, nobody else should. */
  read(): [string, number, string[]] {
    return [this.content, this.cursor, [...this.tags]];
  }
}

class Editor {
  #content = "";
  #cursor = 0;
  #tags: string[] = [];

  type(text: string): void {
    this.#content += text;
    this.#cursor = this.#content.length;
  }

  addTag(tag: string): void {
    this.#tags.push(tag);
  }

  save(): Memento {
    // The array is copied. A shallow snapshot would share it with the live
    // object, and restoring would put back the current state.
    return new Memento(this.#content, this.#cursor, [...this.#tags]);
  }

  restore(m: Memento): void {
    [this.#content, this.#cursor, this.#tags] = m.read();
  }

  toString(): string {
    return `"${this.#content}" cursor=${this.#cursor} tags=[${this.#tags}]`;
  }
}

/** The caretaker. It holds mementos and never looks inside one. */
class History {
  private stack: Memento[] = [];
  constructor(private readonly maxDepth = 3) {}

  backup(editor: Editor): void {
    if (this.stack.length === this.maxDepth) this.stack.shift(); // drop the oldest
    this.stack.push(editor.save());
  }

  undo(editor: Editor): void {
    const last = this.stack.pop();
    if (last === undefined) {
      console.log("nothing left to undo");
      return;
    }
    editor.restore(last);
  }
}

const editor = new Editor();
const history = new History(3);

history.backup(editor);
editor.type("Hello");

history.backup(editor);
editor.type(", world");
editor.addTag("draft");

history.backup(editor);
editor.type("!");

console.log("now        :", editor.toString());
for (let i = 0; i < 4; i++) {
  history.undo(editor);
  console.log("after undo :", editor.toString());
}
