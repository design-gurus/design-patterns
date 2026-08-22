"""Builder: assemble a complex object step by step, and validate it once at the end.

Without it: HttpRequest(url, "GET", 30, 3, True, False) and nobody can read the
call site or tell which boolean is which.

Note what this file shows: Python has keyword arguments with defaults, so the
readability problem is already solved. The builder still earns its place here
for the cross-field validation in build().

Run: python builder.py
"""

from dataclasses import dataclass


@dataclass(frozen=True)
class HttpRequest:
    url: str
    method: str = "GET"
    body: str | None = None
    headers: tuple[str, ...] = ()
    timeout_seconds: int = 30
    retries: int = 0

    def __str__(self) -> str:
        return (
            f"{self.method} {self.url} headers={list(self.headers)} "
            f"timeout={self.timeout_seconds}s retries={self.retries}"
        )


class HttpRequestBuilder:
    def __init__(self, url: str):
        if not url:
            raise ValueError("url is required")
        self._url = url
        self._method = "GET"
        self._body: str | None = None
        self._headers: list[str] = []
        self._timeout = 30
        self._retries = 0

    def method(self, m: str) -> "HttpRequestBuilder":
        self._method = m
        return self

    def body(self, b: str) -> "HttpRequestBuilder":
        self._body = b
        return self

    def header(self, h: str) -> "HttpRequestBuilder":
        self._headers.append(h)
        return self

    def timeout(self, seconds: int) -> "HttpRequestBuilder":
        self._timeout = seconds
        return self

    def retries(self, n: int) -> "HttpRequestBuilder":
        self._retries = n
        return self

    def build(self) -> HttpRequest:
        # One place for every rule that spans more than one field.
        if self._timeout <= 0:
            raise ValueError("timeout must be positive")
        if self._retries < 0:
            raise ValueError("retries cannot be negative")
        if self._body is not None and self._method == "GET":
            raise ValueError("a GET request cannot carry a body")
        return HttpRequest(
            url=self._url,
            method=self._method,
            body=self._body,
            headers=tuple(self._headers),  # copy, so later builder calls cannot mutate it
            timeout_seconds=self._timeout,
            retries=self._retries,
        )


def main() -> None:
    request = (
        HttpRequestBuilder("https://api.example.com/orders")
        .method("POST")
        .header("content-type: application/json")
        .timeout(5)
        .retries(2)
        .build()
    )
    print(request)

    for build in (
        lambda: HttpRequestBuilder("https://api.example.com").timeout(-1).build(),
        lambda: HttpRequestBuilder("https://api.example.com").body("{}").build(),
    ):
        try:
            build()
        except ValueError as e:
            print("rejected:", e)

    # And the version you would usually write in Python, with no builder at all.
    print(HttpRequest("https://api.example.com/ping", method="HEAD", timeout_seconds=2))


if __name__ == "__main__":
    main()
