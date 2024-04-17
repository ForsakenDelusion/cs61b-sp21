package deque;

public class LinkedListDeque<T> {
    class Node<T>{
        T elem;
        Node<T> pri;
        Node<T> next;

        public Node(T elem,Node<T> pri,Node<T> next){
            this.elem = elem;
            this.pri = pri;
            this.next = next;
        }
    }

    Node<T> sentinel;
    int size;
    public LinkedListDeque(){
        sentinel = new Node<>(null,null,null);
        size = 0;
    }

    public void addFirst(T item){
        if(this.isEmpty()){
            sentinel.next = new Node<>(item,sentinel,sentinel.pri);
            sentinel.pri = sentinel.next;
            sentinel.next.next=sentinel;
        }
        else{
            Node<T> curNode = sentinel.next;
            sentinel.next = new Node<>(item,sentinel,curNode);
            curNode.pri = sentinel.next;
        }
        size+=1;
    }

    public void addLast(T item){
        if(this.isEmpty()){
            sentinel.next = new Node<>(item,sentinel,sentinel.pri);
            sentinel.pri = sentinel.next;
            sentinel.next.next=sentinel;
        }
        else{
            Node<T> curNode = sentinel.pri;
            curNode.next = new Node<>(item,curNode,sentinel);
            sentinel.pri = curNode.next;
        }
        size+=1;
    }

    public boolean isEmpty(){
        return size()==0;
    }

    public int size(){
        return size;
    }

    public void printDeque(){
        int count = size;
        Node<T> curNode = sentinel.next;
        for(;size>0;size--){
            System.out.println(curNode.elem);
            curNode = curNode.next;
        }
    }

    public T removeFirst(){
        if(this.isEmpty()){
            return null;
        }
        else{
            T removeElem;
            removeElem = sentinel.next.elem;
            sentinel.next = sentinel.next.next;
            sentinel.next.pri=sentinel;
            size-=1;
            return removeElem;
        }
    }

    public T removeLast(){
        if(this.isEmpty()){
            return null;
        }
        else{
            T removeElem;
            Node<T> curNode = sentinel.pri;
            removeElem = sentinel.pri.elem;
            sentinel.pri = curNode.pri;
            curNode.pri.next = sentinel;
            size-=1;
            return removeElem;
        }
    }

    public T get(int index){
        Node<T> curNode = sentinel;
        for(;index>0;index--){
            if(curNode.next==null){
                return null;
            }
            curNode = curNode.next;
        }
        return curNode.elem;
    }
}

