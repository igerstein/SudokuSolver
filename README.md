# SudokuSolver
This is a simple Java program for solving standard Sudoku puzzles. It is run on the command line as follows:  
`java SudokuSolver <puzzle_file_path>`

For the format of the input file, see [puzzles/puzzle1.txt](https://github.com/igerstein/SudokuSolver/blob/main/puzzles/puzzle1.txt). The characters `X`, `.`, and `0` are all treated as blanks in the Sudoku grid, while the digits 1-9 are treated as fixed givens. All other characters, including newlines, are ignored when parsing the input.

If a solution is found, it is output to a new file named `<input_name>.sln.txt`.
## Algorithms
Three algorithms are used in conjunction to solve the puzzles. These are explained in [this writeup](https://pi.math.cornell.edu/~mec/Summer2009/meerkamp/Site/Solving_any_Sudoku_I.html) and defined as the *simple solving algorithm*, *candidate-checking method*, and *place-finding method*.
## SudokuSolverTest
This class is for running tests on the Sudoku solver by comparing its output with the expected values. It parses the file [tests.txt](https://github.com/igerstein/SudokuSolver/blob/main/tests.txt), which on each line has a test Sudoku input and the expected solution (separated by `;`). The Sudoku input uses `0` to represent blanks. If the solution is left empty, the test will pass if any solution is found rather than checking for one solution in particular. The solution `INVALID_INPUT` means that the given Sudoku is invalid, either due to syntax or due to the rules of Sudoku being violated. `NO_SOLUTION` means that the given Sudoku satisfies the rules but has no solution.

Several of the test cases defined in tests.txt are taken from http://sudopedia.enjoysudoku.com/Test_Cases.html.
