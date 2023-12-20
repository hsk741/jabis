package com.jabis.refund.core.security.cipher;

public interface OneWayPasswordHashOperations {

    String hash(String plaintext);

    boolean verify(String plaintext, String encryptedPassword);
}
