import * as age from 'age-encryption';

const chunks = [];
for await (const chunk of process.stdin) {
  chunks.push(Buffer.from(chunk));
}

const input = JSON.parse(Buffer.concat(chunks).toString('utf8'));
const encrypter = new age.Encrypter();
for (const recipient of input.recipients ?? []) {
  encrypter.addRecipient(recipient);
}

const ciphertext = await encrypter.encrypt(JSON.stringify(input.payload));
process.stdout.write(
  JSON.stringify({
    ciphertext: age.armor.encode(ciphertext),
  }),
);
