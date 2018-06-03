package br.com.newtechs.er.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.newtechs.er.ad.fnCP;
import br.com.newtechs.er.db.DbDefinition;
import br.com.newtechs.er.db.TableDefinition;

/**
 * Created by Paulo on 31/10/2015.
 */
public class LayoutReportDao {

    private SQLiteDatabase database;
    private String [] columns = {
            TableDefinition.LayoutReport._ID,
            TableDefinition.LayoutReport.COLUMN_NAME_REPORT,
            TableDefinition.LayoutReport.COLUMN_NAME_LAYOUT,
            TableDefinition.LayoutReport.COLUMN_NAME_COLPOSITION,
            TableDefinition.LayoutReport.COLUMN_NAME_COLUMN
    };
    private DbDefinition DbCP;

    public LayoutReportDao ( Context context ) {
        DbCP = new DbDefinition(context);
    }

    public void open () throws SQLException {
        database = DbCP.getWritableDatabase();
    }

    public void close () {
        DbCP.close();
    }

    public void create ( LayoutReport layoutReport ) {
        ContentValues values = new ContentValues();
        values.put(TableDefinition.LayoutReport.COLUMN_NAME_REPORT, layoutReport.getReport());
        values.put(TableDefinition.LayoutReport.COLUMN_NAME_LAYOUT, layoutReport.getLayout());
        values.put(TableDefinition.LayoutReport.COLUMN_NAME_COLPOSITION, layoutReport.getColPosition());
        values.put(TableDefinition.LayoutReport.COLUMN_NAME_COLUMN, layoutReport.getColumn());

        long insertId = database.insert(TableDefinition.LayoutReport.TABLE_NAME, null, values);
    }


    public void update(LayoutReport layoutReport) {

        ContentValues values = new ContentValues();
        values.put(TableDefinition.LayoutReport.COLUMN_NAME_COLUMN, layoutReport.getColumn());

        database.update(TableDefinition.LayoutReport.TABLE_NAME, values, TableDefinition.LayoutReport._ID + " = " + layoutReport.getID(), null);
    }

    public void delete(LayoutReport layoutReport ) {
        long id = layoutReport.getID();
        database.delete(TableDefinition.LayoutReport.TABLE_NAME, TableDefinition.LayoutReport._ID + " = " + id, null);
    }

    public void deleteByReport(String reportName) {

        String selection = TableDefinition.LayoutReport.COLUMN_NAME_REPORT + " = ? ";
        String[] args = { reportName };

        database.delete(TableDefinition.LayoutReport.TABLE_NAME , selection , args);
    }

    public void deleteAll() {
        //database.execSQL("DELETE FROM despesas WHERE " + TableDefinition.LayoutReport._ID + " <> 0");
        database.delete(TableDefinition.LayoutReport.TABLE_NAME, TableDefinition.LayoutReport.COLUMN_NAME_REPORT + " <> 0", null);
    }

    public List<LayoutReport> getAll () {

        Cursor cursor = database.query(TableDefinition.LayoutReport.TABLE_NAME,
                columns, null, null, null, null, null);

        //despesas = new ArrayList<Despesa>();
        List<LayoutReport> layoutReports = getListLayoutReport(cursor);

        cursor.close();
        return layoutReports;
    }

    public List<LayoutReport> getByReportLayout (String reportName, String layoutName, String colPosition) {

        String selection = TableDefinition.LayoutReport.COLUMN_NAME_REPORT + " = ? ";
        selection = selection + "AND " + TableDefinition.LayoutReport.COLUMN_NAME_LAYOUT + " = ? ";
        selection = selection + "AND " + TableDefinition.LayoutReport.COLUMN_NAME_COLPOSITION;
        if (colPosition == null) {
            selection = selection + " <> ? ";
        } else {
            selection = selection + " = ? ";
        }

        String[] args = { reportName , layoutName, fnCP.stringNull(colPosition)};

        Cursor cursor = database.query(TableDefinition.LayoutReport.TABLE_NAME,
                columns, selection, args, null, null, null);

        List<LayoutReport> layoutReports = getListLayoutReport(cursor);

        cursor.close();
        return layoutReports;
    }

    private List<LayoutReport> getListLayoutReport (Cursor cursor) {

        List<LayoutReport> layoutReports = new ArrayList<LayoutReport>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            LayoutReport layoutReport = new LayoutReport();

            layoutReport.setID(cursor.getInt(cursor.getColumnIndexOrThrow(TableDefinition.LayoutReport._ID)));
            layoutReport.setReport(cursor.getString(cursor.getColumnIndexOrThrow(TableDefinition.LayoutReport.COLUMN_NAME_REPORT)));
            layoutReport.setLayout(cursor.getString(cursor.getColumnIndexOrThrow(TableDefinition.LayoutReport.COLUMN_NAME_LAYOUT)));
            layoutReport.setColPosition(cursor.getString(cursor.getColumnIndexOrThrow(TableDefinition.LayoutReport.COLUMN_NAME_COLPOSITION)));
            layoutReport.setColumn(cursor.getString(cursor.getColumnIndexOrThrow(TableDefinition.LayoutReport.COLUMN_NAME_COLUMN)));

            layoutReports.add(layoutReport);

            cursor.moveToNext();
        }

        return layoutReports;
    }
}