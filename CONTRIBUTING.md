# Contributing

Thanks for helping improve this free pattern reference. Contributions of all sizes are welcome: a typo fix, a clearer explanation, a missing failure mode, a code example in a language we do not cover yet, or a whole new page.

## The one rule that matters most

This repo has a sibling: [grokking-system-design](https://github.com/design-gurus/grokking-system-design). Both cover system design patterns, and they must not repeat each other.

- **grokking-system-design** is the interview-prep hub. Its pattern pages answer "how do I talk about this in an interview?"
- **design-patterns** (this repo) is the engineering reference. Its pages answer "how do I build this, what are the knobs, and when is it the wrong choice?"

So in this repo:

- Do **not** add "how to talk about it in an interview" sections to pattern pages. Link to the sibling repo's page instead.
- Do add the things a reference needs and an interview guide skips: real numbers, configuration knobs, failure modes, what to instrument, and named systems that use the pattern.
- If a pattern already has a page in the sibling repo, link to it from your page's "Related" section, and open a matching pull request there linking back.

## Ground rules

- Keep it original. Do not paste content from paid courses (including the DesignGurus courses) or other copyrighted sources. Write in your own words.
- Be concrete. "Set a timeout" is not useful. "Set the timeout below the caller's timeout, and below the client's patience, typically 1 to 3 seconds for a user-facing call" is.
- Say when a pattern is wrong. Every page needs a real "when not to use it" section. A page that only sells the pattern will be rejected.
- Stay vendor-neutral in the explanation. Name real systems as examples, not as the only answer.
- No em dashes. Use commas, colons, parentheses, or periods instead. (House style.)
- No horizontal rules inside a page body.

## Adding a code pattern (Gang of Four)

1. Copy `code-patterns/_template.md` into the right family folder, for example `code-patterns/structural/adapter.md`.
2. Fill in every section. The "when not to use it", "what it costs", and "often confused with" sections are the ones readers come back for.
3. Add runnable code to `examples/java/`, `examples/python/`, and `examples/typescript/`. One file per pattern, no framework, no build system, runnable with a single command.
4. Add a row to the catalog table in `code-patterns/README.md`.

## Adding a system pattern

1. Copy `system-patterns/_template.md` into the right group folder, for example `system-patterns/reliability/bulkhead.md`.
2. Fill in every section. "Knobs and typical numbers", "failure modes", and "what to instrument" are what make this repo different from every other pattern list.
3. Add a row to the catalog table in `system-patterns/README.md`.
4. If the sibling repo has a page for the same pattern, link to it and note that the interview framing lives there.

## Style

- One concept per file.
- Short sentences. Define a term the first time it appears.
- Use relative links between files so they resolve on GitHub. CI fails a pull request on a broken relative link.
- Diagrams: Mermaid, or simple ASCII. Both render on GitHub without a build step.
- Course links carry UTM parameters, so we can tell what this repo actually sends:
  `?utm_source=github&utm_medium=repo&utm_campaign=design-patterns`

## "Go deeper" footers

Every page ends with one. Match the page to the right course:

| Page type | Links to |
|---|---|
| Code patterns | [Grokking Design Patterns for Engineers and Managers](https://www.designgurus.io/course/grokking-design-patterns-for-engineers-and-managers?utm_source=github&utm_medium=repo&utm_campaign=design-patterns) |
| System patterns | [System Design Patterns](https://www.designgurus.io/course/system-design-patterns?utm_source=github&utm_medium=repo&utm_campaign=design-patterns) |
| Object-oriented design interview material | [Grokking the Object-Oriented Design Interview](https://www.designgurus.io/course/grokking-the-object-oriented-design-interview?utm_source=github&utm_medium=repo&utm_campaign=design-patterns) |
| System design interview material | [Grokking the System Design Interview](https://www.designgurus.io/course/grokking-the-system-design-interview?utm_source=github&utm_medium=repo&utm_campaign=design-patterns) |

## Pull requests

Open a pull request with a short description of what you changed and why. Small, focused pull requests are easier to review and merge. If you are planning something large, open an issue first so we can agree on the shape before you write it.
