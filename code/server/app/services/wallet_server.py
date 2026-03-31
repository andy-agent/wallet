#!/usr/bin/env python3
"""
区块链钱包服务 - Wallet Server
提供安全的区块链操作服务，仅内部网络访问
"""
import os
import sys
import json
import asyncio
from typing import Optional, Dict, Any
from datetime import datetime, timezone

from fastapi import FastAPI, HTTPException, BackgroundTasks
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field
import uvicorn

# Add parent directory to path
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from app.core.logging import configure_logging, get_logger
from app.integrations.solana import SolanaClient
from app.integrations.tron import TronClient

# Configure logging
configure_logging(debug=os.getenv("DEBUG", "false").lower() == "true")
logger = get_logger(__name__)

# Initialize FastAPI app
app = FastAPI(
    title="Payment Bridge Wallet Service",
    version="1.0.0",
    description="Internal blockchain wallet operations service",
    docs_url=None,  # Disable docs for security
    redoc_url=None,
)

# No CORS - internal service only

# Initialize blockchain clients
solana_client: Optional[SolanaClient] = None
tron_client: Optional[TronClient] = None


class GenerateAddressRequest(BaseModel):
    chain: str = Field(..., description="区块链类型: solana 或 tron")
    index: int = Field(0, description="地址索引")


class GenerateAddressResponse(BaseModel):
    address: str
    chain: str
    public_key: Optional[str] = None


class GetBalanceRequest(BaseModel):
    chain: str
    address: str
    token: Optional[str] = None  # For TRC20 tokens


class GetBalanceResponse(BaseModel):
    chain: str
    address: str
    balance: float
    token: Optional[str] = None
    timestamp: str


class DetectPaymentRequest(BaseModel):
    chain: str
    address: str
    expected_amount: float
    memo: Optional[str] = None
    min_confirmations: int = 1


class DetectPaymentResponse(BaseModel):
    found: bool
    chain: str
    address: str
    tx_hash: Optional[str] = None
    from_address: Optional[str] = None
    amount: Optional[float] = None
    confirmations: Optional[int] = None
    status: Optional[str] = None  # pending, confirmed


class HealthResponse(BaseModel):
    status: str
    timestamp: str
    services: Dict[str, bool]


@app.on_event("startup")
async def startup():
    """Initialize blockchain clients"""
    global solana_client, tron_client
    
    logger.info("wallet_service_starting")
    
    # Initialize Solana client
    solana_rpc = os.getenv("SOLANA_RPC_URL", "https://api.devnet.solana.com")
    solana_mock = os.getenv("SOLANA_MOCK_MODE", "false").lower() == "true"
    solana_client = SolanaClient(rpc_url=solana_rpc, mock_mode=solana_mock)
    logger.info("solana_client_initialized", rpc_url=solana_rpc, mock_mode=solana_mock)
    
    # Initialize Tron client
    tron_rpc = os.getenv("TRON_RPC_URL", "https://nile.trongrid.io")
    tron_mock = os.getenv("TRON_MOCK_MODE", "false").lower() == "true"
    tron_contract = os.getenv("TRON_USDT_CONTRACT", "TXYZopYRdj2D9XRtbG411XZZ3kpm5bGnNF")
    tron_client = TronClient(
        rpc_url=tron_rpc,
        usdt_contract=tron_contract,
        mock_mode=tron_mock
    )
    logger.info("tron_client_initialized", rpc_url=tron_rpc, mock_mode=tron_mock)


@app.on_event("shutdown")
async def shutdown():
    """Cleanup resources"""
    logger.info("wallet_service_shutting_down")


@app.get("/health", response_model=HealthResponse)
async def health_check():
    """Health check endpoint"""
    return HealthResponse(
        status="healthy",
        timestamp=datetime.now(timezone.utc).isoformat(),
        services={
            "solana": solana_client is not None,
            "tron": tron_client is not None,
        }
    )


