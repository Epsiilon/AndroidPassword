package com.esgi.androidPassword;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ConnectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        final Intent intent = new Intent(this, CreationActivity.class);

        Button validateButton = findViewById(R.id.validate);
        validateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Toast.makeText(v.getContext(), "Clicked !", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });
    }


}
