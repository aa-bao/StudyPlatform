# glm coding plan
{
  "env": {
    "ANTHROPIC_AUTH_TOKEN": "e90579b6f4df4a8ab4b9381dfa1b7466.cgxYg4MwekKDlZHO",
    "ANTHROPIC_BASE_URL": "https://open.bigmodel.cn/api/anthropic",
    "API_TIMEOUT_MS": "3000000",
    "CLAUDE_CODE_DISABLE_NONESSENTIAL_TRAFFIC": 1
  }
}


# 龙猫key
```
{
  "env": {
    "ANTHROPIC_AUTH_TOKEN": "ak_2yo0i27vi90L4xY15b2WU6bw53C49",
    "ANTHROPIC_BASE_URL": "https://api.longcat.chat/anthropic",
    "ANTHROPIC_MODEL": "LongCat-Flash-Lite",
    "ANTHROPIC_SMALL_FAST_MODEL": "LongCat-Flash-Lite",
    "ANTHROPIC_DEFAULT_SONNET_MODEL": "LongCat-Flash-Lite",
    "ANTHROPIC_DEFAULT_OPUS_MODEL": "LongCat-Flash-Lite",
    "CLAUDE_CODE_MAX_OUTPUT_TOKENS": "6000",
    "CLAUDE_CODE_DISABLE_NONESSENTIAL_TRAFFIC": 1
  },
  "permissions": {
    "allow": [],
    "deny": []
  }
}
```

# 火山 coding plan
```
{
  "env": {
    "ANTHROPIC_AUTH_TOKEN": "5d392539-dc02-41db-8756-e5f1b29160f0",
    "ANTHROPIC_BASE_URL": "https://ark.cn-beijing.volces.com/api/coding",
    "ANTHROPIC_MODEL": "ark-code-latest",
    "API_TIMEOUT_MS": "3000000",
    "CLAUDE_CODE_DISABLE_NONESSENTIAL_TRAFFIC": "1"
  },
  "permissions": {
    "allow": [
      "Bash(git push:*)"
    ]
  },
  "enableAllProjectMcpServers": true,
  "enabledPlugins": {
    "code-review@claude-plugins-official": true,
    "superpowers@claude-plugins-official": true,
    "context7@claude-plugins-official": true,
    "example-skills@anthropic-agent-skills": true,
    "document-skills@anthropic-agent-skills": true
  },
  "skipDangerousModePermissionPrompt": true
}
```