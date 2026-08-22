/**
 * Template Method: the base class fixes the order of steps, subclasses fill in
 * the ones that differ.
 *
 * The class version is the classic pattern. The function version at the end is
 * what you would usually write in TypeScript: same guarantee, no inheritance.
 *
 * Run: npx tsx template-method.ts
 */

abstract class DataImporter {
  // The template. The order of these steps is the guarantee.
  run(source: string): void {
    console.log(`importing ${source}`);
    const rows = this.read(source);

    const valid = rows.filter((r) => this.isValid(r));
    console.log(`  kept ${valid.length} of ${rows.length} rows`);

    const records = valid.map((r) => this.transform(r));

    this.beforeWrite(records); // hook
    this.write(records);
  }

  // Steps a subclass must supply.
  protected abstract read(source: string): string[];
  protected abstract transform(row: string): string;

  // A default a subclass may override.
  protected isValid(row: string): boolean {
    return row.trim().length > 0;
  }

  // A hook. Empty on purpose, so most subclasses can ignore it.
  protected beforeWrite(_records: string[]): void {}

  // A shared step. Written once, fixed once.
  private write(records: string[]): void {
    console.log(`  wrote ${records.length} records to the database`);
  }
}

class CsvImporter extends DataImporter {
  protected read(_source: string): string[] {
    console.log("  reading CSV");
    return ["1,book,12.00", "", "2,pen,1.50"];
  }
  protected transform(row: string): string {
    const [id, name] = row.split(",");
    return `Record(id=${id}, name=${name})`;
  }
}

class JsonImporter extends DataImporter {
  protected read(_source: string): string[] {
    console.log("  reading JSON");
    return ['{"id":1,"name":"book"}', '{"id":2,"name":"pen"}'];
  }
  protected transform(row: string): string {
    return `Record(from json: ${row})`;
  }
  // This importer is the only one that needs the optional step.
  protected override beforeWrite(_records: string[]): void {
    console.log("  hook: sorting records before write");
  }
}

new CsvImporter().run("orders.csv");
console.log();
new JsonImporter().run("orders.json");

// The function version: the flow is still fixed, the varying steps are passed in.
interface ImportSteps {
  read(source: string): string[];
  transform(row: string): string;
  beforeWrite?(records: string[]): void;
}

function runImport(source: string, steps: ImportSteps): void {
  console.log(`importing ${source}`);
  const records = steps
    .read(source)
    .filter((r) => r.trim().length > 0)
    .map((r) => steps.transform(r));
  steps.beforeWrite?.(records);
  console.log(`  wrote ${records.length} records to the database`);
}

console.log();
runImport("orders.tsv", {
  read: () => ["1\tbook", "2\tpen"],
  transform: (row) => `Record(tsv: ${row.replace("\t", " ")})`,
});
