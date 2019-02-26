package com.esgi.androidPassword;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.esgi.androidPassword.constant.AndroidPasswordConstant.EXPORT_PWD_ASYNC_TASK;
import static com.esgi.androidPassword.constant.AndroidPasswordConstant.UNABLE_TO_WRITE_FILE_CONTENTS;

public class InfosAsyncTask  extends AsyncTask<Void, Void, List<DocumentSnapshot>> {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Context context;
    private Activity activity;
    private TextView textView;

    public InfosAsyncTask(Context context, Activity activity, TextView textView) {
        this.context = context;
        this.activity = activity;
        this.textView = textView;
    }


    @Override
    protected List<DocumentSnapshot> doInBackground(Void... voids) {
        final List<DocumentSnapshot> documents = new ArrayList<>();

        final CollectionReference data = db.collection("infos");
        data.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    documents.addAll(task.getResult().getDocuments());
                }
            }
        });

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Log.e(EXPORT_PWD_ASYNC_TASK, UNABLE_TO_WRITE_FILE_CONTENTS, e);
        }

        return documents;
    }

    @Override
    protected void onPostExecute(List<DocumentSnapshot> documentSnapshots) {
        super.onPostExecute(documentSnapshots);

        Collections.shuffle(documentSnapshots);
        textView.setText((String) documentSnapshots.get(0).get("description"));
    }

}
