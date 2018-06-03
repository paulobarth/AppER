package br.com.newtechs.er.dao;

/**
 * Created by Paulo on 31/10/2015.
 */
public class LayoutReport {

    private Integer ID;
    private String report;
    private String layout;
    private String colPosition;
    private String column;

    public Integer getID () { return ID; }

    public void setID (Integer ID) { this.ID = ID; }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public String getLayout() { return layout; }

    public void setLayout (String layout) {
        this.layout = layout;
    }

    public String getColPosition() {
        return colPosition;
    }

    public void setColPosition (String colPosition) {
        this.colPosition = colPosition;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn (String column) {
        this.column = column;
    }

}
