package com.test.common.util.aes;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;

/**
 * @author lenovo
 * @create 2021-12-08
 */
public class FileTool {

    public static void main(String[] args) {
        try {
//            String key = "fio759m78kcygfao";
//            String iv = "gxtr89596amo2f80";
            String key = "whnqenbzt0pgs0z9";
            String iv = "wah20wwnremq1xf4";
//            encryptFile(key, iv, "D:/files/test.txt", "D:/files/test2.txt");
            decryptFile(key, iv, "C:/Users/lenovo/Desktop/businessDue_20220225.zip", "C:/Users/lenovo/Desktop/businessDue_20220225.zip");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件加密
     *
     * @param key
     * @param iv
     * @param inFilePath  输入文件路径
     * @param outFilePath 输出文件路径
     * @throws Exception
     */
    public static void encryptFile(String key, String iv, String inFilePath, String outFilePath) throws Exception {
        File inFile = new File(inFilePath);
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(inFile));
        byte[] inBytes = new byte[(int) inFile.length()];
        bis.read(inBytes);
        bis.close();
        // AES加密
        byte[] raw = key.getBytes("ASCII");
        SecretKeySpec secretKeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        // 写入文件
        byte[] outBytes = cipher.doFinal(inBytes);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outFilePath));
        bos.write(outBytes);
        bos.close();
    }

    /**
     * 文件解密
     *
     * @param key
     * @param iv
     * @param inFilePath  输入文件路径
     * @param outFilePath 输出文件路径
     * @throws Exception
     */
    public static void decryptFile(String key, String iv, String inFilePath, String outFilePath) throws Exception {
        File inFile = new File(inFilePath);
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(inFile));
        byte[] inBytes = new byte[(int) inFile.length()];
        bis.read(inBytes);
        bis.close();
        // AES解密
        byte[] raw = key.getBytes("ASCII");
        SecretKeySpec secretKeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        // 写入文件
        byte[] outBytes = cipher.doFinal(inBytes);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outFilePath));
        bos.write(outBytes);
        bos.close();
    }
}
