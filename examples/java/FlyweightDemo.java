// Flyweight: share the state that repeats, so a huge number of objects fits.
//
// One million trees. Three distinct tree types. Without sharing, that is a
// million copies of the texture. With sharing, it is three.
//
// Run: java FlyweightDemo.java

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlyweightDemo {
    private static final int TREE_COUNT = 1_000_000;

    public static void main(String[] args) {
        String[] species = { "oak", "pine", "birch" };
        List<Tree> forest = new ArrayList<>(TREE_COUNT);

        for (int i = 0; i < TREE_COUNT; i++) {
            String name = species[i % species.length];
            // Extrinsic state (x, y) stays per tree. Intrinsic state is shared.
            forest.add(new Tree(i % 5000, i / 5000, TreeFactory.get(name)));
        }

        System.out.println("trees created        = " + forest.size());
        System.out.println("TreeType objects     = " + TreeFactory.poolSize());
        System.out.println("texture bytes stored = " + TreeFactory.poolSize() * TreeType.TEXTURE_BYTES);
        System.out.println("without the pattern  = " + (long) forest.size() * TreeType.TEXTURE_BYTES);

        System.out.println();
        System.out.println(forest.get(0).draw());
        System.out.println(forest.get(1).draw());
        System.out.println("first two oaks share one TreeType = "
                + (forest.get(0).type() == forest.get(3).type()));
    }
}

// The flyweight. Immutable, because every holder shares this exact instance.
final class TreeType {
    static final int TEXTURE_BYTES = 2_000_000;

    private final String name;
    private final byte[] texture;

    TreeType(String name) {
        this.name = name;
        this.texture = new byte[0]; // pretend this is TEXTURE_BYTES of pixels
    }

    String draw(int x, int y) {
        return name + " drawn at (" + x + ", " + y + ")";
    }
}

final class TreeFactory {
    private static final Map<String, TreeType> POOL = new HashMap<>();

    // Callers never construct a TreeType directly. That would defeat the sharing.
    static TreeType get(String name) {
        return POOL.computeIfAbsent(name, TreeType::new);
    }

    static int poolSize() {
        return POOL.size();
    }
}

record Tree(int x, int y, TreeType type) {
    String draw() {
        return type.draw(x, y);
    }
}
