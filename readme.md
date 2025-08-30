# AI Code Tools

AI 驱动的代码生成和管理工具集，提供文件操作等基础功能。

## 项目概述

本项目提供了一系列工具，允许 AI 系统通过工具调用的方式执行文件操作，包括文件删除等。这些工具基于 LangChain4j 框架构建，可以集成到 AI 应用中，使 AI 能够安全地操作文件系统。

## 主要功能

### 文件删除工具 (FileDeleteTool)

允许 AI 安全地删除指定路径的文件，具有以下特性：

- 支持相对路径和绝对路径
- 自动定位到项目根目录（基于 appId）
- 安全检查机制，防止删除重要文件
- 详细的日志记录和错误处理

#### 受保护的重要文件

为防止破坏项目结构，以下重要文件受到保护，无法被删除：

- package.json
- package-lock.json
- yarn.lock
- pnpm-lock.yaml
- vite.config.js / vite.config.ts
- vue.config.js
- tsconfig.json 系列配置文件
- index.html
- main.js / main.ts
- App.vue
- .gitignore
- README.md

## 技术栈

- Java 8+
- Spring Boot
- LangChain4j
- Hutool
- Lombok

## 使用说明

### 文件删除工具使用方法

