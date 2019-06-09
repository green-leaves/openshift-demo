package com.greenleaves;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;

public class RxJavaTest {


    @Test
    public void testThrottleLast() throws InterruptedException {
        List<String> ccys = Arrays.asList("USD", "CAD", "SGD", "JPY");
        ExecutorService executorService = Executors.newCachedThreadPool();

        Map<String, BehaviorSubject<String>> subjects = new HashMap<>();
        for (String ccy : ccys) {
            BehaviorSubject<String> subject = BehaviorSubject.create();
            subjects.put(ccy, subject);
        }


        for (int i = 0; i < 5; i++) {
            Thread publishingThread = new Thread(() -> {
                while (true) {
                    try {
                        String ccy = ccys.get(new Random().nextInt(4));
                        subjects.get(ccy).onNext(String.format("Publishing %s at: %s", ccy, LocalTime.now()));
                        sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            executorService.execute(publishingThread);
        }


        List<Observable<String>> observables = subjects.values().stream()
                .map(s -> s.throttleLast(1000, TimeUnit.MILLISECONDS))
                .collect(Collectors.toList())
        ;

        Observable.merge(observables)
                .subscribe(System.out::println);


        Thread.sleep(15000);
    }

    @Test
    public void testObserver() {

        PublishSubject<Object> subject = PublishSubject.create();
        Observer<Object> observer = new Observer<Object>() {
            @Override
            public void onSubscribe(Disposable disposable) {

            }

            @Override
            public void onNext(Object o) {
                System.out.println("observer: " + o);
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onComplete() {

            }
        };
        // observer1 will receive all onNext and onComplete events
        subject.subscribe(observer);
        subject.onNext("one");
        subject.onNext("two");
        // observer2 will only receive "three" and onComplete
        subject.subscribe(observer);
        subject.onNext("three");
        subject.onComplete();

    }

    @Test
    public void testBinarySearch() {

        List<LocalDate> dates = new ArrayList<>();
        dates.add(LocalDate.parse("2019-06-01"));
        dates.add(LocalDate.parse("2019-06-03"));
        dates.add(LocalDate.parse("2019-06-11"));
        dates.add(LocalDate.parse("2019-07-03"));
        dates.add(LocalDate.parse("2019-09-03"));
        dates.add(LocalDate.parse("2019-11-03"));
        dates.add(LocalDate.parse("2020-06-01"));
        int index = Collections.binarySearch(dates, LocalDate.parse("2019-06-02"));
        Pair<LocalDate, LocalDate> pair = Pair.of(dates.get(Math.abs(index) - 2), dates.get(Math.abs(index) - 1));
        System.out.println(pair);

    }

    @Test
    public void testCombineStream() throws InterruptedException {
        BehaviorSubject<Integer> oddStream = BehaviorSubject.create();
        BehaviorSubject<Integer> evenStream = BehaviorSubject.create();
        ExecutorService executorService = Executors.newCachedThreadPool();

        Thread oddThread = new Thread(() -> {
            int i = 1;
            while (true) {
                try {
                    oddStream.onNext(i);
                    if (i % 3 == 0) {
                        Thread.sleep(2000);
                    } else {
                        Thread.sleep(1000);
                    }
                    i = i + 2;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread evenThread = new Thread(() -> {
            int i = 0;
            while (true) {
                try {
                    evenStream.onNext(i);
                    if (i != 0 && i % 4 == 0) {
                        Thread.sleep(4000);
                    } else {
                        Thread.sleep(1000);
                    }
                    i = i + 2;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        executorService.execute(oddThread);
        executorService.execute(evenThread);
        BiFunction<Integer, Integer, String> formatFunction = (x, y) -> String.format("%s . %s", x, y);

//        evenStream
//                .zipWith(oddStream, formatFunction::apply)
//                .subscribe(System.out::println);

//        evenStream
//                .withLatestFrom(oddStream, formatFunction::apply)
//                .subscribe(System.out::println);

        Observable
                .combineLatest(evenStream, oddStream, formatFunction::apply)
                .throttleLast(1, TimeUnit.SECONDS)
                .subscribe(s -> System.out.println(String.format("%s: %s", LocalTime.now(), s)));

        Thread.sleep(30000);
    }

    static BiFunction<Integer, Integer, String> format() {
        return (x, y) -> String.format("%s . %s", x, y);
    }

}
