package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> AL = new AListNoResizing<>();
        BuggyAList<Integer> BL = new BuggyAList<>();
        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform (0, 100);
                AL.addLast(randVal);
                BL.addLast(randVal);
                System.out.println("addLast(" + randVal + ")");
                assertEquals(AL.getLast(), BL.getLast());
            } else if (operationNumber == 1) {
                // size
                int ALsize = AL.size();
                int BLsize = BL.size();
                System.out.println("ALsize: " + ALsize + " BLsize: " + BLsize);
                assertEquals(ALsize, BLsize);
            } else if (AL.size() > 0 && BL.size() > 0) {
                if (operationNumber == 2) {
                    int ALlast = AL.getLast();
                    int BLlast = BL.getLast();
                    System.out.println("the AL last number is " + ALlast + ", the BL last number is " + BLlast);
                    assertEquals(ALlast, BLlast);
                } else if (operationNumber == 3) {
                    int removeALlast = AL.removeLast();
                    int removeBLlast = BL.removeLast();
                    assertEquals(removeALlast, removeBLlast);
                    assertEquals(AL.size(), BL.size());
                }
            }

        }
    }
}
