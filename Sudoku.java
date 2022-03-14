import java.util.HashSet;
import java.util.ArrayList;
import java.awt.Point;
import java.lang.IllegalArgumentException;
import java.lang.NumberFormatException;

/*
Represents a Sudoku puzzle. Numbers in the grid should satisfy the rules of Sudoku:
  - Each row contains the digits 1-9 only once
  - Each column contains the digits 1-9 only once
  - Each 3x3 box contains the digits 1-9 only once
*/
public class Sudoku {
	
	private static final int SIZE = 9;
	private static final int BOX_SIZE = 3;
	
	// Internal representation of the 9x9 Sudoku grid
	private int[][] grid;
	
	// Create the Sudoku from a string of digits
	public Sudoku(String sudokuString) {
		if (sudokuString.length() != SIZE * SIZE) {
			throw new IllegalArgumentException("Invalid input length");
		}
		// Initialize the grid with 0s (blanks)
		grid = new int[SIZE][SIZE];
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				int pos = i * SIZE + j;
				int num = sudokuString.charAt(pos) - '0';
				if (num >= 0 && num <= 9) {
					// Ensure that the digit does not violate the rules of Sudoku
					if (num == 0 || isValid(num, i, j)) {
						grid[i][j] = num;
					} else {
						String m = "Digit " + num + " at input position " + pos;
						m += " violates the Sudoku condition";
						throw new IllegalArgumentException(m);
					}
				} else {
					// The input string contains a non-digit character
					String m = "Invalid input character at position " + pos;
					throw new NumberFormatException(m);
				}
			}
		}
	}
	
	public String toString() {
		return sudokuString(true);
	}
	
	// Builds the string representation of the Sudoku grid
	// If formatted, includes line breaks and uses "X" for blanks
	public String sudokuString(boolean formatted) {
		String result = "";
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				if (formatted && grid[i][j] == 0) {
					result += "X";
				} else {
					result += grid[i][j];
				}
			}
			if (formatted) {
				result += "\n";
			}
		}
		return result;
	}
	
	// Solves the Sudoku puzzle
	// Returns true if successful, false if no solution is found
	public boolean solve() {
		while (true) {
			// Attempt to solve the puzzle by alternating the candidate-checking and
			// place-finding methods
			int candidateCheckingEntered = solveCandidateChecking();
			if (candidateCheckingEntered == -1) {
				return false;
			}
			int placeFindingEntered = solvePlaceFinding();
			if (placeFindingEntered == -1) {
				return false;
			} else if (candidateCheckingEntered + placeFindingEntered == 0) {
				// Proceed once no digits were entered on the last iteration
				break;
			}
		}
		// Fill in any remaining blanks using the brute force method
		return solveBruteForce();
	}
	
	// Solves the Sudoku puzzle using recursive backtracking to check all possible solutions
	// Returns true if successful, false if no solution is found
	private boolean solveBruteForce() {
		return solveBruteForceHelper(1, 0, 0);
	}
	
	// Helper method for the recursive approach
	// Attempts to enter the given digit into the given grid cell
	private boolean solveBruteForceHelper(int entry, int row, int col) {
		if (row == SIZE) {
			// Every grid cell has been successfully traversed, so the puzzle is solved
			return true;
		} else if (col == SIZE) {
			// Proceed to the following row
			return solveBruteForceHelper(1, row + 1, 0);
		} else if (entry == 1 && grid[row][col] != 0) {
			// The grid cell contains a fixed digit, so proceed to the following column
			return solveBruteForceHelper(1, row, col + 1);
		}
		// Find the next digit that is allowed to be entered in the cell
		while (entry <= 9 && !isValid(entry, row, col)) {
			entry++;
		}
		if (entry > 9) {
			// No solution (in this recursive branch), so revert the cell to blank
			grid[row][col] = 0;
			return false;
		}
		// Attempt to finish the puzzle with the entered digit
		grid[row][col] = entry;
		if (solveBruteForceHelper(1, row, col + 1)) {
			return true;
		}
		// No solution was found with the entered digit, so try the next digit
		return solveBruteForceHelper(entry + 1, row, col);
	}
	
	// Attempts to solve the Sudoku puzzle using the "candidate-checking" method
	// Uses the fact that certain cells can only contain a certain number
	// Returns the total number of entries made, or -1 if no solution is possible
	private int solveCandidateChecking() {
		int lastEntered = 0;
		int numEntered = 0;
		while (true) {
			// Continue looping through all cells until no more entries can be made
			for (int i = 0; i < SIZE; i++) {
				for (int j = 0; j < SIZE; j++) {
					if (lastEntered == SIZE * SIZE) {
						// No entries have been made for one complete cycle
						return numEntered;
					} else if (grid[i][j] == 0) {
						HashSet<Integer> validSet = getValidSet(i, j);
						if (validSet.size() == 0) {
							// The cell has no valid possibilities
							return -1;
						} else if (validSet.size() == 1) {
							// The cell has only one possibility, so enter it
							grid[i][j] = (Integer)validSet.toArray()[0];
							lastEntered = 0;
							numEntered++;
						}
					}
					lastEntered++;
				}
			}
		}
	}
	
	// Attempts to solve the Sudoku puzzle using the "place-finding" method
	// Uses the fact that every row/column/box must contain each digit
	// Returns the total number of entries made, or -1 if no solution is possible
	private int solvePlaceFinding() {
		// Get lists of cell coordinates for every row, column, and box
		ArrayList<ArrayList<Point>> cellsLists = new ArrayList<>();
		cellsLists.addAll(getCellsList(true));
		cellsLists.addAll(getCellsList(false));
		cellsLists.addAll(getCellsListBoxes());
		
		int lastEntered = 0;
		int numEnteredTotal = 0;
		while (true) {
			// Continue looping through all rows/columns/boxes until no more entries can be made
			for (ArrayList<Point> cells : cellsLists) {
				if (lastEntered == cellsLists.size()) {
					// No entries have been made for one complete cycle
					return numEnteredTotal;
				}
				// Determine entries for this row/column/box
				int numEntered = solvePlaceFindingHelper(cells);
				if (numEntered == -1) {
					return -1;
				} else if (numEntered > 0) {
					lastEntered = 0;
				}
				lastEntered++;
				numEnteredTotal += numEntered;
			}
		}
	}
	
	// Helper method for the place-finding approach
	// Attempts to make entries into the given row, column, or box
	// Returns the total number of entries made, or -1 if no solution is possible
	private int solvePlaceFindingHelper(ArrayList<Point> cells) {
		int numEntered = 0;
		// For each digit, determine if there is only one possible spot
		for (int entry = 1; entry <= 9; entry++) {
			if (cellsContain(cells, entry)) {
				// This row/column/box already contains the digit
				continue;
			}
			int numValid = 0;
			Point valid = null;
			for (Point cell : cells) {
				if (numValid == 2) {
					// There is more than one valid spot for this digit
					break;
				}
				if (grid[cell.x][cell.y] == 0 && isValid(entry, cell.x, cell.y)) {
					numValid++;
					valid = cell;
				}
			}
			if (numValid == 0) {
				// There are no valid spots for this digit, so the puzzle has no solution
				return -1;
			} else if (numValid == 1) {
				// There is only one valid spot for this digit, so enter it
				grid[valid.x][valid.y] = entry;
				numEntered++;
			}
		}
		return numEntered;
	}
	
	// Determines whether the given digit is valid in the given grid cell
	// (does not violate the rules of Sudoku)
	private boolean isValid(int entry, int row, int col) {
		// Check whether the digit already exists in the cell's row or column
		for (int i = 0; i < SIZE; i++) {
			if (i != col && grid[row][i] == entry) {
				return false;
			}
			if (i != row && grid[i][col] == entry) {
				return false;
			}
		}
		// Check whether the digit already exists in the cell's box
		int rowOffset = row - row % BOX_SIZE;
		int colOffset = col - col % BOX_SIZE;
		for (int i = rowOffset; i < rowOffset + BOX_SIZE; i++) {
			for (int j = colOffset; j < colOffset + BOX_SIZE; j++) {
				if (i != row && j != col && grid[i][j] == entry) {
					return false;
				}
			}
		}
		return true;
	}
	
	// Gets the set of valid digits for the given grid cell
	private HashSet<Integer> getValidSet(int row, int col) {
		// Initialize the set with all digits
		HashSet<Integer> validSet = new HashSet<>();
		for (int i = 1; i <= 9; i++) {
			validSet.add(i);
		}
		// Remove digits that already exist in the cell's row or column
		for (int i = 0; i < SIZE; i++) {
			if (i != col && grid[row][i] != 0) {
				validSet.remove(grid[row][i]);
			}
			if (i != row && grid[i][col] != 0) {
				validSet.remove(grid[i][col]);
			}
		}
		// Remove digits that already exist in the cell's box
		int rowOffset = row - row % BOX_SIZE;
		int colOffset = col - col % BOX_SIZE;
		for (int i = rowOffset; i < rowOffset + BOX_SIZE; i++) {
			for (int j = colOffset; j < colOffset + BOX_SIZE; j++) {
				if (i != row && j != col && grid[i][j] != 0) {
					validSet.remove(grid[i][j]);
				}
			}
		}
		return validSet;
	}
	
	// Gets a list of lists of cells (as Points) in every row or column
	private ArrayList<ArrayList<Point>> getCellsList(boolean rows) {
		ArrayList<ArrayList<Point>> cellsList = new ArrayList<>();
		for (int i = 0; i < SIZE; i++) {
			ArrayList<Point> cells = new ArrayList<>();
			for (int j = 0; j < SIZE; j++) {
				// For columns, simply flip the coordinates
				cells.add(rows ? new Point(i, j) : new Point(j, i));
			}
			cellsList.add(cells);
		}
		return cellsList;
	}
	
	// Gets a list of lists of cells (as Points) in every box
	private ArrayList<ArrayList<Point>> getCellsListBoxes() {
		// Outer two loops are for iterating through each box
		ArrayList<ArrayList<Point>> boxes = new ArrayList<>();
		for (int i = 0; i < SIZE; i += BOX_SIZE) {
			for (int j = 0; j < SIZE; j += BOX_SIZE) {
				// Inner two loops are for iterating through each cell in the current box
				ArrayList<Point> box = new ArrayList<>();
				for (int k = i; k < i + BOX_SIZE; k++) {
					for (int l = j; l < j + BOX_SIZE; l++) {
						box.add(new Point(k, l));
					}
				}
				boxes.add(box);
			}
		}
		return boxes;
	}
	
	// Determines whether the given digit is contained in any of the specified grid cells
	private boolean cellsContain(ArrayList<Point> cells, int entry) {
		for (Point cell : cells) {
			if (grid[cell.x][cell.y] == entry) {
				return true;
			}
		}
		return false;
	}
	
}