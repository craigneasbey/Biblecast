package au.id.neasbey.biblecast.model;

import java.io.Serializable;

/**
 * Created by craigneasbey on 17/09/15.
 *
 * Example:
 * id: eng-CEV
 * name: Contemporary English Version (US Version)
 * abbreviation": CEV
 */
public class BibleVersion implements Serializable {

    private String id;

    private String name;

    private String abbreviation;

    public BibleVersion() {
    }

    public BibleVersion(String id, String name, String abbreviation) {
        this.id = id;
        this.name = name;
        this.abbreviation = abbreviation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    @Override
    public String toString() {
        return abbreviation;
    }
}
