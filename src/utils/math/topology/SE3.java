package utils.math.topology;

import java.util.Arrays;

import utils.math.geom.Vector3d;
import utils.math.mat.Mat2;

/**
 * Implementation of SE(3) group
 * @author david
 * 09/14/2015
 */
public class SE3 {

	/**
	 * r position component [x, y, z]
	 */
	double[] r = new double[3];
	/**
	 * rot rotation component in SO(3)
	 */
	double[][] rot = new double[3][3];
	
	public SE3(double[][] rot, double[] r) {
		this.rot = rot; this.r = r;
	}
	
	public SE3(Mat2 m) {
		if(m.getRows() != 4 || m.getCols() != 4) throw new IllegalArgumentException("Must be a 4x4 matrix");
		r = new double[] {m.get(0, 3), m.get(1, 3), m.get(2, 3)};
		for(int i=0; i<3; i++) {
			for(int j=0; j<3; j++) {
				rot[i][j] = m.get(i, j);
			}
		}
		
	}
	
	public SE3(Vector3d p) {
		double[] p_array = {p.x, p.y, p.z};
		r = p_array;
		rot = Mat2.initIdentity(3).getData();
	}

	/**
	 * 
	 * @return 4x4 matrix in SE(3)
	 */
	public Mat2 get4Mat() {
		double[][] data = new double[4][4];
		for(int i=0; i<3; i++) {
			for(int j=0; j<3; j++) {
				data[i][j] = rot[i][j];
			}
		}
		data[0][3] = r[0];
		data[1][3] = r[1];
		data[2][3] = r[2];
		data[3][3] = 1;
		return new Mat2(data);
	}
	
	public Vector3d getPos() {
		return new Vector3d(r[0], r[1], r[2]);
	}

	public double[] getR() {
		return r;
	}

	public void setR(double[] r) {
		this.r = r;
	}

	public double[][] getRot() {
		return rot;
	}

	public void setRot(double[][] rot) {
		this.rot = rot;
	}
	
	/**
	 * Perform s * this state: transform this state by s
	 * @param s transformation state
	 * @return new state
	 */
	public SE3 transform(SE3 s) {
		return new SE3(this.get4Mat().mult(s.get4Mat()));
	}
	
	@Override
	public String toString() {
		return Arrays.toString(rot[0]) + ",\n" + Arrays.toString(rot[1]) + ",\n" + Arrays.toString(rot[2]) + "\n" + getPos().toString();
	}

}
