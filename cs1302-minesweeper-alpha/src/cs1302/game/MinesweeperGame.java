package cs1302.game;

import java.util.Scanner;
import java.util.NoSuchElementException;
import java.io.FileNotFoundException;
import java.io.File;

/**
 * Class that handles the Minesweeper game.
 */
public class MinesweeperGame {
    int rows = 0; //Number of rows
    int cols = 0; //Number of columns
    int numMines = 0; //Number of mines
    int rounds = 0; //Number of rounds played
    int[] mineSpots = {0}; //The locations of the mines
    boolean[][] bBoard; //Array that holds whether each square has been revealed or guessed/marked
    String[][] iBoard; //Array which is displayed to the user in standard output
    int[][] backBoard; //Array that holds mines and adjacent number of mines
    String seedPath; //Holds the seedPath (from constructor)
    Scanner stdIn; //Holds standard input (from constructor)
    String command; //Holds the user command from standard input
    double score;
    int spotLoc;

    /**
     * Constructs a {@code MinesweeperGame} object that
     * is the current game being played.
     *
     * @param stdIn Standard input from user
     * @param seedPath location of seed file
     */
    public MinesweeperGame(Scanner stdIn, String seedPath) {
        this.stdIn = stdIn;
        this.seedPath = seedPath;
    } //Constructor

    /**
     * Takes the seed file and assigns {@code rows},
     * {@code cols}, {@code numMines} and {@code mineSpots[]} and creates
     * {@code bBoard[][]}, {@code iBoard[][]} and {@code backBoard[][]}
     * for the game, given the seed file is not malformed.
     */
    public void readSeed() {
        try {
            File configFile = new File(seedPath);
            Scanner configScanner = new Scanner(configFile);
            spotLoc = 1;
            while (configScanner.hasNextLine()) { //Loop that reads seed file by line
                while (configScanner.hasNext()) {
                    try {
                        if (spotLoc == 1) { //First number is for the rows
                            rows = Integer.parseInt(configScanner.next());
                            spotLoc++;
                        } else if (spotLoc == 2) { //Second number is for the cols
                            cols = Integer.parseInt(configScanner.next());
                            spotLoc++;
                        } else if (spotLoc == 3) { //Third number is for the mines
                            numMines = Integer.parseInt(configScanner.next());
                            spotLoc++;
                            mineSpots = new int[numMines * 2]; //Holds the locations of the mines
                        } else { //Following numbers are mine locations
                            mineSpots[spotLoc - 4] = Integer.parseInt(configScanner.next());
                            spotLoc++;
                        }
                    }  catch (NoSuchElementException nse) { //If seed is missing elements
                        System.err.println("Seed File Malformed Error: " + nse.getMessage());
                        System.exit(3);
                    } catch (NumberFormatException nfe) { //If seed has non-integers
                        System.err.println("Seed File Malformed Error: " + nfe.getMessage());
                        System.exit(3);
                    } catch (ArrayIndexOutOfBoundsException oob) { //If seed has incorrect elements
                        System.err.println("Seed File Malformed Error: " + oob.getMessage());
                        System.exit(3);
                    }

                }
                configScanner.nextLine();
            } //while
            checkSeed();
        } catch (FileNotFoundException e) { //If seed file could not be found
            System.err.println("Seed File Not Found Error: " + e.getMessage());
            System.exit(2);
        } // try
    } //readSeed

    /**
     * Checks certain elements of the seed file to determine if
     * it is valid or malformed.
     */
    public void checkSeed() {
        if (spotLoc % 2 != 0) { //Mine locations should be in pairs
            System.err.println("Seed File Malformed Error: mine locations have missing values");
            System.exit(3);
        }
        if (rows < 5 || rows > 10 || cols < 5 || cols > 10 || numMines < 1) {
            System.err.println("Seed File Malformed Error: Rows, cols or number of mines");
            System.exit(3);
        }
        if ((numMines > (rows * cols) - 1) || numMines > (spotLoc - 4) / 2) {
            System.err.println("Seed File Malformed Error: Rows, cols or number of mines");
            System.exit(3);
        }

        bBoard = new boolean[rows][cols];
        iBoard = new String[rows][cols];
        backBoard = new int[rows][cols];
    }

