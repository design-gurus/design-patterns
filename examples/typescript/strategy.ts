/**
 * Strategy: swap an algorithm at runtime behind one interface.
 *
 * Without it: quote() is a growing if/else on carrier name, and adding a
 * carrier means editing a method that already works.
 *
 * In TypeScript a one-method strategy is just a function type.
 *
 * Run: npx tsx strategy.ts
 */

interface Parcel {
  kilos: number;
  country: string;
}

type RateStrategy = (parcel: Parcel) => number;

const fedExRate: RateStrategy = (p) => 4.99 + 0.5 * p.kilos;

const upsRate: RateStrategy = (p) => {
  const base = 5.49 + 0.35 * p.kilos;
  return p.country === "DE" ? base : base + 3.0;
};

const localCourierRate: RateStrategy = (p) => (p.kilos <= 5 ? 6.0 : 6.0 + 1.2 * (p.kilos - 5));

class ShippingCalculator {
  constructor(private readonly rate: RateStrategy) {}

  // No branching left here, and none is added when a carrier is.
  quote(parcel: Parcel): number {
    return this.rate(parcel);
  }
}

const parcel: Parcel = { kilos: 2.4, country: "DE" };

const carriers: Record<string, RateStrategy> = {
  fedex: fedExRate,
  ups: upsRate,
  local: localCourierRate,
  flat: () => 9.99,
};

for (const [name, rate] of Object.entries(carriers)) {
  console.log(name.padEnd(6), new ShippingCalculator(rate).quote(parcel).toFixed(2));
}
