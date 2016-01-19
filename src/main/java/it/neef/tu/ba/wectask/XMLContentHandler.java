package it.neef.tu.ba.wectask;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import java.util.ArrayList;

/**
 * Created by gehaxelt on 17.01.16.
 */
public class XMLContentHandler implements ContentHandler {

    /**
     * List of all parsed pages.
     */
    private ArrayList<Page> allPages = new ArrayList<Page>();
    /**
     * Value between the current tag.
     */
    private String cValue;
    /**
     * Current <page>-tag we are working on.
     */
    private Page cPage;
    /**
     * Current <revision>-tag we are working on.
     */
    private Revision cRevision;

    /**
     * Switching modes for inner tag parsing.
     * NONE: We're not parsing a specific tag.
     * PAGE: We're insinde a <page>-tag
     * REVISION: We're inside a <revision>-tag
     * CONTRIBUTOR: We're inside a <contributor>-tag.
     */
    private enum MODES { NONE, PAGE, REVISION, CONTRIBUTOR };

    /**
     * Current parsing mode.
     */
    private MODES cMode;

    @Override
    public void setDocumentLocator(Locator locator) {

    }

    @Override
    public void startDocument() throws SAXException {
        //Start without a specific mode.
        this.cMode = MODES.NONE;
    }

    @Override
    public void endDocument() throws SAXException {

    }

    @Override
    public void startPrefixMapping(String s, String s1) throws SAXException {

    }

    @Override
    public void endPrefixMapping(String s) throws SAXException {

    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        switch(qName) {
            case "page":
                this.cPage = new Page();
                this.cMode = MODES.PAGE;
                break;
            case "revision":
                this.cRevision = new Revision();
                this.cMode = MODES.REVISION;
                break;
            case "contributor":
                this.cMode = MODES.CONTRIBUTOR;
                break;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

        switch(this.cMode) {
            case PAGE:
                switch (qName) {
                    case "id":
                        this.cPage.setId(Integer.valueOf(this.cValue));
                        break;
                    case "title":
                        this.cPage.setTitle(this.cValue);
                        break;
                    case "ns":
                        this.cPage.setNs(Integer.valueOf(this.cValue));
                        break;
                    case "page":
                        this.allPages.add(this.cPage);
                        this.cMode = MODES.NONE;
                        break;
                }
                break;
            case REVISION:
                switch (qName) {
                    case "id":
                        this.cRevision.setId(Integer.valueOf(this.cValue));
                        break;
                    case "revision":
                        //Skip revisions without an username.
                        if(this.cRevision.getUsername() != null) {
                            this.cPage.getRevisions().add(this.cRevision);
                        }
                        this.cMode = MODES.PAGE;
                        break;
                }
                break;
            case CONTRIBUTOR:
                switch (qName) {
                    case "username":
                        this.cRevision.setUsername(this.cValue);
                        break;
                    case "contributor":
                        this.cMode = MODES.REVISION;
                }
        }
    }

    @Override
    public void characters(char[] chars, int i, int i1) throws SAXException {
        //Read content between tags as String.
        this.cValue = new String(chars, i, i1);
    }

    @Override
    public void ignorableWhitespace(char[] chars, int i, int i1) throws SAXException {

    }

    @Override
    public void processingInstruction(String s, String s1) throws SAXException {

    }

    @Override
    public void skippedEntity(String s) throws SAXException {

    }

    public ArrayList<Page> getAllPages() {
        return allPages;
    }

    public void setAllPages(ArrayList<Page> allPages) {
        this.allPages = allPages;
    }
}
