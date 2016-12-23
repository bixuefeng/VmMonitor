package com.vm.sql;

import java.security.MessageDigest;
import Decoder.BASE64Encoder;

public class EncoderByMd5 {
	public static String Encoder(String passWord) throws Exception {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		BASE64Encoder base64 = new BASE64Encoder();
		String newPassWord = base64.encode(md5.digest(passWord.getBytes("utf-8")));
		return newPassWord;
	}
}
