package hu.nye.progtech.domain;

public class ShotResult { //immutable

    private final boolean hit;
    private final boolean sunk;
    private final boolean gameOver;
    private final String message;

    public ShotResult(boolean hit, boolean sunk, boolean gameOver, String message) {
        this.hit = hit;
        this.sunk = sunk;
        this.gameOver = gameOver;
        this.message = message;
    }

    // Getter metódusok a privát mezők lekéréséhez:
    public boolean isHit() {
        return hit;
    }

    public boolean isSunk() {
        return sunk;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public String getMessage() {
        return message;
    }
}
