package deque;

import org.junit.Test;

import java.util.Comparator;
import java.util.Deque;

import static org.junit.Assert.*;
public class MaxArrayDequeTest {

    public static class IntegerComparator implements Comparator<Integer>{
        @Override
        public int compare(Integer o1, Integer o2) {
            return o1 - o2;
        }
    }

    public static class StringComparator implements Comparator<String>{
        @Override
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);
        }
    }
    @Test
    public void testMaxDeafultString(){

        MaxArrayDeque<String> test1 = new MaxArrayDeque<>(new StringComparator());//当场new了个对象传进去()
        test1.addFirst("abc");
        test1.addLast("bca");
        test1.addLast("cab");

        System.out.println(test1.max());
    }

    @Test
    public void testMaxInteger1(){
        MaxArrayDeque<Integer> test2 = new MaxArrayDeque<>(new IntegerComparator());

        test2.addFirst(2);
        test2.addFirst(2);
        test2.addFirst(2);
        test2.addFirst(2);
        test2.addFirst(2);

        System.out.println(test2.max());
    }


}
