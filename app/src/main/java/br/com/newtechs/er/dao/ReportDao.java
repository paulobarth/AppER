package br.com.newtechs.er.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.google.common.collect.Table;

import java.sql.SQLData;
import java.util.ArrayList;
import java.util.List;

import br.com.newtechs.er.ad.fnCP;
import br.com.newtechs.er.db.DbDefinition;
import br.com.newtechs.er.db.TableDefinition;

/**
 * Created by Paulo on 31/10/2015.
 */
public class ReportDao {

    private SQLiteDatabase database;
    private String [] columns = {
            TableDefinition.Report._ID,
            TableDefinition.Report.COLUMN_NAME_CODIGO,
            TableDefinition.Report.COLUMN_NAME_NOME,
            TableDefinition.Report.COLUMN_NAME_TIPO_ORIGEM,
            TableDefinition.Report.COLUMN_NAME_ORIGEM_DADOS,
            TableDefinition.Report.COLUMN_NAME_FORMATO_ORIGEM,
            TableDefinition.Report.COLUMN_NAME_TIPO_ATZ,
            TableDefinition.Report.COLUMN_NAME_LAYOUT,
            TableDefinition.Report.COLUMN_NAME_VISIVEL,
            TableDefinition.Report.COLUMN_NAME_USER_LOGIN_COL,
            TableDefinition.Report.COLUMN_NAME_USER_ID,
            TableDefinition.Report.COLUMN_NAME_SHARED
    };
    private DbDefinition DbCP;
    private String userID;

    public ReportDao ( Context context ) {
        DbCP = new DbDefinition(context);
        userID = fnCP.getUserCode(fnCP.getLoggedUser());
    }

    public void open () throws SQLException {
        database = DbCP.getWritableDatabase();
    }

    public void close () {
        DbCP.close();
    }

    public void create ( Report report ) {
        ContentValues values = new ContentValues();

        String codigo = report.getCodigo().replace("-" + userID, "");
        values.put(TableDefinition.Report.COLUMN_NAME_CODIGO, codigo + "-" + userID);
        values.put(TableDefinition.Report.COLUMN_NAME_NOME, report.getNome());
        values.put(TableDefinition.Report.COLUMN_NAME_TIPO_ORIGEM, report.getTipoOrigem());
        values.put(TableDefinition.Report.COLUMN_NAME_ORIGEM_DADOS, report.getOrigemDados());
        values.put(TableDefinition.Report.COLUMN_NAME_FORMATO_ORIGEM, report.getFormatoOrigem());
        values.put(TableDefinition.Report.COLUMN_NAME_TIPO_ATZ, report.getTipoAtz());
        values.put(TableDefinition.Report.COLUMN_NAME_LAYOUT, report.getLayout());
        values.put(TableDefinition.Report.COLUMN_NAME_VISIVEL, report.getVisivel());
        values.put(TableDefinition.Report.COLUMN_NAME_USER_LOGIN_COL, report.getUserLoginCol());
        values.put(TableDefinition.Report.COLUMN_NAME_USER_ID, userID);
        values.put(TableDefinition.Report.COLUMN_NAME_SHARED, report.getShared());

        long insertId = database.insert(TableDefinition.Report.TABLE_NAME, null, values);
    }

    public void update(Report report) {

        String userID = fnCP.getUserCode(fnCP.getLoggedUser());

        ContentValues values = new ContentValues();
        String codigo = report.getCodigo().replace("-" + userID, "");
        values.put(TableDefinition.Report.COLUMN_NAME_CODIGO, codigo + "-" + userID);
        values.put(TableDefinition.Report.COLUMN_NAME_TIPO_ORIGEM, report.getTipoOrigem());
        values.put(TableDefinition.Report.COLUMN_NAME_ORIGEM_DADOS, report.getOrigemDados());
        values.put(TableDefinition.Report.COLUMN_NAME_FORMATO_ORIGEM, report.getFormatoOrigem());
        values.put(TableDefinition.Report.COLUMN_NAME_TIPO_ATZ, report.getTipoAtz());
        values.put(TableDefinition.Report.COLUMN_NAME_LAYOUT, report.getLayout());
        values.put(TableDefinition.Report.COLUMN_NAME_VISIVEL, report.getVisivel());
        values.put(TableDefinition.Report.COLUMN_NAME_USER_LOGIN_COL, report.getUserLoginCol());
        values.put(TableDefinition.Report.COLUMN_NAME_USER_ID, userID);
        values.put(TableDefinition.Report.COLUMN_NAME_SHARED, report.getShared());

        database.update(TableDefinition.Report.TABLE_NAME, values, TableDefinition.Report._ID + " = " + report.getID(), null);
    }

