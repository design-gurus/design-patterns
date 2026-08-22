// Iterator: walk a structure without exposing how it stores things.
//
// One tree, two traversal orders. The caller writes the same loop for both and
// never learns that a stack or a queue is involved.
//
// Run: java IteratorDemo.java

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class IteratorDemo {
    public static void main(String[] args) {
        TreeNode root = new TreeNode("a",
                new TreeNode("b", new TreeNode("d"), new TreeNode("e")),
                new TreeNode("c", new TreeNode("f")));

        Tree tree = new Tree(root);

        System.out.print("depth first  : ");
        for (String name : tree) System.out.print(name + " ");
        System.out.println();

        System.out.print("breadth first: ");
        for (String name : tree.breadthFirst()) System.out.print(name + " ");
        System.out.println();

        // Two traversals can run at once, because position lives in the
        // iterator and not in the tree.
        Iterator<String> a = tree.iterator();
        Iterator<String> b = tree.iterator();
        a.next();
        System.out.println("independent positions: " + a.next() + " and " + b.next());
    }
}

class TreeNode {
    final String name;
    final List<TreeNode> children = new ArrayList<>();

    TreeNode(String name, TreeNode... children) {
        this.name = name;
        this.children.addAll(List.of(children));
    }
}

class Tree implements Iterable<String> {
    private final TreeNode root;

    Tree(TreeNode root) {
        this.root = root;
    }

    @Override
    public Iterator<String> iterator() {
        return new Iterator<>() {
            private final Deque<TreeNode> stack = new ArrayDeque<>(List.of(root));

            public boolean hasNext() {
                return !stack.isEmpty();
            }

            public String next() {
                if (stack.isEmpty()) throw new NoSuchElementException();
                TreeNode node = stack.pop();
                for (int i = node.children.size() - 1; i >= 0; i--) {
                    stack.push(node.children.get(i));
                }
                return node.name;
            }
        };
    }

    Iterable<String> breadthFirst() {
        return () -> new Iterator<>() {
            private final Deque<TreeNode> queue = new ArrayDeque<>(List.of(root));

            public boolean hasNext() {
                return !queue.isEmpty();
            }

            public String next() {
                if (queue.isEmpty()) throw new NoSuchElementException();
                TreeNode node = queue.poll();
                queue.addAll(node.children);
                return node.name;
            }
        };
    }
}
