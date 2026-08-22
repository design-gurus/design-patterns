/**
 * Proxy: stand in for another object, with the same interface, to control
 * access, cost, or timing.
 *
 * Three of the four classic kinds are here: virtual (lazy), protection, and
 * caching. JavaScript also has a built-in Proxy object, shown at the end.
 *
 * Run: npx tsx proxy.ts
 */

interface VideoService {
  fetch(id: string): string;
}

class RealVideoService implements VideoService {
  constructor() {
    console.log("  building the real service (expensive)");
  }
  fetch(id: string): string {
    console.log(`  real service: fetching ${id}`);
    return `video[${id}]`;
  }
}

/** Virtual proxy. The expensive object is created on first real use. */
class LazyVideoService implements VideoService {
  private real: RealVideoService | null = null;
  fetch(id: string): string {
    if (this.real === null) this.real = new RealVideoService();
    return this.real.fetch(id);
  }
}

/** Caching proxy. A real one needs a size bound and an expiry policy. */
class CachingProxy implements VideoService {
  private cache = new Map<string, string>();
  constructor(private readonly real: VideoService) {}
  fetch(id: string): string {
    const hit = this.cache.get(id);
    if (hit !== undefined) {
      console.log(`  proxy: cache hit for ${id}`);
      return hit;
    }
    const value = this.real.fetch(id);
    this.cache.set(id, value);
    return value;
  }
}

/** Protection proxy. The rule lives outside the object it protects. */
class ProtectionProxy implements VideoService {
  constructor(private readonly real: VideoService, private readonly role: string) {}
  fetch(id: string): string {
    if (this.role !== "member") throw new Error(`${this.role} cannot fetch videos`);
    return this.real.fetch(id);
  }
}

console.log("--- virtual proxy: the heavy object is never built until needed ---");
const lazy = new LazyVideoService();
console.log("proxy created, nothing loaded yet");
console.log(lazy.fetch("v1"));

console.log();
console.log("--- caching proxy: the second call skips the real service ---");
const cached = new CachingProxy(new RealVideoService());
console.log(cached.fetch("v2"));
console.log(cached.fetch("v2"));

console.log();
console.log("--- protection proxy ---");
try {
  new ProtectionProxy(new RealVideoService(), "guest").fetch("v3");
} catch (e) {
  console.log("blocked:", (e as Error).message);
}

console.log();
console.log("--- the language's own Proxy, which intercepts every call ---");
const counted = new Proxy(new RealVideoService(), {
  get(target, prop, receiver) {
    if (prop === "fetch") {
      return (id: string) => {
        console.log(`  intercepted: fetch(${id})`);
        return target.fetch(id);
      };
    }
    return Reflect.get(target, prop, receiver);
  },
});
console.log(counted.fetch("v4"));
