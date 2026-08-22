/**
 * Iterator: walk a structure without exposing how it stores things.
 *
 * One tree, two traversal orders. In TypeScript you almost never write the
 * classic iterator class: a generator function is the same pattern with the
 * state machine written for you.
 *
 * Run: npx tsx iterator.ts
 */

class TreeNode {
  readonly children: TreeNode[];
  constructor(readonly name: string, ...children: TreeNode[]) {
    this.children = children;
  }
}

class Tree {
  constructor(private readonly root: TreeNode) {}

  /** Depth first. Symbol.iterator makes `for...of` work on this object. */
  *[Symbol.iterator](): Generator<string> {
    const stack = [this.root];
    while (stack.length > 0) {
      const node = stack.pop()!;
      for (let i = node.children.length - 1; i >= 0; i--) stack.push(node.children[i]);
      yield node.name;
    }
  }

  /** A second traversal over the same data, named and reusable. */
  *breadthFirst(): Generator<string> {
    const queue: TreeNode[] = [this.root];
    while (queue.length > 0) {
      const node = queue.shift()!;
      queue.push(...node.children);
      yield node.name;
    }
  }
}

const tree = new Tree(
  new TreeNode("a", new TreeNode("b", new TreeNode("d"), new TreeNode("e")), new TreeNode("c", new TreeNode("f"))),
);

console.log("depth first  :", [...tree].join(" "));
console.log("breadth first:", [...tree.breadthFirst()].join(" "));

// Two traversals run at once, because position lives in the iterator.
const a = tree[Symbol.iterator]();
const b = tree[Symbol.iterator]();
a.next();
console.log("independent positions:", a.next().value, "and", b.next().value);

// The trap worth knowing: a generator is consumed once.
const once = tree[Symbol.iterator]();
[...once];
console.log("second pass over a used iterator:", [...once]);
