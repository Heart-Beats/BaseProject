<!-- TRELLIS:START -->
# Trellis 指令

这些说明适用于在此项目中工作的人工智能助手。

该项目由 Trellis 管理。您需要的工作知识位于“.trellis/”下：

- `.trellis/workflow.md` — 开发阶段、何时创建任务、技能路由
- `.trellis/spec/` — 包范围和层范围的编码指南（在给定层中编写代码之前阅读）
- `.trellis/workspace/` — 每个开发人员的日志和会话跟踪
- `.trellis/tasks/` — 活动和存档任务（PRD、研究、jsonl 上下文）

如果您的平台上有 Trellis 命令（例如 `/trellis:finish-work`、`/trellis:continue`），则优先使用它而不是手动步骤。并非每个平台都会公开每个命令。

如果您使用 Codex 或其他支持代理的工具，其他项目范围的帮助程序可能位于：
- `.agents/skills/` — 可重用的格子技能
- `.codex/agents/` — 可选的自定义子代理

由 Trellis 管理。保留该块之外的编辑；内部的编辑可能会被未来的 `trellis update` 覆盖。
<!-- TRELLIS:END -->
