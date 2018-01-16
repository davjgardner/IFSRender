package utils.math.geom;

/**
 * Created by david on 1/17/2017.
 */
public class Vector3i {
	public int x, y, z;
	
	public Vector3i(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector3i(int d) {
		this.x = d;
		this.y = d;
		this.z = d;
	}
	
	public Vector3i() {}
	
	public Vector3i add(Vector3i v) {
		return new Vector3i(x + v.x, y + v.y, z + v.z);
	}
	
	public Vector3i subtract(Vector3i v) {
		return new Vector3i(x - v.x, y - v.y, z - v.z);
	}
	
	public Vector3i mul(int s) {
		return new Vector3i(x*s, y*s, z*s);
	}
	
	public int dot(Vector3i v) {
		return x*v.x + y*v.y + z*v.z;
	}
	
	public Vector3i normalize() {
		double r = Math.sqrt(this.dot(this));
		return new Vector3i((int)(x/r), (int)(y/r), (int)(z/r));
	}
	
	public int dist(Vector3i v) {
		Vector3i l = this.subtract(v);
		return (int) Math.sqrt(l.dot(l));
	}
	
	public int dist2(Vector3i v) {
		Vector3i l = this.subtract(v);
		return l.dot(l);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o.getClass() != Vector3i.class) return false;
		Vector3i v = (Vector3i) o;
		return x == v.x && y == v.y && z == v.z;
	}
	
	@Override
	public int hashCode() {
		return x * 51 + y * 37 + z * 17;
	}
	
}
