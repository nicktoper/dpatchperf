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
    @Param({"1000"})
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
    Worker monoWorker;

    /**
     * Random for shuffling which workers get used in the polymorphic scenario.
     */
    Random random;

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
            int prod = 1;
            for (int i = 1; i < 6; i++) {
                prod = prod ^ ((x + i) * 31);
            }
            return prod;
        }
    }

    public static final class Worker2 implements Worker {
        @Override
        public int doWork(int x) {
            return Integer.rotateLeft(x, (x & 7)) + 0xABCD;
        }
    }

    public static final class Worker3 implements Worker {
        @Override
        public int doWork(int x) {
            // simple fibonacci-ish
            int a=1,b=1;
            for(int i=0; i<4; i++){
                int tmp=a+b+x;
                a=b;b=tmp;
            }
            return b;
        }
    }

    public static final class Worker4 implements Worker {
        @Override
        public int doWork(int x) {
            return (x * 12345) ^ 0x55AA55AA;
        }
    }

    public static final class Worker5 implements Worker {
        @Override
        public int doWork(int x) {
            // Slightly different
            return (x+1)*(x+2)*(x+3);
        }
    }

    public static final class Worker6 implements Worker {
        @Override
        public int doWork(int x) {
            int sum = 0;
            for(int i=0;i<5;i++){
                sum += (x << i) - i;
            }
            return sum;
        }
    }

    public static final class Worker7 implements Worker {
        @Override
        public int doWork(int x) {
            // Some modulo arithmetic
            int r = x;
            for(int i=1;i<=5;i++){
                r = ((r + i)*7) % 999983;
            }
            return r;
        }
    }

    public static final class Worker8 implements Worker {
        @Override
        public int doWork(int x) {
            return x*(x-1)*(x+1);
        }
    }

    public static final class Worker9 implements Worker {
        @Override
        public int doWork(int x) {
            // Another small loop
            int accum = x;
            for(int i=0;i<5;i++){
                accum ^= (accum << 1);
            }
            return accum;
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

        // Create an array the size of "size" (the loop count),
        // so each iteration in the benchmark can pick from it.
        workers = new Worker[size];

        // We'll fill 'workers' with whatever the first N of 'allPossible' are
        // and repeat them in a round-robin if needed.
        for (int i = 0; i < size; i++) {
            // Which worker index for this slot?
            int idx = i % numWorkers;  // e.g., if numWorkers=5, this cycles 0..4
            workers[i] = allPossible[idx];
        }

        // For the monomorphic scenario, we just pick Worker0 (or the first one).
        monoWorker = allPossible[0];
    }

    // ------------------------------------------------------------------------------------
    // 3) The Benchmarks
    // ------------------------------------------------------------------------------------

    /**
     * A purely monomorphic call site: only 1 worker type (Worker0).
     * The JIT can inline heavily.
     */
    @Benchmark
    public void callSimpleMonomorphic(Blackhole bh) {
        Worker w = monoWorker;
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
    public void patternSwitchMegamorphic(Blackhole bh) {
        for (int i = 0; i < size; i++) {
            Worker w = workers[i];
            // slightly faster than switch/case

            if (w instanceof Worker0 a) {
                bh.consume(a.doWork(i));
            } else if (w instanceof Worker1 b) {
                bh.consume(b.doWork(i));
            } else if (w instanceof Worker2 c) {
                bh.consume(c.doWork(i));
            } else if (w instanceof Worker3 d) {
                bh.consume(d.doWork(i));
            } else if (w instanceof Worker4 e) {
                bh.consume(e.doWork(i));
            } else if (w instanceof Worker5 f) {
                bh.consume(f.doWork(i));
            } else if (w instanceof Worker6 g) {
                bh.consume(g.doWork(i));
            } else if (w instanceof Worker7 h) {
                bh.consume(h.doWork(i));
            } else if (w instanceof Worker8 j) {
                bh.consume(j.doWork(i));
            } else if (w instanceof Worker9 k) {
                bh.consume(k.doWork(i));
            } else {
                throw new IllegalStateException("Unknown worker type " + w.getClass());
            }
        }
    }
}
