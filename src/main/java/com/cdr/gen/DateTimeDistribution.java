package com.cdr.gen;

import com.cdr.gen.util.RandomGaussian;
import static com.cdr.gen.PhoneNumberGenerator.PHONE_CODES;
import com.cdr.gen.util.IOUtils;
import com.cdr.gen.util.JavaUtils;
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
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;

/**
 *
 * @author maycon
 */
public class DateTimeDistribution {
    private static final Logger LOG = Logger.getLogger(DateTimeDistribution.class);
    private static final String TIME_DIST_CSV = "/time_dist.csv";
    public static final String TYPE_WEEKDAY = "Weekday";
    public static final String TYPE_WEEKEND = "Weekend";
    
    public static final String[] DAYS = new String[]{"sun", "mon", "tue", "wed", "thu", "fri", "sat"};
    
    private Map<String, Object> outgoingCallParams;
    private LocalTime offPeakStart;
    private LocalTime offPeakEnd;
    
    private Map<String, Double> dayDistribution;
    private List<Double> dayDistributionVals;
    
    private Map<String, Map<String, Double>> timeDistribution;
    private Map<String, List<Double>> timeDistributionVals;
    
    private DateTimeFormatter dateFormatter;
    private DateTimeFormatter dateTimeFormatter;
    
    private DateTime startDate;
    private DateTime endDate;
    private int dateRange;
    
    private Random random;

    public DateTimeDistribution(Map<String, Object> config) {
        outgoingCallParams = (Map<String, Object>) config.get("outgoingCallParams");

        loadOffPeakTimePeriod((Map<String, String>) config.get("offPeakTimePeriod"));
        loadDayDist((Map<String, Double>) config.get("dayDistribution"));
        
        dateFormatter = DateTimeFormat.forPattern("dd/MM/yyyy");
        dateTimeFormatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");
        
        startDate = dateFormatter.parseDateTime((String)config.get("startDate"));
        endDate = dateFormatter.parseDateTime((String)config.get("endDate"));
        
        Interval interval = new Interval(startDate, endDate);
        Duration duration = interval.toDuration();
        
        dateRange = duration.toStandardDays().getDays();
        
        random = new Random(System.currentTimeMillis());
        
        String timeDistFile = config.containsKey("timeDistCsv") 
                ? (String)config.get("timeDistCsv") : TIME_DIST_CSV;
        
        loadTimeDist(timeDistFile);
    }
    
    public void loadDayDist(Map<String, Double> params) {
        dayDistribution = new HashMap<String, Double>(DAYS.length);
        dayDistributionVals = new ArrayList<Double>(DAYS.length);
        
        for (String day : DAYS) {
            dayDistribution.put(day, params.get(day));
            dayDistributionVals.add(params.get(day));
        }
    }
    
    public void loadTimeDist(String filename) {
        timeDistribution = new HashMap<String, Map<String, Double>>();
        timeDistributionVals = new HashMap<String, List<Double>>();
        
        try {
            ICsvListReader listReader;
            
            if (JavaUtils.isJar() && filename.equals(TIME_DIST_CSV)) {
                InputStream is = PhoneNumberGenerator.class.getResourceAsStream(filename);
                listReader = new CsvListReader(
                        new StringReader(IOUtils.convertStreamToString(is)),
                        CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE);
            } else {
                if (filename.equals(TIME_DIST_CSV))
                    filename = "src/main/resources" + filename;
                
                listReader = new CsvListReader(
                        new FileReader(filename), 
                        CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE);
            }
            
            listReader.getHeader(true);

            List<String> timeList;
            while( (timeList = listReader.read()) != null ) {
                String time = timeList.get(0);
                Double prob = Double.parseDouble(timeList.get(1));
                String type = timeList.get(2);
                

                if (!timeDistribution.containsKey(type)) {
                    timeDistribution.put(type, new HashMap<String, Double>());
                }
                
                if (!timeDistributionVals.containsKey(type)) {
                    timeDistributionVals.put(type, new ArrayList<Double>());
                }

                timeDistribution.get(type).put(time, prob);
                timeDistributionVals.get(type).add(prob);
            }

            listReader.close();
        }  catch (FileNotFoundException ex) {
            LOG.error("Unable to find time distribution file.", ex);
        } catch (IOException ex) {
            LOG.error("Error while reading the time distribution file.", ex);
        }
    }
    
