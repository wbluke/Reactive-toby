package reactive.lec1;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.TimeUnit;

/**
 * Reactive streams 표준
 * https://www.reactive-streams.org/
 * <p>
 * - API : https://www.reactive-streams.org/reactive-streams-1.0.3-javadoc/org/reactivestreams/package-summary.html
 * - Specification : https://github.com/reactive-streams/reactive-streams-jvm/blob/v1.0.3/README.md#specification
 */
public class PubSub {

    /*
      In response to a call to `Publisher.subscribe(Subscriber)` the possible invocation sequences for methods
      on the Subscriber are given by the following protocol:

      `onSubscribe onNext* (onError | onComplete)?`
     */
    public static void main(String[] args) throws InterruptedException {
        // Publisher  <- Observable
        // Subscriber <- Observer

        Iterable<Integer> iter = Arrays.asList(1, 2, 3, 4, 5);
        ExecutorService executorService = Executors.newCachedThreadPool();

        Publisher publisher = new Publisher() {
            @Override
            public void subscribe(Subscriber subscriber) {
                Iterator<Integer> it = iter.iterator();

                subscriber.onSubscribe(new Subscription() {
                    @Override
                    public void request(long n) { // 역압 (Back Pressure)
                        executorService.execute(() -> {
                            int i = 0;
                            try {
                                while (i++ < n) {
                                    if (it.hasNext()) {
                                        subscriber.onNext(it.next());
                                    } else {
                                        subscriber.onComplete();
                                        break;
                                    }
                                }
                            } catch (RuntimeException e) {
                                subscriber.onError(e);
                            }

                        });
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        };

        Subscriber<Integer> subscriber = new Subscriber<Integer>() {
            Subscription subscription;

            @Override
            public void onSubscribe(Subscription subscription) {
                System.out.println("onSubscribe");
                this.subscription = subscription;
                this.subscription.request(1);
            }

            @Override
            public void onNext(Integer item) {
                System.out.println(Thread.currentThread().getName() + " onNext " + item);
                this.subscription.request(1);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("onError");
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        };

        publisher.subscribe(subscriber);

        executorService.awaitTermination(10, TimeUnit.SECONDS);
        executorService.shutdown();
    }

}
