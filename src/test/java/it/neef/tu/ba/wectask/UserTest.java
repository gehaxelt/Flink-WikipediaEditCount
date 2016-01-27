package it.neef.tu.ba.wectask;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by gehaxelt on 27.01.16.
 */
public class UserTest extends TestCase {

    private User user;
    private String username = "foobar";

    @Before
    public void setUp() throws Exception {
        this.user = new User(this.username);
    }

    @Test
    public void testToString() throws Exception {
        String out = this.user.toString();
        assertEquals("User: "+this.username+", 0", out);
    }
}