    public void loadOffPeakTimePeriod(Map<String, String> params) {
        String offPeak = params.get("start");
        String[] offPeakTime = offPeak.split(":");
        offPeakStart = new LocalTime(Integer.parseInt(offPeakTime[0]), Integer.parseInt(offPeakTime[1]));
        
        offPeak = params.get("end");
        offPeakTime = offPeak.split(":");
        offPeakEnd = new LocalTime(Integer.parseInt(offPeakTime[0]), Integer.parseInt(offPeakTime[1]));
    }
    
    public int size() {
        return dayDistribution.size();
    }
    
    public Double getVal(int i) {
        return dayDistributionVals.get(i);
    }
    
    public DateTime getDateTime(String type, /*String callType,*/ int currDay/*, long avgCallDuration, long avgOPCallDuration*/) {
        double tmpRnd = 1;
        
        while (tmpRnd > 0) {
            tmpRnd = random.nextDouble();
            
            for (Map.Entry<String, Double> e : timeDistribution.get(type).entrySet()) {
                if (tmpRnd - e.getValue() > 0) {
                    tmpRnd -= e.getValue();
                } else {
                    DateTime date = startDate.plusDays(currDay);
                    String[] timeStr = e.getKey().split(":");
                    date = date.plusHours(Integer.parseInt(timeStr[0]))
                               .plusMinutes(Integer.parseInt(timeStr[1]));
                    return date;
                }
            }
        }
        
        return null;
    }
    
    public int getCallDuration(int dayName, String callType, LocalTime callTime, long avgCallDuration, long avgOPCallDuration) {
        String stdDevParam;
        long average = avgCallDuration;
        Map<String, Object> params = (Map<String, Object>) outgoingCallParams.get(callType);
        
        // Weekend
        // or within offPeak time period
        if ((dayName == 1 || dayName == 7) || 
                (callTime.compareTo(offPeakStart) >= 0 
                && callTime.compareTo(offPeakEnd) <= 0)) {
            stdDevParam = "callOPStdDev2";
            average = avgOPCallDuration;
        } else {
            stdDevParam = "callStdDev2";
        }
        
        return Math.abs(RandomGaussian.generate(
                (Long)params.get(stdDevParam), average)
                .getValueOne().intValue());
    }
    
    public int getDayOfWeek() {
        double tmpRnd = 1;
        int currWeek;
        
        while (tmpRnd > 0) {
            tmpRnd = random.nextDouble();
            
            for (int k=0; k<size(); k++) {
                if (tmpRnd - getVal(k) > 0) {
                    tmpRnd -= getVal(k);
                } else {
                    currWeek = (int) (random.nextDouble() * (dateRange / 7));
                    return (currWeek * 7 - startDate.getDayOfWeek()) + (k+1);
                }
            }
        }
        
        return 1;
    }
    
    protected double getCallCost(Call call) {
        int dayOfWeek = call.getTime().getStart().getDayOfWeek();
        int duration = call.getTime().toDuration().toStandardMinutes().getMinutes();
        LocalTime startTime = call.getTime().getStart().toLocalTime();
        
        Map<String, Object> params = (Map<String, Object>) outgoingCallParams.get(call.getType());
        String callCostParam;
        
        if ((dayOfWeek == 1 || dayOfWeek == 7) || 
                (startTime.compareTo(offPeakStart) >= 0 
                && startTime.compareTo(offPeakEnd) <= 0)) {
            callCostParam = "callOPCost";
        } else {
            callCostParam = "callCost";
        }
        
        long cost = (Long) params.get(callCostParam);
        return duration * cost;
    }

    public DateTime getStartDate() {
        return startDate;
    }

    public DateTime getEndDate() {
        return endDate;
    }
    
    
    
    public static class CallTime {
        private DateTime date;
        private LocalTime time;
        private int duration;

        public DateTime getDate() {
            return date;
        }

        public void setDate(DateTime date) {
            this.date = date;
        }

        public LocalTime getTime() {
            return time;
        }
        
        public void setTime(String time) {
            String timeSplit[] = time.split(":");
            this.time = new LocalTime(Integer.parseInt(timeSplit[0]), Integer.parseInt(timeSplit[1]));
        }

        public void setTime(LocalTime time) {
            this.time = time;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }
        
        
    }
}
