package it.neef.tu.ba.wectask;

import junit.framework.TestCase;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import static org.junit.Assert.*;

/**
 * Created by gehaxelt on 21.01.16.
 */
public class JobTest extends TestCase {

    private File XMLDataFile;
    private File outputFile;
    private XMLContentHandler xml;
    private String resLine1, resLine2;

    public void testJob() {

        try {
            Job.main(new String[] {this.XMLDataFile.getAbsolutePath(), this.outputFile.toURI().toURL().toString()});
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
        this.XMLDataFile = java.nio.file.Files.createTempFile("test-xml",String.valueOf(System.currentTimeMillis())).toFile();

        //Generate temporary filename;
        this.outputFile = java.nio.file.Files.createTempFile("out-csv", String.valueOf(System.currentTimeMillis())).toFile();
        this.outputFile.delete();

        StringBuilder sb = new StringBuilder();
        sb.append("<mediawiki xmlns=\"http://www.mediawiki.org/xml/export-0.10/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.mediawiki.org/xml/export-0.10/ http://www.mediawiki.org/xml/export-0.10.xsd\" version=\"0.10\" xml:lang=\"aa\">\n  <siteinfo>\n    <sitename>Wikipedia</sitename>\n    <dbname>aawiki</dbname>\n    <base>https://aa.wikipedia.org/wiki/Main_Page</base>\n    <generator>MediaWiki 1.27.0-wmf.9</generator>\n    <case>first-letter</case>\n    <namespaces>\n      <namespace key=\"-2\" case=\"first-letter\">Media</namespace>\n      <namespace key=\"-1\" case=\"first-letter\">Special</namespace>\n      <namespace key=\"0\" case=\"first-letter\" />\n      <namespace key=\"1\" case=\"first-letter\">Talk</namespace>\n      <namespace key=\"2\" case=\"first-letter\">User</namespace>\n      <namespace key=\"3\" case=\"first-letter\">User talk</namespace>\n      <namespace key=\"4\" case=\"first-letter\">Wikipedia</namespace>\n      <namespace key=\"5\" case=\"first-letter\">Wikipedia talk</namespace>\n      <namespace key=\"6\" case=\"first-letter\">File</namespace>\n      <namespace key=\"7\" case=\"first-letter\">File talk</namespace>\n      <namespace key=\"8\" case=\"first-letter\">MediaWiki</namespace>\n      <namespace key=\"9\" case=\"first-letter\">MediaWiki talk</namespace>\n      <namespace key=\"10\" case=\"first-letter\">Template</namespace>\n      <namespace key=\"11\" case=\"first-letter\">Template talk</namespace>\n      <namespace key=\"12\" case=\"first-letter\">Help</namespace>\n      <namespace key=\"13\" case=\"first-letter\">Help talk</namespace>\n      <namespace key=\"14\" case=\"first-letter\">Category</namespace>\n      <namespace key=\"15\" case=\"first-letter\">Category talk</namespace>\n      <namespace key=\"828\" case=\"first-letter\">Module</namespace>\n      <namespace key=\"829\" case=\"first-letter\">Module talk</namespace>\n      <namespace key=\"2300\" case=\"first-letter\">Gadget</namespace>\n      <namespace key=\"2301\" case=\"first-letter\">Gadget talk</namespace>\n      <namespace key=\"2302\" case=\"case-sensitive\">Gadget definition</namespace>\n      <namespace key=\"2303\" case=\"case-sensitive\">Gadget definition talk</namespace>\n      <namespace key=\"2600\" case=\"first-letter\">Topic</namespace>\n    </namespaces>\n  </siteinfo>");
        //Page: ns=0, title='Main Page', id=1269, revisions = [Revision: id=12692, username='Arde~aawiki', Revision: id=1270, username='Arde~aawiki']
        sb.append("<page>\n    <title>Main Page</title>\n    <ns>0</ns>\n    <id>1269</id>\n    <restrictions>edit=autoconfirmed:move=autoconfirmed</restrictions>\n    <revision>\n      <id>1269</id>\n      <timestamp>2005-07-07T15:31:37Z</timestamp>\n      <contributor>\n        <username>Arde~aawiki</username>\n        <id>55</id>\n      </contributor>\n      <model>wikitext</model>\n      <format>text/x-wiki</format>\n      <text xml:space=\"preserve\">abc</text>\n      <sha1>sgjoro1gzkwvszypw7metsc9m886zgx</sha1>\n    </revision>\n    <revision>\n      <id>1270</id>\n      <parentid>1269</parentid>\n      <timestamp>2005-07-07T15:34:54Z</timestamp>\n      <contributor>\n        <username>Arde~aawiki</username>\n        <id>55</id>\n      </contributor>\n      <model>wikitext</model>\n      <format>text/x-wiki</format>\n      <text xml:space=\"preserve\">abc2</text>\n      <sha1>sicbraevanhzfxqhe5kuhjaj2tj75pd</sha1>\n    </revision>\n</page>");
        //Page: ns=1, title='Main Page2', id=12692, revisions = [Revision: id=12702, username='tester']
        sb.append("<page>\n    <title>Main Page2</title>\n    <ns>1</ns>\n    <id>12692</id>\n    <restrictions>edit=autoconfirmed:move=autoconfirmed</restrictions>\n    <revision>\n      <id>12692</id>\n      <timestamp>2005-07-07T15:31:37Z</timestamp>\n      <contributor>\n        <id>55</id>\n      </contributor>\n      <model>wikitext</model>\n      <format>text/x-wiki</format>\n      <text xml:space=\"preserve\">abc</text>\n      <sha1>sgjoro1gzkwvszypw7metsc9m886zgx</sha1>\n    </revision>\n    <revision>\n      <id>12702</id>\n      <parentid>1269</parentid>\n      <timestamp>2005-07-07T15:34:54Z</timestamp>\n      <contributor>\n        <username>tester</username>\n        <id>56</id>\n      </contributor>\n      <model>wikitext</model>\n      <format>text/x-wiki</format>\n      <text xml:space=\"preserve\">abc2</text>\n      <sha1>sicbraevanhzfxqhe5kuhjaj2tj75pd</sha1>\n    </revision>\n</page>");
        //Page: ns=0, title='Main Page3', id=12693, revisions = [Revision: id=12693, username='test']
        sb.append("<page>\n    <title>Main Page3</title>\n    <ns>0</ns>\n    <id>12693</id>\n    <restrictions>edit=autoconfirmed:move=autoconfirmed</restrictions>\n    <revision>\n      <id>12693</id>\n      <timestamp>2005-07-07T15:31:37Z</timestamp>\n      <contributor>\n        <username>test</username>\n        <id>55</id>\n      </contributor>\n      <model>wikitext</model>\n      <format>text/x-wiki</format>\n      <text xml:space=\"preserve\">abc</text>\n      <sha1>sgjoro1gzkwvszypw7metsc9m886zgx</sha1>\n    </revision>\n    <revision>\n      <id>12703</id>\n      <parentid>1269</parentid>\n      <timestamp>2005-07-07T15:34:54Z</timestamp>\n      <contributor>\n        <id>55</id>\n      </contributor>\n      <model>wikitext</model>\n      <format>text/x-wiki</format>\n      <text xml:space=\"preserve\">abc2</text>\n      <sha1>sicbraevanhzfxqhe5kuhjaj2tj75pd</sha1>\n    </revision>\n</page>");
        sb.append("</mediawiki>");
        //Result should be:
        //Arde~aawiki, 2
        //test, 1
        this.resLine1 = "Arde~aawiki, 2";
        this.resLine2 = "test, 1";

        FileWriter fWriter = new FileWriter(this.XMLDataFile);
        fWriter.write(sb.toString());
        fWriter.flush();
        fWriter.close();

        this.xml = XMLContentHandler.parseXML(this.XMLDataFile.getAbsolutePath());
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
        this.XMLDataFile.delete();
        File outputDir = new File(this.outputFile.getAbsolutePath() + "/");
        for(File f : outputDir.listFiles()){
            f.delete();
        }
        outputDir.delete();
    }
}