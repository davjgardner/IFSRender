package utils.math.geom;

/**
 * Created by david on 1/17/2017.
 */
public class Vector3d {
	public double x, y, z;
	
	public Vector3d(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector3d(double d) {
		this.x = d;
		this.y = d;
		this.z = d;
	}
	
	public Vector3d() {}
	
	public Vector3d add(Vector3d v) {
		return new Vector3d(x + v.x, y + v.y, z + v.z);
	}
	
	public Vector3d subtract(Vector3d v) {
		return new Vector3d(x - v.x, y - v.y, z - v.z);
	}
	
	public Vector3d mul(double s) {
		return new Vector3d(x*s, y*s, z*s);
	}
	
	public double dot(Vector3d v) {
		return x*v.x + y*v.y + z*v.z;
	}
	
	public Vector3d normalize() {
		double r = Math.sqrt(this.dot(this));
		return new Vector3d(x/r, y/r, z/r);
	}
	
	public double dist(Vector3d v) {
		Vector3d l = this.subtract(v);
		return Math.sqrt(l.dot(l));
	}
	
	public double dist2(Vector3d v) {
		Vector3d l = this.subtract(v);
		return l.dot(l);
	}
}
