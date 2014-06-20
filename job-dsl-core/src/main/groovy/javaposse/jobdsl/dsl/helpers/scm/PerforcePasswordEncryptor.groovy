package javaposse.jobdsl.dsl.helpers.scm

import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec
import java.security.spec.KeySpec

import static javax.crypto.Cipher.ENCRYPT_MODE
import static org.apache.commons.codec.binary.Base64.encodeBase64String

/**
 * Borrowed from the <a href="https://wiki.jenkins-ci.org/display/JENKINS/Perforce+Plugin">Perforce Plugin</a>.
 */
class PerforcePasswordEncryptor {
    private static final String KEY = '405kqo0gc20f9985142rj17779v4922568on29pwj92toqt884'
    private static final String ENCRYPTION_PREFIX = '0f0kqlwa'

    static boolean isEncrypted(String password) {
        password != null && password.startsWith(ENCRYPTION_PREFIX)
    }

    static String encrypt(String password) {
        if (password == null || password.trim().length() == 0) {
            return ''
        }

        KeySpec keySpec = new DESKeySpec(KEY.getBytes('UTF8'))
        SecretKeyFactory factory = SecretKeyFactory.getInstance('DES')
        SecretKey key = factory.generateSecret(keySpec)
        Cipher cipher = Cipher.getInstance('DES')
        cipher.init(ENCRYPT_MODE, key)
        byte[] encryptedText = cipher.doFinal(password.bytes)

        ENCRYPTION_PREFIX + encodeBase64String(encryptedText)
    }
}
