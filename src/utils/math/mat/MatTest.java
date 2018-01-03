package utils.math.mat;

public class MatTest {

	public MatTest() {
		
	}
	
	public static void main(String args[]) {
		Mat2 mat1 = new Mat2(new double[][] {{2f, 2f},{3f,3f}});
		Mat2 mat2 = Mat2.initIdentity(2);
		Mat2 mat3 = mat1.mult(mat2);
		System.out.println(mat3);
	}

}
