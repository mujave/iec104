# 电网规约解析与组装

[![996.icu](https://img.shields.io/badge/link-996.icu-red.svg)](https://996.icu)
[![iec_analysis](https://badgen.net/badge/icon/iec?icon=github&label)](https://github.com/mujave/iec104)

## 项目简介

项目围绕 **IEC 104 规约（DL/T634.5104-2009）** 实现,基于 Java 语言开发。可以完成规约的报文解析和组装工作,适用于电力系统自动化中的通信协议处理场景。

## 技术栈

| 依赖 | 版本 | 用途 |
|------|------|------|
| Java | 1.8 | 开发语言 |
| Netty | 4.1.111.Final | 网络通信框架 |
| Hutool | 5.8.30 | 工具库（字节处理） |
| Lombok | 1.18.30 | 简化代码 |
| Logback | 1.2.6 | 日志框架 |

## 功能特性

### 协议解析层次

```
APDU (应用协议数据单元)
├── APCI (应用协议控制信息) → I帧/S帧/U帧
└── ASDU (应用服务数据单元) → 类型标识+传送原因+地址+信息元素
```

### 支持的帧类型

- **I帧**：信息帧,用于传输实际数据
- **S帧**：监视帧,用于流量控制
- **U帧**：无编号帧,用于控制功能（启动/停止/测试）

### 支持的类型标识符 (TI)

| 类型 | 描述 |
|------|------|
| M_SP_NA_1 | 单点遥信 |
| M_DP_NA_1 | 双点遥信 |
| M_ME_NA_1 | 遥测归一化值 |
| M_ME_NB_1 | 遥测标度化值 |
| M_ME_NC_1 | 遥测短浮点值 |
| M_ME_TF_1 | 遥测短浮点值（带时标） |
| M_SP_TB_1 | 单点遥信（带时标） |
| M_DP_TB_1 | 双点遥信（带时标） |
| C_SC_NA_1 | 遥控单命令 |
| C_DC_NA_1 | 遥控双命令 |
| C_RC_NA_1 | 升降档命令 |
| C_IC_NA_1 | 召唤命令 |
| C_CS_NA_1 | 时钟同步命令 |
| C_RP_NA_1 | 复位进程命令 |
| C_SE_NA_1/2 | 参数设置命令 |

## 项目结构

```
iec/
├── docs/                    # 文档目录（规约解析细则）
├── iec104-core/             # 核心模块
│   ├── src/main/java/com/github/mujave/iec104/core/
│   │   ├── constant/        # 常量枚举（Cot、Ti、FrameType等）
│   │   ├── net/             # Netty网络相关
│   │   ├── parser/          # 解析器核心
│   │   │   ├── frame/       # 帧结构定义
│   │   │   └── infowrapper/ # 信息包装器
│   │   └── Iec104ParserFactory.java
│   └── src/test/java/       # 测试代码
├── pom.xml                  # 父POM
└── README.md
```

## 使用方式

### 解析报文

```java
import cn.hutool.core.util.HexUtil;
import com.github.mujave.iec104.core.Iec104ParserFactory;
import com.github.mujave.iec104.core.parser.APDUParser;
import com.github.mujave.iec104.core.parser.ParserException;

public class Example {
    public static void main(String[] args) throws ParserException {
        APDUParser parser = Iec104ParserFactory.createParser();
        // 解析十六进制报文
        AIec104Frame frame = parser.analysis(HexUtil.decodeHex("680E0000000046010400010000000001"));
        System.out.println(frame.console());
    }
}
```


## 版本历史

| 版本 | 内容 |
|------|------|
| V4.0 | 重构适配 Spring Boot + Netty（进行中） |
| V2.1 | 完成104格式报文组装 |
| V2.0 | 完成101规约报文组装,修复101解析bug |
| V1.0 | 完成101和104规约的解析 |

## 联系

E-mail: mr.zhangyu.me@qq.com