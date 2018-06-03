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
public class ColumnDao {

    private SQLiteDatabase database;
    private String [] columns = {
            TableDefinition.Column._ID,
            TableDefinition.Column.COLUMN_NAME_REPORT,
            TableDefinition.Column.COLUMN_NAME_CODIGO,
            TableDefinition.Column.COLUMN_NAME_LABEL,
            TableDefinition.Column.COLUMN_NAME_PARAM,
            TableDefinition.Column.COLUMN_NAME_FILTRO
    };
    private DbDefinition DbCP;

    public ColumnDao ( Context context ) {
        DbCP = new DbDefinition(context);
    }

    public void open () throws SQLException {
        database = DbCP.getWritableDatabase();
    }

    public void close () {
        DbCP.close();
    }

    public void create ( Column column ) {
        ContentValues values = new ContentValues();
        values.put(TableDefinition.Column.COLUMN_NAME_REPORT, column.getReport());
        values.put(TableDefinition.Column.COLUMN_NAME_CODIGO, column.getCodigo());
        values.put(TableDefinition.Column.COLUMN_NAME_LABEL, column.getLabel());
        values.put(TableDefinition.Column.COLUMN_NAME_PARAM, column.getParam());
        values.put(TableDefinition.Column.COLUMN_NAME_FILTRO, column.getFiltro());

        long insertId = database.insert(TableDefinition.Column.TABLE_NAME, null, values);
    }


    public void update(Column column) {

        ContentValues values = new ContentValues();
        values.put(TableDefinition.Column.COLUMN_NAME_CODIGO, column.getCodigo());
        values.put(TableDefinition.Column.COLUMN_NAME_LABEL, column.getLabel());
        values.put(TableDefinition.Column.COLUMN_NAME_PARAM, column.getParam());
        values.put(TableDefinition.Column.COLUMN_NAME_FILTRO, column.getFiltro());

        database.update(TableDefinition.Column.TABLE_NAME, values, TableDefinition.Column._ID + " = " + column.getID(), null);
    }

    public void delete(Column column ) {
        long id = column.getID();
        database.delete(TableDefinition.Column.TABLE_NAME, TableDefinition.Column._ID + " = " + id, null);
    }

    public void deleteByReport(String reportName ) {
        database.delete(TableDefinition.Column.TABLE_NAME, TableDefinition.Column.COLUMN_NAME_REPORT + " = '" + reportName + "'", null);
    }

    public void deleteAll() {
        //database.execSQL("DELETE FROM despesas WHERE " + TableDefinition.Column._ID + " <> 0");
        database.delete(TableDefinition.Column.TABLE_NAME, TableDefinition.Column.COLUMN_NAME_CODIGO + " <> 0", null);
    }

    public List<Column> getAll () {

        Cursor cursor = database.query(TableDefinition.Column.TABLE_NAME,
                columns, null, null, null, null, null);

        //despesas = new ArrayList<Despesa>();
        List<Column> columns = getListColumn(cursor);

        cursor.close();
        return columns;
    }

    public List<Column> getByReport (String reportName, String filtro) {

        String selection = TableDefinition.Column.COLUMN_NAME_REPORT + " = ? ";

        selection = selection + "AND " + TableDefinition.Column.COLUMN_NAME_FILTRO;
        if (filtro == null) {
            selection = selection + " <> ? ";
        } else {
            selection = selection + " = ? ";
        }

        String[] args = { reportName, fnCP.stringNull(filtro)};

        Cursor cursor = database.query(TableDefinition.Column.TABLE_NAME,
                columns, selection, args, null, null, null);

        List<Column> columns = getListColumn(cursor);

        cursor.close();
        return columns;
    }

    public Column getByReportCol (String reportName, String column) {

        String selection = TableDefinition.Column.COLUMN_NAME_REPORT + " = ? AND " +
                           TableDefinition.Column.COLUMN_NAME_CODIGO + " = ? ";

        String[] args = { reportName , column };

        Cursor cursor = database.query(TableDefinition.Column.TABLE_NAME,
                columns, selection, args, null, null, null);

        List<Column> columns = getListColumn(cursor);

        cursor.close();
        if (columns.size() > 0) {
            return columns.get(0);
        } else {
            return null;
        }
    }


    private List<Column> getListColumn (Cursor cursor) {

        List<Column> columns = new ArrayList<Column>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            Column column = new Column();

            column.setID(cursor.getInt(cursor.getColumnIndexOrThrow(TableDefinition.Column._ID)));
            column.setReport(cursor.getString(cursor.getColumnIndexOrThrow(TableDefinition.Column.COLUMN_NAME_REPORT)));
            column.setCodigo(cursor.getString(cursor.getColumnIndexOrThrow(TableDefinition.Column.COLUMN_NAME_CODIGO)));
            column.setLabel(cursor.getString(cursor.getColumnIndexOrThrow(TableDefinition.Column.COLUMN_NAME_LABEL)));
            column.setParam(cursor.getString(cursor.getColumnIndexOrThrow(TableDefinition.Column.COLUMN_NAME_PARAM)));
            column.setFiltro(cursor.getString(cursor.getColumnIndexOrThrow(TableDefinition.Column.COLUMN_NAME_FILTRO)));

            columns.add(column);

            /*
            Note note = new Note();
            note.setId(cursor.getLong(0));
            note.setNote(cursor.getString(1));
            notes.add(note);
            */

            cursor.moveToNext();
        }

        return columns;
    }

}