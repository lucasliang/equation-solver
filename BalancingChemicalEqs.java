import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.*;

public class BalancingChemicalEqs {
	public static void main (String[] args)
	{ 
		Scanner scan = new Scanner (System.in);
		System.out.println("Please enter your equation without coefficients in the form:");
		System.out.println("\"C6H12O6 + O2 -> CO2 + H2O\"");
		String eq = scan.nextLine();
		String[] sides = eq.split(" *-> *");
		String[] reactants = sides[0].split(" *[+=] *");
		String[] products = sides[1].split(" *[+=] *");
		Pattern elementPattern = Pattern.compile("[A-Z][a-z]*"); //An element must be a capital followed by lowercase letters
		ArrayList<String> elements = new ArrayList<String>();
		for(String reactant : reactants) //first, go through and identify which elements are in the equation
		{
			Matcher m = elementPattern.matcher(reactant);
			while(m.find())
				if(!elements.contains(m.group()))
					elements.add(m.group());
		}
		double[][] matrix = new double[elements.size()][reactants.length + products.length]; //setup a matrix to represent system of eqs.
		for(int i = 0; i < reactants.length; i++)
		{
			Matcher m = elementPattern.matcher(reactants[i]);
			while(m.find()) //add the element's coefficient to the matrix (eg. hydrogen in H20 -> 2)
			{
				String currElement = m.group();
				int coefficientIndex = reactants[i].indexOf(currElement) + currElement.length();
				int coefficientEnd = coefficientIndex;
				while(coefficientEnd < reactants[i].length() && Character.isDigit(reactants[i].charAt(coefficientEnd)))
					coefficientEnd++; //this loop determines the length of the coefficient (could be 2 or more digits)
				double coefficient;
				if(coefficientIndex == coefficientEnd) //no coefficient -> 1 (eg. oxygen in H2O)
					coefficient = 1.0;
				else
					coefficient = Double.parseDouble(reactants[i].substring(coefficientIndex, coefficientEnd));
				matrix[elements.indexOf(currElement)][i] += coefficient;
			}
		}
		for(int i = 0; i < products.length; i++) //same thing for products, but coefficients need to be parsed as negatives
		{
			Matcher m = elementPattern.matcher(products[i]);
			while(m.find())
			{
				String currElement = m.group();
				int coefficientIndex = products[i].indexOf(currElement) + currElement.length();
				int coefficientEnd = coefficientIndex;
				while(coefficientEnd < products[i].length() && Character.isDigit(products[i].charAt(coefficientEnd)))
					coefficientEnd++;
				double coefficient;
				if(coefficientIndex == coefficientEnd)
					coefficient = 1.0;
				else
					coefficient = Double.parseDouble(products[i].substring(coefficientIndex, coefficientEnd));
				matrix[elements.indexOf(currElement)][i+reactants.length] += coefficient;
			}
		}
		solveSystem(matrix); //solves system
		makeIntegers(matrix); //converts to integers
		DecimalFormat df = new DecimalFormat("#.#####");
		String result = "";
		for(int i = 0; i < reactants.length; i++)
		{
			result += df.format(matrix[i][matrix[0].length-1]) + "*"; //represent in terms of the free variable
			result += reactants[i] + " ";
			if(i != reactants.length-1)
				result += "+ ";
		}
		result += "-> ";
		for(int i = 0; i < products.length-1; i++)
		{
			result += df.format(matrix[i+reactants.length][matrix[0].length-1] * -1) + "*";
			result += products[i] + " + ";
		}
		result += df.format(matrix[0][0]) + "*" + products[products.length-1]; //free variable is represented in terms of everything else
		System.out.println("Solutions:");
		System.out.println(result);
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
	
	public static void makeIntegers(double[][] matrix) { //brute force try every integer until one works
		int scalar = 1;
		while(!isInteger(matrix,scalar) && scalar < 1000) //check if everything is an integer
			scalar++;
		for(int r = 0; r < matrix.length; r++)
		{
			for(int c = 0; c < matrix[0].length; c++)
				matrix[r][c] *= scalar;
		}
	}
	
	public static boolean isInteger(double[][] matrix, int scalar) {
		double precision = 0.0001; //prevents .999999 not being rounded to 1
		for(int r = 0; r < matrix.length; r++)
		{
			for(int c = 0; c < matrix[0].length; c++)
			{
				double a = matrix[r][c] * scalar % 1;
				if(!(Math.abs(a) < precision || Math.abs(a-1) < precision || Math.abs(a+1) < precision))
					return false;
			}
		}
		return true;
	}
}
