public class socre<E> {
    int num;
    String name;
    E socre;
    public socre(int num,String name1,E socre){
        this.num=num;
        name=name1;
        this.socre = socre;
    }

    public E getValue(){
        return socre;
    }

    public void setValue(E socre){
        this.socre = socre;

    }


}
