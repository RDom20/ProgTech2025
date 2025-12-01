package hu.nye.progtech.domain;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class AIPlayerTest {

    @Test
    void testRandomShotDoesNotRepeat() {
        AIPlayer ai = new AIPlayer(10, new Random(1));

        int[] s1 = ai.randomShot();
        int[] s2 = ai.randomShot();

        assertFalse(s1[0] == s2[0] && s1[1] == s2[1]);
    }

    @Test
    void randomMoveWithinBounds() {
        AIPlayer ai = new AIPlayer(3, new Random(2));
        int[] move = ai.randomShot();
        assertNotNull(move);
        assertTrue(move[0] >= 0 && move[0] < 3);
        assertTrue(move[1] >= 0 && move[1] < 3);
    }

    @Test
    void testNotifyHitStartsTargeting() {
        AIPlayer ai = new AIPlayer(10, new Random(3));
        int[] shot = {3, 3};

        ai.notifyShotResult(shot, true, false);

        int[] next = ai.nextShot();

        assertNotNull(next);
    }

    @Test
    void testSunkResetsTargeting() {
        AIPlayer ai = new AIPlayer(10, new Random(4));

        ai.notifyShotResult(new int[]{3,3}, true, false);
        ai.notifyShotResult(new int[]{3,4}, true, true);

        int[] next = ai.nextShot();

        assertNotNull(next);
        // a süllyesztett mező már a shotsTaken-ben van, így next nem adhatja vissza ugyanazt
        assertFalse(next[0] == 3 && next[1] == 4);
    }

    @Test
    void testTargetingSequenceAfterMultipleHits() {
        AIPlayer ai = new AIPlayer(5, new Random(5));
        // simulate hits that should start a targeting mode
        ai.notifyShotResult(new int[]{1,1}, true, false);
        ai.notifyShotResult(new int[]{1,2}, true, false);
        int[] next = ai.nextShot();
        assertNotNull(next);
        // next should be within bounds
        assertTrue(next[0] >= 0 && next[0] < 5);
        assertTrue(next[1] >= 0 && next[1] < 5);
    }

    @Test
    void testNoRepeatAfterManyRandomShots() {
        AIPlayer ai = new AIPlayer(3, new Random(6));
        boolean[][] seen = new boolean[3][3];
        for (int i = 0; i < 9; i++) {
            int[] s = ai.randomShot();
            assertNotNull(s);
            assertFalse(seen[s[0]][s[1]]);
            seen[s[0]][s[1]] = true;
        }
    }

    @Test
    void testSunkResetsAndAllowsNewTarget() {
        AIPlayer ai = new AIPlayer(4, new Random(7));
        ai.notifyShotResult(new int[]{2,2}, true, false);
        ai.notifyShotResult(new int[]{2,3}, true, true); // sunk
        int[] next = ai.nextShot();
        assertNotNull(next);
        // ensure not shooting the sunk coordinates repeatedly
        assertFalse(next[0] == 2 && next[1] == 3);
    }

    @Test
    void notifyHitThenSunkResets() {
        AIPlayer ai = new AIPlayer(5, new Random(8));
        ai.notifyShotResult(new int[]{2,2}, true, false);
        ai.notifyShotResult(new int[]{2,3}, true, true);
        int[] next = ai.nextShot();
        assertNotNull(next);
        // ensure not same as sunk
        assertFalse(next[0]==2 && next[1]==3);
    }
}
