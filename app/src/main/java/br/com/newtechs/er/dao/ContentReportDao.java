package br.com.newtechs.er.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;
import br.com.newtechs.er.db.DbDefinition;
import br.com.newtechs.er.db.TableDefinition;

/**
 * Created by Paulo on 31/10/2015.
 */
public class ContentReportDao {

    private SQLiteDatabase database;
    private String [] columns = {
            TableDefinition.ContentReport._ID,
            TableDefinition.ContentReport.COLUMN_NAME_REPORT,
            TableDefinition.ContentReport.COLUMN_NAME_LINE,
            TableDefinition.ContentReport.COLUMN_NAME_COLUMN,
            TableDefinition.ContentReport.COLUMN_NAME_VALUE
    };
    private DbDefinition DbCP;

    public ContentReportDao ( Context context ) {
        DbCP = new DbDefinition(context);
    }

    public void open () throws SQLException {
        database = DbCP.getWritableDatabase();
    }

    public void close () {
        DbCP.close();
    }

    public void create ( ContentReport contentReport ) {
        ContentValues values = new ContentValues();
        values.put(TableDefinition.ContentReport.COLUMN_NAME_REPORT, contentReport.getReport());
        values.put(TableDefinition.ContentReport.COLUMN_NAME_LINE, contentReport.getLine());
        values.put(TableDefinition.ContentReport.COLUMN_NAME_COLUMN, contentReport.getColumn());
        values.put(TableDefinition.ContentReport.COLUMN_NAME_VALUE, contentReport.getValue());

        long insertId = database.insert(TableDefinition.ContentReport.TABLE_NAME, null, values);
    }

    public void delete(ContentReport contentReport ) {
        long id = contentReport.getID();
        database.delete(TableDefinition.ContentReport.TABLE_NAME , TableDefinition.ContentReport._ID + " = " + id , null);
    }

    public void deleteByReport(String reportName) {
        database.delete(TableDefinition.ContentReport.TABLE_NAME , TableDefinition.ContentReport.COLUMN_NAME_REPORT + " = '" + reportName + "'", null);
    }

    public void deleteAll() {
        //database.execSQL("DELETE FROM despesas WHERE " + TableDefinition.ContentReport._ID + " <> 0");
        database.delete(TableDefinition.ContentReport.TABLE_NAME , TableDefinition.ContentReport.COLUMN_NAME_REPORT + " <> '0'", null);
    }

    public List<ContentReport> getAll () {

        Cursor cursor = database.query(TableDefinition.ContentReport.TABLE_NAME,
                columns, null, null, null, null, null);

        //despesas = new ArrayList<Despesa>();
        List<ContentReport> contentReports = getListContentReport(cursor);

        cursor.close();
        return contentReports;
    }

    public List<ContentReport> getByReport (String reportName) {

        String selection = TableDefinition.ContentReport.COLUMN_NAME_REPORT + " = ? ";
        String[] selectionArgs = { reportName };

        Cursor cursor = database.query(TableDefinition.ContentReport.TABLE_NAME,
                columns, selection, selectionArgs, null, null, null);

        List<ContentReport> contentReports = getListContentReport(cursor);

        cursor.close();
        return contentReports;
    }

    public ContentReport getByData (String reportName, String line, String column) {

        String selection = TableDefinition.ContentReport.COLUMN_NAME_REPORT + " = ? AND " +
                           TableDefinition.ContentReport.COLUMN_NAME_LINE   + " = ? AND " +
                           TableDefinition.ContentReport.COLUMN_NAME_COLUMN   + " = ? ";

        String[] selectionArgs = { reportName, line, column };

        Cursor cursor = database.query(TableDefinition.ContentReport.TABLE_NAME,
                columns, selection, selectionArgs, null, null, null);

        List<ContentReport> contentReports = getListContentReport(cursor);

        cursor.close();

        if (contentReports.size() > 0) {
            return contentReports.get(0);
        } else {
            return null;
        }
    }


    /*
    public List<ContentReport> getByDate (String dtIni, String dtFim) {

        // Define 'where' part of query.
        String selection = TableDefinition.ContentReport.COLUMN_NAME_DAT_DESPESA + " >= ? AND " +
                TableDefinition.ContentReport.COLUMN_NAME_DAT_DESPESA + " <= ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { dtIni, dtFim };
        // How you want the results sorted in the resulting Cursor
        String sortOrder = TableDefinition.ContentReport.COLUMN_NAME_DAT_DESPESA + " DESC";

        Cursor cursor = database.query(TableDefinition.ContentReport.TABLE_NAME,
                columns, selection, selectionArgs, null, null, sortOrder);

        //despesas = new ArrayList<Despesa>();
        List<Despesa> despesas = getListDespesa(cursor);

        cursor.close();
        return despesas;
    }
    */

    private List<ContentReport> getListContentReport (Cursor cursor) {

        List<ContentReport> contentReports = new ArrayList<ContentReport>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            ContentReport contentReport = new ContentReport();

            contentReport.setID(cursor.getInt(cursor.getColumnIndexOrThrow(TableDefinition.ContentReport._ID)));
            contentReport.setReport(cursor.getString(cursor.getColumnIndexOrThrow(TableDefinition.ContentReport.COLUMN_NAME_REPORT)));
            contentReport.setLine(cursor.getString(cursor.getColumnIndexOrThrow(TableDefinition.ContentReport.COLUMN_NAME_LINE)));
            contentReport.setColumn(cursor.getString(cursor.getColumnIndexOrThrow(TableDefinition.ContentReport.COLUMN_NAME_COLUMN)));
            contentReport.setValue(cursor.getString(cursor.getColumnIndexOrThrow(TableDefinition.ContentReport.COLUMN_NAME_VALUE)));

            contentReports.add(contentReport);

            /*
            Note note = new Note();
            note.setId(cursor.getLong(0));
            note.setNote(cursor.getString(1));
            notes.add(note);
            */

            cursor.moveToNext();
        }

        return contentReports;
    }
}
