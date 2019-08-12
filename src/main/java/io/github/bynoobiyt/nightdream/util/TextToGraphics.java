package io.github.bynoobiyt.nightdream.util;

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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import net.dv8tion.jda.api.entities.TextChannel;

public class TextToGraphics {
	
	private static final Font FONT_BODY;
	private static final Font FONT_NOSPACE;
	private static final Color BG_COLOR=Color.WHITE;
	private static final Color FG_COLOR=new Color(0x212121);
	
	static {
		Font body = new Font("Arial", Font.PLAIN, 48);
		Font heading = new Font("Arial", Font.BOLD, 48);
		try (InputStream bodyStream=new BufferedInputStream(TextToGraphics.class.getClassLoader().getResourceAsStream("fonts/RedHatDisplay-Black.ttf"));
				InputStream headingStream=new BufferedInputStream(TextToGraphics.class.getClassLoader().getResourceAsStream("fonts/RedHatDisplay-Bold.ttf"))){
			body = Font.createFont(Font.TRUETYPE_FONT, bodyStream);
			heading = Font.createFont(Font.TRUETYPE_FONT, headingStream);
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		FONT_BODY=body.deriveFont(48f);
		FONT_NOSPACE=heading.deriveFont(52f).deriveFont(Font.BOLD);
		
	}
	
	private TextToGraphics() {
		//prevent instantiation
	}
	public static void sendTextAsImage(TextChannel chan, String imgName, String imgText, String metaText) {
		try(ByteArrayOutputStream baos=new ByteArrayOutputStream()){
			createImage(imgText,baos);
			baos.flush();
			chan.sendFile(baos.toByteArray(), imgName).complete();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    private static void createImage(String text,OutputStream out) throws IOException {
    	text=text.replace("\t", "    ");
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setFont(FONT_NOSPACE);
        final FontMetrics fm = g2d.getFontMetrics();
        int width = Stream.of(text.split("\n")).collect(Collectors.summarizingInt((str)->fm.stringWidth(str))).getMax();
        int height = fm.getHeight()*text.split("\n").length+1;
        
        g2d.dispose();

        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
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
        ImageIO.write(img, "png", out);

    }
    private static void drawString(Graphics g, String text, int x, int y) {
        for (String line : text.split("\n")) {
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