package reactive.lec1;

import java.util.Iterator;

/**
 * 쌍대성 (Duality)
 * <p>
 * Iterable          <---> Observable
 * Pull              <---> Push
 * DATA method(void) <---> void method(DATA)
 */
public class IterableObj {

    public static void main(String[] args) {
        Iterable<Integer> iter = () -> new Iterator<Integer>() {
            int i = 0;
            final static int MAX = 10;

            @Override
            public boolean hasNext() {
                return i < MAX;
            }

            @Override
            public Integer next() {
                return ++i;
            }
        };

        for (Integer i : iter) {
            System.out.println(i);
        }

        for (Iterator<Integer> it = iter.iterator(); it.hasNext(); ) {
            System.out.println(it.next()); // pull
        }
    }

}
