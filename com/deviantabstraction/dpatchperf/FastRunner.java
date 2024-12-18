package com.deviantabstraction.dpatchperf;

public class FastRunner {
    public static void main(String[] args) {

        Experiments ip = new Experiments();
        ip.callSimpleSite();
        ip.callComplexSite();
    }
}

