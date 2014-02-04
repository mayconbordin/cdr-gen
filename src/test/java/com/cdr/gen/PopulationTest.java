package com.cdr.gen;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import junit.framework.TestCase;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class PopulationTest extends TestCase {
    private CDRGen generator;
    
    public PopulationTest(String testName) {
        super(testName);
        
        generator = new CDRGen();
    }

    /**
     * Test of create method, of class Population.
     */
    public void testCreate() {
        Population population = new Population(generator.getConfig());
        population.create();
        
        int popSize = population.getPopulation().size();
        
        System.out.println("Population size: " + popSize);
        assertEquals(generator.getConfig().get("numAccounts"), (long)popSize);
        
        for (Person p : population.getPopulation()) {
            System.out.println("Num. of calls: " + p.getNumCalls());
        }
        
        Person one = population.getPopulation().get(0);
        
        for (Map.Entry<String, Long> entry : one.getAvgCallDuration().entrySet()) {
            System.out.println("Average call duration for " + entry.getKey() 
                    + " calls: " + entry.getValue());
        }
        
        // write calls to file
        try {
            DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("HH:mm:ss");
            FileWriter fw = new FileWriter("output.txt");
            String newLine = System.getProperty("line.separator");

            for (Call c : one.getCalls()) {
                fw.append(c.getId() + ","
                        + one.getPhoneNumber() + ","
                        + c.getLine() + ","
                        + c.getDestPhoneNumber() + ","
                        + c.getTime().getStart().toString(dateFormatter) + "," 
                        + c.getTime().getEnd().toString(dateFormatter) + "," 
                        + c.getTime().getStart().toString(timeFormatter) + "," 
                        + c.getTime().getEnd().toString(timeFormatter) + ","
                        + c.getType() + ","
                        + c.getCost()
                        + newLine);
            }

            fw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } 
    }
}
