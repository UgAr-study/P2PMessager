package com.company;

import com.google.gson.Gson;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

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
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return new SealedObject(msg, cipher);
    }

    public String decryptMsg(SealedObject data) throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, IOException, ClassNotFoundException {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return (String) data.getObject(cipher);
    }

    public byte[] getMacMsg(String msg) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKey);
        byte[] data = msg.getBytes("UTF-8");
        return mac.doFinal(data);
    }

    static public byte[] getMacMsg(String msg, SecretKey key) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(key);
        byte[] data = msg.getBytes("UTF-8");
        return mac.doFinal(data);
    }

    static public SealedObject encryptByPwd(String msg, String pwd) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException, IllegalBlockSizeException, InvalidKeySpecException {
        SecretKey key = generateAESKeyByPwd(pwd);

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return new SealedObject(msg, cipher);
    }

    static public String encryptByPwdGson(String msg, String pwd) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException, IllegalBlockSizeException, InvalidKeySpecException {
        SealedObject encryptMsg = SymCryptography.encryptByPwd(msg, pwd);
        Gson gs = new Gson();
        String data = gs.toJson(encryptMsg);
        return data;
    }

    static public String decryptByPwd(SealedObject data, String pwd) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, InvalidKeySpecException {
        SecretKey key = generateAESKeyByPwd(pwd);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return (String) data.getObject(cipher);
    }

    static public String decryptByPwdGson(String data, String pwd) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, InvalidKeySpecException {
        Gson gs = new Gson();
        SealedObject sobj = gs.fromJson(data, SealedObject.class);
        return SymCryptography.decryptByPwd(sobj, pwd);
    }

    private static SecretKey generateAESKeyByPwd(String pwd) throws NoSuchAlgorithmException, InvalidKeySpecException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(pwd.getBytes());
        byte[] encodedhash = digest.digest(pwd.getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(encodedhash, 0, encodedhash.length, "AES");
    }
}