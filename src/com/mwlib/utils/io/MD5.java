/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mwlib.utils.io;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author STsimbalov
 */
public final class MD5{
    // 
    private static String convertToHex(final byte[] data){ 
        // 
        final StringBuilder buf = new StringBuilder();
        for(int i = 0; i < data.length; i++){ 
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do{ 
               if((0 <= halfbyte) && (halfbyte <= 9)) 
                  buf.append((char) ('0' + halfbyte));
               else 
                  buf.append((char) ('a' + (halfbyte - 10)));
               halfbyte = data[i] & 0x0F;
            }while(two_halfs++ < 1);
        } 
        return buf.toString();
    } 
    //    
    public static String digest(final String text, final String enc) throws NoSuchAlgorithmException, UnsupportedEncodingException{ 
        // 
        final MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] md5hash = new byte[32];
        md.update(text.getBytes(enc), 0, text.length());
        md5hash = md.digest();
        return convertToHex(md5hash);
    } 
    //
    /*
    public static void main(String[] strs){
        // 
        try{
            String res = MD5.digest("testString", "iso-8859-1"); 
            // 
            System.out.println("res=" + res);
            System.out.println("res.len=" + res.length());
        }catch(Exception exc){
            exc.printStackTrace();
        }                
    } */    
}
