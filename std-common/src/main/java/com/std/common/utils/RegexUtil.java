
package com.std.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
/**
 * 
 * 校验类中所有方法,如果参数为空和null,则返回false
 */
public class RegexUtil
{
	private static String MOBILE = "^(0|86|(\\+86)|17951)?1[3,4,5,7,8][0-9]\\d{8}$";

	private static String EMAIL = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";

	private static String IP = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";

	private static String TELEPHONE = "0\\d{2,3}-\\d{7,8}|0\\d{4}-\\d{7,8}";

	private static String DATE = "\\d{4}-(0[1-9]|1[0-2])-([0][1-9]|[1,2][0-9]|3[0-1])";

	private static String FULL_DATE = "((^((1[8-9]\\d{2})|([2-9]\\d{3}))(-)(10|12|0?[13578])(-)(3[01]|[12][0-9]|0?[1-9])$)"
			+ "|(^((1[8-9]\\d{2})|([2-9]\\d{3}))(-)(11|0?[469])(-)(30|[12][0-9]|0?[1-9])$)|(^((1[8-9]"
			+ "\\d{2})|([2-9]\\d{3}))(-)(0?2)(-)(2[0-8]|1[0-9]|0?[1-9])$)|(^([2468][048]00)(-)(0?2)(-)"
			+ "(29)$)|(^([3579][26]00)(-)(0?2)(-)(29)$)|(^([1][89][0][48])(-)(0?2)(-)(29)$)|(^([2-9][0-9]"
			+ "[0][48])(-)(0?2)(-)(29)$)|(^([1][89][2468][048])(-)(0?2)(-)(29)$)|(^([2-9][0-9][2468][048])"
			+ "(-)(0?2)(-)(29)$)|(^([1][89][13579][26])(-)(0?2)(-)(29)$)|(^([2-9][0-9][13579][26])(-)(0?2)"
			+ "(-)(29)$))";

	private static String AGE = "120|((1[0-1]|\\d)?\\d)";

	private static String CERT = "[\\d]{6}(19)?[\\d]{2}((0[1-9])|(10|11|12))([012][\\d]|(30|31))[\\d]{3}[xX\\d]*";

	private static String MONEY = "^(([1-9]\\d*)|0)(\\.\\d{1,2})?$";
	private static String POS_INTEGER = "^[1-9]\\d*$";
	private static String DIGIT = "^[0-9]\\d*$";
	private static String VALIDATE_CODE = "^\\d{6}$";
	private static String ALL_DIGIT = "^\\d*(.\\d+)?$";
	private static String ACCOUNT = "[a-zA-Z0-9_\\.@]{1,}";
	private static String CHINESE_CHARACTER = "[\u4e00-\u9fa5]";
	
	/**
	 * @return 不正确的格式,参数为null和空都返回false
	 */
	public static boolean chineseCharacterMatch(String str){
		if(StringUtils.isNotEmpty(str)){
			Pattern p = Pattern.compile(CHINESE_CHARACTER);
			Matcher m = p.matcher(str);
			return m.find();
		}
		return false;
	}
	
	/**
	 * @return 不正确的格式,参数为null和空都返回false
	 */
	public static boolean emailMatch(String email)
	{
		if (StringUtils.isNotEmpty(email))
		{
			return email.matches(EMAIL);
		}
		return false;
	}

	/**
	 * 校验手机.
	 * 
	 * @return 不正确的格式,参数为null和空都返回false
	 */
	public static boolean mobileMacth(String mobile)
	{
		if (StringUtils.isNotEmpty(mobile))
		{
			return mobile.matches(MOBILE);
		}
		return false;
	}

	/**
	 * @return 不正确的格式,参数为null和空都返回false
	 */
	public static boolean ipMacth(String ip)
	{
		if (StringUtils.isNotEmpty(ip))
		{
			return ip.matches(IP);
		}
		return false;
	}

	/**
	 * 校验固定电话号码.
	 * 
	 * @return 不正确的格式,参数为null和空都返回false
	 */
	public static boolean telMacth(String tel)
	{
		if (StringUtils.isNotEmpty(tel))
		{
			return tel.matches(TELEPHONE);
		}
		return false;
	}

	/**
	 * 年份从1900-2099 该方法只能校验简单的日期.无法校验闰年,平年. 也无法校验大月和小月以及二月的月末
	 * 
	 * @return 不正确的格式,参数为null和空都返回false
	 */
	public static boolean simpleDateMatch(String date)
	{
		if (StringUtils.isNotEmpty(date))
		{
			return date.matches(DATE);
		}
		return false;
	}

	/**
	 * 年份从1800-9999 该方法支持闰年平年的检验.以及各月的月末
	 * 
	 * @return 不正确的格式,参数为null和空都返回false
	 */
	public static boolean dateMatch(String date)
	{
		if (StringUtils.isNotEmpty(date))
		{
			return date.matches(FULL_DATE);
		}
		return false;
	}

	/**
	 * 年龄范围0-120
	 * 
	 * @return 不正确的格式,参数为null和空都返回false
	 */
	public static boolean ageMatch(String age)
	{
		if (StringUtils.isNotEmpty(age))
		{
			return age.matches(AGE);
		}
		return false;
	}

	/**
	 * 身份证为15或18位,且生日在1900-1999年之间,支持最后位是x的.大小写都可以
	 * 
	 * @return 不正确的格式,参数为null和空都返回false
	 */
	public static boolean simpleCertMatch(String cert)
	{
		if (StringUtils.isNotEmpty(cert))
		{
			return cert.matches(CERT);
		}
		return false;
	}

	/**
	 * 校验金额.
	 * 
	 * @return 不正确的格式,参数为null和空都返回false
	 */
	public static boolean moneyMatch(String money)
	{
		if (StringUtils.isNotEmpty(money))
		{
			return money.matches(MONEY);
		}
		return false;
	}

	public static boolean posInteger(String num)
	{
		if (StringUtils.isNotEmpty(num))
		{
			return num.matches(POS_INTEGER);
		}
		return false;
	}

	public static boolean validateCode(String num)
	{
		if (StringUtils.isNotEmpty(num))
		{
			return num.matches(VALIDATE_CODE);
		}
		return false;
	}

	public static boolean isDigit(String num)
	{
		if (StringUtils.isNotEmpty(num))
		{
			return num.matches(DIGIT);
		}
		return false;
	}
	public static boolean allDigit(String num)
	{
		if (StringUtils.isNotEmpty(num))
		{
			return num.matches(ALL_DIGIT);
		}
		return false;
	}
	public static boolean accountCheck(String account)
	{
		if (StringUtils.isNotEmpty(account))
		{
			return account.matches(ACCOUNT);
		}
		return false;
	}

	/**
	 * 非法字符替换
	 * 
	 * @param str
	 * @return
	 */
	public static String repInvalidStr(String str)
	{
		String regEx = "[`~!#$%^&*()+=|{}':;',\\[\\].<>/?~！#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}
}
