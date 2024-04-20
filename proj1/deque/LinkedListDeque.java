package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    @Override
    public Iterator<T> iterator() {
        return null;
    }

    private class Node<T> {
        T elem;
        Node<T> pri;
        Node<T> next;

        public Node(T elem, Node<T> pri ,Node<T> next) {
            this.elem = elem;
            this.pri = pri;
            this.next = next;
        }
    }

    private Node<T> sentinel;
    private int size;
    public LinkedListDeque() {
        sentinel = new Node<>(null, null, null);
        sentinel.next = sentinel;
        sentinel.pri = sentinel;
        size = 0;
    }
    @Override
    public void addFirst(T item) {
        if (this.isEmpty()) {
            sentinel.next = new Node<>(item, sentinel, sentinel.pri);
            sentinel.pri = sentinel.next;
            sentinel.next.next = sentinel;
        } else {
            Node<T> curNode = sentinel.next;
            sentinel.next = new Node<>(item, sentinel, curNode);
            curNode.pri = sentinel.next;
        }
        size += 1;
    }
    @Override
    public void addLast(T item){
        if(this.isEmpty()) {
            sentinel.next = new Node<>(item, sentinel, sentinel.pri);
            sentinel.pri = sentinel.next;
            sentinel.next.next=sentinel;
        } else {
            Node<T> curNode = sentinel.pri;
            curNode.next = new Node<>(item, curNode, sentinel);
            sentinel.pri = curNode.next;
        }
        size += 1;
    }
    @Override
    public boolean isEmpty() {
        return size()==0;
    }
    @Override
    public int size(){
        return size;
    }
    @Override
    public void printDeque() {
        int count = size;
        Node<T> curNode = sentinel.next;
        for (;count > 0;count--){
            System.out.print(curNode.elem+" ");
            curNode = curNode.next;
        }
        System.out.println();
    }
    @Override
    public T removeFirst(){
        if (this.isEmpty()) {
            return null;
        } else{
            T removeElem;
            removeElem = sentinel.next.elem;
            sentinel.next = sentinel.next.next;
            sentinel.next.pri = sentinel;
            size -= 1;
            return removeElem;
        }
    }
    @Override
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
    @Override
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



    public LinkedListDeque(LinkedListDeque other){
        sentinel = new Node<>(null,null,null);
        sentinel.next = sentinel;
        sentinel.pri = sentinel;

        for(int i = 0;i < other.size();i++){
            addLast((T)other.get(i));
        }
    }

    public T getRecursive(int index){
        int count = index+1;
        Node<T> curNode = sentinel;
        if(count==0){
            return curNode.elem;
        }
        else{
            count-=1;
            return this.get(count);
        }
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null) {
            return false;
        }

        if (!(o instanceof Deque)) {
            return false;
        }

        Deque<T> other = (Deque<T>) o;
        if (this.size() != other.size()) {
            return false;
        }

        for (int i = 0; i < size(); i++) {
            if (!this.get(i).equals(other.get(i))) {
                return false;
            }
        }
        return true;
    }
}

