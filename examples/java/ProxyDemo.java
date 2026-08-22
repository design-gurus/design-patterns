// Proxy: stand in for another object, with the same interface, to control
// access, cost, or timing.
//
// Three of the four classic kinds are here: virtual (lazy), protection, and
// caching. The client code is identical in every case.
//
// Run: java ProxyDemo.java

import java.util.HashMap;
import java.util.Map;

public class ProxyDemo {
    public static void main(String[] args) {
        System.out.println("--- virtual proxy: the heavy object is never built ---");
        VideoService lazy = new LazyVideoService();
        System.out.println("proxy created, nothing loaded yet");
        System.out.println(lazy.fetch("v1"));   // only now does the real service exist

        System.out.println();
        System.out.println("--- caching proxy: the second call skips the real service ---");
        VideoService cached = new CachingProxy(new RealVideoService());
        System.out.println(cached.fetch("v2"));
        System.out.println(cached.fetch("v2"));

        System.out.println();
        System.out.println("--- protection proxy ---");
        VideoService guarded = new ProtectionProxy(new RealVideoService(), "guest");
        try {
            guarded.fetch("v3");
        } catch (SecurityException e) {
            System.out.println("blocked: " + e.getMessage());
        }
    }
}

interface VideoService {
    String fetch(String id);
}

class RealVideoService implements VideoService {
    RealVideoService() {
        System.out.println("  building the real service (expensive)");
    }

    public String fetch(String id) {
        System.out.println("  real service: fetching " + id);
        return "video[" + id + "]";
    }
}

// Virtual proxy. The expensive object is created on first real use.
class LazyVideoService implements VideoService {
    private RealVideoService real;

    public String fetch(String id) {
        if (real == null) real = new RealVideoService();
        return real.fetch(id);
    }
}

// Caching proxy. Note that a real one needs a size bound and an expiry policy.
class CachingProxy implements VideoService {
    private final VideoService real;
    private final Map<String, String> cache = new HashMap<>();

    CachingProxy(VideoService real) {
        this.real = real;
    }

    public String fetch(String id) {
        String hit = cache.get(id);
        if (hit != null) {
            System.out.println("  proxy: cache hit for " + id);
            return hit;
        }
        String value = real.fetch(id);
        cache.put(id, value);
        return value;
    }
}

// Protection proxy. The rule lives outside the object it protects.
class ProtectionProxy implements VideoService {
    private final VideoService real;
    private final String role;

    ProtectionProxy(VideoService real, String role) {
        this.real = real;
        this.role = role;
    }

    public String fetch(String id) {
        if (!role.equals("member")) throw new SecurityException(role + " cannot fetch videos");
        return real.fetch(id);
    }
}
