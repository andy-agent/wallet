#!/usr/bin/env node

import { readFile, writeFile } from 'node:fs/promises';
import { resolve } from 'node:path';
import pg from 'pg';

const PLACEHOLDER_ADDRESSES = {
  SOLANA: ['So11111111111111111111111111111111111111112'],
  TRON: ['TQn9Y2khEsLJW1ChVWFMSMeRDow5KcbLSE'],
};

const apply = process.argv.includes('--apply');
const backend = resolveBackend();

if (backend === 'file') {
  await cleanFileRuntime(apply);
} else {
  await cleanPostgresRuntime(apply);
}

function resolveBackend() {
  const explicit = process.env.RUNTIME_STATE_BACKEND?.trim().toLowerCase();
  if (explicit === 'postgres' || explicit === 'file') {
    return explicit;
  }
  return (process.env.NODE_ENV ?? 'development').toLowerCase() === 'production'
    ? 'postgres'
    : 'file';
}

async function cleanFileRuntime(shouldApply) {
  const filePath = resolve(
    process.env.RUNTIME_STATE_FILE ?? `${process.cwd()}/.runtime/runtime-state.json`,
  );
  const payload = JSON.parse(await readFile(filePath, 'utf8'));
  const existing = payload.walletPublicAddresses ?? [];
  const stale = existing.filter((item) => isPlaceholder(item.networkCode, item.address));

  printSummary('file', stale, shouldApply);
  if (!shouldApply || stale.length === 0) {
    return;
  }

  payload.walletPublicAddresses = existing.filter(
    (item) => !isPlaceholder(item.networkCode, item.address),
  );
  await writeFile(filePath, JSON.stringify(payload, null, 2) + '\n', 'utf8');
  console.log(`Applied cleanup to ${filePath}`);
}

async function cleanPostgresRuntime(shouldApply) {
  const databaseUrl = process.env.DATABASE_URL;
  if (!databaseUrl) {
    throw new Error('DATABASE_URL is required when runtime backend is postgres');
  }

  const { Client } = pg;
  const client = new Client({ connectionString: databaseUrl });
  await client.connect();
  try {
    const placeholders = [...PLACEHOLDER_ADDRESSES.SOLANA, ...PLACEHOLDER_ADDRESSES.TRON];
    const stale = (
      await client.query(
        `
          SELECT address_id, account_id, network_code, asset_code, address, is_default
          FROM runtime_state_wallet_public_addresses
          WHERE address = ANY($1::text[])
          ORDER BY account_id, network_code, asset_code
        `,
        [placeholders],
      )
    ).rows;

    printSummary('postgres', stale, shouldApply);
    if (!shouldApply || stale.length === 0) {
      return;
    }

    await client.query(
      `
        DELETE FROM runtime_state_wallet_public_addresses
        WHERE address = ANY($1::text[])
      `,
      [placeholders],
    );
    console.log('Applied cleanup to runtime_state_wallet_public_addresses');
  } finally {
    await client.end();
  }
}

function isPlaceholder(networkCode, address) {
  const bucket = PLACEHOLDER_ADDRESSES[networkCode];
  return bucket ? bucket.includes(String(address).trim()) : false;
}

function printSummary(mode, rows, shouldApply) {
  console.log(`Runtime backend: ${mode}`);
  console.log(`Mode: ${shouldApply ? 'apply' : 'dry-run'}`);
  console.log(`Placeholder wallet addresses found: ${rows.length}`);
  for (const row of rows.slice(0, 20)) {
    console.log(
      `${row.account_id ?? row.accountId}\t${row.network_code ?? row.networkCode}\t${row.asset_code ?? row.assetCode}\t${row.address}`,
    );
  }
  if (rows.length > 20) {
    console.log(`... ${rows.length - 20} more`);
  }
}
