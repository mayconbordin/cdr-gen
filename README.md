# cdr-gen

This is a Call Detail Record ([CDR](http://en.wikipedia.org/wiki/Call_detail_record)) generator based on the [CDR Tool](http://paul.kinlan.me/call-detail-record-cdr-generation-tool/), developed by Paul Kinlan.

## Command-line Usage

To generate a dataset of CDR records and save at `<output_file>`:

```bash
java -jar cdr-gen.jar <output_file> [<config_file>]
```

The default configuration file is at `src/main/resources/config.json`, but a custom configuration can be given at `<config_file>`.

## API Usage

```java
// load the default configuration file
CDRGen generator = new CDRGen();
        
Population population = new Population(generator.getConfig());
population.create();

List<Person> customers = population.getPopulation();

for (Person p : customers) {
    for (Call c : p.getCalls()) {
        // do something
    }
}
```

## Configuration Options

- `callsMade`: the average and standard deviation number of calls made by each customer.
- `numAccounts`: the number of customers that will generate calls.
- `startDate` and `endDate`: the period when the calls will be made.
- `callTypes`: a list with all the call types, for now the `Local` type is the only obligatory.
- `dayDistribution`: the probabilities of a call happening in each day of the week.
- `offPeakTimePeriod`: the off peak is a period of time with reduced prices, it happens in the weekends and in the weekdays in a certain period of time, configured by this parameter.
- `outgoingCallParams`: for each type of call it gives the cost of the minute, the average and standard deviation of the duration of a call within or not the off peak period and the probability of the call type happening.
- `outgoingNumberDistribution`: the average and standard deviation number of phone number for each type of call. It is used to build the set of phone numbers that a customer can call.
- `phoneLines`: how many phone lines a customer can have. The number of lines for a customer is randomly generated (gaussian), according to the average and standard deviation.
- `timeDistCsv` **[optional]**: the full path to the file that contains the information about the probabilities of call happening in a given time in a weekday of weekend.