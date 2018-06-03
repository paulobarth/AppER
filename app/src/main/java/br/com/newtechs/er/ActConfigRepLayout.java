package br.com.newtechs.er;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.newtechs.er.dao.Column;
import br.com.newtechs.er.dao.ColumnDao;
import br.com.newtechs.er.dao.LayoutDao;
import br.com.newtechs.er.dao.Layout;
import br.com.newtechs.er.dao.LayoutReportDao;
import br.com.newtechs.er.dao.LayoutReport;
import br.com.newtechs.er.dao.Report;
import br.com.newtechs.er.dao.ReportDao;

public class ActConfigRepLayout extends ActivityER {

    String reportName;
    ListView lsColumns;
    ColumnDao columnDao;
    EditText edLayout;
    String qtCol = new String();
    List<EditText> linesList = new ArrayList<EditText>();
    List<TextView> textList = new ArrayList<TextView>();

    List<TextView> colsList = new ArrayList<TextView>();
    int lHeight = RelativeLayout.LayoutParams.MATCH_PARENT;
    int lWidth = RelativeLayout.LayoutParams.WRAP_CONTENT;
    Button btAplicar;

    int lines;
    int colSequence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_config_rep_layout);

        lines = 0;
        colSequence = 0;

        columnDao = new ColumnDao(this);

        lsColumns = (ListView) findViewById(R.id.lsColumns);
        edLayout = (EditText) findViewById(R.id.edLayout);
        btAplicar = (Button) findViewById(R.id.btAplicar);
        btAplicar.setText(R.string.aplicar);

        Intent myLocalIntent = getIntent();
        Bundle myBundle = myLocalIntent.getExtras();
        reportName = myBundle.getString("report");

        TextView txReportName = (TextView) findViewById(R.id.txReportName);
        txReportName.setText(reportName);

        //Caso tenha nome de layout no relatório deve carregá-lo em tela
        ReportDao reportDao = new ReportDao(this);
        reportDao.open();
        try {

            Report report = reportDao.getReport(reportName);

            reportDao.close();

            if (!report.getLayout().equals("")) {
                fnCarregaLayoutExistente(report.getLayout());
            }
        }
        catch (Exception e) {
            Toast.makeText(ActConfigRepLayout.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void clickBtNewLine (View v) {

        if (lines == 5) {
            Toast.makeText(ActConfigRepLayout.this, "Limite máximo de linha atingido!", Toast.LENGTH_SHORT).show();
            return;
        }

        //Adiciona mais uma linha no TableLayout
        TableRow tr = new TableRow(this);
//        TableRow.LayoutParams pTr = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1);
//        pTr.setMargins(0, 0, 0, 0);
//        tr.setLayoutParams(pTr);
//        tr.setOrientation(TableRow.HORIZONTAL);
        TableLayout tlReport = (TableLayout) findViewById(R.id.tlReport);
        TextView tx1;
        EditText ed1;

        if (lines < linesList.size()) {

            //Utiliza já criada
            tx1 = textList.get(lines);
            tx1.setVisibility(View.VISIBLE);
            ed1 = linesList.get(lines);
            ed1.setVisibility(View.VISIBLE);

            lines++;

        } else {

            //Criam novos objetos
            tx1 = new TextView(this);
            ed1 = new EditText(this);

            lines++;
            tx1.setText("Linha " + String.valueOf(lines) + ":");
//            if (lines < 10) {
//                tx1.setWidth(80);
//            } else {
//                tx1.setWidth(90);
//            }
//            tx1.setMinimumHeight(30);
//            tx1.setHeight(35);

//            TableRow.LayoutParams p = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 0);
//            p.setMargins(0, 0, 0, 0);
//            tx1.setLayoutParams(p);


            ed1.setWidth(80);
//            ed1.setMinimumHeight(30);
//            ed1.setHeight(60);
            ed1.setHintTextColor(1);
            ed1.setInputType(InputType.TYPE_CLASS_NUMBER);
            InputFilter[] filterArray = new InputFilter[1];
            filterArray[0] = new InputFilter.LengthFilter(1);
            ed1.setFilters(filterArray);

            //Adiciona a tabela ao TableLayout
            linesList.add(ed1);
            textList.add(tx1);
        }

        //Adiciona os objetos
        try {

            tr.addView(tx1);
            tr.addView(ed1);

            tlReport.addView(tr, new RelativeLayout.LayoutParams(lHeight, lWidth));
        } catch (Exception e) {
            Toast.makeText(ActConfigRepLayout.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void clickBtDelLine (View v) {

        if (lines == 0) {
            return;
        }

        lines--;

        TableLayout tlReport = (TableLayout) findViewById(R.id.tlReport);

        TableRow tr = (TableRow) tlReport.getChildAt(lines);
        tr.removeAllViews();
        tlReport.removeViewAt(lines);

        textList.get(lines).setVisibility(View.INVISIBLE);
        linesList.get(lines).setVisibility(View.INVISIBLE);
        linesList.get(lines).setText("");
    }

    public void clickBtAplicar (View v) {

        if (btAplicar.getText().toString().equals(getString(R.string.aplicar))) {
            aplicarLayout();
        } else if (btAplicar.getText().toString().equals(getString(R.string.redefinir))) {
            redefinirLayout();
        }
    }

    public void aplicarLayout () {

        if (edLayout.getText().toString().isEmpty()) {
            Toast.makeText(ActConfigRepLayout.this, "Informar nome para layout.", Toast.LENGTH_SHORT).show();
            edLayout.requestFocus();
            return;
        }

        //Verifica se não existe o layout parametrizado
        String col = "";
        for (int cont = 1; cont <= lines; cont++) {

            EditText temp = linesList.get(cont - 1);

            if (!col.isEmpty()) {
                col = col + ";";
            }
            if (temp.getText().toString().isEmpty()) {
                Toast.makeText(ActConfigRepLayout.this, "Informar qtde linhas.", Toast.LENGTH_SHORT).show();
                temp.requestFocus();
                return;
            }

            col = col + temp.getText().toString();
        }

        LayoutDao lyDao = new LayoutDao(this);
        lyDao.open();
        Layout ly = lyDao.getLayoutByModel(String.valueOf(lines), col);
        lyDao.close();

        if (ly != null) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Encontrado layout " + ly.getCodigo() + ". Confirma reutilizá-lo?");
            final String lyName = ly.getCodigo();

            alertDialogBuilder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    ReportDao reportDao = new ReportDao(getBaseContext());
                    reportDao.open();

                    Report report = reportDao.getReport(reportName);
                    report.setLayout(lyName);
                    reportDao.update(report);
                    reportDao.close();

                    for (int cont = 1; cont <= lines; cont++) {

                        EditText temp = linesList.get(cont - 1);

                        temp.setEnabled(false);
                        temp.setFocusable(false);
                    }

                    fnCarregaLayoutExistente(lyName);
                }
            });

            alertDialogBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else {
            fnCriaLayoutNovo();
        }
    }

    //local listenet receiving callbacks from other activities
    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        try {
            if ((requestCode == 101) && (resultCode == Activity.RESULT_OK)) {

                Bundle myResults = data.getExtras();
                String labelSelected = myResults.getString("labelSelected");
                String codigoSelected = myResults.getString("codigoSelected");

                int selectedText = myResults.getInt("selectedText");

                colsList.get(selectedText - 1).setText(labelSelected);

                //Grava o coluna no layout do relatório
                LayoutReportDao layoutReportDao = new LayoutReportDao(this);
                layoutReportDao.open();

                List<LayoutReport> layoutReports = layoutReportDao.getByReportLayout(reportName,
                        edLayout.getText().toString(),
                        String.valueOf(selectedText));
                if (layoutReports.size() > 0) {

                    LayoutReport layoutReport = layoutReports.get(0);
                    layoutReport.setColumn(codigoSelected);
                    layoutReportDao.update(layoutReport);
                }
                layoutReportDao.close();
            }
            else if ((requestCode == 102) && (resultCode == Activity.RESULT_OK)) {

                Bundle myResults = data.getExtras();
                String layoutSelected = myResults.getString("layoutSelected");

                ReportDao reportDao = new ReportDao(this);
                reportDao.open();

                Report report = reportDao.getReport(reportName);
                report.setLayout(layoutSelected);
                reportDao.update(report);
                reportDao.close();

                //Gera layout para o relatório
                LayoutDao lyDao = new LayoutDao(this);
                lyDao.open();
                Layout layout = lyDao.getLayout(layoutSelected);
                lyDao.close();

                colSequence = 0;
                String[] dataCol = layout.getQtCol().split(";");

                LayoutReportDao layoutReportDao = new LayoutReportDao(this);
                layoutReportDao.open();

                for (int contRow = 1; contRow <= Integer.parseInt(layout.getQtRow()); contRow++) {

                    for (int contCol = 1; contCol <= Integer.parseInt(dataCol[contRow - 1]); contCol++) {

                        colSequence++;

                        LayoutReport layoutReport = new LayoutReport();
                        layoutReport.setReport(reportName);
                        layoutReport.setLayout(layoutSelected);
                        layoutReport.setColPosition(String.valueOf(colSequence));
                        layoutReport.setColumn("");

                        layoutReportDao.create(layoutReport);
                    }
                }
                layoutReportDao.close();

                fnCarregaLayoutExistente(report.getLayout());

                /*
                Bundle myResults = data.getExtras();
                String labelSelected = myResults.getString("labelSelected");
                String codigoSelected = myResults.getString("codigoSelected");

                int selectedText = myResults.getInt("selectedText");

                colsList.get(selectedText - 1).setText(labelSelected);

                //Grava o coluna no layout do relatório
                LayoutReportDao layoutReportDao = new LayoutReportDao(this);
                layoutReportDao.open();

                List<LayoutReport> layoutReports = layoutReportDao.getByReportLayout(reportName,
                        edLayout.getText().toString(),
                        String.valueOf(selectedText));
                if (layoutReports.size() > 0) {

                    LayoutReport layoutReport = layoutReports.get(0);
                    layoutReport.setColumn(codigoSelected);
                    layoutReportDao.update(layoutReport);
                }
                layoutReportDao.close();
                */
            }

        }
        catch (Exception e) {
            Toast.makeText(ActConfigRepLayout.this, "Retorno: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void fnCriaLayoutNovo () {

        LayoutReportDao layoutReportDao = new LayoutReportDao(this);
        layoutReportDao.open();

        //Valida caso nao exista o layout
        LayoutDao laDao = new LayoutDao(this);
        laDao.open();
        Layout la = laDao.getLayout(edLayout.getText().toString());
        laDao.close();

        if (la != null) {
            Toast.makeText(ActConfigRepLayout.this, "Layout " + la.getCodigo() + " já existente.", Toast.LENGTH_SHORT).show();
            return;
        }

        TableLayout tlLayout = (TableLayout) findViewById(R.id.tlLayout);
        tlLayout.setVisibility(TableLayout.VISIBLE);

        findViewById(R.id.txLayout).setVisibility(TextView.VISIBLE);

        findViewById(R.id.btLayout).setEnabled(false);
//        btAplicar.setEnabled(false);
        findViewById(R.id.btNewLine).setEnabled(false);
        findViewById(R.id.btDelLine).setEnabled(false);
//        findViewById(R.id.btRedefinir).setVisibility(View.VISIBLE);
        edLayout.setEnabled(false);

        colSequence = 0;
        int contRow = 0;

        //Cria layout na tela
        //for (EditText temp : linesList) {

        for (int cont = 1; cont <= lines; cont++) {

            EditText temp = linesList.get(cont - 1);

            contRow++;

            temp.setEnabled(false);
            temp.setFocusable(false);

            //Cria a tabela de acordo com as linhas e colunas informadas
            final TableRow tr = new TableRow(this);
            tr.setOrientation(TableRow.HORIZONTAL);

            for (int contCol = 1; contCol <= Integer.parseInt(temp.getText().toString()); contCol++) {

                colSequence++;

                final TextView tx1 = new TextView(this);
                tx1.setMinimumHeight(50);
                tx1.setMaxHeight(75);
                tx1.setId(colSequence);
                tx1.setBackgroundResource(R.drawable.text_view_back);
                tx1.setWidth(tr.getWidth() / Integer.parseInt(temp.getText().toString()));

                TableRow.LayoutParams p = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1);
                p.setMargins(2, 2, 2, 2);
                tx1.setLayoutParams(p);

                tx1.setOnClickListener(new View.OnClickListener() {

                    int colId = colSequence;

                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(getBaseContext(), ActConfigRepCol.class);
                        intent.putExtra("report", reportName);
                        intent.putExtra("selectColOnly", "yes");
                        intent.putExtra("selectedText", colId);
                        startActivityForResult(intent, 101);
                    }
                });

                //Guarda os texts criados
                colsList.add(tx1);

                tr.addView(tx1);

                //Cria o layout para o Relatorio
                LayoutReport layoutReport = new LayoutReport();
                layoutReport.setReport(reportName);
                layoutReport.setLayout(edLayout.getText().toString());
                layoutReport.setColPosition(String.valueOf(colSequence));
                layoutReport.setColumn("");

                layoutReportDao.create(layoutReport);
            }

            //Adiciona a tabela ao TableLayout
            tlLayout.addView(tr, new RelativeLayout.LayoutParams(lHeight, lWidth));

            //Reserva o total de colunas por linha para gravar na tabela
            if (!qtCol.equals("")) {
                qtCol = qtCol + ";";
            }

            qtCol = qtCol + temp.getText().toString();

        }

        layoutReportDao.close();

        //Toast.makeText(ActConfigRepLayout.this, linesList.size() + " linhas por " + qtCol + " colunas.", Toast.LENGTH_SHORT).show();

        //Grava o layout
        Layout layout = new Layout();
        layout.setCodigo(edLayout.getText().toString());
        layout.setQtRow(String.valueOf(lines));
        layout.setQtCol(qtCol);
        LayoutDao layoutDao = new LayoutDao(this);
        layoutDao.open();
        layoutDao.create(layout);
        layoutDao.close();


        //Grava o nome do layout no report
        ReportDao reportDao = new ReportDao(this);
        reportDao.open();
        Report report = reportDao.getReport(reportName);
        report.setLayout(edLayout.getText().toString());
        reportDao.update(report);
        reportDao.close();

        btAplicar.setText(R.string.redefinir);
    }


    private void fnCarregaLayoutExistente(String layoutName) {

        findViewById(R.id.btLayout).setEnabled(false);
        edLayout.setEnabled(false);
        edLayout.setText(layoutName);
//        btAplicar.setEnabled(false);
        findViewById(R.id.btNewLine).setEnabled(false);
        findViewById(R.id.btDelLine).setEnabled(false);
//        findViewById(R.id.btRedefinir).setVisibility(View.VISIBLE);

        LayoutDao layoutDao = new LayoutDao(this);
        layoutDao.open();
        Layout layout = layoutDao.getLayout(layoutName);

        TableLayout tlLayout = (TableLayout) findViewById(R.id.tlLayout);
        tlLayout.setVisibility(TableLayout.VISIBLE);

        colSequence = 0;
        String[] data = layout.getQtCol().split(";");

        for (int contRow = 1; contRow <= Integer.parseInt(layout.getQtRow()); contRow++) {

            //Cria a tabela de acordo com as linhas e colunas informadas
            final TableRow tr = new TableRow(this);
            tr.setOrientation(TableRow.HORIZONTAL);
            TableRow.LayoutParams pTr = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            pTr.weight = 1;
            tr.setLayoutParams(pTr);

            for (int contCol = 1; contCol <= Integer.parseInt(data[contRow - 1]); contCol++) {

                colSequence++;

                final TextView tx1 = new TextView(this);
                //tx1.setText("Col " + String.valueOf(colSequence));
                tx1.setMinimumHeight(50);
                tx1.setMaxHeight(75);
                tx1.setId(colSequence);
                tx1.setBackgroundResource(R.drawable.text_view_back);
                tx1.setWidth(tr.getWidth() / Integer.parseInt(data[contRow - 1]));

                TableRow.LayoutParams p = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1);
                p.setMargins(2, 2, 2, 2);
                tx1.setLayoutParams(p);

                tx1.setOnClickListener(new View.OnClickListener() {

                    int colId = colSequence;

                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(getBaseContext(), ActConfigRepCol.class);
                        intent.putExtra("report", reportName);
                        intent.putExtra("selectColOnly", "yes");
                        intent.putExtra("selectedText", colId);
                        startActivityForResult(intent, 101);
                    }
                });

                //Carrega a coluna
                LayoutReportDao layoutReportDao = new LayoutReportDao(this);
                layoutReportDao.open();
                List<LayoutReport> layoutReports = layoutReportDao.getByReportLayout(reportName, layoutName, String.valueOf(colSequence));

                if (layoutReports.size() > 0) {

                    ColumnDao columnDao = new ColumnDao(this);
                    columnDao.open();
                    Column column = columnDao.getByReportCol(reportName, layoutReports.get(0).getColumn());

                    if (column != null) {
                        tx1.setText(column.getLabel());
                    }
                }

                //Guarda os texts criados
                colsList.add(tx1);

                tr.addView(tx1);
            }

            //Adiciona a tabela ao TableLayout
            tlLayout.addView(tr, new RelativeLayout.LayoutParams(lHeight, lWidth));
        }
        //fff
        btAplicar.setText(R.string.redefinir);
    }


//    public void clickBtRedefinir (View v) {
    public void redefinirLayout () {

        textList.clear();
        colsList.clear();
        linesList.clear();

        LayoutReportDao lrDao = new LayoutReportDao(this);
        lrDao.open();
        lrDao.deleteByReport(reportName);
        lrDao.close();

        ReportDao rpDao = new ReportDao(this);
        rpDao.open();

        Report rp = rpDao.getReport(reportName);
        rp.setLayout("");
        rpDao.update(rp);

        rpDao.close();

        onCreate(null);
        onStart();

        btAplicar.setText(R.string.aplicar);
    }

    public void clickBtLayout (View v) {

        Intent intent = new Intent(getBaseContext(), ActLayoutList.class);
        startActivityForResult(intent, 102);
    }
}