WikipediaEditCount
=====================
This is an exercise for writing an apache flink java program. The task is to read a Wikipedia XML Dump (```**-pages-meta-history.xml```) and output author name and edit counts tuples for all pages in namespace 0. 
The full task can be found in ```src/doc/Wikipedia author count using Apache Flink.pdf```. 

[![Build Status](https://travis-ci.org/gehaxelt/Flink-WikipediaEditCount.svg?branch=master)](https://travis-ci.org/gehaxelt/Flink-WikipediaEditCount)

Requirements
=====================
- Java 1.8
- Maven
- Apache Flink 
- (optionally 7z)

Setup
=====================
First of all, start a local apache flink instance. Change into your apache-flink installation directory and run:
```
$> bin/start-local.sh
```

Afterwards clone this repository: 

```
$> git clone https://github.com/gehaxelt/Flink-WikipediaEditCount.git
$> cd Flink-WikipediaEditCount/
```

Build the package with maven:

```
$> mvn clean package
```

This should produce a file called ```WikipediaEditCountTask-1.0-SNAPSHOT.jar``` in the ```target/``` directory.

Download a Wikipedia XML Dump from <https://dumps.wikimedia.org/backup-index.html> with edit history content (```*-pages-meta-history.xml```).
For example <https://dumps.wikimedia.org/aawiki/20160111/aawiki-20160111-pages-meta-history.xml.7z> and decompress it.

To start the computation, change into your apache-flink installation directory again:

```
$> bin/flink run <path-to-target-wikipediaeditcountask-snapshot.jar> <path-to-pages-meta-history.xml> <output-file>
```
where both files should have a prefix (e.g. ```file://```, ```file:///tmp/output```, ```hdfs://```).

Example run:

- Upload Dump to HDFS:
```
$> hadoop fs -put src/doc/aawiki-20160111-pages-meta-history.xml aawiki.xml
```
- Execute Job on Apache Flink:
```
$> bin/flink run ../WikipediaEditCountTask/target/WikipediaEditCountTask-1.0-SNAPSHOT.jar hdfs://localhost:9000/user/gehaxelt/aawiki.xml hdfs://localhost:9000/user/gehaxelt/out-aawiki
01/27/2016 16:57:35 Job execution switched to status RUNNING. 
01/27/2016 16:57:35 CHAIN DataSource (at createInput(ExecutionEnvironment.java:508) (org.apache.flink.api.java.hadoop.mapreduce.HadoopInputFormat)) -> Map (Map at main(Job.java:38)) -> Filter (Filter at main(Job.java:51)) -> FlatMap (FlatMap at main(Job.java:58))(1/4) switched to SCHEDULED
[Snip, more output]
01/27/2016 16:57:38	DataSink (TextOutputFormat (hdfs://localhost:9000/user/gehaxelt/out-aawiki) - UTF-8)(3/4) switched to FINISHED 
01/27/2016 16:57:38	Job execution switched to status FINISHED.
```
- Retrieve the result:
```
$> hadoop fs -getmerge 'out-aawiki/' /tmp/aawiki
$> cat /tmp/aawiki                                                                                                                                                                     Mi 27 Jan 2016 16:59:12 CET
Aeæ~aawiki, 1
Arde~aawiki, 4
Dethic, 1
Johney, 1
Node ue, 1
VolkovBot, 4
Afar god, 1
Lars~aawiki, 1
Pathoschild, 1
Rich Farmbrough, 1
VasilievVV, 2
כל יכול, 2
Aurevilly, 3
Hégésippe Cormier, 1
Jon Harald Søby, 1
Kanabekobaton, 1
Korg, 4
Tim Starling, 1
M7, 1
N3m6~aawiki, 1
Platonides, 1
Rocastelo, 3
Squidward~aawiki, 1
```

License
=====================
See ```LICENSE.md```