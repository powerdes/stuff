## Summary
XML output from Iphone Health app is pretty crappy, this package calculates some averages and outputs the raw data, split by date into csv files for further processing.

## Maven Build Goals
```
mvn clean compile package
```	

## Usage
```
java -cp Health-1.0-SNAPSHOT.jar com.powerdes.Health.ParseHealthExport input/Export.xml
```

## Sample Console Output
```
Between 2016-06-09 and 2016-08-12, you averaged 4878.297 of steps per day
Between 2016-06-09 and 2016-08-12, you averaged 2.39 miles per day
Between 2016-06-09 and 2016-08-12, you averaged 5.164 flights of stairs per day
```

## TODO
Some optimizations can be made
