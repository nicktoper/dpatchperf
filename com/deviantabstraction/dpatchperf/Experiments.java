package com.deviantabstraction.dpatchperf;

import org.openjdk.jmh.annotations.*;

import java.util.*;
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
    public void callSimpleSite() {
        long total = 0;
        for (int i = 0; i < SIZE; i++) {
            ArrayList<Integer> list = new ArrayList<>();
            long startTime = System.nanoTime();
            list.add(2);
            total += System.nanoTime() - startTime;

        }
        //return list;
        System.out.println("Time taken by simple site: " + total / SIZE + " ns");
    }


    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void callComplexSite() {
        List<Integer> list;
        long total = 0;


        for (int j = 0; j < SIZE; j++) {
            var bound = random.nextInt(10);
            if (bound %5 == 0) {
                list = new ArrayList<>();
            } else {
                list = new ArrayList<>();
            }
            long startTime = System.nanoTime();

            list.add(2);
            total += System.nanoTime() - startTime;
        }

        System.out.println("Time taken by complex site: " + total / SIZE + " ns");

    }


}
