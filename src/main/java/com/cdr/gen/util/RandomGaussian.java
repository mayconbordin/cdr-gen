package com.cdr.gen.util;

import java.util.Random;

public class RandomGaussian {
    private double valueOne;
    private double valueTwo;

    public RandomGaussian(double valueOne, double valueTwo) {
        this.valueOne = valueOne;
        this.valueTwo = valueTwo;
    }
    
    public static RandomGaussian generate(double stdDev, double mean) {
        Random rnd = new Random(System.currentTimeMillis());
        
        double v1 = rnd.nextGaussian();
        double v2 = rnd.nextGaussian();
        
        v1 = (v1 * stdDev) + mean;
        v2 = (v2 * stdDev) + mean;
        
        return new RandomGaussian(v1, v2);
    }

    public Double getValueOne() {
        return valueOne;
    }

    public Double getValueTwo() {
        return valueTwo;
    }
}
