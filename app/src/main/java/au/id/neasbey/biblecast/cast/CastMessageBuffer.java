package au.id.neasbey.biblecast.cast;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by craigneasbey on 16/10/15.
 * <p/>
 * Prevents performance limitations in cast device by limiting the number of messages sent
 * <p/>
 * Example:
 * <pre class="prettyprint">
 * new CastMessageBuffer(sendSizeThreshold, intervalMilliSec) {
 *
 *     {@literal @}Override
 *     public void concatenateMessages(List<Object> messages) {
 *         int offSet = 0;
 *
 *         for(Object newOffSet : messages) {
 *             offSet += (int)newOffSet;
 *         }
 *
 *         return offSet;
 *     }
 *
 *     {@literal @}Override
 *     public void sendMessage(Object message) {
 *         Log.d(TAG, "sendMessage");
 *     }
 * }.start();
 * </pre>
 */
public abstract class CastMessageBuffer {

    private final String TAG = CastMessageBuffer.class.getName();

    private Timer timer;

    private int sendSizeThreshold;

    private long intervalMilliSec;

    private int sendSize;

    private List<Object> buffer;

    public CastMessageBuffer(int sendSizeThreshold, long intervalMilliSec) {
        this.sendSizeThreshold = sendSizeThreshold;
        this.intervalMilliSec = intervalMilliSec;

        timer = new Timer();
        buffer = new LinkedList<>();
        sendSize = 0;
    }

    /**
     * Schedules the task to run now
     */
    public void start() {
        final int delay = 0;

        timer.scheduleAtFixedRate(new TimerTask() {

            public void run() {
                resetBuffer();
            }
        }, delay, intervalMilliSec);
    }

    public void bufferMessage(Object message) {

        if (sendSize < sendSizeThreshold) {
            //send message
            sendMessage(message);
            sendSize++;
        } else {
            // buffer message
            Log.d(TAG, "buffered: " + message.toString());

            synchronized(buffer) {
                buffer.add(message);
            }
        }
    }

    /**
     * Send buffer and reset, allowing more direct messages again
     */
    public void resetBuffer() {
        beforeBufferReset(sendSize);

        synchronized(buffer) {
            if (!buffer.isEmpty()) {
                Log.d(TAG, "buffer:" + buffer.toString());
                Log.d(TAG, "bufferSize:" + buffer.size());
                Log.d(TAG, "sendSize:" + sendSize);

                sendMessage(concatenateMessages(buffer));
                buffer.clear();
                sendSize = 1;
            } else {
                sendSize = 0;
            }
        }

        afterBufferReset();
    }

    /**
     * Concatenates buffer messages together to reduce messages
     *
     * @param messages Messages in buffer
     * @return Concatenated message
     */
    protected abstract Object concatenateMessages(List<Object> messages);

    /**
     * Send message to cast
     * @param message Message to send
     */
    protected abstract void sendMessage(Object message);

    /**
     * Notifies the caller that the buffer will be reset
     * @param sendSize Amount of messages sent since last reset
     */
    protected void beforeBufferReset(int sendSize) {}

    /**
     * Notifies the caller that the buffer was reset
     */
    protected void afterBufferReset() {}

    /**
     * Cancel the timer
     */
    public void cancel() {
        timer.cancel();
    }
}
