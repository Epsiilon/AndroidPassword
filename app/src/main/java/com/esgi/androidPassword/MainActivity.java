package com.esgi.androidPassword;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.esgi.androidPassword.dummy.DummyContent.DummyItem;

public class MainActivity extends AppCompatActivity implements DataFragment.OnListFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnGenerate = findViewById(R.id.btnAdd2);
        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                startCreationActivity();
            }
        });

        Button btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                startEditActivity();
            }
        });

        Button btnExport = findViewById(R.id.btnExport);
        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                startExportActivity();
            }
        });



    }

    @Override
    public void onListFragmentInteraction(DummyItem item) {
        Toast.makeText(this, item.toString(), Toast.LENGTH_SHORT).show();
    }

    /**
     * Start EditActivity
     */
    private void startEditActivity() {
        Intent intent = new Intent(this, EditActivity.class);
        startActivity(intent);
    }

    /**
     * Start CreationActivity
     */
    private void startCreationActivity() {
        Intent intent = new Intent(this, CreationActivity.class);
        startActivity(intent);
    }

    /**
     * Start ExportActivity
     */
    private void startExportActivity() {
        Intent intent = new Intent(this, ExportActivity.class);
        startActivity(intent);
    }


}
