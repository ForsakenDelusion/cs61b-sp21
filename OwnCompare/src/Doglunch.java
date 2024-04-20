public class Doglunch{
    public static void main(String[] args) {
        Dog d1 = new Dog("jww",23);
        Dog d2 = new Dog("jm",88);
        Dog d3 = new Dog("hsq",8);
        Dog[] dogs = new Dog[]{d1,d2,d3};
        System.out.println(Max.max(dogs));
        Dog d=(Dog)Max.max(dogs);
        d.bark();
    }
}
