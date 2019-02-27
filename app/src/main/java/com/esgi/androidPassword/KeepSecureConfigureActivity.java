package com.esgi.androidPassword;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

public class KeepSecureConfigureActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keep_secure_configure);


        TextView txtInfos = findViewById(R.id.infos);

        InfosAsyncTask infosAsyncTask = new InfosAsyncTask(
                getApplicationContext(),
                KeepSecureConfigureActivity.this, txtInfos);
        infosAsyncTask.execute();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
