package utils;

/**
 * Encapsulates a three-component color in r, g, b.
 */
public class Color3f {
	public float r, g, b;
	
	/**
	 * Default constructor, sets r, g, b to 0.
	 */
	public Color3f() {}
	
	/**
	 * Creates a new color with r, g, and b set to v.
	 * @param v value to set r, g, b to
	 */
	public Color3f(float v) {
		r = v;
		g = v;
		b = v;
	}
	
	/**
	 * Creates a new color with the given r, g, b values.
	 * @param r red value
	 * @param g green value
	 * @param b blue value
	 */
	public Color3f(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	/**
	 * Creats a copy of the given color
	 * @param c
	 */
	public Color3f(Color3f c) {
		this.r = c.r;
		this.g = c.g;
		this.b = c.b;
	}
	
	
	/**
	 * Multiplies this color by the given color.
	 * @param c color to multiply
	 * @return the result of the multiplication as a new Color3f object
	 */
	public Color3f mul(Color3f c) {
		return new Color3f(r * c.r, g * c.g, b * c.b);
	}
	
	/**
	 * Multiplies this color by the given color and store the result in this.
	 * @param c color to multiply
	 * @return this
	 */
	public Color3f mulThis(Color3f c) {
		this.r *= c.r;
		this.g *= c.g;
		this.b *= c.b;
		return this;
	}
	
	/**
	 * Multiplies this color by the given scalar.
	 * @param f scalar to multiply
	 * @return the result of the multiplication as a new Color3f object
	 */
	public Color3f mul(float f) {
		return new Color3f(r * f, g * f, b * f);
	}
	
	/**
	 * Multiplies this color by the given scalar and store the result in this.
	 * @param f scalar to multiply
	 * @return this
	 */
	public Color3f mulThis(float f) {
		r *= f;
		g *= f;
		b *= f;
		return this;
	}
	
	/**
	 * Adds the given color to this color.
	 * @param c color to add
	 * @return the result of the addition as a new Color3f object
	 */
	public Color3f add(Color3f c) {
		return new Color3f(r + c.r, g + c.g, b + c.b);
	}
	
	/**
	 * Adds the given color to this color and store the result in this.
	 * @param c color to add
	 * @return this
	 */
	public Color3f addThis(Color3f c) {
		this.r += c.r;
		this.g += c.g;
		this.b += c.b;
		return this;
	}
	
	/**
	 * Returns the RGB value of this color packed into an int. Currently calls out to java.awt.Color.getRGB().
	 * @return RGB value of this color
	 */
	public int getRGB() {
		Color3f c = this.clamp();
		return new java.awt.Color(c.r, c.g, c.b).getRGB();
	}
	
	/**
	 * Clamps the r, g, b values of this color to between 0 and 1,
	 * and returns the result as a new Color3f object.
	 * @return the clamped color as a new Color3f object
	 */
	public Color3f clamp() {
		return new Color3f(Math.min(Math.max(r, 0.0f), 1.0f),
				Math.min(Math.max(g, 0.0f), 1.0f),
				Math.min(Math.max(b, 0.0f), 1.0f));
	}
	
	/**
	 * Clamps the r, g, b values of this color between 0 and 1,
	 * and stores the result in this.
	 * @return this
	 */
	public Color3f clampThis() {
		r = Math.min(Math.max(r, 0.0f), 1.0f);
		g = Math.min(Math.max(g, 0.0f), 1.0f);
		b = Math.min(Math.max(b, 0.0f), 1.0f);
		return this;
	}
	
	@Override
	public String toString() {
		return "(" + r + ", " + g + ", " + b + ")";
	}
	
	public static final Color3f black = new Color3f();
	public static final Color3f white = new Color3f(1.0f);
	public static final Color3f red = new Color3f(1.0f, 0.0f, 0.0f);
	public static final Color3f green = new Color3f(0.0f, 1.0f, 0.0f);
	public static final Color3f blue = new Color3f(0.0f, 0.0f, 1.0f);
	public static final Color3f purple = new Color3f(1.0f, 0.0f, 1.0f);
	
}
