
import java.io.IOException;
import java.io.Reader;

/**
 * Translating Reader: a stream that is a translation of an
 * existing reader.
 *
 * @author Wen Zeng
 */
public class TrReader extends Reader {
    /**
     * A new TrReader that produces the stream of characters produced
     * by STR, converting all characters that occur in FROM to the
     * corresponding characters in TO.  That is, change occurrences of
     * FROM.charAt(i) to TO.charAt(i), for all i, leaving other characters
     * in STR unchanged.  FROM and TO must have the same length.
     */
    private Reader str;
    private String from;
    private String to;

    public TrReader(Reader str, String from, String to) {
        // TODO: YOUR CODE HERE
        assert from != null;
        assert to != null;
        assert from.length() == to.length();

        this.str = str;
        this.from = from;
        this.to = to;
    }

    /* TODO: IMPLEMENT ANY MISSING ABSTRACT METHODS HERE
     * NOTE: Until you fill in the necessary methods, the compiler will
     *       reject this file, saying that you must declare TrReader
     *       abstract. Don't do that; define the right methods instead!
     */

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {

        int readStr = this.str.read(cbuf, off, len);
        for (int i = off; i < off + len; i++) {
            cbuf[i] = this.map(cbuf[i]);
        }
        return readStr;
    }

    @Override
    public void close() throws IOException {
        str.close();
    }

    private char map(char originstr) {
        int fromoff = this.from.indexOf(originstr);
        if (fromoff == -1) {
            return originstr;
        } else {
            return this.to.charAt(fromoff);
        }
    }

}
