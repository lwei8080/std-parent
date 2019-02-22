package com.std.manage.support.kaptcha;

import java.util.Random;

import com.google.code.kaptcha.text.TextProducer;

public class CalculationProducer implements TextProducer {
	
	private Random random = new Random();;

	public String getText() {
		int a = this.random.nextInt(11);
		int b = this.random.nextInt(11);
		String plus = (a < b) ? "乘" : ((a + b) % 2 == 1) ? "加" : "减";
		String[] num_cn = { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖","拾" };

		String text = String.valueOf(num_cn[a] + plus + num_cn[b]);
		String result = String.valueOf((a < b) ? a * b : ((a + b) % 2 == 1) ? a + b : a - b);
		
		return text;
	}

}
