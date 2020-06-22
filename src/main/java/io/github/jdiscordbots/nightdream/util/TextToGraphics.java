/*
 * Copyright (c) JDiscordBots 2019 - 2020
 * File: TextToGraphics.java
 * Project: NightDream
 * Licensed under GNU General Public License 3.0
 */

package io.github.jdiscordbots.nightdream.util;

import net.dv8tion.jda.api.entities.MessageChannel;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * utility class for sending images from a given text as a Discord message
 */
public class TextToGraphics {
	
	private static final Font FONT_BODY;
	private static final Font FONT_NOSPACE;
	private static final Color BG_COLOR=Color.WHITE;
	private static final Color FG_COLOR=new Color(0x212121);
	private static final float FONT_SIZE_BODY=13F;
	private static final float FONT_SIZE_NOSPACE=14F;
	private static final Logger LOG=LoggerFactory.getLogger(TextToGraphics.class);
	private static final ExecutorService graphicsThreadPool;
	
	private static final Graphics2D calcG2d;
	
	private static final Pattern NEWLINE_REGEX=Pattern.compile("\n");
	
	static {
		Font body = new Font("Arial", Font.PLAIN, 1);
		Font heading = new Font("Arial", Font.BOLD, 1);
		try (InputStream bodyStream=new BufferedInputStream(TextToGraphics.class.getClassLoader().getResourceAsStream("fonts/RedHatDisplay-Black.ttf"));
				InputStream headingStream=new BufferedInputStream(TextToGraphics.class.getClassLoader().getResourceAsStream("fonts/RedHatDisplay-Bold.ttf"))){
			body = Font.createFont(Font.TRUETYPE_FONT, bodyStream);
			heading = Font.createFont(Font.TRUETYPE_FONT, headingStream);
		} catch (IOException|FontFormatException e) {
			LOG.warn("Error while loading fonts - Using Arial",e);
		}
		FONT_BODY= body.deriveFont(FONT_SIZE_BODY);
		FONT_NOSPACE=heading.deriveFont(FONT_SIZE_NOSPACE).deriveFont(Font.BOLD);
		graphicsThreadPool=Executors.newFixedThreadPool(1,r->{
			Thread t=new Thread(r);
			t.setDaemon(true);
			return t;
		});
		calcG2d = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).createGraphics();
		LOG.info("init");
		graphicsThreadPool.execute(()->{
			try {
				TextToGraphics.createImage("\n\t", new OutputStream() {
					@Override
					public void write(byte[] b, int off, int len) throws IOException {
						//ignore
					}
					@Override
					public void write(int b) throws IOException {
						//ignore
					}
				});
			} catch (IOException e) {//should never happen - an empty implementation of OutputStream cannot throw IOExceptions
				LOG.warn("A weird error occured while initializing text to graphics.");
			}
		});
		Runtime.getRuntime().addShutdownHook(new Thread(calcG2d::dispose));
	}
	public static void ensureInit() {
		//init done by static initializer
	}
	private TextToGraphics() {
		//prevent instantiation
	}
	/**
	 * converts a given text to an image and sends it
	 * @param chan the {@link MessageChannel} where the image should be sent to
	 * @param imgName the name of the image (without file extension)
	 * @param imgText the text that should be converted to the image
	 * @param metaText the text that should be sent with the image
	 */
	public static void sendTextAsImage(MessageChannel chan, String imgName, String imgText, String metaText) {
		graphicsThreadPool.execute(()->{
			try(ByteArrayOutputStream baos=new ByteArrayOutputStream()){
				createImage(imgText,baos);
				baos.flush();
				chan.sendMessage(metaText).addFile(baos.toByteArray(), imgName+".jpg").queue();
			} catch (IOException e) {
				LOG.warn("Error while generating image from text: \n{}",imgText,e);
			}
		});
	}
	private static FontMetrics getMetricsForLine(String line,FontMetrics fmNoSpace,FontMetrics fmBody) {
		return line.startsWith("\t")?fmBody:fmNoSpace;
	}
    private static void createImage(String text,OutputStream out) throws IOException {
    	text=text.replace("\t", "    ");
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        int width;
        int height;
        try {
            final FontMetrics fmNoSpace = g2d.getFontMetrics(FONT_BODY);
            
            final FontMetrics fmBody = g2d.getFontMetrics(FONT_NOSPACE);
            String[] split = NEWLINE_REGEX.split(text);
            int securityMargin=fmNoSpace.getHeight()/2;
			width = Stream.of(split).collect(Collectors.summarizingInt(line->getMetricsForLine(line,fmNoSpace,fmBody).stringWidth(line))).getMax()+securityMargin;
			height = Stream.of(split).collect(Collectors.summingInt(line->getMetricsForLine(line,fmNoSpace,fmBody).getHeight())).intValue()+securityMargin;
        }finally {
        	g2d.dispose();
        }
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        try {
        	g2d = img.createGraphics();
            
            g2d.setBackground(BG_COLOR);
            g2d.clearRect(0, 0, width, height);
            g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
            g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
            g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
            g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
            g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
            g2d.setColor(FG_COLOR);
            drawString(g2d,text,0,0);
        }finally {
        	g2d.dispose();
        }
        ImageIO.write(img, "JPG", out);
    }
    private static void drawString(Graphics g, String text, int x, int y) {
        for (String line : NEWLINE_REGEX.split(text)) {
        	if(line.startsWith(" ")) {
        		g.setFont(FONT_BODY);
        	}else {
        		g.setFont(FONT_NOSPACE);
        	}
        	y += g.getFontMetrics().getHeight();
            g.drawString(line, x+g.getFontMetrics().charWidth(' '), y);
        }
    }
}
