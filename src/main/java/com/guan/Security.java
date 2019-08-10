package com.guan;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

@SuppressWarnings("restriction")
public class Security {
	public String des3EncodeECB(String key, String message) throws Exception {
		Key deskey = null;

		byte[] keyBytes = key.getBytes();
		byte[] data = message.getBytes("GBK");

		DESedeKeySpec spec = new DESedeKeySpec(keyBytes);
		SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("DESede");
		deskey = keyfactory.generateSecret(spec);
		Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
		cipher.init(1, deskey);
		byte[] bOut = cipher.doFinal(data);
		return new BASE64Encoder().encode(bOut);
	}

	public String des3DecodeECB(String key, String message) throws Exception {
		Key deskey = null;

		byte[] keyBytes = key.getBytes();
		byte[] data = new BASE64Decoder().decodeBuffer(message);

		DESedeKeySpec spec = new DESedeKeySpec(keyBytes);
		SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("DESede");
		deskey = keyfactory.generateSecret(spec);
		Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
		cipher.init(2, deskey);
		byte[] bOut = cipher.doFinal(data);
		return new String(bOut, "GBK");
	}

}
