/**
 * Decorator: add behavior to one object at runtime, in stackable layers.
 *
 * Watch the order. Retry outside cache retries a cache lookup. Cache outside
 * retry caches the result of a retried call. Those are different systems.
 *
 * Run: npx tsx decorator.ts
 */

interface DataSource {
  read(key: string): string;
}

class FlakyDataSource implements DataSource {
  private attempts = new Map<string, number>();

  read(key: string): string {
    const n = (this.attempts.get(key) ?? 0) + 1;
    this.attempts.set(key, n);
    console.log(`    source: attempt ${n} for ${key}`);
    if (n === 1) throw new Error("transient failure");
    return `row(${key})`;
  }
}

class LoggingSource implements DataSource {
  constructor(private readonly inner: DataSource) {}
  read(key: string): string {
    console.log(`  logging: read ${key}`);
    const value = this.inner.read(key);
    console.log(`  logging: done ${key}`);
    return value;
  }
}

class CachingSource implements DataSource {
  private cache = new Map<string, string>();
  constructor(private readonly inner: DataSource) {}
  read(key: string): string {
    const hit = this.cache.get(key);
    if (hit !== undefined) {
      console.log(`  caching: hit ${key}`);
      return hit;
    }
    console.log(`  caching: miss ${key}`);
    const value = this.inner.read(key);
    this.cache.set(key, value);
    return value;
  }
}

class RetryingSource implements DataSource {
  constructor(private readonly inner: DataSource, private readonly maxAttempts = 3) {}
  read(key: string): string {
    let last: unknown;
    for (let attempt = 1; attempt <= this.maxAttempts; attempt++) {
      try {
        return this.inner.read(key);
      } catch (e) {
        last = e;
        console.log(`  retrying: attempt ${attempt} failed`);
      }
    }
    throw last;
  }
}

const wrapped = new LoggingSource(new CachingSource(new RetryingSource(new FlakyDataSource())));

console.log("--- first read (miss, may retry) ---");
console.log("value =", wrapped.read("user:1"));

console.log();
console.log("--- second read (cache hit, no retry needed) ---");
console.log("value =", wrapped.read("user:1"));

// The function version. For a one-method interface, higher-order functions
// give you the same stacking with none of the classes.
const withTiming =
  (fn: (key: string) => string) =>
  (key: string): string => {
    console.log(`  timing: start ${key}`);
    return fn(key);
  };

console.log();
console.log("--- function version ---");
console.log("value =", withTiming((k) => wrapped.read(k))("user:1"));
