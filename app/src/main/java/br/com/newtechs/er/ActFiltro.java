package br.com.newtechs.er;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import br.com.newtechs.er.ad.fnCP;
import br.com.newtechs.er.dao.Column;
import br.com.newtechs.er.dao.ColumnDao;
import br.com.newtechs.er.dao.LayoutReport;
import br.com.newtechs.er.dao.LayoutReportDao;
import br.com.newtechs.er.dao.Report;
import br.com.newtechs.er.dao.ReportDao;

public class ActFiltro extends ActivityER {

    private Intent superIntent;
    private String reportName;
    private List<EditText> lsCampos;
    private String sJsonErFiltro;
    private List<String> lsParam;
    private ListView lsFiltro;
    private List<Column> columnList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_filtro);

        TableLayout tlFiltro = (TableLayout) findViewById(R.id.tlFiltro);

        superIntent = getIntent();
        Bundle myBundle = superIntent.getExtras();
        reportName = myBundle.getString("report");
        lsParam = new ArrayList<String>();
        sJsonErFiltro = myBundle.getString("filtro");

        if (!sJsonErFiltro.isEmpty()) {

            try {

                JSONObject jsContent;
                JSONArray jsEntry = new JSONArray(sJsonErFiltro);

                for (int contLine = 0; contLine <= jsEntry.length() - 1; contLine++) {

                    jsContent = jsEntry.getJSONObject(contLine);

                    lsParam.add(jsContent.getString("value"));
                }
            } catch (Exception e) {
            }
        }

        //lsFiltro = (ListView) findViewById(R.id.lsFiltro);

        //carregaTela();

        //fff
        ReportDao repDao = new ReportDao(this);
        repDao.open();
        ColumnDao columnDao = new ColumnDao(this);
        columnDao.open();
        LayoutReportDao lrDao = new LayoutReportDao(this);
        lrDao.open();

        Report report = repDao.getReport(reportName);

//      Tras apenas as colunas marcadas como filtro
        List<Column> columns = columnDao.getByReport(reportName, fnCP.TRUE);

