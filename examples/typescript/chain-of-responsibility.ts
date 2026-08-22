/**
 * Chain of Responsibility: pass a request along handlers until one stops it.
 *
 * The function form is shown, because that is what every real middleware stack
 * uses. A handler that never calls next() ends the request, which is both the
 * pattern's power and its most common bug.
 *
 * Run: npx tsx chain-of-responsibility.ts
 */

interface Request {
  user: string;
  token: string;
  body: string;
}

type Middleware = (req: Request, next: () => void) => void;

function runChain(req: Request, chain: Middleware[]): void {
  const step = (i: number): void => {
    if (i >= chain.length) return;
    chain[i](req, () => step(i + 1));
  };
  step(0);
}

const auth: Middleware = (req, next) => {
  console.log(`  auth: checking ${req.user}`);
  if (!req.token) {
    console.log("  rejected 401: missing token");
    return; // not calling next() ends the request here
  }
  next();
};

const rateLimit = (allowance: number): Middleware => {
  const used = new Map<string, number>();
  return (req, next) => {
    const count = (used.get(req.user) ?? 0) + 1;
    used.set(req.user, count);
    console.log(`  rate limit: ${req.user} has used ${count} of ${allowance}`);
    if (count > allowance) {
      console.log("  rejected 429: slow down");
      return;
    }
    next();
  };
};

const validate: Middleware = (req, next) => {
  console.log("  validation: checking body");
  if (!req.body) {
    console.log("  rejected 400: body is required");
    return;
  }
  next();
};

// The terminal handler. Reaching the end with nothing done should never be
// silent, so there is always something at the tail.
const business: Middleware = (req, _next) => {
  console.log(`  handled: created order for ${req.user}`);
};

const chain = [auth, rateLimit(2), validate, business];

console.log("--- valid request ---");
runChain({ user: "alice", token: "token-ok", body: '{"item":"book"}' }, chain);

console.log();
console.log("--- no token: stopped at the first handler ---");
runChain({ user: "bob", token: "", body: '{"item":"book"}' }, chain);

console.log();
console.log("--- empty body: stopped at validation ---");
runChain({ user: "alice", token: "token-ok", body: "" }, chain);

console.log();
console.log("--- rate limit: alice has now used her allowance ---");
runChain({ user: "alice", token: "token-ok", body: '{"item":"pen"}' }, chain);
