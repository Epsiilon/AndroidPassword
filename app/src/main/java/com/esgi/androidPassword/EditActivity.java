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

import java.security.SecureRandom;

import static com.esgi.androidPassword.constant.AndroidPasswordConstant.DATAS;
import static com.esgi.androidPassword.constant.AndroidPasswordConstant.DOCUMENT_SNAPSHOT_SUCCESSFULLY_WRITTEN;
import static com.esgi.androidPassword.constant.AndroidPasswordConstant.EDIT_ACTIVITY;
import static com.esgi.androidPassword.constant.AndroidPasswordConstant.ERROR_DURING_REQUEST;
import static com.esgi.androidPassword.constant.AndroidPasswordConstant.ERROR_WRITING_DOCUMENT;
import static com.esgi.androidPassword.constant.AndroidPasswordConstant.ID_DATA;
import static com.esgi.androidPassword.constant.AndroidPasswordConstant.NOTES;
import static com.esgi.androidPassword.constant.AndroidPasswordConstant.PASSWORD;
import static com.esgi.androidPassword.constant.AndroidPasswordConstant.TITLE;
import static com.esgi.androidPassword.constant.AndroidPasswordConstant.USERNAME;

public class EditActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        final String idData = getIntent().getStringExtra(ID_DATA);

        if (null == idData) {
            displayFormSave();
        } else {
            displayFormEdit(idData);
        }
    }

    /**
     * Display form edit
     * @param idData
     */
    private void displayFormEdit(final String idData) {

        final EditText title = findViewById(R.id.title);
        final EditText username = findViewById(R.id.username);
        final EditText password = findViewById(R.id.password);
        final EditText notes = findViewById(R.id.notes);

        fillForm(idData, title, username, password, notes);

        Button validateButton = findViewById(R.id.save);
        validateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DummyItem d = new DummyItem();
                d.setId(idData);
                d.setPassword(password.getText().toString());
                d.setNotes(notes.getText().toString());
                try {
                    d.setPassword(PasswordUtils.encryptToAES(password.getText().toString()));
                } catch (Exception e) {
                    Log.e(EDIT_ACTIVITY, ERROR_WRITING_DOCUMENT, e);
                }
                d.setUsername(username.getText().toString());
                d.setTitle(title.getText().toString());

                db.collection(DATAS).document(idData)
                        .set(d)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.i(EDIT_ACTIVITY, DOCUMENT_SNAPSHOT_SUCCESSFULLY_WRITTEN);
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(EDIT_ACTIVITY, ERROR_WRITING_DOCUMENT, e);
                                finish();
                            }
                        });
            }
        });


    }

    /**
     * fill data in form.
     * @param idData
     * @param title
     * @param username
     * @param password
     * @param notes
     */
    private void fillForm(String idData, EditText title, EditText username, EditText password, EditText notes) {
        DocumentReference datas = db.collection(DATAS).document(idData);
        datas.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    title.setText((String) doc.get(TITLE));
                    username.setText((String) doc.get(USERNAME));
                    try {
                        password.setText(PasswordUtils.decryptFromAES((String) doc.get(PASSWORD)));
                    } catch (Exception e) {
                        Log.e(EDIT_ACTIVITY, ERROR_WRITING_DOCUMENT, e);
                    }
                    notes.setText((String) doc.get(NOTES));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(ERROR_DURING_REQUEST, e.getMessage());
            }
        });
    }

    /**
     * Display form save
     */
    private void displayFormSave() {
        Button validateButton = findViewById(R.id.save);
        validateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final EditText title = findViewById(R.id.title);
                final EditText username = findViewById(R.id.username);
                final EditText password = findViewById(R.id.password);
                final EditText notes = findViewById(R.id.notes);

                SecureRandom secureRandom = new SecureRandom();
                String id = Integer.toString(secureRandom.nextInt());
                // Collecting of data
                DummyItem dummyItem = new DummyItem();
                dummyItem.setId(id);
                dummyItem.setTitle(title.getText().toString());
                dummyItem.setUsername(username.getText().toString());
                dummyItem.setPassword(password.getText().toString());
                try {
                    dummyItem.setPassword(PasswordUtils.encryptToAES(password.getText().toString()));
                } catch (Exception e) {
                    Log.e(EDIT_ACTIVITY, ERROR_WRITING_DOCUMENT, e);
                }
                dummyItem.setNotes(notes.getText().toString());

                db.collection(DATAS).document(id)
                        .set(dummyItem).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(EDIT_ACTIVITY, DOCUMENT_SNAPSHOT_SUCCESSFULLY_WRITTEN);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(EDIT_ACTIVITY, ERROR_WRITING_DOCUMENT, e);
                        finish();
                    }
                });
            }
        });
    }

}
