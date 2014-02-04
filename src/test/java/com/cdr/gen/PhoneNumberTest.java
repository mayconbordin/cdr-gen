package com.cdr.gen;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;

public class PhoneNumberTest extends TestCase {
    
    public PhoneNumberTest(String testName) {
        super(testName);
    }

    public void test() {
        
        List<String> types = Arrays.asList("Local", "PRS", "Intl", "Mobile", "National", "Free");
        
        for (Map.Entry<String, List<String>> entry : PhoneNumberGenerator.PHONE_CODES.entrySet()) {
            System.out.println(entry.getKey());
            assertTrue(types.contains(entry.getKey()));
        }
    }
}
