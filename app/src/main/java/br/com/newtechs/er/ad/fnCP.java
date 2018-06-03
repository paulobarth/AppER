package br.com.newtechs.er.ad;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import br.com.newtechs.er.JsonDocER;
import br.com.newtechs.er.dao.Column;
import br.com.newtechs.er.dao.ColumnDao;
import br.com.newtechs.er.dao.ContentReport;
import br.com.newtechs.er.dao.ContentReportDao;
import br.com.newtechs.er.dao.Report;
import br.com.newtechs.er.dao.ReportDao;
import br.com.newtechs.er.db.DbDefinition;
import br.com.newtechs.er.google.SheetApiTest;

/**
 * Created by Paulo on 23/10/2015.
 */
public class fnCP {

    public static final String TRUE = "true";
    public static final String FALSE = "false";
    public static final String CSVLocal = "CSVLocal";
    public static final String GOOGLE_DRIVE = "GOOGLE_DRIVE";
    public static final String PREF_ACCOUNT_NAME = "PREF_ACCOUNT_NAME";
    public static final String PICKED_GOOGLE_ACCOUNT = "PICKED_GOOGLE_ACCOUNT";
    public static final String PREF_ACCOUNT_PASS = "PREF_ACCOUNT_PASS";
    public static final String PREF_LOCAL_USER_LOGIN = "PREF_LOCAL_USER_LOGIN";
    public static final String CREATE_NEW_PLAN = "CREATE_NEW_PLAN";
    public static final String UPDATE_DATA_PLAN = "UPDATE_DATA_PLAN";
    public static final String LOCAL_TYPE = "LOCAL_TYPE";
    public static final String ONLINE_TYPE = "ONLINE_TYPE";
    public static final String DB_OLD_VERSION = "DB_OLD_VERSION";

    public static final String REPORT_SHARED = "REPORT_SHARED";
    //FireBase Infomations
    public static final String FIREBASE_URL             = "https://fluid-cosmos-132903.firebaseio.com";
    public static final String FIREBASE_APP_NAME        = "ER";
    public static final String FIREBASE_DB_USERLOGIN    = "USERLOGIN";
    public static final String FIREBASE_DB_USERREPORTS  = "USERREPORTS";
    public static final String FIREBASE_DB_USERCOLUMN   = "USERCOLUMN";
    public static final String FIREBASE_DB_USERLAYOUT   = "USERLAYOUT";
    public static final String FIREBASE_DB_USERLAYOUTREPORT  = "USERLAYOUTREPORT";
    public static final String FIREBASE_DB_USERSHARED   = "USERSHARED";
    public static final String FIREBASE_DB_OWNERSHARED  = "OWNERSHARED";

    private ProgressDialog mProgress;
    private Handler h = new Handler();

    private static Context act;

    public fnCP ( Context context ) {
        act = context;
    }

    private DecimalFormat formater = new DecimalFormat("#,###.00");

    public String formatDecimal (Float v) {
        return formater.format(v);
    }

    public String dateSQLFormat (String dt) {

        String[] data = dt.split("/");

        return (data[2].toString() + "-" +
                data[1].toString() + "-" +
                data[0].toString());
    }

    public String dateStringFormat (String dt) {

        String[] data = dt.split("-");

        return (data[2].toString() + "/" +
                data[1].toString() + "/" +
                data[0].toString());
    }

    public static String stringNull (String s) {

        String ret;
        if (s == null) {
            ret = "";
        } else {
            ret = s;
        }

        return ret;
    }

    public static String removerAcentos(String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    public static boolean isEmailValid(String email) {
        return email.contains("@");
    }

    public Boolean isUserLogged () {

        if (act == null) {
            return false;
        }

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            return true;
        } else {
            SharedPreferences settings = act.getSharedPreferences("auth", Context.MODE_PRIVATE);
            if (settings.contains(fnCP.PREF_ACCOUNT_NAME)) {
                if (!settings.getString(fnCP.PREF_ACCOUNT_NAME, null).isEmpty()) {
                    return true;
                }
            }
        }

        return false;
    }

    public static String getLoggedUser () {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            return user.getEmail();
        } else {
            SharedPreferences settings = act.getSharedPreferences("auth", Context.MODE_PRIVATE);
            if (settings.contains(fnCP.PREF_ACCOUNT_NAME)) {
                if (!settings.getString(fnCP.PREF_ACCOUNT_NAME, null).isEmpty()) {
                    return settings.getString(fnCP.PREF_ACCOUNT_NAME, null);
                }
            }
        }

