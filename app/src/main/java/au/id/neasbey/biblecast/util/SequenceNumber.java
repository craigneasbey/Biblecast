package au.id.neasbey.biblecast.util;

/**
 * Created by craigneasbey on 9/09/15.
 *
 * Provides a unique sequence number
 */
public class SequenceNumber {

    private int number = 0;

    public int get() {
        return number++;
    }
}
