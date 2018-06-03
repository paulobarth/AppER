package br.com.newtechs.er.dao;

/**
 * Created by Paulo on 31/10/2015.
 */
public class Layout {

    private Integer ID;
    private String codigo;
    private String qtRow;
    private String qtCol;

    public Integer getID () { return ID; }

    public void setID (Integer ID) { this.ID = ID; }

    public String getCodigo() { return codigo; }

    public void setCodigo (String codigo) {
        this.codigo = codigo;
    }

    public String getQtRow() {
        return qtRow;
    }

    public void setQtRow (String qtRow) {
        this.qtRow = qtRow;
    }

    public String getQtCol() {
        return qtCol;
    }

    public void setQtCol (String qtCol) {
        this.qtCol = qtCol;
    }
}
