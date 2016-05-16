package engine;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import engine.math.Color;

public class Texture {
	public static final Texture error;
	static {
		BufferedImage errbmp = new BufferedImage(4, 4, BufferedImage.TYPE_INT_ARGB);
		Graphics2D canvas = errbmp.createGraphics();
		canvas.setColor(java.awt.Color.magenta);
		canvas.fillRect(0, 0, errbmp.getWidth(), errbmp.getHeight());
		canvas.dispose();
		error = new Texture(errbmp);
	}
	
	private Color[] pixels;
	private int width, height;
	public float repeatX = 1, repeatY = 1;
	private int offsetX, offsetY;
	
	// Textures must be a power of 2
	public Texture(String path) throws IOException {
		this(ImageIO.read(new File(path)));
	}
	
	public Texture(BufferedImage tex) {
		width = tex.getWidth();
		height = tex.getHeight();
		
		this.pixels = new Color[width * height];
		int i=0;
		int color = 0;
		for (int y=0; y<height; y++)
			for (int x=0; x<width; x++) {
				color = tex.getRGB(x, y);
				this.pixels[i++] = new Color(color);
			}
	}
	
	public void setOffsetX(int offset) {
		offsetX = offset % width;
	}
	public void setOffsetY(int offset) {
		offsetY = offset % height;
	}
	
	public Color map(float tu, float tv) {
		tu = Math.abs(tu);
		tv = Math.abs(tv);
		int u = (int)(tu * width * repeatX + offsetX);
		int v = (int)(tv * height * repeatY + offsetY);
		u &= width-1;
		v &= height-1;
		Color pixel = pixels[v*width+u];
		return pixel;
	}
}