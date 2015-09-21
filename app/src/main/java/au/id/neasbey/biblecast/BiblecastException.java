package au.id.neasbey.biblecast;

/**
 * Created by craigneasbey on 11/08/15.
 *
 * Exception holder for Bible Search API
 */
public class BiblecastException extends Exception {


    /**
     * Constructs a new {@code BiblecastException} with its stack
     * trace filled in.
     */
    public BiblecastException() {
    }

    /**
     * Constructs a new {@code BiblecastException} with its stack
     * trace and detail message filled in.
     *
     * @param detailMessage the detail message for this exception.
     */
    public BiblecastException(String detailMessage) {
        super(detailMessage);
    }
}
