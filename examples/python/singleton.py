"""Singleton: one shared instance, with one place that owns its lifecycle.

Without it: every caller builds its own ConfigStore, so the file is read many
times and nothing agrees on the values.

Run: python singleton.py
"""


class ConfigStore:
    _instance = None

    def __new__(cls) -> "ConfigStore":
        if cls._instance is None:
            print("reading config from disk (this line must print once)")
            cls._instance = super().__new__(cls)
            cls._instance.values = {"region": "us-east-1", "tier": "standard"}
        return cls._instance

    def get(self, key: str) -> str:
        return self.values[key]


# The idiomatic Python version. A module runs once, so anything importing this
# module gets the same object. No __new__ trickery needed at all.
config = ConfigStore()


def main() -> None:
    a = ConfigStore()
    b = ConfigStore()

    print("region      =", a.get("region"))
    print("same object =", a is b)
    print("module-level object is the same one =", a is config)


if __name__ == "__main__":
    main()
