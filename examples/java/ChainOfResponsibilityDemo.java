// Chain of Responsibility: pass a request along handlers until one stops it.
//
// This is the pipeline variant, which is what web middleware uses: every
// handler runs in order, and any of them may end the request.
//
// Run: java ChainOfResponsibilityDemo.java

public class ChainOfResponsibilityDemo {
    public static void main(String[] args) {
        Handler chain = new AuthHandler();
        chain.setNext(new RateLimitHandler(2))
             .setNext(new ValidationHandler())
             .setNext(new BusinessHandler());

        System.out.println("--- valid request ---");
        chain.handle(new Request("alice", "token-ok", "{\"item\":\"book\"}"));

        System.out.println();
        System.out.println("--- no token: stopped at the first handler ---");
        chain.handle(new Request("bob", "", "{\"item\":\"book\"}"));

        System.out.println();
        System.out.println("--- empty body: stopped at validation ---");
        chain.handle(new Request("alice", "token-ok", ""));

        System.out.println();
        System.out.println("--- rate limit: alice has now used her allowance ---");
        chain.handle(new Request("alice", "token-ok", "{\"item\":\"pen\"}"));
        chain.handle(new Request("alice", "token-ok", "{\"item\":\"pen\"}"));
    }
}

class Request {
    final String user;
    final String token;
    final String body;
    private boolean stopped = false;

    Request(String user, String token, String body) {
        this.user = user;
        this.token = token;
        this.body = body;
    }

    void reject(int status, String reason) {
        stopped = true;
        System.out.println("  rejected " + status + ": " + reason);
    }

    boolean isStopped() {
        return stopped;
    }
}

abstract class Handler {
    private Handler next;

    Handler setNext(Handler next) {
        this.next = next;
        return next;    // returns the new tail, so wiring reads in order
    }

    // Final, so a subclass cannot forget to call the next handler.
    final void handle(Request request) {
        if (process(request)) {
            if (next != null) {
                next.handle(request);
            }
        }
    }

    // Return false to stop the chain here.
    protected abstract boolean process(Request request);
}

class AuthHandler extends Handler {
    protected boolean process(Request r) {
        System.out.println("  auth: checking " + r.user);
        if (r.token.isEmpty()) {
            r.reject(401, "missing token");
            return false;
        }
        return true;
    }
}

class RateLimitHandler extends Handler {
    private final java.util.Map<String, Integer> used = new java.util.HashMap<>();
    private final int allowance;

    RateLimitHandler(int allowance) {
        this.allowance = allowance;
    }

    protected boolean process(Request r) {
        int count = used.merge(r.user, 1, Integer::sum);
        System.out.println("  rate limit: " + r.user + " has used " + count + " of " + allowance);
        if (count > allowance) {
            r.reject(429, "slow down");
            return false;
        }
        return true;
    }
}

class ValidationHandler extends Handler {
    protected boolean process(Request r) {
        System.out.println("  validation: checking body");
        if (r.body.isEmpty()) {
            r.reject(400, "body is required");
            return false;
        }
        return true;
    }
}

// The terminal handler. Reaching the end with nothing done should never be
// silent, so there is always something at the tail.
class BusinessHandler extends Handler {
    protected boolean process(Request r) {
        System.out.println("  handled: created order for " + r.user);
        return true;
    }
}
