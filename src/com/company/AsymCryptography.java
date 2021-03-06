package com.company;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;

import java.lang.reflect.GenericDeclaration;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class AsymCryptography {
    private static String path = "res/AsymKeyStore";
    private PrivateKey privateKey = null;

    static public PublicKey generateNewPair(String pwd) throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException, IllegalBlockSizeException, InvalidKeyException, InvalidKeySpecException, NoSuchPaddingException {
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
    }
    
    public PrivateKey getPrivateKeyFromString(String keyStr) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] data = Base64.getDecoder().decode((keyStr.getBytes()));
        X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
        KeyFactory fact = KeyFactory.getInstance("RSA");
        return fact.generatePrivate(spec);
    }

    public PublicKey getPublicKeyFromString(String keyStr) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] data = Base64.getDecoder().decode((keyStr.getBytes(StandardCharsets.UTF_8)));
        X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
        KeyFactory fact = KeyFactory.getInstance("RSA");
        return fact.generatePublic(spec);
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

    public SealedObject encryptMsg(String msg, PublicKey publicKey) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, IOException {
        Cipher encrypt=Cipher.getInstance("RSA");
        try {
            encrypt.init(Cipher.ENCRYPT_MODE, publicKey);
        }
        catch (InvalidKeyException e) {
            e.getMessage();
            System.err.println("an attempt was made to encrypt empty text and the private key was not loaded");
        }
        return new SealedObject( msg, encrypt);
    }

    public String decryptMsg(SealedObject encryptMsg) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, IllegalBlockSizeException, IOException, ClassNotFoundException {
        Cipher decrypt = Cipher.getInstance("RSA");
        try {
            decrypt.init(Cipher.DECRYPT_MODE, privateKey);
        }
        catch (InvalidKeyException e) {
            e.getMessage();
            System.err.println("an attempt was made to encrypt empty text and the private key was not loaded");
        }
        return (String) encryptMsg.getObject(decrypt);
    }
}