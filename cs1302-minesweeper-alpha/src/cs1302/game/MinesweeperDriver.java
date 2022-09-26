package cs1302.game;

import java.util.Scanner;

/**
 * The driver class for {@code MinesweeperGame}.
 */
public class MinesweeperDriver {

    /**
     * The main method for {@code MinesweeperDriver}.
     *
     * @param args the seed file path
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: MinesweeperDriver SEED_FILE_PATH");
            System.exit(1);
        }
        String fileSpot = args[0];
        Scanner stdIn = new Scanner(System.in);
        MinesweeperGame test = new MinesweeperGame(stdIn, fileSpot);
        test.play();
    } //main
} //MineSweeperDriver
