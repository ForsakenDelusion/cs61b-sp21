public class Main {
    static <T> T yell (T t){
        return t;
    }
    public void main(String[] args) {
        lambda test3 = (c) -> {
            String a = "hello im lambda verible";
            System.out.println("hello i am lambda");
            System.out.println(a);
            return  c;
        };
        test3.testLambda(2);
        System.out.println(test3.testLambda(3));

        System.out.println(yell("yell"));
        System.out.println(yell("205"));
        class A implements test2<Integer>{

            @Override
            public Integer test2() {
                return null;
            }
        }

        socre<? super Object> s1 = new socre<>(90,"Math","优秀");
        Object score = s1.socre;
        socre<? extends Number> s2= new socre<>(80, "ENG", 90);
        Integer integer = (Integer)s2.getValue();
        socre<? super Number> s3 = new socre<>(12,"Chinese",23);
        s3.setValue(23.3);
        System.out.println(s3.getValue());

        B b1 = new B();
        B b = new B();
        String i = b.test("233");


    }
    class B extends A<String>{

        @Override
        public String test(String s) {
            return null;
        }
    }
}
