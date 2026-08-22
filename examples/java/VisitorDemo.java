// Visitor: add an operation to a class hierarchy without editing its classes.
//
// Three node types, three operations, and the node classes contain nothing but
// accept(). Adding a fourth operation adds one file and edits none.
//
// Run: java VisitorDemo.java

import java.util.List;

public class VisitorDemo {
    public static void main(String[] args) {
        List<DocNode> document = List.of(
                new Paragraph("Design patterns are named solutions to recurring problems."),
                new ImageNode("diagram.png", "a class diagram"),
                new CodeBlock("java", "int x = 1;\nint y = 2;"),
                new Paragraph("Each one has a cost."));

        WordCounter words = new WordCounter();
        HtmlExporter html = new HtmlExporter();
        PlainTextExporter text = new PlainTextExporter();

        for (DocNode node : document) {
            node.accept(words);
            node.accept(html);
            node.accept(text);
        }

        System.out.println("word count = " + words.total);
        System.out.println();
        System.out.println("--- html ---");
        System.out.println(html.output());
        System.out.println("--- plain text ---");
        System.out.println(text.output());
    }
}

interface Visitor {
    void visitParagraph(Paragraph p);
    void visitImage(ImageNode i);
    void visitCode(CodeBlock c);
}

interface DocNode {
    void accept(Visitor v);
}

// Each node class has exactly one line of dispatch and nothing else.
record Paragraph(String text) implements DocNode {
    public void accept(Visitor v) { v.visitParagraph(this); }
}

record ImageNode(String src, String alt) implements DocNode {
    public void accept(Visitor v) { v.visitImage(this); }
}

record CodeBlock(String language, String source) implements DocNode {
    public void accept(Visitor v) { v.visitCode(this); }
}

// One operation, one file. This is the payoff.
class WordCounter implements Visitor {
    int total = 0;

    public void visitParagraph(Paragraph p) { total += p.text().split("\\s+").length; }
    public void visitImage(ImageNode i) { /* images have no words */ }
    public void visitCode(CodeBlock c) { /* code is not prose */ }
}

class HtmlExporter implements Visitor {
    private final StringBuilder sb = new StringBuilder();

    public void visitParagraph(Paragraph p) { sb.append("<p>").append(p.text()).append("</p>\n"); }

    public void visitImage(ImageNode i) {
        sb.append("<img src=\"").append(i.src()).append("\" alt=\"").append(i.alt()).append("\">\n");
    }

    public void visitCode(CodeBlock c) {
        sb.append("<pre lang=\"").append(c.language()).append("\">").append(c.source()).append("</pre>\n");
    }

    String output() { return sb.toString(); }
}

class PlainTextExporter implements Visitor {
    private final StringBuilder sb = new StringBuilder();

    public void visitParagraph(Paragraph p) { sb.append(p.text()).append("\n"); }
    public void visitImage(ImageNode i) { sb.append("[image: ").append(i.alt()).append("]\n"); }
    public void visitCode(CodeBlock c) { sb.append(c.source().indent(4)); }

    String output() { return sb.toString(); }
}
