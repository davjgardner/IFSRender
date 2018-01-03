package utils.math.geom;

/**
 * Created by david on 1/17/2017.
 */
public class Vector3f {
	public float x, y, z;
	
	public Vector3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector3f(float d) {
		this.x = d;
		this.y = d;
		this.z = d;
	}
	
	public Vector3f() {}
	
	public Vector3f add(Vector3f v) {
		return new Vector3f(x + v.x, y + v.y, z + v.z);
	}
	
	public Vector3f subtract(Vector3f v) {
		return new Vector3f(x - v.x, y - v.y, z - v.z);
	}
	
	public Vector3f mul(float s) {
		return new Vector3f(x*s, y*s, z*s);
	}
	
	public float dot(Vector3f v) {
		return x*v.x + y*v.y + z*v.z;
	}
	
	public Vector3f normalize() {
		float r = (float) Math.sqrt(this.dot(this));
		return new Vector3f(x/r, y/r, z/r);
	}
	
	public float dist(Vector3f v) {
		Vector3f l = this.subtract(v);
		return (float) Math.sqrt(l.dot(l));
	}
	
	public float dist2(Vector3f v) {
		Vector3f l = this.subtract(v);
		return l.dot(l);
	}
}
