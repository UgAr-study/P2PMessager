package com.company;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class AsymCryptography {
    protected KeyStore keyStore;
    protected String path = "Artem_LOH";
    protected String passwordKeyStore;
    protected String alias = "msg-key";
    String publicKey = "";

    public AsymCryptography (String passwordKeyStore) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, IOException, KeyStoreException, NoSuchAlgorithmException {
        keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null, passwordKeyStore.toCharArray());
        FileOutputStream fos = new FileOutputStream("newKeyStoreFileName.jks");
        keyStore.store(fos, passwordKeyStore.toCharArray());
    }

    public void generateNewPair() throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA/ECB/PKCS1Padding");
        keyPairGenerator.initialize(512);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        InputStream keyStoreData = new FileInputStream(path);
        keyStore.load(keyStoreData, passwordKeyStore.toCharArray());
        X509Certificate[] certificateChain = new X509Certificate[2];
        keyStore.setKeyEntry(alias, keyPair.getPrivate(), passwordKeyStore.toCharArray(), certificateChain);
    }

    public String encryptMsg(String publicKey, String msg) throws NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher encrypt=Cipher.getInstance("RSA/ECB/PKCS1Padding");
        Key pubKey = new SecretKeySpec(, );
        encrypt.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedMessage = encrypt.doFinal(msg.getBytes(StandardCharsets.UTF_8));
    }
}
