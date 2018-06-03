package br.com.newtechs.er.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;
import br.com.newtechs.er.ad.fnCP;
import br.com.newtechs.er.db.DbDefinition;
import br.com.newtechs.er.db.TableDefinition;

/**
 * Created by Paulo on 20/05/2016.
 */
public class UserLoginDao {

    private SQLiteDatabase database;
    private String [] columns = {
            TableDefinition.UserLogin._ID,
            TableDefinition.UserLogin.COLUMN_NAME_USER,
            TableDefinition.UserLogin.COLUMN_NAME_PASSWORD,
            TableDefinition.UserLogin.COLUMN_NAME_EMAIL,
            TableDefinition.UserLogin.COLUMN_NAME_DATA_ULT_ACESSO,
            TableDefinition.UserLogin.COLUMN_NAME_COMPANY_NAME
    };
    private DbDefinition DbCP;

    public UserLoginDao(Context context ) {
        DbCP = new DbDefinition(context);
    }

    public void open () throws SQLException {
        database = DbCP.getWritableDatabase();
    }

    public void close () {
        DbCP.close();
    }

    public void create ( UserLogin userLogin ) {
        ContentValues values = new ContentValues();
        values.put(TableDefinition.UserLogin.COLUMN_NAME_USER, userLogin.getUser());
        values.put(TableDefinition.UserLogin.COLUMN_NAME_PASSWORD, userLogin.getPassword());
        values.put(TableDefinition.UserLogin.COLUMN_NAME_EMAIL, userLogin.getEmail());
        values.put(TableDefinition.UserLogin.COLUMN_NAME_DATA_ULT_ACESSO, userLogin.getDataUltAcesso());
        values.put(TableDefinition.UserLogin.COLUMN_NAME_COMPANY_NAME, userLogin.getCompanyName());

        long insertId = database.insert(TableDefinition.UserLogin.TABLE_NAME, null, values);
    }


    public void update(UserLogin userLogin) {

        ContentValues values = new ContentValues();
        values.put(TableDefinition.UserLogin.COLUMN_NAME_USER, userLogin.getUser());
        values.put(TableDefinition.UserLogin.COLUMN_NAME_PASSWORD, userLogin.getPassword());
        //Como o email é único na tabela não deverá ser atualizado
        //values.put(TableDefinition.UserLogin.COLUMN_NAME_EMAIL, userLogin.getEmail());
        values.put(TableDefinition.UserLogin.COLUMN_NAME_DATA_ULT_ACESSO, userLogin.getDataUltAcesso());
        values.put(TableDefinition.UserLogin.COLUMN_NAME_COMPANY_NAME, userLogin.getCompanyName());

        database.update(TableDefinition.UserLogin.TABLE_NAME, values, TableDefinition.UserLogin._ID + " = " + userLogin.getID(), null);
    }

    public void delete(UserLogin userLogin ) {
        long id = userLogin.getID();
        database.delete(TableDefinition.UserLogin.TABLE_NAME, TableDefinition.UserLogin._ID + " = " + id, null);
    }

    public void deleteAll() {
        //database.execSQL("DELETE FROM despesas WHERE " + TableDefinition.UserLogin._ID + " <> 0");
        database.delete(TableDefinition.UserLogin.TABLE_NAME, null, null);
    }

    public List<UserLogin> getAll () {

        Cursor cursor = database.query(TableDefinition.UserLogin.TABLE_NAME,
                columns, null, null, null, null, null);

        List<UserLogin> userLogins = getListUserLogin(cursor);

        cursor.close();
        return userLogins;
    }

    public UserLogin getByUser (String user) {

        String selection = TableDefinition.UserLogin.COLUMN_NAME_USER + " = ? ";
        String[] args = { user };

        Cursor cursor = database.query(TableDefinition.UserLogin.TABLE_NAME,
                columns, selection, args, null, null, null);

        List<UserLogin> userLogins = getListUserLogin(cursor);

        cursor.close();
        if (userLogins.size() > 0) {
            return userLogins.get(0);
        } else {
            return null;
        }
    }

    public UserLogin getByEmail (String email) {

        String selection = TableDefinition.UserLogin.COLUMN_NAME_EMAIL + " = ? ";
        String[] args = { email };

        Cursor cursor = database.query(TableDefinition.UserLogin.TABLE_NAME,
                columns, selection, args, null, null, null);

        List<UserLogin> userLogins = getListUserLogin(cursor);

        cursor.close();
        if (userLogins.size() > 0) {
            return userLogins.get(0);
        } else {
            return null;
        }
    }

    private List<UserLogin> getListUserLogin (Cursor cursor) {

        List<UserLogin> userLogins = new ArrayList<UserLogin>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            UserLogin userLogin = new UserLogin();

            userLogin.setID(cursor.getInt(cursor.getColumnIndexOrThrow(TableDefinition.UserLogin._ID)));
            userLogin.setUser(cursor.getString(cursor.getColumnIndexOrThrow(TableDefinition.UserLogin.COLUMN_NAME_USER)));
            userLogin.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(TableDefinition.UserLogin.COLUMN_NAME_PASSWORD)));
            userLogin.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(TableDefinition.UserLogin.COLUMN_NAME_EMAIL)));
            userLogin.setDataUltAcesso(cursor.getString(cursor.getColumnIndexOrThrow(TableDefinition.UserLogin.COLUMN_NAME_DATA_ULT_ACESSO)));
            userLogin.setCompanyName(cursor.getString(cursor.getColumnIndexOrThrow(TableDefinition.UserLogin.COLUMN_NAME_COMPANY_NAME)));

            userLogins.add(userLogin);

            cursor.moveToNext();
        }

        return userLogins;
    }

    public String createNewUserFirebase(UserLogin userLogin) {

        String result = "";

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(fnCP.FIREBASE_APP_NAME);

        userLogin.setPassword("");

        String userID = new fnCP(null).getUserCode(userLogin.getEmail());

        try {
            myRef.child(fnCP.FIREBASE_DB_USERLOGIN).child(userID).setValue(userLogin);
            result = "OK";
        } catch (Exception e) {
            result = e.getMessage();
        }

        return result;
    }
}