package tex61;

import java.io.PrintWriter;
import java.util.List;

/** A PageAssembler that sends lines immediately to a PrintWriter, with
 *  terminating newlines.
 *  @author Greg
 */
class PagePrinter extends PageAssembler {

    /** A new PagePrinter that sends lines to PAGES and OUT. */
    PagePrinter(List<String> pages, PrintWriter out) {
        super(pages);
        _out = out;
    }

    /** Print LINE to my output. */
    @Override
    void write(String line) {
        _out.write(line + "\n");
    }

    /** Writes all of the lines to _out. */
    void writeAll() {
        for (String line : _pages) {
            write(line);
        }
    }

    /** This PagePrinter's PrintWriter. */
    private PrintWriter _out;
}
