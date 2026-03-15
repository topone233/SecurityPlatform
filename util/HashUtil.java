package org.example.securityplatform.util;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 哈希工具类
 */
public class HashUtil {

    /**
     * 计算文件的MD5值
     *
     * @param filePath 文件路径
     * @return MD5哈希值
     * @throws IOException 读取文件异常
     */
    public static String calculateMD5(String filePath) throws IOException {
        try (InputStream is = new FileInputStream(filePath)) {
            return DigestUtils.md5Hex(is);
        }
    }

    /**
     * 计算字节数组的MD5值
     *
     * @param data 字节数组
     * @return MD5哈希值
     */
    public static String calculateMD5(byte[] data) {
        return DigestUtils.md5Hex(data);
    }
}