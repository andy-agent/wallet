#!/bin/bash
# 测试服务器部署脚本
# 服务器: 154.36.173.184
# 约束: 不使用443端口，不暴露钱包服务

set -e

# 配置
SERVER_IP="154.36.173.184"
SSH_KEY="/users/cnyirui/server/154.36.173.184/keys/154.36.173.184.pem"
REMOTE_DIR="/opt/payment-bridge"
LOCAL_DIR="$(cd "$(dirname "$0")/.." && pwd)"

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}=== Payment Bridge 测试服务器部署 ===${NC}"
echo "服务器: ${SERVER_IP}"
echo "部署目录: ${REMOTE_DIR}"
echo ""

# 检查 SSH 密钥
if [ ! -f "${SSH_KEY}" ]; then
    echo -e "${RED}错误: SSH 密钥不存在: ${SSH_KEY}${NC}"
    exit 1
fi

chmod 600 "${SSH_KEY}"

# 安装 Docker 和 Docker Compose（如未安装）
echo -e "${YELLOW}>>> 检查 Docker 安装...${NC}"
ssh -o StrictHostKeyChecking=no -i "${SSH_KEY}" root@${SERVER_IP} << 'REMOTE_SCRIPT'
    # 安装 Docker
    if ! command -v docker &> /dev/null; then
        echo "安装 Docker..."
        apt-get update
        apt-get install -y apt-transport-https ca-certificates curl gnupg lsb-release
        curl -fsSL https://download.docker.com/linux/ubuntu/gpg | gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
        echo "deb [arch=amd64 signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | tee /etc/apt/sources.list.d/docker.list > /dev/null
        apt-get update
        apt-get install -y docker-ce docker-ce-cli containerd.io
        systemctl enable docker
        systemctl start docker
    fi

    # 安装 Docker Compose
    if ! command -v docker-compose &> /dev/null; then
        echo "安装 Docker Compose..."
        curl -L "https://github.com/docker/compose/releases/download/v2.23.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
        chmod +x /usr/local/bin/docker-compose
    fi

    docker --version
    docker-compose --version
REMOTE_SCRIPT

# 创建远程目录
echo -e "${YELLOW}>>> 创建远程目录...${NC}"
ssh -o StrictHostKeyChecking=no -i "${SSH_KEY}" root@${SERVER_IP} "mkdir -p ${REMOTE_DIR}"

# 上传代码
echo -e "${YELLOW}>>> 上传代码到服务器...${NC}"
rsync -avz -e "ssh -o StrictHostKeyChecking=no -i ${SSH_KEY}" \
    --exclude='.git' \
    --exclude='__pycache__' \
    --exclude='*.pyc' \
    --exclude='.env' \
    --exclude='*.log' \
    "${LOCAL_DIR}/" root@${SERVER_IP}:${REMOTE_DIR}/

# 配置环境变量
echo -e "${YELLOW}>>> 配置环境变量...${NC}"
ssh -o StrictHostKeyChecking=no -i "${SSH_KEY}" root@${SERVER_IP} << REMOTE_SCRIPT
    cd ${REMOTE_DIR}/deploy
    
    # 如果 .env 不存在，从示例创建
    if [ ! -f .env ]; then
        cp .env.example .env
        echo -e "${YELLOW}警告: 请编辑 ${REMOTE_DIR}/deploy/.env 文件，填入正确的配置${NC}"
    fi
REMOTE_SCRIPT

# 启动服务
echo -e "${YELLOW}>>> 启动 Docker 服务...${NC}"
ssh -o StrictHostKeyChecking=no -i "${SSH_KEY}" root@${SERVER_IP} << REMOTE_SCRIPT
    cd ${REMOTE_DIR}/deploy
    
    # 停止旧服务
    docker-compose -f docker-compose.test.yml down || true
    
    # 拉取最新镜像并构建
    docker-compose -f docker-compose.test.yml pull
    docker-compose -f docker-compose.test.yml build
    
    # 启动服务
    docker-compose -f docker-compose.test.yml up -d
    
    # 等待数据库就绪
    echo "等待数据库就绪..."
    sleep 10
    
    # 执行数据库迁移
    docker-compose -f docker-compose.test.yml exec -T api alembic upgrade head || echo "迁移失败，请手动检查"
    
    # 检查服务状态
    echo -e "${GREEN}>>> 服务状态:${NC}"
    docker-compose -f docker-compose.test.yml ps
REMOTE_SCRIPT

# 健康检查
echo -e "${YELLOW}>>> 健康检查...${NC}"
sleep 5
HEALTH_STATUS=$(ssh -o StrictHostKeyChecking=no -i "${SSH_KEY}" root@${SERVER_IP} "curl -s -o /dev/null -w '%{http_code}' http://localhost:8080/healthz" || echo "000")

if [ "${HEALTH_STATUS}" == "200" ]; then
    echo -e "${GREEN}✓ 部署成功! API 健康检查通过 (http://${SERVER_IP}:8080/healthz)${NC}"
else
    echo -e "${RED}✗ 健康检查失败 (HTTP ${HEALTH_STATUS})${NC}"
    echo "请检查日志: ssh -i ${SSH_KEY} root@${SERVER_IP} 'cd ${REMOTE_DIR}/deploy && docker-compose -f docker-compose.test.yml logs'"
    exit 1
fi

echo ""
echo -e "${GREEN}=== 部署完成 ===${NC}"
echo "API 地址: http://${SERVER_IP}:8080"
echo "管理命令:"
echo "  查看日志: ssh -i ${SSH_KEY} root@${SERVER_IP} 'cd ${REMOTE_DIR}/deploy && docker-compose -f docker-compose.test.yml logs -f'"
echo "  重启服务: ssh -i ${SSH_KEY} root@${SERVER_IP} 'cd ${REMOTE_DIR}/deploy && docker-compose -f docker-compose.test.yml restart'"
echo "  进入容器: ssh -i ${SSH_KEY} root@${SERVER_IP} 'cd ${REMOTE_DIR}/deploy && docker-compose -f docker-compose.test.yml exec api bash'"
