package br.com.newtechs.er;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import br.com.newtechs.er.ad.ReportsRepository;
import br.com.newtechs.er.ad.fnCP;
import br.com.newtechs.er.dao.ColumnDao;
import br.com.newtechs.er.dao.ContentReportDao;
import br.com.newtechs.er.dao.LayoutDao;
import br.com.newtechs.er.dao.LayoutReportDao;
import br.com.newtechs.er.dao.Report;
import br.com.newtechs.er.dao.ReportDao;
import br.com.newtechs.er.dao.UserLogin;
import br.com.newtechs.er.dao.UserLoginDao;
import br.com.newtechs.er.db.DbDefinition;
import br.com.newtechs.er.google.SheetApiTest;

public class MainActivity extends Activity {

    TextView txUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txUser = (TextView) findViewById(R.id.txUser);

        if (!new fnCP(this).isUserLogged()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        startService(this.getIntent());
        trataDbUpgrade();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //TODO Criar classe para trabalhar com as preferencias de autenticação
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            UserLoginDao ulDao = new UserLoginDao(MainActivity.this);
            ulDao.open();
            UserLogin ul = ulDao.getByEmail(user.getEmail());
            ulDao.close();
            if (ul != null) {
                txUser.setText("Olá, seja bem vindo(a) " + ul.getUser() + ".");
            } else {
                txUser.setText("Olá, seja bem vindo(a) " + user.getEmail() + ".");
            }
        } else {
//          Quando tiver sem internet usara as prefêrencias locais.
            SharedPreferences settings = getSharedPreferences("auth", Context.MODE_PRIVATE);
            if (settings.contains(fnCP.PREF_ACCOUNT_NAME)) {
                if (!settings.getString(fnCP.PREF_ACCOUNT_NAME, null).isEmpty()) {

                    UserLoginDao ulDao = new UserLoginDao(MainActivity.this);
                    ulDao.open();
                    UserLogin ul = ulDao.getByEmail(settings.getString(fnCP.PREF_ACCOUNT_NAME, null).toString());
                    ulDao.close();
                    if (ul != null) {
                        txUser.setText("Olá, seja bem vindo(a) " + ul.getUser() + ".");
                    } else {
                        txUser.setText("Olá, seja bem vindo(a) " +
                                settings.getString(fnCP.PREF_ACCOUNT_NAME, null).toString() + ".");
                    }
                }
            }
        }
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (txInf != null) {
//            cont++;
//            txInf.append(cont + " ON RESUME");
//            txInf.append("\n");
//
//        }
//    }
//
//    @Override
//    protected void onPostResume() {
//        super.onPostResume();
//        if (txInf != null) {
//            cont++;
//            txInf.append(cont + " ON POST RESUME");
//            txInf.append("\n");
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (txInf != null) {
//            cont++;
//            txInf.append(cont + " ON PAUSE");
//            txInf.append("\n");
//        }
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (txInf != null) {
//            cont++;
//            txInf.append(cont + " ON STOP");
//            txInf.append("\n");
//        }
//    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (!new fnCP(this).isUserLogged()) {
            finish();
        }
    }


    public void clickBtMeuRelat (View v) {
        Intent intent = new Intent(this, ActReports.class);
        startActivity(intent);
    }

    public void clickBtConfig (View v) {
        Intent intent = new Intent(this, ActConfig.class);
        startActivity(intent);
    }

    public void clickBtTeste1 (View v) {

        ReportsRepository rpFirebase = new ReportsRepository(this);

        try {
            rpFirebase.sendSinc();
            returnFirebaseMessage(rpFirebase);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public void clickBtTeste2 (View v)  {

        ReportsRepository rpFirebase = new ReportsRepository(this);

        try {
            rpFirebase.receiveSinc();
            returnFirebaseMessage(rpFirebase);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void returnFirebaseMessage (ReportsRepository rpFirebase) {

        if (rpFirebase.hasErros()) {
            List<String> erros = rpFirebase.getErros();
            Toast.makeText(this, erros.get(0), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, rpFirebase.getSuccessMsg(), Toast.LENGTH_SHORT).show();
        }
    }

    public void clickBtLogout  (View v) {

        //TODO Criar classe para trabalhar com as preferencias de autenticação
        SharedPreferences settings = getSharedPreferences("auth", Context.MODE_PRIVATE);
        if (settings.contains(fnCP.PREF_ACCOUNT_NAME)) {

            if (!settings.getString(fnCP.PREF_ACCOUNT_NAME, null).isEmpty()) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(fnCP.PREF_ACCOUNT_NAME, "");
                editor.putString(fnCP.PREF_ACCOUNT_PASS, "");
                editor.apply();

//                Toast.makeText(MainActivity.this, "Logout realizado com sucesso!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            }
        }
        FirebaseAuth.getInstance().signOut();
    }

    //    Procedimentos antigos
    public void clickBtZerar (View v) {

        //Elimina todos os relatorios e suas colunas e conteúdos
        try {

            ContentReportDao contentReportDao = new ContentReportDao(this);
            contentReportDao.open();
            contentReportDao.deleteAll();
            contentReportDao.close();

            ColumnDao columnDao = new ColumnDao(this);
            columnDao.open();
            columnDao.deleteAll();
            columnDao.close();

            ReportDao reportDao = new ReportDao(this);
            reportDao.open();
            reportDao.deleteAll();
            reportDao.close();

            LayoutReportDao layoutReportDao = new LayoutReportDao(this);
            layoutReportDao.open();
            layoutReportDao.deleteAll();
            layoutReportDao.close();

            LayoutDao layoutDao = new LayoutDao(this);
            layoutDao.open();
            layoutDao.deleteAll();
            layoutDao.close();

            Toast.makeText(this, "Dados zerados com sucesso!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void trataDbUpgrade() {
        SharedPreferences settings = getSharedPreferences("dbversions", Context.MODE_PRIVATE);
        int oldVersion = 6;
        int newVersion = new DbDefinition(this).getReadableDatabase().getVersion();
        if (settings.contains(fnCP.DB_OLD_VERSION)) {
            oldVersion = settings.getInt(fnCP.DB_OLD_VERSION, oldVersion);
        }
        if (oldVersion == newVersion) {
            return;
        }
        fnCP func = new fnCP(MainActivity.this);
        for (int vrs = oldVersion; vrs < newVersion; vrs++) {
            switch (vrs) {
                case 6:
                    func.dbUpgradeVersion6to7();
                    break;
            }
        }

        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(fnCP.DB_OLD_VERSION, newVersion);
        editor.apply();
    }
}
