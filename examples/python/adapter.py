"""Adapter: make an incompatible interface fit the one your code expects.

Checkout talks to PaymentPort. The vendor client speaks cents, currency codes,
and its own exception type. The adapter is the only place that knows.

Run: python adapter.py
"""

from dataclasses import dataclass
from typing import Protocol


@dataclass(frozen=True)
class Money:
    amount: float
    currency: str

    @property
    def cents(self) -> int:
        return round(self.amount * 100)


@dataclass(frozen=True)
class Receipt:
    id: str
    paid: Money


class PaymentFailed(Exception):
    pass


class PaymentPort(Protocol):
    def pay(self, amount: Money) -> Receipt: ...


# The vendor library. You cannot change any of this.
class StripeError(Exception):
    pass


class StripeClient:
    def charge(self, amount_in_cents: int, currency_code: str) -> str:
        if amount_in_cents <= 0:
            raise StripeError("amount_invalid")
        return f"ch_{amount_in_cents}{currency_code.lower()}"


# The adapter. Our types out, vendor types in, and nothing leaks.
class StripeAdapter:
    def __init__(self, client: StripeClient):
        self._client = client

    def pay(self, amount: Money) -> Receipt:
        try:
            charge_id = self._client.charge(amount.cents, amount.currency)
        except StripeError as e:
            raise PaymentFailed(f"payment declined: {e}") from e
        return Receipt(charge_id, amount)


class Checkout:
    def __init__(self, payments: PaymentPort):
        self._payments = payments

    def buy(self, amount: float, currency: str) -> str:
        receipt = self._payments.pay(Money(amount, currency))
        return f"paid {amount} {currency}, receipt {receipt.id}"


def main() -> None:
    checkout = Checkout(StripeAdapter(StripeClient()))
    print(checkout.buy(49.99, "USD"))

    try:
        checkout.buy(-1, "USD")
    except PaymentFailed as e:
        # Our exception type, not the vendor's. That is the point.
        print("handled our own error type:", e)


if __name__ == "__main__":
    main()
