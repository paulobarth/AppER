package br.com.newtechs.er.dao;

/**
 * Created by Paulo on 04/06/2017.
 */
public class ShareReport {

    private Integer ID;
    private String report;
    private String email;

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}