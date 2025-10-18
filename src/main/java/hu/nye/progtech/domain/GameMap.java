package hu.nye.progtech.domain;

import java.util.ArrayList;
import java.util.List;

public class GameMap {
    private final int size;
    private final List<Ship> ships;
    private final List<RocketDestination> misses = new ArrayList<>();

    // ✅ Ez a konstruktor kell
    public GameMap(int size, List<Ship> ships) {
        this.size = size;
        this.ships = ships;
    }

    public int getSize() {
        return size;
    }

    public List<Ship> getShips() {
        return ships;
    }

    public List<RocketDestination> getMisses() {
        return misses;
    }

    public void addMiss(int row, int col) {
        misses.add(new RocketDestination(row, col));
    }
}
