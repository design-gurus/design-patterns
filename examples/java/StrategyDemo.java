// Strategy: swap an algorithm at runtime behind one interface.
//
// Without it: quote() is a growing if/else on carrier name, and adding a
// carrier means editing a method that already works.
//
// Run: java StrategyDemo.java

import java.util.LinkedHashMap;
import java.util.Map;

public class StrategyDemo {
    public static void main(String[] args) {
        Parcel parcel = new Parcel(2.4, "DE");

        Map<String, RateStrategy> carriers = new LinkedHashMap<>();
        carriers.put("fedex", new FedExRate());
        carriers.put("ups", new UpsRate());
        carriers.put("local", new LocalCourierRate());
        // A strategy can be a lambda, because the interface has one method.
        carriers.put("flat", p -> 9.99);

        for (Map.Entry<String, RateStrategy> e : carriers.entrySet()) {
            ShippingCalculator calc = new ShippingCalculator(e.getValue());
            System.out.printf("%-6s %.2f%n", e.getKey(), calc.quote(parcel));
        }
    }
}

record Parcel(double kilos, String country) {}

interface RateStrategy {
    double rate(Parcel parcel);
}

class FedExRate implements RateStrategy {
    public double rate(Parcel p) {
        return 4.99 + 0.50 * p.kilos();
    }
}

class UpsRate implements RateStrategy {
    public double rate(Parcel p) {
        double base = 5.49 + 0.35 * p.kilos();
        return p.country().equals("DE") ? base : base + 3.00;
    }
}

class LocalCourierRate implements RateStrategy {
    public double rate(Parcel p) {
        return p.kilos() <= 5 ? 6.00 : 6.00 + 1.20 * (p.kilos() - 5);
    }
}

class ShippingCalculator {
    private final RateStrategy strategy;

    ShippingCalculator(RateStrategy strategy) {
        this.strategy = strategy;
    }

    // No branching left here, and none is added when a carrier is.
    double quote(Parcel parcel) {
        return strategy.rate(parcel);
    }
}
