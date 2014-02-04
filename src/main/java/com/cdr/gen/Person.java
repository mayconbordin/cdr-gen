package com.cdr.gen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores information about a person.
 * @author Maycon Viana Bordin <mayconbordin@gmail.com>
 */
public class Person {
    private String phoneNumber;
    private int phoneLines;
    
    // summary info
    private long numCalls;
    
    // model information about the call spread
    private Map<String, Long> avgCallDuration = new HashMap<String, Long>();
    private Map<String, Long> avgOffPeakCallDuration = new HashMap<String, Long>();
    
    private List<Call> calls;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getPhoneLines() {
        return phoneLines;
    }

    public void setPhoneLines(int phoneLines) {
        this.phoneLines = phoneLines;
    }

    public long getNumCalls() {
        return numCalls;
    }

    public void setNumCalls(long numCalls) {
        this.numCalls = numCalls;
        this.calls = new ArrayList<Call>(((Long)numCalls).intValue());
    }

    public Map<String, Long> getAvgCallDuration() {
        return avgCallDuration;
    }

    public void setAvgCallDuration(Map<String, Long> avgCallDuration) {
        this.avgCallDuration = avgCallDuration;
    }

    public Map<String, Long> getAvgOffPeakCallDuration() {
        return avgOffPeakCallDuration;
    }

    public void setAvgOffPeakCallDuration(Map<String, Long> avgOffPeakCallDuration) {
        this.avgOffPeakCallDuration = avgOffPeakCallDuration;
    }

    public List<Call> getCalls() {
        return calls;
    }

    public void setCalls(List<Call> calls) {
        this.calls = calls;
    }
    
    
}
