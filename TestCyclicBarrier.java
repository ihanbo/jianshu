package com.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestCyclicBarrier {

    public static void main(String[] args){
        new Race().start();
    }
    static class Race {
        ExecutorService executor = Executors.newFixedThreadPool(8);
        private CyclicBarrier cyclicBarrier = new CyclicBarrier(8, new Runnable() {
            int i = 1;
            @Override
            public void run() {
                // cyclicBarrier.reset();
                System.out.println("第"+i+"轮结束！！");
                i++;
            }
        });
        public void start() {
            List<Test2.Athlete> athleteList = new ArrayList<>();
            athleteList.add(new Test2.Athlete(cyclicBarrier,"博尔特"));
            athleteList.add(new Test2.Athlete(cyclicBarrier,"鲍威尔"));
            athleteList.add(new Test2.Athlete(cyclicBarrier,"盖伊"));
            athleteList.add(new Test2.Athlete(cyclicBarrier,"布雷克"));
            athleteList.add(new Test2.Athlete(cyclicBarrier,"加特林"));
            athleteList.add(new Test2.Athlete(cyclicBarrier,"苏炳添"));
            athleteList.add(new Test2.Athlete(cyclicBarrier,"路人甲"));
            athleteList.add(new Test2.Athlete(cyclicBarrier,"路人乙"));

            athleteList.add(new Test2.Athlete(cyclicBarrier,"博尔特1"));
            athleteList.add(new Test2.Athlete(cyclicBarrier,"鲍威尔1"));
            athleteList.add(new Test2.Athlete(cyclicBarrier,"盖伊1"));
            athleteList.add(new Test2.Athlete(cyclicBarrier,"布雷克1"));
            athleteList.add(new Test2.Athlete(cyclicBarrier,"加特林1"));
            athleteList.add(new Test2.Athlete(cyclicBarrier,"苏炳添1"));
            athleteList.add(new Test2.Athlete(cyclicBarrier,"路人甲1"));
            athleteList.add(new Test2.Athlete(cyclicBarrier,"路人乙1"));

            athleteList.add(new Test2.Athlete(cyclicBarrier,"博尔特2"));
            athleteList.add(new Test2.Athlete(cyclicBarrier,"鲍威尔2"));
            athleteList.add(new Test2.Athlete(cyclicBarrier,"盖伊2"));
            athleteList.add(new Test2.Athlete(cyclicBarrier,"布雷克2"));
            athleteList.add(new Test2.Athlete(cyclicBarrier,"加特林2"));
            athleteList.add(new Test2.Athlete(cyclicBarrier,"苏炳添2"));
            athleteList.add(new Test2.Athlete(cyclicBarrier,"路人甲2"));
            athleteList.add(new Test2.Athlete(cyclicBarrier,"路人乙2"));

            for (Test2.Athlete athlete : athleteList) {
                executor.execute(athlete);
            }
        }
    }
    //运动员
    static class Sporter implements Runnable {

        private CyclicBarrier cyclicBarrier;
        private String name;

        public Sporter(CyclicBarrier cyclicBarrier, String name) {
            this.cyclicBarrier = cyclicBarrier;
            this.name = name;
        }

        @Override
        public void run() {
            System.out.println(name + "就位");
            try {
                cyclicBarrier.await();
                Random random =new Random();
                double time = random.nextDouble() + 9;
                System.out.println(name + ": "+ time);
            } catch (Exception e) {
            }
        }
    }
}
