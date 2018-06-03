package br.com.newtechs.er.ad;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.newtechs.er.dao.Column;
import br.com.newtechs.er.dao.ColumnDao;
import br.com.newtechs.er.dao.Report;
import br.com.newtechs.er.dao.ReportDao;
import br.com.newtechs.er.dao.Layout;
import br.com.newtechs.er.dao.LayoutDao;
import br.com.newtechs.er.dao.LayoutReport;
import br.com.newtechs.er.dao.LayoutReportDao;
import br.com.newtechs.er.dao.ShareReport;
import br.com.newtechs.er.dao.ShareReportDao;

/**
 * Created by pbart on 15/05/2017.
 */

public class ReportsRepository {

    private static Context act;
    private String userID = "";
    private fnCP func;
    private Boolean bShared;
    private List<String> errors;
    private DatabaseReference myRef;
    private List<Report> reportList;
    private List<LayoutReport> layoutReportList;
    private List<ShareReport> sharedReportList;
    private String msgSuccess;

    public ReportsRepository (Context context) {
        act         = context;
        func        = new fnCP(context);
        userID      = func.getLoggedUser();
        userID      = func.getUserCode(userID);
        bShared     = false;
        errors      = new ArrayList<>();
        reportList  = new ArrayList<Report>();
        layoutReportList    = new ArrayList<LayoutReport>();
        myRef               = FirebaseDatabase.getInstance().getReference(fnCP.FIREBASE_APP_NAME);
    }

    public void setUserId (String userID) {
        this.userID = userID;
    }

    public void setShared (Boolean option) {
        bShared = option;
    }

    public Boolean isShared () {
        return bShared;
    }

    public void sendSinc () {
        msgSuccess = "Enviado com sucesso!";
//      Save Reports deve ser executado primeiro para gerar a lista de relatórios do usuário
//      logado para os demais
        saveReports();
        saveColumn();
        saveLayoutReport();
        saveLayout();
        sendSincSharedReports();
    }

    public void receiveSinc() {
        msgSuccess = "Recebido com sucesso!";
        getDbContent(fnCP.FIREBASE_DB_USERREPORTS);
        getDbContent(fnCP.FIREBASE_DB_USERCOLUMN);
        getDbContent(fnCP.FIREBASE_DB_USERLAYOUT);
        getDbContent(fnCP.FIREBASE_DB_USERLAYOUTREPORT);
        getDbContent(fnCP.FIREBASE_DB_OWNERSHARED);
        getDbContent(fnCP.FIREBASE_DB_USERSHARED);
    }

    public void sendSincSharedReports () {

//      Grava OWNERSHARED. Lista de emails do relatório do usuário.
        List<ShareReport> shareReportList = new ArrayList<ShareReport>();
        ShareReportDao shareReportDao = new ShareReportDao(act);
        shareReportDao.open();
        List<ShareReport> shareReports;
        for (Report temp : reportList) {
            shareReports = shareReportDao.getByReport(temp.getNome());
            for (ShareReport temp2 : shareReports) {
                shareReportList.add(temp2);
            }
        }

        try {
            myRef.child(fnCP.FIREBASE_DB_OWNERSHARED).
                 child(userID).setValue(shareReportList);
        } catch (Exception e) {
            errors.add(e.getMessage());
        }

//      Grata USERSHARED. O relatório por email compartilhado.
        try {
            Map<String, Object> emailList = new HashMap<>();
            for (ShareReport temp : shareReportList) {
                emailList.put(temp.getEmail(), "");
            }
            Object[] items = emailList.keySet().toArray();
            for (Object email : items) {
                shareReportList.clear();
                shareReportList = shareReportDao.getByEmail(email.toString());
                myRef.child(fnCP.FIREBASE_DB_USERSHARED).
                        child(fnCP.getUserCode(email.toString())).
                        child(userID).
                        setValue(shareReportList);
            }
        } catch (Exception e) {
            errors.add(e.getMessage());
        }
        shareReportDao.close();
    }

    private void saveReports () {

        ReportDao reportDao = new ReportDao(act);
        reportDao.open();
        List<Report> reports;
        reports = reportDao.getAllToSinc();
        reportList.clear();
        for (Report temp : reports) {
            reportList.add(temp);
        }
        reportDao.close();

        try {
            myRef.child(fnCP.FIREBASE_DB_USERREPORTS).child(userID).setValue(reportList);
        } catch (Exception e) {
            errors.add(e.getMessage());
        }
    }

    private void saveColumn () {

        List<Column> columnList = new ArrayList<Column>();
        ColumnDao columnDao = new ColumnDao(act);
        columnDao.open();
        List<Column> columns;
        columnList.clear();
        for (Report temp : reportList) {
            columns = columnDao.getByReport(temp.getNome(), null);
            for (Column temp2 : columns) {
                columnList.add(temp2);
            }
        }
        columnDao.close();

        try {
            myRef.child(fnCP.FIREBASE_DB_USERCOLUMN).child(userID).setValue(columnList);
        } catch (Exception e) {
            errors.add(e.getMessage());
        }
    }

    private void saveLayoutReport () {

        LayoutReportDao layoutReportDao = new LayoutReportDao(act);
        layoutReportDao.open();
        List<LayoutReport> layoutReports;
        layoutReportList.clear();
        for (Report temp : reportList) {
            layoutReports = layoutReportDao.getByReportLayout(temp.getNome(), temp.getLayout(), null);
            for (LayoutReport temp2 : layoutReports) {
                layoutReportList.add(temp2);
            }
        }
        layoutReportDao.close();

        try {
            myRef.child(fnCP.FIREBASE_DB_USERLAYOUTREPORT).child(userID).setValue(layoutReportList);
        } catch (Exception e) {
            errors.add(e.getMessage());
        }
    }

