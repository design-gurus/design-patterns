// Template Method: the base class fixes the order of steps, subclasses fill in
// the ones that differ.
//
// Note the three kinds of step: shared (write), abstract (read, transform), and
// hook (beforeWrite, which subclasses may ignore).
//
// Run: java TemplateMethodDemo.java

import java.util.ArrayList;
import java.util.List;

public class TemplateMethodDemo {
    public static void main(String[] args) {
        new CsvImporter().run("orders.csv");
        System.out.println();
        new JsonImporter().run("orders.json");
    }
}

abstract class DataImporter {
    // The template. Final, so the order is not negotiable.
    public final void run(String source) {
        System.out.println("importing " + source);
        List<String> rows = read(source);

        List<String> valid = new ArrayList<>();
        for (String row : rows) {
            if (isValid(row)) valid.add(row);
        }
        System.out.println("  kept " + valid.size() + " of " + rows.size() + " rows");

        List<String> records = new ArrayList<>();
        for (String row : valid) records.add(transform(row));

        beforeWrite(records);   // hook
        write(records);
    }

    // Steps a subclass must supply.
    protected abstract List<String> read(String source);
    protected abstract String transform(String row);

    // A default a subclass may override.
    protected boolean isValid(String row) {
        return !row.isBlank();
    }

    // A hook. Empty on purpose, so most subclasses can ignore it.
    protected void beforeWrite(List<String> records) {
    }

    // A shared step. Written once, fixed once.
    private void write(List<String> records) {
        System.out.println("  wrote " + records.size() + " records to the database");
    }
}

class CsvImporter extends DataImporter {
    protected List<String> read(String source) {
        System.out.println("  reading CSV");
        return List.of("1,book,12.00", "", "2,pen,1.50");
    }

    protected String transform(String row) {
        String[] parts = row.split(",");
        return "Record(id=" + parts[0] + ", name=" + parts[1] + ")";
    }
}

class JsonImporter extends DataImporter {
    protected List<String> read(String source) {
        System.out.println("  reading JSON");
        return List.of("{\"id\":1,\"name\":\"book\"}", "{\"id\":2,\"name\":\"pen\"}");
    }

    protected String transform(String row) {
        return "Record(from json: " + row + ")";
    }

    // This importer is the only one that needs the optional step.
    @Override
    protected void beforeWrite(List<String> records) {
        System.out.println("  hook: sorting records before write");
    }
}
