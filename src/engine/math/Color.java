package engine.math;

public class Color {
	public static final Color
	red = new Color(255,0,0),
	green = new Color(0,255,0),
	blue = new Color(0,0,255),
	black = new Color(0,0,0),
	white = new Color(255,255,255),
	alpha = new Color(0,0,0,0);
	
	//TODO: subtractive and additive modes
	public float r, g, b, a = 1;
	
	public Color(){}

	public Color(int argb) {
		set(argb);	
	}
	public Color(int a, int r, int g, int b) {
		set(
				(float)a / 255f,
				(float)r / 255f,
				(float)g / 255f,
				(float)b / 255f
			);
	}
	public Color(int r, int g, int b) {
		set(
				0,
				(float)r / 255f,
				(float)g / 255f,
				(float)b / 255f
			);
	}
	public Color(byte r, byte g, byte b) {
		set(
				0,
				(float)r / 255f,
				(float)g / 255f,
				(float)b / 255f
			);
	}
	public Color(float r, float g, float b) {
		set(0, r, g, b);
	}
	public Color(float a, float r, float g, float b) {
		set(a, r, g, b);
	}
	
	public void set(int argb) {
		set(
				(argb >> 24) & 0xFF,
				argb & 0xFF,
				(argb >> 8) & 0xFF,
				(argb >> 16) & 0xFF
			);	
	}
	public void set(int a, int r, int g, int b) {
		set(
			(float)(a / 255f),
			(float)(r / 255f),
			(float)(g / 255f),
			(float)(b / 255f)
		);
	}
	public void set(int r, int g, int b) {
		set(
				0f,
				(float)r / 255f,
				(float)g / 255f,
				(float)b / 255f
			);
	}
	public void set(byte r, byte g, byte b) {
		set(
				0,
				(float)r / 255f,
				(float)g / 255f,
				(float)b / 255f
			);
	}
	
	public void set(float a, float r, float g, float b) {
		if (!isValidColor(a, r, g, b))
			throw new IllegalArgumentException("ARGB value outside of supported range.");
		
		this.a = a;
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	public void set(Color c) {
		this.r = c.r;
		this.g = c.g;
		this.b = c.b; 
	}
	
	public Color add(Color c) {
		r += c.r * c.a;
		g += c.g * c.a;
		b += c.b * c.a;
		a += c.a;
		return this;
	}
	
	public Color subtract(Color c) {
		r -= c.r;
		g -= c.g;
		b -= c.b;
		return this;
	}
	
	public Color multiply(Color c) {
		r *= c.r;
		g *= c.g;
		b *= c.b;
		return this;
	}
	
	public Color multiply(float scale) {
		r *= scale;
		g *= scale;
		b *= scale;
		return this;
	}
	
	
	protected boolean isValidColor(float a, float r, float g, float b) {
		if (a > 1f) return false;
		if (r > 1f) return false;
		if (g > 1f) return false;
		if (b > 1f) return false;
		if (a < 0f) return false;
		if (r < 0f) return false;
		if (g < 0f) return false;
		if (b < 0f) return false;
		return true;
	}
	
	public void ensureScale() {
		a = Math.min(1, Math.max(0, a));
		r = Math.min(1, Math.max(0, r));
		g = Math.min(1, Math.max(0, g));
		b = Math.min(1, Math.max(0, b));
	}
	
	
	public static boolean hasAlpha(int argb) {
		return ((argb >> 24) & 0xFF) < 255;
	}
	
	
	public Color clone() {
		return new Color(a,r,g,b);
	}
	
	public java.awt.Color toColor() {
		return new java.awt.Color(r,g,b);
	}
	
	public int toARGB() {
		ensureScale();
		int
		a = (int)this.a,
		r = (int)(this.r * 255f),
		g = (int)(this.g * 255f),
		b = (int)(this.b * 255f);
		
		return (0xFF << 24) | (b << 16) | (g << 8) | r; 
	}
	
	private float invertChannel(float a) {
		return Math.abs(a - 1);
	}

}