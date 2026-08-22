// Composite: treat a single object and a tree of objects the same way.
//
// size() is written once. It does not care whether it is holding a file or a
// folder of folders, and no caller writes an instanceof check.
//
// Run: java CompositeDemo.java

import java.util.ArrayList;
import java.util.List;

public class CompositeDemo {
    public static void main(String[] args) {
        Node tree = new FolderNode("project")
                .add(new FileNode("README.md", 2_048))
                .add(new FolderNode("src")
                        .add(new FileNode("main.java", 12_000))
                        .add(new FolderNode("util")
                                .add(new FileNode("strings.java", 3_500))
                                .add(new FileNode("dates.java", 4_100))))
                .add(new FolderNode("docs")
                        .add(new FileNode("guide.md", 8_800)));

        System.out.println(tree.print(0));
        System.out.println("total bytes = " + tree.size());
        System.out.println("file count  = " + tree.count());

        // A single file answers exactly the same questions.
        Node one = new FileNode("notes.txt", 120);
        System.out.println("one file    = " + one.size() + " bytes, " + one.count() + " file");
    }
}

interface Node {
    long size();
    int count();
    String print(int depth);
}

final class FileNode implements Node {
    private final String name;
    private final long bytes;

    FileNode(String name, long bytes) {
        this.name = name;
        this.bytes = bytes;
    }

    public long size() { return bytes; }

    public int count() { return 1; }

    public String print(int depth) {
        return "  ".repeat(depth) + name + " (" + bytes + ")";
    }
}

final class FolderNode implements Node {
    private final String name;
    private final List<Node> children = new ArrayList<>();

    FolderNode(String name) {
        this.name = name;
    }

    // add() lives only on the composite, so a file never gets a method that throws.
    FolderNode add(Node child) {
        children.add(child);
        return this;
    }

    public long size() {
        long total = 0;
        for (Node c : children) total += c.size(); // works for files and folders alike
        return total;
    }

    public int count() {
        int total = 0;
        for (Node c : children) total += c.count();
        return total;
    }

    public String print(int depth) {
        StringBuilder sb = new StringBuilder("  ".repeat(depth) + name + "/");
        for (Node c : children) sb.append("\n").append(c.print(depth + 1));
        return sb.toString();
    }
}
