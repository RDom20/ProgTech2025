package hu.nye.progtech.service;

import hu.nye.progtech.persistence.*;

import java.util.List;

public class HighScoreService {
    private final PlayerDao playerDao;

    public HighScoreService(PlayerDao dao) { this.playerDao = dao; }

    public void recordWin(String playerName) {
        playerDao.upsertWin(playerName);
    }

    public List<PlayerRecord> getTop(int n) {
        return playerDao.getHighScores(n);
    }

    public static void displayHighScores(HighScoreService hs, int limit) {
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
