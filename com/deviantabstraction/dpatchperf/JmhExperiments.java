package com.deviantabstraction.dpatchperf;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Demonstrates:
 *  - A sealed interface Worker with many final implementations
 *  - A monomorphic call site (only 1 Worker)
 *  - A polymorphic/megamorphic call site (N distinct Workers)
 *  - A pattern-switch version (Java 17+ or 21+), showing sealed switch dispatch
 *  - Param for how many Worker classes to create, and how big the loop is
 *
 *  Adjust the @Param values to see how overhead scales with #Workers and loop size.
 *  "size" = how many calls per benchmark iteration
 *  "numWorkers" = how many distinct Worker classes to use
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Thread)
public class JmhExperiments {

    /**
     * Number of loop iterations we do per benchmark method call (batching).
     * Try "100", "1000", "10000" etc.
     */
    @Param({"15000"})
    int size;

    /**
     * Number of distinct Worker classes we actually instantiate
     * (from 1 up to 10 or 20).
     * E.g. @Param({"1","5","10"}) to see monomorphic -> megamorphic.
     */
    @Param({"1","5","10"})
    int numWorkers;

    /**
     * We'll hold references to Worker classes in this array.
     * If numWorkers=1, we effectively have a monomorphic scenario (array repeats Worker0).
     * If numWorkers=5 or 10, we have multiple different Worker classes => more polymorphism.
     */
    Worker[] workers;

    /**
     * We'll also store a single Worker for the truly "monomorphic" benchmark.
     */
    Worker0 monoWorker;

    /**
     * Random for shuffling which workers get used in the polymorphic scenario.
     */
    Random random;

    //Added by compiler
    // Create arrays for each worker type
    private int[] nextFreeSlot = new int[10];
    Worker0[] worker0s = new Worker0[size];
    Worker1[] worker1s = new Worker1[size];
    Worker2[] worker2s = new Worker2[size];
    Worker3[] worker3s = new Worker3[size];
    Worker4[] worker4s = new Worker4[size];
    Worker5[] worker5s = new Worker5[size];
    Worker6[] worker6s = new Worker6[size];
    Worker7[] worker7s = new Worker7[size];
    Worker8[] worker8s = new Worker8[size];
    Worker9[] worker9s = new Worker9[size];

    // ------------------------------------------------------------------------------------
    // 1) Define a sealed interface Worker with final classes  (Java 17+)
    //    If you're on Java <17, remove 'sealed' & 'permits' and just make them normal classes.
    // ------------------------------------------------------------------------------------

    sealed interface Worker permits Worker0, Worker1, Worker2, Worker3, Worker4,
            Worker5, Worker6, Worker7, Worker8, Worker9 {
        int doWork(int x);

    }

    public static final class Worker0 implements Worker {
        @Override
        public int doWork(int x) {
            // Some arithmetic
            int sum = 0;
            for (int i = 0; i < 5; i++) {
                sum += (x + i) * (x - i + 2);
            }
            return sum;
        }
    }

    public static final class Worker1 implements Worker {
        @Override
        public int doWork(int x) {
            // Some arithmetic
            int sum = 0;
            for (int i = 0; i < 5; i++) {
                sum += (x + i) * (x - i + 2);
            }
            return sum;

        }
    }

    public static final class Worker2 implements Worker {
        @Override
        public int doWork(int x) {
            // Some arithmetic
            int sum = 0;
            for (int i = 0; i < 5; i++) {
                sum += (x + i) * (x - i + 2);
            }
            return sum;
        }
    }

    public static final class Worker3 implements Worker {
        @Override
        public int doWork(int x) {
            // Some arithmetic
            int sum = 0;
            for (int i = 0; i < 5; i++) {
                sum += (x + i) * (x - i + 2);
            }
            return sum;
        }
    }

    public static final class Worker4 implements Worker {
        @Override
        public int doWork(int x) {
            // Some arithmetic
            int sum = 0;
            for (int i = 0; i < 5; i++) {
                sum += (x + i) * (x - i + 2);
            }
            return sum;
        }
    }

    public static final class Worker5 implements Worker {
        @Override
        public int doWork(int x) {
            // Some arithmetic
            int sum = 0;
            for (int i = 0; i < 5; i++) {
                sum += (x + i) * (x - i + 2);
            }
            return sum;
        }
    }

    public static final class Worker6 implements Worker {
        @Override
        public int doWork(int x) {
            // Some arithmetic
            int sum = 0;
            for (int i = 0; i < 5; i++) {
                sum += (x + i) * (x - i + 2);
            }
            return sum;
        }
    }

    public static final class Worker7 implements Worker {
        @Override
        public int doWork(int x) {
            // Some arithmetic
            int sum = 0;
            for (int i = 0; i < 5; i++) {
                sum += (x + i) * (x - i + 2);
            }
            return sum;
        }
    }

    public static final class Worker8 implements Worker {
        @Override
        public int doWork(int x) {
            // Some arithmetic
            int sum = 0;
            for (int i = 0; i < 5; i++) {
                sum += (x + i) * (x - i + 2);
            }
            return sum;
        }
    }

    public static final class Worker9 implements Worker {
        @Override
        public int doWork(int x) {
            // Some arithmetic
            int sum = 0;
            for (int i = 0; i < 5; i++) {
                sum += (x + i) * (x - i + 2);
            }
            return sum;
        }
    }

