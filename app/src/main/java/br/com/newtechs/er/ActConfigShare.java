package br.com.newtechs.er;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.newtechs.er.ad.fnCP;
import br.com.newtechs.er.dao.ShareReport;
import br.com.newtechs.er.dao.ShareReportDao;

public class ActConfigShare extends ActivityER {

    private ListView lsShareReport;
    private List<ShareReport> shareReportList = new ArrayList<ShareReport>();
    private TextView txMsg;
    private String reportName;
    private EditText edEmail;
    private FilesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_config_share);

        lsShareReport =(ListView) findViewById(R.id.lsShareReport);

        txMsg = (TextView) findViewById(R.id.txMsg);
        edEmail = (EditText) findViewById(R.id.edEmail);

        Intent myLocalIntent = getIntent();
        Bundle myBundle = myLocalIntent.getExtras();
        reportName = myBundle.getString("report");
    }

    @Override
    protected void onStart() {
        super.onStart();
        carregaTela();
        screenStatus();
    }

    private void screenStatus() {
        if (shareReportList.isEmpty()) {
            txMsg.setVisibility(View.VISIBLE);
            lsShareReport.setVisibility(View.INVISIBLE);
        } else {
            txMsg.setVisibility(View.INVISIBLE);
            lsShareReport.setVisibility(View.VISIBLE);
        }
    }
    public void clickBtAddEmail (View v) {

        if (!fnCP.isEmailValid(edEmail.getText().toString())) {
            Toast.makeText(this, "Email inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        ShareReportDao shareReportDao = new ShareReportDao(this);
        shareReportDao.open();

        ShareReport shareReport = new ShareReport();
        shareReport.setReport(reportName);
        shareReport.setEmail(edEmail.getText().toString());

        if (shareReportDao.emailReportExists(shareReport)) {
            Toast.makeText(this, "Email já compartilhado", Toast.LENGTH_SHORT).show();
        } else {
//          Grava o registro no banco de dados
            shareReportDao.create(shareReport);
            shareReport.setID(shareReportDao.getInsertId());

//          Adiciona o email na lista da tela
            shareReportList.add(shareReport);
            mAdapter.notifyDataSetChanged();

        }
        shareReportDao.close();
        edEmail.selectAll();

        screenStatus();
    }

    public void clickbtSalvar (View v) {

        finish();
    }

    private void carregaTela() {

        fnCarregaLista();

        mAdapter = new FilesAdapter(ActConfigShare.this);

        lsShareReport.setAdapter(mAdapter);
        lsShareReport.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ShareReport shareReport = shareReportList.get(position);
                try {
//                  Remove o email da tela.
                    shareReportList.remove(position);
                    mAdapter.notifyDataSetChanged();

//                  Remove o email do banco de dados.
                    ShareReportDao shareReportDao = new ShareReportDao(ActConfigShare.this);
                    shareReportDao.open();
                    shareReportDao.delete(shareReport);
                    shareReportDao.close();
                } catch (Exception e) {
                    Toast.makeText(ActConfigShare.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    class FilesAdapter extends ArrayAdapter {

        Activity context;
        FilesAdapter(Activity context) {
            super(context, R.layout.activity_act_standard_list, shareReportList);
            this.context = context;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View row = inflater.inflate(R.layout.activity_act_standard_list, null);

            TextView txTexto = (TextView) row.findViewById(R.id.txTexto);
            ImageView imImage = (ImageView) row.findViewById(R.id.imImagem);
            imImage.setBackgroundResource(android.R.drawable.ic_menu_close_clear_cancel);
            imImage.setOnClickListener(new View.OnClickListener() {
                int idPos = position;
                @Override
                public void onClick(View v) {
                    ShareReport shareReport = shareReportList.get(idPos);
                    try {
//                      Remove o email do banco de dados.
                        ShareReportDao shareReportDao = new ShareReportDao(ActConfigShare.this);
                        shareReportDao.open();
                        shareReportDao.delete(shareReport);
                        shareReportDao.close();

//                      Remove o email da tela.
                        shareReportList.remove(position);
                        mAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Toast.makeText(ActConfigShare.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            ShareReport shareReport = shareReportList.get(position);

            txTexto.setText(shareReport.getEmail());

            return row;
        }
    }

    private void fnCarregaLista () {

        ShareReportDao shareReportDao = new ShareReportDao(this);
        shareReportDao.open();

        List<ShareReport> shareReports;

        shareReports = shareReportDao.getByReport(reportName);

        shareReportList.clear();

        for (ShareReport temp : shareReports) {

            shareReportList.add(temp);
        }

        shareReportDao.close();

    }
}
