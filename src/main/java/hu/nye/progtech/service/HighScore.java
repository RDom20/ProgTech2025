package hu.nye.progtech.service;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        if ("AI".equalsIgnoreCase(playerName.trim())) {
            return;
        }
        playerDao.upsertWin(playerName);
    }

    public List<PlayerRecord> getTop(int n) {
        return playerDao.getHighScores(n);
    }

    /**
     * Megjeleníti a top listát: beolvassa a forrást és kiírja rendezve.
     * Színezés szabálya:
     * - Az első (legmagasabb) győzelmi értékű játékosok arannyal jelennek meg.
     * - A második legmagasabb győzelmi értékű játékosok ezüsttel jelennek meg.
     * - A harmadik legmagasabb győzelmi értékű játékosok bronzzal jelennek meg.
     * Ha több játékosnak ugyanannyi győzelme van, holtversenyben vannak: ugyanazt a pozíciót kapják
     * (ugyanaz a szín), és a rangszám a "competition ranking" szabály szerint jelenik meg (pl. 1,2,2,4).
     */
    public static void displayHighScores(HighScore hs, int limit) {
        System.out.println("\n=== High Scores ===");
        if (hs == null) {
            System.out.println("Nincs highscore forrás megadva.");
            return;
        }

        List<PlayerRecord> top = hs.getTop(limit);
        if (top == null || top.isEmpty()) {
            System.out.println("Nincsenek eredmények.");
            return;
        }

        // Rendezés: wins csökkenő, név növekvő (case-insensitive)
        top = top.stream()
                .sorted(Comparator.comparingInt(PlayerRecord::getWins).reversed()
                        .thenComparing(p -> p.getName().toLowerCase()))
                .collect(Collectors.toList());

        // Kiválasztjuk az egyedi győzelmi értékeket a sorrendjükben (legnagyobbtól)
        Set<Integer> distinctWins = top.stream()
                .map(PlayerRecord::getWins)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        // ANSI 24-bit színkódok a megadott hex értékek alapján
        // gold = #DFB21E -> (223,178,30)
        // silver = #979592 -> (151,149,146)
        // bronze = #AB7917 -> (171,121,23)
        final String RESET = "\u001B[0m";
        final String GOLD = "\u001B[38;2;223;178;30m";
        final String SILVER = "\u001B[38;2;151;149;146m";
        final String BRONZE = "\u001B[38;2;171;121;23m";

        // Mapeljük a legfelső három különböző wins értéket érme-színre
        Integer firstWins = null;
        Integer secondWins = null;
        Integer thirdWins = null;

        int idx = 0;
        for (Integer w : distinctWins) {
            if (idx == 0) firstWins = w;
            else if (idx == 1) secondWins = w;
            else if (idx == 2) thirdWins = w;
            else break;
            idx++;
        }

        System.out.printf("%-4s %-20s %s%n", "Rank", "Name", "Wins");

        int index = 0;
        int displayRank = 0;
        Integer prevWins = null;
        for (PlayerRecord p : top) {
            index++;
            int wins = p.getWins();

            // competition ranking: ha az aktuális wins megegyezik az előzővel, ugyanaz a displayRank,
            // különben displayRank = current index
            if (prevWins == null || wins != prevWins) {
                displayRank = index;
            }

            String color = "";
            if (firstWins != null && wins == firstWins) {
                color = GOLD;
            } else if (secondWins != null && wins == secondWins) {
                color = SILVER;
            } else if (thirdWins != null && wins == thirdWins) {
                color = BRONZE;
            }

            if (!color.isEmpty()) {
                System.out.printf(color + "%-4d %-20s %d" + RESET + "%n", displayRank, p.getName(), p.getWins());
            } else {
                System.out.printf("%-4d %-20s %d%n", displayRank, p.getName(), p.getWins());
            }

            prevWins = wins;
        }
        System.out.println("===================\n");
    }
}
