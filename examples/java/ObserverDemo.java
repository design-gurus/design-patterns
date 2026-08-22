// Observer: notify many dependents when one object changes.
//
// The order no longer imports email, inventory, or analytics. It knows one
// interface. The two failures that matter (a throwing observer, and never
// unsubscribing) are both demonstrated.
//
// Run: java ObserverDemo.java

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ObserverDemo {
    public static void main(String[] args) {
        Order order = new Order("A-1");

        order.subscribe(o -> System.out.println("  email: receipt for " + o.id()));
        order.subscribe(o -> { throw new RuntimeException("analytics is down"); });
        OrderObserver warehouse = o -> System.out.println("  warehouse: pick items for " + o.id());
        order.subscribe(warehouse);

        System.out.println("--- paying, with three observers ---");
        order.markPaid();

        System.out.println();
        System.out.println("--- after unsubscribing the warehouse ---");
        order.unsubscribe(warehouse);
        order.markPaid();
    }
}

interface OrderObserver {
    void onPaid(Order order);
}

class Order {
    private final String id;
    // Copy on write, so an observer that unsubscribes itself during the loop
    // does not throw a concurrent modification error.
    private final List<OrderObserver> observers = new CopyOnWriteArrayList<>();

    Order(String id) {
        this.id = id;
    }

    String id() {
        return id;
    }

    void subscribe(OrderObserver o) {
        observers.add(o);
    }

    void unsubscribe(OrderObserver o) {
        observers.remove(o);
    }

    void markPaid() {
        System.out.println(id + ": paid");
        for (OrderObserver o : observers) {
            try {
                o.onPaid(this);
            } catch (RuntimeException e) {
                // One failing observer must not stop the others.
                System.out.println("  observer failed: " + e.getMessage());
            }
        }
    }
}
