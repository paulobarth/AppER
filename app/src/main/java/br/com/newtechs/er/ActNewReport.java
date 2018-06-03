package br.com.newtechs.er;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import br.com.newtechs.er.ad.fnCP;
import br.com.newtechs.er.dao.Column;
import br.com.newtechs.er.dao.ColumnDao;
import br.com.newtechs.er.dao.ContentReport;
import br.com.newtechs.er.dao.ContentReportDao;
import br.com.newtechs.er.dao.Report;
import br.com.newtechs.er.dao.ReportDao;
import br.com.newtechs.er.google.ActGoogleLogin;
import br.com.newtechs.er.google.PickFileWithOpenerActivity;
import br.com.newtechs.er.google.SheetApiTest;

public class ActNewReport extends ActivityER {

    private static final int CHOOSE_FILE_REQUESTCODE = 1;
    private static final int GOOGLE_DRIVE_REQUESTCODE = 2;
    private static final int GOOGLE_SHEET_LOAD = 3;

    private String idGoogleSelected = "";

//    EditText edReportName;
    AutoCompleteTextView edReportName;
    TextView txInfOrigem;
    RadioButton rbArqLocal;
    RadioButton rbGoogleDrive;
    Button btAtz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_new_report);

        txInfOrigem = (TextView) findViewById(R.id.txInfOrigem);
//        edReportName = (EditText) findViewById(R.id.edReportName);
        edReportName = (AutoCompleteTextView) findViewById(R.id.edReportName);
        rbArqLocal = (RadioButton) findViewById(R.id.rbArqLocal);
        rbGoogleDrive = (RadioButton) findViewById(R.id.rbGoogleDrive);
        btAtz = (Button) findViewById(R.id.btAtz);


//        Diretório SDCard
//        txInfOrigem.setText(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/agenda.csv");
//        edDir.setText(Environment.getExternalStorageDirectory().getPath());

//        Diretório da Aplicação
//        edDir.setText(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath());
    }

    public void clickBuscar (View v) {

//      TESTE **********************
//        rbGoogleDrive.setChecked(true);
//        idGoogleSelected = edReportName.getText().toString();
//        Toast.makeText(this, idGoogleSelected, Toast.LENGTH_SHORT).show();
//        txInfOrigem.setText("Planilha: " + "Teste");
//
//        btAtz.setVisibility(Button.VISIBLE);

        if (rbArqLocal.isChecked()) {

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);

            // special intent for Samsung file manager
            Intent sIntent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
            // if you want any file type, you can skip next line
            sIntent.putExtra("CONTENT_TYPE", "*/*");
            sIntent.addCategory(Intent.CATEGORY_DEFAULT);

            Intent chooserIntent;
            if (getPackageManager().resolveActivity(sIntent, 0) != null){
                // it is device with samsung file manager
                chooserIntent = Intent.createChooser(sIntent, "Open file");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { intent});
            }
            else {
                chooserIntent = Intent.createChooser(intent, "Open file");
            }

            try {
                startActivityForResult(chooserIntent, CHOOSE_FILE_REQUESTCODE);
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(getApplicationContext(), "No suitable File Manager was found.", Toast.LENGTH_SHORT).show();
            }
        }

        if (rbGoogleDrive.isChecked()) {

//            OLD
//            Intent intent = new Intent(this, ActGoogleLogin.class);
//            startActivityForResult(intent, GOOGLE_DRIVE_REQUESTCODE);

            Intent intent = new Intent(this, PickFileWithOpenerActivity.class);
            startActivityForResult(intent, GOOGLE_DRIVE_REQUESTCODE);

        }
    }

