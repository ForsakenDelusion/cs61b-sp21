

public class testanimal {
    public static void main(String[] args) {
        Animal animal = new cat();
        cat cat = (cat) animal;


        cat.ls();
        cat.hi();

        Animal sleepcat = new cat();
        Animal sleepanimal = new Animal();
        cat catsleep = new cat();
        sleepanimal.sleep();
        sleepcat.sleep();
        catsleep.sleep();



        cat.ls();
        animal.ls();
    }




}
