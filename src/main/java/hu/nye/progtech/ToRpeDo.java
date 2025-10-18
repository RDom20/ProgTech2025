package hu.nye.progtech;

import hu.nye.progtech.display.MapDisplayer;
import hu.nye.progtech.domain.Game;
import hu.nye.progtech.domain.GameMap;
import hu.nye.progtech.domain.Player;
import hu.nye.progtech.domain.RocketDestination;
import hu.nye.progtech.domain.Ship;
import hu.nye.progtech.service.GameService;
import hu.nye.progtech.service.GameStateDeciderService;

import java.util.*;

public class ToRpeDo {

    private static final int MAP_SIZE = 10;
    private static final char EMPTY_CELL = '\u25A1';  // □ üres négyzet
    private static final char SHIP_CELL = '\u25A0';   // ■ teli négyzet

    private static Map<Integer, Integer> shipInventory = new LinkedHashMap<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n=== TORPEDÓ JÁTÉK ===");
        System.out.print("Készítette: Ruska Dominik\n");
        System.out.println("Üdvözöllek!\n");

        // Játékosok száma
        int playersCount = 0;
        while (playersCount < 1 || playersCount > 2) {
            System.out.print("Hány játékos lesz? (1 vagy 2): ");
            if (scanner.hasNextInt()) {
                playersCount = scanner.nextInt();
                if (playersCount != 1 && playersCount != 2) {
                    System.out.println("Csak 1 vagy 2 lehet.");
                }
            } else {
                System.out.println("Adj meg egy számot!");
                scanner.next();
            }
        }
        scanner.nextLine(); // buffer kiürítése

        initShips();

        // Játékosok és térképek inicializálása
        List<Player> players = new ArrayList<>();
        List<GameMap> maps = new ArrayList<>();
        List<char[][]> mapsGrids = new ArrayList<>();

        for (int i = 1; i <= playersCount; i++) {
            System.out.print("\nAdd meg a " + i + ". játékos nevét: ");
            String name = scanner.nextLine();
            Player player = new Player(name);
            players.add(player);

            char[][] mapGrid = new char[MAP_SIZE][MAP_SIZE];
            for (int r = 0; r < MAP_SIZE; r++) {
                Arrays.fill(mapGrid[r], EMPTY_CELL);
            }
            mapsGrids.add(mapGrid);
        }

        // Hajók elhelyezése
        for (int playerIndex = 0; playerIndex < playersCount; playerIndex++) {
            char[][] mapGrid = mapsGrids.get(playerIndex);

            Map<Integer, Integer> localShipInventory = new LinkedHashMap<>(shipInventory);
            List<Ship> ships = new ArrayList<>();
            int totalShips = localShipInventory.values().stream().mapToInt(Integer::intValue).sum();

            // AI hajók elhelyezése automatikusan, ha 1 játékos van
            if (playersCount == 1 && playerIndex == 1) {
                break;
            }

            System.out.println("\n" + players.get(playerIndex).getName() + ", helyezd el a hajóidat: \n");

            while (totalShips > 0) {
                printMap(mapGrid, ships, playerIndex);

                System.out.println("\nHajók készlete:");
                localShipInventory.forEach((size, count) -> System.out.println(size + " hosszú hajó: " + count + " db"));

                System.out.print("\nAdd meg a hajó hosszát (pl. 1, 2, 3, 4, 5): ");
                int length;
                if (scanner.hasNextInt()) {
                    length = scanner.nextInt();
                    if (length >= 1 && length <= 5) {
                        if (!localShipInventory.containsKey(length) || localShipInventory.get(length) == 0) {
                            System.out.println("Az ilyen hosszú hajó elfogyott!\n");
                            continue;
                        }
                    } else {
                        System.out.println("Érvénytelen hosszúság! Kérlek válassz 1 és 5 között!\n");
                        continue;
                    }
                } else {
                    System.out.println("Érvénytelen hosszúság! Kérlek válassz egy számot!\n");
                    scanner.nextLine();
                    continue;
                }
                scanner.nextLine();  // buffer kiürítése


                int startRow, startCol, endRow = 0, endCol = 0;

                System.out.println("\nAdd meg a hajó kezdő koordinátáit:");

                System.out.print("Sor (1-10): ");
                if (scanner.hasNextInt()) {
                    startRow = scanner.nextInt() - 1;
                } else {
                    System.out.println("Érvénytelen szám!\n");
                    scanner.nextLine();
                    continue;
                }

                System.out.print("Oszlop (1-10): ");
                if (scanner.hasNextInt()) {
                    startCol = scanner.nextInt() - 1;
                } else {
                    System.out.println("Érvénytelen szám!\n");
                    scanner.nextLine();
                    continue;
                }

                if (length > 1) {
                    System.out.println("\nAdd meg a hajó végső koordinátáit:");

                    System.out.print("Sor (1-10): ");
                    if (scanner.hasNextInt()) {
                        endRow = scanner.nextInt() - 1;
                    } else {
                        System.out.println("Érvénytelen szám!\n");
                        scanner.nextLine();
                        continue;
                    }

                    System.out.print("Oszlop (1-10): ");
                    if (scanner.hasNextInt()) {
                        endCol = scanner.nextInt() - 1;
                    } else {
                        System.out.println("Érvénytelen szám!\n");
                        scanner.nextLine();
                        continue;
                    }
                } else {
                    endRow = startRow;
                    endCol = startCol;
                }
                scanner.nextLine();

                boolean horizontal;
                if (startRow == endRow) {
                    horizontal = true;
                } else if (startCol == endCol) {
                    horizontal = false;
                } else {
                    System.out.println("A hajó csak vízszintesen vagy függőlegesen helyezhető el!\n");
                    continue;
                }

                // Kezdő koordináták rendezése (balról jobbra / fentről lefelé)
                if (horizontal && endCol < startCol) {
                    int temp = startCol;
                    startCol = endCol;
                    endCol = temp;
                } else if (!horizontal && endRow < startRow) {
                    int temp = startRow;
                    startRow = endRow;
                    endRow = temp;
                }

                int calculatedLength = horizontal ? (endCol - startCol + 1) : (endRow - startRow + 1);
                if (calculatedLength != length) {
                    System.out.println("A hajó hosszának meg kell egyeznie a választott hosszúsággal (" + length + ")!\n");
                    continue;
                }

                if (!isValidPosition(mapGrid, ships, startRow, startCol, length, horizontal)) {
                    System.out.println("Hibás pozíció vagy ütközés!\n");
                    continue;
                }

                placeShip(mapGrid, startRow, startCol, length, horizontal);

                ships.add(new Ship(length, startRow, startCol, horizontal));
                localShipInventory.put(length, localShipInventory.get(length) - 1);
                totalShips--;
            }

            System.out.println("Minden hajó elhelyezve.");
            maps.add(new GameMap(MAP_SIZE, ships));
        }

