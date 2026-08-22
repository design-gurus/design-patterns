// Factory Method: the base class defines the flow, a subclass decides which
// concrete object gets created.
//
// Without it: planDelivery() contains "if road ... else if sea ...", and every
// new transport means editing a method that already works.
//
// Run: java FactoryMethodDemo.java

public class FactoryMethodDemo {
    public static void main(String[] args) {
        Logistics[] offices = { new RoadLogistics(), new SeaLogistics(), new AirLogistics() };

        for (Logistics office : offices) {
            System.out.println(office.planDelivery("Berlin"));
        }
    }
}

abstract class Logistics {
    // The shared algorithm. Note that it never names a concrete Transport.
    public String planDelivery(String destination) {
        Transport transport = createTransport();
        String route = "plan route to " + destination;
        return route + " -> " + transport.deliver(destination);
    }

    // The hook. Protected, because it exists for subclasses, not for callers.
    protected abstract Transport createTransport();
}

class RoadLogistics extends Logistics {
    @Override
    protected Transport createTransport() {
        return new Truck();
    }
}

class SeaLogistics extends Logistics {
    @Override
    protected Transport createTransport() {
        return new Ship();
    }
}

class AirLogistics extends Logistics {
    @Override
    protected Transport createTransport() {
        return new Plane();
    }
}

interface Transport {
    String deliver(String destination);
}

class Truck implements Transport {
    public String deliver(String destination) {
        return "truck arrives in " + destination + " in 3 days";
    }
}

class Ship implements Transport {
    public String deliver(String destination) {
        return "ship arrives in " + destination + " in 21 days";
    }
}

class Plane implements Transport {
    public String deliver(String destination) {
        return "plane arrives in " + destination + " in 1 day";
    }
}
