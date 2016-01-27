package it.neef.tu.ba.wectask;

import junit.framework.TestCase;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;

import java.io.*;
import java.util.Collection;

/**
 * Created by gehaxelt on 21.01.16.
 */
public class JobTest extends TestCase {

    private File XMLDataFile;
    private File outputFile;
    private String resLine1, resLine2;

    public void testJob() {

        try {
            Job.main(new String[] {this.XMLDataFile.toURI().toURL().toString(), this.outputFile.toURI().toURL().toString()});
        } catch (Exception e) {
            fail("Exception occurred " + e);
        }
        FileReader fReader = null;
        StringBuilder sb = new StringBuilder();

        /**
         * Dirty workaround.
         * When project is packaged and ran with bin/flink project.jar input file:///output, all output is written to /output.
         * However, when Job.main() is called in this test, /output is a directory, with the small files containing parts of
         * the intended file. It seems like "bin/flink" concatenates those files, but directly calling Job.main() doesn't.
         *
         * REASON:
         * fs.output.always-create-directory: File writers running with a parallelism larger than one create a directory
         * for the output file path and put the different result files (one per parallel writer task) into that directory.
         * If this option is set to true, writers with a parallelism of 1 will also create a directory and place a single
         * result file into it. If the option is set to false, the writer will directly create the file directly at the
         * output path, without creating a containing directory. (DEFAULT: false)
         * SRC: https://ci.apache.org/projects/flink/flink-docs-master/setup/config.html#other
         */
        Collection<File> files = FileUtils.listFiles(this.outputFile, null, false);
        for(File file: files) {
            try {
                fReader = new FileReader(file);

                int c;
                while ((c = fReader.read()) != -1) {
                    sb.append((char) c);
                }
            } catch (FileNotFoundException e) {
                fail("Reading output file failed");
            } catch (IOException e) {
                fail("I/O error");
            }
        }

        String output = sb.toString();
        String[] lines = output.split("\n");

        assertEquals(2, lines.length);

        for(String line : lines) {
            if(! (this.resLine1.equals(line) || this.resLine2.equals(line))) {
                fail("Wrong line in output");
            }
        }

    }

    @Before
    public void setUp() throws Exception {
        super.setUp();

        //Page: ns=0, title='Main Page', id=1269, revisions = [Revision: id=12692, username='Arde~aawiki', Revision: id=1270, username='Arde~aawiki']
        //Page: ns=1, title='Main Page2', id=12692, revisions = [Revision: id=12702, username='tester']
        //Page: ns=0, title='Main Page3', id=12693, revisions = [Revision: id=12693, username='test']
        this.XMLDataFile = FileUtils.getFile("src","test", "resources", "jobtest-test-dump.xml");

        //Generate temporary filename;
        this.outputFile = java.nio.file.Files.createTempFile("out-csv", String.valueOf(System.currentTimeMillis())).toFile();
        this.outputFile.delete();

        this.resLine1 = "Arde~aawiki, 2";
        this.resLine2 = "test, 1";
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
        File outputDir = new File(this.outputFile.getAbsolutePath() + "/");
        for(File f : outputDir.listFiles()){
            f.delete();
        }
        outputDir.delete();
    }
}