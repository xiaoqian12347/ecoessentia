# 本地编译依赖

`ecoaeextension-1.2.0.jar` 是官方 ECO 1.2.0 编译依赖，不纳入源码仓库，也不会嵌入发布 JAR。运行 `python scripts/prepare_dependencies.py` 可下载并校验；支持手动下载后改名放到本目录。

- 上游：https://github.com/sddsd2332/NovaEngineering-ECOAEExtension
- 下载：https://github.com/sddsd2332/NovaEngineering-ECOAEExtension/releases/download/1.2.0/Nova-Engineering-ECO-AE-Extension-1.2.0.jar
- SHA-256：`c6c0bf0a3869c8f780296589aa5010d07ced5fa9524e1f584028a3e82bb03e2e`
- 上游许可证：GPL-3.0，参见其仓库 LICENSE。

构建使用的其他依赖由 Gradle 从 CurseMaven 获取。
