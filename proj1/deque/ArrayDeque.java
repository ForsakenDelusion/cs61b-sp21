package deque;

import afu.org.checkerframework.checker.oigj.qual.O;

public class ArrayDeque<T> {
    int size;
    T[] array;
    public ArrayDeque(){
        array = (T [])new Object[8];
        size = 0;
    }

    public void cleaner(){
        if((double) size /array.length<0.25){
            T[] cleanerArray = (T []) new Object[array.length-size];
            System.arraycopy(cleanerArray,0,array,size,cleanerArray.length);
        }
    }

    public void addFirst(T item){
        if(size>=array.length){
            resize();
        }
        T[] curArray = array;//这里是防止curArray数组的位置不够,所以直接令他等于刚刚扩充之后的array
        System.arraycopy(array, 0, curArray, 1, size);//实际上就是把元素向后移的操作
        curArray[0] = item;
        size+=1;
    }

    public void addLast(T item){
        if(isEmpty()){
            addFirst(item);//省懒
        }
        else{
            if(size>=array.length){
                resize();
            }
            T[] tempArray = array;
            System.arraycopy(array, 0, tempArray, 0, size-1);
            tempArray[size] = item;
            size+=1;
        }

    }

    public boolean isEmpty(){
        return size()==0;
    }

    public int size(){
        return size;
    }

    public void printDeque(){
        int curIndex = 0;
        for(;curIndex<size;curIndex++) {
            System.out.print(array[curIndex] + " ");
        }
        System.out.println();
    }

    public T removeFirst(){
        if(isEmpty()){
            return null;
        }
        T elem = array[0];
        System.arraycopy(array,1,array,0,size-1);
        size-=1;
        cleaner();
        return elem;
    }

    public T removeLast(){
        if(isEmpty()){
            return null;
        }
        T elem =array[size-1];
        size-=1;
        cleaner();
        return elem;
    }

    public T get(int index){
        if(array[index]!=null){
            return array[index];
        }else{
            return null;
        }
    }


    public void resize(){
         T[] newArray = (T[]) new Object[array.length*2];
         System.arraycopy(array,0,newArray,0,size);
         array = newArray;
    }

    public ArrayDeque(ArrayDeque other){

    }

}
