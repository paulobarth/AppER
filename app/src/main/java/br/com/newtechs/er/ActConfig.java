package br.com.newtechs.er;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import br.com.newtechs.er.ad.fnCP;
import br.com.newtechs.er.dao.Report;
import br.com.newtechs.er.dao.ReportDao;

public class ActConfig extends ActivityER {

    ListView lsReport;
    List<Report> reportList = new ArrayList<Report>();
    TextView txMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_config);

        lsReport =(ListView) findViewById(R.id.lsReport);
        txMsg = (TextView) findViewById(R.id.txMsg);
    }

    @Override
    protected void onStart() {
        super.onStart();

        carregaTela();

        if (reportList.isEmpty()) {
            txMsg.setVisibility(View.VISIBLE);
            lsReport.setVisibility(View.INVISIBLE);
        } else {
            txMsg.setVisibility(View.INVISIBLE);
            lsReport.setVisibility(View.VISIBLE);
        }
    }

    public void clickBtNewRelat (View v) {
        Intent intent = new Intent(this, ActNewReport.class);
        startActivity(intent);
    }

    public void clickBtConfig (View v) {
//        Intent intent = new Intent(this, ActReports.class);
//        intent.putExtra("opcao", "config");
//        startActivity(intent);
    }

    private void carregaTela() {

        fnCarregaLista();

        lsReport.setAdapter(new FilesAdapter(ActConfig.this));

        lsReport.setOnItemClickListener(new AdapterView.OnItemClickListener() {

               @Override
               public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                   Intent intent = new Intent(ActConfig.this, ActConfigRepMenu.class);
                   intent.putExtra("report", reportList.get(position).getNome());
                   startActivity(intent);

               }
           }
        );

    }

    class FilesAdapter extends ArrayAdapter {

        Activity context;
        FilesAdapter(Activity context) {
            super(context, R.layout.activity_act_standard_list, reportList);
            this.context = context;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View row = inflater.inflate(R.layout.activity_act_standard_list, null);

            TextView txTexto = (TextView) row.findViewById(R.id.txTexto);

            Report report = reportList.get(position);

            txTexto.setText(report.getNome());

            return row;
        }
    }

    private void fnCarregaLista () {

        ReportDao reportDao = new ReportDao(this);
        reportDao.open();

        List<Report> reports;

        reports = reportDao.getReportByUserID(fnCP.getUserCode(fnCP.getLoggedUser()));

        reportList.clear();

        for (Report temp : reports) {

            reportList.add(temp);
        }

        reportDao.close();

    }
}
