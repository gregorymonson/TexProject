package tex61;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/** Receives (partial) words and commands, performs commands, and
 *  accumulates and formats words into lines of text, which are sent to a
 *  designated PageAssembler.  At any given time, a Controller has a
 *  current word, which may be added to by addText, a current list of
 *  words that are being accumulated into a line of text, and a list of
 *  lines of endnotes.
 *  @author Greg
 */
class Controller {

    /** A new Controller that sends formatted output to OUT. */
    Controller(PrintWriter out) {
        _out = out;
        _pagecollector = new PageCollector(new ArrayList<String>());
        _textassembler = new LineAssembler(_pagecollector, false);
        _lineassembler = _textassembler;
        _endnotecollector = new PageCollector(new ArrayList<String>());
        _endnoteassembler = new LineAssembler(_endnotecollector, true);
    }

    /** Add TEXT to the end of the word of formatted text currently
     *  being accumulated. */
    void addText(String text) {
        _lineassembler.addText(text);
    }

    /** Finish any current word of text and, if present, add to the
     *  list of words for the next line.  Has no effect if no unfinished
     *  word is being accumulated. */
    void endWord() {
        _lineassembler.finishWord();
    }

    /** Finish any current word of formatted text and process an end-of-line
     *  according to the current formatting parameters. */
    void addNewline() {
        _lineassembler.newLine();
    }

    /** Finish any current word of formatted text, format and output any
     *  current line of text, and start a new paragraph. */
    void endParagraph() {
        _lineassembler.endParagraph();
    }

    /** If valid, process TEXT into an endnote, first appending a reference
     *  to it to the line currently being accumulated. */
    void formatEndnote(String text) {
        addText(String.format("[%d]", _refNum));

        setEndnoteMode();
        String ref = String.format("[%d] ", _refNum);

        InputParser endnoteParser = new InputParser(text, this, ref);
        endnoteParser.process();

        _refNum += 1;
    }

    /** Set the current text height (number of lines per page) to VAL, if
     *  it is a valid setting.  Ignored when accumulating an endnote. */
    void setTextHeight(int val) {
        if (!_endnoteMode) {
            if (!(Integer.class.isInstance(val)) || val <= 0) {
                throw new FormatException("Value must be a positive integer.");
            }
            _pagecollector.setTextHeight(val);
        }
    }

    /** Set the current text width (width of lines including indentation)
     *  to VAL, if it is a valid setting. */
    void setTextWidth(int val) {
        if (!(Integer.class.isInstance(val)) || val <= 0) {
            throw new FormatException("Value must be a positive integer.");
        }
        _lineassembler.setTextWidth(val);
    }

    /** Set the current text indentation (number of spaces inserted before
     *  each line of formatted text) to VAL, if it is a valid setting. */
    void setIndentation(int val) throws FormatException {
        if (!(Integer.class.isInstance(val)) || val < 0) {
            throw new FormatException("Value must be a positive integer.");
        }
        _lineassembler.setIndentation(val);
    }

    /** Set the current paragraph indentation (number of spaces inserted before
     *  first line of a paragraph in addition to indentation) to VAL, if it is
     *  a valid setting. */
    void setParIndentation(int val) {
        if (!(Integer.class.isInstance(val)) || val < 0) {
            throw new FormatException("Value must be a positive integer.");
        }
        _lineassembler.setParIndentation(val);
    }

    /** Set the current paragraph skip (number of blank lines inserted before
     *  a new paragraph, if it is not the first on a page) to VAL, if it is
     *  a valid setting. */
    void setParSkip(int val) {
        if (!(Integer.class.isInstance(val)) || val < 0) {
            throw new FormatException("Value must be a positive integer.");
        }
        _lineassembler.setParSkip(val);
    }

    /** Iff ON, begin filling lines of formatted text. */
    void setFill(boolean on) {
        _lineassembler.setFill(on);
    }

    /** Iff ON, begin justifying lines of formatted text whenever filling is
     *  also on. */
    void setJustify(boolean on) {
        _lineassembler.setJustify(on);
    }


    /** Finish the current formatted document or ends endnote.
     *  Formats and outputs all pending text. */
    void close() {
        endParagraph();

        if (_endnoteMode) {
            setNormalMode();
        } else {
            List<String> endnotes = _endnotecollector.accessPages();
            for (String line : endnotes) {
                _pagecollector.write(line);
            }
            PagePrinter printer = new
                PagePrinter(_pagecollector.accessPages(), _out);

            printer.writeAll();
        }
    }

    /** Start directing all formatted text to the endnote assembler. */
    private void setEndnoteMode() {
        _endnoteMode = true;
        _lineassembler = _endnoteassembler;
    }

    /** Return to directing all formatted text to _mainText. */
    private void setNormalMode() {
        _endnoteMode = false;
        _lineassembler = _textassembler;
    }


    /** True iff we are currently processing an endnote. */
    private boolean _endnoteMode;

    /** Number of next endnote. */
    private int _refNum = 1;

    /** This Controller's PrintWriter. */
    private PrintWriter _out;

    /** The list of endnote lines. */
    private PageAssembler _endnotecollector;

    /** This controller's pageCollector. */
    private PageAssembler _pagecollector;

    /** This controller's lineAssembler.
    * Switches between _ textassembler and _endnoteassembler.  */
    private LineAssembler _lineassembler;

    /** A line assembler for endnotes. */
    private LineAssembler _endnoteassembler;

    /** The main text LineAssembler. */
    private LineAssembler _textassembler;

    /** This controller's PagePrinter. */
    private PageAssembler _pageprinter;

}