//  Usado com ReadPlanGoogle
//    public void doIt () {
//
//        if (jsReadER.getJsonContent().hasErro()) {
//            Toast.makeText(ActNewReport.this, jsReadER.getJsonContent().getErro(),
//                    Toast.LENGTH_LONG).show();
//
//        } else {
//            String ret = fnCP.carregaJSON(this, edReportName.getText().toString(), jsReadER.getJsonContent(),
//                                            fnCP.CREATE_NEW_PLAN);
//
//            if (ret.equals("OK")) {
//                Toast.makeText(this, "Relatório Criado com sucesso.", Toast.LENGTH_LONG).show();
//
//                finish();
//            } else {
//                Toast.makeText(this, ret, Toast.LENGTH_LONG).show();
//            }
//        }
//    }


    public void clickAtz (View v) {

        if (edReportName.getText().toString().isEmpty()) {
            Toast.makeText(this, "Informe nome do relatório.", Toast.LENGTH_LONG).show();
            return;
        }

        ReportDao repDao = new ReportDao(this);
        repDao.open();
        Report report = repDao.getReport(edReportName.getText().toString());
        repDao.close();
        if (report != null) {
            Toast.makeText(this, "Relatório já existente.", Toast.LENGTH_LONG).show();
            return;
        }

        if (rbArqLocal.isChecked()) {

            //Cria diretório se ele não existe.
            File fileDir = new File(txInfOrigem.getText().toString());
            if (!fileDir.mkdirs()) {
            }

            carregaCSV(this, edReportName.getText().toString(), txInfOrigem.getText().toString());
        }

        if (rbGoogleDrive.isChecked()) {

            if (idGoogleSelected.isEmpty()) {
                Toast.makeText(this, "Nenhuma planilha do Google selecionada.", Toast.LENGTH_LONG).show();
                return;
            }

//            Antigo Modo
//            jsReadER = new ReadPlanGoogle();
//            jsReadER.setId(idGoogleSelected);
//            jsReadER.setType(ReadPlanGoogle.SPREADSHEET_TYPE);
//            //Ao finalizar chamará a rotina doIt
//            jsReadER.execute(this, ActNewReport.class.getName());

//            Novo Modo
            Intent intent = new Intent(this, SheetApiTest.class);
            Bundle data = new Bundle();
            data.putString(SheetApiTest.ID_GOOGLE_SELECTED, idGoogleSelected);
            intent.putExtras(data);
            startActivityForResult(intent, GOOGLE_SHEET_LOAD);
        }
    }


    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            //user requestCode do find out who is talking back us
            switch (requestCode) {
                case (CHOOSE_FILE_REQUESTCODE): {
                    if (resultCode == Activity.RESULT_OK) {

                        String selectedContact = data.getDataString();
                        txInfOrigem.setText(selectedContact.toString().replace("file://", ""));

                        btAtz.setVisibility(Button.VISIBLE);
                    }
                    else {
                        //user pressed the back button
                    }
                    break;
                }
                case (GOOGLE_DRIVE_REQUESTCODE): {
                    if (resultCode == Activity.RESULT_OK) {

//                        OLD
//                        Bundle myBundle = data.getExtras();
//                        String fileSelected = myBundle.getString(ActGoogleLogin.FILE_SELECTED_NAME);
//                        idGoogleSelected = myBundle.getString(ActGoogleLogin.FILE_SELECTED_ID);
//                        txInfOrigem.setText("Planilha: " + fileSelected.toString());
                        DriveId driveId = (DriveId) data.getParcelableExtra(
                                OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
                        idGoogleSelected = driveId.getResourceId();
                        txInfOrigem.setText("Planilha: " + driveId.getResourceId());

                        btAtz.setVisibility(Button.VISIBLE);
                    }
                    else if (resultCode != Activity.RESULT_CANCELED) {
                        Toast.makeText(ActNewReport.this, "Ops.! Algum erro ocorreu na tentativa de conectar no Google.\n" +
                                        data.getStringExtra(ActGoogleLogin.RESULT_MSG),
                                        Toast.LENGTH_LONG).show();
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
                            txInfOrigem.setText("Nulo");
                        } else {
                            txInfOrigem.setText("Retorno: " + "\nID:" + json);

                            JsonDocER jsonDoc = new JsonDocER();
                            jsonDoc.setTipoOrigem(fnCP.GOOGLE_DRIVE);
                            jsonDoc.setOrigemDados(idGoogleSelected);
                            jsonDoc.setFormatoOrigem(SheetApiTest.SPREADSHEET_TYPE);
                            jsonDoc.write(json);
                            String ret = fnCP.carregaJSON(this, edReportName.getText().toString(), jsonDoc,
                                    fnCP.CREATE_NEW_PLAN);

                            if (ret.equals("OK")) {
                                Toast.makeText(this, "Relatório Criado com sucesso.", Toast.LENGTH_LONG).show();

                                finish();
                            } else {
                                Toast.makeText(this, ret, Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                    else if (resultCode != Activity.RESULT_CANCELED) {
                        Toast.makeText(ActNewReport.this, "Ops.! Algum erro ocorreu na tentativa de conectar ler sua planilha.\n" +
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

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }


    public void carregaCSV (Activity container, String reportName, String fileName) {

        try {

/* Criação do arquivo
            File file = new File(edDir.getText().toString(), edFile.getText().toString());
            FileOutputStream out = new FileOutputStream(file);
            PrintWriter print_writer = new PrintWriter(out);
            print_writer.print("Teste de texto");
            print_writer.close();
*/

            // Gets the data repository in write mode
            //DbDefinition DbCP = new DbDefinition(this);
            //SQLiteDatabase db = DbCP.getWritableDatabase();

            //Código para eliminar a tabela.
            // Define 'where' part of query.
            //String selection = TableDefinition.Despesas.COLUMN_NAME_COD_DESPESA + " LIKE ?";
            // Specify arguments in placeholder order.
            //String[] selectionArgs = { "oi" };
            // Issue SQL statement.
            //db.delete(TableDefinition.Despesas.TABLE_NAME, selection, selectionArgs);
            //ou
            //DespesaDao despesaDao = new DespesaDao(this);
            //despesaDao.open();
            //despesaDao.deleteAll();

            //File arquivo = new File(dir, file);
            File arquivo = new File(fileName);

            if (arquivo.exists()) {

                // 1 Passo - Criar o relatório
                //String reportName = new String(reportName);

                ReportDao reportDao = new ReportDao(container);
                reportDao.open();
                Report report = new Report();
                report.setCodigo(reportName);
                report.setNome(reportName);
                report.setTipoOrigem(fnCP.CSVLocal);
                report.setOrigemDados(arquivo.getAbsoluteFile().toString());
                report.setFormatoOrigem(SheetApiTest.SPREADSHEET_TYPE);
                report.setTipoAtz(fnCP.LOCAL_TYPE);
                report.setLayout("");
                report.setVisivel(fnCP.FALSE);
                report.setUserLoginCol("");
                report.setUserID(fnCP.getUserCode(fnCP.getLoggedUser()));

                reportDao.create(report);
                reportDao.close();

                // 2 Passo - Criar as colunas e conteúdo do relatório

                ContentReportDao contRepDao = new ContentReportDao(container);
                contRepDao.open();

                FileReader      reader  = new FileReader(arquivo);
                BufferedReader  leitor  = new BufferedReader(reader);
                String lnRead  = "";

                Integer cont = 0;

                lnRead = leitor.readLine();
                String [] lista;
                while(lnRead != null) {
                    lnRead = fnCP.removerAcentos(lnRead);
                    lista = lnRead.split(";");

                    cont++;

                    if (cont == 1) {

                        ColumnDao colDao = new ColumnDao(container);
                        colDao.open();

                        //Cria as colunas
                        for (int i = 0; lista.length > i; i++) {

                            Column column = new Column();
                            column.setReport(reportName);
                            column.setCodigo(String.valueOf(i + 1));
                            column.setLabel(lista[i].toString());
                            column.setNegrito(false);
                            column.setSize(14);
                            column.setFiltro(fnCP.FALSE);
                            colDao.create(column);
                        }

                        colDao.close();

                    } else {
                        //Cria o conteúdo
                        for (int i = 0; lista.length > i; i++) {

                            ContentReport contRep = new ContentReport();
                            contRep.setReport(reportName);
                            contRep.setLine(String.valueOf(cont - 1));
                            contRep.setColumn(String.valueOf(i + 1));
                            contRep.setValue(lista[i].toString());

                            contRepDao.create(contRep);
                        }
                    }

                    lnRead = leitor.readLine();
                }

                contRepDao.close();

                Toast.makeText(container, "Relatório Criado com sucesso.", Toast.LENGTH_LONG).show();

                leitor.close();
                reader.close();

                finish();
            } else {
                Toast.makeText(ActNewReport.this, "*** Não foi possível ler, arquivo não encontrado ***", Toast.LENGTH_LONG).show();
            }
        }
        catch (IOException e) {
            Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();

        }
    }
}
