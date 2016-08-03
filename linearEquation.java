import java.util.Scanner;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class linearEquation {
	public static void main (String[] args)
	{ 
		Scanner scan = new Scanner (System.in);
		System.out.println("Please enter your equations in the form:");
		System.out.println("3x1 + -2x2 + -x5 = 3");
		System.out.println("Current maximum number of variables: 20");
		System.out.println("Enter 'q' when finished");
		String next = scan.nextLine();
		ArrayList<String[]> equations = new ArrayList<String[]>(); //use an arrayList because we don't know how many eqs there are initially
		while(!next.equals("q"))
		{
			String[] tokens = next.split(" *[+=] *");
			equations.add(tokens);
			next = scan.nextLine();
		}
		double[][] matrix = new double[equations.size()][21];
		for(int k = 0; k < matrix.length; k++)
		{
			String[] eq = equations.get(k);
			for(int i = 0; i < eq.length-1; i++)
			{
				String[] tokens = eq[i].split(" *x *");
				if(tokens[0].equals(""))
					matrix[k][Integer.parseInt(tokens[1])-1] = 1.0; //no leading coefficient, means it is a 1
				else if (tokens[0].equals("-"))
					matrix[k][Integer.parseInt(tokens[1])-1] = -1.0; //just a - sign, means a -1
				else
					matrix[k][Integer.parseInt(tokens[1])-1] = Double.parseDouble(tokens[0]);
			}
			matrix[k][20] = Double.parseDouble(eq[eq.length-1]); //we will use column 20 as the augmented portion of the matrix
		}
		solveSystem(matrix);
	}
	
	public static void solveSystem (double[][] matrix) {
		for(int i = 0; i < matrix.length; i++) {
			if(matrix[i][i] == 0) { //if the pivotal value is 0, we will look to switch it
				int j = i;
				while(j < matrix.length) { //keep looking down
					if(matrix[j][i] != 0) { //switch the rows
						double[] temp = matrix[i];
						matrix[i] = matrix[j];
						matrix[j] = temp;
						break;
					}
					j++;
				}
			}
			rowReduce(matrix,i);
		}
		printSolutions(matrix);
	}
	
	public static void rowReduce(double[][] matrix, int row) { //performs one row reduction
		double scaleFactor = matrix[row][row]; //keep track of the initial 'pivotal value'
		if(scaleFactor == 0)
			return;
		for(int i = 0; i < matrix[0].length; i++)
			matrix[row][i] /= scaleFactor;
		for(int j = 0; j < matrix.length; j++) {
			if(j==row)
				continue;
			double mult = matrix[j][row];
			for(int k = 0; k < matrix[0].length; k++)
				matrix[j][k] -= matrix[row][k] * mult;
		}
	}
	
	public static void printSolutions(double[][] matrix) { //prints out solutions (if any)
		String result = "Solutions:\n";
		DecimalFormat df = new DecimalFormat("#.#####");
		for(int row = 0; row < matrix.length; row++)
		{
			boolean hasNonZeroEntry = false;
			for(int col = 0; col < matrix[0].length-1; col++)
			{
				if(matrix[row][col] != 0.0)
				{
					if(!hasNonZeroEntry) //leading one
						result += "x" + (col+1) + " = ";
					else //means there are free variables
						result += df.format(-1*matrix[row][col]) + "x" + (col+1) + " + ";
					hasNonZeroEntry = true;
				}
			}
			if(!hasNonZeroEntry && matrix[row][matrix[0].length-1] != 0.0) //if everything is 0 but augmented is non-zero, means there are no solutions
			{
				System.out.println("No solutions");
				return;
			}
			else if(hasNonZeroEntry)
			{
				result += df.format(matrix[row][matrix[0].length-1]);
				result += "\n";
			}
		}
		System.out.print(result);
	}
}
