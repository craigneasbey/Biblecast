package au.id.neasbey.biblecast.model;

/**
 * Created by craigneasbey on 21/09/15.
 * <p/>
 * Holds screen dimensions
 */

public class Dimensions {
    private int width;
    private int height;

    public Dimensions() {
        this.width = 0;
        this.height = 0;
    }

    public Dimensions(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Dimensions that = (Dimensions) o;

        if (getWidth() != that.getWidth()) return false;
        return getHeight() == that.getHeight();

    }

    @Override
    public int hashCode() {
        int result = getWidth();
        result = 31 * result + getHeight();
        return result;
    }
}