        // AI hajók létrehozása, ha egy játékos van
        List<Ship> aiShips = new ArrayList<>();
        char[][] aiMap = new char[MAP_SIZE][MAP_SIZE];
        for (int r = 0; r < MAP_SIZE; r++) {
            Arrays.fill(aiMap[r], EMPTY_CELL);
        }
        if (playersCount == 1) {
            placeRandomShipsOnMap(aiShips, aiMap);
            maps.add(new GameMap(MAP_SIZE, aiShips));
        }

        GameStateDeciderService deciderService = new GameStateDeciderService();
        MapDisplayer mapDisplayer = new MapDisplayer();
        GameService gameService = new GameService(mapDisplayer);

        Game game = new Game(maps, players);

        System.out.println("\nMinden játékos hajói elhelyezve, indulhat a játék!");

        if (playersCount == 2) {
            gameService.startGame(game);
        } else {
            playAgainstAI(scanner, mapsGrids.get(0), maps.get(0).getShips(), aiMap, aiShips, mapsGrids);

        }

        scanner.close();
    }

    private static void playAgainstAI(Scanner scanner, char[][] playerMap, List<Ship> playerShips,
                                      char[][] aiMap, List<Ship> aiShips, List<char[][]> mapsGrids) {
        Set<String> playerShots = new HashSet<>();
        AIPlayer ai = new AIPlayer(MAP_SIZE);
        Random random = new Random();

        int turn = 0; // 0 = player, 1 = AI

        while (true) {
            if (turn == 0) {
                System.out.println("\nAI térkép:");
                printMapMask(aiMap);

                int shotRow, shotCol;
                while (true) {
                    System.out.print("Sor (1-10): ");
                    if (scanner.hasNextInt()) {
                        shotRow = scanner.nextInt() - 1;
                    } else {
                        System.out.println("Érvénytelen szám!");
                        scanner.nextLine();
                        continue;
                    }

                    System.out.print("Oszlop (1-10): ");
                    if (scanner.hasNextInt()) {
                        shotCol = scanner.nextInt() - 1;
                    } else {
                        System.out.println("Érvénytelen szám!");
                        scanner.nextLine();
                        continue;
                    }
                    scanner.nextLine();

                    String pos = shotRow + "," + shotCol;
                    if (shotRow < 0 || shotRow >= MAP_SIZE || shotCol < 0 || shotCol >= MAP_SIZE) {
                        System.out.println("Érvénytelen koordináta!");
                    } else if (playerShots.contains(pos)) {
                        System.out.println("Erre a mezőre már lőttél!");
                    } else {
                        playerShots.add(pos);
                        break;
                    }
                }

                RocketDestination playerTarget = new RocketDestination(shotRow, shotCol);
                boolean hit = fireAt(aiShips, aiMap, playerTarget);

                if (allShipsSunk(aiShips)) {
                    System.out.println("Gratulálok, nyertél!");
                    break;
                }

                if (hit) {
                    System.out.println("Talált! Újra lőhetsz.");
                    turn = 0;
                } else {
                    System.out.println("Nem talált.");
                    turn = 1;
                }

            } else {
                System.out.println("\nAI köre...");

                int[] aiShot = ai.nextShot();
                System.out.println("AI lő: sor " + (aiShot[0] + 1) + ", oszlop " + (aiShot[1] + 1));

                RocketDestination aiTarget = new RocketDestination(aiShot[0], aiShot[1]);
                boolean hit = fireAt(playerShips, mapsGrids.get(0), aiTarget);

                boolean sunk = false;
                if (hit) {
                    for (Ship s : playerShips) {
                        if (shipContains(s, aiShot[0], aiShot[1]) && s.isSunk()) {
                            sunk = true;
                            break;
                        }
                    }
                }

                ai.notifyShotResult(aiShot, hit, sunk);

                if (allShipsSunk(playerShips)) {
                    System.out.println("Sajnos az AI nyert!");
                    break;
                }

                if (hit) {
                    System.out.println("AI talált! Újra lő.");
                    turn = 1;
                } else {
                    System.out.println("AI nem talált.");
                    turn = 0;
                }

                System.out.println("\nSaját térkép állapota:");
                printMap(mapsGrids.get(0), playerShips, 0); // AI által célzott térkép mutatása

            }
        }
    }

    private static boolean shipContains(Ship ship, int row, int col) {
        int sr = ship.getStartRow();
        int sc = ship.getStartCol();
        int len = ship.getLength();
        if (ship.isHorizontal()) {
            return row == sr && col >= sc && col < sc + len;
        } else {
            return col == sc && row >= sr && row < sr + len;
        }
    }

    private static void placeRandomShipsOnMap(List<Ship> ships, char[][] map) {
        Random random = new Random();
        int[] lengths = {5, 4, 3, 2, 2, 1, 1, 1, 1};
        for (int length : lengths) {
            boolean placed = false;
            while (!placed) {
                boolean horizontal = random.nextBoolean();
                int row = random.nextInt(MAP_SIZE);
                int col = random.nextInt(MAP_SIZE);

                if (isValidPosition(map, ships, row, col, length, horizontal)) {
                    placeShip(map, row, col, length, horizontal);
                    ships.add(new Ship(length, row, col, horizontal));
                    placed = true;
                }
            }
        }
    }

    private static boolean isValidPosition(char[][] map, List<Ship> existingShips,
                                           int startRow, int startCol, int length, boolean horizontal) {
        int endRow = horizontal ? startRow : startRow + length - 1;
        int endCol = horizontal ? startCol + length - 1 : startCol;

        if (startRow < 0 || startCol < 0 || endRow >= MAP_SIZE || endCol >= MAP_SIZE) {
            return false;
        }

        // Ütközés a pályán
        for (int i = 0; i < length; i++) {
            int r = horizontal ? startRow : startRow + i;
            int c = horizontal ? startCol + i : startCol;

            if (map[r][c] == SHIP_CELL) {
                return false;
            }
        }

        // Túl közeli elhelyezés ellenőrzése
        if (isTooClose(existingShips, startRow, startCol, length, horizontal)) {
            return false;
        }

        return true;
    }


    private static void placeShip(char[][] map, int startRow, int startCol, int length, boolean horizontal) {
        for (int i = 0; i < length; i++) {
            int r = horizontal ? startRow : startRow + i;
            int c = horizontal ? startCol + i : startCol;
            map[r][c] = SHIP_CELL;
        }
    }

    private static boolean isTooClose(List<Ship> existingShips,
                                      int startRow, int startCol,
                                      int length, boolean horizontal) {
        // Az új hajó mezőinek ellenőrzése
        for (int i = 0; i < length; i++) {
            int r = horizontal ? startRow : startRow + i;
            int c = horizontal ? startCol + i : startCol;

            // Csak közvetlen szomszédos (nem átlós) mezők ellenőrzése
            int[][] directions = {
                    {-1, 0}, // fel
                    {1, 0},  // le
                    {0, -1}, // bal
                    {0, 1}   // jobb
            };

            for (int[] dir : directions) {
                int rr = r + dir[0];
                int cc = c + dir[1];

                if (rr >= 0 && rr < MAP_SIZE && cc >= 0 && cc < MAP_SIZE) {
                    for (Ship s : existingShips) {
                        for (RocketDestination coord : s.getCoordinates()) {
                            if (coord.getRow() == rr && coord.getCol() == cc) {
                                return true; // túl közel
                            }
                        }
                    }
                }
            }
        }

        return false; // nincs probléma
    }

    private static boolean fireAt(List<Ship> ships, char[][] map, RocketDestination target) {
        int row = target.getRow();
        int col = target.getCol();

        if (map[row][col] == 'X') {
            System.out.println("Ezt a mezőt már eltaláltad.");
            return false;
        }

        for (Ship ship : ships) {
            if (shipContains(ship, row, col)) {
                map[row][col] = 'X';
                ship.registerHit(row, col);
                System.out.println("Talált!");
                return true;
            }
        }

        map[row][col] = 'O';
        System.out.println("Nem talált.");
        return false;
    }

    private static boolean allShipsSunk(List<Ship> ships) {
        for (Ship ship : ships) {
            if (!ship.isSunk()) {
                return false;
            }
        }
        return true;
    }

    private static void markSurroundings(char[][] map, Ship ship) {
        int startRow = ship.getStartRow();
        int startCol = ship.getStartCol();
        int length = ship.getLength();
        boolean horizontal = ship.isHorizontal();

        // Végignézi a hajó összes mezőjét
        for (int i = -1; i <= length; i++) {
            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    int r, c;
                    if (horizontal) {
                        r = startRow + dr;
                        c = startCol + i + dc;
                    } else {
                        r = startRow + i + dr;
                        c = startCol + dc;
                    }

                    // Érvényesség ellenőrzése
                    if (r >= 0 && r < MAP_SIZE && c >= 0 && c < MAP_SIZE) {
                        // Ha még üres mező, akkor jelölje '0'-val
                        if (map[r][c] == EMPTY_CELL) {
                            map[r][c] = '0';
                        }
                    }
                }
            }
        }
    }

    private static Ship getShipAt(List<Ship> ships, int row, int col) {
        for (Ship ship : ships) {
            if (shipContains(ship, row, col)) {
                return ship;
            }
        }
        return null;
    }

    private static void initShips() {
        shipInventory.put(1, 5);
        shipInventory.put(2, 4);
        shipInventory.put(3, 3);
        shipInventory.put(4, 2);
        shipInventory.put(5, 1);
    }

    private static void printMap(char[][] mapGrid, List<Ship> ships, int playerIndex) {  // playermap
        System.out.println("\n" + (playerIndex + 1) + ". játékos térképe:\n");
        System.out.print("    ");
        for (int i = 1; i <= MAP_SIZE; i++) {
            System.out.printf("%-3d", i);
        }
        System.out.println();

        for (int r = 0; r < MAP_SIZE; r++) {
            System.out.printf("%-3d ", r + 1);
            for (int c = 0; c < MAP_SIZE; c++) {
                char cell = mapGrid[r][c];
                if (cell == SHIP_CELL) {
                    // Hajó színe: játékos 1 = piros, játékos 2 vagy AI = kék
                    if (playerIndex == 0) {
                        System.out.print("\u001B[31m" + SHIP_CELL + "\u001B[0m  "); // piros
                    } else {
                        System.out.print("\u001B[34m" + SHIP_CELL + "\u001B[0m  "); // kék
                    }
                } else if (cell == 'X') {
                    System.out.print("\u001B[32mX\u001B[0m  "); // találat = zöld
                } else if (cell == 'O') {
                    System.out.print("\u001B[33mO\u001B[0m  "); // mellé = sárga
                } else {
                    System.out.print(EMPTY_CELL + "  ");
                }
            }
            System.out.println();
        }
    }

    private static void printMapMask(char[][] map) {  // aimap
        System.out.print("    ");
        for (int i = 1; i <= MAP_SIZE; i++) {
            System.out.printf("%-3d", i);  // -: balra igazít, 3: szélesség 3 karakter, d: egész szám (int)
        }
        System.out.println();

        for (int row = 0; row < MAP_SIZE; row++) {
            System.out.printf("%-3d ", row + 1);
            for (int col = 0; col < MAP_SIZE; col++) {
                char c = map[row][col];
                if (c == 'X') {
                    System.out.print("\u001B[32mX\u001B[0m  ");
                } else if (c == 'O') {
                    System.out.print("\u001B[33mO\u001B[0m  ");
                } else {
                    System.out.print("\u25A1  ");
                }
            }
            System.out.println();
        }
    }
}