    // ------------------------------------------------------------------------------------
    // 2) Setup: create an array with 'numWorkers' distinct classes (up to 10),
    //           fill the rest by repeating the last worker if numWorkers < 10
    // ------------------------------------------------------------------------------------
    @Setup
    public void setup() {
        random = new Random(42);

        // We'll store references to up to 10 distinct worker classes.
        // If numWorkers=1, we only use Worker0; if numWorkers=5, we use Worker0..Worker4, etc.
        Worker[] allPossible = new Worker[]{
                new Worker0(), new Worker1(), new Worker2(), new Worker3(), new Worker4(),
                new Worker5(), new Worker6(), new Worker7(), new Worker8(), new Worker9()
        };
        nextFreeSlot = new int[10];  // fixed size 10 for all worker types
        worker0s = new Worker0[size];
        worker1s = new Worker1[size];
        worker2s = new Worker2[size];
        worker3s = new Worker3[size];
        worker4s = new Worker4[size];
        worker5s = new Worker5[size];
        worker6s = new Worker6[size];
        worker7s = new Worker7[size];
        worker8s = new Worker8[size];
        worker9s = new Worker9[size];




        // Create an array the size of "size" (the loop count),
        // so each iteration in the benchmark can pick from it.
        workers = new Worker[size];

        // We'll fill 'workers' with whatever the first N of 'allPossible' are
        // and repeat them in a round-robin if needed.
        for (int i = 0; i < size; i++) {
            // Which worker index for this slot?
            int idx = i % numWorkers;  // e.g., if numWorkers=5, this cycles 0..4
            Worker worker = allPossible[idx];
            workers[i] = worker;

            //ADDEED
            if (worker instanceof Worker0) worker0s[nextFreeSlot[0]++] = (Worker0) worker;
            else if (worker instanceof Worker1) worker1s[nextFreeSlot[1]++] = (Worker1) worker;
            else if (worker instanceof Worker2) worker2s[nextFreeSlot[2]++] = (Worker2) worker;
            else if (worker instanceof Worker3) worker3s[nextFreeSlot[3]++] = (Worker3) worker;
            else if (worker instanceof Worker4) worker4s[nextFreeSlot[4]++] = (Worker4) worker;
            else if (worker instanceof Worker5) worker5s[nextFreeSlot[5]++] = (Worker5) worker;
            else if (worker instanceof Worker6) worker6s[nextFreeSlot[6]++] = (Worker6) worker;
            else if (worker instanceof Worker7) worker7s[nextFreeSlot[7]++] = (Worker7) worker;
            else if (worker instanceof Worker8) worker8s[nextFreeSlot[8]++] = (Worker8) worker;
            else if (worker instanceof Worker9) worker9s[nextFreeSlot[9]++] = (Worker9) worker;
        }



        // For the monomorphic scenario, we just pick Worker0 (or the first one).
        monoWorker = new Worker0();
    }

    // ------------------------------------------------------------------------------------
    // 3) The Benchmarks
    // ------------------------------------------------------------------------------------

    /**
     * A purely monomorphic call site: only 1 worker type (Worker0).
     * The JIT can inline heavily.
     */
    @Benchmark
    public void callDirect(Blackhole bh) {
        Worker0 w = monoWorker;
        for (int i = 0; i < size; i++) {
            bh.consume(w.doWork(i));
        }
    }

    /**
     * A polymorphic/megamorphic call site:
     * We shuffle among 'numWorkers' distinct Worker classes in the 'workers' array.
     */
    @Benchmark
    public void callMegamorphic(Blackhole bh) {
        for (int i = 0; i < size; i++) {
            // picks among up to 10 distinct Workers
            Worker w = workers[i];
            bh.consume(w.doWork(i));
        }
    }

    /**
     * A "pattern switch" version (Java 17/21+ with sealed classes) that
     * attempts to manually dispatch among multiple Worker classes.
     * The JIT *might* compile it into a switch table.
     */
    @Benchmark
    public void callOptim(Blackhole bh) {
        for (int i = 0; i < nextFreeSlot[0]; i++) bh.consume(worker0s[i].doWork(i));
        for (int i = 0; i < nextFreeSlot[1]; i++) bh.consume(worker1s[i].doWork(i));
        for (int i = 0; i < nextFreeSlot[2]; i++) bh.consume(worker2s[i].doWork(i));
        for (int i = 0; i < nextFreeSlot[3]; i++) bh.consume(worker3s[i].doWork(i));
        for (int i = 0; i < nextFreeSlot[4]; i++) bh.consume(worker4s[i].doWork(i));
        for (int i = 0; i < nextFreeSlot[5]; i++) bh.consume(worker5s[i].doWork(i));
        for (int i = 0; i < nextFreeSlot[6]; i++) bh.consume(worker6s[i].doWork(i));
        for (int i = 0; i < nextFreeSlot[7]; i++) bh.consume(worker7s[i].doWork(i));
        for (int i = 0; i < nextFreeSlot[8]; i++) bh.consume(worker8s[i].doWork(i));
        for (int i = 0; i < nextFreeSlot[9]; i++) bh.consume(worker9s[i].doWork(i));
    }
}
