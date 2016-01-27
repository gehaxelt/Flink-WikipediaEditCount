package it.neef.tu.ba.wectask;

import junit.framework.TestCase;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;

/**
 * Created by gehaxelt on 19.01.16.
 */
public class XMLContentHandlerTest extends TestCase {

    private File XMLDataFile;
    private XMLContentHandler xml;

    public void testInvalidFile() {
        XMLContentHandler xml = XMLContentHandler.parseXML(this.XMLDataFile.getAbsolutePath() + ".notexistant");
        assertNull(xml);
    }

    public void testParsedPage() {
        if(this.xml == null) {
            fail("Parsing failed");
        }
        Page p = new Page();
        p.setId(1269);
        p.setNs(0);
        p.setTitle("Main Page");

        ArrayList<Page> pages = this.xml.getAllPages();
        assertEquals(1, pages.size());
        Page out = pages.get(0);

        assertEquals(p.getId(), out.getId());
        assertEquals(p.getNs(), out.getNs());
        assertEquals(p.getTitle(), out.getTitle());
    }

    public void testParsedRevisions() {
        if(this.xml == null) {
            fail("Parsing failed");
        }
        ArrayList<Page> pages = this.xml.getAllPages();

        Page p = new Page();
        p.setId(1269);
        p.setNs(0);
        p.setTitle("Main Page");

        assertEquals(1, pages.size());
        Page out = pages.get(0);

        assertEquals(p.getId(), out.getId());
        assertEquals(p.getNs(), out.getNs());
        assertEquals(p.getTitle(), out.getTitle());

        ArrayList<Revision> revisions = out.getRevisions();
        assertEquals(2, revisions.size());

        Revision rev1 = new Revision();
        rev1.setId(1269);
        rev1.setUsername("Arde~aawiki");

        Revision rev2 = new Revision();
        rev2.setId(2818);
        rev2.setUsername("Escarbot");

        Revision r1 = revisions.get(0);
        Revision r2 = revisions.get(1);

        assertEquals(rev1.getId(), r1.getId());
        assertEquals(rev1.getUsername(), r1.getUsername());

        assertEquals(rev2.getId(), r2.getId());
        assertEquals(rev2.getUsername(), r2.getUsername());
    }

    public void testStringPageParsing() {
        File tmp = FileUtils.getFile("src","test", "resources", "xmlcontenthandlertest-page-dump.xml");
        String data = null;
        try {
            data = new String(Files.readAllBytes(tmp.toPath()));
        } catch (IOException e) {
            fail("Reading file failed");
        }

        ArrayList<Page> pages = XMLContentHandler.parseXMLString(data);

        assertEquals(1, pages.size());
        Page out = pages.get(0);

        ArrayList<Revision> revisions = out.getRevisions();
        assertEquals(2, revisions.size());

        Revision rev1 = new Revision();
        rev1.setId(1269);
        rev1.setUsername("Arde~aawiki");

        Revision rev2 = new Revision();
        rev2.setId(2818);
        rev2.setUsername("Escarbot");

        Revision r1 = revisions.get(0);
        Revision r2 = revisions.get(1);

        assertEquals(rev1.getId(), r1.getId());
        assertEquals(rev1.getUsername(), r1.getUsername());

        assertEquals(rev2.getId(), r2.getId());
        assertEquals(rev2.getUsername(), r2.getUsername());

    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        this.XMLDataFile = FileUtils.getFile("src","test", "resources", "xmlcontenthandlertest-test-dump.xml");
        this.xml = XMLContentHandler.parseXML(this.XMLDataFile.getAbsolutePath());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}