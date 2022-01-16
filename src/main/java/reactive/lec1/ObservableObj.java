package reactive.lec1;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
                System.out.println(Thread.currentThread().getName() + " " + arg); // pool-1-thread-1 i
            }
        };

        IntObservable intObservable = new IntObservable();
        intObservable.addObserver(observer);

        // push 방식은 별도의 스레드에서 동작하도록 구성하기 용이하다.
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(intObservable);

        System.out.println(Thread.currentThread().getName() + " EXIT"); // main EXIT
        executorService.shutdown();
    }

}
