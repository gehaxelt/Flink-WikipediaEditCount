package it.neef.tu.ba.wectask;

import com.google.common.io.Closeables;
import org.apache.commons.io.Charsets;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.Seekable;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.*;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by gehaxelt on 12.02.16.
 */
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//
public class CompressedXmlInputFormat extends TextInputFormat {
    private static final Logger log = LoggerFactory.getLogger(CompressedXmlInputFormat.class);
    public static final String START_TAG_KEY = "xmlinput.start";
    public static final String END_TAG_KEY = "xmlinput.end";

    public CompressedXmlInputFormat() {
    }

    public RecordReader<LongWritable, Text> createRecordReader(InputSplit split, TaskAttemptContext context) {
        return new CompressedXmlInputFormat.XmlRecordReader();
    }

    public static class XmlRecordReader extends RecordReader<LongWritable, Text> {
        private byte[] startTag;
        private byte[] endTag;
        private long start;
        private long pos;
        private long end;
        private FSDataInputStream fsin;
        private Seekable filePosition;
        private final DataOutputBuffer buffer = new DataOutputBuffer();
        private LongWritable currentKey;
        private Text currentValue;
        private boolean isCompressedInput;
        private Decompressor decompressor;
        private InputStream in;

        private boolean next(LongWritable key, Text value) throws IOException {
            if(getFilePosition() < this.end && this.readUntilMatch(this.startTag, false)) {
                boolean var3;
                try {
                    this.buffer.write(this.startTag);
                    if(!this.readUntilMatch(this.endTag, true)) {
                        return false;
                    }

                    key.set(getFilePosition());
                    value.set(this.buffer.getData(), 0, this.buffer.getLength());
                    var3 = true;
                } finally {
                    this.buffer.reset();
                }

                return var3;
            } else {
                return false;
            }
        }

        public void close() throws IOException {
            Closeables.close(this.fsin, true);
            if(this.decompressor != null) {
                CodecPool.returnDecompressor(this.decompressor);
            }

        }

        public float getProgress() throws IOException {
            return (float)(getFilePosition() - this.start) / (float)(this.end - this.start);
        }

        private boolean readUntilMatch(byte[] match, boolean withinBlock) throws IOException {
            int i = 0;

            do {
                int b = this.in.read();
                if(b == -1) {
                    return false;
                }
                this.pos++;

                if(withinBlock) {
                    this.buffer.write(b);
                }

                if(b == match[i]) {
                    ++i;
                    if(i >= match.length) {
                        return true;
                    }
                } else {
                    i = 0;
                }
            } while(withinBlock || i != 0 || getFilePosition() < this.end);

            return false;
        }

        public LongWritable getCurrentKey() throws IOException, InterruptedException {
            return this.currentKey;
        }

        public Text getCurrentValue() throws IOException, InterruptedException {
            return this.currentValue;
        }

        private long getFilePosition() throws IOException {
            long retVal;
            if(this.isCompressedInput && null != this.filePosition) {
                retVal = this.filePosition.getPos();
            } else {
                retVal = this.pos;
            }

            return retVal;
        }

        public void initialize(InputSplit Isplit, TaskAttemptContext context) throws IOException, InterruptedException {
            Configuration conf = context.getConfiguration();
            FileSplit split = (FileSplit)Isplit;
            this.startTag = conf.get("xmlinput.start").getBytes(Charsets.UTF_8);
            this.endTag = conf.get("xmlinput.end").getBytes(Charsets.UTF_8);
            this.start = split.getStart();
            this.end = this.start + split.getLength();
            Path file = split.getPath();
            FileSystem fs = file.getFileSystem(conf);
            this.fsin = fs.open(split.getPath());

            CompressionCodec codec = (new CompressionCodecFactory(conf)).getCodec(file);
            if(null != codec) {
                this.isCompressedInput = true;
                this.decompressor = CodecPool.getDecompressor(codec);
                if(codec instanceof SplittableCompressionCodec) {
                    SplitCompressionInputStream cIn = ((SplittableCompressionCodec)codec).createInputStream(this.fsin, this.decompressor, this.start, this.end, SplittableCompressionCodec.READ_MODE.BYBLOCK);
                    this.start = cIn.getAdjustedStart();
                    this.end = cIn.getAdjustedEnd();
                    this.filePosition = cIn;
                    this.in = cIn;

                } else {
                    this.filePosition = this.fsin;
                    this.in  = codec.createInputStream(this.fsin, this.decompressor);
                }
            } else {
                this.fsin.seek(this.start);
                this.in = fsin;
                this.filePosition = this.fsin;
            }

            this.pos = this.start;
        }

        public boolean nextKeyValue() throws IOException, InterruptedException {
            this.currentKey = new LongWritable();
            this.currentValue = new Text();
            return this.next(this.currentKey, this.currentValue);
        }
    }
}
