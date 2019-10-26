import static org.junit.Assert.*;

import javax.sound.midi.Track;
import org.junit.*;
import junit.framework.TestCase;
import java.lang.*;
import java.beans.Transient;
import java.io.*;

import java.io.InputStream;
import java.io.ByteArrayInputStream;

public class jUnitTests extends TestCase {

    @Test
    public void testCheckEmptyArray() {
        Inspector inspect = new Inspector();
        String [] a = {};
        String [] b = {"1","2","3","4"};
        String [] c = {"100"};

        assertEquals(true, inspect.checkArrayEmpty(a));
        assertEquals(false, inspect.checkArrayEmpty(b));
        assertEquals(false, inspect.checkArrayEmpty(c)); 
    }
    
    @Test
    public void testFormatOutputDepth() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        Inspector inspect = new Inspector();

        inspect.formatOutputDepth("yes", 1);
        assertEquals("\tyes\n", outContent.toString());
    }

    @Test
    public void testGetClass() {
        ClassA a = new ClassA();
        Inspector inspect = new Inspector();

        assertEquals(a.getClass(), inspect.getClass(a));
    }

}