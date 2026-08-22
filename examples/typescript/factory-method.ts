/**
 * Factory Method: the base class defines the flow, a subclass decides which
 * concrete object gets created.
 *
 * Without it: planDelivery() contains "if road ... else if sea ...", and every
 * new transport means editing a method that already works.
 *
 * Run: npx tsx factory-method.ts
 */

interface Transport {
  deliver(destination: string): string;
}

class Truck implements Transport {
  deliver(destination: string): string {
    return `truck arrives in ${destination} in 3 days`;
  }
}

class Ship implements Transport {
  deliver(destination: string): string {
    return `ship arrives in ${destination} in 21 days`;
  }
}

class Plane implements Transport {
  deliver(destination: string): string {
    return `plane arrives in ${destination} in 1 day`;
  }
}

abstract class Logistics {
  // The shared algorithm. It never names a concrete Transport.
  planDelivery(destination: string): string {
    const transport = this.createTransport();
    return `plan route to ${destination} -> ${transport.deliver(destination)}`;
  }

  protected abstract createTransport(): Transport;
}

class RoadLogistics extends Logistics {
  protected createTransport(): Transport {
    return new Truck();
  }
}

class SeaLogistics extends Logistics {
  protected createTransport(): Transport {
    return new Ship();
  }
}

/**
 * The version you would actually write in TypeScript: pass the factory in as a
 * function. No subclass, and a test can supply an arrow function.
 */
class ConfigurableLogistics extends Logistics {
  constructor(private readonly factory: () => Transport) {
    super();
  }
  protected createTransport(): Transport {
    return this.factory();
  }
}

const offices: Logistics[] = [
  new RoadLogistics(),
  new SeaLogistics(),
  new ConfigurableLogistics(() => new Plane()),
];

for (const office of offices) {
  console.log(office.planDelivery("Berlin"));
}
