"""Iterator: walk a structure without exposing how it stores things.

One tree, two traversal orders. In Python you almost never write the classic
iterator class: a generator is the same pattern with the state machine written
for you.

Run: python iterator.py
"""

from collections import deque
from typing import Iterator


class TreeNode:
    def __init__(self, name: str, *children: "TreeNode"):
        self.name = name
        self.children = list(children)


class Tree:
    def __init__(self, root: TreeNode):
        self.root = root

    def __iter__(self) -> Iterator[str]:
        """Depth first. The yield turns this function into an iterator."""
        stack = [self.root]
        while stack:
            node = stack.pop()
            stack.extend(reversed(node.children))
            yield node.name

    def breadth_first(self) -> Iterator[str]:
        """A second traversal over the same data, named and reusable."""
        queue = deque([self.root])
        while queue:
            node = queue.popleft()
            queue.extend(node.children)
            yield node.name


def main() -> None:
    root = TreeNode(
        "a",
        TreeNode("b", TreeNode("d"), TreeNode("e")),
        TreeNode("c", TreeNode("f")),
    )
    tree = Tree(root)

    print("depth first  :", " ".join(tree))
    print("breadth first:", " ".join(tree.breadth_first()))

    # Two traversals run at once, because position lives in the iterator.
    a, b = iter(tree), iter(tree)
    next(a)
    print("independent positions:", next(a), "and", next(b))

    # The trap worth knowing: an iterator is consumed once.
    once = iter(tree)
    list(once)
    print("second pass over a used iterator:", list(once))


if __name__ == "__main__":
    main()
