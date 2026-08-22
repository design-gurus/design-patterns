"""Observer: notify many dependents when one object changes.

The order no longer imports email, inventory, or analytics. It knows one
callback shape. The two failures that matter (a throwing observer, and never
unsubscribing) are both demonstrated.

Run: python observer.py
"""

from typing import Callable

OrderObserver = Callable[["Order"], None]


class Order:
    def __init__(self, order_id: str):
        self.id = order_id
        self._observers: list[OrderObserver] = []

    def subscribe(self, fn: OrderObserver) -> Callable[[], None]:
        """Returns an unsubscribe function. Handing the caller the only thing
        needed to stop is the shape that prevents most listener leaks."""
        self._observers.append(fn)

        def unsubscribe() -> None:
            if fn in self._observers:
                self._observers.remove(fn)

        return unsubscribe

    def mark_paid(self) -> None:
        print(f"{self.id}: paid")
        # Iterate a copy, in case a handler subscribes or unsubscribes.
        for fn in list(self._observers):
            try:
                fn(self)
            except Exception as e:
                # One failing observer must not stop the others.
                print(f"  observer failed: {e}")


def main() -> None:
    order = Order("A-1")

    order.subscribe(lambda o: print(f"  email: receipt for {o.id}"))

    def broken_analytics(_: Order) -> None:
        raise RuntimeError("analytics is down")

    order.subscribe(broken_analytics)
    stop_warehouse = order.subscribe(lambda o: print(f"  warehouse: pick items for {o.id}"))

    print("--- paying, with three observers ---")
    order.mark_paid()

    print()
    print("--- after unsubscribing the warehouse ---")
    stop_warehouse()
    order.mark_paid()


if __name__ == "__main__":
    main()
