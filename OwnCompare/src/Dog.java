import java.util.Comparator;

public class Dog implements Comparable<Dog> {
    String name;
    int size;

    public Dog(String name,int size){
        this.name = name;
        this.size = size;
    }

    public static void main(String[] args) {

    }

    public void bark(){
        System.out.println(this.name+"bark");
    }
//    @Override
//    public int compara(Object o) {
//        Dog uddaDog = (Dog) o;
//        if (this.size < uddaDog.size) {
//            return -1;
//        } else if (this.size == uddaDog.size) {
//            return 0;
//        }
//        return 1;
//    }

    @Override
    public int compareTo(Dog uddaDog) {
        return this.size - uddaDog.size;
    }

    private static class NameComparator implements Comparator<Dog> {
        public int compare(Dog a, Dog b) {
            return a.name.compareTo(b.name);
        }
    }
}
