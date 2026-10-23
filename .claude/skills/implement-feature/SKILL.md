---
name: implement-feature
description: Implement one requirement from the task spec using plan → test → code → review.
disable-model-invocation: true
---
1. Read the requirement and restate it in your own words. List any assumptions.
2. Propose a short plan and wait for my approval.
3. Write failing tests, then the implementation, then run ./gradlew test.
4. Invoke the hardener subagent for the proper quality
5. Invoke the reviewer subagent and fix the high-priority findings.
6. Summarize what changed and suggest a commit message.