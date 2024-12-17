package com.deviantabstraction.dpatchperf;

public class FastRunner {
    public static void main(String[] args) {

        Experiments ip = new Experiments();
        //ip.testAddToListWithLinkedList();
        //ip.testAddToListWithLinkedListAndArrayList();
        ip.testAddToArrayList();
        //ip.testAddToLinkedList();
        ip.testUnpredictable();
    }
}

