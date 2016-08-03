import java.util.Scanner;

public class determinant {
	
	public static void main(String[] args) {
		System.out.println("Enter your matrix:");
		Scanner scan = new Scanner (System.in);
		String[] row1 = scan.nextLine().split(" "); //get the length of the first row
		double[][] matrix = new double[row1.length][row1.length];
		for(int i = 0; i < matrix.length; i++)
			matrix[0][i] = Double.parseDouble(row1[i]);
		for(int i = 1; i < matrix.length; i++) { //repeat for the remaining rows
			String[] row = scan.nextLine().split(" ");
			for(int j = 0; j < matrix.length; j++)
				matrix[i][j] = Double.parseDouble(row[j]);
		}
		System.out.println("Determinant: " + Double.toString(findDeterminant(matrix)));
	}
	
	public static double findDeterminant (double[][] matrix) {
		double det = 1.0; //keeps track of the changes made during row reduction
		for(int i = 0; i < matrix.length; i++) {
			if(matrix[i][i] == 0) { //if the pivotal value is 0, we need to switch it
				int j = i;
				while(j < matrix.length) { //keep looking down
					if(matrix[j][i] != 0) { //switch the rows
						double[] temp = matrix[i];
						matrix[i] = matrix[j];
						matrix[j] = temp;
						break;
					}
					j++;
					if(j == matrix.length) //if entire rest of column is 0, determinant is 0
						return 0;
				}
				det *= -1; //when we switch rows we negate the determinant
			}
			det *= rowReduce(matrix,i);
		}
		for(int i = 0; i < matrix.length; i++) //end up with an upper triangle matrix
			det *= matrix[i][i];
		return det;
	}
	
	public static double rowReduce(double[][] matrix, int row) { //performs one row reduction
		double scaleFactor = matrix[row][row]; //keep track of the initial 'pivotal value'
		for(int i = 0; i < matrix.length; i++)
			matrix[row][i] /= scaleFactor;
		for(int j = row+1; j < matrix.length; j++) {
			double mult = matrix[j][row];
			for(int k = 0; k < matrix.length; k++)
				matrix[j][k] -= matrix[row][k] * mult;
		}
		return scaleFactor;
	}
	
}