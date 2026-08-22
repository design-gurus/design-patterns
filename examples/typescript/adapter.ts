/**
 * Adapter: make an incompatible interface fit the one your code expects.
 *
 * Checkout talks to PaymentPort. The vendor client speaks cents, currency
 * codes, and its own error type. The adapter is the only place that knows.
 *
 * Run: npx tsx adapter.ts
 */

interface Money {
  amount: number;
  currency: string;
}

interface Receipt {
  id: string;
  paid: Money;
}

class PaymentFailed extends Error {}

interface PaymentPort {
  pay(amount: Money): Receipt;
}

// The vendor library. You cannot change any of this.
class StripeError extends Error {}

class StripeClient {
  charge(amountInCents: number, currencyCode: string): string {
    if (amountInCents <= 0) throw new StripeError("amount_invalid");
    return `ch_${amountInCents}${currencyCode.toLowerCase()}`;
  }
}

// The adapter. Our types out, vendor types in, and nothing leaks.
class StripeAdapter implements PaymentPort {
  constructor(private readonly client: StripeClient) {}

  pay(amount: Money): Receipt {
    try {
      const id = this.client.charge(Math.round(amount.amount * 100), amount.currency);
      return { id, paid: amount };
    } catch (e) {
      throw new PaymentFailed(`payment declined: ${(e as Error).message}`);
    }
  }
}

class Checkout {
  constructor(private readonly payments: PaymentPort) {}

  buy(amount: number, currency: string): string {
    const receipt = this.payments.pay({ amount, currency });
    return `paid ${amount} ${currency}, receipt ${receipt.id}`;
  }
}

const checkout = new Checkout(new StripeAdapter(new StripeClient()));
console.log(checkout.buy(49.99, "USD"));

try {
  checkout.buy(-1, "USD");
} catch (e) {
  // Our error type, not the vendor's. That is the point.
  console.log("handled our own error type:", (e as Error).message);
}
