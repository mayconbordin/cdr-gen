package com.cdr.gen;

import com.cdr.gen.util.IOUtils;
import com.cdr.gen.util.JavaUtils;
import com.cdr.gen.util.RandomUtil;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.log4j.Logger;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;

/**
 * Generates phone numbers and codes.
 * @author Maycon Viana Bordin <mayconbordin@gmail.com>
 */
public class PhoneNumberGenerator {
    private static final Logger LOG = Logger.getLogger(PhoneNumberGenerator.class);
    private static final String PHONE_CODES_CSV = "/phone_codes.csv";
    public static final Map<String, List<String>> PHONE_CODES = new HashMap<String, List<String>>();
    
    static {
        try {            
            LOG.info("Loading phone codes file.");
            ICsvListReader listReader;
            
            if (JavaUtils.isJar()) {
                InputStream is = PhoneNumberGenerator.class.getResourceAsStream(PHONE_CODES_CSV);
                listReader = new CsvListReader(
                        new StringReader(IOUtils.convertStreamToString(is)),
                        CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE);
            } else {
                listReader = new CsvListReader(
                        new FileReader("src/main/resources" + PHONE_CODES_CSV), 
                        CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE);
            }
            
            listReader.getHeader(true);
                        
            List<String> customerList;
            while( (customerList = listReader.read()) != null ) {
                String typeName = customerList.get(0);
                String startNumber = customerList.get(1);
                
                if (!PHONE_CODES.containsKey(typeName)) {
                    PHONE_CODES.put(typeName, new ArrayList<String>());
                }
                
                PHONE_CODES.get(typeName).add(startNumber);
            }
            
            listReader.close();
        } catch (FileNotFoundException ex) {
            LOG.error("Unable to find phone codes file.", ex);
        } catch (IOException ex) {
            LOG.error("Error while reading the phones code file.", ex);
        }
    }
    
    /**
     * Generates a random phone number.
     * @param numDigits The number of digits
     * @return The random phone number
     */
    public static String getRandomNumber(int numDigits) {
        Random rnd = new Random(System.currentTimeMillis());
        
        String number = "";
        for (int i=0; i<numDigits; i++) {
            number += RandomUtil.randInt(0, 9);
        }
        
        return number;
    }
    
    /**
     * Returns a randomly picked phone code
     * @param callType The type of call for the phone code
     * @param currCode The code to be excluded from the returned code
     * @return The phone code
     */
    public static String getRandomPhoneCode(String callType, String currCode) {
        int num;
        String code = "";
        
        if (!PHONE_CODES.containsKey(callType)) {
            return null;
        }
        
        do {
            num = RandomUtil.randInt(0, PHONE_CODES.get(callType).size()-1);
            code = PHONE_CODES.get(callType).get(num);
        } while (code.equals(currCode));
        
        return code;
    }
    
    
}
