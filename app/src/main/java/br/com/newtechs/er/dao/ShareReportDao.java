package br.com.newtechs.er.dao;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.newtechs.er.ad.fnCP;
import br.com.newtechs.er.db.DbDefinition;
import br.com.newtechs.er.db.TableDefinition;

/**
 * Created by Paulo on 04/06/2017.
 */
public class ShareReportDao {

    private SQLiteDatabase database;
    private String [] shareReports = {
            TableDefinition.ShareReport._ID,
            TableDefinition.ShareReport.COLUMN_NAME_REPORT,
            TableDefinition.ShareReport.COLUMN_NAME_EMAIL
    };
    private DbDefinition DbCP;
    private long insertId;

    public ShareReportDao ( Context context ) {
        DbCP = new DbDefinition(context);
    }

    public void open () throws SQLException {
        database = DbCP.getWritableDatabase();
    }

    public void close () {
        DbCP.close();
    }

    public void create ( ShareReport shareReport ) {
        ContentValues values = new ContentValues();
        values.put(TableDefinition.ShareReport.COLUMN_NAME_REPORT, shareReport.getReport());
        values.put(TableDefinition.ShareReport.COLUMN_NAME_EMAIL, shareReport.getEmail());

        insertId = database.insert(TableDefinition.ShareReport.TABLE_NAME, null, values);
    }

    public int getInsertId() {
        return (int) insertId;
    }

//    public void update(ShareReport shareReport) {
//
//        ContentValues values = new ContentValues();
//
//        database.update(TableDefinition.ShareReport.TABLE_NAME, values, TableDefinition.ShareReport._ID + " = " + shareReport.getID(), null);
//    }

    public void delete(ShareReport shareReport ) {
        long id = shareReport.getID();
        database.delete(TableDefinition.ShareReport.TABLE_NAME, TableDefinition.ShareReport._ID + " = " + id, null);
    }

    public void deleteByReport(String reportName ) {
        database.delete(TableDefinition.ShareReport.TABLE_NAME, TableDefinition.ShareReport.COLUMN_NAME_REPORT + " = '" + reportName + "'", null);
    }

    public void deleteAll() {
        database.delete(TableDefinition.ShareReport.TABLE_NAME, TableDefinition.ShareReport.COLUMN_NAME_REPORT + " <> 0", null);
    }

    public List<ShareReport> getAll () {

        Cursor cursor = database.query(TableDefinition.ShareReport.TABLE_NAME,
                shareReports, null, null, null, null, null);

        //despesas = new ArrayList<Despesa>();
        List<ShareReport> shareReports = getListShareReport(cursor);

        cursor.close();
        return shareReports;
    }

    public List<ShareReport> getByReport (String reportName) {

        String selection = TableDefinition.ShareReport.COLUMN_NAME_REPORT + " = ? ";

        String[] args = { reportName };

        Cursor cursor = database.query(TableDefinition.ShareReport.TABLE_NAME,
                shareReports, selection, args, null, null, null);

        List<ShareReport> shareReports = getListShareReport(cursor);

        cursor.close();
        return shareReports;
    }

    public List<ShareReport> getByEmail (String email) {

        String selection = TableDefinition.ShareReport.COLUMN_NAME_EMAIL + " = ? ";

        String[] args = { email };

        Cursor cursor = database.query(TableDefinition.ShareReport.TABLE_NAME,
                shareReports, selection, args, null, null, null);

        List<ShareReport> shareReports = getListShareReport(cursor);

        cursor.close();
        return shareReports;
    }

    public Boolean emailReportExists (ShareReport shareReport) {

        String selection = TableDefinition.ShareReport.COLUMN_NAME_REPORT + " = ? AND " +
                           TableDefinition.ShareReport.COLUMN_NAME_EMAIL  + " = ? ";

        String[] args = { shareReport.getReport(), shareReport.getEmail() };

        Cursor cursor = database.query(TableDefinition.ShareReport.TABLE_NAME,
                shareReports, selection, args, null, null, null);

        List<ShareReport> shareReports = getListShareReport(cursor);

        cursor.close();
        if (shareReports.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    private List<ShareReport> getListShareReport (Cursor cursor) {

        List<ShareReport> shareReports = new ArrayList<ShareReport>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            ShareReport shareReport = new ShareReport();

            shareReport.setID(cursor.getInt(cursor.getColumnIndexOrThrow(TableDefinition.ShareReport._ID)));
            shareReport.setReport(cursor.getString(cursor.getColumnIndexOrThrow(TableDefinition.ShareReport.COLUMN_NAME_REPORT)));
            shareReport.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(TableDefinition.ShareReport.COLUMN_NAME_EMAIL)));

            shareReports.add(shareReport);

            cursor.moveToNext();
        }

        return shareReports;
    }

}