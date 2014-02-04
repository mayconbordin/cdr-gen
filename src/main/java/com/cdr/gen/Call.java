package com.cdr.gen;

import org.joda.time.Interval;

/**
 * Holds information about a call.
 * @author Maycon Viana Bordin <mayconbordin@gmail.com>
 */
public class Call {
    private long id;
    private int line;
    private String type;
    private Interval time;
    private double cost;
    private String destPhoneNumber;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Interval getTime() {
        return time;
    }

    public void setTime(Interval time) {
        this.time = time;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public String getDestPhoneNumber() {
        return destPhoneNumber;
    }

    public void setDestPhoneNumber(String destPhoneNumber) {
        this.destPhoneNumber = destPhoneNumber;
    }
    
    
}
