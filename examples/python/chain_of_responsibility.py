"""Chain of Responsibility: pass a request along handlers until one stops it.

This is the pipeline variant, which is what web middleware uses: every handler
runs in order, and any of them may end the request.

Run: python chain_of_responsibility.py
"""

from abc import ABC, abstractmethod
from dataclasses import dataclass, field


@dataclass
class Request:
    user: str
    token: str
    body: str
    stopped: bool = field(default=False)

    def reject(self, status: int, reason: str) -> None:
        self.stopped = True
        print(f"  rejected {status}: {reason}")


class Handler(ABC):
    def __init__(self) -> None:
        self._next: "Handler | None" = None

    def set_next(self, handler: "Handler") -> "Handler":
        self._next = handler
        return handler  # returns the new tail, so wiring reads in order

    def handle(self, request: Request) -> None:
        if self.process(request) and self._next is not None:
            self._next.handle(request)

    @abstractmethod
    def process(self, request: Request) -> bool:
        """Return False to stop the chain here."""


class AuthHandler(Handler):
    def process(self, request: Request) -> bool:
        print(f"  auth: checking {request.user}")
        if not request.token:
            request.reject(401, "missing token")
            return False
        return True


class RateLimitHandler(Handler):
    def __init__(self, allowance: int):
        super().__init__()
        self._allowance = allowance
        self._used: dict[str, int] = {}

    def process(self, request: Request) -> bool:
        count = self._used.get(request.user, 0) + 1
        self._used[request.user] = count
        print(f"  rate limit: {request.user} has used {count} of {self._allowance}")
        if count > self._allowance:
            request.reject(429, "slow down")
            return False
        return True


class ValidationHandler(Handler):
    def process(self, request: Request) -> bool:
        print("  validation: checking body")
        if not request.body:
            request.reject(400, "body is required")
            return False
        return True


class BusinessHandler(Handler):
    """The terminal handler. Reaching the end with nothing done should never be
    silent, so there is always something at the tail."""

    def process(self, request: Request) -> bool:
        print(f"  handled: created order for {request.user}")
        return True


def main() -> None:
    chain = AuthHandler()
    chain.set_next(RateLimitHandler(2)).set_next(ValidationHandler()).set_next(BusinessHandler())

    print("--- valid request ---")
    chain.handle(Request("alice", "token-ok", '{"item":"book"}'))

    print()
    print("--- no token: stopped at the first handler ---")
    chain.handle(Request("bob", "", '{"item":"book"}'))

    print()
    print("--- empty body: stopped at validation ---")
    chain.handle(Request("alice", "token-ok", ""))

    print()
    print("--- rate limit: alice has now used her allowance ---")
    chain.handle(Request("alice", "token-ok", '{"item":"pen"}'))


if __name__ == "__main__":
    main()
