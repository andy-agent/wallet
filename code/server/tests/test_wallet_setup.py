"""
测试钱包准备指南 - Test Wallet Setup Guide

本文件提供测试钱包准备和真实交易测试的指南
"""
import os
from dataclasses import dataclass

# 设置环境变量
os.environ["SOLANA_MOCK_MODE"] = "false"
os.environ["TRON_MOCK_MODE"] = "false"


@dataclass
class TestWallet:
    """测试钱包数据结构"""
    chain: str
    address: str
    private_key: str  # 注意：仅用于测试
    network: str
    has_test_tokens: bool = False


# ============================================
# Solana 测试钱包
# ============================================

SOLANA_TEST_WALLETS = {
    "devnet_faucet": {
        "address": "9xQeWvG816bUx9EPjHmaT23yvVM2ZWbrrpZb9PusVFin",
        "network": "devnet",
        "description": "Solana Devnet 活跃地址（已有余额）",
        "balance_sol": 43.73,
        "faucet_url": "https://faucet.solana.com/",
    }
}

# Solana Devnet 获取测试币步骤：
# 1. 创建钱包：使用 Phantom 或 Solflare 钱包
# 2. 切换到 Devnet 网络
# 3. 访问 https://faucet.solana.com/
# 4. 输入钱包地址，申请 5 SOL 测试币
# 5. 或使用命令行：solana airdrop 5 <ADDRESS> --url devnet

# ============================================
# Tron 测试钱包
# ============================================

TRON_TEST_WALLETS = {
    "nile_faucet": {
        "address": "TV6MuMXfmLbBqPZvBHdwFsDnQAePKC2yU5",
        "network": "nile",
        "description": "Tron Nile 测试网地址",
        "faucet_url": "https://nileex.io/join/getJoinPage",
        "usdt_contract": "TXYZopYRdj2D9XRtbG411XZZ3kpm5bGnNF",
    }
}

# Tron Nile 测试网获取测试币步骤：
# 1. 创建钱包：使用 TronLink 钱包
# 2. 切换到 Nile 测试网
# 3. 访问 https://nileex.io/join/getJoinPage
# 4. 登录并申请测试 TRX
# 5. 对于 USDT，需要在 Nile 测试网 DEX 上交换或通过合约获取


# ============================================
# 测试交易步骤
# ============================================

TEST_TRANSACTION_STEPS = """
# Solana 测试交易步骤

## 1. 准备钱包
- 安装 Phantom 钱包（浏览器插件）
- 创建新钱包或导入现有钱包
- 切换到 Devnet 网络
- 访问 https://faucet.solana.com/ 获取测试 SOL

## 2. 发送测试交易
使用命令行或钱包发送交易：

```bash
# 使用 Solana CLI
solana transfer <RECIPIENT_ADDRESS> 0.1 --url devnet --from <KEYPAIR>
```

或在 Phantom 钱包中：
- 点击 Send
- 输入接收地址
- 输入金额（如 0.1 SOL）
- 确认交易

## 3. 验证交易检测
运行测试：
```bash
python -m pytest tests/test_real_blockchain_connection.py::TestRealPaymentDetection -v -s
```

# Tron 测试交易步骤

## 1. 准备钱包
- 安装 TronLink 钱包（浏览器插件）
- 创建新钱包或导入现有钱包
- 切换到 Nile 测试网
- 访问 https://nileex.io/join/getJoinPage 获取测试 TRX

## 2. 获取测试 USDT
- 在 Nile 测试网上找到 USDT 合约
- 使用合约的 mint 功能获取测试 USDT
- 或在测试网 DEX 上交换

## 3. 发送测试交易
在 TronLink 钱包中：
- 选择 USDT 代币
- 点击 Send
- 输入接收地址
- 输入金额（如 10 USDT）
- 确认交易

## 4. 验证交易检测
运行测试：
```bash
python -m pytest tests/test_real_blockchain_connection.py::TestRealPaymentDetection -v -s
```
"""


def print_setup_guide():
    """打印设置指南"""
    print("=" * 70)
    print("区块链测试钱包准备指南")
    print("=" * 70)
    
    print("\n## Solana Devnet 测试钱包")
    print("-" * 70)
    for name, info in SOLANA_TEST_WALLETS.items():
        print(f"\n钱包: {name}")
        print(f"  地址: {info['address']}")
        print(f"  网络: {info['network']}")
        print(f"  描述: {info['description']}")
        if 'balance_sol' in info:
            print(f"  当前余额: {info['balance_sol']} SOL")
        print(f"  水龙头: {info['faucet_url']}")
    
    print("\n## Tron Nile 测试网钱包")
    print("-" * 70)
    for name, info in TRON_TEST_WALLETS.items():
        print(f"\n钱包: {name}")
        print(f"  地址: {info['address']}")
        print(f"  网络: {info['network']}")
        print(f"  描述: {info['description']}")
        print(f"  USDT 合约: {info['usdt_contract']}")
        print(f"  水龙头: {info['faucet_url']}")
    
    print("\n" + "=" * 70)
    print("测试交易步骤")
    print("=" * 70)
    print(TEST_TRANSACTION_STEPS)


if __name__ == "__main__":
    print_setup_guide()
