"""Mediator: route component-to-component talk through one coordinator.

Four components would otherwise hold references to each other. Here each one
knows only the mediator, and every interaction rule lives in one method.

Run: python mediator.py
"""

from typing import Protocol


class Mediator(Protocol):
    def changed(self, source: "Component") -> None: ...


class Component:
    def __init__(self, mediator: Mediator):
        self._mediator = mediator
        self.value = ""

    def set(self, value: str) -> None:
        self.value = value
        self._mediator.changed(self)  # tell the mediator, never another component


class TextField(Component):
    pass


class Dropdown(Component):
    def __init__(self, mediator: Mediator):
        super().__init__(mediator)
        self.options: list[str] = []

    def set_options(self, options: list[str]) -> None:
        self.options = options
        print(f"  room type options -> {options}")


class Label(Component):
    def set_text(self, text: str) -> None:
        print(f"  price label -> {text}")


class Button(Component):
    def set_enabled(self, enabled: bool) -> None:
        print(f"  submit -> {'enabled' if enabled else 'disabled'}")


class BookingForm:
    def __init__(self) -> None:
        self.date = TextField(self)
        self.room_type = Dropdown(self)
        self.price = Label(self)
        self.submit = Button(self)

    # Every interaction rule, in one readable place.
    def changed(self, source: Component) -> None:
        if source is self.date:
            self.room_type.set_options(["single", "double", "suite"] if self.date.value else [])

        if source in (self.date, self.room_type):
            ready = bool(self.date.value and self.room_type.value)
            self.price.set_text(self._quote(self.room_type.value) if ready else "select a date and room")

        self.submit.set_enabled(bool(self.date.value and self.room_type.value))

    @staticmethod
    def _quote(room: str) -> str:
        return {"suite": "320.00 EUR", "double": "180.00 EUR"}.get(room, "120.00 EUR")


def main() -> None:
    form = BookingForm()

    print("--- user picks a date ---")
    form.date.set("2026-09-14")

    print()
    print("--- user picks a room type ---")
    form.room_type.set("suite")

    print()
    print("--- user clears the date ---")
    form.date.set("")


if __name__ == "__main__":
    main()
