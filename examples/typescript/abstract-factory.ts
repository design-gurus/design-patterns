/**
 * Abstract Factory: create families of related objects that must match.
 *
 * Without it: a caller builds a MacButton and a WindowsCheckbox, which compiles
 * fine and ships as a bug report with a screenshot attached.
 *
 * Run: npx tsx abstract-factory.ts
 */

interface Widget {
  paint(): string;
}

class MacButton implements Widget {
  paint(): string { return "[ rounded button ]"; }
}
class MacCheckbox implements Widget {
  paint(): string { return "( o ) rounded checkbox"; }
}
class WindowsButton implements Widget {
  paint(): string { return "[= square button =]"; }
}
class WindowsCheckbox implements Widget {
  paint(): string { return "[x] square checkbox"; }
}

interface UIFactory {
  createButton(): Widget;
  createCheckbox(): Widget;
}

class MacFactory implements UIFactory {
  createButton(): Widget { return new MacButton(); }
  createCheckbox(): Widget { return new MacCheckbox(); }
}

class WindowsFactory implements UIFactory {
  createButton(): Widget { return new WindowsButton(); }
  createCheckbox(): Widget { return new WindowsCheckbox(); }
}

/**
 * The idiomatic TypeScript version. A plain object of factory functions gives
 * the same guarantee with no classes and no `implements` clause.
 */
const macFactory: UIFactory = {
  createButton: () => new MacButton(),
  createCheckbox: () => new MacCheckbox(),
};

class SettingsScreen {
  private readonly button: Widget;
  private readonly checkbox: Widget;

  constructor(factory: UIFactory) {
    this.button = factory.createButton();
    this.checkbox = factory.createCheckbox();
  }

  render(): void {
    console.log(this.button.paint());
    console.log(this.checkbox.paint());
  }
}

for (const [name, factory] of [
  ["mac (class factory)", new MacFactory()],
  ["windows (class factory)", new WindowsFactory()],
  ["mac (object factory)", macFactory],
] as const) {
  console.log(`--- ${name} ---`);
  new SettingsScreen(factory).render();
}
