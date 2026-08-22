# [Pattern name]

> One line: the failure or limit this pattern removes.

**Group:** Communication | Storage | Caching | Reliability | Scaling | Consistency | API and edge | Operations | Data-intensive | AI-era
**Interview framing:** link to the matching page in the sibling repo, if it has one. Do not write the interview framing here.

Note for authors: placeholder paths below are shown as plain text so this template does not fail the link check. In a real page, write them as relative markdown links, for example a link to `../reliability/timeout.md`.

## What it is

Two or three sentences in plain language. A reader who has never heard the name should finish this section knowing what the thing does.

## The failure it prevents

Describe the specific bad outcome you get without it. Not "poor performance". Something like: one slow downstream call holds a thread, threads run out, healthy endpoints on the same service start timing out too.

## How it works

The mechanism, step by step. Then a diagram.

```mermaid
flowchart LR
    Client --> Component --> Store
```

## Knobs and typical numbers

The section that makes this a reference instead of a summary. Give real starting values and say what moves them.

| Knob | Typical starting value | Raise it when | Lower it when |
|---|---|---|---|
| Example: timeout | 1 to 3 seconds for a user-facing call | The downstream is genuinely slow and the user is waiting | The caller has a tighter deadline |

## Trade-offs

| You gain | You pay |
|---|---|
| Benefit | Cost, risk, or new operational burden |

## Failure modes

What breaks when this pattern is present but misconfigured. This is where most production incidents actually live.

- The misconfiguration, the symptom it produces, and how to tell it apart from a different cause.
- The interaction failure: what happens when this pattern is combined with another one badly, for example retries without idempotency.

## What to instrument

- The metric that tells you the pattern is working.
- The metric that tells you it is about to stop working.
- The alert worth paging on, and the one that will only wake people up for nothing.

## Real systems that use it

Two or three named examples with one line each. Prefer systems with public documentation or a published post-mortem.

## Pairs with

- The pattern that this one is incomplete without, linked as `../<group>/<pattern>.md`.
- The pattern it is often confused with, and the difference in one sentence.

## Go deeper

This pattern is covered lesson by lesson, with diagrams, real incidents, and trade-offs, in [System Design Patterns](https://www.designgurus.io/course/system-design-patterns?utm_source=github&utm_medium=repo&utm_campaign=design-patterns). For how it comes up in an interview, see the matching page in [grokking-system-design](https://github.com/design-gurus/grokking-system-design) and the [Grokking the System Design Interview](https://www.designgurus.io/course/grokking-the-system-design-interview?utm_source=github&utm_medium=repo&utm_campaign=design-patterns) course.
