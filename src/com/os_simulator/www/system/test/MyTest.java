package com.os_simulator.www.system.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MyTest {
    public static void main(String[] args){
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i<10; i++){
            list.add(i);
        }

        Iterator iterator = list.iterator();
        iterator.next();
        while (iterator.hasNext()){
            System.out.println(iterator.next());
        }
    }
}
