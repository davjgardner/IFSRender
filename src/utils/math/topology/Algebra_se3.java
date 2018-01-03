package utils.math.topology;

import utils.math.mat.Mat2;

/**
 * implementation of se(3) algebra, and some utility functions
 * @author David Gardner
 * 09/14/2015
 */
public class Algebra_se3 {

	/**
	 * r the position component {x, y, z}
	 */
	double[] r = new double[3];
	/**
	 * omega the rotation component {xrot, yrot, zrot}
	 */
	double[] omega = new double[3];
	
	public Algebra_se3(double[] r, double[] omega) {
		this.r = r; this.omega = omega;
	}
	
	/**
	 * Exponential function, se(3) to SE(3)
	 * @param a algebra element in se(3)
	 * @return exp(a) in SE(3)
	 */
	public static SE3 exp(Algebra_se3 a) {
		double srx = a.omega[0]*a.omega[0]; //square of x rotation
		double sry = a.omega[1]*a.omega[1];
		double srz = a.omega[2]*a.omega[2];
		double stheta = srx + sry + srz; //square of normal
		double theta = Math.sqrt(stheta); //normal
		
		Mat2 I3 = Mat2.initIdentity(3);
		Mat2 omegax = new Mat2(toSkewSymmetric(a.omega)); //[w]x
		Mat2 somegax = omegax.mult(omegax); //[w]x^2
		
		Mat2 omegaxs = omegax.mult(Math.sin(theta)/theta); //scaled
		Mat2 somegaxs = somegax.mult((1-Math.cos(theta))/stheta);
		
		double[][] SO3 = I3.add(omegaxs).add(somegaxs).getData();
		
		Mat2 somegaxs2 = somegax.mult( (theta - Math.sin(theta)) / (stheta*theta) );
		
		Mat2 V = I3.add(somegaxs).add(somegaxs2);
		
		Mat2 m_t = V.mult(new Mat2(new double[][] {{a.r[0]}, {a.r[1]}, {a.r[2]}}));
		
		return new SE3(SO3, new double[] {m_t.get(0, 0), m_t.get(1, 0), m_t.get(2, 0)});
	}
	
	/** 
	 * 
	 * @param m {m1, m2, m3}
	 * @return Skew-Symmetric matrix of m (3x3)
	 */
	private static double[][] toSkewSymmetric(double[] m) {
		double[][] ss = {{    0, -m[2],  m[1]},
						 { m[2],     0, -m[0]},
						 {-m[1],  m[0],    0}};
		return ss;
	}

}
