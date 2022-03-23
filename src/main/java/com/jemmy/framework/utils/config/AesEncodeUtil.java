package com.jemmy.framework.utils.config;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AesEncodeUtil {

    // 初始向量（偏移）
    private static final String IV = "ShAesEncodeUtils";   //AES 为16bytes. DES 为8bytes

    // 编码方式
    private static final String bm = "UTF-8";

    /**
     * 加密
     *
     * @param cleartext 加密前的字符串
     * @return 加密后的字符串
     */
    public static String encrypt(String ase_key, String cleartext) {
        try {
            IvParameterSpec zeroIv = new IvParameterSpec(IV.getBytes());
            //两个参数，第一个为私钥字节数组， 第二个为加密方式 AES或者DES
            SecretKeySpec key = new SecretKeySpec(ase_key.getBytes(), "AES");
            //实例化加密类，参数为加密方式，要写全
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); //PKCS5Padding比PKCS7Padding效率高，PKCS7Padding可支持IOS加解密
            //初始化，此方法可以采用三种方式，按加密算法要求来添加。（1）无第三个参数（2）第三个参数为SecureRandom random = new SecureRandom();中random对象，随机数。(AES不可采用这种方法)（3）采用此代码中的IVParameterSpec
            cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);

            // 加密后的字节数组
            byte[] encryptedData = cipher.doFinal(cleartext.getBytes(bm));
            // 对加密后的字节数组进行base64编码
            return Base64.getMimeEncoder().encodeToString(encryptedData);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 解密
     *
     * @param encrypted 解密前的字符串（也就是加密后的字符串）
     * @return 解密后的字符串（也就是加密前的字符串）
     */
    public static String decrypt(String ase_key, String encrypted) {
        try {
            //将base64编码的字节数组转化为在加密之后的字节数组
            byte[] byteMi = Base64.getMimeDecoder().decode(encrypted);

            IvParameterSpec zeroIv = new IvParameterSpec(IV.getBytes());
            SecretKeySpec key = new SecretKeySpec(ase_key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            //与加密时不同MODE:Cipher.DECRYPT_MODE
            cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
            byte[] decryptedData = cipher.doFinal(byteMi);
            return new String(decryptedData, bm);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
