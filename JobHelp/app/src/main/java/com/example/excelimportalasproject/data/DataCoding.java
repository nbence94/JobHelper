package com.example.excelimportalasproject.data;

import android.util.Base64;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class DataCoding {
    final private String AES = "AES";
    final String CODE_KEY = "mykey";

    public String encrypt(String kodolando_szoveg) throws Exception {
        SecretKeySpec key = generateKey(CODE_KEY);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(kodolando_szoveg.getBytes());
        return Base64.encodeToString(encVal, Base64.DEFAULT);
    }

    public String decrypt(String kodolt_szoveg) throws Exception {
        SecretKeySpec key = generateKey(CODE_KEY);
        Log.e("DEBUG", CODE_KEY);
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] dekodolt_ertek = Base64.decode(kodolt_szoveg, Base64.DEFAULT);
        byte[] decVal = c.doFinal(dekodolt_ertek);
        String dekodolt_eredmeny = new String(decVal);
        return dekodolt_eredmeny;

    }

    private SecretKeySpec generateKey(String codekey) throws Exception {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = codekey.getBytes(StandardCharsets.UTF_8);
        digest.update(bytes, 0, bytes.length);
        byte[] key = digest.digest();
        return new SecretKeySpec(key, AES);
    }
}
