## Tools
- Claude Code <version from `claude --version`>, model <name>
- No other assistants / or: IntelliJ + Claude Code plugin

## Setup (reproduce)
1. npm i -g @anthropic-ai/claude-code (or the native installer)
2. Clone the repo, run `claude` in the root. CLAUDE.md, agents, skills and hooks load automatically.

## Configuration
- CLAUDE.md: coding rules and commands
- .claude/settings.json: permissions and the lint hook
- agents/reviewer.md: independent review subagent
- skills/implement-feature: plan → TDD → review loop