public class implement extends Test {


    public String study() {
        System.out.println("hello");
        return null;
    }//用子类继承实现抽象方法

    public static void main(String[] args){
        //Test test1 = new Test();//没用子类继承实现
        implement test2 = new implement();
        test2.study();
        Test test3 = new Test() {
            @Override
            public String study() {
                System.out.println("hi,我是匿名内部类");
                return null;
            }
        };
    }












}
