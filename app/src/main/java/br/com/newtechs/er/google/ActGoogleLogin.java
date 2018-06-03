package br.com.newtechs.er.google;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
//import com.google.api.services.sheets.v4.SheetsScopes;
//import com.google.api.services.sheets.v4.model.Spreadsheet;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.newtechs.er.ActivityER;
import br.com.newtechs.er.R;
import br.com.newtechs.er.ad.fnCP;

public class ActGoogleLogin extends ActivityER {

    private final int MY_PERMISSIONS_REQUEST_GET_ACCOUNT = 101;
    public static final String RESULT_MSG = "RESULT_MSG";
    public static final String FILE_SELECTED_NAME = "FILE_SELECTED_NAME";
    public static final String FILE_SELECTED_ID = "FILE_SELECTED_ID";

    GoogleAccountCredential mCredential;
//    private TextView mOutputText;
    ProgressDialog mProgress;
    Intent superIntent;
    ListView lsFiles;
    List<File> filesList = new ArrayList<File>();

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { DriveScopes.DRIVE_METADATA_READONLY };
    //private static final String[] SCOPES = { DriveScopes.DRIVE };

    /**
     * Create the main activity.
     * @param savedInstanceState previously saved instance data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_google_login);

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Conectando com Google Drive ...");

        lsFiles = (ListView) findViewById(R.id.lsFiles);

        superIntent = getIntent();

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.GET_ACCOUNTS)) {
                setReturn(9999, "Verifique as permissões de acesso nas configurações do APP.");
                finish();

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.GET_ACCOUNTS},
                        MY_PERMISSIONS_REQUEST_GET_ACCOUNT);

                // MY_PERMISSIONS_REQUEST_GET_ACCOUNT is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {

//          Selecionar a conta do google todas as vezes.
//            Para conectar na ultima conta escolhida automaticamente
            SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
            mCredential = GoogleAccountCredential.usingOAuth2(
                    getApplicationContext(), Arrays.asList(SCOPES))
                    .setBackOff(new ExponentialBackOff())
                    .setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));
//                    .setSelectedAccountName(null);
        }
    }


    private void carregaTela() {
//
        lsFiles.setAdapter(new FilesAdapter(ActGoogleLogin.this));

        lsFiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Bundle myBundle = new Bundle();

            myBundle.putString(FILE_SELECTED_NAME, filesList.get(position).getName());
            myBundle.putString(FILE_SELECTED_ID, filesList.get(position).getId());

            superIntent.putExtras(myBundle);

            setResult(Activity.RESULT_OK, superIntent);

            finish();
                }
            }
        );
    }

    class FilesAdapter extends ArrayAdapter {

        Activity context;
        FilesAdapter(Activity context) {
            super(context, R.layout.activity_act_standard_list, filesList);
            this.context = context;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View row = inflater.inflate(R.layout.activity_act_standard_list, null);

            TextView txTexto = (TextView) row.findViewById(R.id.txTexto);
            txTexto.setText(filesList.get(position).getName());

            return row;
        }
    }

    /**
     * Called whenever this activity is pushed to the foreground, such as after
     * a call to onCreate().
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (isGooglePlayServicesAvailable()) {
            refreshResults();
        } else {
            setReturn(9999, "Necessário Google Play Services. Após instalar, reiniciar este App.");
            finish();
//            mOutputText.setText("Google Play Services required: " +
//                    "after installing, close and relaunch this app.");
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    isGooglePlayServicesAvailable();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
//                FFF
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        mCredential.setSelectedAccountName(accountName);
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    setReturn(0, "Conta não especificada.");
//                    mOutputText.setText("Conta não especificada.");
                    finish();
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode != RESULT_OK) {
                    chooseAccount();
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * Attempt to get a set of data from the Drive API to display. If the
     * email address isn't known yet, then call chooseAccount() method so the
     * user can pick an account.
     */
    private void refreshResults() {
        if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else {
            if (new fnCP(this).isDeviceOnline()) {
                new MakeRequestTask(mCredential).execute();
            } else {
                setReturn(9999, "Sem conexão com a internet.");
                finish();
            }
        }
    }

