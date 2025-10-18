package hu.nye.progtech.domain;

import java.util.ArrayList;
import java.util.List;

public class Ship {

    private int length;
    private int startRow;
    private int startCol;
    private boolean horizontal;
    private List<RocketDestination> hits = new ArrayList<>();

    public Ship(int length, int startRow, int startCol, boolean horizontal) {
        this.length = length;
        this.startRow = startRow;
        this.startCol = startCol;
        this.horizontal = horizontal;
    }

    public int getLength() {
        return length;
    }

    public int getStartRow() {
        return startRow;
    }

    public int getStartCol() {
        return startCol;
    }

    public boolean isHorizontal() {
        return horizontal;
    }

    public List<RocketDestination> getCoordinates() {
        List<RocketDestination> coordinates = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            int row = horizontal ? startRow : startRow + i;
            int col = horizontal ? startCol + i : startCol;
            coordinates.add(new RocketDestination(row, col));
        }
        return coordinates;
    }

    public boolean isHitAt(int row, int col) {
        for (RocketDestination hit : hits) {
            if (hit.getRow() == row && hit.getCol() == col) {
                return true;
            }
        }
        return false;
    }

    public void registerHit(int row, int col) {
        if (!isHitAt(row, col)) {
            hits.add(new RocketDestination(row, col));
        }
    }

    public boolean isSunk() {
        return hits.size() >= length;
    }


}