        return "";
    }

    public static String getUserCode (String user) {
        return user.replace("@", "").replace("-", "").replace(".", "");
    }

    public Boolean localUserLogin () {

        if (act == null) {
            return false;
        }
//
//        SharedPreferences settings = act.getSharedPreferences("localUserLogin", Context.MODE_PRIVATE);
//        if (settings.contains(fnCP.PREF_LOCAL_USER_LOGIN)) {
//
//            String cUsers = settings.getString(fnCP.PREF_LOCAL_USER_LOGIN, null);
//
//            String[] list = cUsers.split("\t");
//
//            for (String item : list) {
//
//                item.split("\n")
//
//            }
//
//            if () {
//                return true;
//            }
//        }
        return false;
    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    public static boolean isDeviceOnline() {

        if (act == null) {
            return false;
        }

        ConnectivityManager connMgr =
                (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }


    public static String carregaJSON (Activity container, String reportName, JsonDocER jsER, String option) {

        if (!option.equals(CREATE_NEW_PLAN) &&
            !option.equals(UPDATE_DATA_PLAN)) {
            return "A opção solicitada é inválida.";
        }

        try {

            if (option.equals(CREATE_NEW_PLAN)) {

                // 1 Passo - Criar o relatório
                ReportDao reportDao = new ReportDao(container);
                reportDao.open();
                Report report = new Report();
                report.setCodigo(reportName);
                report.setNome(reportName);
                report.setTipoOrigem(jsER.getTipoOrigem());
                report.setOrigemDados(jsER.getOrigemDados());
                report.setFormatoOrigem(jsER.getFormatoOrigem());
                report.setTipoAtz(fnCP.LOCAL_TYPE);
                report.setLayout("");
                report.setVisivel(fnCP.FALSE);
                report.setUserLoginCol("");

                reportDao.create(report);
                reportDao.close();
            }

            // 2 Passo - Criar as colunas e conteúdo do relatório

            ContentReportDao contRepDao = new ContentReportDao(container);
            contRepDao.open();
            if (option.equals(UPDATE_DATA_PLAN)) {
                contRepDao.deleteByReport(reportName);
            }

            String lnRead  = "";
            JSONArray cols = null;

            JSONArray jsEntry = new JSONArray(jsER.getJsonDoc());

            //Navega em cada registro
            for (int contLine = 0; contLine <= jsEntry.length() - 1; contLine++) {

                JSONObject jsContent = jsEntry.getJSONObject(contLine);

                //Primeira linha gravar cabecalho.
                if (cols == null) {
                    cols = jsContent.names();

                    if (option.equals(CREATE_NEW_PLAN)) {

                        ColumnDao colDao = new ColumnDao(container);
                        colDao.open();
                        for (int cont2 = 0; cont2 <= cols.length() - 1; cont2++) {

                            Column column = new Column();
                            column.setReport(reportName);
                            column.setCodigo(String.valueOf(cont2 + 1));
                            column.setLabel(cols.get(cont2).toString());
                            column.setNegrito(false);
                            column.setSize(14);
                            column.setFiltro(fnCP.FALSE);

                            colDao.create(column);
                        }
                        colDao.close();
                    }
                }

                //Cria o conteúdo
                for (int cont2 = 0; cont2 <= cols.length() - 1; cont2++) {

                    ContentReport contRep = new ContentReport();
                    contRep.setReport(reportName);
                    contRep.setLine(String.valueOf(contLine + 1));
                    contRep.setColumn(String.valueOf(cont2 + 1));
                    contRep.setValue(jsContent.getString(cols.get(cont2).toString()));

                    contRepDao.create(contRep);
                }
            }

            contRepDao.close();

            return "OK";
        }
        catch (Exception e) {
            return "Erro: " + e.getMessage();
        }
    }

    public static void dbUpgradeVersion6to7() {
//          Upgrade da versão 6 para 7;
//            - Criado campo:
//               Tabela: Report    Campo: shared
        ReportDao reportDao = new ReportDao(act);
        reportDao.open();
        List<Report> reportList = reportDao.getAll();
        reportDao.deleteAll();
        for (Report report : reportList) {
            report.setShared(false);
            reportDao.create(report);
        }
        reportDao.close();
    }

    /*
    private static final Locale BRASIL = new Locale("pt", "BR");
    private static final NumberFormat nf = NumberFormat.getCurrencyInstance(BRASIL);

    //Coloca virgula
    public static String formatMoeda(Float d){
        return nf.format(d);
    }

    //tira a virgula
    public static Float formatToDouble(String s){
        try {
            return (nf.parse(s)).floatValue();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0f;
    }
    */

}
