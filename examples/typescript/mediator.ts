/**
 * Mediator: route component-to-component talk through one coordinator.
 *
 * Four components would otherwise hold references to each other. Here each one
 * knows only the mediator, and every interaction rule lives in one method.
 *
 * Run: npx tsx mediator.ts
 */

interface Mediator {
  changed(source: Component): void;
}

class Component {
  value = "";
  constructor(protected readonly mediator: Mediator) {}

  set(value: string): void {
    this.value = value;
    this.mediator.changed(this); // tell the mediator, never another component
  }
}

class TextField extends Component {}

class Dropdown extends Component {
  options: string[] = [];
  setOptions(options: string[]): void {
    this.options = options;
    console.log(`  room type options -> [${options}]`);
  }
}

class Label extends Component {
  setText(text: string): void {
    console.log(`  price label -> ${text}`);
  }
}

class Button extends Component {
  setEnabled(enabled: boolean): void {
    console.log(`  submit -> ${enabled ? "enabled" : "disabled"}`);
  }
}

class BookingForm implements Mediator {
  readonly date = new TextField(this);
  readonly roomType = new Dropdown(this);
  readonly price = new Label(this);
  readonly submit = new Button(this);

  // Every interaction rule, in one readable place.
  changed(source: Component): void {
    if (source === this.date) {
      this.roomType.setOptions(this.date.value ? ["single", "double", "suite"] : []);
    }

    if (source === this.date || source === this.roomType) {
      const ready = Boolean(this.date.value && this.roomType.value);
      this.price.setText(ready ? BookingForm.quote(this.roomType.value) : "select a date and room");
    }

    this.submit.setEnabled(Boolean(this.date.value && this.roomType.value));
  }

  private static quote(room: string): string {
    return { suite: "320.00 EUR", double: "180.00 EUR" }[room] ?? "120.00 EUR";
  }
}

const form = new BookingForm();

console.log("--- user picks a date ---");
form.date.set("2026-09-14");

console.log();
console.log("--- user picks a room type ---");
form.roomType.set("suite");

console.log();
console.log("--- user clears the date ---");
form.date.set("");
