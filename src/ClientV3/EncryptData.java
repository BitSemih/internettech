package ClientV3;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Base64;

/**
 * The type Encrypt data.
 */
public class EncryptData {
    //Define type of algorithm
    private static final String algorithm = "AES";
    //The key that only the clients know
    private static final byte[] keyValue = "SEMIHISCOOLLL420".getBytes();
    private Key key;
    private Cipher c;

    /**
     * Instantiates a new Encrypt data.
     */
    public EncryptData() {
        this.key = generateSecretKey();
        try {
            this.c = Cipher.getInstance(algorithm);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    //Encrypt given string
    public String EncryptString(String message) {
        try {
            //Init cipher
            this.c.init(Cipher.ENCRYPT_MODE, this.key);
            byte[] encValue = this.c.doFinal(message.getBytes());
            String encryptedValue = Base64.getEncoder().encodeToString(encValue);
            return encryptedValue;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    //Decrypt given string
    public String DecryptString(String message) {
        try {
            //Init cipher
            this.c.init(Cipher.DECRYPT_MODE, this.key);
            byte[] decodedValue = Base64.getDecoder().decode(message.getBytes());
            byte[] decryptedVal = this.c.doFinal(decodedValue);
            return new String(decryptedVal, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    //Generate the encryption key
    private static Key generateSecretKey() {
        Key key = new SecretKeySpec(keyValue, algorithm);
        return key;
    }
}
