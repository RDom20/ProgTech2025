package hu.nye.progtech.service;

import hu.nye.progtech.domain.Ship;

public class GameStateDeciderService {

    public boolean isFinished(final Ship ship) {
        return ship.isSunk();  // feltételezve, hogy van isSunk() metódus a Ship-ben
    }
}
