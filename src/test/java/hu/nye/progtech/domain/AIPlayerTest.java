package hu.nye.progtech.domain;

import hu.nye.progtech.AIPlayer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AIPlayerTest {

    @Test
    void testRandomShotDoesNotRepeat() {
        AIPlayer ai = new AIPlayer(10);

        int[] s1 = ai.randomShot();
        int[] s2 = ai.randomShot();

        assertFalse(s1[0] == s2[0] && s1[1] == s2[1]);
    }

    @Test
    void testNotifyHitStartsTargeting() {
        AIPlayer ai = new AIPlayer(10);
        int[] shot = {3, 3};

        ai.notifyShotResult(shot, true, false);

        int[] next = ai.nextShot();

        assertNotNull(next);
    }

    @Test
    void testSunkResetsTargeting() {
        AIPlayer ai = new AIPlayer(10);

        ai.notifyShotResult(new int[]{3,3}, true, false);
        ai.notifyShotResult(new int[]{3,4}, true, true);

        int[] next = ai.nextShot();

        assertNotNull(next);
        assertFalse(next[0] == 3 && next[1] == 4); // ne ugyanoda lőjön
    }
}
