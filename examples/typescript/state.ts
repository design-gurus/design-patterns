/**
 * State: let an object change its behavior as its internal state changes.
 *
 * Without it: pay(), ship(), and cancel() each start with the same switch on a
 * status field, and adding a state means finding every one of them.
 *
 * Run: npx tsx state.ts
 */

abstract class OrderState {
  abstract readonly name: string;

  // Base rejects every action. A state overrides only what it allows.
  pay(order: Order): void { order.reject("pay", this.name); }
  ship(order: Order): void { order.reject("ship", this.name); }
  deliver(order: Order): void { order.reject("deliver", this.name); }
  cancel(order: Order): void { order.reject("cancel", this.name); }
}

class DraftState extends OrderState {
  readonly name = "draft";
  override pay(order: Order): void { order.moveTo(new PaidState()); }
  override cancel(order: Order): void { order.moveTo(new CancelledState()); }
}

class PaidState extends OrderState {
  readonly name = "paid";
  override ship(order: Order): void { order.moveTo(new ShippedState()); }
  override cancel(order: Order): void {
    order.refund();
    order.moveTo(new CancelledState());
  }
}

class ShippedState extends OrderState {
  readonly name = "shipped";
  override deliver(order: Order): void { order.moveTo(new DeliveredState()); }
}

class DeliveredState extends OrderState {
  readonly name = "delivered";
}

class CancelledState extends OrderState {
  readonly name = "cancelled";
}

class Order {
  private state: OrderState = new DraftState();

  constructor(private readonly id: string) {
    console.log(`${id}: created as ${this.state.name}`);
  }

  // Every public method forwards. There is no switch anywhere in this class.
  pay(): void { this.state.pay(this); }
  ship(): void { this.state.ship(this); }
  deliver(): void { this.state.deliver(this); }
  cancel(): void { this.state.cancel(this); }

  moveTo(next: OrderState): void {
    console.log(`${this.id}: ${this.state.name} -> ${next.name}`);
    this.state = next;
  }

  refund(): void {
    console.log(`${this.id}: refunding the payment`);
  }

  reject(action: string, stateName: string): void {
    console.log(`${this.id}: cannot ${action} while ${stateName}`);
  }
}

const happy = new Order("A-1");
happy.pay();
happy.ship();
happy.deliver();

console.log();
const refunded = new Order("A-2");
refunded.pay();
refunded.cancel();

console.log();
const tooLate = new Order("A-3");
tooLate.pay();
tooLate.ship();
tooLate.cancel();
