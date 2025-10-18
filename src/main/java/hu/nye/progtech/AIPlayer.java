package hu.nye.progtech;

import java.util.*;

/**
 * AIPlayer — egyszerű célzó AI:
 * - random lövéseket ad, amíg találat nincs
 * - találat után elmenti az első találatot és körülnéz 4 irányban
 * - ha találatot ér el egy irányban, abban az irányban folytatja (lépésről lépésre)
 * - ha mellélő, az adott irányt kizárja
 * - az AI addig lő egymás után, amíg találatot ér el (a játékos nem lő közben)
 */
public class AIPlayer {

    private final int boardSize;
    private final Set<String> shotsTaken = new HashSet<>();

    // Targeting állapot
    private int firstHitRow = -1;
    private int firstHitCol = -1;
    private int lastHitRow = -1;
    private int lastHitCol = -1;

    // lehetséges irányok az első találat körül (1: row+, 2: row-, 3: col+, 4: col-)
    private final List<Integer> possibleDirections = new ArrayList<>();

    // ha már kiválasztott irány, ezt tároljuk (0 = nincs)
    private int currentDirection = 0;

    private final Random rnd = new Random();

    public AIPlayer(int boardSize) {
        this.boardSize = boardSize;
        resetTargeting();
    }

    private void resetTargeting() {
        firstHitRow = -1;
        firstHitCol = -1;
        lastHitRow = -1;
        lastHitCol = -1;
        currentDirection = 0;
        possibleDirections.clear();
        possibleDirections.addAll(Arrays.asList(1, 2, 3, 4));
    }

    private String posKey(int r, int c) {
        return r + "," + c;
    }

    // Véletlenszerű lövés (olyan mezőt választ, amit még nem lőttek)
    public int[] randomShot() {
        int r, c;
        do {
            r = rnd.nextInt(boardSize);
            c = rnd.nextInt(boardSize);
        } while (shotsTaken.contains(posKey(r, c)));
        shotsTaken.add(posKey(r, c));
        return new int[]{r, c};
    }

    // Következő lövés célozva (ha van aktív találat), különben randomShot()
    public int[] nextShot() {
        // nincs aktív találat -> véletlen
        if (firstHitRow < 0) {
            return randomShot();
        }

        // ha van irányban folytatás, próbáljuk meg a következő cellát az utolsó találattól
        if (currentDirection != 0) {
            int nr = lastHitRow;
            int nc = lastHitCol;
            switch (currentDirection) {
                case 1 -> nr++;
                case 2 -> nr--;
                case 3 -> nc++;
                case 4 -> nc--;
            }
            if (isValidShot(nr, nc)) {
                shotsTaken.add(posKey(nr, nc));
                return new int[]{nr, nc};
            } else {
                // az irány lezárult vagy nem valós, visszaállítjuk az irányt és próbálkozunk más iránnyal
                currentDirection = 0;
            }
        }

        // nincs aktuális irány: válasszunk véletlenszerűen egyet a még lehetséges irányokból (az első találat körül)
        Collections.shuffle(possibleDirections, rnd);
        Iterator<Integer> it = possibleDirections.iterator();
        while (it.hasNext()) {
            int dir = it.next();
            int cr = firstHitRow;
            int cc = firstHitCol;
            switch (dir) {
                case 1 -> cr++;
                case 2 -> cr--;
                case 3 -> cc++;
                case 4 -> cc--;
            }
            if (isValidShot(cr, cc)) {
                // ezt választjuk, beállítjuk az aktuális irányt is
                currentDirection = dir;
                shotsTaken.add(posKey(cr, cc));
                return new int[]{cr, cc};
            } else {
                // ezt az irányt nem próbáljuk többet
                it.remove();
            }
        }

        // ha már nincs több irány, reset és random
        resetTargeting();
        return randomShot();
    }

    // A játékmechanika hívja ezt, amikor megvan a lövés eredménye.
    // shot: [row, col]; wasHit: találat-e; wasSunk: az adott lövés után elsüllyedt-e a célzott hajó
    public void notifyShotResult(int[] shot, boolean wasHit, boolean wasSunk) {
        int r = shot[0], c = shot[1];
        if (wasHit) {
            if (firstHitRow < 0) {
                firstHitRow = r;
                firstHitCol = c;
                // még nem választottunk irányt, lehetségesDirs benne maradnak
            }
            // minden találat után az utolsó találat frissül (innen haladunk tovább, ha irány van)
            lastHitRow = r;
            lastHitCol = c;
        }

        if (wasSunk) {
            // ha elsüllyedt a hajó, teljesen resetelünk targetinget
            resetTargeting();
            return;
        }

        if (!wasHit) {
            // ha mellélőttünk és volt aktuális irány, azt az irányt zárjuk ki
            if (currentDirection != 0) {
                possibleDirections.remove(Integer.valueOf(currentDirection));
                currentDirection = 0;
            } else {
                // ha még nem volt irány (első találat után próbálgattuk),
                // a mellélövésnél a már próbált irányt is elimináljuk (már kivette a nextShot)
                // semmi más dolga nincs itt
            }
        }
    }

    private boolean isValidShot(int r, int c) {
        return r >= 0 && r < boardSize && c >= 0 && c < boardSize && !shotsTaken.contains(posKey(r, c));
    }
}
