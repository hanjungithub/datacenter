package com.zhizheng.utils;

import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.DefaultHashService;
import org.apache.shiro.crypto.hash.HashRequest;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.SimpleByteSource;

public class PasswordUtils {

    public static String getPasswordSalt(){
        SecureRandomNumberGenerator randomNumberGenerator = new SecureRandomNumberGenerator();
        randomNumberGenerator.setSeed((System.currentTimeMillis() + "").getBytes());
        String salt = randomNumberGenerator.nextBytes().toHex();
       return salt;
    }


    public static void main(String[] args) {

    }
}
