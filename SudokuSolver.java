import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;

/*
Solves a Sudoku puzzle in a text file specified via the command line.
*/
public class SudokuSolver {
	
	// Reads in the Sudoku from the given file and outputs the solution to another file
	public static void main(String[] args) throws FileNotFoundException, IOException {
		if (args.length == 0) {
			System.out.println("Usage: java SudokuSolver <puzzle_file_path>");
			return;
		}
		String filePath = args[0];
		
		// Create and print the input puzzle
		Sudoku puzzle = new Sudoku(readSudoku(filePath));
		System.out.println("Input:\n" + puzzle);
		
		if (puzzle.solve()) {
			// A solution was found - print and write it to a file
			System.out.println("Solution:\n" + puzzle);
			String outputFilePath = filePath.substring(0, filePath.lastIndexOf(".")) + ".sln.txt";
			outputSudoku(outputFilePath, puzzle);
			System.out.println("Wrote to file: " + outputFilePath);
		} else {
			System.out.println("No solution");
		}
	}
	
	// Reads the given file and returns a string of digits representing a Sudoku puzzle
	// (blanks are represented by 0s)
	private static String readSudoku(String filePath) throws FileNotFoundException, IOException {
		String sudokuString = "";
		FileReader reader = new FileReader(filePath);
		int in;
		while ((in = reader.read()) != -1) {
			char c = (char)in;
			if (c == 'X' || c == '.') {
				// These characters represent blank grid cells
				sudokuString += "0";
			} else {
				// If the character is a digit, add to the string; otherwise, ignore it
				int num = c - '0';
				if (num >= 0 && num <= 9) {
					sudokuString += c;
				}
			}
		}
		return sudokuString;
	}
	
	// Writes the given Sudoku to the given file (creates the file if necessary)
	private static void outputSudoku(String filePath, Sudoku puzzle) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
		writer.write(puzzle.toString());
		writer.close();
	}
	
}