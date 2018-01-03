package utils.math;

public class MathUtils {

	/**
	 * Raise a complex number to an integer power: <re, im*i>^n where n>=0
	 * Modified code taken from here:
	 * 	https://github.com/Arkainnihx/COMP1206-Coursework-Fractal/blob/master/src/com/github/arkainnihx/comp1206/coursework/fractal/Complex.java
	 * @param re real component
	 * @param im imaginary component
	 * @param n power
	 * @return {re, im}
	 */
	public static double[] cpow(double re, double im, int n) {
		if (n == 0) {
			return new double[] {1, 0};
		}
		if (n == 1) {
			return new double[] {re, im};
		}
		if (n == 2) {
			return csquare(re, im);
		}
		/*if (n < 0) {
			Complex cPow = pow(-n);
			return new Complex(1, 0).divide(cPow);
		}*/
		if ((Math.log(n) / Math.log(2)) % 1 == 0) { //to a power of 2
			return csquare(cpow(re, im, n / 2));
		}
		int closestnOfTwo = (int) Math.floor((Math.log(n) / Math.log(2)));
		double[] cPow = cpow(re, im, (int) Math.pow(2, closestnOfTwo));
		for (int count = (int) Math.pow(2, closestnOfTwo); count < n; count++) {
			cPow = cmult(cPow, new double[] {re, im});
		}
		return cPow;
	}
	
	/**
	 * Raise a complex number to an integer power: <re, im*i>^n where n>=0
	 * @param c {re, im}
	 * @param n power
	 * @return {re, im}
	 */
	public static double[] cpow(double[] c, int n) {
		return cpow(c[0], c[1], n);
	}
	
	/**
	 * Square a complex number
	 * @param re real component
	 * @param im imaginary component
	 * @return {re, im}
	 */
	public static double[] csquare(double re, double im) {
		return new double[] {re*re - im*im, 2*re*im};
	}
	
	/**
	 * Square a complex number
	 * @param c {re, im}
	 * @return {re, im}
	 */
	public static double[] csquare(double[] c) {
		return csquare(c[0], c[1]);
	}
	
	/**
	 * Multiply two complex numbers
	 * @param c1 {re1, im1}
	 * @param c2 {re2, im2}
	 * @return {re, im}
	 */
	public static double[] cmult(double[] c1, double[] c2) {
		double re = c1[0]*c2[0] - c1[1]*c2[1];
		double im = c1[0]*c2[1] + c1[1]*c2[0];
		return new double[] {re, im};
	}

}
