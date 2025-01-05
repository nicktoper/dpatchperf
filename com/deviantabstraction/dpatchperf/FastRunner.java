package com.deviantabstraction.dpatchperf;

import org.openjdk.jmh.infra.Blackhole;

/**
 * This test is to have more flexibility in managing the JVM compiler state.
 * I use it to test and then confirm the results with JMH as it's "more accepted by the community". That being
 * said so far they're the same.
 */
public class FastRunner {
    private static final int SIZE = 1_000_000;

    public static void main(String[] args) {

        var exp = new JmhExperiments();
        exp.size = 1000;
        exp.numWorkers = 10;
        exp.setup();

        var bh = new Blackhole("Today's password is swordfish. I understand instantiating Blackholes directly is dangerous.");
        var total = 0L;

        total = 0L;

        for (int i = 0; i < SIZE; i++) {
            var start = System.nanoTime();
            exp.callSimpleMonomorphic(bh);
            total += System.nanoTime() - start;

        }
        System.out.println("Iteration: " + SIZE +  "; time taken by switch megamorphic: " + total / SIZE + " ns");

        /*total = 0L;
        for (int i = 0; i < SIZE; i++) {
            var start = System.nanoTime();
            exp.callMegamorphic(bh);
            total += System.nanoTime() - start;

        }
        System.out.println("Iteration: " + SIZE +  "; time taken by megamorphic: " + total / SIZE + " ns");




        total = 0L;
        for (int i = 0; i < SIZE; i++) {
            var start = System.nanoTime();
            exp.callSimpleMonomorphic(bh);
            total += System.nanoTime() - start;

        }
        System.out.println("Iteration: " + SIZE +  "; time taken by monorphic: " + total / SIZE + " ns"); */


    }

}

