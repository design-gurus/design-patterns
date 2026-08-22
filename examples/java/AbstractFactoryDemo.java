// Abstract Factory: create families of related objects that must match.
//
// Without it: a caller builds a MacButton and a WindowsCheckbox, which compiles
// fine and ships as a bug report with a screenshot attached.
//
// Run: java AbstractFactoryDemo.java

public class AbstractFactoryDemo {
    public static void main(String[] args) {
        // The choice is made once, at the composition root.
        String os = args.length > 0 ? args[0] : "mac";
        UIFactory factory = os.equals("windows") ? new WindowsFactory() : new MacFactory();

        SettingsScreen screen = new SettingsScreen(factory);
        screen.render();

        // A mismatched pair cannot be built: the screen never names a concrete class.
    }
}

interface Button {
    String paint();
}

interface Checkbox {
    String paint();
}

interface UIFactory {
    Button createButton();
    Checkbox createCheckbox();
}

class MacFactory implements UIFactory {
    public Button createButton() { return new MacButton(); }
    public Checkbox createCheckbox() { return new MacCheckbox(); }
}

class WindowsFactory implements UIFactory {
    public Button createButton() { return new WindowsButton(); }
    public Checkbox createCheckbox() { return new WindowsCheckbox(); }
}

class MacButton implements Button {
    public String paint() { return "[ rounded button ]"; }
}

class MacCheckbox implements Checkbox {
    public String paint() { return "( o ) rounded checkbox"; }
}

class WindowsButton implements Button {
    public String paint() { return "[= square button =]"; }
}

class WindowsCheckbox implements Checkbox {
    public String paint() { return "[x] square checkbox"; }
}

class SettingsScreen {
    private final Button button;
    private final Checkbox checkbox;

    SettingsScreen(UIFactory factory) {
        this.button = factory.createButton();
        this.checkbox = factory.createCheckbox();
    }

    void render() {
        System.out.println(button.paint());
        System.out.println(checkbox.paint());
    }
}
