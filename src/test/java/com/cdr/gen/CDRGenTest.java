package com.cdr.gen;

import java.util.Map;
import junit.framework.TestCase;

public class CDRGenTest extends TestCase {
    
    public CDRGenTest(String testName) {
        super(testName);
    }

    /**
     * Test of loadConfig method, of class CDRGen.
     */
    public void testLoadConfig() {
        CDRGen generator = new CDRGen();
        
        Map<String, Object> config = generator.getConfig();
        
        for (Map.Entry<String, Object> entry : generator.getConfig().entrySet()) {
            //System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        
        Map<String, Object> outgoingCallParams = (Map<String, Object>) config.get("outgoingCallParams");
        Map<String, Object> conf = (Map<String, Object>) outgoingCallParams.get("Free");
        
        long stdDev = (Long)conf.get("callStdDev");
        long mean = (Long)conf.get("callDur");
    
        System.out.println("Std Dev: " + stdDev + "\nMean: " + mean);
    }
}
