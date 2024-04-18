package deque;

import org.junit.Test;
import static org.junit.Assert.*;
public class ArrayDequeTest {
    @Test
    public void testAddFirst(){
        ArrayDeque<Integer> arrayTest = new ArrayDeque<>();
        assertTrue("A newly initialized LLDeque should be empty", arrayTest.isEmpty());
        arrayTest.addFirst(1);

        assertEquals(1, arrayTest.size());

        arrayTest.addFirst(2);
        arrayTest.addFirst(3);
        arrayTest.addFirst(4);
        arrayTest.addFirst(5);
        arrayTest.addFirst(6);
        arrayTest.addFirst(7);
        arrayTest.addFirst(8);

        assertEquals(8, arrayTest.size());

    }

    @Test
    public void testResize(){
        ArrayDeque<Integer> arrayTest = new ArrayDeque<>();
        for(int i = 100;i > 0 ;i--){
            arrayTest.addLast(1);
        }
        for(int i = 67;i > 0 ;i--){
            arrayTest.removeLast();
        }
        arrayTest.removeLast();
        arrayTest.removeLast();
        arrayTest.removeLast();

        arrayTest.printDeque();
    }


}
