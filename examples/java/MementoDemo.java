// Memento: capture state so it can be restored, without exposing internals.
//
// History stores mementos and cannot read them. That is the whole point: the
// caretaker manages history without knowing what state looks like.
//
// Run: java MementoDemo.java

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class MementoDemo {
    public static void main(String[] args) {
        Editor editor = new Editor();
        History history = new History(3);   // bounded, so history is not a leak

        history.backup(editor);
        editor.type("Hello");

        history.backup(editor);
        editor.type(", world");
        editor.addTag("draft");

        history.backup(editor);
        editor.type("!");

        System.out.println("now        : " + editor);
        history.undo(editor);
        System.out.println("after undo : " + editor);
        history.undo(editor);
        System.out.println("after undo : " + editor);
        history.undo(editor);
        System.out.println("after undo : " + editor);
        history.undo(editor);
    }
}

class Editor {
    private String content = "";
    private int cursor = 0;
    private List<String> tags = new ArrayList<>();

    void type(String text) {
        content += text;
        cursor = content.length();
    }

    void addTag(String tag) {
        tags.add(tag);
    }

    Memento save() {
        // The list is copied. A shallow snapshot would share it with the live
        // object, and restoring would put back the current state.
        return new Memento(content, cursor, new ArrayList<>(tags));
    }

    void restore(Memento m) {
        this.content = m.content;
        this.cursor = m.cursor;
        this.tags = new ArrayList<>(m.tags);
    }

    @Override
    public String toString() {
        return "\"" + content + "\" cursor=" + cursor + " tags=" + tags;
    }

    // Nested and private-field only: nothing outside Editor can read this.
    static final class Memento {
        private final String content;
        private final int cursor;
        private final List<String> tags;

        private Memento(String content, int cursor, List<String> tags) {
            this.content = content;
            this.cursor = cursor;
            this.tags = tags;
        }
    }
}

// The caretaker. It holds mementos and cannot look inside any of them.
class History {
    private final Deque<Editor.Memento> stack = new ArrayDeque<>();
    private final int maxDepth;

    History(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    void backup(Editor editor) {
        if (stack.size() == maxDepth) stack.removeLast();  // drop the oldest
        stack.push(editor.save());
    }

    void undo(Editor editor) {
        if (stack.isEmpty()) {
            System.out.println("nothing left to undo");
            return;
        }
        editor.restore(stack.pop());
    }
}
