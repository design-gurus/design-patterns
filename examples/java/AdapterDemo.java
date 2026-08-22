// Adapter: make an incompatible interface fit the one your code expects.
//
// Checkout talks to PaymentPort. The vendor client speaks cents, currency
// codes, and its own exception type. The adapter is the only place that knows.
//
// Run: java AdapterDemo.java

public class AdapterDemo {
    public static void main(String[] args) {
        // Checkout never learns which vendor is behind the port.
        Checkout checkout = new Checkout(new StripeAdapter(new StripeClient()));
        System.out.println(checkout.buy(49.99, "USD"));

        try {
            checkout.buy(-1, "USD");
        } catch (PaymentFailed e) {
            // Our exception type, not the vendor's. That is the point.
            System.out.println("handled our own error type: " + e.getMessage());
        }
    }
}

record Money(double amount, String currency) {
    int cents() {
        return (int) Math.round(amount * 100);
    }
}

record Receipt(String id, Money paid) {}

class PaymentFailed extends RuntimeException {
    PaymentFailed(String message, Throwable cause) {
        super(message, cause);
    }
}

interface PaymentPort {
    Receipt pay(Money amount);
}

// The vendor library. You cannot change any of this.
class StripeClient {
    StripeCharge charge(int amountInCents, String currencyCode) {
        if (amountInCents <= 0) throw new StripeException("amount_invalid");
        return new StripeCharge("ch_" + amountInCents + currencyCode.toLowerCase());
    }
}

class StripeCharge {
    private final String id;
    StripeCharge(String id) { this.id = id; }
    String getId() { return id; }
}

class StripeException extends RuntimeException {
    StripeException(String message) { super(message); }
}

// The adapter. Our interface out, vendor types in, and nothing leaks.
class StripeAdapter implements PaymentPort {
    private final StripeClient client;

    StripeAdapter(StripeClient client) {
        this.client = client;
    }

    @Override
    public Receipt pay(Money amount) {
        try {
            StripeCharge charge = client.charge(amount.cents(), amount.currency());
            return new Receipt(charge.getId(), amount);
        } catch (StripeException e) {
            throw new PaymentFailed("payment declined: " + e.getMessage(), e);
        }
    }
}

class Checkout {
    private final PaymentPort payments;

    Checkout(PaymentPort payments) {
        this.payments = payments;
    }

    String buy(double amount, String currency) {
        Receipt r = payments.pay(new Money(amount, currency));
        return "paid " + amount + " " + currency + ", receipt " + r.id();
    }
}
