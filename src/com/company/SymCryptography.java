package com.company;

import javax.crypto.*;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

class SymCryptography {
    private SecretKey secretKey = null;

    public SecretKey getSecretKey() {
        return secretKey;
    }

    public SymCryptography() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        secretKey = keyGenerator.generateKey();
    }

    public SealedObject encryptMsg(String msg) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, this.secretKey);
        return new SealedObject(msg, cipher);
    }

    public String decryptMsg(SealedObject data) throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, IOException, ClassNotFoundException {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, this.secretKey);
        return (String) data.getObject(cipher);
    }
}