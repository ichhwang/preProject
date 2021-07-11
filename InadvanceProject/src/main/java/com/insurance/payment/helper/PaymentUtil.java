package com.insurance.payment.helper;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PaymentUtil {
	
	/**
	 * 20자리 key를 리턴
	 * @return key
	 */
	public static String makeKey() {
		String key = new String();
        key = String.valueOf((int)(Math.random()*999));     
        if(key.length() < 3) {
        	int size = key.length();
        	for(int i=0; i<3-size; i++) {
        		key = "0"+key;
        	}
        }
        key = key.substring(0,3);
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Calendar dateTime = Calendar.getInstance();
        key = key + sdf.format(dateTime.getTime());
		
		return key;
	}
	
	/**
	 * 암호화
	 * @param str
	 * @return
	 */
	public static String encrypt(String str) {
		String result = new String();

		try {
			AES256Util aes = new AES256Util();
			result = aes.encrypt(str);
		} catch (UnsupportedEncodingException | GeneralSecurityException e) {
			
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * 복호화
	 * @param str
	 * @return
	 */
	public static String decrypt(String str) {
		String result = new String();
		str = str.replace(" ", "");
		try {
			AES256Util aes = new AES256Util();
			result = aes.decrypt(str);
		} catch (UnsupportedEncodingException | GeneralSecurityException e) {
			
			e.printStackTrace();
		}
		
		return result;
	}
	
}
