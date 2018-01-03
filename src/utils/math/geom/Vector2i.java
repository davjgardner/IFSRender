package utils.math.geom;

/**
 * Represents a 2-component vector of integers
 */
public class Vector2i {
	
	public int x, y;
	
	/**
	 * Creates a new vector with the given components
	 * @param x x component
	 * @param y y component
	 */
	public Vector2i(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Creates a new vector with both components set to the given value
	 * @param d value of both components
	 */
	public Vector2i(int d) {
		this.x = d;
		this.y = d;
	}
	
	/**
	 * Creates a new vector with value (0, 0)
	 */
	public Vector2i() {
		this.x = 0;
		this.y = 0;
	}
	
	/**
	 * Adds this vector and the given vector
	 * @param v vector to add
	 * @return result of the addition
	 */
	public Vector2i add(Vector2i v) {
		return new Vector2i(x + v.x, y + v.y);
	}
	
	/**
	 * Subtracts the given vector from this vector
	 * @param v vector to subtract
	 * @return result of the subtraction
	 */
	public Vector2i subtract(Vector2i v) {
		return new Vector2i(x - v.x, y - v.y);
	}
	
	/**
	 * Multiplies this vector by a scalar
	 * @param s scalar value
	 * @return the result of the multiplication
	 */
	public Vector2i mul(int s) {
		return new Vector2i(x*s, y*s);
	}
	
	/**
	 * Dots this vector with the given vector
	 * @param v vector to dot
	 * @return the dot product of this vector and the given vector
	 */
	public int dot(Vector2i v) {
		return x*v.x + y*v.y;
	}
	
	/**
	 * Normalizes this vector
	 * @return this vector normalized
	 */
	public Vector2i normalize() {
		double r = Math.sqrt(this.dot(this));
		return new Vector2i((int)(x/r), (int)(y/r));
	}
	
	/**
	 * Calculates the distance between this vector and v
	 * @param v vector to find distance to
	 * @return distance between the two vectors
	 */
	public int dist(Vector2i v) {
		Vector2i l = this.subtract(v);
		return (int) Math.sqrt(l.dot(l));
	}
	
	/**
	 * Calculates the square of the distance between this vector and v
	 * @param v vector to find distance to
	 * @return square of the distance bewteen the two vectors
	 */
	public int dist2(Vector2i v) {
		Vector2i l = this.subtract(v);
		return (int) l.dot(l);
	}
}
