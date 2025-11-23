package hu.nye.progtech.persistence;

import java.sql.*;
import java.util.*;

public class PlayerDao {
    private final DatabaseManager db;

    public PlayerDao(DatabaseManager db) { this.db = db; }

    public void upsertWin(String playerName) {
        try (Connection c = db.getConnection()) {
            PreparedStatement ps = c.prepareStatement("MERGE INTO players (name, wins) KEY(name) VALUES (?, COALESCE((SELECT wins FROM players WHERE name=?),0)+1)");
            ps.setString(1, playerName);
            ps.setString(2, playerName);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<PlayerRecord> getHighScores(int limit) {
        List<PlayerRecord> list = new ArrayList<>();
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT id, name, wins FROM players ORDER BY wins DESC, name ASC LIMIT ?")) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PlayerRecord p = new PlayerRecord(rs.getString("name"), rs.getInt("wins"));
                // ha szükséged van az id-re tárold külön, vagy bővíts a konstruktoron
                list.add(p);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }
}
