package tex61;

import java.util.ArrayList;
import java.util.List;


/** An object that receives a sequence of words of text and formats
 *  the words into filled and justified text lines that are sent to a receiver.
 *  @author Greg
 */
class LineAssembler {

    /** A new, empty line assembler with default settings of all
     *  parameters, sending finished lines to PAGECOLLECTOR.
     *  ENDNOTE true iff we are in endnote mode. */
    LineAssembler(PageAssembler pagecollector, boolean endnote) {
        _pagecollector = pagecollector;
        if (endnote) {
            _textWidth = Defaults.ENDNOTE_TEXT_WIDTH;
            _parSkip = Defaults.ENDNOTE_PARAGRAPH_SKIP;
            _parIndentation = Defaults.ENDNOTE_PARAGRAPH_INDENTATION;
            _indentation = Defaults.ENDNOTE_INDENTATION;
        } else {
            _textWidth = Defaults.TEXT_WIDTH;
            _parSkip = Defaults.PARAGRAPH_SKIP;
            _parIndentation = Defaults.PARAGRAPH_INDENTATION;
            _indentation = Defaults.INDENTATION;
        }
    }

    /** Add TEXT to the word currently being built. */
    void addText(String text) {
        _word = _word + text;
    }

    /** Finish the current word, if any, and add to words being accumulated. */
    void finishWord() {
        if (!_word.equals("")) {
            addWord(_word);
            _word = "";
        }
    }

    /** Add WORD to the formatted text. */
    void addWord(String word) {
        int ind = 0;
        int spc = _words.size() - 1;
        int lngth = lineLength(_words);

        if (_fill) {
            if (_newParagraph) {
                ind = _indentation + _parIndentation;
            } else {
                ind = _indentation;
            }

            lngth = lngth + ind;
            if ((_textWidth < lngth + word.length() + 1 + spc)
                    & _words.size() > 0) {
                if (_fill & _justify) {
                    spc = Math.min(_textWidth - lngth, 3 * spc);
                }


                emitLine(ind, spc);
                _words.add(word);


            } else if ((_textWidth < lngth + word.length() + 1 + spc)
                & _words.size() == 0) {
                _words.add(word);

                emitLine(ind, 0);

            } else {
                _words.add(word);
            }
        } else {
            _words.add(word);
        }


    }

    /** Add LINE to our output, with no preceding paragraph skip.  There must
     *  not be an unfinished line pending. */
    void addLine(String line) {
        _pagecollector.addLine(line);
    }

    /** Set the current indentation to VAL. VAL >= 0. */
    void setIndentation(int val) {
        _indentation = val;
    }

    /** Set the current paragraph indentation to VAL. VAL >= 0. */
    void setParIndentation(int val) {
        _parIndentation = val;
    }

    /** Set the text width to VAL, where VAL >= 0. */
    void setTextWidth(int val) {
        _textWidth = val;
    }

    /** Iff ON, set fill mode. */
    void setFill(boolean on) {
        _fill = on;
    }

    /** Iff ON, set justify mode (which is active only when filling is
     *  also on). */
    void setJustify(boolean on) {
        _justify = on;
    }

    /** Set paragraph skip to VAL.  VAL >= 0. */
    void setParSkip(int val) {
        _parSkip = val;
    }

    /** Process the end of the current input line.  No effect if
     *  current line accumulator is empty or in fill mode.  Otherwise,
     *  adds a new complete line to the finished line queue and clears
     *  the line accumulator. */
    void newLine() {
        if (!_fill & _words.size() > 0) {
            int spaces = _words.size() - 1;
            int ind = _indentation;
            if (_newParagraph) {
                ind += _parIndentation;
            }
            emitLine(ind, spaces);
        }
    }

    /** If there is a current unfinished paragraph pending, close it
     *  out and start a new one. */
    void endParagraph() {

        finishWord();
        outputLast();

        _newParagraph = true;

    }

    /** Transfer contents of _words to _pageassembler, adding INDENT characters
     *  of indentation, and a total of SPACES spaces between words, evenly
     *  distributed.  Assumes _words is not empty.  Clears _words. */
    private void emitLine(int indent, int spaces) {
        if (_newParagraph && !_firstline) {
            for (int i = 0; i < _parSkip; i += 1) {
                addLine(null);
            }

        }
        _firstline = false;
        _newParagraph = false;

        int numWords = _words.size();

        String updated = addSpaces(_words.get(0), indent);
        _words.remove(0);
        _words.add(0, updated);
        int sp;

        for (int i = 1; i < numWords; i += 1) {
            sp = numBlanks(i, spaces, numWords)
                - numBlanks(i - 1, spaces, numWords);
            updated = addSpaces(_words.get(i), sp);
            _words.remove(i);
            _words.add(i, updated);
        }

        String line = "";
        for (String word : _words) {
            line += word;
        }

        _pagecollector.write(line);
        _words.clear();
    }

    /** Returns the number of blanks between words 0 and K.
     * B is the total number of blanks, N the number of words. */
    int numBlanks(int k, int b, int n) {
    	double kk = k;
    	double bb = b;
    	double nn = n;
    	double dbl = .5 + ((kk * bb) / (nn - 1));
    	return (int) dbl;
    }

    /** Outputs the last line of a paragraph. */
    void outputLast() {
        if (_words.size() > 0) {
            int ind = 0;
            int sp = _words.size() - 1;
            ind += _indentation;

            if (_newParagraph) {
                ind += _parIndentation;
            }

            emitLine(ind, sp);
        }
    }


    /** Returns the number of characters of the Strings in WORDS. */
    private int lineLength(List<String> words) {
        int count = 0;
        for (String word : words) {
            count += word.length();
        }
        return count;
    }

    /** Returns a string with N spaces added to beginning of STR,
     * or STR if N < 1. */
    private String addSpaces(String str, int n) {
        while (n > 0) {
            str = " " + str;
            n -= 1;
        }
        return str;
    }

    /** Destination given in constructor for formatted lines. */
    private final PageAssembler _pagecollector;

    /** The current word. */
    private String _word = "";

    /** True iff we are doing the first line of a paragraph. */
    private boolean _newParagraph = true;

    /** True iff this is the first line of the document. */
    private boolean _firstline = true;

    /** List of words to be compiled to a line. */
    private List<String> _words = new ArrayList<String>();

    /** True iff fill mode is on. */
    private boolean _fill = true;

    /** True iff justify mode is on. */
    private boolean _justify = true;

    /** The current text Width. */
    private int _textWidth = Defaults.TEXT_WIDTH;

    /** The current text indentation. */
    private int _indentation = Defaults.INDENTATION;

    /** The current text paragraph indentation. */
    private int _parIndentation = Defaults.PARAGRAPH_INDENTATION;

    /** The current text paragraph skip. */
    private int _parSkip = Defaults.PARAGRAPH_SKIP;

}
