
public class SLList {
    public intNode first;

    public SLList(int x){
        first = new intNode(x,null);
    }

    public void addFirst(int x ){
        first = new intNode(x,first);
    }

    public void addLast(int x) {
        if (first == null) {
            first = new intNode(x, null);
        } else {
            intNode current = first;
            while (current.nextP != null) {
                current = current.nextP;
            }
            current.nextP = new intNode(x, null);
        }
    }
    public int getFirst(){
        return first.elem;
    }




    public static void main(String args[]){
        SLList list = new SLList(5);
        list.addFirst(10);
        list.addFirst(90);
        list.addLast(0);



    }



}
