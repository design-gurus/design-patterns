"""Factory Method: the base class defines the flow, a subclass decides which
concrete object gets created.

Without it: plan_delivery() contains "if road ... elif sea ...", and every new
transport means editing a method that already works.

Run: python factory_method.py
"""

from abc import ABC, abstractmethod
from typing import Callable, Protocol


class Transport(Protocol):
    def deliver(self, destination: str) -> str: ...


class Truck:
    def deliver(self, destination: str) -> str:
        return f"truck arrives in {destination} in 3 days"


class Ship:
    def deliver(self, destination: str) -> str:
        return f"ship arrives in {destination} in 21 days"


class Plane:
    def deliver(self, destination: str) -> str:
        return f"plane arrives in {destination} in 1 day"


class Logistics(ABC):
    def plan_delivery(self, destination: str) -> str:
        transport = self.create_transport()
        return f"plan route to {destination} -> {transport.deliver(destination)}"

    @abstractmethod
    def create_transport(self) -> Transport: ...


class RoadLogistics(Logistics):
    def create_transport(self) -> Transport:
        return Truck()


class SeaLogistics(Logistics):
    def create_transport(self) -> Transport:
        return Ship()


class FunctionLogistics(Logistics):
    """The version you would actually write in Python.

    A factory function passed in beats a subclass per product. It composes,
    it needs no new class, and a test can pass a lambda.
    """

    def __init__(self, factory: Callable[[], Transport]):
        self._factory = factory

    def create_transport(self) -> Transport:
        return self._factory()


def main() -> None:
    for office in (RoadLogistics(), SeaLogistics(), FunctionLogistics(Plane)):
        print(office.plan_delivery("Berlin"))


if __name__ == "__main__":
    main()
