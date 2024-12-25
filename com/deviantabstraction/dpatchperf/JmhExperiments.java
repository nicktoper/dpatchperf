package com.deviantabstraction.dpatchperf;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

/**
 * Demonstrates a "simple" monomorphic case vs.
 * a "megamorphic" case with multiple Worker implementations.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Thread)
public class JmhExperiments {

    /**
     * Number of loop iterations we do per benchmark method call.
     * Adjust as needed to increase total measured time.
     */
    @Param({"1000"})
    int size;

    /**
     * "Megamorphic" array of Workers:
     * multiple distinct implementations so the call site sees many types.
     */
    Worker[] megamoWorkers;

    /**
     * A single Worker for the "simple" (monomorphic) scenario.
     */
    Worker simpleWorker;

    /**
     * This interface has multiple implementations below.
     */
    public interface Worker {
        int doWork();
    }

    public static class WorkerA implements Worker {
        public int doWork() { return 1; }
    }

    public static class WorkerB implements Worker {
        public int doWork() { return 2; }
    }

    public static class WorkerC implements Worker {
        public int doWork() { return 3; }
    }

    public static class WorkerD implements Worker {
        public int doWork() { return 4; }
    }

    public static class WorkerE implements Worker {
        public int doWork() { return 5; }
    }

    @Setup
    public void setup() {
        // Prepare a single worker for the simple/monomorphic call site
        simpleWorker = new WorkerA();

        // Prepare many worker implementations for the megamorphic call site
        megamoWorkers = new Worker[]{
                new WorkerA(), new WorkerB(),
                new WorkerC(), new WorkerD(),
                new WorkerE()
        };
    }

    /**
     * A "simple" call site: always the same implementation (WorkerA).
     * The JVM can easily inline this, making overhead near-zero.
     */
    @Benchmark
    public void callSimpleMonomorphic(Blackhole bh) {
        Worker w = simpleWorker;
        for (int i = 0; i < size; i++) {
            bh.consume(w.doWork());
        }
    }

    /**
     * A "megamorphic" call site: picks from 5 implementations in a round-robin.
     * The JVM can't just inline everything if it sees many distinct types often.
     */
    @Benchmark
    public void callMegamorphic(Blackhole bh) {
        // We'll cycle through the array, ensuring each iteration sees a different type
        for (int i = 0; i < size; i++) {
            Worker w = megamoWorkers[i % megamoWorkers.length];
            bh.consume(w.doWork());
        }
    }
}