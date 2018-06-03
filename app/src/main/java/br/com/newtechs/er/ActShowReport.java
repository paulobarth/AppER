package br.com.newtechs.er;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.newtechs.er.ad.fnCP;
import br.com.newtechs.er.dao.Column;
import br.com.newtechs.er.dao.ColumnDao;
import br.com.newtechs.er.dao.ContentReport;
import br.com.newtechs.er.dao.ContentReportDao;
import br.com.newtechs.er.dao.Layout;
import br.com.newtechs.er.dao.LayoutDao;
import br.com.newtechs.er.dao.Report;
import br.com.newtechs.er.dao.ReportDao;
import br.com.newtechs.er.dao.LayoutReport;
import br.com.newtechs.er.dao.LayoutReportDao;
import br.com.newtechs.er.google.ActGoogleLogin;
import br.com.newtechs.er.google.SheetApiTest;

public class ActShowReport extends ActivityER {

    private static final int GOOGLE_SHEET_LOAD = 3;

    int qtdeLin;
    String reportName;
    String layoutName;
    String sJsonErFiltro = new String();
    ListView lsReport;
    List<String> reportList = new ArrayList<String>();
    TextView txMsg;
    Layout layout;
    Report report;
    String[] listCol;
    int totalRow;
    int totalCol;
    int colSequence;
    int lHeight = RelativeLayout.LayoutParams.MATCH_PARENT;
    int lWidth = RelativeLayout.LayoutParams.WRAP_CONTENT;

