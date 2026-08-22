"""Abstract Factory: create families of related objects that must match.

Without it: a caller builds a MacButton and a WindowsCheckbox, which runs fine
and ships as a bug report with a screenshot attached.

Run: python abstract_factory.py
"""

from typing import Protocol


class Widget(Protocol):
    def paint(self) -> str: ...


class MacButton:
    def paint(self) -> str:
        return "[ rounded button ]"


class MacCheckbox:
    def paint(self) -> str:
        return "( o ) rounded checkbox"


class WindowsButton:
    def paint(self) -> str:
        return "[= square button =]"


class WindowsCheckbox:
    def paint(self) -> str:
        return "[x] square checkbox"


class UIFactory(Protocol):
    def create_button(self) -> Widget: ...
    def create_checkbox(self) -> Widget: ...


class MacFactory:
    def create_button(self) -> Widget:
        return MacButton()

    def create_checkbox(self) -> Widget:
        return MacCheckbox()


class WindowsFactory:
    def create_button(self) -> Widget:
        return WindowsButton()

    def create_checkbox(self) -> Widget:
        return WindowsCheckbox()


class SettingsScreen:
    def __init__(self, factory: UIFactory):
        # The screen never names a concrete widget, so a mismatched pair is
        # not expressible here.
        self.button = factory.create_button()
        self.checkbox = factory.create_checkbox()

    def render(self) -> None:
        print(self.button.paint())
        print(self.checkbox.paint())


def main() -> None:
    for name, factory in (("mac", MacFactory()), ("windows", WindowsFactory())):
        print(f"--- {name} ---")
        SettingsScreen(factory).render()


if __name__ == "__main__":
    main()
