package com.esgi.androidPassword;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.esgi.androidPassword.exception.AndroidPasswordException;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.drive.CreateFileActivityOptions;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.tasks.Task;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import static com.esgi.androidPassword.constant.AndroidPasswordConstant.CREATING_NEW_CONTENTS;
import static com.esgi.androidPassword.constant.AndroidPasswordConstant.CREATOR_REQUEST_CODE;
import static com.esgi.androidPassword.constant.AndroidPasswordConstant.EXPORT_ACTIVITY;
import static com.esgi.androidPassword.constant.AndroidPasswordConstant.FAILED_TO_CREATE_NEW_CONTENTS;
import static com.esgi.androidPassword.constant.AndroidPasswordConstant.FILE_EXPORT_PSS;
import static com.esgi.androidPassword.constant.AndroidPasswordConstant.IMAGE_SUCCESSFULLY_SAVED;
import static com.esgi.androidPassword.constant.AndroidPasswordConstant.NEW_CONTENTS_CREATED;
import static com.esgi.androidPassword.constant.AndroidPasswordConstant.SIGNED_IN_SUCCESSFULLY;
import static com.esgi.androidPassword.constant.AndroidPasswordConstant.SIGN_IN_REQUEST_CODE;
import static com.esgi.androidPassword.constant.AndroidPasswordConstant.START_SIGN_IN;
import static com.esgi.androidPassword.constant.AndroidPasswordConstant.UNABLE_TO_WRITE_FILE_CONTENTS;
import static java.security.AccessController.getContext;

public class ExportActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SIGN_IN = 0;
    private static final int REQUEST_CODE_CREATOR = 2;
    private static final String TEXT_PLAIN = "text/plain";
    private static final String FILE_NAME = "passwords";

    private DriveClient mDriveClient;
    private DriveResourceClient mDriveResourceClient;

    private Button btnSignIn;
    private Button btnExport;
    private Context context;

    private boolean isAuthenticated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        btnSignIn = findViewById(R.id.signIn);
        btnSignIn.setOnClickListener((View v) -> {
            if (isAuthenticated) {
                Toast.makeText(
                        context,
                        "Déjà authentifié !",
                        Toast.LENGTH_SHORT
                ).show();
            }

            if (!isAuthenticated) {
                context = v.getContext();
                signIn();
            }
        });

        btnExport = findViewById(R.id.btnExport);
        btnExport.setEnabled(false);
        btnExport.setOnClickListener((View v) -> {
            ExportPasswordsAsyncTask e = new ExportPasswordsAsyncTask(
                    getApplicationContext(), ExportActivity.this);
            e.execute();
        });
    }

    /**
     * Start sign in activity.
     */
    private void signIn() {
        Log.i(EXPORT_ACTIVITY, START_SIGN_IN);
        GoogleSignInClient googleSignInClient = buildGoogleSignInClient();
        startActivityForResult(googleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }

    /**
     * Build a Google SignIn client.
     */
    private GoogleSignInClient buildGoogleSignInClient() {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(Drive.SCOPE_FILE)
                        .build();
        return GoogleSignIn.getClient(this, signInOptions);
    }

    /**
     * Create a new file and save it to Drive.
     */
    public void saveFileToDrive() {
        // Start by creating a new contents, and setting a callback.
        Log.i(EXPORT_ACTIVITY, CREATING_NEW_CONTENTS);

        mDriveResourceClient
                .createContents()
                .continueWithTask(
                        task -> {
                            FileInputStream fis = openFileInput(FILE_EXPORT_PSS);
                            return createFileIntentSender(task.getResult(), fis);
                        })
                .addOnFailureListener(
                        e -> Log.w(EXPORT_ACTIVITY, FAILED_TO_CREATE_NEW_CONTENTS, e));

    }

    /**
     * Creates an {@link IntentSender} to start a dialog activity with configured {@link
     * CreateFileActivityOptions} for user to create in Drive.
     */
    private Task<Void> createFileIntentSender(DriveContents driveContents, FileInputStream fis) throws AndroidPasswordException {
        Log.i(EXPORT_ACTIVITY, NEW_CONTENTS_CREATED);
        // Get an output stream for the contents.
        OutputStream outputStream = driveContents.getOutputStream();
        // Write the data from it.
        try {
            outputStream.write(readFromFIS(fis).toString().getBytes());
        } catch (IOException e) {
            Log.e(EXPORT_ACTIVITY, UNABLE_TO_WRITE_FILE_CONTENTS, e);
            throw new AndroidPasswordException(UNABLE_TO_WRITE_FILE_CONTENTS, e);
        }

        // Create the initial metadata - MIME type and title.
        MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                .setTitle(FILE_NAME)
                .setMimeType(TEXT_PLAIN)
                .setStarred(true)
                .build();

        // Set up options to configure and display the create file activity.
        CreateFileActivityOptions createFileActivityOptions =
                new CreateFileActivityOptions.Builder()
                        .setInitialMetadata(metadataChangeSet)
                        .setInitialDriveContents(driveContents)
                        .build();

        return mDriveClient
                .newCreateFileActivityIntentSender(createFileActivityOptions)
                .continueWith(
                        task -> {
                            startIntentSenderForResult(task.getResult(), REQUEST_CODE_CREATOR, null, 0, 0, 0);
                            return null;
                        });
    }

    @NonNull
    private StringBuilder readFromFIS(FileInputStream fis) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cr = 0;
        while ((cr = fis.read()) != -1) {
            sb.append((char) cr);
        }
        return sb;
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                Log.i(EXPORT_ACTIVITY, SIGN_IN_REQUEST_CODE);
                // Called after user is signed in.
                if (RESULT_OK == resultCode) {
                    Log.i(EXPORT_ACTIVITY, SIGNED_IN_SUCCESSFULLY);
                    isAuthenticated = true;
                    Toast.makeText(
                            context,
                            "Connexion avec succès",
                            Toast.LENGTH_SHORT
                    ).show();
                    btnExport.setEnabled(true);
                    // Use the last signed in account here since it already have a Drive scope.
                    mDriveClient = Drive.getDriveClient(this,
                            GoogleSignIn.getLastSignedInAccount(this));
                    // Build a drive resource client.
                    mDriveResourceClient =
                            Drive.getDriveResourceClient(this,
                                    GoogleSignIn.getLastSignedInAccount(this));
                }
                break;
            case REQUEST_CODE_CREATOR:
                Log.i(EXPORT_ACTIVITY, CREATOR_REQUEST_CODE);
                // Called after a file is saved to Drive.
                if (resultCode == RESULT_OK) {
                    Log.i(EXPORT_ACTIVITY, IMAGE_SUCCESSFULLY_SAVED);
                }
                break;
            default:
                break;
        }
    }

}