    /**
     * Starts an activity in Google Play Services so the user can pick an
     * account.
     */
    private void chooseAccount() {
        startActivityForResult(
                mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
    }

    /**
     * Check that Google Play services APK is installed and up to date. Will
     * launch an error dialog for the user to update Google Play Services if
     * possible.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        final int connectionStatusCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
            return false;
        } else if (connectionStatusCode != ConnectionResult.SUCCESS ) {
            return false;
        }
        return true;
    }

    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                connectionStatusCode,
                ActGoogleLogin.this,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    public static String readContent(InputStream IS) {
        String sRetorno = "";
        InputStream inputstream;
        try {
            //inputstream = new BufferedInputStream( new FileInputStream("/tmp/input_text"));
            inputstream = new BufferedInputStream(IS);
            int data = inputstream.read();
            while (data != -1) {
                sRetorno = sRetorno + (char) data;
                data = inputstream.read();
            }
            inputstream.close();
        } catch (FileNotFoundException e1) {
            // Auto-generated catch block e1.printStackTrace();
            sRetorno = "ERRO: " + e1.getMessage();
        } catch (IOException e) {
            // Auto-generated catch block e.printStackTrace();
            sRetorno = "ERRO: " + e.getMessage();
        }

        return sRetorno;
    }


/**
 * An asynchronous task that handles the Drive API call.
 * Placing the API calls in their own task ensures the UI stays responsive.
 */
private class MakeRequestTask extends AsyncTask<Void, Void, List<File>> {
    private com.google.api.services.drive.Drive mService = null;
    private Exception mLastError = null;

    public MakeRequestTask(GoogleAccountCredential credential) {
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        String f = getBaseContext().getApplicationInfo().name;
        mService = new com.google.api.services.drive.Drive.Builder(
                transport, jsonFactory, credential)
//                .setApplicationName("NewTechER")
                .setApplicationName("br.com.newtechs.er")
                .build();
    }

    /**
     * Background task to call Drive API.
     * @param params no parameters needed for this task.
     */
    @Override
    protected List<File> doInBackground(Void... params) {
        try {
            return getDataFromApi();
        } catch (IOException e) {
            mLastError = e;
            cancel(true);
            return null;
        }
    }

    /**
     * Fetch a list of up to 10 file names and IDs.
     * @return List of Strings describing files, or an empty list if no files
     *         found.
     * @throws IOException
     */
    private List<File> getDataFromApi() throws IOException {



//            // Get Spreadsheet content in TXT format.
//            List<String> fileInfo = new ArrayList<String>();
//            FileList result = mService.files().list()
//                    .execute();
////            FileList result = mService.files().list()
////                    .setPageSize(10)
////                    .setFields("nextPageToken, items(id, name)")
////                    .execute();
//            List<File> files = result.getFiles();
//            if (files != null) {
//                for (File file : files) {
//
//                    fileInfo.add(String.format("%s (%s)\n",
//                            file.getName(), file.getId()));
//                }
//            }


        //fff
        List<File> fileInfo = new ArrayList<File>();
        try {
//            FileList result = mService.files().list()
//                    .setPageSize(10)
//                    .setFields("nextPageToken, files(id, name)")
//                    .execute();
//            List<File> files = result.getFiles();
            mService.files().list().execute();
            fileInfo.add(new File().setName("asd"));

//            FileList result = mService.files().list().execute();

        } catch (IOException e) {
            Toast.makeText(ActGoogleLogin.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(ActGoogleLogin.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

//
//        // Get a list of up to 10 files.
//        List<File> fileInfo = new ArrayList<File>();
//        try {
//
////            FileList result = mService.files().list().execute();
//            FileList result = null;
//
////            FileList result = mService.files().list()
////                    .setPageSize(10)
////                    .setFields("nextPageToken, items(id, name)")
////                    .execute();
//
//
//    fileInfo.add(new File());
//
//    if (result != null) {
//
//
//        List<File> files = result.getFiles();
//        String fileURL = "";
//        if (files != null) {
//            for (File file : files) {
//
//                //fff
//                if (file.getMimeType().contains("spreadsheet")) {
//                    fileInfo.add(file);
//                }
//
////                    JSON Format:
////                    https://spreadsheets.google.com/feeds/list/FILEID/od6/public/basic?alt=json
////
////                    XML Format:
////                    https://spreadsheets.google.com/feeds/list/FILEID/od6/public/values
//
//
//
////                    USANDO COMANDO GET
////                    File fExcel = mService.files().get(file.getId()).execute();
////                    try {
////                        Permission p = fExcel.getPermissions().get(0);
////                        fileInfo.add(p.getAllowFileDiscovery().toString());
////                    } catch (Exception e) {
////                        // An error occurred.
////                        fileInfo.add("ERROR: " + e.getMessage());
////                        //e.printStackTrace();
////                        //return null;
////                    }
//
//
//
////                    USANDO COMANDO EXPORT
////                    File fExcel = mService.files().get(file.getId()).execute();
////
////                    String fileId = fExcel.getId();
////                    OutputStream outputStream = new ByteArrayOutputStream();
////
////                    //mService.files().export(fileId, "application/csv").
////                    mService.files().export(fileId, "application/json").set("alt", "json")
////                            .executeMediaAndDownloadTo(outputStream);
//
//
//
//
//
//
//
////                    MODELO DE JSON A SER MONTADO COMO CAMADA PARA ENTRAR NO BANCO DE DADOS DO APP.
////                    {
////                        "nome-relatorio":"exemploa01",
////                            "identificador": "askkjalskjlaskljkls",
////                            "colunas":["campo01", "campo02]},
////                        "registros":[
////                        {
////                            "campo1":"informacao"
////                            "campo2":"informacao"
////                        },
////                        {
////                            "campo1":"informacao"
////                            "campo2":"informacao"
////                        }
////                        ]
////                    }
////
////                //fileInfo.add(String.format("%s (%s)\n", file.getName(), file.getId()));
////                fileInfo.add("Nome do arquivo: " + file.getName());
////                fileInfo.add("\n");
////                fileInfo.add("ID do arquivo: " + file.getId());
////                fileInfo.add("\n");
////                File fExcel = mService.files().get(file.getId()).execute();
////                //fileURL = "https://www.googleapis.com/drive/v2/files/" + fExcel.getId();
////
////                // ID exemple: 1aH32CMGpCqS53U9ZXm2dpjp-E2BLw3jjQ_remHBeyR8
////                // Link JSon: https://spreadsheets.google.com/feeds/list/FILEID/od6/public/basic?alt=json
////                //1aH32CMGpCqS53U9ZXm2dpjp-E2BLw3jjQ_remHBeyR8
////                //1-14dMBBGdh2udST9vW77z_oOtz0ekMn49d-slV9TBoo
////                fileURL = "https://spreadsheets.google.com/feeds/list/" +
////                        fExcel.getId() + "/od6/public/basic?alt=json";
////                //https://www.googleapis.com/drive/v2/files/fileId
////                //if (fExcel.getDownloadUrl() != null && file.getDownloadUrl().length() > 0) {
////
////                try {
////                    HttpResponse resp =
////                            mService.getRequestFactory().buildGetRequest(new GenericUrl(fileURL))
////                                    .execute();
////
////                    JSONObject jsonObject = new JSONObject(readContent(resp.getContent()));
////                    JSONObject jsFeed = new JSONObject(jsonObject.getString("feed"));
////                    JSONObject jsTotalResults = new JSONObject(jsFeed.getString("openSearch$totalResults"));
////                    int qtEntry = Integer.parseInt(jsTotalResults.getString("$t")) - 1;
////
////                    JSONArray jsEntry = new JSONArray(jsFeed.getString("entry"));
////
////                    //Navega em cada registro
////                    for (int cont = 0; cont <= qtEntry; cont++) {
////
////                        JSONObject jsItem = jsEntry.getJSONObject(cont);
////                        //fileInfo.add("Content: " + jsItem.getString("content"));
////
////                        JSONObject jsContent = jsItem.getJSONObject("content");
////                        //fileInfo.add(jsContent.getString("$t"));
////
////                        //JSONObject jsInf = new JSONObject("{" + jsContent.getString("$t") + "}");
////
////                        //JSONObject jsInf = new JSONObject("{nomecol01:oi, estadocol02:SC}");
////                        JSONObject jsInf = new JSONObject("{\"nome col01\": \"Jaqueline Barth\", cidadecol02: Barueri, estadocol03: SP, telcol04: 1198222-3456}");
////                        fileInfo.add("Col01: "+ jsInf.getString("nome col01"));
////
////
////                        fileInfo.add("\n");
////
////
////                        //Iterator<String> content = jsContent.keys();
////
////                        //String field = jsContent.getString("$t");
////                        //field = "["+ field + "]";
////
////                        //JSONArray ja = new JSONArray(field);
////
////                        //fileInfo.add(fields);
////
////                        //Possui dado da primeira coluna. Sugerir que seja criado sempre com uma coluna identificadora
////                        //fileInfo.add("Title: " + jsItem.getString("title"));
////
////
////                    }
////                } catch (IOException e) {
////                    // An error occurred.
////                    fileInfo.add("ERROR: " + e.getMessage());
////                    //e.printStackTrace();
////                    //return null;
////                } catch (Exception ex) {
////                    // An error occurred.
////                    fileInfo.add("ERROR: " + ex.getMessage());
////                    //e.printStackTrace();
////                    //return null;
////                }
////                break;
//
//            }
//
//
//        }
//    }
//
//
//        } catch (Exception e) {
//            Toast.makeText(ActGoogleLogin.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//        }

        return fileInfo;
    }


    @Override
    protected void onPreExecute() {
//        mOutputText.setText("");
        mProgress.show();
    }

    @Override
    protected void onPostExecute(List<File> output) {
        mProgress.hide();
        if (output == null) {
            setReturn(9999, "Acesso não liberado.");
            finish();
        } if (output.size() == 0) {
            setReturn(9999, "Nenhum arquivo encontrado.");
            finish();
        } else {
            filesList = output;
            carregaTela();
        }
    }

    @Override
    protected void onCancelled() {
        mProgress.hide();
        if (mLastError != null) {
            if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                showGooglePlayServicesAvailabilityErrorDialog(
                        ((GooglePlayServicesAvailabilityIOException) mLastError)
                                .getConnectionStatusCode());
            } else if (mLastError instanceof UserRecoverableAuthIOException) {
                startActivityForResult(
                        ((UserRecoverableAuthIOException) mLastError).getIntent(),
                        ActGoogleLogin.REQUEST_AUTHORIZATION);
            } else {
                setReturn(9999, "Seguinte erro ocorrou: " + mLastError.getMessage());
//                mOutputText.setText("The following error occurred:\n" + mLastError.getMessage());
                finish();
            }
        } else {
            setReturn(9999, "Solicitação cancelada.");
//            mOutputText.setText("Request cancelled.");
            finish();
        }
    }
}





    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_GET_ACCOUNT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
//                  CHAMAR QUANDO O USUARIO DER OK DA PERMISSAO DE ACESSO AOS CONTATOS

                    // Initialize credentials and service object.
                    SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
                    mCredential = GoogleAccountCredential.usingOAuth2(
                            getApplicationContext(), Arrays.asList(SCOPES))
                            .setBackOff(new ExponentialBackOff())
                            .setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));
                } else {

                    //look into the bundle sent to Activity2 for data items
                    Bundle myBundle = superIntent.getExtras();
                    if (myBundle == null) {
                        myBundle = new Bundle();
                    }
                    myBundle.putString(RESULT_MSG, "Não foi possivel seguir.");
                    superIntent.putExtras(myBundle);

                    setResult(9999, superIntent);

                    finish();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void setReturn (int retorno, String msg) {
        //look into the bundle sent to Activity2 for data items
        Bundle myBundle = superIntent.getExtras();
        if (myBundle == null) {
            myBundle = new Bundle();
        }
        myBundle.putString(RESULT_MSG, msg);
        superIntent.putExtras(myBundle);

        //return sending an OK signal to calling activity
        setResult(retorno, superIntent);
    }
}
