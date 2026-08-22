# Design Patterns

> Patterns at two altitudes: the **22 Gang of Four patterns** that shape your code, and the **60+ system design patterns** that shape your architecture. The free, open companion to the pattern courses by [DesignGurus.io](https://www.designgurus.io?utm_source=github&utm_medium=repo&utm_campaign=design-patterns), created by Arslan Ahmad.

[![GitHub stars](https://img.shields.io/github/stars/design-gurus/design-patterns?style=social)](https://github.com/design-gurus/design-patterns/stargazers)
[![Last commit](https://img.shields.io/github/last-commit/design-gurus/design-patterns)](https://github.com/design-gurus/design-patterns/commits/main)
[![License: CC BY 4.0](https://img.shields.io/badge/License-CC%20BY%204.0-lightgrey.svg)](LICENSE)
[![PRs welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](CONTRIBUTING.md)

Most pattern references pick one altitude. A Gang of Four repo teaches you Decorator and stops. A distributed systems repo teaches you sharding and never explains why your service layer is unreadable. Real engineering work moves between the two every day, so this repo covers both, and says plainly where each one stops being the right answer.

## Contents

- [Which altitude do you need?](#which-altitude-do-you-need)
- [Is there a design patterns PDF or book?](#is-there-a-design-patterns-pdf-or-book)
- [How to use this repo](#how-to-use-this-repo)
- [Code patterns: the Gang of Four](#code-patterns-the-gang-of-four)
- [System patterns](#system-patterns)
- [What each page gives you](#what-each-page-gives-you)
- [Also in this repo](#also-in-this-repo)
- [Related repository](#related-repository)
- [Contributing](#contributing)

## Which altitude do you need?

| If the question is | You want | Start at |
|---|---|---|
| "This class is doing too much and I cannot test it" | Code patterns | [code-patterns/](code-patterns/) |
| "How do I add a payment provider without touching the checkout flow?" | Code patterns | [code-patterns/](code-patterns/) |
| "The database is the bottleneck at 10x traffic" | System patterns | [system-patterns/](system-patterns/) |
| "One slow downstream service is taking the whole site down" | System patterns | [system-patterns/](system-patterns/) |
| "I am in an object-oriented design interview" | Code patterns, then the interview notes | [code-patterns/](code-patterns/) |
| "I am in a system design interview" | System patterns, plus the interview-focused repo | [grokking-system-design](https://github.com/design-gurus/grokking-system-design) |

A short rule: code patterns change how one service is written, system patterns change how many services behave together.

## Is there a design patterns PDF or book?

The original Gang of Four book is *Design Patterns: Elements of Reusable Object-Oriented Software* (1994). It is still worth owning, and it is not what most people are searching for when they ask this. What they usually want is a current, readable reference with modern code and honest trade-offs.

That is what this repository is, and it is free in your browser with no account. There is no official PDF of the DesignGurus courses. The PDFs that circulate are unofficial copies of old versions, missing every fix made since. The current courses are online:

- [Grokking Design Patterns for Engineers and Managers](https://www.designgurus.io/course/grokking-design-patterns-for-engineers-and-managers?utm_source=github&utm_medium=repo&utm_campaign=design-patterns) for the code-level patterns.
- [System Design Patterns](https://www.designgurus.io/course/system-design-patterns?utm_source=github&utm_medium=repo&utm_campaign=design-patterns) for the system-level patterns.

## How to use this repo

1. Read the classification note in [code-patterns/](code-patterns/) so the 22 patterns stop looking like a flat list of 22 names.
2. Learn a pattern from its page, then read the "when not to use it" section. That section is the one that changes how you write code.
3. Run the matching example in `examples/` in whichever language you think in.
4. When you move up an altitude, read [system-patterns/](system-patterns/) the same way: mechanism first, then failure modes.
5. Go deeper in the full courses when you want lessons, diagrams, and worked designs.

## Code patterns: the Gang of Four

Twenty-two patterns in three families. The families matter: they tell you which shelf to look on before you know the pattern's name.

### Creational: how objects get made

| Pattern | What it solves |
|---|---|
| Singleton | One shared instance, with one place that owns its lifecycle |
| Factory Method | Let a subclass decide which concrete type to create |
| Abstract Factory | Create whole families of related objects that must match |
| Builder | Assemble a complex object step by step, without a 12-argument constructor |
| Prototype | Create a new object by copying a configured one |

### Structural: how objects are put together

| Pattern | What it solves |
|---|---|
| Adapter | Make an incompatible interface fit the one your code already expects |
| Bridge | Vary an abstraction and its implementation independently |
| Composite | Treat a single object and a tree of objects the same way |
| Decorator | Add behavior to one object without touching its class |
| Facade | Hide a complicated subsystem behind one simple entry point |
| Flyweight | Share the common part of state so millions of objects fit in memory |
| Proxy | Stand in for another object to control access, cost, or timing |

### Behavioral: how objects talk

| Pattern | What it solves |
|---|---|
| Chain of Responsibility | Pass a request down a line of handlers until one takes it |
| Command | Turn a request into an object you can queue, log, retry, or undo |
| Iterator | Walk a collection without exposing how it stores things |
| Mediator | Route object-to-object talk through one coordinator |
| Memento | Capture and restore state without breaking encapsulation |
| Observer | Notify many dependents when one object changes |
| State | Change behavior when internal state changes, instead of a growing switch |
| Strategy | Swap an algorithm at runtime behind one interface |
| Template Method | Fix the steps of an algorithm, let subclasses fill in the steps |
| Visitor | Add a new operation to a class hierarchy without editing the hierarchy |

Full catalog with links, comparisons, and the "often confused with" map: [code-patterns/README.md](code-patterns/README.md).

## System patterns

Sixty-two patterns, grouped the way you meet them: by the problem you are trying to solve, not alphabetically.

| Group | Patterns | Covers |
|---|---|---|
| [Communication](system-patterns/README.md#communication) | 7 | Request-response, queues, pub-sub, event-driven, webhooks, SSE, streaming |
| [Storage and replication](system-patterns/README.md#storage-and-replication) | 6 | Primary-replica, sharding, consistent hashing, write-ahead log, event sourcing, CQRS |
| [Caching](system-patterns/README.md#caching) | 5 | Cache-aside, read-through, write-through, write-behind, stampede prevention |
| [Reliability](system-patterns/README.md#reliability) | 7 | Timeout, retry, idempotency, circuit breaker, bulkhead, dead letter queue, degradation |
| [Scaling](system-patterns/README.md#scaling) | 5 | Vertical, horizontal, load balancing, auto-scaling, connection pooling |
| [Distributed consistency](system-patterns/README.md#distributed-consistency) | 4 | Two-phase commit, saga, quorum, vector clocks |
| [API and edge](system-patterns/README.md#api-and-edge) | 9 | Reverse proxy, CDN, API gateway, BFF, rate limiting, pagination, versioning, sidecar, service mesh |
| [Operations](system-patterns/README.md#operations) | 5 | Health checks, tracing, blue-green, canary, feature flags |
| [Data-intensive](system-patterns/README.md#data-intensive) | 7 | MapReduce, stream processing, lambda and kappa, CDC, exactly-once, backpressure, partitioned consumption |
| [AI-era infrastructure](system-patterns/README.md#ai-era-infrastructure) | 7 | Feature store, model serving, GPU auto-scaling, LLM gateway, semantic caching, vector DB sharding, RAG |

Full catalog: [system-patterns/README.md](system-patterns/README.md).

## What each page gives you

Every page is written to a fixed shape, because a reference is only useful if you can find the same thing in the same place every time.

Code pattern pages: the problem, a plain analogy, a class diagram, minimal code in Java, Python, and TypeScript, when to use it, **when not to**, what it costs, the mistakes that show up in code review, the patterns it gets confused with.

System pattern pages: the mechanism, a diagram, **knobs and typical numbers**, trade-offs, **failure modes when it is misconfigured**, real systems that use it, **what to instrument**, and the patterns it pairs with.

## Also in this repo

Being built in batches. Current state is visible in the two catalogs above.

- `examples/`: runnable code for every Gang of Four pattern in Java, Python, and TypeScript. No framework, no build system, one file per pattern.
- `decision-guides/`: the side-by-side sheets. Strategy vs State. Factory Method vs Abstract Factory. Decorator vs Proxy vs Adapter. Observer vs pub-sub. Saga vs two-phase commit. Blue-green vs canary.
- `anti-patterns/`: the cost side. Singleton as a global variable, pattern-itis, retry storms, the distributed monolith, saga without compensations.
- `for-managers/`: reviewing a design doc for cargo cult, an ADR template, a per-family design-review checklist, and the onboarding cost of every pattern you approve.
- `case-studies/`: four full designs (news feed, ride-sharing dispatch, payment platform, AI copilot), read as "here is every pattern this composes, and why each one is there".
- `interview/`: how patterns actually get tested, in object-oriented design rounds and system design rounds.
- `cheat-sheets/`: flashcards, selection tables, one-pagers.

## Related repository

[**grokking-system-design**](https://github.com/design-gurus/grokking-system-design) is the interview-prep companion: the framework, 60+ question walkthroughs, 120+ company-specific guides, deep dives, and study roadmaps.

The split between the two repos is deliberate. That repo answers "how do I talk about this in an interview". This one answers "how do I build it, what are the knobs, and when is it the wrong choice". Overlapping patterns link to each other rather than repeating each other.

## Contributing

Pull requests are welcome. Read [CONTRIBUTING.md](CONTRIBUTING.md) first: it has the page templates, the house style rules, and the one rule that keeps this repo from duplicating its sibling.

## License

Content is [CC BY 4.0](LICENSE). Use it, adapt it, teach from it. Please keep the attribution.
