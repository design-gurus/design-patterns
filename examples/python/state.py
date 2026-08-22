"""State: let an object change its behavior as its internal state changes.

Without it: pay(), ship(), and cancel() each start with the same if/elif on a
status field, and adding a state means finding every one of them.

Run: python state.py
"""


class OrderState:
    """Base rejects every action. A state overrides only what it allows, so
    illegal transitions fail the same way everywhere with no duplication."""

    name = "unknown"

    def pay(self, order: "Order") -> None:
        order.reject("pay", self.name)

    def ship(self, order: "Order") -> None:
        order.reject("ship", self.name)

    def deliver(self, order: "Order") -> None:
        order.reject("deliver", self.name)

    def cancel(self, order: "Order") -> None:
        order.reject("cancel", self.name)


class DraftState(OrderState):
    name = "draft"

    def pay(self, order: "Order") -> None:
        order.move_to(PaidState())

    def cancel(self, order: "Order") -> None:
        order.move_to(CancelledState())


class PaidState(OrderState):
    name = "paid"

    def ship(self, order: "Order") -> None:
        order.move_to(ShippedState())

    def cancel(self, order: "Order") -> None:
        order.refund()
        order.move_to(CancelledState())


class ShippedState(OrderState):
    name = "shipped"

    def deliver(self, order: "Order") -> None:
        order.move_to(DeliveredState())


class DeliveredState(OrderState):
    name = "delivered"


class CancelledState(OrderState):
    name = "cancelled"


class Order:
    def __init__(self, order_id: str):
        self.id = order_id
        self.state: OrderState = DraftState()
        print(f"{self.id}: created as {self.state.name}")

    # Every public method forwards. There is no if/elif anywhere in this class.
    def pay(self) -> None:
        self.state.pay(self)

    def ship(self) -> None:
        self.state.ship(self)

    def deliver(self) -> None:
        self.state.deliver(self)

    def cancel(self) -> None:
        self.state.cancel(self)

    def move_to(self, next_state: OrderState) -> None:
        print(f"{self.id}: {self.state.name} -> {next_state.name}")
        self.state = next_state

    def refund(self) -> None:
        print(f"{self.id}: refunding the payment")

    def reject(self, action: str, state_name: str) -> None:
        print(f"{self.id}: cannot {action} while {state_name}")


def main() -> None:
    happy = Order("A-1")
    happy.pay()
    happy.ship()
    happy.deliver()

    print()
    refunded = Order("A-2")
    refunded.pay()
    refunded.cancel()

    print()
    too_late = Order("A-3")
    too_late.pay()
    too_late.ship()
    too_late.cancel()


if __name__ == "__main__":
    main()
