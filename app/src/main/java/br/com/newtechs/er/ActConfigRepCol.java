package br.com.newtechs.er;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import br.com.newtechs.er.dao.Column;
import br.com.newtechs.er.dao.ColumnDao;
import br.com.newtechs.er.ad.fnCP;

public class ActConfigRepCol extends ActivityER {

    String reportName;
    ListView lsColumns;
    ColumnDao columnDao;
    Boolean selectColOnly;
    Intent superIntent;
    int superText;
    List<Column> columnList = new ArrayList<Column>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_config_rep_col);

        columnDao = new ColumnDao(this);

        lsColumns = (ListView) findViewById(R.id.lsColumns);

        superIntent = getIntent();
        Bundle myBundle = superIntent.getExtras();
        reportName = myBundle.getString("report");
        selectColOnly = myBundle.getString("selectColOnly").equals("yes");

        TextView txReportName = (TextView) findViewById(R.id.txReportName);
        txReportName.setText(reportName);

        if (selectColOnly) {
            superText = myBundle.getInt("selectedText");
        }

        setResult(Activity.RESULT_CANCELED, superIntent);

        carregaTela();
    }

    private void carregaTela() {

        fnCarregaLista();

        lsColumns.setAdapter(new IconicAdapter(this));

        lsColumns.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                 @Override
                 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                 Bundle myBundle = superIntent.getExtras();

                 Column col = columnList.get(position);
                 myBundle.putString("labelSelected", col.getLabel());
                 myBundle.putString("codigoSelected", col.getCodigo());
                 myBundle.putInt("selectedText", superText);

                 superIntent.putExtras(myBundle);

                 setResult(Activity.RESULT_OK, superIntent);

                 finish();
                 }
             }
        );
    }

    class IconicAdapter extends ArrayAdapter {

        Activity context;
        IconicAdapter(Activity context) {
            super(context, R.layout.activity_act_config_col_list, columnList);
            this.context = context;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View row = inflater.inflate(R.layout.activity_act_config_col_list, null);

            TextView txColumn = (TextView) row.findViewById(R.id.txColumn);
            TextView txSize = (TextView) row.findViewById(R.id.txSize);
            RelativeLayout rlConfig = (RelativeLayout) row.findViewById(R.id.rlConfig);
            CheckBox chNegrito = (CheckBox) row.findViewById(R.id.chNegrito);
            CheckBox chFiltro = (CheckBox) row.findViewById(R.id.chFiltro);
            Spinner spSize = (Spinner) row.findViewById(R.id.spSize);
            Column col = columnList.get(position);

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.letter_size, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spSize.setAdapter(adapter);

            txColumn.setText(col.getLabel());

            if (!selectColOnly) {

                chNegrito.setChecked(col.isNegrito());
                chFiltro.setChecked(col.getFiltro().equals(fnCP.TRUE));

                spSize.setSelection(col.getSizeArrayPos());

                chNegrito.setOnClickListener(new View.OnClickListener() {

                    int posCol = position;

                    @Override
                    public void onClick(View v) {

                        columnDao.open();
                        Column column = columnList.get(posCol);

                        CheckBox chN = (CheckBox) v.findViewById(R.id.chNegrito);
                        column.setNegrito(chN.isChecked());

                        columnDao.update(column);
                        columnDao.close();
                    }
                });

                chFiltro.setOnClickListener(new View.OnClickListener() {

                    int posCol = position;

                    @Override
                    public void onClick(View v) {

                        columnDao.open();
                        Column column = columnList.get(posCol);

                        CheckBox chF = (CheckBox) v.findViewById(R.id.chFiltro);
                        if (chF.isChecked()) {
                            column.setFiltro(fnCP.TRUE);
                        } else {
                            column.setFiltro(fnCP.FALSE);
                        }

                        columnDao.update(column);
                        columnDao.close();
                    }
                });

                spSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener () {

                    int posCol = position;

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        String f = (String) parent.getItemAtPosition(position);

                        columnDao.open();
                        Column column = columnList.get(posCol);

                        column.setSize(Integer.valueOf(f));

                        columnDao.update(column);
                        columnDao.close();

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            } else {
                rlConfig.setVisibility(View.INVISIBLE);
//                chNegrito.setVisibility(View.INVISIBLE);
//                chFiltro.setVisibility(View.INVISIBLE);
//                txSize.setVisibility(View.INVISIBLE);
//                spSize.setVisibility(View.INVISIBLE);
            }

            return row;
        }
    }

    private void fnCarregaLista () {

        columnDao.open();

        List<Column> columns;

        columns = columnDao.getByReport(reportName, null);

        columnList.clear();

        for (Column temp : columns) {
            columnList.add(temp);
        }

        columnDao.close();
    }
}
