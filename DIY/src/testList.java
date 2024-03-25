public class testList{
    public int first;
    public testList rest;
    public testList(int first,testList rest){
        this.first=first;
        this.rest=rest;
    }

    public int size(){
        if(rest==null){
            return 1;
        }
        return 1+this.rest.size();
    }

    public int getNum(int index){
        if(index==0){
            return this.first;
        }
        return this.rest.getNum(index - 1);
    }


    public static void main(String[] args) {
        testList list = new testList(1,null);
        list.rest = new testList(10,null);
        list.rest.rest = new testList(90,null);
        System.out.println(list.getNum(0));


    }


}