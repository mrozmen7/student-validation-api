# MCP Mode Guide

Shared MCP configs live under `.mcp/`. Start Claude with the mode that matches your task.
`--strict-mcp-config` disables every server not listed in the chosen file.

---

## Modes

| Mode | Config | Servers | Use when |
|------|--------|---------|----------|
| CODING | `.mcp/coding.json` | Context7 | Daily dev — CRUD, service, validation, refactoring |
| TESTING | `.mcp/testing.json` | Context7 | Writing or debugging tests, Testcontainers setup |
| RESEARCH | `.mcp/research.json` | Context7 + Tavily | Architecture decisions, tech comparison, external search |
| BROWSER | `.mcp/browser.json` | Playwright | UI verification, scraping, browser automation |

---

## Commands

```bash
# CODING
claude --mcp-config .mcp/coding.json --strict-mcp-config

# TESTING
claude --mcp-config .mcp/testing.json --strict-mcp-config

# RESEARCH
claude --mcp-config .mcp/research.json --strict-mcp-config

# BROWSER
claude --mcp-config .mcp/browser.json --strict-mcp-config
```

---

## Setup notes

**Tavily** (`RESEARCH`): replace `YOUR_TAVILY_API_KEY_HERE` in `.mcp/research.json` with your API key.

**Playwright** (`BROWSER`): requires Node.js — `npx @playwright/mcp@latest` is fetched on first run.

---

## Local vs shared

| File | Committed | Purpose |
|------|-----------|---------|
| `.mcp/*.json` | Yes | Shared team configs — one per mode |
| `.mcp.json` (root) | No (git-ignored) | Personal local default |
