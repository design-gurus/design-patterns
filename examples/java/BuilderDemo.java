// Builder: assemble a complex object step by step, and validate it once at the end.
//
// Without it: new HttpRequest(url, "GET", 30, 3, true, false) and nobody can
// read the call site or tell which boolean is which.
//
// Run: java BuilderDemo.java

import java.util.ArrayList;
import java.util.List;

public class BuilderDemo {
    public static void main(String[] args) {
        HttpRequest request = HttpRequest.to("https://api.example.com/orders")
                .method("POST")
                .header("content-type: application/json")
                .timeout(5)
                .retries(2)
                .build();

        System.out.println(request);

        // build() is the one place that checks rules spanning several fields.
        try {
            HttpRequest.to("https://api.example.com").timeout(-1).build();
        } catch (IllegalArgumentException e) {
            System.out.println("rejected: " + e.getMessage());
        }

        try {
            HttpRequest.to("https://api.example.com").method("GET").body("{}").build();
        } catch (IllegalArgumentException e) {
            System.out.println("rejected: " + e.getMessage());
        }
    }
}

final class HttpRequest {
    private final String url;
    private final String method;
    private final String body;
    private final List<String> headers;
    private final int timeoutSeconds;
    private final int retries;

    private HttpRequest(Builder b) {
        this.url = b.url;
        this.method = b.method;
        this.body = b.body;
        this.headers = List.copyOf(b.headers); // copy, so the builder cannot mutate us later
        this.timeoutSeconds = b.timeoutSeconds;
        this.retries = b.retries;
    }

    static Builder to(String url) {
        return new Builder(url);
    }

    @Override
    public String toString() {
        return method + " " + url + " headers=" + headers
                + " timeout=" + timeoutSeconds + "s retries=" + retries;
    }

    static final class Builder {
        private final String url;
        private String method = "GET";
        private String body = null;
        private final List<String> headers = new ArrayList<>();
        private int timeoutSeconds = 30;
        private int retries = 0;

        private Builder(String url) {
            if (url == null || url.isBlank()) throw new IllegalArgumentException("url is required");
            this.url = url;
        }

        Builder method(String m) { this.method = m; return this; }
        Builder body(String b) { this.body = b; return this; }
        Builder header(String h) { this.headers.add(h); return this; }
        Builder timeout(int seconds) { this.timeoutSeconds = seconds; return this; }
        Builder retries(int n) { this.retries = n; return this; }

        HttpRequest build() {
            if (timeoutSeconds <= 0) throw new IllegalArgumentException("timeout must be positive");
            if (retries < 0) throw new IllegalArgumentException("retries cannot be negative");
            if (body != null && method.equals("GET")) {
                throw new IllegalArgumentException("a GET request cannot carry a body");
            }
            return new HttpRequest(this);
        }
    }
}
