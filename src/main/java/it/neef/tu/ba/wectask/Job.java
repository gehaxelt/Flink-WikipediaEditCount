package it.neef.tu.ba.wectask;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.io.TextOutputFormat;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

import java.util.ArrayList;

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

        DataSet<Tuple2<LongWritable, Text>> input =
                env.readHadoopFile(new PageXMLInputFormat(), LongWritable.class, Text.class, args[0]);

        DataSet<Page> pages = input.map(new MapFunction<Tuple2<LongWritable, Text>, Page>() {
            @Override
            public Page map(Tuple2<LongWritable, Text> longWritableTextTuple2) throws Exception {
                ArrayList<Page> pages = XMLContentHandler.parseXMLString(longWritableTextTuple2.f1.toString());
                if(pages == null || pages.size() != 1 || pages.get(0) == null) {
                    return null;
                }
                return pages.get(0);
            }
        });



        pages = pages.filter(new FilterFunction<Page>() {
            @Override
            public boolean filter(Page page) throws Exception {
                return  page.getNs() == NS_FILTER;
            }
        });

        DataSet<User> users = pages.flatMap(new FlatMapFunction<Page, User>() {
            @Override
            public void flatMap(Page page, Collector<User> collector) throws Exception {
                for(Revision r : page.getRevisions()) {
                    collector.collect(new User(r.getUsername(), 1));
                }
            }
        });

        users
            .groupBy("username")
            .reduce(new ReduceFunction<User>() {
                @Override
                public User reduce(User user, User t1) throws Exception {
                    return new User(user.getUsername(), user.getEditCount() + t1.getEditCount());
                }
            })
            .writeAsFormattedText(args[1], new TextOutputFormat.TextFormatter<User>() {
                @Override
                public String format(User user) {
                    return user.getUsername() + ", " + String.valueOf(user.getEditCount());
                }
            });

        env.execute("Wikipedia user edit count");
    }
}
