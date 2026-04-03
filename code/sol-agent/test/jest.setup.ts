// E2E 测试设置
// 使用 mock 模式避免慢速 RPC 调用
process.env.SOLANA_RPC_MODE = 'mock';
process.env.INTERNAL_AUTH_TOKEN = 'test-token';
