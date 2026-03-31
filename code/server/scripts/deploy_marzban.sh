#!/bin/bash
# Marzban 部署脚本
# 用于在服务器上快速部署 Marzban VPN 面板

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 配置变量
MARZBAN_VERSION="latest"
MARZBAN_DIR="/opt/marzban"
MARZBAN_DATA="$MARZBAN_DIR/data"

log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查是否为 root 用户
check_root() {
    if [[ $EUID -ne 0 ]]; then
        log_error "请以 root 用户运行此脚本"
        exit 1
    fi
}

# 检查系统要求
check_requirements() {
    log_info "检查系统要求..."
    
    # 检查 Docker
    if ! command -v docker &> /dev/null; then
        log_warn "Docker 未安装，正在安装..."
        curl -fsSL https://get.docker.com | sh
        systemctl enable docker
        systemctl start docker
    fi
    
    # 检查 Docker Compose
    if ! command -v docker-compose &> /dev/null; then
        log_warn "Docker Compose 未安装，正在安装..."
        curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
        chmod +x /usr/local/bin/docker-compose
    fi
    
    log_info "系统要求检查完成"
}

# 创建目录结构
setup_directories() {
    log_info "创建目录结构..."
    
    mkdir -p $MARZBAN_DATA
    mkdir -p $MARZBAN_DIR/xray-configs
    mkdir -p $MARZBAN_DATA/certs
    
    log_info "目录创建完成: $MARZBAN_DIR"
}

# 创建 docker-compose.yml
create_docker_compose() {
    log_info "创建 docker-compose.yml..."
    
    cat > $MARZBAN_DIR/docker-compose.yml << 'EOF'
version: "3.8"

services:
  marzban:
    image: gozargah/marzban:latest
    restart: always
    env_file:
      - .env
    ports:
      - "8000:8000"
      - "443:443"
      - "80:80"
    volumes:
      - ./data:/var/lib/marzban
      - ./xray-configs:/var/lib/marzban/xray_config
      - ./data/certs:/var/lib/marzban/certs
    network_mode: host
    
  marzban-db:
    image: mysql:8.0
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: marzban_root_password
      MYSQL_DATABASE: marzban
      MYSQL_USER: marzban
      MYSQL_PASSWORD: marzban_password
    volumes:
      - ./mysql-data:/var/lib/mysql
    ports:
      - "3306:3306"
EOF

    log_info "docker-compose.yml 创建完成"
}

# 创建环境配置文件
create_env_file() {
    log_info "创建环境配置文件..."
    
    # 生成随机密码
    ADMIN_PASSWORD=$(openssl rand -base64 32 | tr -dc 'a-zA-Z0-9' | head -c 16)
    JWT_SECRET=$(openssl rand -base64 32)
    
    cat > $MARZBAN_DIR/.env << EOF
# Marzban 配置
UVICORN_HOST=0.0.0.0
UVICORN_PORT=8000
UVICORN_UDS=/var/lib/marzban/marzban.sock

# 管理员配置
SUDO_USERNAME=admin
SUDO_PASSWORD=$ADMIN_PASSWORD

# JWT 密钥
JWT_SECRET=$JWT_SECRET

# 数据库配置 (使用 MySQL)
MYSQL_ROOT_PASSWORD=marzban_root_password
MYSQL_DATABASE=marzban
MYSQL_USER=marzban
MYSQL_PASSWORD=marzban_password

# Xray 配置路径
XRAY_JSON=/var/lib/marzban/xray_config.json

# 订阅配置
SUBSCRIPTION_URL_PREFIX=
EOF

    log_info "环境配置文件创建完成"
    log_info "管理员用户名: admin"
    log_info "管理员密码: $ADMIN_PASSWORD"
    
    # 保存密码到文件
    echo "Admin Username: admin" > $MARZBAN_DIR/admin_credentials.txt
    echo "Admin Password: $ADMIN_PASSWORD" >> $MARZBAN_DIR/admin_credentials.txt
    chmod 600 $MARZBAN_DIR/admin_credentials.txt
    
    log_info "凭据已保存到: $MARZBAN_DIR/admin_credentials.txt"
}

