package com.esgi.androidPassword;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.esgi.androidPassword.util.PasswordUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.esgi.androidPassword.constant.AndroidPasswordConstant.EXPORT_PWD_ASYNC_TASK;
import static com.esgi.androidPassword.constant.AndroidPasswordConstant.FILE_EXPORT_PSS;
import static com.esgi.androidPassword.constant.AndroidPasswordConstant.UNABLE_TO_WRITE_FILE_CONTENTS;

class ExportPasswordsAsyncTask extends AsyncTask<Void, Void, List<DocumentSnapshot>> {

    private static final String COMMA = ",";
    private static final String BACKSLASH_N = "\n";

    private static final String TITLE = "title";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String NOTES = "notes";

    private static final String TITLE_USERNAME_PASSWORD_NOTES = "Title, Username, Password, Notes\n";

    private static final String UTF_8 = "UTF-8";
    private static final String SAVE_FILE_TO_DRIVE = "saveFileToDrive";
    private static final String DATAS = "datas";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Context context;
    private Activity activity;

    public ExportPasswordsAsyncTask(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    @Override
    protected List<DocumentSnapshot> doInBackground(Void... v) {
        final List<DocumentSnapshot> documents = new ArrayList<>();

        final CollectionReference data = db.collection(DATAS);
        data.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    documents.addAll(task.getResult().getDocuments());
                }
            }
        });

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Log.e(EXPORT_PWD_ASYNC_TASK, UNABLE_TO_WRITE_FILE_CONTENTS, e);
        }

        return documents;
    }

    @Override
    protected void onPostExecute(List<DocumentSnapshot> documentSnapshots) {
        super.onPostExecute(documentSnapshots);
        FileOutputStream fos = null;

        try {
            fos = context.openFileOutput(FILE_EXPORT_PSS, MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            Log.e(EXPORT_PWD_ASYNC_TASK, UNABLE_TO_WRITE_FILE_CONTENTS, e);
        }

        final StringBuilder content = new StringBuilder();
        content.append(getHeader());
        content.append(getBody(documentSnapshots));

        write(fos, content);

        callSaveDrive();
    }

    private String getHeader() {
        String header = null;
        try {
            header = PasswordUtils.encryptToAES(TITLE_USERNAME_PASSWORD_NOTES);
        } catch (Exception e) {
            Log.e(EXPORT_PWD_ASYNC_TASK, UNABLE_TO_WRITE_FILE_CONTENTS, e);
        }
        return header;
    }

    @NonNull
    private String getBody(List<DocumentSnapshot> documentSnapshots) {
        final StringBuilder body = new StringBuilder();

        for (final DocumentSnapshot documentSnapshot : documentSnapshots) {
            try {
                body.append(PasswordUtils.encryptToAES((String) documentSnapshot.get(TITLE)));
                body.append(COMMA);
                body.append(PasswordUtils.encryptToAES((String) documentSnapshot.get(USERNAME)));
                body.append(COMMA);
                body.append(PasswordUtils.encryptToAES((String) documentSnapshot.get(PASSWORD)));
                body.append(COMMA);
                body.append(PasswordUtils.encryptToAES((String) documentSnapshot.get(NOTES)));
                body.append(BACKSLASH_N);
            } catch (Exception e) {
                Log.e(EXPORT_PWD_ASYNC_TASK, UNABLE_TO_WRITE_FILE_CONTENTS, e);
            }
        }
        return body.toString();
    }

    private void write(FileOutputStream fos, StringBuilder content) {
        try {
            fos.write(content.toString().getBytes(Charset.forName(UTF_8)));
        } catch (IOException e) {
            Log.e(EXPORT_PWD_ASYNC_TASK, UNABLE_TO_WRITE_FILE_CONTENTS, e);
        } finally {
            try {
                fos.flush();
                fos.close();
            } catch (IOException e) {
                Log.e(EXPORT_PWD_ASYNC_TASK, UNABLE_TO_WRITE_FILE_CONTENTS, e);
            }
        }
    }

    private void callSaveDrive() {
        try {
            Method saveFileToDrive = activity.getClass().getMethod(SAVE_FILE_TO_DRIVE);
            saveFileToDrive.invoke(activity);
        } catch (Exception e) {
            Log.e(EXPORT_PWD_ASYNC_TASK, UNABLE_TO_WRITE_FILE_CONTENTS, e);
        }
    }
}