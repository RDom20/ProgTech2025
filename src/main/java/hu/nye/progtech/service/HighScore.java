package hu.nye.progtech.service;

import java.util.List;

import hu.nye.progtech.persistence.PlayerDao;
import hu.nye.progtech.persistence.PlayerRecord;


public class HighScore {
    private final PlayerDao playerDao;

    public HighScore(PlayerDao dao) {
        this.playerDao = dao;
    }

    public void recordWin(String playerName) {
        if (playerName == null || playerName.isBlank()) {
            return;
        }
        playerDao.upsertWin(playerName);
    }

    public List<PlayerRecord> getTop(int n) {
        return playerDao.getHighScores(n);
    }

    public static void displayHighScores(HighScore hs, int limit) {
        System.out.println("\n=== High Scores ===");
        List<PlayerRecord> top = hs.getTop(limit);
        if (top == null || top.isEmpty()) {
            System.out.println("Nincsenek eredmények.");
            return;
        }
        System.out.printf("%-4s %-20s %s%n", "Rank", "Name", "Wins");
        int rank = 1;
        for (PlayerRecord p : top) {
            System.out.printf("%-4d %-20s %d%n", rank++, p.getName(), p.getWins());
        }
        System.out.println("===================\n");
    }
}
