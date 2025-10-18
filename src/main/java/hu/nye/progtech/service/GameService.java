package hu.nye.progtech.service;

import hu.nye.progtech.display.MapDisplayer;
import hu.nye.progtech.domain.Game;
import hu.nye.progtech.domain.GameMap;
import hu.nye.progtech.domain.Player;
import hu.nye.progtech.domain.RocketDestination;
import hu.nye.progtech.domain.Ship;
import java.util.List;
import java.util.Scanner;

public class GameService {

    private final MapDisplayer mapDisplayer;
    private final Scanner scanner = new Scanner(System.in);

    public GameService(MapDisplayer mapDisplayer) {
        this.mapDisplayer = mapDisplayer;
    }

    public void startGame(Game game) {
        List<Player> players = game.getPlayers();
        List<GameMap> gameMaps = game.getGameMaps();

        int currentPlayerIndex = 0;
        int otherPlayerIndex = 1;
        boolean gameOver = false;

        while (!gameOver) {
            Player currentPlayer = players.get(currentPlayerIndex);
            Player otherPlayer = players.get(otherPlayerIndex);

            GameMap currentPlayerMap = gameMaps.get(currentPlayerIndex);
            GameMap otherPlayerMap = gameMaps.get(otherPlayerIndex);

            System.out.println(currentPlayer.getName() + " támad!");

            // Maszkolt ellenség térkép megjelenítése
            printMaskedEnemyMap(otherPlayerMap);

            // Lövés bekérése
            RocketDestination shot = getShotFromPlayer();

            // Lövés feldolgozása
            boolean hit = fireAt(otherPlayerMap, shot);

            if (hit) {
                System.out.println("Talált! Újra lőhetsz.");

                if (allShipsSunk(otherPlayerMap)) {
                    System.out.println(currentPlayer.getName() + " nyert!");
                    gameOver = true;
                }

                // nem váltunk játékost, mert újra lő a támadó
            } else {
                System.out.println("Nem talált.");
                // váltás
                int temp = currentPlayerIndex;
                currentPlayerIndex = otherPlayerIndex;
                otherPlayerIndex = temp;
            }

            // Megjelenítjük a saját térképet (üresen vagy teljesen, hogy lássa a játékos)
            System.out.println(currentPlayer.getName() + " saját térképe:");
            mapDisplayer.displayMap(currentPlayerMap);
        }
    }

    private RocketDestination getShotFromPlayer() {
        System.out.print("Add meg a lövés sorát: ");
        int row = scanner.nextInt();
        System.out.print("Add meg a lövés oszlopát: ");
        int col = scanner.nextInt();

        return new RocketDestination(row, col);
    }

    private boolean fireAt(GameMap gameMap, RocketDestination shot) {
        // Egyszerűsített példa: végigmegyünk a hajókon, megnézzük, hogy eltaláltuk-e
        for (Ship ship : gameMap.getShips()) {
            if (isHit(ship, shot)) {
                ship.isHitAt(shot.getRow(), shot.getCol()); // feltételezzük, hogy van ilyen metódus
                return true;
            }
        }
        return false;
    }

    private boolean isHit(Ship ship, RocketDestination shot) {
        // Feltételezve, hogy a Ship osztály tudja, hogy hol vannak a részei
        int startRow = ship.getStartRow();
        int startCol = ship.getStartCol();
        int length = ship.getLength();

        for (int i = 0; i < length; i++) {
            int currentRow = startRow + (ship.isHorizontal() ? 0 : i);
            int currentCol = startCol + (ship.isHorizontal() ? i : 0);

            if (currentRow == shot.getRow() && currentCol == shot.getCol()) {
                return true;
            }
        }
        return false;
    }

    private boolean allShipsSunk(GameMap gameMap) {
        for (Ship ship : gameMap.getShips()) {
            if (!ship.isSunk()) {
                return false;
            }
        }
        return true;
    }

    private void printMaskedEnemyMap(GameMap enemyMap) {
        // Itt készíts egy maszkolt térképet, ahol csak a talált helyek és a mellélövések látszanak
        // Ez egy nagyon egyszerűsített változat:
        int size = enemyMap.getSize();
        char[][] display = new char[size][size];

        // Alap: mindenhol ~
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                display[i][j] = '~';
            }
        }

        // Jelöljük a találatokat 'X'-el és mellélövéseket 'O'-val
        for (Ship ship : enemyMap.getShips()) {
            for (int i = 0; i < ship.getLength(); i++) {
                int row = ship.getStartRow() + (ship.isHorizontal() ? 0 : i);
                int col = ship.getStartCol() + (ship.isHorizontal() ? i : 0);
                if (ship.isHitAt(row, col)) { // feltételezve, hogy van ilyen metódus
                    display[row][col] = 'X';
                }
            }
        }

        // Feltételezve, hogy van lista a mellélőtt helyekről
        for (RocketDestination miss : enemyMap.getMisses()) {
            display[miss.getRow()][miss.getCol()] = 'O';
        }

        // Kiírás
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(display[i][j] + " ");
            }
            System.out.println();
        }
    }
}