    public void delete(Report report ) {
        long id = report.getID();
        database.delete(TableDefinition.Report.TABLE_NAME , TableDefinition.Report._ID + " = " + id , null);
    }

    public void deleteAll() {
        //database.execSQL("DELETE FROM despesas WHERE " + TableDefinition.Report._ID + " <> 0");
        database.delete(TableDefinition.Report.TABLE_NAME , TableDefinition.Report.COLUMN_NAME_CODIGO + " <> 0", null);
    }

    public List<Report> getAll () {

        Cursor cursor = database.query(TableDefinition.Report.TABLE_NAME,
                columns, null, null, null, null, null);

        List<Report> reports = getListReport(cursor);

        cursor.close();
        return reports;
    }

    public List<Report> getAllByUserID () {

        String selection = TableDefinition.Report.COLUMN_NAME_USER_ID + " = ? ";
        String[] args = { userID };

        Cursor cursor = database.query(TableDefinition.Report.TABLE_NAME,
                columns, selection, args, null, null, null);

        List<Report> reports = getListReport(cursor);

        cursor.close();
        return reports;
    }

    public List<Report> getAllToSinc () {

        String selection = TableDefinition.Report.COLUMN_NAME_USER_ID + " = ? AND " +
                           TableDefinition.Report.COLUMN_NAME_SHARED  + " = ? ";

        String[] args = { userID, "0" };

        Cursor cursor = database.query(TableDefinition.Report.TABLE_NAME,
                columns, selection, args, null, null, null);

        List<Report> reports = getListReport(cursor);

        cursor.close();
        return reports;
    }
    public Report getReport (String reportName) {

        String selection = TableDefinition.Report.COLUMN_NAME_CODIGO + " = ? ";
        String[] args = { reportName + "-" + userID };

        Cursor cursor = database.query(TableDefinition.Report.TABLE_NAME,
                columns, selection, args, null, null, null);

        List<Report> reports = getListReport(cursor);

        cursor.close();
        if (reports.size() > 0) {
            return reports.get(0);
        } else {
            return null;
        }
    }

    public List<Report> getReportByUserID (String userID) {

        String selection = TableDefinition.Report.COLUMN_NAME_USER_ID + " = ? ";
        String[] args = { userID };

        Cursor cursor = database.query(TableDefinition.Report.TABLE_NAME,
                columns, selection, args, null, null, null);

        List<Report> reports = getListReport(cursor);

        cursor.close();
        return reports;
    }

    private List<Report> getListReport (Cursor cursor) {

        List<Report> reports = new ArrayList<Report>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            Report report = new Report();

            report.setID(cursor.getInt(cursor.getColumnIndexOrThrow(TableDefinition.Report._ID)));
            report.setCodigo(cursor.getString(cursor.getColumnIndexOrThrow(TableDefinition.Report.COLUMN_NAME_CODIGO)));
            report.setNome(cursor.getString(cursor.getColumnIndexOrThrow(TableDefinition.Report.COLUMN_NAME_NOME)));
            report.setTipoOrigem(cursor.getString(cursor.getColumnIndexOrThrow(TableDefinition.Report.COLUMN_NAME_TIPO_ORIGEM)));
            report.setOrigemDados(cursor.getString(cursor.getColumnIndexOrThrow(TableDefinition.Report.COLUMN_NAME_ORIGEM_DADOS)));
            report.setFormatoOrigem(cursor.getString(cursor.getColumnIndexOrThrow(TableDefinition.Report.COLUMN_NAME_FORMATO_ORIGEM)));
            report.setTipoAtz(cursor.getString(cursor.getColumnIndexOrThrow(TableDefinition.Report.COLUMN_NAME_TIPO_ATZ)));
            report.setLayout(cursor.getString(cursor.getColumnIndexOrThrow(TableDefinition.Report.COLUMN_NAME_LAYOUT)));
            report.setVisivel(cursor.getString(cursor.getColumnIndexOrThrow(TableDefinition.Report.COLUMN_NAME_VISIVEL)));
            report.setUserLoginCol(cursor.getString(cursor.getColumnIndexOrThrow(TableDefinition.Report.COLUMN_NAME_USER_LOGIN_COL)));
            report.setUserID(cursor.getString(cursor.getColumnIndexOrThrow(TableDefinition.Report.COLUMN_NAME_USER_ID)));
            if (cursor.getString(cursor.getColumnIndexOrThrow(TableDefinition.Report.COLUMN_NAME_SHARED)) != null) {
                report.setShared(cursor.getString(cursor.getColumnIndexOrThrow(TableDefinition.Report.COLUMN_NAME_SHARED)).equals("1"));
            }

            reports.add(report);

            cursor.moveToNext();
        }

        return reports;
    }
}
