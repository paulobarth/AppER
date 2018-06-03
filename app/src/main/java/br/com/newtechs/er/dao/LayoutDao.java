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
public class LayoutDao {

    private SQLiteDatabase database;
    private String [] columns = {
            TableDefinition.Layout._ID,
            TableDefinition.Layout.COLUMN_NAME_CODIGO,
            TableDefinition.Layout.COLUMN_NAME_QTROW,
            TableDefinition.Layout.COLUMN_NAME_QTCOL
    };
    private DbDefinition DbCP;

    public LayoutDao ( Context context ) {
        DbCP = new DbDefinition(context);
    }

    public void open () throws SQLException {
        database = DbCP.getWritableDatabase();
    }

    public void close () {
        DbCP.close();
    }

    public void create ( Layout layout ) {
        ContentValues values = new ContentValues();
        values.put(TableDefinition.Layout.COLUMN_NAME_CODIGO, layout.getCodigo());
        values.put(TableDefinition.Layout.COLUMN_NAME_QTROW, layout.getQtRow());
        values.put(TableDefinition.Layout.COLUMN_NAME_QTCOL, layout.getQtCol());

        long insertId = database.insert(TableDefinition.Layout.TABLE_NAME, null, values);
    }


    public void update(Layout layout) {

        ContentValues values = new ContentValues();
        values.put(TableDefinition.Layout.COLUMN_NAME_CODIGO, layout.getCodigo());
        values.put(TableDefinition.Layout.COLUMN_NAME_QTROW, layout.getQtRow());
        values.put(TableDefinition.Layout.COLUMN_NAME_QTCOL, layout.getQtCol());

        database.update(TableDefinition.Layout.TABLE_NAME, values, TableDefinition.Layout._ID + " = " + layout.getID(), null);
    }

    public void delete(Layout layout ) {
        long id = layout.getID();
        database.delete(TableDefinition.Layout.TABLE_NAME, TableDefinition.Layout._ID + " = " + id, null);
    }

    public void deleteAll() {
        //database.execSQL("DELETE FROM despesas WHERE " + TableDefinition.Layout._ID + " <> 0");
        database.delete(TableDefinition.Layout.TABLE_NAME, TableDefinition.Layout.COLUMN_NAME_CODIGO + " <> 0", null);
    }

    public List<Layout> getAll () {

        Cursor cursor = database.query(TableDefinition.Layout.TABLE_NAME,
                columns, null, null, null, null, null);

        List<Layout> layout = getListLayout(cursor);

        cursor.close();
        return layout;
    }

    public Layout getLayout (String codigo) {

        String selection = TableDefinition.Layout.COLUMN_NAME_CODIGO + " = ? ";

        String[] args = { codigo };

        Cursor cursor = database.query(TableDefinition.Layout.TABLE_NAME,
                columns, selection, args, null, null, null);

        List<Layout> layout = getListLayout(cursor);

        cursor.close();
        if (layout.isEmpty()) {
            return null;
        } else {
            return layout.get(0);
        }
    }

    public Layout getLayoutByModel (String qtRow, String qtCol) {

        String selection = TableDefinition.Layout.COLUMN_NAME_QTROW + " = ? AND " +
                           TableDefinition.Layout.COLUMN_NAME_QTCOL + " = ? ";

        String[] args = { qtRow, qtCol };

        Cursor cursor = database.query(TableDefinition.Layout.TABLE_NAME,
                columns, selection, args, null, null, null);

        List<Layout> layout = getListLayout(cursor);

        if (layout.isEmpty()) {
            return null;
        } else {
            return layout.get(0);
        }
    }

    private List<Layout> getListLayout (Cursor cursor) {

        List<Layout> layouts = new ArrayList<Layout>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            Layout layout = new Layout();

            layout.setID(cursor.getInt(cursor.getColumnIndexOrThrow(TableDefinition.Layout._ID)));
            layout.setCodigo(cursor.getString(cursor.getColumnIndexOrThrow(TableDefinition.Layout.COLUMN_NAME_CODIGO)));
            layout.setQtRow(cursor.getString(cursor.getColumnIndexOrThrow(TableDefinition.Layout.COLUMN_NAME_QTROW)));
            layout.setQtCol(cursor.getString(cursor.getColumnIndexOrThrow(TableDefinition.Layout.COLUMN_NAME_QTCOL)));

            layouts.add(layout);

            cursor.moveToNext();
        }

        return layouts;
    }
}