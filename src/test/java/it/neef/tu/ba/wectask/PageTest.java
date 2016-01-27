package it.neef.tu.ba.wectask;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by gehaxelt on 27.01.16.
 */
public class PageTest extends TestCase {

    private Page p;
    private String title = "test page";
    private int id = 1;
    private int ns = 0;

    @Before
    public void setUp() throws Exception {
        this.p = new Page();
        this.p.setTitle(this.title);
        this.p.setId(this.id);
        this.p.setNs(this.ns);
    }

    @Test
    public void testToString() throws Exception {
        String out = this.p.toString();
        assertEquals("Page: " + String.valueOf(this.id) + " ("+String.valueOf(this.ns)+"), " + this.title + ", revs: " + String.valueOf(0), out);
    }
}