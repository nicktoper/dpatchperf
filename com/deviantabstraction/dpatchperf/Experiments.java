package com.deviantabstraction.dpatchperf;

import org.openjdk.jmh.annotations.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(5)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class Experiments {


    //Play with size to check the various settings in JIT
    private static final int SIZE = 1_000_000;
    Random random = new Random();

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void testAddToLinkedList() {
        long total = 0;
        for (int i = 0; i < SIZE; i++) {
            LinkedList<Integer> list = new LinkedList<>();
            long startTime = System.nanoTime();
            list.add(2);
            total += System.nanoTime() - startTime;

        }
        //return list;
        System.out.println("Time taken by testAddToLinkedList: " + total / SIZE + " ns");
    }


    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void testAddToArrayList() {
        long total = 0;
        for (int i = 0; i < SIZE; i++) {
            ArrayList<Integer> list = new ArrayList<>();
            long startTime = System.nanoTime();
            list.add(2);
            total += System.nanoTime() - startTime;

        }
        //return list;
        System.out.println("Time taken by testAddToArrayList: " + total / SIZE + " ns");
    }


    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void testAddToListWithLinkedList() {
        long total = 0;

        for (int i = 0; i < SIZE; i++) {
            List<Integer> list = new LinkedList<>();
            long startTime = System.nanoTime();
            list.add(5);
            total += System.nanoTime() - startTime;

        }
        System.out.println("Time taken by testAddToListWithLinkedList: " + total / SIZE + " ns");
        //return list;

    }


    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void testPredictableAddToListWithLinkedListAndArrayList() {
        List<Integer> list;
        long total = 0;
        var bound = random.nextInt(2);

        for (int j = 0; j < SIZE; j++) {
            if (bound % 2 == 0) {
                list = new ArrayList<>();
            } else {
                list = new ArrayList<>();
            }
            long startTime = System.nanoTime();

            list.add(2);
            total += System.nanoTime() - startTime;
        }


        System.out.println("Time taken by predictable test: " + total / SIZE + " ns");
        //return list;

    }


    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void testUnpredictable() {
        List<Integer> list;
        long total = 0;


        for (int j = 0; j < SIZE; j++) {
            var bound = random.nextInt(2);
            if (bound % 2 == 0) {
                list = new ArrayList<>();
            } else {
                list = new ArrayList<>();
            }
            long startTime = System.nanoTime();

            list.add(2);
            total += System.nanoTime() - startTime;
        }


        System.out.println("Time taken by unpredictable test (50-50): " + total / SIZE + " ns");
        //return list;

    }

}
