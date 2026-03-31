"""
Application configuration using pydantic-settings
"""
from decimal import Decimal
from functools import lru_cache
from typing import List, Optional

from pydantic import Field
from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding="utf-8",
        case_sensitive=False,
        extra="ignore"
    )

    # App
    app_name: str = "v2rayng-payment-bridge"
    app_version: str = "1.0.0"
    debug: bool = Field(default=False, alias="DEBUG")
    environment: str = Field(default="development", alias="APP_ENV")
    
    # Server
    host: str = "0.0.0.0"
    port: int = 8000
    workers: int = 1
    
    # Database
    database_url: str = Field(alias="DATABASE_URL")
    database_echo: bool = False
    
    # Redis
    redis_url: str = Field(default="redis://localhost:6379/0", alias="REDIS_URL")
    
    # JWT
    jwt_secret: str = Field(alias="JWT_SECRET")
    jwt_algorithm: str = "HS256"
    jwt_access_token_expire_minutes: int = 60  # 1 hour
    jwt_refresh_token_expire_days: int = 90
    
    # Encryption
    encryption_master_key: str = Field(alias="ENCRYPTION_MASTER_KEY")
    
    # Marzban
    marzban_base_url: str = Field(alias="MARZBAN_BASE_URL")
    marzban_admin_username: str = Field(alias="MARZBAN_ADMIN_USERNAME")
    marzban_admin_password: str = Field(alias="MARZBAN_ADMIN_PASSWORD")
    
    # Solana
    solana_rpc_url: str = Field(
        default="https://api.devnet.solana.com",
        alias="SOLANA_RPC_URL"
    )
    solana_confirmations: int = 12
    solana_mock_mode: bool = Field(default=False, alias="SOLANA_MOCK_MODE")
    
    # SPL Token Configuration
    spl_token_mint: str = Field(
        default="8zFP8GeszFz7FvuHesguekTxDjm4KLsJEYBZTKyMLEoE",
        alias="SPL_TOKEN_MINT"
    )
    spl_token_decimals: int = Field(default=6, alias="SPL_TOKEN_DECIMALS")
    spl_token_symbol: str = Field(default="USDC", alias="SPL_TOKEN_SYMBOL")
    spl_token_enabled: bool = Field(default=True, alias="SPL_TOKEN_ENABLED")
    
    # Tron
    tron_rpc_url: str = Field(
        default="https://nile.trongrid.io",
        alias="TRON_RPC_URL"
    )
    tron_confirmations: int = 19
    tron_usdt_contract: str = Field(
        default="TXYZopYRdj2D9XRtbG411XZZ3kpm5bGnNF",  # Nile testnet USDT
        alias="TRON_USDT_CONTRACT"
    )
    tron_mock_mode: bool = Field(default=False, alias="TRON_MOCK_MODE")
    
    # Order
    order_expire_minutes: int = 15
    order_amount_tolerance: float = 0.001  # 0.1%
    
    # Worker
    worker_enabled: bool = True
    worker_scan_interval_seconds: int = 10
    
    # Admin
    admin_token: Optional[str] = Field(default=None, alias="ADMIN_TOKEN")
    
    # Pricing
    base_price_usd: Decimal = Field(default=Decimal("3.00"), alias="BASE_PRICE_USD")
    
    @property
    def is_development(self) -> bool:
        return self.environment == "development"
    
    @property
    def is_production(self) -> bool:
        return self.environment == "production"


@lru_cache()
def get_settings() -> Settings:
    return Settings()
