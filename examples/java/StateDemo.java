// State: let an object change its behavior as its internal state changes.
//
// Without it: pay(), ship(), and cancel() each start with the same switch on a
// status field, and adding a state means finding every one of them.
//
// Run: java StateDemo.java

public class StateDemo {
    public static void main(String[] args) {
        Order happy = new Order("A-1");
        happy.pay();
        happy.ship();
        happy.deliver();

        System.out.println();
        Order refunded = new Order("A-2");
        refunded.pay();
        refunded.cancel();      // legal while paid, and it triggers a refund

        System.out.println();
        Order tooLate = new Order("A-3");
        tooLate.pay();
        tooLate.ship();
        tooLate.cancel();       // not legal once shipped
    }
}

// Default methods reject every action. A state overrides only what it allows,
// so illegal transitions fail the same way everywhere without any duplication.
interface OrderState {
    String name();

    default void pay(Order o) { o.reject("pay", name()); }
    default void ship(Order o) { o.reject("ship", name()); }
    default void deliver(Order o) { o.reject("deliver", name()); }
    default void cancel(Order o) { o.reject("cancel", name()); }
}

class DraftState implements OrderState {
    public String name() { return "draft"; }
    public void pay(Order o) { o.moveTo(new PaidState()); }
    public void cancel(Order o) { o.moveTo(new CancelledState()); }
}

class PaidState implements OrderState {
    public String name() { return "paid"; }
    public void ship(Order o) { o.moveTo(new ShippedState()); }
    public void cancel(Order o) {
        o.refund();
        o.moveTo(new CancelledState());
    }
}

class ShippedState implements OrderState {
    public String name() { return "shipped"; }
    public void deliver(Order o) { o.moveTo(new DeliveredState()); }
}

class DeliveredState implements OrderState {
    public String name() { return "delivered"; }
}

class CancelledState implements OrderState {
    public String name() { return "cancelled"; }
}

class Order {
    private final String id;
    private OrderState state = new DraftState();

    Order(String id) {
        this.id = id;
        System.out.println(id + ": created as " + state.name());
    }

    // Every public method forwards. There is no switch anywhere in this class.
    void pay() { state.pay(this); }
    void ship() { state.ship(this); }
    void deliver() { state.deliver(this); }
    void cancel() { state.cancel(this); }

    void moveTo(OrderState next) {
        System.out.println(id + ": " + state.name() + " -> " + next.name());
        this.state = next;
    }

    void refund() {
        System.out.println(id + ": refunding the payment");
    }

    void reject(String action, String stateName) {
        System.out.println(id + ": cannot " + action + " while " + stateName);
    }
}
