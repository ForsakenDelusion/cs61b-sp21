package deque;

public class Test {

    public static void main(String[] args) {
        LinkedListDeque<Integer> list = new LinkedListDeque<>();
        list.addFirst(1);
        list.addFirst(3);
        list.addFirst(5);
        list.addFirst(2);
        list.printDeque();
        System.out.print(list.size);

    }
}
