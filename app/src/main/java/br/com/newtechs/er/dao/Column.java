package br.com.newtechs.er.dao;

import br.com.newtechs.er.ad.fnCP;

/**
 * Created by Paulo on 31/10/2015.
 */
public class Column {

    private Integer ID;
    private String report;
    private String codigo;
    private String label;
    private String param;
    private String filtro;

    private Boolean negrito = false;
    private int size = 8;

    public Integer getID () { return ID; }

    public void setID (Integer ID) { this.ID = ID; }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public String getCodigo() { return codigo; }

    public void setCodigo (String codigo) {
        this.codigo = codigo;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel (String label) {
        this.label = label;
    }

    public String getParam() {
        return param;
    }

    public void setParam (String param) {
        this.param = param;
        splitParam();
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro (String filtro) {
        this.filtro = filtro;
    }

    //Regras especiais para os parametros
    // Posição 1 - Negrito (true or false)
    // Posição 2 - Tamanho da letra

    public Boolean isNegrito () {
        return negrito;
    }

    public void setNegrito (Boolean neg) {
        this.negrito = neg;
        concatenaParam();
    }

    public int getSize () {
        return size;
    }

    public void setSize (int size) {
        this.size = size;
        concatenaParam();
    }

    private void concatenaParam () {
        param = String.valueOf(negrito) + ";" + String.valueOf(size);
    }

    private void splitParam() {

        String[] data = param.split(";");

        if (data.length == 2) {

            negrito = data[0].equals(fnCP.TRUE);
            size = Integer.valueOf(data[1]);
        }
    }

    public int getSizeArrayPos () {

        //Pegar esta sequencia do res/values/leterSize.xml
        int pos = 8;
        int cont = 0;
        while (size > pos) {
            pos = pos + 2;
            cont ++;
        }

        return cont;
    }
}