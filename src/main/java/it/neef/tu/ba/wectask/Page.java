package it.neef.tu.ba.wectask;

import java.util.ArrayList;

/**
 * Created by gehaxelt on 17.01.16.
 */
public class Page {
    /**
     * Page title
     */
    private String title;
    /**
     * Page ID
     */
    private int id;
    /**
     * Page namespace
     */
    private int ns;
    /**
     * Page's revisions
     */
    private ArrayList<Revision> revisions = new ArrayList<Revision>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNs() {
        return ns;
    }

    public void setNs(int ns) {
        this.ns = ns;
    }

    public ArrayList<Revision> getRevisions() {
        return revisions;
    }

    public void setRevisions(ArrayList<Revision> revisions) {
        this.revisions = revisions;
    }

    @Override
    public String toString() {
        return "Page: " + String.valueOf(this.id) + " ("+String.valueOf(this.ns)+"), " + this.title + ", revs: " + String.valueOf(revisions.size());
    }
}
