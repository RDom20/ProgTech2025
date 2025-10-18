package hu.nye.progtech.display;

import hu.nye.progtech.domain.GameMap;
import hu.nye.progtech.domain.Ship;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"PMD.AtLeastOneConstructor", "PMD.GuardLogStatement", "PMD.UseVarargs"})
//AtLeastOneConstructor: nem kell konstruktort írni.
// GuardLogStatement: logolás előtt nem kell if (LOGGER.isInfoEnabled()) ellenőrzés.
// UseVarargs: nem kell String... helyett tömböt használni.

public class MapDisplayer {
    private static final Logger LOGGER = LoggerFactory.getLogger(MapDisplayer.class); //A Logger segítségével naplóüzeneteket írunk ki

    //Térkép kirajzolása logba
    public void displayMap(final GameMap gameMap) {
        final int size = gameMap.getSize();
        final char[][] matrix = emptySetup(size);

        // Jelöljük a hajókat és a találatokat
        for (Ship ship : gameMap.getShips()) {
            for (int i = 0; i < ship.getLength(); i++) {
                int row = ship.getStartRow();
                int col = ship.getStartCol();

                if (ship.isHorizontal()) {
                    col += i;
                } else {
                    row += i;
                }

                if (ship.isHitAt(row, col)) {
                    matrix[row][col] = 'X'; // találat
                } else {
                    matrix[row][col] = 'O'; // hajó test
                }
            }
        }


        printColoredMap(matrix);
    }

    private void printColoredMap(char[][] map) {
        int size = map.length;
        System.out.print("    ");
        for (int i = 1; i <= size; i++) {
            System.out.printf("%-2d ", i);
        }
        System.out.println();

        for (int row = 0; row < size; row++) {
            System.out.printf("%-4d", row + 1);
            for (int col = 0; col < size; col++) {
                char c = map[row][col];
                switch (c) {
                    case '■': // hajó test
                        System.out.print("\u001B[34m" + c + "\u001B[0m  "); // kék
                        break;
                    case 'X': // találat
                        System.out.print("\u001B[32m" + c + "\u001B[0m  "); // zöld
                        break;
                    case 'O': // mellélövés
                        System.out.print("\u001B[33m" + c + "\u001B[0m  "); // sárga
                        break;
                    default:
                        System.out.print(c + "  "); // üres mező
                }
            }
            System.out.println();
        }
    }



    private char[][] emptySetup(final int mapSize) {
        final char[][] matrix = new char[mapSize][mapSize];

        for (int row = 0; row < mapSize; row++) {
            for (int col = 0; col < mapSize; col++) {
                matrix[row][col] = '\u25A1';
            }
        }

        return matrix;
    }

    private void addShipHitsToDisplay(final Ship ship, final char[][] matrix) {
        for (int i = 0; i < ship.getLength(); i++) {
            int row = ship.getStartRow();
            int col = ship.getStartCol();

            if (ship.isHorizontal()) {
                col = col + i;
            } else {
                row = row + i;
            }
        }
    }
}
