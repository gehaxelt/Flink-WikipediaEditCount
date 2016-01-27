package it.neef.tu.ba.wectask;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by gehaxelt on 27.01.16.
 */
public class RevisionTest extends TestCase {

    private Revision rev;
    private String username = "foobar";
    private int id = 1337;

    @Before
    public void setUp() throws Exception {
        this.rev = new Revision();
        this.rev.setUsername(this.username);
        this.rev.setId(this.id);
    }

    @Test
    public void testToString() throws Exception {
        String out = this.rev.toString();
        assertEquals("Revision: "+String.valueOf(this.id)+", "+this.username, out);
    }
}