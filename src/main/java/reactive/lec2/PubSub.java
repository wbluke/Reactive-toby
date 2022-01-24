package reactive.lec2;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Reactive Streams - Operators
 * <p>
 * Publisher -> [Data1] -> Operator1 -> [Data2] -> Operator2 -> [Data3] -> Subscriber
 * 1. map (d1 -> f -> d2)
 */
@Slf4j
public class PubSub {

    public static void main(String[] args) {
        Publisher<Integer> publisher = iterPublisher(
                Stream.iterate(1, a -> a + 1)
                        .limit(10)
                        .collect(Collectors.toList())
        );

        Publisher<Integer> mapPublisher = mapPublisher(publisher, i -> i * 10);
        Publisher<Integer> mapPublisher2 = mapPublisher(mapPublisher, i -> -i);

        Subscriber<Integer> subscriber = logSubscriber();

        mapPublisher2.subscribe(subscriber);
    }

    private static Publisher<Integer> mapPublisher(Publisher<Integer> publisher, Function<Integer, Integer> function) {
        return new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> subscriber) {
                subscriber.onSubscribe(new Subscription() {
                    @Override
                    public void request(long n) {
                        publisher.subscribe(new DelegateSubscriber(subscriber) {
                            @Override
                            public void onNext(Integer integer) {
                                subscriber.onNext(function.apply(integer));
                            }
                        });
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        };
    }

    private static Publisher<Integer> iterPublisher(final List<Integer> iter) {
        return new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> subscriber) {
                subscriber.onSubscribe(new Subscription() {
                    @Override
                    public void request(long n) {
                        try {
                            iter.forEach(subscriber::onNext);
                            subscriber.onComplete();
                        } catch (Throwable t) {
                            subscriber.onError(t);
                        }
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        };
    }

    private static Subscriber<Integer> logSubscriber() {
        Subscriber<Integer> subscriber = new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                log.debug("onSubscribe");
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer integer) {
                log.debug("onNext: {}", integer);
            }

            @Override
            public void onError(Throwable t) {
                log.debug("onError: {}", t.getMessage());
            }

            @Override
            public void onComplete() {
                log.debug("onComplete");
            }
        };
        return subscriber;
    }

}
