package it.neef.tu.ba.wectask;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.io.TextOutputFormat;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Count user edits from wikipedia metadata dumps.
 */
public class Job {

    //Filter for specific namespace.
    public static final int NS_FILTER = 0;


	public static void main(String[] args) throws Exception {

        if(args.length != 2) {
            System.err.println("USAGE: Job <wikipediadump.xml> <output>");
            return;
        }

        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        XMLContentHandler xmlCH = XMLContentHandler.parseXML(args[0]);

        if(xmlCH==null) {
            System.err.println("Error parsing XML!");
            return ;
        }

        //Filter for specific namespace!
        DataSet<Page> pageSet = env.fromCollection(xmlCH.getAllPages()).filter(new FilterFunction<Page>() {
            @Override
            public boolean filter(Page page) throws Exception {
                return  page.getNs() == NS_FILTER;
            }
        });

        //Put all <Username,Edits> (Edits = 1 per revision) into a dataset.
        DataSet<Tuple2<String, Integer>> userEditSet = pageSet.flatMap(new FlatMapFunction<Page, Tuple2<String, Integer>>() {
            @Override
            public void flatMap(Page page, Collector<Tuple2<String, Integer>> collector) throws Exception {
                for(Revision r : page.getRevisions()) {
                    collector.collect(new Tuple2<String, Integer>(r.getUsername(), 1));
                }
            }
        });

        //Group by Username and sum by Edits
        userEditSet
            .groupBy(0)
            .sum(1)
            //.print();
            .writeAsFormattedText(args[1], new TextOutputFormat.TextFormatter<Tuple2<String, Integer>>() {
                @Override
                public String format(Tuple2<String, Integer> stringIntegerTuple2) {
                    return stringIntegerTuple2.f0 + ", " + String.valueOf(stringIntegerTuple2.f1);
                }
            });

        env.execute("Wikipedia user edit count");

    }
}
