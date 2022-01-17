package practice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;

import static java.time.temporal.ChronoUnit.SECONDS;

public class ReactorPracticeTest {

    /**
     * Mono : 데이터 0개 or 1개짜리 Publisher
     * Flux : 데이터 N개짜리 Publisher
     */
    @Test
    void mono() throws InterruptedException {
        // data source. not data.
        Mono<Integer> mono = Mono.just(1)
                .delayElement(Duration.of(1, SECONDS));
//        System.out.println(">>>>>>>>>>>>>>>>>> " + mono.block()); // 일단 모르겠고 blocking 호출
        mono.doOnNext(integer -> {
            try {
                Thread.sleep(500);
                System.out.println(">>>>>>>>>>>>>>>>>> " + integer);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        })
                .subscribe();

        Flux<Integer> integerFlux = Flux.fromIterable(List.of(1, 2, 3, 4, 5));
        integerFlux.doOnNext(integer -> {
            try {
                Thread.sleep(500);
                System.out.println(">>>>>>>>>>>>>>>>>> " + integer);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        })
                .subscribe();

        Thread.sleep(2000);
    }

    @Test
    void verifyMonoComplete() {
        Mono<Integer> mono = Mono.just(1)
                .delayElement(Duration.of(1, SECONDS));

        StepVerifier.create(mono)
                .expectNext(1)
                .verifyComplete();
    }

    @DisplayName("")
    @Test
    void verifyMonoError() {
        Mono<Integer> mono = Mono.error(new IllegalArgumentException("으악!"));

        StepVerifier.create(mono)
                .verifyError(IllegalArgumentException.class);
    }

}
