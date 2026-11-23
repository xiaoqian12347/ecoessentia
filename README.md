# ECO Essentia Storage

Minecraft 1.12.2 / Cleanroom 的 ECO 源质存储附属，联动神秘时代 6 和神秘能源 Extended Life。使用神秘能源的独立源质通道，将源质存入 ECO 存储矩阵。

- 作者：**xiaoqian**
- Mod ID：`ecoessentia`
- Java 包名：`com.xiaoqian.ecoessentia`
- 版本：**0.2.0**
- 许可证：**GPL-3.0-only**，素材与依赖来源见 [NOTICE.md](NOTICE.md)。

## 存储矩阵

| 显示名 | 容量 | 源质种类上限 | 每种开销 | ECO 存储倍率 | 支持控制器 |
| --- | ---: | ---: | ---: | ---: | --- |
| ECO - LE4 源质存储矩阵 | 16,384,000 字节 | 25 | 4,096 字节 | 4 | L4 / L6 / L9 |
| ECO - LE6 源质存储矩阵 | 65,536,000 字节 | 25 | 16,384 字节 | 16 | L6 / L9 |
| ECO - LE9 源质存储矩阵 | 262,144,000 字节 | 25 | 65,536 字节 | 64 | L9 |

容量参数参考 ECO 流体矩阵，M 沿用 ECO 的 `1000 × 1024` 字节定义。每字节可存的源质数量 = 神秘能源通道原生单位数 × ECO 倍率；编译参考版本原生值为 8，因此三档分别是 32 / 128 / 512 点源质每字节。

所有源质共享容量：`已用字节 = 种类数 × 每种开销 + 向上取整(总源质数量 / 每字节单位数)`。数量与 NBT 均使用 `long`；模拟操作不写入数据，每次实际增删只更新对应源质并通知宿主保存。未知附属源质的数据会保留。

**不提供合成配方**，由整合包脚本配置。物品在创造模式的 ECO 分类中：

```text
ecoessentia:estorage_cell_essentia_16m
ecoessentia:estorage_cell_essentia_64m
ecoessentia:estorage_cell_essentia_256m
```

支持中文和英文名称，等级配色参考 ECO。物品源质指示灯为 `#8400ff`，元件仓标识及控制器源质类型条为 `#996dc2`。物品的绿 / 橙 / 红状态分别表示可新增种类、仅可继续存已有源质、容量已满；中间状态并非 50% 容量。

## 安装与兼容性

客户端和服务端均安装同版本附属及依赖：

- Cleanroom（开发基线 **0.7.4-alpha**；构建使用 **JDK 25**）。
- NovaEngineering ECO AE Extension **1.2.x**（编译及 Mixin 验证基线 **1.2.0**）。
- AE2 Unofficial Extended Life（编译文件 `6302098`）。
- Thaumic Energistics Extended Life（编译文件 `6321322`）。
- Thaumcraft **6.1.BETA26**、Baubles 以及 ECO 自身全部前置，包括 MMCE。
- The One Probe 可选；CTM 发光依赖整合包的渲染支持。

发布 JAR 使用 Java 8 字节码，但这不代表已验证普通 Forge / Java 8 环境。本项目在 Cleanroom 工具链构建和验证。ECO 的其他 1.2.x 版本仍需确认 Mixin 目标兼容。

0.2.0 将命名空间由 `xiaoqian_ecoessentia` 改为 `ecoessentia`，**不提供旧物品 ID 映射**。旧版物品不会自动转换；升级整合包前应先处理旧物品并更新脚本。内部 NBT 数据键仍为 `xiaoqian_ecoessentia`，这是存储格式字段，不是物品 ID。

源质类型扩展了 ECO 的枚举和 GUI 网络数据，客户端与服务端不能混用不同版本。控制器总容量包含源质；原版物品 / 流体 / 气体分类图仍只统计各自通道。

## 从源码构建

需要 JDK 25、Python 3.8+ 和网络连接。Gradle 由 wrapper 自动下载；Python 脚本仅使用标准库。

```powershell
# 将 JAVA_HOME 设置为本机 JDK 25 的安装目录
python scripts/prepare_dependencies.py
.\gradlew.bat build
```

Linux / macOS：

```bash
python3 scripts/prepare_dependencies.py
bash gradlew build
```

准备脚本下载上游固定版本 ECO JAR 并验证 SHA-256。也可手动将官方 1.2.0 发布 JAR 放入 `libs/ecoaeextension-1.2.0.jar`；详见 [libs/README.md](libs/README.md)。其余依赖由 Gradle 解析，所有依赖均不会嵌入发布 JAR。

产物位于 `build/libs/`：

- `ecoessentia-0.2.0.jar`：可安装的重混淆发布文件。
- `ecoessentia-0.2.0-sources.jar`：源码和资源。
- `ecoessentia-0.2.0-mcp.jar`：开发环境文件，勿安装到整合包。

首次构建需要下载较多 Cleanroom 依赖。下载失败时应检查网络及文件完整性。CI 已包含依赖准备、构建和产物上传；标签工作流只构建产物，不会自动发布到模组平台。

## 验证与反馈

已有 13 项核心测试，覆盖容量与种类开销、25 种上限、模拟操作、部分接受、提取释放、大于 int 的数量、未知数据保留和溢出拒绝。已进行真实 ECO 1.2.0 类的 Mixin 转换及枚举初始化检查。

发布前建议在目标整合包实测：L4 / L6 / L9 插拔及等级限制、源质终端和总线、重启后数据、状态图标和元件仓模型、GUI 与可选 TOP。自动化构建不代替游戏内测试。

反馈问题请附 Minecraft / Cleanroom / ECO / AE2 / 神秘能源版本、复现步骤，以及 `latest.log` 或崩溃报告。贡献修改请确保 `gradlew build` 通过，并说明是否完成游戏内验证。

## 来源

- [NovaEngineering ECO AE Extension](https://github.com/sddsd2332/NovaEngineering-ECOAEExtension)：ECO 存储体系及原始贴图、模型。
- 神秘能源公开 API：源质通道与配置槽。
- xiaoqian：本附属实现及源质贴图修改。

更新记录见 [CHANGELOG.md](CHANGELOG.md)。
