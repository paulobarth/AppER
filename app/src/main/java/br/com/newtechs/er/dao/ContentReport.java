package br.com.newtechs.er.dao;

/**
 * Created by Paulo on 31/10/2015.
 */
public class ContentReport {

    private Integer ID;
    private String report;
    private String line;
    private String column;
    private String value;

    public Integer getID () { return ID; }

    public void setID (Integer ID) { this.ID = ID; }

    public String getReport() {
        return report;
    }

    public void setReport (String report) {
        this.report = report;
    }

    public String getLine() {
        return line;
    }

    public void setLine (String line) {
        this.line = line;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn (String column) {
        this.column = column;
    }

    public String getValue() {
        return value;
    }

    public void setValue (String value) {
        this.value = value;
    }
}
