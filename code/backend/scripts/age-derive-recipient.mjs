import * as fs from 'node:fs';
import * as path from 'node:path';
import * as age from 'age-encryption';

const identityPath =
  process.argv[2] ||
  '/Users/cnyirui/server/区块恢复私钥';

const identity = fs.readFileSync(path.resolve(identityPath), 'utf8').trim();
const recipient = await age.identityToRecipient(identity);
process.stdout.write(`${recipient}\n`);
