"""Flyweight: share the state that repeats, so a huge number of objects fits.

This file measures real memory with sys.getsizeof, so the saving is a number
you can see rather than a claim.

Run: python flyweight.py
"""

import sys

TREE_COUNT = 200_000
TEXTURE_BYTES = 2_000_000


class TreeType:
    """The flyweight. Immutable, because every holder shares this instance."""

    __slots__ = ("name", "texture")

    def __init__(self, name: str):
        self.name = name
        self.texture = b""  # pretend this is TEXTURE_BYTES of pixels

    def draw(self, x: int, y: int) -> str:
        return f"{self.name} drawn at ({x}, {y})"


class TreeFactory:
    _pool: dict[str, TreeType] = {}

    @classmethod
    def get(cls, name: str) -> TreeType:
        # Callers never construct a TreeType directly. That would defeat sharing.
        if name not in cls._pool:
            cls._pool[name] = TreeType(name)
        return cls._pool[name]

    @classmethod
    def pool_size(cls) -> int:
        return len(cls._pool)


class Tree:
    """Extrinsic state stays here, one per tree."""

    __slots__ = ("x", "y", "type")

    def __init__(self, x: int, y: int, tree_type: TreeType):
        self.x, self.y, self.type = x, y, tree_type

    def draw(self) -> str:
        return self.type.draw(self.x, self.y)


def main() -> None:
    species = ("oak", "pine", "birch")
    forest = [Tree(i % 5000, i // 5000, TreeFactory.get(species[i % 3])) for i in range(TREE_COUNT)]

    print("trees created        =", len(forest))
    print("TreeType objects     =", TreeFactory.pool_size())
    print("texture bytes stored =", TreeFactory.pool_size() * TEXTURE_BYTES)
    print("without the pattern  =", len(forest) * TEXTURE_BYTES)

    print()
    print(forest[0].draw())
    print("two oaks share one TreeType =", forest[0].type is forest[3].type)

    # Python interns short strings for the same reason: identical immutable
    # values do not need separate storage.
    print("string interning is a flyweight:", sys.intern("oak") is sys.intern("oak"))


if __name__ == "__main__":
    main()
