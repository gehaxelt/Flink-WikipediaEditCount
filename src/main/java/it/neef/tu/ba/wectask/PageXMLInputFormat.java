package it.neef.tu.ba.wectask;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.mahout.text.wikipedia.XmlInputFormat;

public class PageXMLInputFormat extends CompressedXmlInputFormat {
    @Override
    public RecordReader<LongWritable, Text> createRecordReader(InputSplit split, TaskAttemptContext context) {
        Configuration conf = context.getConfiguration();
        conf.set(PageXMLInputFormat.START_TAG_KEY, "<page>");
        conf.set(PageXMLInputFormat.END_TAG_KEY, "</page>");

        return super.createRecordReader(split, context);
    }
}