package utils.math.mat;

import java.util.Arrays;

import utils.math.geom.*;

/**
 * Matrix library
 * @author david
 *
 */
public class Mat2 {

	double[][] data;
	int rows, cols;
	
	/**
	 * Create a matrix using the given 2D array
	 * @param data [rows][cols]
	 */
	public Mat2(double[][] data) {
		this.data = data;
		rows = data.length; cols = (rows==0)? 0 : data[0].length;
	}
	
	/**
	 * Initialize an empty matrix with the given dimensions
	 * @param rows
	 * @param cols
	 */
	public Mat2(int rows, int cols) {
		data = new double[rows][cols];
		for(int i=0; i<rows; i++) {
			for(int j=0; j<cols; j++) {
				data[i][j] = 0;
			}
		}
		this.rows = rows; this.cols = cols;
	}
	
	/**
	 * Initialize an empty square matrix of the given size
	 * @param size
	 */
	public Mat2(int size) {
		this(size, size);
	}
	
	/**
	 * Initialize an identity matrix of the given size
	 * @param size
	 * @return
	 */
	public static Mat2 initIdentity(int size) {
		double[][] data = new double[size][size];
		for(int i=0; i<size; i++) {
			for(int j=0; j<size; j++) {
				data[i][j] = 0;
				if(i==j) data[i][j] = 1;
			}
		}
		return new Mat2(data);
	}
	
	public static Mat2 toColMatrix(Vector3d p) {
		return new Mat2(new double[][] {{p.x},{p.y},{p.z}});
	}
	public static Mat2 toColMatrix(Vector3i p) {
		return new Mat2(new double[][] {{p.x},{p.y},{p.z}});
	}
	public static Mat2 toColMatrix(Vector3f p) {
		return new Mat2(new double[][] {{p.x},{p.y},{p.z}});
	}
	public static Mat2 toColMatrix(Vector2d p) {
		return new Mat2(new double[][] {{p.x},{p.y}});
	}
	public static Mat2 toColMatrix(Vector2i p) {
		return new Mat2(new double[][] {{p.x},{p.y}});
	}
	public static Mat2 toColMatrix(Vector2f p) {
		return new Mat2(new double[][] {{p.x},{p.y}});
	}
	
	/**
	 * 
	 * @return the 2D array of the matrix data
	 */
	public double[][] getData() {
		return data;
	}
	
	/**
	 * Get a specific element of the matrix
	 * @param i row
	 * @param j col
	 * @return the [i,j]th element of the matrix
	 */
	public double get(int i, int j) {
		return data[i][j];
	}
	/**
	 * Set an element to a value
	 * @param i row
	 * @param j col
	 * @param value
	 * @return the value
	 */
	public double set(int i, int j, double value) {
		data[i][j] = value;
		return value;
	}
	
	public int getRows() {return rows;}
	public int getCols() {return cols;}
	
	/**
	 * Perform scalar multiplication on the matrix
	 * @param s scalar value
	 * @return scaled matrix
	 */
	public Mat2 mult(double s) {
		double[][] newData = new double[rows][cols];
		for(int i=0; i<rows; i++) {
			for(int j=0; j<cols; j++) {
				newData[i][j] = data[i][j] * s;
			}
		}
		return new Mat2(newData);
	}
	
	/**
	 * Perform the product A * B where A = this matrix and B = m
	 * @param m
	 * @return the result of the multiplication
	 */
	public Mat2 mult(Mat2 m) {
		double[][] newData = new double[rows][m.cols];
		//if(rows != m.cols) throw new IllegalArgumentException("cannot multiply a " + rows + " by " + cols + 
			//	" matrix and a " + m.rows + " by " + m.cols + " matrix");
		for(int i=0; i<rows; i++) {
			for(int j=0; j<m.cols; j++) {
				double total = 0.0;
				for(int c=0; c<cols; c++) {
					total += data[i][c] * m.data[c][j];
				}
				newData[i][j] = total;
			}
		}
		return new Mat2(newData);
	}
	/**
	 * Perform the product A * B where A = this matrix and B = m
	 * @param m
	 * @param dest the matrix in which to place the result
	 */
	public void mult(Mat2 m, Mat2 dest) {
		for(int i=0; i<rows; i++) {
			for(int j=0; j<m.cols; j++) {
				double total = 0.0f;
				for(int c=0; c<cols; c++) {
					total += data[i][c] * m.data[c][j];
				}
				dest.set(i, j, total);
			}
		}
	}
	
	/**
	 * Performs multiplication the given vector by this matrix
	 * @param vector
	 */
	public double[] mult(double[] vector) {
		if(this.cols != vector.length) return null;
		double[] result = new double[this.rows];
		for(int i=0; i<this.rows; i++) {
			for(int j=0; j<this.cols; j++) {
				result[i] += data[i][j] * vector[j];
			}
		}
		return result;
	}
	
	/**
	 * Add the two matrices
	 * @param m
	 * @return
	 */
	public Mat2 add(Mat2 m) {
		if(this.getRows()!=m.getRows() || this.getCols()!=m.getCols()) return null;
		Mat2 m2 = new Mat2(this.getRows(), this.getCols());
		for(int i=0; i<this.getRows(); i++) {
			for(int j=0; j<this.getCols(); j++) {
				m2.set(i, j, this.get(i, j)+m.get(i, j));
			}
		}
		return m2;
	}
	
	/**
	 * 
	 * @return the transverse of the matrix (each element [i,j] is switched with [j,i])
	 */
	public Mat2 transverse() {
		double[][] data_new = new double[getCols()][getRows()];
		for(int i=0; i<rows; i++) {
			for(int j=0; j<cols; j++) {
				data_new[j][i] = data[i][j];
			}
		}
		return new Mat2(data_new);
	}
	
	@Override
	public String toString() {
		String s = "{\n";
		for(int i=0; i<rows; i++) {
			s+= "{";
			for(int j=0; j<cols; j++) {
				s+= data[i][j] + ",";
			}
			s+= "},\n";
		}
		s+= "}";
		return s;
	}

	public static void main(String args[]) {
		Mat2 mat1 = new Mat2(new double[][] {{1,0,-2},{0,3,-1}});
		Mat2 mat2 = new Mat2(new double[][] {{0,3},{-2,-1},{0,4}});
		Mat2 mat3 = mat1.mult(mat2);
		System.out.println(mat3);
	}
}
