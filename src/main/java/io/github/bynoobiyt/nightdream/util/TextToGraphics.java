/*
 * Copyright (c) danthe1st and byNoobiYT 2019.
 * File: TextToGraphics.java
 * Project: NightDream
 * All rights reserved!
 */

package io.github.bynoobiyt.nightdream.util;

import net.dv8tion.jda.api.entities.MessageChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;

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
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TextToGraphics implements Runnable {
	
	private static final Font FONT_BODY;
	private static final Font FONT_NOSPACE;
	private static final Color BG_COLOR=Color.WHITE;
	private static final Color FG_COLOR=new Color(0x212121);
	private static final Logger LOG=LoggerFactory.getLogger(TextToGraphics.class);
	private static TextToGraphics executor=new TextToGraphics();
	private static final Thread graphicsThread=new Thread(executor);
	
	private Queue<Runnable> waiting=new LinkedBlockingQueue<>();
	
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
		FONT_BODY=body.deriveFont(10F);
		FONT_NOSPACE=heading.deriveFont(12F).deriveFont(Font.BOLD);
		
		graphicsThread.start();
	}
	
	private TextToGraphics() {
		//prevent instantiation
	}
	public static void sendTextAsImage(MessageChannel chan, String imgName, String imgText, String metaText) {
		synchronized (executor) {
			executor.waiting.add(()->{
				try(ByteArrayOutputStream baos=new ByteArrayOutputStream()){
					createImage(imgText,baos);
					baos.flush();
					chan.sendMessage(metaText).addFile(baos.toByteArray(), imgName).queue();
				} catch (IOException e) {
					LOG.warn("Error while generating image from text: \n"+imgText,e);
				}
			});
			executor.notifyAll();
		}
	}
    private static void createImage(String text,OutputStream out) throws IOException {
    	text=text.replace("\t", "    ");
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setFont(FONT_NOSPACE);
        final FontMetrics fm = g2d.getFontMetrics();
        int width = Stream.of(NEWLINE_REGEX.split(text)).collect(Collectors.summarizingInt(fm::stringWidth)).getMax();
        int height = fm.getHeight()*NEWLINE_REGEX.split(text).length+1;
        
        g2d.dispose();

        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        g2d = img.createGraphics();
        
        g2d.setBackground(BG_COLOR);
        g2d.clearRect(0, 0, width, height);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.setColor(FG_COLOR);
        
        drawString(g2d,text,0,0);
        g2d.dispose();
        
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
	@Override
	public void run() {
		while(!Thread.currentThread().isInterrupted()) {
			try {
				synchronized (this) {
					if(waiting.isEmpty()) {
						this.wait();
					}
				}
				while(!waiting.isEmpty()) {
					waiting.poll().run();
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}
}
