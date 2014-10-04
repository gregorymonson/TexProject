package tex61;

import java.util.List;

/** A PageAssembler accepts complete lines of text (minus any
 *  terminating newlines) and turns them into pages, adding form
 *  feeds as needed.  It prepends a form feed (Control-L  or ASCII 12)
 *  to the first line of each page after the first.  By overriding the
 *  'write' method, subtypes can determine what is done with
 *  the finished lines.
 *  @author Greg
 */
abstract class PageAssembler {

    /** Create a new PageAssembler that sends its output to OUT.
     *  Initially, its text height is unlimited. It prepends a form
     *  feed character to the first line of each page except the first.
     *  PAGES is the list of lines. */
    PageAssembler(List<String> pages) {
        _pages = pages;
    }

    /** Add LINE to the current page, starting a new page with it if
     *  the previous page is full. A null LINE indicates a skipped line,
     *  and has no effect at the top of a page. */
    void addLine(String line) {
        if (line != null && !line.isEmpty()) {
            if (_textHeight == -1 || _numLines < _textHeight) {
                _pages.add(line);
                _numLines += 1;
            } else {
                _pages.add("\f" + line);
                _numLines = 1;
            }
        } else if (_numLines != _textHeight) {
            _pages.add("");
            _numLines += 1;
        }
    }

    /** Set text height to VAL, where VAL > 0. */
    void setTextHeight(int val) {
        _textHeight = val;
    }

    /** Returns _pages. */
    List<String> accessPages() {
        return _pages;
    }

    /** Perform final disposition of LINE, as determined by the
     *  concrete subtype. */
    abstract void write(String line);

    /** The current pages. */
    protected List<String> _pages;

    /** The number of lines written on the current page. */
    private int _numLines = 0;

    /** The current text Height. */
    private int _textHeight = -1;
}
