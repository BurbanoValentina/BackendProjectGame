package com.example.gamebackend.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
    private MD5Util() {}
    
    /**
     * Encripta un string usando MD5
     * @param input String a encriptar
     * @return String encriptado en MD5
     */
    public static String encrypt(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al encriptar con MD5", e);
        }
    }
    
    /**
     * Verifica si un string coincide con su hash MD5
     * @param input String sin encriptar
     * @param hash Hash MD5 para comparar
     * @return true si coinciden
     */
    public static boolean verify(String input, String hash) {
        String inputHash = encrypt(input);
        return inputHash.equals(hash);
    }
}
