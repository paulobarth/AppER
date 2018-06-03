package br.com.newtechs.er;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.ArrayList;
import java.util.List;

import br.com.newtechs.er.ad.fnCP;
import br.com.newtechs.er.dao.UserLogin;
import br.com.newtechs.er.dao.UserLoginDao;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
//public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor>,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS  = 0;
    private static final int RC_SIGN_IN             = 1;

    /**
     * A dummy authentication store containing known user names and passwords.
     * remove after connecting to a real authentication system.
     */
//    private static final String[] DUMMY_CREDENTIALS = new String[]{
//            "aaaa@aaaa.com.br:aaaaaa", "bar@example.com:world"
//    };

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private String sErroLogin;
    private TextView txMsg;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Boolean isLocal;
    private JsonDocER jsLogin;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient mGoogleApiClient;
    private Button mEmailSignInButton;
    private com.google.android.gms.common.SignInButton mGoogleSignInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        txMsg = (TextView) findViewById(R.id.txMsg);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    return true;
                }
                return false;
            }
        });

        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        mGoogleSignInButton = (com.google.android.gms.common.SignInButton) findViewById(R.id.google_sign_in_button);
        mGoogleSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptGoogleLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    public void clickNewUser(View v) {
        Intent intent = new Intent(this, NewUserActivity.class);
        intent.putExtra("email", mEmailView.getText().toString());
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        try {
            if ((requestCode == 101)) {

                if (resultCode == Activity.RESULT_OK) {

                    Bundle myResults = data.getExtras();

                    mEmailView.setText(myResults.getString("email"));
                    mPasswordView.setText(myResults.getString("password"));

                    mEmailSignInButton.callOnClick();
                }
            } else if (requestCode == RC_SIGN_IN) {
                if (resultCode == Activity.RESULT_OK) {
                    GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

//                  Colocado no campo da tela para seguir o padrão de gravar a autenticação internamente
//                  no app, no SharedPreferences("auth", Context.MODE_PRIVATE);
                    mEmailView.setText(result.getSignInAccount().getEmail());
                    mPasswordView.setText(result.getSignInAccount().getId());
                    if (result.isSuccess()) {
                        showProgress(true);
                        // Google Sign In was successful, authenticate with Firebase
                        createUserLoginForGoogle(result.getSignInAccount());
                        firebaseAuthWithGoogle(result.getSignInAccount());

                    } else {
//                        showProgress(false);
                        Toast.makeText(this, "Não foi possível captar usuário do Google.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        catch (Exception e) {
            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        //fff
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        showProgress(false);
                        if (!task.isSuccessful()) {
                            postLoginUser(false, task.getException().getMessage());
                        } else {
                            postLoginUser(true, "");
                        }
                    }
                });
    }

    private void createUserLoginForGoogle(GoogleSignInAccount googleSignInAccount) {

        UserLoginDao ulDao = new UserLoginDao(this);
        ulDao.open();
        UserLogin ul = ulDao.getByEmail(googleSignInAccount.getEmail());
        String a1 = googleSignInAccount.getFamilyName();
        String a2 = googleSignInAccount.getGivenName();
        if (ul == null) {

            ul = new UserLogin();

            ul.setUser(googleSignInAccount.getDisplayName());
            ul.setEmail(googleSignInAccount.getEmail());
            ul.setPassword(googleSignInAccount.getId());
            ul.setCompanyName("");
            ulDao.create(ul);
        } else {

            ul.setUser(googleSignInAccount.getDisplayName());
            ul.setEmail(ul.getEmail());
            ul.setPassword(googleSignInAccount.getId());
            ul.setCompanyName("");
            ulDao.update(ul);
        }

        String retorno = ulDao.createNewUserFirebase(ul);
        ulDao.close();
    }
//
//    private void handleSignInResult(GoogleSignInResult result) {
//        if (result.isSuccess()) {
//            // Signed in successfully, show authenticated UI.
//            GoogleSignInAccount acct = result.getSignInAccount();
////            mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
////            updateUI(true);
//        } else {
//            // Signed out, show unauthenticated UI.
////            updateUI(false);
//        }
//    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }
//        else if (!isEmailValid(email)) {
//            mEmailView.setError(getString(R.string.error_invalid_email));
//            focusView = mEmailView;
//            cancel = true;
//        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
//      Sign out do usuário
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                        }
                    });

            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            showProgress(false);
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                postLoginUser(false, task.getException().getMessage());
                            } else {
                                postLoginUser(true, "");
                            }
                        }
                    });
        }
    }

    private void attemptGoogleLogin() {
//      Sign out do usuário
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                    }
                });

//      Caso queira limpar os dados do usuário internamente
//        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
//                new ResultCallback<Status>() {
//                    @Override
//                    public void onResult(Status status) {
//                    }
//                });
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void postLoginUser (Boolean success, String msg) {

        String mEmail = mEmailView.getText().toString();
        final String mPassword = mPasswordView.getText().toString();

        if (success) {
            //TODO Criar classe para trabalhar com as preferencias de autenticação
            SharedPreferences settings = getSharedPreferences("auth", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(fnCP.PREF_ACCOUNT_NAME, mEmail);
            editor.putString(fnCP.PREF_ACCOUNT_PASS, mPassword);
            editor.apply();

            finish();
        } else {
            if (msg.contains("A network error")) {
                String result = execLocalLogin(mEmail, mPassword);
                if (result.equals("NOK-erro")) {
                    setTxMsg("Sem conexão com internet.");
                } else if (result.equals("NOK-Senha inválida")) {
                    setTxMsg("Sem conexão. Informe última senha utilizada.");
                } else if (result.equals("OK")) {
                    //Usuário pode logar
                    postLoginUser(true, "");
                }
            } else {
                setTxMsg(msg);
                mEmailView.setError("Login inválido");
                mEmailView.requestFocus();
            }
        }
    }

    private String execLocalLogin (String email, String passowrd) {
        //Verificar antes se existe histórico de usuários
        UserLoginDao ulDao = new UserLoginDao(this);
        ulDao.open();
        UserLogin ul = ulDao.getByEmail(email);
        ulDao.close();
        if (ul != null) {
            if (ul.getPassword().equals(passowrd)) {
                return "OK";
            } else {
                return "NOK-Senha inválida";
            }
        }

        return "NOK-erro";
    }

    private boolean isEmailValid(String email) {
        //Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onClick(View v) {
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    private void setTxMsg (String msg) {
        txMsg.setText(msg);
        new ClearTxMsg().execute();
    }

    private class ClearTxMsg extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                return false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            txMsg.setText("");
        }
    }
}