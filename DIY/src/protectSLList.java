public class protectSLList {
    public class intNode {
        public int elem;
        public intNode nextP;
        public intNode(int i, intNode n) {
            elem = i;
            nextP = n;
        }
    }
    private intNode sentinel;
    private intNode first;
    private int size;


    public protectSLList(int x){
        sentinel = new intNode(205,null);
        sentinel.nextP = new intNode(x,null);
        size = 1 ;
    }

    public protectSLList() {
        sentinel = new intNode(-205,null);
        size = 0;
    }

    public void addFirst(int x ){
        first = new intNode(x,first);0
        size+=1;
    }

    public void myAddLast(int x) {

            intNode current = first;
            while (current.nextP != null) {
                current = current.nextP;
            }
            current.nextP = new intNode(x, null);

        size+=1;

    }

    public void addLast(int x) {
        intNode p = first;

        /* Advance p to the end of the list. */
        while (p.nextP != null) {
            p = p.nextP;
        }
        p.nextP = new intNode(x, null);

        size+=1;

    }

    public int getFirst(){
        return first.elem;
    }

    public static int mysize(protectSLList p){
        if(p.first.nextP == null){
            return 1;
        }
        p.first.nextP=p.first.nextP.nextP;
        return 1 + mysize(p);
    }


    private static int size(intNode p) {
        if (p.nextP == null) {
            return 1;
        }

        return 1 + size(p.nextP);
    }

    public int size() {
        return size;
    }

    public static void main(String args[]){
        protectSLList list = new protectSLList();
        list.myAddLast(15);
        System.out.println(list.first.elem);
        System.out.println(list.size());


    }

}
