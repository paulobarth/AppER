package br.com.newtechs.er;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import br.com.newtechs.er.ad.fnCP;

/**
 * Created by Paulo on 20/05/2016.
 */
public class ActivityER extends Activity {

    Button btVoltarAct;
    Button btMenuPrinc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!new fnCP(this).isUserLogged()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        RelativeLayout ll = (RelativeLayout) findViewById(R.id.llPrinc);

//        if (btVoltarAct == null) {
//
//            //Criacao botão voltar
//            btVoltarAct = new Button(this);
//            RelativeLayout.LayoutParams pVoltarAct = new RelativeLayout.LayoutParams(45, 45);
//            pVoltarAct.setMargins(0, 10, 0, 0);
//    //        btVoltarAct.setText("<");
//            btVoltarAct.setBackgroundResource(android.R.drawable.ic_menu_revert);
//
//            btVoltarAct.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    finish();
//                }
//            });
//
//            if (ll != null) {
//                ll.addView(btVoltarAct, pVoltarAct);
//            }
//        }

        Boolean lCria = true;
        try {

            Button bt = (Button) ll.findViewById(Integer.parseInt("9900"));
            if (bt != null) {
                lCria = false;
            }
        } catch (Exception e) {

        }
//        //Criação botão Menu Principal
        if (lCria) {

            if (ll != null) {

                btMenuPrinc = new Button(this);

                RelativeLayout.LayoutParams pMenuPrinc = new RelativeLayout.LayoutParams(50, 50);
                pMenuPrinc.setMargins(0, 10, 0, 0);
                btMenuPrinc.setBackgroundResource(android.R.drawable.ic_menu_sort_by_size);
                btMenuPrinc.setId(Integer.parseInt("9900"));

                btMenuPrinc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ActivityER.this, MainActivity.class);
                        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });

                ll.addView(btMenuPrinc, pMenuPrinc);
            }
        }
    }
}
