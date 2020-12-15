package com.company;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.Cipher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;

import java.util.Base64;

public class AsymCryptography {
    private static String path = "res/AsymKeyStore";
    private PrivateKey privateKey = null;

    static public PublicKey generateNewPair(String pwd) {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(1024);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            File keyStore = new File(path);
            if (!keyStore.exists()) {
                keyStore.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(keyStore);
            SealedObject encryptKey = SymCryptography.encryptByPwd(keyPair.getPrivate().toString(), pwd);
            ObjectOutputStream outputStream = new ObjectOutputStream(fileOutputStream);

            outputStream.writeObject(encryptKey);

            keyStore.setReadOnly();

            return keyPair.getPublic();
        } catch (Exception e) {
            return null;
        }
    }

    static public PrivateKey getPrivateKeyFromString(String keyStr) {
        try {
            byte[] data = Base64.getDecoder().decode((keyStr.getBytes()));
            X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            return fact.generatePrivate(spec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            return null;
        }
    }

    static public PublicKey getPublicKeyFromString(String keyStr) {

        try {
            byte[] data = Base64.getDecoder().decode((keyStr.getBytes(StandardCharsets.UTF_8)));
            X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            return fact.generatePublic(spec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            return null;
        }
    }

    private BigInteger readModulusFromString(String keyData) {
        int begin = keyData.lastIndexOf("modulus");
        int end = keyData.indexOf('\n', begin);
        String modulusStr = keyData.substring(begin + 9, end);
        return new BigInteger(modulusStr);
    }

    static public String getStringAsymKey(PublicKey pubKey) {
        byte [] byte_pubkey = pubKey.getEncoded();
        return Base64.getEncoder().encodeToString(byte_pubkey);
    }

    private BigInteger readExponentFromString(String keyData) {
        int begin = keyData.lastIndexOf("exponent:");
        int end = keyData.indexOf('\n', begin);
        String modulusStr = keyData.substring(begin + 10);
        return new BigInteger(modulusStr);
    }

    public void loadPrivateKey(String pwd) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, ClassNotFoundException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        InputStream in = new FileInputStream(path);
        ObjectInputStream inputStream = new ObjectInputStream(in);
        SealedObject encryptKeyData = (SealedObject) inputStream.readObject();
        String keyData = SymCryptography.decryptByPwd(encryptKeyData, pwd);
        BigInteger modulus = readModulusFromString(keyData);
        BigInteger prExponent = readExponentFromString(keyData);
        RSAPrivateKeySpec privateSpec = new RSAPrivateKeySpec(modulus, prExponent);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        privateKey = factory.generatePrivate(privateSpec);
    }

    static public SealedObject encryptMsg(String msg, PublicKey publicKey) {
        try {
            Cipher encrypt=Cipher.getInstance("RSA");
            encrypt.init(Cipher.ENCRYPT_MODE, publicKey);
            return new SealedObject( msg, encrypt);
        }
        catch (NoSuchPaddingException | NoSuchAlgorithmException | IllegalBlockSizeException | IOException | InvalidKeyException e) {
            return null;
        }
    }

    public String decryptMsg(SealedObject encryptMsg) {
        try {
            Cipher decrypt = Cipher.getInstance("RSA");
            decrypt.init(Cipher.DECRYPT_MODE, privateKey);
            return (String) encryptMsg.getObject(decrypt);
        }

        catch (Exception e) {
            return null;
        }
    }
}