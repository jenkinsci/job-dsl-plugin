package hudson.plugins.perforce;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class PerforcePasswordEncryptor {

    private static final String keyString = "405kqo0gc20f9985142rj17779v4922568on29pwj92toqt884";
    private static final String ENCRYPTION_PREFIX = "0f0kqlwa";

    public PerforcePasswordEncryptor() {
    }

    public boolean appearsToBeAnEncryptedPassword(String toCheck) {
        return toCheck.startsWith(ENCRYPTION_PREFIX);
    }

    public String encryptString(String toEncrypt) {
        if (toEncrypt == null || toEncrypt.trim().length() == 0)
            return "";

        SecretKey key = desKeyFromString(keyString);
        Cipher cipher = desCipherForModeWithKey(Cipher.ENCRYPT_MODE, key);

        byte[] encryptedtext = null;
        try {
            encryptedtext = cipher.doFinal(toEncrypt.getBytes());
        } catch (IllegalBlockSizeException ibse) {
            System.err.println(ibse);
        } catch (BadPaddingException bpe) {
            System.err.println(bpe);
        }
        String encodedString = Base64.encodeBase64String(encryptedtext).trim();

        return ENCRYPTION_PREFIX + encodedString;
    }

    public String decryptString(String toDecrypt) {
        if (toDecrypt == null || toDecrypt.length() == 0)
            return "";

        SecretKey key = desKeyFromString(keyString);
        Cipher cipher = desCipherForModeWithKey(Cipher.DECRYPT_MODE, key);

        byte[] cleartext = null;
        try {
            // workaround bug with passwords which have a "+" in their BASE64 encoded form
            String processedToDecrypt = toDecrypt.replaceFirst(ENCRYPTION_PREFIX, "").replaceAll(" ", "+");
            byte[] encryptedtext = Base64.decodeBase64(processedToDecrypt.getBytes());
            cleartext = cipher.doFinal(encryptedtext);
        } catch (IllegalBlockSizeException ibse) {
            System.err.println(ibse);
        } catch (BadPaddingException bpe) {
            System.err.println(bpe);
        }

        return convertBytesToString(cleartext);
    }

    private static SecretKey desKeyFromString(String keystr) {
        try {
            byte[] keyBytes = keystr.getBytes("UTF8");
            KeySpec keySpec = new DESKeySpec(keyBytes);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("DES");
            return factory.generateSecret(keySpec);
        } catch (UnsupportedEncodingException uee) {
            System.err.println(uee);
        } catch (InvalidKeyException ike) {
            System.err.println("Unable to create DES keyspec " + ike);
        } catch (NoSuchAlgorithmException nsal) {
            System.err.println(nsal);
        } catch (InvalidKeySpecException ikse) {
            System.err.print("Unable to generate secret key: " + ikse);
        }
        return null;
    }

    private static Cipher desCipherForModeWithKey(int mode, SecretKey key) {
        try {
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(mode, key);
            return cipher;
        } catch (NoSuchAlgorithmException nsal) {
            System.err.println(nsal);
        } catch (NoSuchPaddingException nspe) {
            System.err.println(nspe);
        } catch (InvalidKeyException ike) {
            System.err.print("Unable to init cipher: " + ike);
        }
        return null;
    }

    private static String convertBytesToString(byte[] bytes) {
        if (bytes == null)
            return "";
        StringBuffer stringBuffer = new StringBuffer(bytes.length);
        for (int i = 0; i < bytes.length; i++) {
            stringBuffer.append((char) bytes[i]);
        }
        return stringBuffer.toString();
    }

}