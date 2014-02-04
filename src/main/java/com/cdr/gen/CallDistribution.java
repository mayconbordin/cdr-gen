package com.cdr.gen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Used mainly for obtaining a random phone call type given the probabilities
 * set in the configuration file.
 * @author Maycon Viana Bordin <mayconbordin@gmail.com>
 */
public class CallDistribution {
    private Map<String, Double> weigths;
    private List<String> weigthKeys;
    private List<Double> weigthVals;
    
    private List<String> callTypes;
    private Map<String, Object> outgoingCallParams;
    
    private Random random;

    public CallDistribution(Map<String, Object> config) {
        callTypes = (List<String>) config.get("callTypes");
        outgoingCallParams = (Map<String, Object>) config.get("outgoingCallParams");
    
        weigths    = new HashMap<String, Double>(callTypes.size());
        weigthKeys = new ArrayList<String>(callTypes.size());
        weigthVals = new ArrayList<Double>(callTypes.size());
        
        for (String callType : callTypes) {
            Map<String, Object> params = (Map<String, Object>) outgoingCallParams.get(callType);
            weigths.put(callType, (Double) params.get("callProb"));
            weigthKeys.add(callType);
            weigthVals.add((Double) params.get("callProb"));
        }
        
        random = new Random(System.currentTimeMillis());
    }
    
    /**
     * @return A randomly selected phone call type
     */
    public String getRandomCallType() {
        double tmpRnd = 1;

        while (tmpRnd > 0) {
            tmpRnd = random.nextDouble();
            for (int k = 0; k < size(); k++) {
                if (tmpRnd - getVal(k) > 0) {
                    tmpRnd = tmpRnd - getVal(k);
                } else {
                    return getKey(k);
                }
            }
        }
        
        return null;
    }
    
    public int size() {
        return weigths.size();
    }

    public Map<String, Double> getWeigths() {
        return weigths;
    }

    public List<String> getKeys() {
        return weigthKeys;
    }
    
    public String getKey(int i) {
        return weigthKeys.get(i);
    }

    public List<Double> getVals() {
        return weigthVals;
    }
    
    public Double getVal(int i) {
        return weigthVals.get(i);
    }
}
