package utils.math.geom;

/**
 * Represents a 2-component vector of doubles
 */
public class Vector2d {
	
	public double x, y;
	
	/**
	 * Creates a new vector with the given components
	 * @param x x component
	 * @param y y component
	 */
	public Vector2d(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Creates a new vector with both components set to the given value
	 * @param d value of both components
	 */
	public Vector2d(double d) {
		this.x = d;
		this.y = d;
	}
	
	/**
	 * Creates a new vector with value (0, 0)
	 */
	public Vector2d() {
		this.x = 0;
		this.y = 0;
	}
	
	/**
	 * Adds this vector and the given vector
	 * @param v vector to add
	 * @return result of the addition
	 */
	public Vector2d add(Vector2d v) {
		return new Vector2d(x + v.x, y + v.y);
	}
	
	/**
	 * Subtracts the given vector from this vector
	 * @param v vector to subtract
	 * @return result of the subtraction
	 */
	public Vector2d subtract(Vector2d v) {
		return new Vector2d(x - v.x, y - v.y);
	}
	
	/**
	 * Multiplies this vector by a scalar
	 * @param s scalar value
	 * @return the result of the multiplication
	 */
	public Vector2d mul(double s) {
		return new Vector2d(x*s, y*s);
	}
	
	/**
	 * Dots this vector with the given vector
	 * @param v vector to dot
	 * @return the dot product of this vector and the given vector
	 */
	public double dot(Vector2d v) {
		return x*v.x + y*v.y;
	}
	
	/**
	 * Normalizes this vector
	 * @return this vector normalized
	 */
	public Vector2d normalize() {
		double r = Math.sqrt(this.dot(this));
		return new Vector2d(x/r, y/r);
	}
	
	/**
	 * Calculates the distance between this vector and v
	 * @param v vector to find distance to
	 * @return distance between the two vectors
	 */
	public double dist(Vector2d v) {
		Vector2d l = this.subtract(v);
		return Math.sqrt(l.dot(l));
	}
	
	/**
	 * Calculates the square of the distance between this vector and v
	 * @param v vector to find distance to
	 * @return square of the distance bewteen the two vectors
	 */
	public double dist2(Vector2d v) {
		Vector2d l = this.subtract(v);
		return l.dot(l);
	}
}
