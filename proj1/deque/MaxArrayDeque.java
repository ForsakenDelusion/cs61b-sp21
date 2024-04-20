package deque;
import java.util.Comparator;
public class MaxArrayDeque<T> extends ArrayDeque<T>{
    private Comparator<T> cmp;//定义MaxArrayDeque的Comparator为cmp
    public MaxArrayDeque (Comparator<T> c){
        super();
        this.cmp = c;//这样就是让MaxArrayDeque的Comparator等于外面传进来的c
    }


    public T max(){
        return max(cmp);
    }

    public T max(Comparator<T> c){
        if(this.isEmpty()){
            return null;
        }

        int index = 0;
        for (int count = 0; count < this.size(); count++) {
            if(cmp.compare(get(index),get(count))<0){
                index = count;
            }

        }

        return get(index);
    }

}
