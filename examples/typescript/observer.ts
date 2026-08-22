/**
 * Observer: notify many dependents when one object changes.
 *
 * The order no longer imports email, inventory, or analytics. It knows one
 * callback shape. The two failures that matter (a throwing observer, and never
 * unsubscribing) are both demonstrated.
 *
 * Run: npx tsx observer.ts
 */

type OrderObserver = (order: Order) => void;

class Order {
  private observers: OrderObserver[] = [];

  constructor(readonly id: string) {}

  /**
   * Returns an unsubscribe function. Handing the caller the only thing needed
   * to stop is the shape that prevents most listener leaks.
   */
  subscribe(fn: OrderObserver): () => void {
    this.observers.push(fn);
    return () => {
      this.observers = this.observers.filter((o) => o !== fn);
    };
  }

  markPaid(): void {
    console.log(`${this.id}: paid`);
    // Iterate a copy, in case a handler subscribes or unsubscribes.
    for (const fn of [...this.observers]) {
      try {
        fn(this);
      } catch (e) {
        // One failing observer must not stop the others.
        console.log(`  observer failed: ${(e as Error).message}`);
      }
    }
  }
}

const order = new Order("A-1");

order.subscribe((o) => console.log(`  email: receipt for ${o.id}`));
order.subscribe(() => {
  throw new Error("analytics is down");
});
const stopWarehouse = order.subscribe((o) => console.log(`  warehouse: pick items for ${o.id}`));

console.log("--- paying, with three observers ---");
order.markPaid();

console.log();
console.log("--- after unsubscribing the warehouse ---");
stopWarehouse();
order.markPaid();
