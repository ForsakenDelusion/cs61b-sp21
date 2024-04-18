package deque;

import org.junit.Test;
import static org.junit.Assert.*;
public class ArrayDequeTest {
    @Test
    public void testSize(){
        ArrayDeque<Integer> arrayTest = new ArrayDeque<>();
        assertTrue("A newly initialized LLDeque should be empty", arrayTest.isEmpty());
        arrayTest.addFirst(1);

        assertEquals(1, arrayTest.size());
        assertFalse("lld1 should now contain 1 item", arrayTest.isEmpty());

        arrayTest.addFirst(1);
        assertEquals(2, arrayTest.size());
        assertFalse("lld1 should now contain 2 item", arrayTest.isEmpty());
    }





}
