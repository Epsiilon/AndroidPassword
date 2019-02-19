package com.esgi.androidPassword;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.esgi.androidPassword.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import static com.esgi.androidPassword.constant.AndroidPasswordConstant.ERROR_DURING_REQUEST;

public class ExportActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final String FILE_EXPORT_PSS = "myFile.pss";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        Button btnExport = findViewById(R.id.btnExport);
        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                writeFile(v);
            }
        });
    }




    public void writeFile(View view) {
        try {
            FileOutputStream fos = openFileOutput(FILE_EXPORT_PSS, MODE_PRIVATE);
            writeFOS(fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void writeFOS(final FileOutputStream fos) {
        CollectionReference datas = db.collection("datas");
        datas.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            final String header = "Title, Username, Password, Notes\n";
                            final StringBuilder sBody = new StringBuilder();
                            final StringBuilder sContent = new StringBuilder();

                            List<DocumentSnapshot> myListOfDocuments = task.getResult().getDocuments();

                            for (DocumentSnapshot documentSnapshot: myListOfDocuments) {

                                sBody.append(documentSnapshot.get("title"));
                                sBody.append(",");
                                sBody.append(documentSnapshot.get("username"));
                                sBody.append(",");
                                sBody.append(documentSnapshot.get("password"));
                                sBody.append(",");
                                sBody.append(documentSnapshot.get("title"));
                                sBody.append(",");
                                sBody.append(documentSnapshot.get("notes"));
                                sBody.append("\n");
                            }

                                sContent.append(header);
                                sContent.append(sBody);

                                try {
                                    fos.write(sContent.toString().getBytes(Charset.forName("UTF-8")));
                                } catch (IOException e) {
                                    // TODO add LOG
                                    e.printStackTrace();
                                } finally {
                                    try {
                                        fos.flush();
                                        fos.close();
                                    } catch (IOException e) {
                                        // TODO add LOG
                                        e.printStackTrace();
                                    }
                                }

                        }
                    }
                });

    }
}
