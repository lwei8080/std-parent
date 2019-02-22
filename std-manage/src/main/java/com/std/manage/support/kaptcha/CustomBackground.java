package com.std.manage.support.kaptcha;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import com.google.code.kaptcha.BackgroundProducer;
import com.google.code.kaptcha.util.Configurable;

public class CustomBackground extends Configurable implements
		BackgroundProducer {
	
	private Random random = new Random();

	@Override
	public BufferedImage addBackground(BufferedImage baseImage) {
		int width = baseImage.getWidth();
		int height = baseImage.getHeight();

		// create an opaque image
		BufferedImage imageWithBackground = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);

		Graphics2D graph = (Graphics2D) imageWithBackground.getGraphics();
		RenderingHints hints = new RenderingHints(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);

		hints.add(new RenderingHints(RenderingHints.KEY_COLOR_RENDERING,
				RenderingHints.VALUE_COLOR_RENDER_QUALITY));
		hints.add(new RenderingHints(RenderingHints.KEY_ALPHA_INTERPOLATION,
				RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY));

		hints.add(new RenderingHints(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY));

		graph.setRenderingHints(hints);
		GradientPaint paint = new GradientPaint(0, 0, new Color(211,211,211), width, height,new Color(255,255,255));
		graph.setPaint(paint);
		
		//graph.setColor(getRandomColor(200, 250));
		
		graph.fill(new Rectangle2D.Double(0, 0, width, height));

		// draw the transparent image over the background
		graph.drawImage(baseImage, 0, 0, null);

		return imageWithBackground;
	}
	
    // 得到随机颜色
    private Color getRandomColor(int fc, int bc) {// 给定范围获得随机颜色
        if (fc > 255)
            fc = 255;
        if (bc > 255)
            bc = 255;
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

    // 得到随机颜色
    private Color getRandomColor(int r_min, int r_max, int g_min, int g_max, int b_min, int b_max) {// 给定范围获得随机颜色
        if (r_max > 255)
        	r_max = 255;
        if (g_max > 255)
        	g_max = 255;
        if (b_max > 255)
        	b_max = 255;
        int r = getRandom(r_min, r_max);
        int g = getRandom(g_min, g_max);
        int b = getRandom(b_min, b_max);
        return new Color(r, g, b);
    }
    
	/**
	 * 获取min - max 之间的随机数 max > min
	 * @param max
	 * @param min
	 * @return
	 */
	public static int getRandom(int min , int max){
		if(max<=min){
			return 0;
		}
		return new Random().nextInt(max + 1 - min) + min;
	}
}
