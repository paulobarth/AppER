package br.com.newtechs.er;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import br.com.newtechs.er.ad.fnCP;
import br.com.newtechs.er.dao.Layout;
import br.com.newtechs.er.dao.LayoutDao;
import br.com.newtechs.er.dao.LayoutReport;
import br.com.newtechs.er.dao.LayoutReportDao;
import br.com.newtechs.er.dao.Report;
import br.com.newtechs.er.dao.ReportDao;

public class ActReports extends ActivityER {

    String opcaoTela;
    ListView lsReport;
    List<Report> reportList = new ArrayList<Report>();
    TextView txMsg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_reports);

        Intent myLocalIntent = getIntent();
        Bundle myBundle = myLocalIntent.getExtras();
//        opcaoTela = myBundle.getString("opcao");

        lsReport = (ListView) findViewById(R.id.lsReport);
        txMsg = (TextView) findViewById(R.id.txMsg);

//        carregaTela();
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


    private void carregaTela() {

        fnCarregaLista();

        lsReport.setAdapter(new IconicAdapter(this));

        lsReport.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ActReports.this, ActShowReport.class);
                intent.putExtra("report", reportList.get(position).getNome());
                startActivity(intent);
                }
            }
        );
    }

    class IconicAdapter extends ArrayAdapter {

        Activity context;
        IconicAdapter(Activity context) {
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

//    **************************************************************************************
//    **************************************************************************************
//    BLOCO RESPONSÁVEL POR SINCRONIZAR OS RELATÓRIOS COMPARTILHADOS.
//    TODO: BUSCAR DO FIREBASE
    public void clickBtSincReports (View v) {

    }


}
