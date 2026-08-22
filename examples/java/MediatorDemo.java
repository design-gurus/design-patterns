// Mediator: route component-to-component talk through one coordinator.
//
// Five components would otherwise hold references to each other. Here each one
// knows only the mediator, and every interaction rule lives in one method.
//
// Run: java MediatorDemo.java

import java.util.List;

public class MediatorDemo {
    public static void main(String[] args) {
        BookingForm form = new BookingForm();

        System.out.println("--- user picks a date ---");
        form.date.set("2026-09-14");

        System.out.println();
        System.out.println("--- user picks a room type ---");
        form.roomType.set("suite");

        System.out.println();
        System.out.println("--- user clears the date ---");
        form.date.set("");
    }
}

interface Mediator {
    void changed(Component source);
}

abstract class Component {
    protected final Mediator mediator;
    private String value = "";

    protected Component(Mediator mediator) {
        this.mediator = mediator;
    }

    String value() {
        return value;
    }

    void set(String v) {
        this.value = v;
        mediator.changed(this);   // tell the mediator, never another component
    }
}

class TextField extends Component {
    TextField(Mediator m) { super(m); }
}

class Dropdown extends Component {
    private List<String> options = List.of();

    Dropdown(Mediator m) { super(m); }

    void setOptions(List<String> options) {
        this.options = options;
        System.out.println("  room type options -> " + options);
    }

    List<String> options() { return options; }
}

class Label extends Component {
    Label(Mediator m) { super(m); }

    void setText(String text) {
        System.out.println("  price label -> " + text);
    }
}

class Button extends Component {
    Button(Mediator m) { super(m); }

    void setEnabled(boolean enabled) {
        System.out.println("  submit -> " + (enabled ? "enabled" : "disabled"));
    }
}

class BookingForm implements Mediator {
    final TextField date = new TextField(this);
    final Dropdown roomType = new Dropdown(this);
    final Label price = new Label(this);
    final Button submit = new Button(this);

    // Every interaction rule, in one readable place.
    @Override
    public void changed(Component source) {
        if (source == date) {
            roomType.setOptions(date.value().isEmpty() ? List.of() : List.of("single", "double", "suite"));
        }
        if (source == date || source == roomType) {
            boolean ready = !date.value().isEmpty() && !roomType.value().isEmpty();
            price.setText(ready ? quote(roomType.value()) : "select a date and room");
        }
        submit.setEnabled(!date.value().isEmpty() && !roomType.value().isEmpty());
    }

    private String quote(String room) {
        return switch (room) {
            case "suite" -> "320.00 EUR";
            case "double" -> "180.00 EUR";
            default -> "120.00 EUR";
        };
    }
}