//      Traz as colunas apresentadas no layout, pois deve apresentar os filtros apenas dos
//      campos apresentados no layout.
        List<LayoutReport> listLr = lrDao.getByReportLayout(reportName, report.getLayout(), null);
        String cLayCols = "";
        for (LayoutReport lay : listLr) {
            cLayCols += lay.getColumn();
        }

        lsCampos = new ArrayList<EditText>();

        int cont = 0;
        for (Column c : columns) {

            if (cLayCols.contains(c.getCodigo())) {

//              Label
                final TextView tx1 = new TextView(this);
                tx1.setMinimumHeight(25);
                tx1.setTextColor(Color.BLACK);
                tx1.setText(c.getLabel());
                tx1.setTextSize(16);
                TableRow tRow1 = new TableRow(getBaseContext());
                tRow1.setOrientation(TableRow.HORIZONTAL);
                tRow1.addView(tx1);

//              Texto do Filtro
                final EditText ed1 = new EditText(this);
                ed1.setMinimumHeight(25);
                ed1.setTextColor(Color.BLACK);
                ed1.setHint("Informe " + c.getLabel());
                ed1.setId(Integer.parseInt(c.getCodigo()));

                if (lsParam.size() >= (cont + 1)) {
                    ed1.setText(lsParam.get(cont).toString());
                }

                TableRow.LayoutParams p = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1);
                //p.setMargins(0, 0, 0, 20);
                ed1.setLayoutParams(p);
                TableRow tRow2 = new TableRow(getBaseContext());
                tRow2.setOrientation(TableRow.HORIZONTAL);
                tRow2.addView(ed1);


//              Botão para limpar o campo texto
                final Button bt1 = new Button(this);

                //TableRow.LayoutParams pBt = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 0);
                TableRow.LayoutParams pBt = new TableRow.LayoutParams(50, 50, 0);
                pBt.setMargins(2, 2, 2, 2);
                bt1.setLayoutParams(pBt);
                bt1.setBackgroundResource(android.R.drawable.ic_menu_close_clear_cancel);
                bt1.setId(Integer.parseInt(c.getCodigo()));
                bt1.setOnClickListener(new View.OnClickListener() {

                    int btId = bt1.getId();

                    @Override
                    public void onClick(View v) {

                        try {

                            for (EditText temp : lsCampos) {

                                if (temp.getId() == btId) {
                                    temp.setText("");
                                }
                            }
                        } catch (Exception e) {
                        }
                    }
                });
                tRow2.addView(bt1);

                tlFiltro.addView(tRow1, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT));
                tlFiltro.addView(tRow2, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT));

                lsCampos.add(ed1);
                cont ++;
            }
        }

        TextView txMsg = (TextView) findViewById(R.id.txMsg);
        if (cont == 0) {
            txMsg.setVisibility(EditText.VISIBLE);
        } else {
            txMsg.setVisibility(EditText.INVISIBLE);
        }

        lrDao.close();
        columnDao.close();
        repDao.close();
    }



    private void carregaTela() {

        fnCarregaLista();

        lsFiltro.setAdapter(new IconicAdapter(this));
    }


    private void fnCarregaLista () {

        ReportDao repDao = new ReportDao(this);
        repDao.open();
        ColumnDao columnDao = new ColumnDao(this);
        columnDao.open();
        LayoutReportDao lrDao = new LayoutReportDao(this);
        lrDao.open();

        Report report = repDao.getReport(reportName);

//      Tras apenas as colunas marcadas como filtro
        List<Column> columns = columnDao.getByReport(reportName, fnCP.TRUE);

//      Traz as colunas apresentadas no layout, pois deve apresentar os filtros apenas dos
//      campos apresentados no layout.
        List<LayoutReport> listLr = lrDao.getByReportLayout(reportName, report.getLayout(), null);
        String cLayCols = "";
        for (LayoutReport lay : listLr) {
            cLayCols += lay.getColumn();
        }

        lsCampos = new ArrayList<EditText>();

        columnList = new ArrayList<Column>();
        columnList.clear();

        for (Column c : columns) {

            if (cLayCols.contains(c.getCodigo())) {

//              Criar o campo filtro inicial e final.
                columnList.add(c);
            }
        }

        lrDao.close();
        columnDao.close();
        repDao.close();

    }


    class IconicAdapter extends ArrayAdapter {

        int cont = 0;
        Activity context;
        IconicAdapter(Activity context) {
            super(context, R.layout.activity_act_table_list, columnList);
            this.context = context;
            cont = 0;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View row = inflater.inflate(R.layout.activity_act_table_list, null);
            TableLayout tlFiltro = (TableLayout) row.findViewById(R.id.tlFiltro);

            Column col = columnList.get(position);

//fff
//          Label
            final TextView tx1 = new TextView(context);
            tx1.setMinimumHeight(25);
            tx1.setTextColor(Color.BLACK);
            tx1.setText(col.getLabel());
            tx1.setTextSize(16);

            TableRow tRow1 = new TableRow(getBaseContext());
            tRow1.setOrientation(TableRow.HORIZONTAL);
            tRow1.addView(tx1);

//          Texto do Filtro
            final EditText ed1 = new EditText(context);
            ed1.setMinimumHeight(25);
            ed1.setTextColor(Color.BLACK);
            ed1.setHint("Informe " + col.getLabel());
            ed1.setId(Integer.parseInt(col.getCodigo()));

            if (lsParam.size() >= (cont + 1)) {
                ed1.setText(lsParam.get(cont).toString());
            }

            TableRow.LayoutParams p = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1);
            ed1.setText("");
            //p.setMargins(0, 0, 0, 20);
            ed1.setLayoutParams(p);

            TableRow tRow2 = new TableRow(getBaseContext());
            tRow2.setOrientation(TableRow.HORIZONTAL);
            tRow2.addView(ed1);
            lsCampos.add(ed1);


//              Botão para limpar o campo texto
            final Button bt1 = new Button(context);
            TableRow.LayoutParams pBt = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 0);
            pBt.setMargins(2, 2, 2, 2);
            bt1.setLayoutParams(pBt);
            bt1.setBackgroundResource(android.R.drawable.ic_menu_close_clear_cancel);
            bt1.setId(Integer.parseInt(col.getCodigo()));
            bt1.setOnClickListener(new View.OnClickListener() {

                int btId = bt1.getId();

                @Override
                public void onClick(View v) {
                    lsCampos.get(btId - 1).setText("");
                    Toast.makeText(context, String.valueOf(btId), Toast.LENGTH_SHORT).show();
                }
            });

            tRow2.addView(bt1);




            tlFiltro.addView(tRow1, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT));
            tlFiltro.addView(tRow2, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT));

            cont ++;
            return row;
        }
    }

    public void clickBtAplicar (View v) {

        JsonDocER jEr = new JsonDocER();
        jEr.write("[");

        String msg = "";
        int cont = 0;
        for (EditText ed : lsCampos) {

            cont++;

            if (cont > 1) {
                jEr.write(",");
                jEr.newLine();
            }

            jEr.write("{");
            jEr.write("\"code\": \"" + ed.getId() + "\"");
            jEr.write(", ");
            jEr.write("\"value\": \"" + ed.getText() + "\"");
            jEr.write("}");
        }
        jEr.write("]");

        Bundle myBundle = new Bundle();

        myBundle.putString("filtro", jEr.getJsonDoc());
        superIntent.putExtras(myBundle);

        setResult(Activity.RESULT_OK, superIntent);

        finish();
    }

}
