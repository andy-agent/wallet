"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
var PaymentService_1;
Object.defineProperty(exports, "__esModule", { value: true });
exports.PaymentService = void 0;
const common_1 = require("@nestjs/common");
const web3_js_1 = require("@solana/web3.js");
const solana_rpc_service_1 = require("../solana/solana.rpc.service");
const DEFAULT_NETWORK_CODE = 'solana-mainnet';
const MAINNET_USDT_MINT = 'Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB';
const SOL_DECIMALS = 9;
const USDT_DECIMALS = 6;
let PaymentService = PaymentService_1 = class PaymentService {
    constructor(solanaRpc) {
        this.solanaRpc = solanaRpc;
        this.logger = new common_1.Logger(PaymentService_1.name);
        this.paymentStore = new Map();
    }
    async getPaymentStatus(address, networkCode) {
        const effectiveNetworkCode = networkCode ?? DEFAULT_NETWORK_CODE;
        try {
            const balanceInfo = await this.solanaRpc.getBalance(address, effectiveNetworkCode);
            const txInfo = await this.solanaRpc.getRecentTransactions(address, effectiveNetworkCode, 5);
            const hasBalance = balanceInfo.balance > 0;
            const hasTransactions = txInfo.signatures.length > 0;
            let status = 'pending';
            if (hasBalance && hasTransactions) {
                status = 'received';
            }
            else if (hasBalance) {
                status = 'received';
            }
            const result = {
                address,
                networkCode: effectiveNetworkCode,
                status,
                receivedAmount: balanceInfo.balanceInSOL,
                expectedAmount: null,
                txHash: hasTransactions ? txInfo.signatures[0] : null,
                confirmations: hasTransactions ? 1 : 0,
                balance: balanceInfo.balance,
                recentTxCount: txInfo.signatures.length,
                updatedAt: new Date().toISOString(),
            };
            this.paymentStore.set(address, result);
            return result;
        }
        catch (error) {
            this.logger.error(`Failed to get payment status for ${address}:`, error);
            return {
                address,
                networkCode: effectiveNetworkCode,
                status: 'error',
                receivedAmount: '0',
                expectedAmount: null,
                txHash: null,
                confirmations: 0,
                error: error instanceof Error ? error.message : String(error),
                updatedAt: new Date().toISOString(),
            };
        }
    }
    async detectPayment(body) {
        const effectiveNetworkCode = body.networkCode ?? DEFAULT_NETWORK_CODE;
        try {
            this.logger.log(`Detecting payment for ${body.address} on ${effectiveNetworkCode}`);
            const balanceInfo = await this.solanaRpc.getBalance(body.address, effectiveNetworkCode);
            const txInfo = await this.solanaRpc.getRecentTransactions(body.address, effectiveNetworkCode, 10);
            const receivedAmount = parseFloat(balanceInfo.balanceInSOL);
            const expectedAmount = body.expectedAmount
                ? parseFloat(body.expectedAmount)
                : null;
            let status = 'pending';
            if (expectedAmount !== null && receivedAmount >= expectedAmount) {
                status = 'confirmed';
            }
            else if (receivedAmount > 0) {
                status = 'partial';
            }
            const result = {
                address: body.address,
                networkCode: effectiveNetworkCode,
                status,
                receivedAmount: balanceInfo.balanceInSOL,
                expectedAmount: body.expectedAmount ?? null,
                txHash: txInfo.signatures.length > 0 ? txInfo.signatures[0] : null,
                confirmations: txInfo.signatures.length,
                recentTransactions: txInfo.signatures,
                updatedAt: new Date().toISOString(),
            };
            this.paymentStore.set(body.address, result);
            this.logger.log(`Payment detection result for ${body.address}: ${status}, ${balanceInfo.balanceInSOL} SOL`);
            return result;
        }
        catch (error) {
            this.logger.error(`Payment detection failed for ${body.address}:`, error);
            return {
                address: body.address,
                networkCode: effectiveNetworkCode,
                status: 'error',
                receivedAmount: '0',
                expectedAmount: body.expectedAmount ?? null,
                txHash: null,
                confirmations: 0,
                error: error instanceof Error ? error.message : String(error),
                updatedAt: new Date().toISOString(),
            };
        }
    }
    async verifyTransaction(body) {
        const networkCode = body.networkCode ?? DEFAULT_NETWORK_CODE;
        const recipientAddress = this.assertPublicKey(body.recipientAddress, 'recipientAddress');
        const asset = this.resolveAsset(body, networkCode);
        const expectedAmountRaw = this.toBaseUnits(body.expectedAmount, asset.decimals, 'expectedAmount');
        try {
            const txResult = await this.solanaRpc.getParsedTransaction(body.signature, networkCode);
            if (!txResult.transaction) {
                return this.buildVerificationResult({
                    body,
                    asset,
                    recipientAddress,
                    expectedAmountRaw,
                    status: 'pending',
                    receivedAmountRaw: 0n,
                    matchedAccounts: [],
                    slot: null,
                    blockTime: null,
                });
            }
            const parsedTx = txResult.transaction;
            if (parsedTx.meta?.err) {
                return this.buildVerificationResult({
                    body,
                    asset,
                    recipientAddress,
                    expectedAmountRaw,
                    status: 'failed',
                    receivedAmountRaw: 0n,
                    matchedAccounts: [],
                    slot: parsedTx.slot,
                    blockTime: parsedTx.blockTime ?? null,
                    error: JSON.stringify(parsedTx.meta.err),
                });
            }
            const paymentMatch = asset.assetKind === 'NATIVE_SOL'
                ? this.extractSolReceipt(parsedTx, recipientAddress)
                : this.extractSplReceipt(parsedTx, recipientAddress, asset.mintAddress);
            const status = paymentMatch.receivedAmountRaw >= expectedAmountRaw &&
                paymentMatch.receivedAmountRaw > 0n
                ? 'verified'
                : 'mismatch';
            return this.buildVerificationResult({
                body,
                asset,
                recipientAddress,
                expectedAmountRaw,
                status,
                receivedAmountRaw: paymentMatch.receivedAmountRaw,
                matchedAccounts: paymentMatch.matchedAccounts,
                slot: parsedTx.slot,
                blockTime: parsedTx.blockTime ?? null,
            });
        }
        catch (error) {
            this.logger.error(`Transaction verification failed for ${body.signature}:`, error);
            throw error;
        }
    }
    async scanIncomingTransfers(body) {
        const networkCode = body.networkCode ?? DEFAULT_NETWORK_CODE;
        const collectionAddress = this.assertPublicKey(body.collectionAddress, 'collectionAddress');
        const asset = this.resolveAsset(body, networkCode);
        const limit = body.limit ?? 20;
        const minSlotExclusive = body.minSlotExclusive;
        const signatureScanSources = await this.collectSignatureScanSources({
            collectionAddress,
            networkCode,
            assetKind: asset.assetKind,
            mintAddress: asset.mintAddress,
            limit,
            beforeSignature: body.beforeSignature,
        });
        const mergedSignatures = this.mergeSignatureSources(signatureScanSources);
        const filteredSignatures = minSlotExclusive === undefined
            ? mergedSignatures
            : mergedSignatures.filter((signatureInfo) => signatureInfo.slot !== null &&
                signatureInfo.slot > minSlotExclusive);
        if (filteredSignatures.length === 0) {
            return {
                collectionAddress,
                networkCode,
                assetCode: asset.assetCode,
                assetKind: asset.assetKind,
                mintAddress: asset.mintAddress,
                decimals: asset.decimals,
                scannedSignatures: 0,
                matchedTransfers: 0,
                nextBeforeSignature: null,
                items: [],
            };
        }
        const parsedTransactions = await this.solanaRpc.getParsedTransactions(filteredSignatures.map((item) => item.signature), networkCode);
        const transactionMap = new Map(parsedTransactions.transactions.map((item) => [item.signature, item]));
        const items = filteredSignatures
            .map((signatureInfo) => {
            if (signatureInfo.err) {
                return null;
            }
            const parsedTx = transactionMap.get(signatureInfo.signature)?.transaction ?? null;
            if (!parsedTx || parsedTx.meta?.err) {
                return null;
            }
            const paymentMatch = asset.assetKind === 'NATIVE_SOL'
                ? this.extractSolReceipt(parsedTx, collectionAddress)
                : this.extractSplReceipt(parsedTx, collectionAddress, asset.mintAddress);
            if (paymentMatch.receivedAmountRaw <= 0n) {
                return null;
            }
            return {
                signature: signatureInfo.signature,
                slot: signatureInfo.slot ?? parsedTx.slot ?? null,
                blockTime: signatureInfo.blockTime ?? parsedTx.blockTime ?? null,
                confirmationStatus: signatureInfo.confirmationStatus,
                collectionAddress,
                assetCode: asset.assetCode,
                assetKind: asset.assetKind,
                mintAddress: asset.mintAddress,
                decimals: asset.decimals,
                amount: this.fromBaseUnits(paymentMatch.receivedAmountRaw, asset.decimals),
                amountRaw: paymentMatch.receivedAmountRaw.toString(),
                matchedAccounts: paymentMatch.matchedAccounts,
            };
        })
            .filter((item) => item !== null);
        return {
            collectionAddress,
            networkCode,
            assetCode: asset.assetCode,
            assetKind: asset.assetKind,
            mintAddress: asset.mintAddress,
            decimals: asset.decimals,
            scannedSignatures: filteredSignatures.length,
            matchedTransfers: items.length,
            nextBeforeSignature: filteredSignatures[filteredSignatures.length - 1]?.signature ?? null,
            items,
        };
    }
    async collectSignatureScanSources(input) {
        const ownerSignatures = await this.solanaRpc.getSignatureInfos(input.collectionAddress, input.networkCode, {
            limit: input.limit,
            beforeSignature: input.beforeSignature,
        });
        const sources = [
            {
                sourceAddress: input.collectionAddress,
                signatures: ownerSignatures.signatures,
            },
        ];
        if (input.assetKind !== 'SPL_TOKEN' || !input.mintAddress) {
            return sources;
        }
        const associatedTokenAddress = this.solanaRpc.deriveAssociatedTokenAddress(input.collectionAddress, input.mintAddress);
        const ataSignatures = await this.solanaRpc.getSignatureInfos(associatedTokenAddress, input.networkCode, {
            limit: input.limit,
            beforeSignature: input.beforeSignature,
        });
        sources.push({
            sourceAddress: associatedTokenAddress,
            signatures: ataSignatures.signatures,
        });
        return sources;
    }
    mergeSignatureSources(sources) {
        const merged = new Map();
        for (const source of sources) {
            for (const signatureInfo of source.signatures) {
                if (!merged.has(signatureInfo.signature)) {
                    merged.set(signatureInfo.signature, signatureInfo);
                }
            }
        }
        return Array.from(merged.values()).sort((left, right) => {
            const leftSlot = left.slot ?? Number.MIN_SAFE_INTEGER;
            const rightSlot = right.slot ?? Number.MIN_SAFE_INTEGER;
            if (rightSlot !== leftSlot) {
                return rightSlot - leftSlot;
            }
            const leftBlockTime = left.blockTime ?? Number.MIN_SAFE_INTEGER;
            const rightBlockTime = right.blockTime ?? Number.MIN_SAFE_INTEGER;
            if (rightBlockTime !== leftBlockTime) {
                return rightBlockTime - leftBlockTime;
            }
            return right.signature.localeCompare(left.signature);
        });
    }
    buildVerificationResult(input) {
        return {
            signature: input.body.signature,
            networkCode: input.body.networkCode ?? DEFAULT_NETWORK_CODE,
            status: input.status,
            recipientAddress: input.recipientAddress,
            assetCode: input.asset.assetCode,
            assetKind: input.asset.assetKind,
            mintAddress: input.asset.mintAddress,
            decimals: input.asset.decimals,
            expectedAmount: input.body.expectedAmount,
            expectedAmountRaw: input.expectedAmountRaw.toString(),
            receivedAmount: this.fromBaseUnits(input.receivedAmountRaw, input.asset.decimals),
            receivedAmountRaw: input.receivedAmountRaw.toString(),
            recipientMatched: input.receivedAmountRaw > 0n,
            amountSatisfied: input.receivedAmountRaw >= input.expectedAmountRaw,
            matchedAccounts: input.matchedAccounts,
            slot: input.slot,
            blockTime: input.blockTime,
            error: input.error,
            verifiedAt: new Date().toISOString(),
        };
    }
    resolveAsset(body, networkCode) {
        const assetCode = body.assetCode.trim().toUpperCase();
        if (!assetCode) {
            throw new common_1.BadRequestException('assetCode is required');
        }
        if (assetCode === 'SOL') {
            return {
                assetCode,
                assetKind: 'NATIVE_SOL',
                mintAddress: null,
                decimals: SOL_DECIMALS,
            };
        }
        let mintAddress = body.mintAddress?.trim() ?? null;
        if (!mintAddress &&
            assetCode === 'USDT' &&
            networkCode === DEFAULT_NETWORK_CODE) {
            mintAddress = MAINNET_USDT_MINT;
        }
        if (!mintAddress) {
            throw new common_1.BadRequestException('mintAddress is required for SPL token verification');
        }
        const decimals = body.assetDecimals ?? (assetCode === 'USDT' ? USDT_DECIMALS : undefined);
        if (decimals === undefined) {
            throw new common_1.BadRequestException('assetDecimals is required for non-USDT SPL token verification');
        }
        return {
            assetCode,
            assetKind: 'SPL_TOKEN',
            mintAddress: this.assertPublicKey(mintAddress, 'mintAddress'),
            decimals,
        };
    }
    extractSolReceipt(tx, recipientAddress) {
        const accountKeys = this.getAccountKeys(tx);
        const recipientIndex = accountKeys.findIndex((accountKey) => accountKey === recipientAddress);
        if (recipientIndex < 0) {
            return {
                matchedAccounts: [],
                receivedAmountRaw: 0n,
            };
        }
        const preBalance = BigInt(tx.meta?.preBalances?.[recipientIndex] ?? 0);
        const postBalance = BigInt(tx.meta?.postBalances?.[recipientIndex] ?? 0);
        const delta = postBalance - preBalance;
        return {
            matchedAccounts: delta > 0n ? [recipientAddress] : [],
            receivedAmountRaw: delta > 0n ? delta : 0n,
        };
    }
    extractSplReceipt(tx, recipientAddress, mintAddress) {
        const accountKeys = this.getAccountKeys(tx);
        const preBalances = this.normalizeTokenBalances(tx.meta?.preTokenBalances, accountKeys);
        const postBalances = this.normalizeTokenBalances(tx.meta?.postTokenBalances, accountKeys);
        const matchedAccounts = new Set();
        let receivedAmountRaw = 0n;
        const candidateIndices = new Set([
            ...Array.from(preBalances.keys()),
            ...Array.from(postBalances.keys()),
        ]);
        for (const accountIndex of candidateIndices) {
            const before = preBalances.get(accountIndex);
            const after = postBalances.get(accountIndex);
            const balance = after ?? before;
            if (!balance || balance.mint !== mintAddress) {
                continue;
            }
            const accountMatchesRecipient = balance.accountAddress === recipientAddress ||
                balance.owner === recipientAddress;
            if (!accountMatchesRecipient) {
                continue;
            }
            const preAmount = before?.amount ?? 0n;
            const postAmount = after?.amount ?? 0n;
            const delta = postAmount - preAmount;
            if (delta > 0n) {
                receivedAmountRaw += delta;
                matchedAccounts.add(balance.accountAddress);
            }
        }
        return {
            matchedAccounts: Array.from(matchedAccounts),
            receivedAmountRaw,
        };
    }
    normalizeTokenBalances(tokenBalances, accountKeys) {
        const balances = new Map();
        for (const tokenBalance of tokenBalances ?? []) {
            balances.set(tokenBalance.accountIndex, {
                accountAddress: accountKeys[tokenBalance.accountIndex] ?? '',
                owner: tokenBalance.owner ?? null,
                mint: tokenBalance.mint,
                amount: BigInt(tokenBalance.uiTokenAmount.amount ?? '0'),
                decimals: tokenBalance.uiTokenAmount.decimals,
            });
        }
        return balances;
    }
    getAccountKeys(tx) {
        return tx.transaction.message.accountKeys.map((accountKey) => {
            if (accountKey instanceof web3_js_1.PublicKey) {
                return accountKey.toBase58();
            }
            const parsedAccount = accountKey;
            return typeof parsedAccount.pubkey === 'string'
                ? parsedAccount.pubkey
                : parsedAccount.pubkey.toBase58();
        });
    }
    assertPublicKey(address, fieldName) {
        try {
            return new web3_js_1.PublicKey(address).toBase58();
        }
        catch {
            throw new common_1.BadRequestException(`${fieldName} must be a valid Solana public key`);
        }
    }
    toBaseUnits(amount, decimals, fieldName) {
        const normalized = amount.trim();
        if (!/^\d+(\.\d+)?$/.test(normalized)) {
            throw new common_1.BadRequestException(`${fieldName} must be a non-negative decimal string`);
        }
        const [wholePart, fractionalPart = ''] = normalized.split('.');
        if (fractionalPart.length > decimals) {
            throw new common_1.BadRequestException(`${fieldName} has more than ${decimals} decimal places`);
        }
        const paddedFraction = fractionalPart
            .padEnd(decimals, '0')
            .slice(0, decimals);
        return (BigInt(wholePart) * 10n ** BigInt(decimals) +
            BigInt(paddedFraction || '0'));
    }
    fromBaseUnits(amount, decimals) {
        if (amount === 0n) {
            return '0';
        }
        const negative = amount < 0n;
        const absoluteAmount = negative ? -amount : amount;
        const factor = 10n ** BigInt(decimals);
        const wholePart = absoluteAmount / factor;
        const fractionalPart = absoluteAmount % factor;
        if (fractionalPart === 0n) {
            return `${negative ? '-' : ''}${wholePart.toString()}`;
        }
        const paddedFraction = fractionalPart
            .toString()
            .padStart(decimals, '0')
            .replace(/0+$/, '');
        return `${negative ? '-' : ''}${wholePart.toString()}.${paddedFraction}`;
    }
};
exports.PaymentService = PaymentService;
exports.PaymentService = PaymentService = PaymentService_1 = __decorate([
    (0, common_1.Injectable)(),
    __metadata("design:paramtypes", [solana_rpc_service_1.SolanaRpcService])
], PaymentService);
//# sourceMappingURL=payment.service.js.map