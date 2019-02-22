package com.std.common.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.std.common.consts.CommonConsts;
/**
 * AES 加解密
 * @author L.Wei
 */
public class EncryptAES {
	private final static Logger logger = LoggerFactory.getLogger(EncryptAES.class);
	
	private static String ckey = CommonConsts.ENCRYPT_AES_CKEY;

	
	/** 
	 * 对字符串加密 
	 * @param str
	 * @return
	 */
	public static String encrypt(String str){
		// 加密，结果将byte数组转换为表示16进制值的字符串
		String result = null;
		try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(ckey.getBytes());
            kgen.init(128, random);  
            SecretKey secretKey = kgen.generateKey();  
            byte[] enCodeFormat = secretKey.getEncoded();  
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");  
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器   
            byte[] byteContent = str.getBytes("utf-8");  
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化   
			result = byteArr2HexStr(cipher.doFinal(byteContent));
		}catch (Exception e) {
			logger.error(getStackTrace(e));
		}
		return result;
	}

	/** 
	 * 对字符串解密 
	 * @param hexStr
	 * @return
	 */
	public static String decrypt(String hexStr){
		String result = null;
		try {
			//将表示16进制值的字符串转换为byte数组
			byte[] buff = hexStr2ByteArr(hexStr);
            KeyGenerator kgen = KeyGenerator.getInstance("AES");  
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(ckey.getBytes());
            kgen.init(128, random);  
            SecretKey secretKey = kgen.generateKey();  
            byte[] enCodeFormat = secretKey.getEncoded();  
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");              
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器   
            cipher.init(Cipher.DECRYPT_MODE, key);// 初始化  
			result = new String(cipher.doFinal(buff));
		}catch (Exception e) {
			logger.error(getStackTrace(e));
		}
		return result;
	}

	/** 
     * 将byte数组转换为表示16进制值的字符串， 如：byte[]{8,18}转换为：0813， 和public static byte[] 
     * hexStr2ByteArr(String strIn) 互为可逆的转换过程 
     *  
     * @param arrB 需要转换的byte数组 
     * @return 转换后的字符串 
     * @throws Exception 本方法不处理任何异常，所有异常全部抛出 
     */  
    private static String byteArr2HexStr(byte[] arrB) throws Exception {  
        int iLen = arrB.length;  
        // 每个byte用两个字符才能表示，所以字符串的长度是数组长度的两倍  
        StringBuffer sb = new StringBuffer(iLen * 2);  
        for (int i = 0; i < iLen; i++) {  
            int intTmp = arrB[i];  
            // 把负数转换为正数  
            while (intTmp < 0) {  
                intTmp = intTmp + 256;  
            }  
            // 小于0F的数需要在前面补0  
            if (intTmp < 16) {  
                sb.append("0");  
            }  
            sb.append(Integer.toString(intTmp, 16));  
        }  
        return sb.toString();  
    }  
  
    /** 
     * 将表示16进制值的字符串转换为byte数组， 和public static String byteArr2HexStr(byte[] arrB) 
     * 互为可逆的转换过程 
     *  
     * @param strIn 
     *            需要转换的字符串 
     * @return 转换后的byte数组 
     * @throws Exception 
     *             本方法不处理任何异常，所有异常全部抛出
     */  
    private static byte[] hexStr2ByteArr(String strIn) throws Exception {  
        byte[] arrB = strIn.getBytes();  
        int iLen = arrB.length;  
  
        // 两个字符表示一个字节，所以字节数组长度是字符串长度除以2  
        byte[] arrOut = new byte[iLen / 2];  
        for (int i = 0; i < iLen; i = i + 2) {  
            String strTmp = new String(arrB, i, 2);  
            arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);  
        }  
        return arrOut;  
    } 
    
    /** 
     * 获取异常的堆栈信息 
     *  
     * @param t 
     * @return 
     */  
    private static String getStackTrace(Throwable t)  {  
        StringWriter sw = new StringWriter();  
        PrintWriter pw = new PrintWriter(sw); 
        try {
            t.printStackTrace(pw);  
            return sw.toString();  
        } finally {  
            pw.close();  
        }  
    }  
    
    public static void main(String[] args) {
		System.out.println(encrypt("root"));
		System.out.println(encrypt("123456"));
		System.out.println(encrypt("@test#$(84567.*!"));
	}

}
