public class Max {
    public static OurCompare max(OurCompare[] items) {
        int maxDex = 0;
        for (int i = 0; i < items.length; i += 1) {
            int cmp = items[i].compare(items[maxDex]);
            if (cmp > 0) {
                maxDex = i;
            }
        }
        System.out.println(items.getClass());
        return items[maxDex];
    }

}
