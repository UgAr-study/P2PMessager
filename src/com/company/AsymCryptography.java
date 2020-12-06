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
    private static String path = "res/KeyStore";
    private PrivateKey privateKey = null;
    private PublicKey publicKey = null;

    static public PublicKey generateNewPair(String pwd) throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(4096);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        File keyStore = new File(path);
        if (!keyStore.exists()) {
            keyStore.createNewFile();
        }
        FileOutputStream fileOutputStream = new FileOutputStream(keyStore);
        fileOutputStream.write(keyPair.getPrivate().toString().getBytes());
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();
        return keyPair.getPublic();
    }

    public PublicKey getPublicKey() {
        return publicKey;
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

    private BigInteger readModulus(String pathKS) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(pathKS);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
        bufferedReader.readLine();
        bufferedReader.readLine();
        String modulusStr = bufferedReader.readLine().substring(11);
        return new BigInteger(modulusStr);
    }

    private BigInteger readModulus() throws IOException {
        return readModulus(path);
    }

        public String getStringPublicKey() {
        byte [] byte_pubkey = publicKey.getEncoded();
        return Base64.getEncoder().encodeToString(byte_pubkey);
    }

    static public String getStringPublicKey(PublicKey pubKey) {
        byte [] byte_pubkey = pubKey.getEncoded();
        return Base64.getEncoder().encodeToString(byte_pubkey);
    }

    private String getStringPrivateKey() {
        byte [] byte_prkey = privateKey.getEncoded();
        String str_key = Base64.getEncoder().encodeToString(byte_prkey);
        return str_key;
    }

    private BigInteger readExponent(String pathKS) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(pathKS);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
        bufferedReader.readLine();
        bufferedReader.readLine();
        bufferedReader.readLine();
        String exponentStr = bufferedReader.readLine().substring(20);
        return new BigInteger(exponentStr);
    }

    private BigInteger readExponent() throws IOException {
        return readExponent(path);
    }

    public void loadPrivateKey(String pathKS) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        BigInteger modulus = readModulus(pathKS);
        BigInteger prExponent = readExponent(pathKS);
        RSAPrivateKeySpec privateSpec = new RSAPrivateKeySpec(modulus, prExponent);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        privateKey = factory.generatePrivate(privateSpec);
    }

    public void loadPrivateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        loadPrivateKey(path);
    }

        public SealedObject encryptMsg(String msg, PublicKey publicKey) throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException, IOException {
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