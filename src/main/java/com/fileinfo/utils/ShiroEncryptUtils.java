package com.fileinfo.utils;

import org.apache.shiro.crypto.hash.SimpleHash;

import java.security.MessageDigest;

public final class ShiroEncryptUtils {
    private static final String MD5 = "MD5";
    private static final int HASH_ITERATIONS = 1024;

    public static String MD5(String salt, String password) {
        SimpleHash simpleHash = new SimpleHash(MD5, password, salt, HASH_ITERATIONS);
        return simpleHash.toHex();
    }
//
//    public static void main(String[] args) {
//        String s = MD5("admin", "admin123");
//        System.out.println(s);
//    }
}
