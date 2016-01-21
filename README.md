Wikipedia EditCount
=====================
This is an exercise for writing an apache flink java program. The task is to read a Wikipedia XML Dump (```**-pages-meta-history.xml```) and output author name and edit counts tuples for all pages in namespace 0. 
The full task can be found in ```src/doc/Wikipedia author count using Apache Flink.pdf```. 

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
bin/start-local.sh
```

Afterwards clone this repository: 

```
https://gitlab.tubit.tu-berlin.de/gehaxelt/ba-dima-wikipediaeditcountask.git
cd ba-dima-wikipediaeditcountask
```

Build the package with maven:

```
mvn clean package
```

This should produce a file called ```WikipediaEditCountTask-1.0-SNAPSHOT.jar``` in the ```target/``` directory.

Download a Wikipedia XML Dump from <https://dumps.wikimedia.org/backup-index.html> with edit history content (```*-pages-meta-history.xml```).
For example <https://dumps.wikimedia.org/aawiki/20160111/aawiki-20160111-pages-meta-history.xml.7z> and decompress it.

To start the computation, change into your apache-flink installation directory again:

```
bin/flink run <path-to-target-wikipediaeditcountask-snapshot.jar> <path-to-pages-meta-history.xml> <output-file>
```
where ```<output-file>``` should have a prefix (e.g. ```file://```, ```file:///tmp/output```).  

License
=====================
See ```LICENSE.md```