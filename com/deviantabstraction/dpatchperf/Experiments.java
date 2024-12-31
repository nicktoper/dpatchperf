package com.deviantabstraction.dpatchperf;

import org.openjdk.jmh.annotations.*;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Old experiment. I didn't use it because Reddit smart people told me to use JMH.
 * That being said they're in line with JMH results.
 */
public class Experiments {


    //Play with size to check the various settings in JIT
    private static final int SIZE = 1_000_000;
    Random random = new Random();

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

    public void callComplexSite() {
        List<Integer> list;
        long total = 0;


        for (int j = 0; j < SIZE; j++) {
            var bound = random.nextInt(10);
            if (bound % 5 == 0) {
                list = new LinkedList<>();
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