    private static final int FILTRO_REQUESTCODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_act_show_report);

        lsReport = (ListView) findViewById(R.id.lsReport);

        Intent myLocalIntent = getIntent();
        Bundle myBundle = myLocalIntent.getExtras();
        reportName = myBundle.getString("report");

        txMsg = (TextView) findViewById(R.id.txMsg);

        ReportDao reportDao = new ReportDao(this);
        reportDao.open();
        report = reportDao.getReport(reportName);
        reportDao.close();

        if (report.getLayout().isEmpty()) {
            txMsg.setVisibility(View.VISIBLE);
        } else {

            txMsg.setVisibility(View.INVISIBLE);

            LayoutDao layoutDao = new LayoutDao(this);
            layoutDao.open();
            layout = layoutDao.getLayout(report.getLayout());
            layoutName = report.getLayout();
            listCol = layout.getQtCol().split(";");
            totalRow = Integer.parseInt(layout.getQtRow());
            layoutDao.close();

            carregaTela();
        }
    }

    private void carregaTela() {

        if (!report.getLayout().isEmpty()) {

            fnCarregaLista();

            lsReport.setAdapter(new IconicAdapter(this));
        }
    }

    class IconicAdapter extends ArrayAdapter {

        Activity context;
        IconicAdapter(Activity context) {
            //super(context, R.layout.activity_act_standard_list, reportList);
            super(context, R.layout.activity_act_table_list, reportList);
            this.context = context;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View row = inflater.inflate(R.layout.activity_act_table_list, null);

            colSequence = 0;
            LayoutReportDao lrDao = new LayoutReportDao(context);
            lrDao.open();
            ColumnDao columnDao = new ColumnDao(context);
            columnDao.open();
            ContentReportDao contentReportDao = new ContentReportDao(context);
            contentReportDao.open();

            String linePosition = reportList.get(position);

            TableLayout tlReport = (TableLayout) row.findViewById(R.id.tlReport);

            for (int contRow = 0; contRow < totalRow; contRow++) {

                totalCol = Integer.parseInt(listCol[contRow]);

                TableRow tr1 = new TableRow(getBaseContext());
                tr1.setOrientation(TableRow.HORIZONTAL);

                for (int contCol = 0; contCol < totalCol; contCol++) {

                    colSequence++;

                    final TextView tx1 = new TextView(context);
                    tx1.setMinimumHeight(25);
//                    tx1.setTextColor(Color.BLACK);
                    tx1.setWidth(tr1.getWidth() / totalCol);

                    TableRow.LayoutParams p = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1);
                    p.setMargins(2, 2, 2, 2);
                    tx1.setLayoutParams(p);
                    if (contCol == totalCol - 1 && contCol > 0) {
                        tx1.setGravity(Gravity.RIGHT);
                    }

                    List<LayoutReport> listLr = lrDao.getByReportLayout(reportName, layoutName, String.valueOf(colSequence));
                    if (listLr.size() > 0) {

                        ContentReport contentReport = contentReportDao.getByData(reportName, linePosition, listLr.get(0).getColumn());
                        if (contentReport != null) {

                            tx1.setText(contentReport.getValue());

                            Column column = columnDao.getByReportCol(reportName, contentReport.getColumn());
                            if (column != null) {
                                tx1.setTextSize(column.getSize());
                                if (column.isNegrito()) {
                                    tx1.setTypeface(null, Typeface.BOLD);
                                }
                            }
                        }
                    }

                    tr1.addView(tx1);

                }

                tlReport.addView(tr1, new RelativeLayout.LayoutParams(lHeight, lWidth));
            }

            lrDao.close();
            contentReportDao.close();
            columnDao.close();

            return row;
        }
    }


    private void fnCarregaLista () {

        String sLin = new String();
        String sCol = new String();

        //Le as colunas
        ColumnDao columnDao = new ColumnDao(this);
        columnDao.open();

        List<Column> columns;

        reportList.clear();

        columns = columnDao.getByReport(reportName, null);

        for (Column temp : columns) {
            sCol = sCol + temp.getCodigo();
        }

        columnDao.close();

        //Le os conteúdos
        ContentReportDao contentReportDao = new ContentReportDao(this);
        contentReportDao.open();

//      Aplicar Filtros
        List<ContentReport> contRep = aplicarFilros(contentReportDao.getByReport(reportName), report.getUserLoginCol());

        int line = 1;
        sLin = String.valueOf(line);
        Boolean lLastLine = false;
        for (ContentReport temp : contRep) {

            //A função aplicarFiltros zera a coluna quando o registro não atende os filtros.
            if (temp.getColumn().equals("")) {
                continue;
            }

            //Representa uma linha nova
            if (line != Integer.parseInt(temp.getLine())) {
                if (lLastLine) {
                    reportList.add(sLin);
                }
                line = Integer.parseInt(temp.getLine());
                sLin = String.valueOf(line);
            }

            lLastLine = true;

        }
        if (lLastLine) {
            //Adiciona a ultima linha montada
            reportList.add(sLin);
        }

        contentReportDao.close();
    }

    private List<ContentReport> aplicarFilros(List<ContentReport> contRep, String userLoginCol) {

        List<ContentReport> bkpRep = contRep;
        JSONObject jsContent;

        //TODO Criar classe para trabalhar com as preferencias de autenticação
        SharedPreferences settings = getSharedPreferences("auth", Context.MODE_PRIVATE);
        String userLogged = settings.getString(fnCP.PREF_ACCOUNT_NAME, null);

        if (!sJsonErFiltro.isEmpty() || !userLoginCol.isEmpty()) {

            try {

                JSONArray jsEntry;

                if (!sJsonErFiltro.isEmpty()) {
                    jsEntry = new JSONArray(sJsonErFiltro);
                } else {
                    jsEntry = new JSONArray("[]");
                }

                List<String> lsDesconsiderar = new ArrayList<String>();
                for (ContentReport temp : contRep) {

                    if (!sJsonErFiltro.isEmpty()) {

                        for (int contLine = 0; contLine <= jsEntry.length() - 1; contLine++) {

                            jsContent = jsEntry.getJSONObject(contLine);
                            if (jsContent.getString("value").isEmpty()) {
                                continue;
                            }

                            //Fazer filtro
                            if (temp.getColumn().equals(jsContent.getString("code"))) {

                                if (!temp.getValue().toUpperCase().contains(jsContent.getString("value").toUpperCase())) {
                                    lsDesconsiderar.add(temp.getLine());
                                    continue;
                                }
                            }
                        }
                    }

                    //Fazer filtro da coluna de usuário, caso o relatório possua.
                    if (temp.getColumn().equals(userLoginCol)) {

                        if (!temp.getValue().contains(userLogged)) {
                            lsDesconsiderar.add(temp.getLine());
                            continue;
                        }
                    }
                }

                int line = 0;
                for (ContentReport temp : contRep) {

                    for (String c : lsDesconsiderar) {

                        if (c.equals(temp.getLine())) {
                            contRep.get(line).setColumn("");
                            break;
                        }
                    }
                    line++;
                }
            } catch (Exception e) {
                return bkpRep;
            }
        }

        return contRep;
    }

    public void clickBtAtz (View v) {

        if (report.getTipoOrigem().equals(fnCP.CSVLocal)) {
            carregaCSV(this);
        } else if (report.getTipoOrigem().equals(fnCP.GOOGLE_DRIVE)) {

//            Antigo Modo
//            jsReadER = new ReadPlanGoogle();
//            jsReadER.setId(report.getOrigemDados());
//            jsReadER.setType(ReadPlanGoogle.SPREADSHEET_TYPE);
//            //Ao finalizar chamará a rotina doIt
//            jsReadER.execute(this, ActShowReport.class.getName());

//            Novo Modo
            Intent intent = new Intent(this, SheetApiTest.class);
            Bundle data = new Bundle();
            data.putString(SheetApiTest.ID_GOOGLE_SELECTED, report.getOrigemDados());
            intent.putExtras(data);
            startActivityForResult(intent, GOOGLE_SHEET_LOAD);
        }
    }

    public void clickBtFiltro (View v) {
        Intent intent = new Intent(this, ActFiltro.class);
        intent.putExtra("report", reportName);
        intent.putExtra("filtro", sJsonErFiltro);
        startActivityForResult(intent, FILTRO_REQUESTCODE);
    }


    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            //user requestCode do find out who is talking back us
            switch (requestCode) {
                case (FILTRO_REQUESTCODE): {
                    if (resultCode == Activity.RESULT_OK) {

                        //TODO Aplicar o filtro
                        Bundle bundle = data.getExtras();

                        sJsonErFiltro = bundle.getString("filtro");

                        if (!report.getLayout().isEmpty()) {
                            carregaTela();
                        }
                    }
                    else {
                        //user pressed the back button
                    }
                    break;
                }
                case (GOOGLE_SHEET_LOAD): {
                    if (resultCode == Activity.RESULT_OK) {
                        Bundle myBundle = data.getExtras();
                        String json = myBundle.getString("JSON");
                        if (json == null) {
                            Toast.makeText(this, "Atualização com problemas.", Toast.LENGTH_SHORT).show();
                        } else {
                            JsonDocER jsonDoc = new JsonDocER();
                            jsonDoc.setTipoOrigem(report.getTipoOrigem());
                            jsonDoc.setOrigemDados(report.getOrigemDados());
                            jsonDoc.setFormatoOrigem(report.getFormatoOrigem());
                            jsonDoc.write(json);
                            String ret = fnCP.carregaJSON(this, report.getNome(), jsonDoc,
                                    fnCP.UPDATE_DATA_PLAN);
                            if (ret.equals("OK")) {
                                carregaTela();
                                Toast.makeText(this, "Relatório atualizado com sucesso.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, ret, Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                    else if (resultCode != Activity.RESULT_CANCELED) {
                        Toast.makeText(this, "Ops.! Algum erro ocorreu na tentativa de conectar ler sua planilha.\n" +
                                        data.getStringExtra(ActGoogleLogin.RESULT_MSG),
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
        catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void carregaCSV (Activity container) {

        try {

            //File arquivo = new File(dir, file);
            File arquivo = new File(report.getOrigemDados());

            if (arquivo.exists()) {

                // 2 Passo - Criar as colunas e conteúdo do relatório
                ContentReportDao contRepDao = new ContentReportDao(container);
                contRepDao.open();
                contRepDao.deleteByReport(report.getNome());

                FileReader reader  = new FileReader(arquivo);
                BufferedReader leitor  = new BufferedReader(reader);
                String lnRead  = "";

                Integer cont = 0;

                //txInf.setText("");
                lnRead = leitor.readLine();
                String [] lista;
                while(lnRead != null) {
                    lnRead = fnCP.removerAcentos(lnRead);
                    lista = lnRead.split(";");

                    cont++;

                    if (cont == 1) {

//                        ColumnDao colDao = new ColumnDao(container);
//                        colDao.open();
//
//                        //Cria as colunas
//                        for (int i = 0; lista.length > i; i++) {
//
//                            Column column = new Column();
//                            column.setReport(reportName);
//                            column.setCodigo(String.valueOf(i + 1));
//                            column.setLabel(lista[i].toString());
//                            column.setNegrito(false);
//                            column.setSize(14);
//                            column.setFiltro(fnCP.FALSE);
//                            colDao.create(column);
//
//                            //txInf.append(i + " - " + lista[i].toString() + " | ");
//                        }
//
//                        colDao.close();

                    } else {
                        //Cria o conteúdo
                        for (int i = 0; lista.length > i; i++) {

                            ContentReport contRep = new ContentReport();
                            contRep.setReport(reportName);
                            contRep.setLine(String.valueOf(cont - 1));
                            contRep.setColumn(String.valueOf(i + 1));
                            contRep.setValue(lista[i].toString());

                            contRepDao.create(contRep);

                            //txInf.append(i + " - " + lista[i].toString() + " | ");
                        }
                    }

                    lnRead = leitor.readLine();
                }

                contRepDao.close();

                Toast.makeText(container, "Relatório Atualizado com sucesso.", Toast.LENGTH_LONG).show();

                leitor.close();
                reader.close();

                carregaTela();
            } else {
                Toast.makeText(container, "*** Não foi possível ler, arquivo não encontrado ***", Toast.LENGTH_LONG).show();
            }
        }
        catch (IOException e) {
            Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();

        }
    }

//  Usado com ReadPlanGoogle
//    public void doIt () {
//
//        if (jsReadER.getJsonContent().hasErro()) {
//
//            Toast.makeText(this, jsReadER.getJsonContent().getErro(), Toast.LENGTH_LONG).show();
//
//        } else {
//            carregaJSON(this, jsReadER.getJsonContent());
//        }
//    }

//    public void carregaJSON (Activity container, JsonDocER jsER) {
//
//        String ret = fnCP.carregaJSON(container, reportName, jsER, fnCP.UPDATE_DATA_PLAN);
//
//        if (ret.equals("OK")) {
//            Toast.makeText(container, "Relatório Atualizado com sucesso.", Toast.LENGTH_LONG).show();
//
//            carregaTela();
//        } else {
//            Toast.makeText(this, ret, Toast.LENGTH_LONG).show();
//        }
//    }
}