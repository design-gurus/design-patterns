/**
 * Prototype: create a new object by copying a configured one.
 *
 * The whole difficulty is copy depth. This file shows both, side by side, so
 * the failure mode is visible rather than described.
 *
 * Run: npx tsx prototype.ts
 */

class Circle {
  constructor(public radius: number, public tags: string[]) {}

  copy(): Circle {
    return new Circle(this.radius, [...this.tags]); // the array is copied too
  }

  brokenShallowCopy(): Circle {
    return new Circle(this.radius, this.tags); // shares the array, which is the bug
  }

  toString(): string {
    return `circle r=${this.radius} [${this.tags}]`;
  }
}

const template = new Circle(10, ["draft"]);

const good = template.copy();
good.tags.push("reviewed");
console.log("original tags after copy edit =", template.tags);
console.log("copy tags                     =", good.tags);

console.log();
console.log("shallow copy shares the array, so editing the copy changed the original:");
const bad = template.brokenShallowCopy();
bad.tags.push("oops");
console.log("original tags =", template.tags);
console.log("copy tags     =", bad.tags);

// structuredClone is the built-in deep copy for plain data. It does not keep
// the class prototype, so the result is data, not a Circle.
const cloned = structuredClone({ radius: template.radius, tags: template.tags });
cloned.tags.push("independent");
console.log();
console.log("structuredClone is independent:", template.tags, cloned.tags);
