package com.std.manage.support.kaptcha;

import java.io.UnsupportedEncodingException;
import java.util.Random;

import com.google.code.kaptcha.text.TextProducer;

public class ChineseTextProducer implements TextProducer {
	private Random random = new Random();
	private final int LENGTH = 2;

	/**
	 * 原理是从汉字区位码找到汉字。在汉字区位码中分高位与底位， 且其中简体又有繁体。位数越前生成的汉字繁体的机率越大。
	 * 所以在本例中高位从171取，底位从161取， 去掉大部分的繁体和生僻字。但仍然会有！！
	 */
	@Override
	public String getText() {
		return getChinese(LENGTH);
	}
	
	private String getChinese(int length){
		StringBuilder text = new StringBuilder();
		try {
			int hightPos, lowPos; // 定义高低位
			for(int i = 0 ; i < length ; i++){
				hightPos = (176 + Math.abs(random.nextInt(39)));// 获取高位值
				lowPos = (161 + Math.abs(random.nextInt(93)));// 获取低位值
				byte[] b = new byte[2];
				b[0] = (new Integer(hightPos).byteValue());
				b[1] = (new Integer(lowPos).byteValue());
				text.append(new String(b, "GBK"));// 转成中文
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return text.toString();
	}
}
