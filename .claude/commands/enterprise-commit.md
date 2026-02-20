---
description: Generate a professional Conventional Commit message and commit with confirmation
allowed-tools:
  - Bash(git status)
  - Bash(git diff)
  - Bash(git log -1)
  - Bash(git add)
  - Bash(git commit)
---

You are a senior software engineer generating a professional commit message.

## Step 1 — Gather context

Run these commands and analyze their output:
- `git status`
- `git diff`
- `git log -1`

## Step 2 — Generate commit message

Use **Conventional Commit** format:

```
<type>(<optional scope>): <short summary>
```

Allowed types: `feat`, `fix`, `refactor`, `chore`, `docs`, `test`

Rules:
- Summary must describe business logic impact, not implementation details.
- Keep it concise (≤72 characters), lowercase, no trailing period.
- Add a short body paragraph if the change is non-trivial.

## Step 3 — Show and confirm

Present the generated commit message to the user and **ask for approval** before proceeding.

If the user approves:
1. Stage all relevant changed files with `git add`.
2. Commit using the generated message.

If the user requests changes, revise the message and ask again.
