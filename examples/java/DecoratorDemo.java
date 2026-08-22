// Decorator: add behavior to one object at runtime, in stackable layers.
//
// Watch the order. Retry outside cache retries a cache lookup. Cache outside
// retry caches the result of a retried call. Those are different systems.
//
// Run: java DecoratorDemo.java

import java.util.HashMap;
import java.util.Map;

public class DecoratorDemo {
    public static void main(String[] args) {
        DataSource plain = new FlakyDataSource();

        DataSource wrapped = new LoggingSource(new CachingSource(new RetryingSource(plain)));

        System.out.println("--- first read (miss, may retry) ---");
        System.out.println("value = " + wrapped.read("user:1"));

        System.out.println();
        System.out.println("--- second read (cache hit, no retry needed) ---");
        System.out.println("value = " + wrapped.read("user:1"));
    }
}

interface DataSource {
    String read(String key);
}

// The thing being decorated. Fails the first time it sees any key.
class FlakyDataSource implements DataSource {
    private final Map<String, Integer> attempts = new HashMap<>();

    public String read(String key) {
        int n = attempts.merge(key, 1, Integer::sum);
        System.out.println("    source: attempt " + n + " for " + key);
        if (n == 1) throw new RuntimeException("transient failure");
        return "row(" + key + ")";
    }
}

// A base decorator that forwards everything. Extending this means a subclass
// cannot silently drop a method it forgot to override.
abstract class DataSourceDecorator implements DataSource {
    protected final DataSource inner;

    protected DataSourceDecorator(DataSource inner) {
        this.inner = inner;
    }

    public String read(String key) {
        return inner.read(key);
    }
}

class LoggingSource extends DataSourceDecorator {
    LoggingSource(DataSource inner) { super(inner); }

    @Override
    public String read(String key) {
        System.out.println("  logging: read " + key);
        String result = inner.read(key);
        System.out.println("  logging: done " + key);
        return result;
    }
}

class CachingSource extends DataSourceDecorator {
    private final Map<String, String> cache = new HashMap<>();

    CachingSource(DataSource inner) { super(inner); }

    @Override
    public String read(String key) {
        String hit = cache.get(key);
        if (hit != null) {
            System.out.println("  caching: hit " + key);
            return hit;
        }
        System.out.println("  caching: miss " + key);
        String value = inner.read(key);
        cache.put(key, value);
        return value;
    }
}

class RetryingSource extends DataSourceDecorator {
    private static final int MAX_ATTEMPTS = 3;

    RetryingSource(DataSource inner) { super(inner); }

    @Override
    public String read(String key) {
        RuntimeException last = null;
        for (int i = 1; i <= MAX_ATTEMPTS; i++) {
            try {
                return inner.read(key);
            } catch (RuntimeException e) {
                last = e;
                System.out.println("  retrying: attempt " + i + " failed");
            }
        }
        throw last;
    }
}
