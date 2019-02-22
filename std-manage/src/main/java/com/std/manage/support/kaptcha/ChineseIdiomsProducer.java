package com.std.manage.support.kaptcha;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.google.code.kaptcha.text.TextProducer;

public class ChineseIdiomsProducer implements TextProducer {

	private static String[] simplifiedChineseTexts = null;

	private final String IDIOM_FILE = "/chinese.idiom";

	public ChineseIdiomsProducer() {
		if(null==simplifiedChineseTexts){
			simplifiedChineseTexts = readIdiom(IDIOM_FILE);
		}
	}

	@Override
	public String getText() {
		if(null==simplifiedChineseTexts){
			return "";
		}
		return simplifiedChineseTexts[new java.util.Random().nextInt(simplifiedChineseTexts.length)];
	}

	public String[] readIdiom(String path) {
		InputStream inputStream = super.getClass().getResourceAsStream(path);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(inputStream,"utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		String idiom = null;
		List<String> list = new ArrayList<String>();
		try {
			while ((idiom = reader.readLine()) != null)
				list.add(idiom.trim());
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return (String[]) list.toArray(new String[0]);
	}
}
