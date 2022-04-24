package com.fz.common.encrypt;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

/**
 * 私钥加解密
 *
 * @author dingpeihua
 * @version 1.0
 * @date 2019/4/12 09:33
 */
public class PrivateKeyEncrypt extends RSAEncrypt {
    /**
     * 得到私钥
     *
     * @param bysKey
     * @return
     */
    private static PrivateKey getPrivateKeyFromX509(String bysKey) throws Exception {
        byte[] decodedKey = Base64.getDecoder().decode(bysKey);
        return getPrivateKeyFromX509(decodedKey);
    }

    /**
     * 得到私钥
     *
     * @param bysKey
     * @return
     */
    private static PrivateKey getPrivateKeyFromX509(byte[] bysKey) throws Exception {
        PKCS8EncodedKeySpec x509 = new PKCS8EncodedKeySpec(bysKey);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        return keyFactory.generatePrivate(x509);
    }

    @Override
    public String encrypt(byte[] key, String content) throws Exception {
        // 得到私钥对象
        PrivateKey keyPrivate = getPrivateKeyFromX509(key);
        // 加密数据
        Cipher ci = Cipher.getInstance(TRANSFORMATION);
        ci.init(Cipher.ENCRYPT_MODE, keyPrivate);
        byte[] bytes = content.getBytes(UTF_8);
        byte[] encryptedData = ci.doFinal(bytes);
        return new String(Base64.getEncoder().encode(encryptedData), UTF_8);
    }

    @Override
    public String encrypt(String key, String content) throws Exception {
        return encrypt(key.getBytes(UTF_8), content);
    }

    @Override
    public String decrypt(byte[] key, String content) throws Exception {
        // 得到私钥对象
        PrivateKey keyPrivate = getPrivateKeyFromX509(key);
        // 解密数据
        Cipher cp = Cipher.getInstance(TRANSFORMATION);
        cp.init(Cipher.DECRYPT_MODE, keyPrivate);
        byte[] arr = cp.doFinal(Base64.getDecoder().decode(content.getBytes(UTF_8)));
        return new String(arr, UTF_8);
    }

    @Override
    public String decrypt(String key, String content) throws Exception {
        return decrypt(key.getBytes(UTF_8), content);
    }
}