# 创建 Xray 配置
create_xray_config() {
    log_info "创建 Xray 配置..."
    
    cat > $MARZBAN_DIR/xray-configs/xray_config.json << 'EOF'
{
  "log": {
    "access": "/var/lib/marzban/logs/access.log",
    "error": "/var/lib/marzban/logs/error.log",
    "loglevel": "warning"
  },
  "api": {
    "services": [
      "HandlerService",
      "LoggerService",
      "StatsService"
    ],
    "tag": "api"
  },
  "inbounds": [
    {
      "tag": "api",
      "listen": "127.0.0.1",
      "port": 10085,
      "protocol": "dokodemo-door",
      "settings": {
        "address": "127.0.0.1"
      }
    },
    {
      "tag": "VMess TCP",
      "listen": "0.0.0.0",
      "port": 8080,
      "protocol": "vmess",
      "settings": {
        "clients": []
      },
      "streamSettings": {
        "network": "tcp"
      },
      "sniffing": {
        "enabled": true,
        "destOverride": ["http", "tls"]
      }
    },
    {
      "tag": "VLESS TCP XTLS",
      "listen": "0.0.0.0",
      "port": 8443,
      "protocol": "vless",
      "settings": {
        "clients": [],
        "decryption": "none"
      },
      "streamSettings": {
        "network": "tcp",
        "security": "tls",
        "tlsSettings": {
          "certificates": []
        }
      },
      "sniffing": {
        "enabled": true,
        "destOverride": ["http", "tls"]
      }
    }
  ],
  "outbounds": [
    {
      "protocol": "freedom",
      "settings": {},
      "tag": "direct"
    },
    {
      "protocol": "blackhole",
      "settings": {
        "response": {
          "type": "http"
        }
      },
      "tag": "blocked"
    }
  ],
  "routing": {
    "rules": [
      {
        "inboundTag": ["api"],
        "outboundTag": "api",
        "type": "field"
      },
      {
        "ip": ["geoip:private"],
        "outboundTag": "blocked",
        "type": "field"
      }
    ]
  },
  "stats": {},
  "policy": {
    "levels": {
      "0": {
        "statsUserUplink": true,
        "statsUserDownlink": true
      }
    },
    "system": {
      "statsInboundUplink": true,
      "statsInboundDownlink": true
    }
  }
}
EOF

    log_info "Xray 配置创建完成"
}

# 启动服务
start_services() {
    log_info "启动 Marzban 服务..."
    
    cd $MARZBAN_DIR
    
    # 拉取最新镜像
    docker-compose pull
    
    # 启动服务
    docker-compose up -d
    
    # 等待服务启动
    log_info "等待服务启动..."
    sleep 10
    
    # 检查服务状态
    if docker-compose ps | grep -q "Up"; then
        log_info "Marzban 服务启动成功!"
    else
        log_error "Marzban 服务启动失败，请检查日志: docker-compose logs"
        exit 1
    fi
}

# 显示连接信息
show_connection_info() {
    log_info ""
    log_info "========================================"
    log_info "Marzban 部署完成!"
    log_info "========================================"
    log_info ""
    log_info "面板地址: http://$(hostname -I | awk '{print $1}'):8000/dashboard"
    log_info "API 地址: http://$(hostname -I | awk '{print $1}'):8000/api"
    log_info ""
    log_info "管理员凭据:"
    cat $MARZBAN_DIR/admin_credentials.txt
    log_info ""
    log_info "配置文件位置: $MARZBAN_DIR"
    log_info ""
    log_info "常用命令:"
    log_info "  查看日志: docker-compose -f $MARZBAN_DIR/docker-compose.yml logs -f"
    log_info "  重启服务: docker-compose -f $MARZBAN_DIR/docker-compose.yml restart"
    log_info "  停止服务: docker-compose -f $MARZBAN_DIR/docker-compose.yml down"
    log_info ""
    log_info "========================================"
}

# 主函数
main() {
    log_info "开始部署 Marzban..."
    
    check_root
    check_requirements
    setup_directories
    create_docker_compose
    create_env_file
    create_xray_config
    start_services
    show_connection_info
    
    log_info "部署完成!"
}

# 运行主函数
main "$@"
