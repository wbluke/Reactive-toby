package reactive.lec1;

import java.util.Observable;
import java.util.Observer;

/**
 * 쌍대성 (Duality)
 * <p>
 * Iterable          <---> Observable
 * Pull              <---> Push
 * DATA method(void) <---> void method(DATA)
 */
public class ObservableObj {

    static class IntObservable extends Observable implements Runnable {
        @Override
        public void run() {
            for (int i = 1; i <= 10; i++) {
                setChanged();
                notifyObservers(i); // push
            }
        }
    }

    public static void main(String[] args) {
        Observer observer = new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                System.out.println(arg);
            }
        };

        IntObservable intObservable = new IntObservable();
        intObservable.addObserver(observer);

        intObservable.run();
    }

}
