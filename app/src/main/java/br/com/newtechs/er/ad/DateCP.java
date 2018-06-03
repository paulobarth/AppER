package br.com.newtechs.er.ad;


/**
 * Created by Paulo on 25/10/2015.
 */
public final class DateCP {

    private String dtSQL = "";
    private String dtScreen = "";
    private fnCP fnCP = new fnCP(null);

    public void setSQLDate(String dt) {
        dtSQL = dt;

        dtScreen = fnCP.dateStringFormat(dt);
    }

    public void setStringDate(String dt) {
        dtScreen = dt;

        dtSQL = fnCP.dateSQLFormat(dt);
    }

    public String toSQLDate() {

        return dtSQL;
    }

    public String toString() {

        return dtScreen;
    }
}
