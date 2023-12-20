package com.jabis.refund.core.security.cipher;

public interface CryptographyOperations {

    String encrypt(String plaintext);

    String decrypt(String encryptedText);
}
