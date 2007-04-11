package com.kazak.smi.lib.misc;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Tool {
    private static final char hexChars[] = 
    {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
    private MessageDigest md;


    public MD5Tool(String text) {
        try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
        md.update(text.getBytes());
    }
  
    public String getDigest()  {
        return this.hexToString(md.digest());
    }
   
    private String hexToString(byte buffer[]) {
        StringBuffer hexString = new StringBuffer(2 * buffer.length);
        for (int i = 0; i < buffer.length; i++) {
            this.appendHexPair(buffer[i], hexString);
        }

        return hexString.toString();
    }

    private void appendHexPair(byte b, StringBuffer hexString) {
        char firstByte = hexChars[(b & 0xF0) >> 4];
        char secondByte = hexChars[b & 0x0F];

        hexString.append(firstByte);
        hexString.append(secondByte);
    }
}