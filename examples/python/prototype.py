"""Prototype: create a new object by copying a configured one.

The whole difficulty is copy depth. This file shows both, side by side, so the
failure mode is visible rather than described.

Run: python prototype.py
"""

import copy


class Circle:
    def __init__(self, radius: int, tags: list[str]):
        self.radius = radius
        self.tags = tags

    def deep_copy(self) -> "Circle":
        return copy.deepcopy(self)

    def broken_shallow_copy(self) -> "Circle":
        return copy.copy(self)  # shares self.tags with the original, which is the bug

    def __str__(self) -> str:
        return f"circle r={self.radius} {self.tags}"


def main() -> None:
    template = Circle(10, ["draft"])

    good = template.deep_copy()
    good.tags.append("reviewed")
    print("original tags after deep copy edit =", template.tags)
    print("copy tags                          =", good.tags)

    print()
    print("shallow copy shares the list, so editing the copy changed the original:")
    bad = template.broken_shallow_copy()
    bad.tags.append("oops")
    print("original tags =", template.tags)
    print("copy tags     =", bad.tags)


if __name__ == "__main__":
    main()
