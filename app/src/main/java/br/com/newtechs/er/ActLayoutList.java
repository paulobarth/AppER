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
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.newtechs.er.dao.Layout;
import br.com.newtechs.er.dao.LayoutDao;

public class ActLayoutList extends Activity {

    ListView lsReport;
    List<Layout> layoutList = new ArrayList<Layout>();
    String[] listCol;
    Intent superIntent;
    int totalRow;
    int totalCol;
    int colSequence;
    int lHeight = RelativeLayout.LayoutParams.FILL_PARENT;
    int lWidth = RelativeLayout.LayoutParams.WRAP_CONTENT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_act_layout_list);

        lsReport = (ListView) findViewById(R.id.lsReport);

        superIntent = getIntent();

        setResult(Activity.RESULT_CANCELED, superIntent);

        try {
            carregaTela();
        } catch (Exception e) {
            Toast.makeText(ActLayoutList.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            carregaTela();
        } catch (Exception e) {
            Toast.makeText(ActLayoutList.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void carregaTela() {

        fnCarregaLista();

        lsReport.setAdapter(new IconicAdapter(this));

        lsReport.setOnItemClickListener(new AdapterView.OnItemClickListener() {

             @Override
             public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                 Bundle myBundle = new Bundle();

                 Layout ly = layoutList.get(position);
                 myBundle.putString("layoutSelected", ly.getCodigo());

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
            super(context, R.layout.activity_act_tab_layout_list, layoutList);
            this.context = context;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View row = inflater.inflate(R.layout.activity_act_tab_layout_list, null);

            Layout ly = layoutList.get(position);

            totalRow = Integer.parseInt(ly.getQtRow());
            listCol = ly.getQtCol().split(";");

            colSequence = 0;

            TableLayout tlReport = (TableLayout) row.findViewById(R.id.tlReport);
            TextView txName = (TextView) row.findViewById(R.id.txName);
            txName.setText(ly.getCodigo());

            for (int contRow = 0; contRow < totalRow; contRow++) {

                totalCol = Integer.parseInt(listCol[contRow]);

                TableRow tr1 = new TableRow(getBaseContext());
                tr1.setOrientation(TableRow.HORIZONTAL);

                for (int contCol = 0; contCol < totalCol; contCol++) {

                    colSequence++;

                    final TextView tx1 = new TextView(context);
                    tx1.setMinimumHeight(25);
                    //tx1.setMaxHeight(75);
                    //tx1.setVisibility(TextView.VISIBLE);
                    //tx1.setHeight(40);
                    //tx1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                    //tx1.setId(1);
                    //tx1.setGravity(TextView.TEXT_ALIGNMENT_CENTER);
                    tx1.setBackgroundResource(R.drawable.text_view_back);
                    //tx1.setTextColor(Color.BLACK);
                    tx1.setWidth(tr1.getWidth() / totalCol);

                    TableRow.LayoutParams p = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1);
                    p.setMargins(2, 2, 2, 2);
                    tx1.setLayoutParams(p);

                    tr1.addView(tx1);

                }

                tlReport.addView(tr1, new RelativeLayout.LayoutParams(lHeight, lWidth));
            }

            return row;
        }
    }


    private void fnCarregaLista () {

        LayoutDao layoutDao = new LayoutDao(this);
        layoutDao.open();

        layoutList = layoutDao.getAll();

        layoutDao.close();
    }
}