    /**
     * Creates the minesweeper 2D arrays, as stated in {@code readSeed()}.
     */
    public void createBoard() {
        for (int i = 0; i < bBoard.length; i++) {
            for (int j = 0; j < bBoard[i].length; j++) {
                bBoard[i][j] = false; //False meaning the mine shows up as " "
                iBoard[i][j] = " "; //What the mine shows as
                backBoard[i][j] = 0; //Temporarily sets spots to zero
            } //for
        } //for
        for (int i = 0; i < mineSpots.length; i += 2) {
            try {
                iBoard[mineSpots[i]][mineSpots[i + 1]] = "b"; //b represents a bomb
                backBoard[mineSpots[i]][mineSpots[i + 1]] = 9; //9 represents a bomb
            } catch (ArrayIndexOutOfBoundsException oob) { //Issue with seed file with throw this
                System.err.println("Seed File Malformed Error: " + oob.getMessage());
                System.exit(3);
            }
        }
        int closeMines = 0; //Holds # of adjacent mines to the square
        for (int i = 0; i < iBoard.length; i++) { //Adds nums to non-bomb squares
            for (int j = 0; j < iBoard[i].length; j++) {
                closeMines = 0;
                if (!iBoard[i][j].equals("b")) { //If it is NOT a mine
                    if (isInBounds(i - 1, j - 1)) { //Verifies it is in bounds and is a mine
                        closeMines++;
                    }
                    if (isInBounds(i, j - 1)) { //Checks for each surrounding square
                        closeMines++;
                    }
                    if (isInBounds(i - 1, j)) {
                        closeMines++;
                    }
                    if (isInBounds(i - 1, j + 1)) {
                        closeMines++;
                    }
                    if (isInBounds(i + 1, j - 1)) {
                        closeMines++;
                    }
                    if (isInBounds(i + 1, j + 1)) {
                        closeMines++;
                    }
                    if (isInBounds(i, j + 1)) {
                        closeMines++;
                    }
                    if (isInBounds(i + 1, j)) {
                        closeMines++;
                    }
                    if (closeMines > 0) { //If it is greater than zero it will change the square
                        iBoard[i][j] = "" + closeMines;
                        backBoard[i][j] = closeMines;
                    }
                }
            }
        }
    } //createBoard

    /**
     * Prints the {@code iBoard[][]} array to standard output, along with
     * the row and column numbers and | markers.
     */
    public void printMineField() {
        System.out.println("\n Rounds Completed: " + rounds + "\n");
        for (int i = 0; i < iBoard.length; i++) {
            System.out.print(" " + i);
            for (int j = 0; j < iBoard[i].length; j++) {
                if (bBoard[i][j] == true) {
                    System.out.print("| " + iBoard[i][j] + " ");
                } else {
                    System.out.print("|   ");
                }
            }
            System.out.print("|");
            System.out.println();
        }
        System.out.print(" ");
        for (int i = 0; i < cols; i++) {
            System.out.print("   " + i);
        }
        System.out.println("\n");

    } //printMineField

    /**
    * Indicates whether or not the square is in the game grid
    * and whether it is a mine.
    *
    * @param row the row index of the square
    * @param col the column index of the square
    * @return true if the square is in the game grid and is a mine; false otherwise
    */
    private boolean isInBounds(int row, int col) {
        String test;
        try {
            test = iBoard[row][col];
            if (!test.equals("b")) { //If it is a bomb, return false
                return false;
            }
        } catch (ArrayIndexOutOfBoundsException oob) { //If it is oob, returns false
            return false;
        }
        return true; //If it passes the try, catch, it's in bounds
    }

