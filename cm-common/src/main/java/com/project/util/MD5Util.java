package com.project.util;

import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 敏感信息加密
 */
@Component
public class MD5Util {
    public static String calculateMD5(String originalString) {
        try {
            // 创建MD5加密实例
            MessageDigest md = MessageDigest.getInstance("MD5");

            // 执行加密操作
            byte[] messageDigest = md.digest(originalString.getBytes());

            // 将得到的散列值转换为十六进制
            StringBuilder sb = new StringBuilder();
            for (byte b : messageDigest) {
                sb.append(String.format("%02x", b));
            }

            // 返回MD5散列值
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5加密算法不可用", e);
        }
    }
}