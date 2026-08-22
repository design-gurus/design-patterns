/**
 * Singleton: one shared instance, with one place that owns its lifecycle.
 *
 * Without it: every caller builds its own ConfigStore, so the file is read
 * many times and nothing agrees on the values.
 *
 * Run: npx tsx singleton.ts
 */

class ConfigStore {
  private static instance: ConfigStore | null = null;
  private readonly values: Map<string, string>;

  private constructor() {
    console.log("reading config from disk (this line must print once)");
    this.values = new Map([
      ["region", "us-east-1"],
      ["tier", "standard"],
    ]);
  }

  static getInstance(): ConfigStore {
    if (ConfigStore.instance === null) {
      ConfigStore.instance = new ConfigStore();
    }
    return ConfigStore.instance;
  }

  get(key: string): string | undefined {
    return this.values.get(key);
  }
}

// The idiomatic version. A module body runs once, so exporting a built object
// gives every importer the same instance with no private constructor needed.
export const config = ConfigStore.getInstance();

const a = ConfigStore.getInstance();
const b = ConfigStore.getInstance();

console.log("region      =", a.get("region"));
console.log("same object =", a === b);
console.log("module export is the same one =", a === config);
