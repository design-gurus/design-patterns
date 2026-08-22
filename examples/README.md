# Runnable examples

One file per pattern, per language. No framework, no build system, no dependencies. Each file runs on its own and prints output that shows the pattern doing something.

Every file follows each language's own naming convention, because that is what you would actually write:

| Language | File | Run it with |
|---|---|---|
| Java | `java/StrategyDemo.java` | `java java/StrategyDemo.java` (Java 17 or newer, no compile step needed) |
| Python | `python/strategy.py` | `python python/strategy.py` (Python 3.10 or newer) |
| TypeScript | `typescript/strategy.ts` | `npx tsx typescript/strategy.ts` |

## What these are, and what they are not

They are the smallest version of each pattern that still shows why it exists. Every file has a "without the pattern" comment at the top naming the problem, then the pattern, then a short `main` that runs it.

They are not production code. Real implementations add error handling, thread safety, logging, and dependency injection. Those would triple the length and hide the idea. The pattern pages in [../code-patterns/](../code-patterns/) cover the production concerns in the "mistakes that show up in code review" section.

## Index

| Pattern | Java | Python | TypeScript |
|---|---|---|---|
| Singleton | `SingletonDemo.java` | `singleton.py` | `singleton.ts` |
| Factory Method | `FactoryMethodDemo.java` | `factory_method.py` | `factory-method.ts` |
| Abstract Factory | `AbstractFactoryDemo.java` | `abstract_factory.py` | `abstract-factory.ts` |
| Builder | `BuilderDemo.java` | `builder.py` | `builder.ts` |
| Prototype | `PrototypeDemo.java` | `prototype.py` | `prototype.ts` |
| Adapter | `AdapterDemo.java` | `adapter.py` | `adapter.ts` |
| Bridge | `BridgeDemo.java` | `bridge.py` | `bridge.ts` |
| Composite | `CompositeDemo.java` | `composite.py` | `composite.ts` |
| Decorator | `DecoratorDemo.java` | `decorator.py` | `decorator.ts` |
| Facade | `FacadeDemo.java` | `facade.py` | `facade.ts` |
| Flyweight | `FlyweightDemo.java` | `flyweight.py` | `flyweight.ts` |
| Proxy | `ProxyDemo.java` | `proxy.py` | `proxy.ts` |
| Chain of Responsibility | `ChainOfResponsibilityDemo.java` | `chain_of_responsibility.py` | `chain-of-responsibility.ts` |
| Command | `CommandDemo.java` | `command.py` | `command.ts` |
| Iterator | `IteratorDemo.java` | `iterator.py` | `iterator.ts` |
| Mediator | `MediatorDemo.java` | `mediator.py` | `mediator.ts` |
| Memento | `MementoDemo.java` | `memento.py` | `memento.ts` |
| Observer | `ObserverDemo.java` | `observer.py` | `observer.ts` |
| State | `StateDemo.java` | `state.py` | `state.ts` |
| Strategy | `StrategyDemo.java` | `strategy.py` | `strategy.ts` |
| Template Method | `TemplateMethodDemo.java` | `template_method.py` | `template-method.ts` |
| Visitor | `VisitorDemo.java` | `visitor.py` | `visitor.ts` |

## A note on the Python and TypeScript versions

Several of these patterns are shorter in Python and TypeScript than in Java, and a few nearly disappear. Strategy is a function. Decorator has syntax support in Python. Iterator is a generator. Where that happens, the file shows the classic structure first and then the idiomatic short version, so you can see what the pattern was compensating for. The "in modern languages" section on each pattern page explains why.
