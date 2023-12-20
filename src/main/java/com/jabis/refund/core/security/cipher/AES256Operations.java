package com.jabis.refund.core.security.cipher;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
public class AES256Operations implements CryptographyOperations {

    public static String alg = "AES/CBC/PKCS5Padding";
    private final String key = "abcdefghabcdefghabcdefghabcdefgh"; // 32byte
    private final String iv = "0123456789abcdef"; // 16byte

    @Override
    public String encrypt(String plaintext) {

        byte[] encrypted;
        try {

            Cipher cipher = Cipher.getInstance(alg);
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec ivParamSpec = new IvParameterSpec(iv.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParamSpec);

            encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {

            log.error("Failed to encrypt", e);

            throw new RuntimeException(e);
        }

        return Base64.getEncoder().encodeToString(encrypted);
    }

    @Override
    public String decrypt(String encryptedText) {

        byte[] decrypted;
        try {

            Cipher cipher = Cipher.getInstance(alg);
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec ivParamSpec = new IvParameterSpec(iv.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParamSpec);

            byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
            decrypted = cipher.doFinal(decodedBytes);
        } catch (Exception e) {

            log.error("Failed to decrypt", e);

            throw new RuntimeException(e);
        }

        return new String(decrypted, StandardCharsets.UTF_8);
    }
}
