"""Decorator: add behavior to one object at runtime, in stackable layers.

Watch the order. Retry outside cache retries a cache lookup. Cache outside
retry caches the result of a retried call. Those are different systems.

Run: python decorator.py
"""

from typing import Callable, Protocol


class DataSource(Protocol):
    def read(self, key: str) -> str: ...


class FlakyDataSource:
    """Fails the first time it sees any key."""

    def __init__(self) -> None:
        self._attempts: dict[str, int] = {}

    def read(self, key: str) -> str:
        n = self._attempts.get(key, 0) + 1
        self._attempts[key] = n
        print(f"    source: attempt {n} for {key}")
        if n == 1:
            raise RuntimeError("transient failure")
        return f"row({key})"


class LoggingSource:
    def __init__(self, inner: DataSource):
        self._inner = inner

    def read(self, key: str) -> str:
        print(f"  logging: read {key}")
        value = self._inner.read(key)
        print(f"  logging: done {key}")
        return value


class CachingSource:
    def __init__(self, inner: DataSource):
        self._inner = inner
        self._cache: dict[str, str] = {}

    def read(self, key: str) -> str:
        if key in self._cache:
            print(f"  caching: hit {key}")
            return self._cache[key]
        print(f"  caching: miss {key}")
        value = self._inner.read(key)
        self._cache[key] = value
        return value


class RetryingSource:
    def __init__(self, inner: DataSource, max_attempts: int = 3):
        self._inner = inner
        self._max_attempts = max_attempts

    def read(self, key: str) -> str:
        last: Exception | None = None
        for attempt in range(1, self._max_attempts + 1):
            try:
                return self._inner.read(key)
            except RuntimeError as e:
                last = e
                print(f"  retrying: attempt {attempt} failed")
        raise last  # type: ignore[misc]


def with_timing(fn: Callable[[str], str]) -> Callable[[str], str]:
    """Python's own @decorator syntax is the function-level version of this
    pattern. For a one-method object it removes the classes entirely."""

    def wrapper(key: str) -> str:
        print(f"  timing: start {key}")
        return fn(key)

    return wrapper


def main() -> None:
    wrapped = LoggingSource(CachingSource(RetryingSource(FlakyDataSource())))

    print("--- first read (miss, may retry) ---")
    print("value =", wrapped.read("user:1"))

    print()
    print("--- second read (cache hit, no retry needed) ---")
    print("value =", wrapped.read("user:1"))

    print()
    print("--- function-level decorator ---")
    print("value =", with_timing(wrapped.read)("user:1"))


if __name__ == "__main__":
    main()
