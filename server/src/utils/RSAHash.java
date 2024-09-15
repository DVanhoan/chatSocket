package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;

public class RSAHash {

    private static RSAHash instance;
    private static final String PUBLIC_KEY_FILE = "public.key";
    private static final String PRIVATE_KEY_FILE = "private.key";
    private PublicKey publicKey;
    private PrivateKey privateKey;

    private RSAHash() {
        try {
            File pubKeyFile = new File(PUBLIC_KEY_FILE);
            File privKeyFile = new File(PRIVATE_KEY_FILE);

            if (pubKeyFile.exists() && privKeyFile.exists()) {
                // Đọc khóa từ file
                this.publicKey = readPublicKeyFromFile(PUBLIC_KEY_FILE);
                this.privateKey = readPrivateKeyFromFile(PRIVATE_KEY_FILE);
            } else {
                // Tạo mới cặp khóa
                KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
                keyGen.initialize(2048);
                KeyPair pair = keyGen.generateKeyPair();
                this.publicKey = pair.getPublic();
                this.privateKey = pair.getPrivate();

                // Lưu khóa vào file
                saveKeyToFile(PUBLIC_KEY_FILE, this.publicKey.getEncoded());
                saveKeyToFile(PRIVATE_KEY_FILE, this.privateKey.getEncoded());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static RSAHash getInstance() {
        if (instance == null) {
            synchronized (RSAHash.class) {
                if (instance == null) {
                    instance = new RSAHash();
                }
            }
        }
        return instance;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    private void saveKeyToFile(String fileName, byte[] key) throws IOException {
        FileOutputStream fos = new FileOutputStream(fileName);
        fos.write(Base64.getEncoder().encode(key));
        fos.close();
    }

    private PublicKey readPublicKeyFromFile(String fileName) throws Exception {
        FileInputStream fis = new FileInputStream(fileName);
        byte[] keyBytes = fis.readAllBytes();
        fis.close();

        X509EncodedKeySpec spec = new X509EncodedKeySpec(Base64.getDecoder().decode(keyBytes));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }

    private PrivateKey readPrivateKeyFromFile(String fileName) throws Exception {
        FileInputStream fis = new FileInputStream(fileName);
        byte[] keyBytes = fis.readAllBytes();
        fis.close();

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(keyBytes));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(spec);
    }

    public static String encrypt(String data) throws Exception {
        RSAHash generator = RSAHash.getInstance();
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, generator.getPublicKey());
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decrypt(String data) throws Exception {
        RSAHash generator = RSAHash.getInstance();
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, generator.getPrivateKey());
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(data));
        return new String(decryptedBytes);
    }

    public static String sign(String data) throws Exception {
        RSAHash generator = RSAHash.getInstance();
        Signature rsa = Signature.getInstance("SHA256withRSA");
        rsa.initSign(generator.getPrivateKey());
        rsa.update(data.getBytes());
        byte[] signature = rsa.sign();
        return Base64.getEncoder().encodeToString(signature);
    }

    public static boolean verify(String data, String signature) throws Exception {
        RSAHash generator = RSAHash.getInstance();
        Signature rsa = Signature.getInstance("SHA256withRSA");
        rsa.initVerify(generator.getPublicKey());
        rsa.update(data.getBytes());
        byte[] signatureBytes = Base64.getDecoder().decode(signature);
        return rsa.verify(signatureBytes);
    }

    public static void main(String[] args) {
        try {
            String data = "This is a test";
            System.out.println("Encrypted Data: " + RSAHash.encrypt(data));
            System.out.println("Decrypted Data: " + RSAHash.decrypt(RSAHash.encrypt(data)));
            String signature = RSAHash.sign(data);
            System.out.println("Signature: " + signature);
            System.out.println("Verified: " + RSAHash.verify(data, signature));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
