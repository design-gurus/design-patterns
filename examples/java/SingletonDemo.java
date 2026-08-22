// Singleton: one shared instance, with one place that owns its lifecycle.
//
// Without it: every caller builds its own ConfigStore, so the file is read
// many times and nothing agrees on the values.
//
// Run: java SingletonDemo.java

import java.util.HashMap;
import java.util.Map;

public class SingletonDemo {
    public static void main(String[] args) {
        ConfigStore a = ConfigStore.getInstance();
        ConfigStore b = ConfigStore.getInstance();

        System.out.println("region      = " + a.get("region"));
        System.out.println("same object = " + (a == b));

        // The enum version is the shortest thread-safe singleton in Java.
        System.out.println("enum region = " + EnumConfig.INSTANCE.get("region"));
    }
}

final class ConfigStore {
    // A static final field. The class loader guarantees this runs once,
    // and it is thread safe without any locking.
    private static final ConfigStore INSTANCE = new ConfigStore();

    private final Map<String, String> values = new HashMap<>();

    private ConfigStore() {
        System.out.println("reading config from disk (this line must print once)");
        values.put("region", "us-east-1");
        values.put("tier", "standard");
    }

    static ConfigStore getInstance() {
        return INSTANCE;
    }

    String get(String key) {
        return values.get(key);
    }
}

enum EnumConfig {
    INSTANCE;

    private final Map<String, String> values = Map.of("region", "eu-west-1");

    String get(String key) {
        return values.get(key);
    }
}