    /**
     * Prompts the user for command input from {@code stdIn}, then handles
     * the input based on their command.
     * Will call {@code reveal()} or {@code noFog()} if those commands
     * are input.
     */
    public void promptUser() {
        System.out.print("minesweeper-alpha: ");
        String fullCommand = stdIn.nextLine(); //Holds the user input
        Scanner commandScan = new Scanner(fullCommand); //Scanner for the command
        try { //e is only thrown if the user uses a command with too many variables after it
            NoSuchElementException e = new NoSuchElementException("Unknown command");
            command = commandScan.next(); //Holds the main command
            if (command.equals("r") || command.equals("reveal")) { //If it is reveal
                int rowsIn = Integer.parseInt(commandScan.next());
                int colsIn = Integer.parseInt(commandScan.next());
                if (commandScan.hasNext()) { //Throw if more than two things are input
                    throw e;
                }
                reveal(rowsIn, colsIn); //Calls reveal
            } else if (command.equals("m") || command.equals("mark")) { //If it is mark
                int rowsIn = Integer.parseInt(commandScan.next());
                int colsIn = Integer.parseInt(commandScan.next());
                if (commandScan.hasNext()) { //Throw if more than two things are input
                    throw e;
                }
                bBoard[rowsIn][colsIn] = true;
                iBoard[rowsIn][colsIn] = "F"; //Marks it
                rounds++;
            } else if (command.equals("g") || command.equals("guess")) { //If it is guess
                int rowsIn = Integer.parseInt(commandScan.next());
                int colsIn = Integer.parseInt(commandScan.next());
                if (commandScan.hasNext()) { //Throw if more than two things are input
                    throw e;
                }
                bBoard[rowsIn][colsIn] = true;
                iBoard[rowsIn][colsIn] = "?"; //Guesses it
                rounds++;
            } else if (command.equals("nofog")) { //If it is nofog
                noFog(); //Calls noFog
                if (commandScan.hasNext()) { //Throws if anything is input after nofog
                    throw e;
                }
            } else if (command.equals("h") || command.equals("help")) { //If it is help
                if (commandScan.hasNext()) { //Throw if anytrhing is input after help
                    throw e;
                }
                printHelp(); //Calls printHelp
            } else if (command.equals("q") || command.equals("quit")) { //If it is quit
                if (commandScan.hasNext()) { //Throws if anything is input after quit
                    throw e;
                }
                System.out.println("\nQuitting the game...\n" + "Bye!");
                System.exit(0); //Gracefully exits
            } else { //Other commands not known
                System.err.println("\nInvalid Command: Unknown command '" + command + "'");
            }
        } catch (NoSuchElementException nse) { //If it expected certain output and got none
            System.err.println("\nInvalid Command: " + nse.getMessage());
        } catch (NumberFormatException nfe) { //If it expected ints and got non-ints
            System.err.println("\nInvalid Command: " + nfe.getMessage());
        } catch (ArrayIndexOutOfBoundsException oob) { //If any input was oob
            System.err.println("\nInvalid Command: " + oob.getMessage());
        }
    } //promptUser

    /**
     * Prints the available commands and adds one to the round total.
     */
    public void printHelp() {
        System.out.println("Commands Available...\n" + "- Reveal: r/reveal row col");
        System.out.print("-   Mark: m/mark   row col\n-  Guess: g/guess");
        System.out.println("  row col\n" + "-   Help: h/help\n" + "-   Quit: q/quit");
        rounds++;
    }

    /**
     * Reveals the input square.
     *
     * @param row the row index of the square
     * @param col the col index of the square
     */
    public void reveal(int row, int col) {
        try {
            bBoard[row][col] = true;
            if (backBoard[row][col] == 9) {
                printLoss();
                System.exit(0);
            }
            iBoard[row][col] = "" + backBoard[row][col];
            rounds++;
        } catch (ArrayIndexOutOfBoundsException oob) { //If the input was out of bounds
            System.err.println("\nInvalid Command: " + oob.getMessage());
        }
    } //reveal

