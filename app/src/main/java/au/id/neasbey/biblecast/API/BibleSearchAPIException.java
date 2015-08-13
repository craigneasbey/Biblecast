package au.id.neasbey.biblecast.API;

/**
 * Created by craigneasbey on 11/08/15.
 */
public class BibleSearchAPIException extends Exception {


    /**
     * Constructs a new {@code BibleSearchAPIException} with its stack
     * trace filled in.
     */
    public BibleSearchAPIException() {
    }

    /**
     * Constructs a new {@code BibleSearchAPIException} with its stack
     * trace and detail message filled in.
     *
     * @param detailMessage the detail message for this exception.
     */
    public BibleSearchAPIException(String detailMessage) {
        super(detailMessage);
    }
}
