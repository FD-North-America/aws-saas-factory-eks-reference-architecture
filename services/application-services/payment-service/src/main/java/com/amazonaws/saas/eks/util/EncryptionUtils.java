package com.amazonaws.saas.eks.util;

import com.amazonaws.saas.eks.secretsmanager.SecretsClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class EncryptionUtils {
    private static final Logger logger = LogManager.getLogger(EncryptionUtils.class);

    @Autowired
    private SecretsClient secretsClient;

    public String encrypt(String input) {
        try {
            String key = secretsClient.getEncryptionKey();
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES"); // Create a secret key spec from the key
            Cipher cipher = Cipher.getInstance("AES"); // Get an instance of cipher for AES algorithm
            cipher.init(Cipher.ENCRYPT_MODE, keySpec); // Initialize cipher for encryption
            byte[] encryptedBytes = cipher.doFinal(input.getBytes()); // Perform encryption on input bytes
            return Base64.getEncoder().encodeToString(encryptedBytes); // Encode the encrypted bytes to base64 string
        } catch (Exception ex) {
            logger.error(ex);
        }
        return null;
    }

    public String decrypt(String input) {
        try {
            String key = secretsClient.getEncryptionKey();
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES"); // Create a secret key spec from the key
            Cipher cipher = Cipher.getInstance("AES"); // Get an instance of cipher for AES algorithm
            cipher.init(Cipher.DECRYPT_MODE, keySpec); // Initialize cipher for decryption
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(input)); // Decode input to get encrypted bytes and perform decryption
            return new String(decryptedBytes); // Convert the decrypted bytes to string
        } catch (Exception ex) {
            logger.error(ex);
        }
        return null;
    }
}

