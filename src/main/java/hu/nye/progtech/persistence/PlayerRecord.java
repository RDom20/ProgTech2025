package hu.nye.progtech.persistence;

import java.util.Objects;

public class PlayerRecord {
    private Long id;      // objektum típus, alapértelmezett null
    private String name;
    private int wins;

    // Paraméter nélküli konstruktor (a tesztek ezt használják)
    public PlayerRecord() {
    }

    // Kényelmi konstruktorok
    public PlayerRecord(String name, int wins) {
        this(null, name, wins);
    }

    public PlayerRecord(Long id, String name, int wins) {
        this.id = id;
        this.name = name;
        this.wins = wins;
    }

    // Getterek
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getWins() {
        return wins;
    }

    // Setterek (a tesztek ezeket hívják)
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PlayerRecord)) {
            return false;
        }
        PlayerRecord that = (PlayerRecord) o;
        return wins == that.wins && Objects.equals(id, that.id) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, wins);
    }

    @Override
    public String toString() {
        return "PlayerRecord{id=" + id + ", name='" + name + "', wins=" + wins + "}";
    }
}
