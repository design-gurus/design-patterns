/**
 * Builder: assemble a complex object step by step, and validate it once at the end.
 *
 * Without it: new HttpRequest(url, "GET", 30, 3, true, false) and nobody can
 * read the call site or tell which boolean is which.
 *
 * Run: npx tsx builder.ts
 */

interface HttpRequest {
  readonly url: string;
  readonly method: string;
  readonly body?: string;
  readonly headers: readonly string[];
  readonly timeoutSeconds: number;
  readonly retries: number;
}

class HttpRequestBuilder {
  private method_ = "GET";
  private body_?: string;
  private headers_: string[] = [];
  private timeout_ = 30;
  private retries_ = 0;

  constructor(private readonly url: string) {
    if (!url) throw new Error("url is required");
  }

  method(m: string): this { this.method_ = m; return this; }
  body(b: string): this { this.body_ = b; return this; }
  header(h: string): this { this.headers_.push(h); return this; }
  timeout(seconds: number): this { this.timeout_ = seconds; return this; }
  retries(n: number): this { this.retries_ = n; return this; }

  build(): HttpRequest {
    // One place for every rule that spans more than one field.
    if (this.timeout_ <= 0) throw new Error("timeout must be positive");
    if (this.retries_ < 0) throw new Error("retries cannot be negative");
    if (this.body_ !== undefined && this.method_ === "GET") {
      throw new Error("a GET request cannot carry a body");
    }
    return Object.freeze({
      url: this.url,
      method: this.method_,
      body: this.body_,
      headers: [...this.headers_], // copy, so later builder calls cannot mutate it
      timeoutSeconds: this.timeout_,
      retries: this.retries_,
    });
  }
}

function show(r: HttpRequest): string {
  return `${r.method} ${r.url} headers=[${r.headers}] timeout=${r.timeoutSeconds}s retries=${r.retries}`;
}

const request = new HttpRequestBuilder("https://api.example.com/orders")
  .method("POST")
  .header("content-type: application/json")
  .timeout(5)
  .retries(2)
  .build();

console.log(show(request));

for (const attempt of [
  () => new HttpRequestBuilder("https://api.example.com").timeout(-1).build(),
  () => new HttpRequestBuilder("https://api.example.com").body("{}").build(),
]) {
  try {
    attempt();
  } catch (e) {
    console.log("rejected:", (e as Error).message);
  }
}
