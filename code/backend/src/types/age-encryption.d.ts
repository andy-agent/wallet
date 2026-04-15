declare module 'age-encryption' {
  export const armor: {
    encode(input: Uint8Array | string): string;
  };

  export class Encrypter {
    addRecipient(recipient: string): void;
    encrypt(plaintext: string): Promise<Uint8Array | string>;
  }
}