    private void saveLayout () {

        List<Layout> layoutList = new ArrayList<Layout>();
        LayoutDao layoutDao = new LayoutDao(act);
        layoutDao.open();
        Layout layout;
        layoutList.clear();
        for (LayoutReport temp : layoutReportList) {
            layout = layoutDao.getLayout(temp.getLayout());
            if (layout != null) {
                layoutList.add(layout);
            }
        }
        layoutDao.close();

        try {
            myRef.child(fnCP.FIREBASE_DB_USERLAYOUT).child(userID).setValue(layoutList);
        } catch (Exception e) {
            errors.add(e.getMessage());
        }
    }

    public void getDbContent (final String child) {

        Query myTopPostsQuery = myRef.child(child).child(userID)
                .orderByKey();

        myTopPostsQuery.addChildEventListener(
                new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                      Entra para cada objeto existente.
                        try {
                            switch (child) {
                                case fnCP.FIREBASE_DB_USERREPORTS:
                                    importReport(dataSnapshot.getValue(Report.class));
                                    break;
                                case fnCP.FIREBASE_DB_USERCOLUMN:
                                    importColumn(dataSnapshot.getValue(Column.class));
                                    break;
                                case fnCP.FIREBASE_DB_USERLAYOUT:
                                    importLayout(dataSnapshot.getValue(Layout.class));
                                    break;
                                case fnCP.FIREBASE_DB_USERLAYOUTREPORT:
                                    importLayoutReport(dataSnapshot.getValue(LayoutReport.class));
                                    break;
                                case fnCP.FIREBASE_DB_OWNERSHARED:
                                    importaShareReport(dataSnapshot.getValue(ShareReport.class));
                                    break;
                                case fnCP.FIREBASE_DB_USERSHARED:
                                    List<ShareReport> shareReportList = new ArrayList<ShareReport>();
                                    for (DataSnapshot message : dataSnapshot.getChildren()) {
                                        shareReportList.add(message.getValue(ShareReport.class));
                                    }
                                    importaUserShared(dataSnapshot.getKey(), shareReportList);
                                    break;
                            }
                        } catch (Exception e) {
                            Toast.makeText(act, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) { }
                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) { }
                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) { }
                    @Override
                    public void onCancelled(DatabaseError databaseError) { }
                }
        );
    }

    private void importReport(Report repFB) {
        if (isShared()) {
//          Caso seja a importação de um relatório compartilhado deve filtrar.
            Boolean lTem = false;
            for (ShareReport temp: sharedReportList) {
                if (temp.getReport().equals(repFB.getNome())) {
                    lTem = true;
                    break;
                }
            }
            if (!lTem) {
                return;
            }
            repFB.setShared(true);
        }
        ReportDao repDao = new ReportDao(act);
        repDao.open();
//      Tira o usuário pois o getReport já adiciona na busca.
        repFB.setCodigo(repFB.getCodigo().replace("-" + userID, ""));
        Report repLocal = repDao.getReport(repFB.getCodigo());
        if (repLocal == null) {
            repDao.create(repFB);
        } else {
            repDao.update(repFB);
        }
        repDao.close();
    }
    private void importColumn(Column colFB) {
        ColumnDao colDao = new ColumnDao(act);
        colDao.open();
        Column colLocal = colDao.getByReportCol(colFB.getReport(), colFB.getCodigo());
        if (colLocal == null) {
            colDao.create(colFB);
        } else {
            colDao.update(colFB);
        }
        colDao.close();
    }
    private void importLayout(Layout layFB) {

        LayoutDao layDao = new LayoutDao(act);
        layDao.open();
        Layout layLocal = layDao.getLayout(layFB.getCodigo());
        if (layLocal == null) {
            layDao.create(layFB);
        } else {
            layDao.update(layFB);
        }
        layDao.close();
    }
    private void importLayoutReport(LayoutReport layRepFB) {

        LayoutReportDao layRepDao = new LayoutReportDao(act);
        layRepDao.open();
        List<LayoutReport> layRepLocal = layRepDao.getByReportLayout(layRepFB.getReport(),
                                                               layRepFB.getLayout(),
                                                               layRepFB.getColPosition());
        if (layRepLocal.isEmpty()) {
            layRepDao.create(layRepFB);
        } else {
            layRepDao.update(layRepFB);
        }
        layRepDao.close();
    }
    private void importaShareReport(ShareReport shareReportFB) {
        ShareReportDao shareReportDao = new ShareReportDao(act);
        shareReportDao.open();
        if (!shareReportDao.emailReportExists(shareReportFB)) {
            shareReportDao.create(shareReportFB);
        }
        shareReportDao.close();
    }

    private void importaUserShared(String email, List<ShareReport> shareReportList) {

        ReportsRepository rpFirebase = new ReportsRepository(act);
        rpFirebase.setUserId(email);
        rpFirebase.setShared(true);
        rpFirebase.setSharedReportList(shareReportList);
        rpFirebase.getDbContent(fnCP.FIREBASE_DB_USERREPORTS);
    }

    public Boolean hasErros () {
        return !errors.isEmpty();
    }
    public List<String> getErros () {
        return errors;
    }
    public String getSuccessMsg () { return msgSuccess; }

    public void setSharedReportList(List<ShareReport> sharedReportList) {
        this.sharedReportList = sharedReportList;
    }
}
