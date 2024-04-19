package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T>{
    private Comparator<T> comparator;

    // 构造函数，创建一个具有给定比较器的 MaxArrayDeque
    public MaxArrayDeque(Comparator<T> c) {
        super(); // 调用父类的无参构造函数
        this.comparator = c; // 初始化比较器
    }

    public T max(){
        public T max() {
            if (isEmpty()) {
                return null; // 如果队列为空，则返回null
            }

            // 初始化最大元素为队列的第一个元素
            T maxElement = get(0);

            // 从第二个元素开始遍历队列，找到比当前最大元素更大的元素
            for (int i = 1; i < size(); i++) {
                T current = get(i);
                // 使用比较器比较当前元素和最大元素
                if (comparator.compare(current, maxElement) > 0) {
                    maxElement = current; // 更新最大元素
                }
            }

            return maxElement; // 返回最大元素
        }
    };

    public T max(Comparator<T> c){

    };

}
