package com.cdr.gen;

import com.cdr.gen.util.RandomGaussian;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author maycon
 */
public class PhoneBucketGenerator {
    private Map<String, Object> outgoingNumberDist;

    public PhoneBucketGenerator(Map<String, Object> config) {
        outgoingNumberDist = (Map<String, Object>) config.get("outgoingNumberDistribution");
        
    }
    
    public Map<String, List<String>> createPhoneBucket(Person p, Map<String, Integer> callTypeSummary) {
        Map<String, Long> params;
        Map<String, List<String>> destPhoneNumbers = new HashMap<String, List<String>>(callTypeSummary.size());
        
        for (Map.Entry<String, Integer> e : callTypeSummary.entrySet()) {
            params = (Map<String, Long>) outgoingNumberDist.get(e.getKey());
            
            RandomGaussian gauss = RandomGaussian.generate(
                    params.get("stdDev"), params.get("mean"));
            
            Double count = Math.ceil((e.getValue() / 100.0) * Math.abs(gauss.getValueOne()));
            int phoneCount = (count > 0) ? count.intValue() : 1;
            
            if (!destPhoneNumbers.containsKey(e.getKey())) {
                destPhoneNumbers.put(e.getKey(), new ArrayList<String>(phoneCount));
            }
            
            String phoneNumber;
            String code = p.getPhoneNumber().substring(0, 4);
            
            for (int i=0; i<phoneCount; i++) {
                if (e.getKey().equals("Local")) {
                    phoneNumber = code + PhoneNumberGenerator.getRandomNumber(7);
                } else {
                    String destCode = PhoneNumberGenerator.getRandomPhoneCode(e.getKey(), code);
                    phoneNumber = destCode + PhoneNumberGenerator.getRandomNumber(11 - destCode.length());
                }

                destPhoneNumbers.get(e.getKey()).add(phoneNumber);
            }
        }
        
        return destPhoneNumbers;
    }
}
