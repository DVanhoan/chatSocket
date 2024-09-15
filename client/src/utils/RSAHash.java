package utils;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;

public class RSAHash {

    private static RSAHash instance;
    private static final String PUBLIC_KEY_FILE = "public.key";
    private PublicKey publicKey;

    private RSAHash() throws Exception {
        File pubKeyFile = new File(PUBLIC_KEY_FILE);
        if (pubKeyFile.exists()) {
            this.publicKey = readPublicKeyFromFile(PUBLIC_KEY_FILE);
        } else {
            throw new Exception("Public key file not found");
        }
    }

    public static RSAHash getInstance() throws Exception {
        if (instance == null) {
            synchronized (RSAHash.class) {
                if (instance == null) {
                    instance = new RSAHash();
                }
            }
        }
        return instance;
    }

    private PublicKey readPublicKeyFromFile(String fileName) throws Exception {
        FileInputStream fis = new FileInputStream(fileName);
        byte[] keyBytes = fis.readAllBytes();
        fis.close();

        X509EncodedKeySpec spec = new X509EncodedKeySpec(Base64.getDecoder().decode(keyBytes));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }

    public static String encrypt(String data) throws Exception {
        RSAHash encryptor = RSAHash.getInstance();
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, encryptor.publicKey);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static void main(String[] args) {
        try {
            String data = "123";
            String encryptedData = RSAHash.encrypt(data);
            System.out.println("Encrypted Data: " + encryptedData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
