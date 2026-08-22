"""Proxy: stand in for another object, with the same interface, to control
access, cost, or timing.

Three of the four classic kinds are here: virtual (lazy), protection, and
caching. The client code is identical in every case.

Run: python proxy.py
"""

from typing import Protocol


class VideoService(Protocol):
    def fetch(self, video_id: str) -> str: ...


class RealVideoService:
    def __init__(self) -> None:
        print("  building the real service (expensive)")

    def fetch(self, video_id: str) -> str:
        print(f"  real service: fetching {video_id}")
        return f"video[{video_id}]"


class LazyVideoService:
    """Virtual proxy. The expensive object is created on first real use."""

    def __init__(self) -> None:
        self._real: RealVideoService | None = None

    def fetch(self, video_id: str) -> str:
        if self._real is None:
            self._real = RealVideoService()
        return self._real.fetch(video_id)


class CachingProxy:
    """Caching proxy. A real one needs a size bound and an expiry policy."""

    def __init__(self, real: VideoService):
        self._real = real
        self._cache: dict[str, str] = {}

    def fetch(self, video_id: str) -> str:
        if video_id in self._cache:
            print(f"  proxy: cache hit for {video_id}")
            return self._cache[video_id]
        value = self._real.fetch(video_id)
        self._cache[video_id] = value
        return value


class ProtectionProxy:
    """Protection proxy. The rule lives outside the object it protects."""

    def __init__(self, real: VideoService, role: str):
        self._real = real
        self._role = role

    def fetch(self, video_id: str) -> str:
        if self._role != "member":
            raise PermissionError(f"{self._role} cannot fetch videos")
        return self._real.fetch(video_id)


def main() -> None:
    print("--- virtual proxy: the heavy object is never built until needed ---")
    lazy = LazyVideoService()
    print("proxy created, nothing loaded yet")
    print(lazy.fetch("v1"))

    print()
    print("--- caching proxy: the second call skips the real service ---")
    cached = CachingProxy(RealVideoService())
    print(cached.fetch("v2"))
    print(cached.fetch("v2"))

    print()
    print("--- protection proxy ---")
    guarded = ProtectionProxy(RealVideoService(), "guest")
    try:
        guarded.fetch("v3")
    except PermissionError as e:
        print("blocked:", e)


if __name__ == "__main__":
    main()
