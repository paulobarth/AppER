package br.com.newtechs.er.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import br.com.newtechs.er.ad.fnCP;

/**
 * Created by Paulo on 03/10/2015.
 */
public class DbDefinition extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 7;
    public static final String DATABASE_NAME = "ER.db";

    public DbDefinition(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_REPORT);
        db.execSQL(SQL_CREATE_COLUMN);
        db.execSQL(SQL_CREATE_CONTENTREPORT);
        db.execSQL(SQL_CREATE_LAYOUT);
        db.execSQL(SQL_CREATE_LAYOUTREPORT);
        db.execSQL(SQL_CREATE_USERLOGIN);
        db.execSQL(SQL_CREATE_SHAREREPORT);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        if (oldVersion < 6) {
//          Até a versão 6 não era feito tratamento de upgrade do banco de dados.
            db.execSQL(SQL_DELETE_REPORT);
            db.execSQL(SQL_DELETE_COLUMN);
            db.execSQL(SQL_DELETE_CONTENTREPORT);
            db.execSQL(SQL_DELETE_LAYOUT);
            db.execSQL(SQL_DELETE_LAYOUTREPORT);
            db.execSQL(SQL_DELETE_USERLOGIN);
            db.execSQL(SQL_DELETE_SHAREREPORT);
            onCreate(db);
        } else {
            for (int vrs = oldVersion; vrs < newVersion; vrs++) {
                execUpgrades(vrs, db);
            }
        }
    }

    private void execUpgrades(int version, SQLiteDatabase db) {
        switch (version) {
            case 6:
                execUpgradeVersion6(db);
                break;
        }
    }

    private void execUpgradeVersion6(SQLiteDatabase db) {
        db.execSQL(SQL_UPGRADE_VERSION_6_TO_7);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    //private static final String REAL_TYPE = " REAL";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_REPORT =
            "CREATE TABLE " + TableDefinition.Report.TABLE_NAME + " (" +
                    TableDefinition.Report._ID + INT_TYPE + " PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    TableDefinition.Report.COLUMN_NAME_CODIGO       + TEXT_TYPE + COMMA_SEP +
                    TableDefinition.Report.COLUMN_NAME_NOME         + TEXT_TYPE + COMMA_SEP +
                    TableDefinition.Report.COLUMN_NAME_TIPO_ORIGEM  + TEXT_TYPE + COMMA_SEP +
                    TableDefinition.Report.COLUMN_NAME_ORIGEM_DADOS + TEXT_TYPE + COMMA_SEP +
                    TableDefinition.Report.COLUMN_NAME_FORMATO_ORIGEM + TEXT_TYPE + COMMA_SEP +
                    TableDefinition.Report.COLUMN_NAME_TIPO_ATZ     + TEXT_TYPE + COMMA_SEP +
                    TableDefinition.Report.COLUMN_NAME_LAYOUT       + TEXT_TYPE + COMMA_SEP +
                    TableDefinition.Report.COLUMN_NAME_VISIVEL      + TEXT_TYPE + COMMA_SEP +
                    TableDefinition.Report.COLUMN_NAME_USER_LOGIN_COL   + TEXT_TYPE + COMMA_SEP +
                    TableDefinition.Report.COLUMN_NAME_USER_ID          + TEXT_TYPE +  COMMA_SEP +
                    TableDefinition.Report.COLUMN_NAME_SHARED           + TEXT_TYPE +
            " ) ";
    private static final String SQL_CREATE_COLUMN =
            "CREATE TABLE " + TableDefinition.Column.TABLE_NAME + " (" +
                    TableDefinition.Column._ID + INT_TYPE + " PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    TableDefinition.Column.COLUMN_NAME_REPORT       + TEXT_TYPE + COMMA_SEP +
                    TableDefinition.Column.COLUMN_NAME_CODIGO       + TEXT_TYPE + COMMA_SEP +
                    TableDefinition.Column.COLUMN_NAME_LABEL        + TEXT_TYPE + COMMA_SEP +
                    TableDefinition.Column.COLUMN_NAME_PARAM        + TEXT_TYPE + COMMA_SEP +
                    TableDefinition.Column.COLUMN_NAME_FILTRO       +
            " ) ";
    private static final String SQL_CREATE_CONTENTREPORT =
            "CREATE TABLE " + TableDefinition.ContentReport.TABLE_NAME + " (" +
                    TableDefinition.ContentReport._ID + INT_TYPE + " PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    TableDefinition.ContentReport.COLUMN_NAME_COLUMN    + TEXT_TYPE + COMMA_SEP +
                    TableDefinition.ContentReport.COLUMN_NAME_LINE      + TEXT_TYPE + COMMA_SEP +
                    TableDefinition.ContentReport.COLUMN_NAME_REPORT    + TEXT_TYPE + COMMA_SEP +
                    TableDefinition.ContentReport.COLUMN_NAME_VALUE     + TEXT_TYPE +
            " ) ";
    private static final String SQL_CREATE_SHAREREPORT =
            "CREATE TABLE " + TableDefinition.ShareReport.TABLE_NAME + " (" +
                    TableDefinition.ShareReport._ID + INT_TYPE + " PRIMARY KEY AUTOINCREMENT " + COMMA_SEP +
                    TableDefinition.ShareReport.COLUMN_NAME_REPORT      + TEXT_TYPE + COMMA_SEP +
                    TableDefinition.ShareReport.COLUMN_NAME_EMAIL       + TEXT_TYPE +
                    " ) ";
    private static final String SQL_CREATE_LAYOUTREPORT =
            "CREATE TABLE " + TableDefinition.LayoutReport.TABLE_NAME + " (" +
                    TableDefinition.LayoutReport._ID + INT_TYPE + " PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    TableDefinition.LayoutReport.COLUMN_NAME_REPORT    + TEXT_TYPE + COMMA_SEP +
                    TableDefinition.LayoutReport.COLUMN_NAME_LAYOUT    + TEXT_TYPE + COMMA_SEP +
                    TableDefinition.LayoutReport.COLUMN_NAME_COLPOSITION    + TEXT_TYPE + COMMA_SEP +
                    TableDefinition.LayoutReport.COLUMN_NAME_COLUMN    + TEXT_TYPE +
                    " ) ";
    private static final String SQL_CREATE_LAYOUT =
            "CREATE TABLE " + TableDefinition.Layout.TABLE_NAME + " (" +
                    TableDefinition.Layout._ID + INT_TYPE + " PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    TableDefinition.Layout.COLUMN_NAME_CODIGO    + TEXT_TYPE + COMMA_SEP +
                    TableDefinition.Layout.COLUMN_NAME_QTROW     + TEXT_TYPE + COMMA_SEP +
                    TableDefinition.Layout.COLUMN_NAME_QTCOL     + TEXT_TYPE +
                    " ) ";
    private static final String SQL_CREATE_USERLOGIN =
            "CREATE TABLE " + TableDefinition.UserLogin.TABLE_NAME + " (" +
                    TableDefinition.UserLogin._ID + INT_TYPE + " PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    TableDefinition.UserLogin.COLUMN_NAME_USER      + TEXT_TYPE + COMMA_SEP +
                    TableDefinition.UserLogin.COLUMN_NAME_PASSWORD  + TEXT_TYPE + COMMA_SEP +
                    TableDefinition.UserLogin.COLUMN_NAME_EMAIL     + TEXT_TYPE + COMMA_SEP +
                    TableDefinition.UserLogin.COLUMN_NAME_DATA_ULT_ACESSO   + TEXT_TYPE + COMMA_SEP +
                    TableDefinition.UserLogin.COLUMN_NAME_COMPANY_NAME      + TEXT_TYPE +
                    " ) ";

    private static final String SQL_DELETE_REPORT =
            "DROP TABLE IF EXISTS " + TableDefinition.Report.TABLE_NAME;
    private static final String SQL_DELETE_COLUMN =
            "DROP TABLE IF EXISTS " + TableDefinition.Column.TABLE_NAME;
    private static final String SQL_DELETE_CONTENTREPORT =
            "DROP TABLE IF EXISTS " + TableDefinition.ContentReport.TABLE_NAME;
    private static final String SQL_DELETE_LAYOUTREPORT =
            "DROP TABLE IF EXISTS " + TableDefinition.LayoutReport.TABLE_NAME;
    private static final String SQL_DELETE_SHAREREPORT =
            "DROP TABLE IF EXISTS " + TableDefinition.ShareReport.TABLE_NAME;
    private static final String SQL_DELETE_LAYOUT =
            "DROP TABLE IF EXISTS " + TableDefinition.Layout.TABLE_NAME;
    private static final String SQL_DELETE_USERLOGIN =
            "DROP TABLE IF EXISTS " + TableDefinition.UserLogin.TABLE_NAME;

//  Version 6
    private static final String SQL_UPGRADE_VERSION_6_TO_7 =
        "ALTER TABLE " + TableDefinition.Report.TABLE_NAME + " ADD " +
                TableDefinition.Report.COLUMN_NAME_SHARED + TEXT_TYPE;
}