@app.post("/address/generate", response_model=GenerateAddressResponse)
async def generate_address(request: GenerateAddressRequest):
    """Generate a new wallet address"""
    logger.info("generate_address", chain=request.chain, index=request.index)
    
    if request.chain.lower() == "solana":
        # Generate deterministic Solana address from HD wallet
        # For now, return a mock address - in production, use proper HD wallet
        import secrets
        address = secrets.token_hex(32)[:43]  # Mock address
        return GenerateAddressResponse(
            address=address,
            chain="solana",
            public_key=secrets.token_hex(32)
        )
    elif request.chain.lower() == "tron":
        # Generate Tron address
        import secrets
        address = "T" + secrets.token_hex(20)[:33].upper()  # Mock address
        return GenerateAddressResponse(
            address=address,
            chain="tron",
            public_key=secrets.token_hex(32)
        )
    else:
        raise HTTPException(status_code=400, detail=f"Unsupported chain: {request.chain}")


@app.post("/balance", response_model=GetBalanceResponse)
async def get_balance(request: GetBalanceRequest):
    """Get balance for an address"""
    logger.info("get_balance", chain=request.chain, address=request.address)
    
    try:
        if request.chain.lower() == "solana":
            if not solana_client:
                raise HTTPException(status_code=503, detail="Solana client not initialized")
            
            balance = await solana_client.get_balance(request.address)
            return GetBalanceResponse(
                chain="solana",
                address=request.address,
                balance=balance,
                timestamp=datetime.now(timezone.utc).isoformat()
            )
            
        elif request.chain.lower() == "tron":
            if not tron_client:
                raise HTTPException(status_code=503, detail="Tron client not initialized")
            
            if request.token and request.token.upper() == "USDT":
                balance = await tron_client.get_usdt_balance(request.address)
            else:
                balance = await tron_client.get_trx_balance(request.address)
            
            return GetBalanceResponse(
                chain="tron",
                address=request.address,
                balance=balance,
                token=request.token,
                timestamp=datetime.now(timezone.utc).isoformat()
            )
        else:
            raise HTTPException(status_code=400, detail=f"Unsupported chain: {request.chain}")
            
    except Exception as e:
        logger.error("balance_check_failed", chain=request.chain, address=request.address, error=str(e))
        raise HTTPException(status_code=500, detail=f"Balance check failed: {str(e)}")


@app.post("/payment/detect", response_model=DetectPaymentResponse)
async def detect_payment(request: DetectPaymentRequest):
    """Detect payment on blockchain"""
    logger.info(
        "detect_payment",
        chain=request.chain,
        address=request.address,
        expected_amount=request.expected_amount
    )
    
    try:
        if request.chain.lower() == "solana":
            if not solana_client:
                raise HTTPException(status_code=503, detail="Solana client not initialized")
            
            result = await solana_client.detect_payment(
                address=request.address,
                expected_amount=request.expected_amount,
                memo=request.memo,
                min_confirmations=request.min_confirmations
            )
            
            return DetectPaymentResponse(
                found=result.found,
                chain="solana",
                address=request.address,
                tx_hash=result.tx_hash if result.found else None,
                from_address=result.from_address if result.found else None,
                amount=result.amount if result.found else None,
                confirmations=result.confirmations if result.found else None,
                status=result.status if result.found else None
            )
            
        elif request.chain.lower() == "tron":
            if not tron_client:
                raise HTTPException(status_code=503, detail="Tron client not initialized")
            
            result = await tron_client.detect_payment(
                address=request.address,
                expected_amount=request.expected_amount,
                min_confirmations=request.min_confirmations
            )
            
            return DetectPaymentResponse(
                found=result.found,
                chain="tron",
                address=request.address,
                tx_hash=result.tx_hash if result.found else None,
                from_address=result.from_address if result.found else None,
                amount=result.amount if result.found else None,
                confirmations=result.confirmations if result.found else None,
                status=result.status if result.found else None
            )
        else:
            raise HTTPException(status_code=400, detail=f"Unsupported chain: {request.chain}")
            
    except Exception as e:
        logger.error(
            "payment_detection_failed",
            chain=request.chain,
            address=request.address,
            error=str(e)
        )
        raise HTTPException(status_code=500, detail=f"Payment detection failed: {str(e)}")


@app.get("/")
async def root():
    """Root endpoint"""
    return {
        "name": "Payment Bridge Wallet Service",
        "version": "1.0.0",
        "mode": "internal"
    }


if __name__ == "__main__":
    port = int(os.getenv("WALLET_PORT", "9000"))
    host = os.getenv("WALLET_HOST", "0.0.0.0")
    
    logger.info("starting_wallet_server", host=host, port=port)
    uvicorn.run(app, host=host, port=port, log_level="info")