    /**
     * Reveals the locations of the mines for one turn.
     */
    public void noFog() { //Essentially printMineField with noFog changes
        rounds++;
        System.out.println("\n Rounds Completed: " + rounds + "\n");
        for (int i = 0; i < iBoard.length; i++) {
            System.out.print(" " + i);
            for (int j = 0; j < iBoard[i].length; j++) {
                if (bBoard[i][j] == true) {
                    if (backBoard[i][j] == 9) {
                        System.out.print("|<" + iBoard[i][j] + ">|");
                    } else {
                        System.out.print("| " + iBoard[i][j] + " ");
                    }
                } else {
                    if (backBoard[i][j] == 9) {
                        System.out.print("|< >");
                    } else {
                        System.out.print("|   ");
                    }
                }
            }
            System.out.print("|");
            System.out.println();
        }
        System.out.print(" ");
        for (int i = 0; i < cols; i++) {
            System.out.print("   " + i);
        }
        System.out.println("\n");

    } //noFog

    /**
     * Determines whether or not the game has been won yet.
     *
     * @return true is the game has been won; false otherwise.
     */
    public boolean isWon() {
        for (int i = 0; i < bBoard.length; i++) {
            for (int j = 0; j < bBoard[i].length; j++) {
                if (backBoard[i][j] != 9) { //If it is not a bomb
                    String compare = "" + backBoard[i][j];
                    if ((!compare.equals(iBoard[i][j])) || (!bBoard[i][j])) {
                        return false; //Not won if square wasnt revealed
                    }
                } else { //If it is a mine
                    if (!iBoard[i][j].equals("F")) { //Square must be marked
                        return false;
                    }
                }
            }
        }
        return true; //Returns true if the loops finish with no false returns
    } //isWon

    /**
     * Prints the welcome art using {@code printFiles(String)}.
     */
    public void printWelcome() {
        printFiles("resources/welcome.txt");
    }

    /**
     * Prints the win art and the user's score.
     */
    public void printWin() {
        /*
         * Print win must print score on the final line with the ASCII Art,
         * So a separate method was used
         */
        try {
            File txtFile = new File("resources/gamewon.txt");
            Scanner txtReader = new Scanner(txtFile);
            while (txtReader.hasNextLine()) {
                System.out.print(txtReader.nextLine());
                if (txtReader.hasNextLine()) {
                    System.out.println();
                }
            }
            System.out.printf(" %.2f", score);
            System.out.println();
        } catch (FileNotFoundException fne) {
            System.err.println("MinesweeperGame: " + fne.getMessage());
        }

    } //printWin

    /**
     * Prints the loss art.
     */
    public void printLoss() {
        printFiles("resources/gameover.txt");
    } //printLoss

    /**
     * Prints the contents of an input file to standard output.
     *
     * @param fileLocation the path to the desired file to be output
     */
    public void printFiles(String fileLocation) {
        try {
            File txtFile = new File(fileLocation);
            Scanner txtReader = new Scanner(txtFile);
            while (txtReader.hasNextLine()) {
                if (txtReader.hasNextLine()) {
                    System.out.println(txtReader.nextLine());
                } else {
                    System.out.print(txtReader.next());
                }
            }
        } catch (FileNotFoundException fne) {
            System.err.println("MinesweeperGame: " + fne.getMessage());
        }
    }

    /**
     * The main method that controls the flow of the {@code minesweeperGame}.
     * Uses {@code readSeed()}, {@code createBoard()}, {@code printWelcome()},
     * {@code printMineField()}, {@code isWon()}, {@code promptUser()}, and
     * {@code printWin()}.
     */
    public void play() {
        readSeed();
        createBoard();
        printWelcome();
        printMineField();
        while (!isWon()) {
            promptUser();
            if (!command.equals("nofog")) { //noFog will print it's own minefield
                printMineField();
            }
        }
        score = (100.0 * rows * cols / rounds);
        printWin();
    } //play

} //MineSweeperGame
