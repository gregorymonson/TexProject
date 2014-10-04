package tex61;

import java.util.List;

/** A PageAssembler that collects its lines into a designated List.
 *  @author Greg
 */
class PageCollector extends PageAssembler {

    /** A new PageCollector that stores lines in PAGES (protected). */
    PageCollector(List<String> pages) {
        super(pages);
    }

    /** Add LINE to my List. */
    @Override
    void write(String line) {
        addLine(line);
    }

}
