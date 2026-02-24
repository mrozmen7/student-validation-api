# MCP Configuration Guide

Three purpose-built MCP configs live under `.mcp/`. Pick the one that matches your task.

---

## Modes

### CODING — `.mcp/coding.json`
**Use for:** daily development — CRUD, service logic, validation, tests, refactoring.

Servers: Context7 (live library docs)

```bash
claude --mcp-config .mcp/coding.json --strict-mcp-config
```

---

### RESEARCH — `.mcp/research.json`
**Use for:** architecture decisions, comparing technologies, searching external resources.

Servers: Context7 + Tavily (web search)

> **Setup:** Replace `YOUR_TAVILY_API_KEY_HERE` in `.mcp/research.json` with your Tavily API key.

```bash
claude --mcp-config .mcp/research.json --strict-mcp-config
```

---

### BROWSER — `.mcp/browser.json`
**Use for:** UI verification, scraping, end-to-end browser automation.

Servers: Playwright

> **Setup:** Requires Node.js. `npx @playwright/mcp@latest` is fetched automatically on first run.

```bash
claude --mcp-config .mcp/browser.json --strict-mcp-config
```

---

## Notes

- `--strict-mcp-config` disables all servers not listed in the chosen config.
- The root `.mcp.json` (if present) is your personal local default — it is git-ignored.
- All `.mcp/*.json` configs are committed and shared across the team.
