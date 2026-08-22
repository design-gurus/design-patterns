"""Template Method: the base class fixes the order of steps, subclasses fill in
the ones that differ.

Note the three kinds of step: shared (write), abstract (read, transform), and
hook (before_write, which subclasses may ignore).

Run: python template_method.py
"""

from abc import ABC, abstractmethod


class DataImporter(ABC):
    def run(self, source: str) -> None:
        """The template. The order of these steps is the guarantee."""
        print(f"importing {source}")
        rows = self.read(source)

        valid = [row for row in rows if self.is_valid(row)]
        print(f"  kept {len(valid)} of {len(rows)} rows")

        records = [self.transform(row) for row in valid]

        self.before_write(records)  # hook
        self._write(records)

    # Steps a subclass must supply.
    @abstractmethod
    def read(self, source: str) -> list[str]: ...

    @abstractmethod
    def transform(self, row: str) -> str: ...

    # A default a subclass may override.
    def is_valid(self, row: str) -> bool:
        return bool(row.strip())

    # A hook. Empty on purpose, so most subclasses can ignore it.
    def before_write(self, records: list[str]) -> None:
        pass

    # A shared step. Written once, fixed once.
    def _write(self, records: list[str]) -> None:
        print(f"  wrote {len(records)} records to the database")


class CsvImporter(DataImporter):
    def read(self, source: str) -> list[str]:
        print("  reading CSV")
        return ["1,book,12.00", "", "2,pen,1.50"]

    def transform(self, row: str) -> str:
        parts = row.split(",")
        return f"Record(id={parts[0]}, name={parts[1]})"


class JsonImporter(DataImporter):
    def read(self, source: str) -> list[str]:
        print("  reading JSON")
        return ['{"id":1,"name":"book"}', '{"id":2,"name":"pen"}']

    def transform(self, row: str) -> str:
        return f"Record(from json: {row})"

    # This importer is the only one that needs the optional step.
    def before_write(self, records: list[str]) -> None:
        print("  hook: sorting records before write")


def main() -> None:
    CsvImporter().run("orders.csv")
    print()
    JsonImporter().run("orders.json")


if __name__ == "__main__":
    main()
