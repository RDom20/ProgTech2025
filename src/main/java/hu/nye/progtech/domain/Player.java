package hu.nye.progtech.domain;

import java.util.Objects;

public class Player {
    private final String name;
    private final char symbol;

    public Player(String name, char symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public char getSymbol() {
        return symbol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Player)) {
            return false;
        }
        Player player = (Player) o;
        return symbol == player.symbol && Objects.equals(name, player.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, symbol);
    }

    @Override
    public String toString() {
        return "Player{name='" + name + "', symbol=" + symbol + "}";
    }
}
