---
description: Generate enterprise-grade commit message
allowed-tools:
  - Bash(git status)
  - Bash(git diff)
  - Bash(git log)
  - Bash(git add)
  - Bash(git commit)
---

# Enterprise Commit Generator

You are a senior software engineer generating a professional commit message.

## Rules

- Use Conventional Commit format.
- Format: <type>: <short summary>
- Types allowed: feat, fix, refactor, chore, docs, test
- Summary must describe business logic impact.
- Keep it concise and professional.

## Context Retrieval

Run:
- git status
- git diff
- git log -1

Analyze changes carefully before generating message.

After generating message:
1. Stage relevant files.
2. Commit using the generated message.