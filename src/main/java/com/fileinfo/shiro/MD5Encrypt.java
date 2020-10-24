package com.fileinfo.shiro;

import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.util.ByteSource;

public class MD5Encrypt {
    private static final int hashIterations = 1024;

    public static String MD5(String password,String salt){
        Md5Hash md5Hash = new Md5Hash(password,ByteSource.Util.bytes(salt),hashIterations);
        return md5Hash.toHex();
    }
}
