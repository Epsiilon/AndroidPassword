package com.esgi.androidPassword;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.esgi.androidPassword.dummy.DummyContent.DummyItem;
import com.esgi.androidPassword.util.PasswordUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.esgi.androidPassword.constant.AndroidPasswordConstant.ERROR_DURING_REQUEST;

public class EditActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        final String idData = getIntent().getStringExtra("idData");

        DocumentReference datas = db.collection("datas").document(idData);
        datas.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();

                    final EditText title = findViewById(R.id.title);
                    final EditText username = findViewById(R.id.username);
                    final EditText password = findViewById(R.id.password);
                    final EditText notes = findViewById(R.id.notes);

                    title.setText((String) doc.get("title"));
                    username.setText((String) doc.get("username"));
                    try {
                        password.setText(PasswordUtils.decryptFromAES((String) doc.get("password")));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    notes.setText((String) doc.get("notes"));

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(ERROR_DURING_REQUEST,  e.getMessage());
            }
        });



        Button validateButton = findViewById(R.id.save);
        validateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DocumentReference datas = db.collection("datas").document(idData);

                final EditText title = findViewById(R.id.title);
                final EditText username = findViewById(R.id.username);
                final EditText password = findViewById(R.id.password);
                final EditText notes = findViewById(R.id.notes);

                DummyItem d = new DummyItem();
                d.setId(idData);
                d.setPassword(password.getText().toString());
                d.setNotes(notes.getText().toString());
                try {
                    d.setPassword(PasswordUtils.encryptToAES(password.getText().toString()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                d.setUsername(username.getText().toString());
                d.setTitle(title.getText().toString());

                db.collection("datas").document(idData)
                        .set(d)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("", "DocumentSnapshot successfully written!");
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("", "Error writing document", e);
                                finish();
                            }
                        });
            }
        });
    }
}
