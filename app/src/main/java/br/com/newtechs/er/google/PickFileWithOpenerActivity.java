/**
 * Copyright 2013 Google Inc. All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.newtechs.er.google;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.OpenFileActivityBuilder;

import br.com.newtechs.er.ad.fnCP;

/**
 * An activity to illustrate how to pick a file with the
 * opener intent.
 */
public class PickFileWithOpenerActivity extends BaseDriveActivity {

    private static final int REQUEST_CODE_OPENER = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        super.onConnected(connectionHint);
        IntentSender intentSender = Drive.DriveApi
                .newOpenFileActivityBuilder()
                .setMimeType(new String[] {
//                        "text/plain",
                        "application/vnd.google-apps.spreadsheet",
//                        "application/vnd.google-apps.document"
//                        "application/vnd.ms-excel", XLS
//                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" XLSX
                })
                .build(getGoogleApiClient());
        try {
            startIntentSenderForResult(
                    intentSender, REQUEST_CODE_OPENER, null, 0, 0, 0);
        } catch (SendIntentException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
//          Log.w(TAG, "Unable to send intent", e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case REQUEST_CODE_OPENER:
            if (resultCode == RESULT_OK) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(getGClient());
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(signInIntent);
                if (result != null) {
                    String s = result.getSignInAccount().getEmail();
                    String f = "";
                }


//                DriveId driveId = (DriveId) data.getParcelableExtra(
//                        OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
//                showMessage("Selected file's ID: " + driveId);

//                String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
//                if (accountName != null) {
////                    mCredential.setSelectedAccountName(accountName);
//                    SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
//                    SharedPreferences.Editor editor = settings.edit();
//                    editor.putString(fnCP.PICKED_GOOGLE_ACCOUNT, accountName);
//                    editor.apply();
//                }

                setResult(resultCode, data);
            }
            finish();
            break;
        default:
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
