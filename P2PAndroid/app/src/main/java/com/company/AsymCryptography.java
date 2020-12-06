package com.company;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
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
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(512);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        InputStream keyStoreData = new FileInputStream(path);
        keyStore.load(keyStoreData, passwordKeyStore.toCharArray());
        X509Certificate[] certificateChain = new X509Certificate[2];
        keyStore.setKeyEntry(alias, keyPair.getPrivate(), passwordKeyStore.toCharArray(), certificateChain);
    }
}
