package hu.nye.progtech.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;


/**
 * Egyszerű AI játékos célzó logikával.
 * Javítások:
 * - minden lőtt pozíciót rögzítünk a shotsTaken halmazban,
 * - randomShot először összegyűjti az elérhető mezőket (elkerüli a végtelen ciklust),
 * - nextShot és notifyShotResult a shotsTaken-et használja, így soha nem lő újra kipróbált mezőre,
 * - süllyesztéskor (wasSunk) a targeting állapot törlődik, de a shotsTaken megmarad.
 */
public class AIPlayer {

    private final int boardSize;
    private final Set<String> shotsTaken = new HashSet<>();

    private int firstHitRow = -1;
    private int firstHitCol = -1;
    private int lastHitRow = -1;
    private int lastHitCol = -1;

    private final List<Integer> possibleDirections = new ArrayList<>();
    private int currentDirection = 0;

    private final Random rnd;

    public AIPlayer(int boardSize) {
        this(boardSize, new Random());
    }

    // Konstruktor seed-elhető Random-hoz (hasznos teszteléshez)
    public AIPlayer(int boardSize, Random rnd) {
        this.boardSize = boardSize;
        this.rnd = rnd == null ? new Random() : rnd;
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

    /**
     * Visszaad egy véletlenszerű, még nem kipróbált mezőt.
     * Ha nincs több elérhető mező, null-t ad vissza.
     */
    public int[] randomShot() {
        List<int[]> available = new ArrayList<>();
        for (int r = 0; r < boardSize; r++) {
            for (int c = 0; c < boardSize; c++) {
                if (!shotsTaken.contains(posKey(r, c))) {
                    available.add(new int[]{r, c});
                }
            }
        }
        if (available.isEmpty()) {
            return null;
        }
        int[] pick = available.get(rnd.nextInt(available.size()));
        shotsTaken.add(posKey(pick[0], pick[1]));
        return pick;
    }

    /**
     * Visszaadja a következő célpontot: ha van aktív targeting, azt folytatja,
     * különben véletlenszerű, még nem kipróbált mezőt ad.
     */
    public int[] nextShot() {
        if (firstHitRow < 0) {
            return randomShot();
        }

        if (currentDirection != 0) {
            int nr = lastHitRow;
            int nc = lastHitCol;
            switch (currentDirection) {
                case 1 -> nr++;
                case 2 -> nr--;
                case 3 -> nc++;
                case 4 -> nc--;
                default -> { /* no-op */ }
            }
            if (isValidShot(nr, nc)) {
                shotsTaken.add(posKey(nr, nc));
                return new int[]{nr, nc};
            } else {
                currentDirection = 0;
            }
        }

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
                default -> { /* no-op */ }
            }
            if (isValidShot(cr, cc)) {
                currentDirection = dir;
                shotsTaken.add(posKey(cr, cc));
                return new int[]{cr, cc};
            } else {
                it.remove();
            }
        }

        // ha nem találunk targeting célpontot, reset és random
        resetTargeting();
        return randomShot();
    }

    /**
     * Értesítés a lövés eredményéről.
     * - mindig jelöljük a lőtt mezőt kipróbáltnak,
     * - találat esetén beállítjuk a targeting koordinátákat,
     * - süllyesztéskor töröljük a targeting állapotot (de nem töröljük a shotsTaken-et).
     */
    public void notifyShotResult(int[] shot, boolean wasHit, boolean wasSunk) {
        int r = shot[0];
        int c = shot[1];

        // mindig jelöljük kipróbáltnak
        shotsTaken.add(posKey(r, c));

        if (wasHit) {
            if (firstHitRow < 0) {
                firstHitRow = r;
                firstHitCol = c;
            }
            lastHitRow = r;
            lastHitCol = c;
        }

        if (wasSunk) {
            // süllyesztéskor töröljük a targeting állapotot, de a shotsTaken megmarad
            resetTargeting();
            return;
        }

        if (!wasHit) {
            if (currentDirection != 0) {
                possibleDirections.remove(Integer.valueOf(currentDirection));
                currentDirection = 0;
            }
        }
    }

    private boolean isValidShot(int r, int c) {
        return r >= 0 && r < boardSize && c >= 0 && c < boardSize && !shotsTaken.contains(posKey(r, c));
    }
}
