package hu.nye.progtech.persistence;

public class PlayerRecord {
    private Long id;
    private String name;
    private int wins;

    public PlayerRecord() {}

    public PlayerRecord(String name, int wins) {
        this.name = name;
        this.wins = wins;
    }

    public PlayerRecord(Long id, String name, int wins) {
        this.id = id;
        this.name = name;
        this.wins = wins;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    @Override
    public String toString() {
        return "PlayerRecord{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", wins=" + wins +
                '}';
    }
}
