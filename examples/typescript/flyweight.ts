/**
 * Flyweight: share the state that repeats, so a huge number of objects fits.
 *
 * One million trees. Three distinct tree types. Without sharing, that is a
 * million copies of the texture. With sharing, it is three.
 *
 * Run: npx tsx flyweight.ts
 */

const TREE_COUNT = 1_000_000;
const TEXTURE_BYTES = 2_000_000;

/** The flyweight. Immutable, because every holder shares this exact instance. */
class TreeType {
  constructor(readonly name: string, readonly texture: Uint8Array = new Uint8Array(0)) {}

  draw(x: number, y: number): string {
    return `${this.name} drawn at (${x}, ${y})`;
  }
}

class TreeFactory {
  private static pool = new Map<string, TreeType>();

  // Callers never construct a TreeType directly. That would defeat the sharing.
  static get(name: string): TreeType {
    let existing = TreeFactory.pool.get(name);
    if (existing === undefined) {
      existing = new TreeType(name);
      TreeFactory.pool.set(name, existing);
    }
    return existing;
  }

  static poolSize(): number {
    return TreeFactory.pool.size;
  }
}

/** Extrinsic state stays here, one per tree. */
interface Tree {
  x: number;
  y: number;
  type: TreeType;
}

const species = ["oak", "pine", "birch"];
const forest: Tree[] = [];

for (let i = 0; i < TREE_COUNT; i++) {
  forest.push({ x: i % 5000, y: Math.floor(i / 5000), type: TreeFactory.get(species[i % 3]) });
}

console.log("trees created        =", forest.length);
console.log("TreeType objects     =", TreeFactory.poolSize());
console.log("texture bytes stored =", TreeFactory.poolSize() * TEXTURE_BYTES);
console.log("without the pattern  =", forest.length * TEXTURE_BYTES);

console.log();
console.log(forest[0].type.draw(forest[0].x, forest[0].y));
console.log("two oaks share one TreeType =", forest[0].type === forest[3].type);
