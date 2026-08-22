// Prototype: create a new object by copying a configured one.
//
// The whole difficulty is copy depth. This file shows both, side by side, so
// the failure mode is visible rather than described.
//
// Run: java PrototypeDemo.java

import java.util.ArrayList;
import java.util.List;

public class PrototypeDemo {
    public static void main(String[] args) {
        Shape template = new Circle(10, new ArrayList<>(List.of("draft")));

        Shape deep = template.copy();
        deep.tags().add("reviewed");

        System.out.println("original tags after deep copy edit = " + template.tags());
        System.out.println("copy tags                          = " + deep.tags());

        // Now the mistake, so you can see what it looks like.
        Shape shallow = ((Circle) template).brokenShallowCopy();
        shallow.tags().add("oops");
        System.out.println();
        System.out.println("shallow copy shares the list, so editing the copy changed the original:");
        System.out.println("original tags = " + template.tags());
        System.out.println("copy tags     = " + shallow.tags());
    }
}

interface Shape {
    Shape copy();          // the caller never names a concrete type
    List<String> tags();
    String describe();
}

final class Circle implements Shape {
    private final int radius;
    private final List<String> tags;

    Circle(int radius, List<String> tags) {
        this.radius = radius;
        this.tags = tags;
    }

    // A copy constructor is the clear way to do this in Java.
    // Avoid Cloneable: it skips constructors and its default is shallow.
    @Override
    public Shape copy() {
        return new Circle(this.radius, new ArrayList<>(this.tags)); // the list is copied too
    }

    Shape brokenShallowCopy() {
        return new Circle(this.radius, this.tags); // shares the list, which is the bug
    }

    @Override
    public List<String> tags() {
        return tags;
    }

    @Override
    public String describe() {
        return "circle r=" + radius + " " + tags;
    }
}
