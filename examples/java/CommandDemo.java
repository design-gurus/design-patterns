// Command: turn a request into an object, so it can be queued, logged, or undone.
//
// Each command records what it needs to reverse itself during execute(). That
// is the part people skip, and it is what makes undo correct.
//
// Run: java CommandDemo.java

import java.util.ArrayDeque;
import java.util.Deque;

public class CommandDemo {
    public static void main(String[] args) {
        Document doc = new Document();
        Editor editor = new Editor();

        editor.run(new AppendText(doc, "Hello"));
        editor.run(new AppendText(doc, ", world"));
        editor.run(new UpperCaseAll(doc));

        System.out.println("after 3 commands : " + doc);
        System.out.println("history          : " + editor.history());

        editor.undoLast();
        System.out.println("after 1 undo     : " + doc);

        editor.undoLast();
        editor.undoLast();
        System.out.println("after 3 undos    : \"" + doc + "\"");
    }
}

class Document {
    private StringBuilder text = new StringBuilder();

    int length() { return text.length(); }

    void append(String s) { text.append(s); }

    void deleteFrom(int position, int count) { text.delete(position, position + count); }

    String snapshot() { return text.toString(); }

    void replaceAll(String s) { text = new StringBuilder(s); }

    @Override
    public String toString() { return text.toString(); }
}

interface Command {
    String name();
    void execute();
    void undo();
}

class AppendText implements Command {
    private final Document doc;
    private final String text;
    private int position;   // recorded during execute, needed by undo

    AppendText(Document doc, String text) {
        this.doc = doc;
        this.text = text;
    }

    public String name() { return "append(\"" + text + "\")"; }

    public void execute() {
        position = doc.length();
        doc.append(text);
    }

    public void undo() {
        doc.deleteFrom(position, text.length());
    }
}

// Some operations have no cheap inverse. Those record the previous value
// instead, which is the Memento pattern doing the work inside a Command.
class UpperCaseAll implements Command {
    private final Document doc;
    private String before;

    UpperCaseAll(Document doc) {
        this.doc = doc;
    }

    public String name() { return "upperCaseAll()"; }

    public void execute() {
        before = doc.snapshot();
        doc.replaceAll(before.toUpperCase());
    }

    public void undo() {
        doc.replaceAll(before);
    }
}

class Editor {
    private final Deque<Command> history = new ArrayDeque<>();

    void run(Command c) {
        c.execute();
        history.push(c);
    }

    void undoLast() {
        if (history.isEmpty()) {
            System.out.println("nothing to undo");
            return;
        }
        history.pop().undo();
    }

    String history() {
        return history.stream().map(Command::name).toList().toString();
    }
}
