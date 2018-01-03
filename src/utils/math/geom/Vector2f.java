package utils.math.geom;

/**
 * Represents a 2-component vector of floats
 */
public class Vector2f {
	
	public float x, y;
	
	/**
	 * Creates a new vector with the given components
	 * @param x x component
	 * @param y y component
	 */
	public Vector2f(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Creates a new vector with both components set to the given value
	 * @param d value of both components
	 */
	public Vector2f(float d) {
		this.x = d;
		this.y = d;
	}
	
	/**
	 * Creates a new vector with value (0, 0)
	 */
	public Vector2f() {
		this.x = 0;
		this.y = 0;
	}
	
	/**
	 * Adds this vector and the given vector
	 * @param v vector to add
	 * @return result of the addition
	 */
	public Vector2f add(Vector2f v) {
		return new Vector2f(x + v.x, y + v.y);
	}
	
	/**
	 * Subtracts the given vector from this vector
	 * @param v vector to subtract
	 * @return result of the subtraction
	 */
	public Vector2f subtract(Vector2f v) {
		return new Vector2f(x - v.x, y - v.y);
	}
	
	/**
	 * Multiplies this vector by a scalar
	 * @param s scalar value
	 * @return the result of the multiplication
	 */
	public Vector2f mul(float s) {
		return new Vector2f(x*s, y*s);
	}
	
	/**
	 * Dots this vector with the given vector
	 * @param v vector to dot
	 * @return the dot product of this vector and the given vector
	 */
	public float dot(Vector2f v) {
		return x*v.x + y*v.y;
	}
	
	/**
	 * Normalizes this vector
	 * @return this vector normalized
	 */
	public Vector2f normalize() {
		float r = (float) Math.sqrt(this.dot(this));
		return new Vector2f(x/r, y/r);
	}
	
	/**
	 * Calculates the distance between this vector and v
	 * @param v vector to find distance to
	 * @return distance between the two vectors
	 */
	public float dist(Vector2f v) {
		Vector2f l = this.subtract(v);
		return (float) Math.sqrt(l.dot(l));
	}
	
	/**
	 * Calculates the square of the distance between this vector and v
	 * @param v vector to find distance to
	 * @return square of the distance bewteen the two vectors
	 */
	public float dist2(Vector2f v) {
		Vector2f l = this.subtract(v);
		return l.dot(l);
	}
}
