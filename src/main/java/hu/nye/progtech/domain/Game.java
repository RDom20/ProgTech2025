package hu.nye.progtech.domain;

import java.util.List;

public final class Game {  //játékosok, térképek adatok tárolása, nem örökölhető, immutable

    private final List<GameMap> gameMaps;
    private final List<Player> players;

    public Game(final List<GameMap> gameMaps, final List<Player> players) {
        this.gameMaps = gameMaps;
        this.players = players;
    }

    public List<GameMap> getGameMaps() {
        return gameMaps;
    }

    public List<Player> getPlayers() {
        return players;
    }
}
