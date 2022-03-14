import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.IllegalArgumentException;

/*
Performs the Sudoku tests specified in the file "tests.txt". Each line represents one test.
*/
public class SudokuSolverTest {
	
	// Reads each line of the tests file
	public static void main(String[] args) throws FileNotFoundException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader("tests.txt"));
		String line;
		int lineNum = 1;
		int passNum = 0;
		while ((line = reader.readLine()) != null) {
			// Split the line into the test input and solution
			int split = line.indexOf(";");
			String input = line.substring(0, split);
			String solution = line.substring(split + 1);
			
			// If the test passes, add to the count
			passNum += test(input, solution, lineNum) ? 1 : 0;
			lineNum++;
		}
		System.out.println("Total: " + passNum + " / " + (lineNum - 1));
	}
	
	// Performs a single test and prints the result
	private static boolean test(String input, String solution, int lineNum) {
		boolean pass;
		String solvedString = null;
		if (solution.equals("INVALID_INPUT")) {
			// If the input is invalid, an exception should be thrown
			try {
				Sudoku puzzle = new Sudoku(input);
				pass = false;
			} catch (IllegalArgumentException ex) {
				pass = true;
			}
		} else {
			Sudoku puzzle = new Sudoku(input);
			if (solution.equals("NO_SOLUTION")) {
				pass = !puzzle.solve();
			} else if (solution.equals("")) {
				// Case for testing that any valid solution is found
				pass = puzzle.solve();
			} else {
				// Case for testing that a specific solution is found
				if (puzzle.solve()) {
					solvedString = puzzle.sudokuString(false);
					pass = solvedString.equals(solution);
				} else {
					pass = false;
				}
			}
		}
		
		System.out.println("Test " + lineNum + ": " + (pass ? "Pass" : "Fail"));
		// If the test failed, print more information
		if (!pass && !solution.equals("")) {
			System.out.println("  Expected: " + solution);
			if (solvedString != null) {
				System.out.println("  Actual: " + solvedString);
			}
		}
		return pass;
	}
	
}