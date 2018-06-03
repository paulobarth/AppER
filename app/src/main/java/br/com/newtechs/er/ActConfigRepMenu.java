package br.com.newtechs.er;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import br.com.newtechs.er.dao.ColumnDao;
import br.com.newtechs.er.dao.ContentReportDao;
import br.com.newtechs.er.dao.LayoutReportDao;
import br.com.newtechs.er.dao.ReportDao;
import br.com.newtechs.er.dao.ShareReport;
import br.com.newtechs.er.dao.ShareReportDao;

public class ActConfigRepMenu extends ActivityER {

    String reportName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_config_rep_menu);

        Intent myLocalIntent = getIntent();
        Bundle myBundle = myLocalIntent.getExtras();
        reportName = myBundle.getString("report");

        TextView txReportName = (TextView) findViewById(R.id.txReportName);
        txReportName.setText(reportName);
    }

    public void clickBtParamCol (View v) {
        Intent intent = new Intent(this, ActConfigRepCol.class);
        intent.putExtra("report", reportName);
        intent.putExtra("selectColOnly", "no");
        startActivity(intent);
    }

    public void clickBtLayout (View v) {
        Intent intent = new Intent(this, ActConfigRepLayout.class);
        intent.putExtra("report", reportName);
        startActivity(intent);
    }

    public void clickBtShareReport (View v) {
        Intent intent = new Intent(this, ActConfigShare.class);
        intent.putExtra("report", reportName);
        startActivity(intent);
    }

    public void clickBtExcluir (View v) {

        //Elimina todos os relatorios e suas colunas e conteúdos
        try {

            ContentReportDao contentReportDao = new ContentReportDao(this);
            contentReportDao.open();
            contentReportDao.deleteByReport(reportName);
            contentReportDao.close();

            ColumnDao columnDao = new ColumnDao(this);
            columnDao.open();
            columnDao.deleteByReport(reportName);
            columnDao.close();

            ReportDao reportDao = new ReportDao(this);
            reportDao.open();
            reportDao.delete(reportDao.getReport(reportName));
            reportDao.close();

            LayoutReportDao layoutReportDao = new LayoutReportDao(this);
            layoutReportDao.open();
            layoutReportDao.deleteByReport(reportName);
            layoutReportDao.close();

            ShareReportDao shareReportDao = new ShareReportDao(this);
            shareReportDao.open();
            shareReportDao.deleteByReport(reportName);
            shareReportDao.close();

            Toast.makeText(this, "Relatório Eliminado com sucesso!", Toast.LENGTH_SHORT).show();

            finish();
        } catch (Exception e) {
            Toast.makeText(ActConfigRepMenu.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
