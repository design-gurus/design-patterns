"""Strategy: swap an algorithm at runtime behind one interface.

Without it: quote() is a growing if/elif on carrier name, and adding a carrier
means editing a method that already works.

In Python a one-method strategy is just a function. Both forms are here so the
difference is visible.

Run: python strategy.py
"""

from dataclasses import dataclass
from typing import Callable


@dataclass(frozen=True)
class Parcel:
    kilos: float
    country: str


RateStrategy = Callable[[Parcel], float]


def fedex_rate(parcel: Parcel) -> float:
    return 4.99 + 0.50 * parcel.kilos


def ups_rate(parcel: Parcel) -> float:
    base = 5.49 + 0.35 * parcel.kilos
    return base if parcel.country == "DE" else base + 3.00


def local_courier_rate(parcel: Parcel) -> float:
    return 6.00 if parcel.kilos <= 5 else 6.00 + 1.20 * (parcel.kilos - 5)


class ShippingCalculator:
    def __init__(self, rate: RateStrategy):
        self._rate = rate

    def quote(self, parcel: Parcel) -> float:
        # No branching left here, and none is added when a carrier is.
        return self._rate(parcel)


def main() -> None:
    parcel = Parcel(2.4, "DE")

    carriers: dict[str, RateStrategy] = {
        "fedex": fedex_rate,
        "ups": ups_rate,
        "local": local_courier_rate,
        "flat": lambda _: 9.99,
    }

    for name, rate in carriers.items():
        print(f"{name:<6} {ShippingCalculator(rate).quote(parcel):.2f}")


if __name__ == "__main__":
    main()
