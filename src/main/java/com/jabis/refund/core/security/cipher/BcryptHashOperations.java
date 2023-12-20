package com.jabis.refund.core.security.cipher;

import org.mindrot.jbcrypt.BCrypt;

public class BcryptHashOperations implements OneWayPasswordHashOperations {

    @Override
    public String hash(String plaintext) {
        return BCrypt.hashpw(plaintext, BCrypt.gensalt());
    }

    @Override
    public boolean verify(String plaintext, String hashedText) {
        return BCrypt.checkpw(plaintext, hashedText);
    }
}
