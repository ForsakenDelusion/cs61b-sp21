package deque;


import org.junit.Test;
import static org.junit.Assert.*;

public class ArrayDequeTest {
    @Test
    public void addIsEmptySizeTest(){
        ArrayDeque<String> arrayTest = new ArrayDeque<>();

        assertTrue("刚初始化的队列应该是空的",arrayTest.isEmpty());
        arrayTest.addFirst("前面");

        assertEquals(1,arrayTest.size());
        assertFalse("arrayTest应该包含一个元素",arrayTest.isEmpty());

        arrayTest.addLast("中间");
        assertEquals(2,arrayTest.size());

        arrayTest.addLast("后面");
        assertEquals(3,arrayTest.size());

        System.out.println("Deque:");
        arrayTest.printDeque();
    }

    @Test
    public void addRemoveTest(){
        ArrayDeque<Integer> arrayTest = new ArrayDeque<>();
        assertTrue("刚初始化的队列应该是空的",arrayTest.isEmpty());

        arrayTest.addFirst(10);
        assertFalse("arrayTest应该包含一个元素",arrayTest.isEmpty());

        arrayTest.removeFirst();
        assertTrue("移除操作结束之后队列应该是空的",arrayTest.isEmpty());
    }
    @Test
    public void removeEmptyTest(){

        ArrayDeque<Integer> arrayTest = new ArrayDeque<>();
        arrayTest.addFirst(3);

        arrayTest.removeLast();
        arrayTest.removeFirst();
        arrayTest.removeLast();
        arrayTest.removeFirst();

        String errorMsg = "size不对";
        errorMsg += "size()返回的是"+arrayTest.size()+"\n";
        errorMsg += "实际的size()应该返回0";

        assertEquals(errorMsg,0,arrayTest.size());

    }

    @Test
    public void multipleParamTest(){
        ArrayDeque<String> array1 = new ArrayDeque<>();
        ArrayDeque<Double> array2 = new ArrayDeque<>();
        ArrayDeque<Boolean> array3 = new ArrayDeque<>();

        array1.addFirst("string");
        array2.addFirst(3.14159);
        array3.addFirst(true);

        String s = array1.removeFirst();
        double d = array2.removeFirst();
        boolean b = array3.removeFirst();
    }

    @Test
    public void bigArrayDequeTest(){

        ArrayDeque<Integer> arrayTest = new ArrayDeque<>();
        for (int i = 0; i < 1000000; i++) {
            arrayTest.addLast(i);
        }

        for (double i = 0; i < 500000; i++) {
            assertEquals("应该具有相同的值", i, (double) arrayTest.removeFirst(), 0.0);
        }

        for (double i = 999999; i > 500000; i--) {
            assertEquals("应该具有相同的值", i, (double) arrayTest.removeLast(), 0.0);
        }
    }

    @Test
    public void testIterator(){
        ArrayDeque<Integer> arrayTest = new ArrayDeque<>();
        arrayTest.addFirst(1);
        arrayTest.addFirst(2);
        arrayTest.addFirst(3);
        arrayTest.addFirst(4);

        for(Integer i : arrayTest){
            System.out.println(i);
        }

    }

}
