# System Patterns

Sixty-two patterns for the layer above your code: how services talk, where data lives, what happens when a dependency gets slow, and how any of it survives a traffic spike.

Pages ship in batches. A linked pattern name means the page is live. An unlinked name is on the list.

**Where the interview framing lives.** Several of these patterns already have an interview-focused page in the sibling repo, [grokking-system-design](https://github.com/design-gurus/grokking-system-design). Those pages answer "how do I talk about this in a round". The pages here answer "how do I build it, what are the knobs, and how does it fail". The third column points at the interview version where one exists.

## Contents

- [Communication](#communication)
- [Storage and replication](#storage-and-replication)
- [Caching](#caching)
- [Reliability](#reliability)
- [Scaling](#scaling)
- [Distributed consistency](#distributed-consistency)
- [API and edge](#api-and-edge)
- [Operations](#operations)
- [Data-intensive](#data-intensive)
- [AI-era infrastructure](#ai-era-infrastructure)
- [How to read this catalog](#how-to-read-this-catalog)

## Communication

How one service gets a message to another, and what it gives up in exchange.

| Pattern | What it solves | Interview view |
|---|---|---|
| Request-Response | The default. Caller waits, gets an answer, handles the error itself | |
| Message Queue | Work outlives the request. The producer stops waiting for the consumer | [sibling page][gsd-queues] |
| Publish-Subscribe | One event, many independent consumers, none of them known to the producer | [sibling page][gsd-queues] |
| Event-Driven Architecture | Services coordinate through facts that happened, not commands to obey | |
| Webhooks | You push to them instead of making them poll you | |
| Server-Sent Events | One-way server push over plain HTTP, with automatic reconnect | [sibling page][gsd-realtime] |
| Bidirectional Streaming | Both sides send whenever they have something, over one connection | [sibling page][gsd-realtime] |

## Storage and replication

Where the data sits, how many copies exist, and what the copies owe each other.

| Pattern | What it solves | Interview view |
|---|---|---|
| Primary-Replica | Read scaling and failover, at the cost of replication lag | [sibling page][gsd-replication] |
| Sharding | Data larger than one machine, split by a key you have to choose carefully | [sibling page][gsd-sharding] |
| Consistent Hashing | Adding or removing a node without reshuffling everything | [sibling page][gsd-chash] |
| Write-Ahead Log | Durability before acknowledgement, and the basis for replication and recovery | [sibling page][gsd-wal] |
| Event Sourcing | Store what happened, derive the current state, keep the whole history | [sibling page][gsd-es] |
| CQRS | Separate the write model from the read model when they want different shapes | [sibling page][gsd-es] |

## Caching

Five strategies that are usually taught as one word. The differences decide whether stale data is possible and who pays for a miss.

| Pattern | What it solves | Interview view |
|---|---|---|
| Cache-Aside | Application controls the cache. The most common, and the easiest to get subtly wrong | [sibling page][gsd-caching] |
| Read-Through | The cache owns the fetch, so callers never see the data source | [sibling page][gsd-caching] |
| Write-Through | Write to cache and store together, buying consistency with write latency | [sibling page][gsd-caching] |
| Write-Behind | Acknowledge fast, persist later, and accept a window where data can be lost | [sibling page][gsd-caching] |
| Cache Stampede Prevention | Stop a thousand requests from rebuilding the same expired key at once | [sibling page][gsd-caching] |

## Reliability

Seven patterns that exist because dependencies get slow before they get fast again.

| Pattern | What it solves | Interview view |
|---|---|---|
| Timeout | Bounding how long you are willing to be stuck. The one nobody sets correctly | |
| Retry with Exponential Backoff | Recovering from a transient failure without turning it into a stampede | |
| Idempotency | Making a repeated request safe, which is what makes retries safe at all | [sibling page][gsd-idempotency] |
| Circuit Breaker | Failing fast when a dependency is already down, instead of queueing behind it | [sibling page][gsd-cb] |
| Bulkhead | Isolating resources so one sick dependency cannot drown the whole service | |
| Dead Letter Queue | Somewhere for the message that will never succeed, so the queue keeps moving | |
| Graceful Degradation | Serving something useful when the ideal answer is unavailable | |

## Scaling

| Pattern | What it solves | Interview view |
|---|---|---|
| Vertical Scaling | The bigger machine. Underrated, and it runs out | |
| Horizontal Scaling | More machines, which forces you to deal with state | |
| Load Balancing | Spreading traffic, and deciding what "healthy" means | [sibling page][gsd-lb] |
| Auto-Scaling | Matching capacity to load, with the lag between the two as the real problem | |
| Connection Pooling | Not paying the connection cost per request, and not exhausting the database | |

## Distributed consistency

| Pattern | What it solves | Interview view |
|---|---|---|
| Two-Phase Commit | Atomic commit across systems, at the cost of a blocking coordinator | [sibling page][gsd-txn] |
| Saga | Long-lived transactions as steps plus compensations, with no global lock | [sibling page][gsd-txn] |
| Quorum | Agreeing with a majority instead of everyone, so a minority can fail | [sibling page][gsd-quorum] |
| Vector Clocks | Telling concurrent updates apart from ordered ones without a global clock | [sibling page][gsd-clocks] |

## API and edge

Everything between the user and your services.

| Pattern | What it solves | Interview view |
|---|---|---|
| Reverse Proxy | One front door: TLS, routing, buffering, and a place to put policy | [sibling page][gsd-proxies] |
| CDN | Serving bytes from near the user, and the cache invalidation that comes with it | [sibling page][gsd-cdn] |
| API Gateway | Auth, rate limits, and routing in one hop, plus the risk of a new monolith | [sibling page][gsd-gateway] |
| Backend for Frontend | A tailored API per client, instead of one API that fits none of them | |
| Rate Limiting | Protecting capacity, and choosing an algorithm that matches your traffic shape | [sibling page][gsd-ratelimit] |
| Cursor-Based Pagination | Stable paging over changing data, which offset pagination cannot give you | |
| API Versioning | Changing a contract without breaking clients you do not control | |
| Sidecar | Moving cross-cutting concerns out of the application process | [sibling page][gsd-proxies] |
| Service Mesh | Sidecars with a control plane, and whether you are big enough to need one | |

## Operations

| Pattern | What it solves | Interview view |
|---|---|---|
| Health Check Endpoint | Telling the load balancer the truth, including the difference between live and ready | [sibling page][gsd-heartbeats] |
| Distributed Tracing | Following one request across services when the logs are in twelve places | |
| Blue-Green Deployment | Two full environments and an instant switch, including instant rollback | |
| Canary Deployment | Exposing a small share of real traffic before you commit | |
| Feature Flags | Separating deploy from release, and the flag debt that follows | |

## Data-intensive

| Pattern | What it solves | Interview view |
|---|---|---|
| MapReduce | Splitting a huge batch job across machines, and why it fell out of fashion | [sibling page][gsd-batch] |
| Stream Processing | Computing on data in motion, with windows and late arrivals | [sibling page][gsd-batch] |
| Lambda and Kappa | Whether you keep a batch layer alongside the stream, or just the stream | [sibling page][gsd-batch] |
| Change Data Capture | Turning database writes into a stream without dual writes | |
| Exactly-Once Semantics | What it actually means, and why it is really effectively-once | |
| Backpressure | Letting a slow consumer slow the producer instead of falling over | [sibling page][gsd-backpressure] |
| Partitioned Consumption | Parallel consumers with ordering guarantees inside a partition | |

## AI-era infrastructure

The seven patterns that showed up in ordinary system design work once models went to production.

| Pattern | What it solves | Interview view |
|---|---|---|
| Feature Store | The same feature computed the same way in training and in serving | |
| Model Serving | Getting a model behind an API with batching, versions, and rollback | |
| GPU Auto-Scaling | Scaling the most expensive machine you rent, with a cold start measured in minutes | |
| LLM Gateway | One control point for keys, quotas, routing, fallbacks, and cost per team | |
| Semantic Caching | Reusing an answer for a question that is similar, not identical | |
| Vector Database Sharding | Splitting an index when recall depends on seeing enough of it | |
| RAG Pipeline | Retrieval, ranking, and generation as a system with real failure modes | |

## How to read this catalog

Three suggestions, depending on why you are here.

- **You have a live problem.** Go to the group that matches the symptom, not the pattern name. Slow reads is Caching. One dependency taking down the service is Reliability. Cannot deploy without downtime is Operations.
- **You are learning the space.** Read one group end to end. The patterns inside a group are alternatives to each other, and the differences between them are the actual lesson.
- **You are preparing for an interview.** Start in [grokking-system-design](https://github.com/design-gurus/grokking-system-design) instead. Come back here when you want the numbers behind the answer.

## Go deeper

All 62 patterns are taught, with diagrams, real incidents, and honest trade-offs, in [System Design Patterns](https://www.designgurus.io/course/system-design-patterns?utm_source=github&utm_medium=repo&utm_campaign=design-patterns). The course also builds four full designs (a news feed, ride-sharing dispatch, a payment platform, and an AI copilot) out of these patterns.

To add a page, copy [_template.md](_template.md) and follow [CONTRIBUTING.md](../CONTRIBUTING.md).

[gsd-queues]: https://github.com/design-gurus/grokking-system-design/blob/main/patterns/message-queues.md
[gsd-realtime]: https://github.com/design-gurus/grokking-system-design/blob/main/patterns/long-polling-websockets-sse.md
[gsd-replication]: https://github.com/design-gurus/grokking-system-design/blob/main/patterns/replication.md
[gsd-sharding]: https://github.com/design-gurus/grokking-system-design/blob/main/patterns/sharding-partitioning.md
[gsd-chash]: https://github.com/design-gurus/grokking-system-design/blob/main/patterns/consistent-hashing.md
[gsd-wal]: https://github.com/design-gurus/grokking-system-design/blob/main/patterns/write-ahead-log.md
[gsd-es]: https://github.com/design-gurus/grokking-system-design/blob/main/patterns/event-sourcing-cqrs.md
[gsd-caching]: https://github.com/design-gurus/grokking-system-design/blob/main/patterns/caching.md
[gsd-idempotency]: https://github.com/design-gurus/grokking-system-design/blob/main/patterns/idempotency.md
[gsd-cb]: https://github.com/design-gurus/grokking-system-design/blob/main/patterns/circuit-breaker.md
[gsd-lb]: https://github.com/design-gurus/grokking-system-design/blob/main/patterns/load-balancing.md
[gsd-txn]: https://github.com/design-gurus/grokking-system-design/blob/main/patterns/distributed-transactions.md
[gsd-quorum]: https://github.com/design-gurus/grokking-system-design/blob/main/patterns/quorum.md
[gsd-clocks]: https://github.com/design-gurus/grokking-system-design/blob/main/patterns/logical-clocks.md
[gsd-proxies]: https://github.com/design-gurus/grokking-system-design/blob/main/patterns/proxies.md
[gsd-cdn]: https://github.com/design-gurus/grokking-system-design/blob/main/patterns/cdn.md
[gsd-gateway]: https://github.com/design-gurus/grokking-system-design/blob/main/patterns/api-gateway.md
[gsd-ratelimit]: https://github.com/design-gurus/grokking-system-design/blob/main/patterns/rate-limiting.md
[gsd-heartbeats]: https://github.com/design-gurus/grokking-system-design/blob/main/patterns/heartbeats.md
[gsd-batch]: https://github.com/design-gurus/grokking-system-design/blob/main/patterns/batch-vs-stream-processing.md
[gsd-backpressure]: https://github.com/design-gurus/grokking-system-design/blob/main/patterns/backpressure.md
