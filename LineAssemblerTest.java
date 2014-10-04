package tex61;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/** Unit tests of LineAssemblers.
 *  @author Greg
 */
public class LineAssemblerTest {

    private PageAssembler pagecollector;
    private LineAssembler lineassembler;

    @Before
    public void setup() {
        pagecollector = new PageCollector(new ArrayList<String>());
        lineassembler = new LineAssembler(pagecollector, false);
    }


    @Test
    public void testAddWord1() {
        List<String> tester = new ArrayList<String>();

        lineassembler.addWord("123456789012345678901234567890");
        assertTrue(pagecollector.accessPages().equals(tester));
        lineassembler.addWord("123456789012345678901234567890");
        assertTrue(pagecollector.accessPages().equals(tester));
        lineassembler.addWord("123456789012345678901234567890");

        tester.add("   123456789012345678901234567890"
            + "   123456789012345678901234567890");
        assertTrue(pagecollector.accessPages().equals(tester));
    }

    @Test
    public void testAddWord2() {
        List<String> tester = new ArrayList<String>();
        String str = "I need to write a sufficiently long line to see"
            + " how my lineassembler works. Is this long enough?"
            + " There are 109 nonspace characters. ";
        for (String word : str.split("\\s+")) {
            lineassembler.addWord(word);
        }

        tester.add("   I need to write a sufficiently  long line to see"
            + " how my lineassembler");
        assertEquals(tester, pagecollector.accessPages());

    }

    @Test
    public void testAddWord3() {
        List<String> tester = new ArrayList<String>();
        String str = "The following quotation about writing"
            + " test programs for a document";
        for (String word : str.split("\\s+")) {
            lineassembler.addWord(word);
        }
        lineassembler.addWord("AReallyBigWordThatSpillsToNextLine");

        tester.add("   The following  quotation about writing  test"
            + " programs for  a document");
        assertEquals(tester, pagecollector.accessPages());

    }

    @Test
    public void testAddWord4() {
        String str = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
            + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        String test = "   " + str;
        lineassembler.addWord(str);

        assertEquals(test, pagecollector.accessPages().get(0));
    }

    @Test
    public void testEndParagraph() {
        String str = "hello";
        List<String> test1 = new ArrayList<String>();
        pagecollector.setTextHeight(9);

        for (int i = 0; i < 17; i += 1) {
            lineassembler.addWord(str);
        }
        lineassembler.endParagraph();
        for (int i = 0; i < 17; i += 1) {
            lineassembler.addWord(str);
        }
        lineassembler.endParagraph();
        lineassembler.addWord("Hubba hubba");
        lineassembler.endParagraph();
        lineassembler.addWord("Boo yeah!");
        lineassembler.endParagraph();

        String line1 = "   hello hello  hello hello  hello hello "
            + "hello  hello hello  hello hello";
        String line2 = "hello hello hello hello hello hello";
        String blank = "";
        String line3 = "   Hubba hubba";
        String line4 = "   Boo yeah!";

        test1.add(line1);
        test1.add(line2);
        test1.add(blank);
        test1.add(line1);
        test1.add(line2);
        test1.add(blank);
        test1.add(line3);
        test1.add(blank);
        test1.add(line4);

        assertEquals(test1, pagecollector.accessPages());
    }

    @Test
    public void testNumBlanks() {

        int[] values = { 2, 4, 5, 7, 9, 11, 13, 14, 16, 18 };
        for (int k = 1; k < 11; k += 1) {
        	assertEquals(values[k - 1],
        			lineassembler.numBlanks(k, 18, 11));
        }

    }
}
