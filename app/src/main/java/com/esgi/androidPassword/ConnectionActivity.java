package com.esgi.androidPassword;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.esgi.androidPassword.util.PasswordUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.Nullable;

import static com.esgi.androidPassword.constant.AndroidPasswordConstant.ERROR_CRYPTING_PASSWORD;
import static com.esgi.androidPassword.constant.AndroidPasswordConstant.ERROR_DURING_REQUEST;

public class ConnectionActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        Button validateButton = findViewById(R.id.validate);
        validateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                final EditText editText = findViewById(R.id.editText);
                final String editTextValue = editText.getText().toString();
                checkPassword(v, editTextValue);
            }

            private void checkPassword(final View v, final String editTextValue) {
                DocumentReference user = db.collection("user").document("1");
                user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot doc = task.getResult();

                            String cryptedPassword = getPassword(editTextValue);

                            if (doc.get("passwordKey").equals(cryptedPassword)) {
                                startCreationActivity();
                            } else {
                                Toast.makeText(
                                        v.getContext(),
                                        getString(R.string.wrong_password),
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(ERROR_DURING_REQUEST,  e.getMessage());
                        Toast.makeText(
                                v.getContext(),
                                getString(R.string.error_request),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
            }
        });
    }

    @Nullable
    private String getPassword(String editTextValue) {
        String cryptedPassword = null;
        try {
            cryptedPassword = PasswordUtils.encryptToAES(editTextValue);
        } catch (Exception e) {
            Log.e(ERROR_CRYPTING_PASSWORD, "Error during crypting password {}" +
                    e.getMessage());
        }

        final StringBuilder password = new StringBuilder();
        password.append(cryptedPassword.substring(0, cryptedPassword.length() -1));
        password.append("\\n");

        return password.toString();
    }

    /**
     * Start ConnectionActivity
     */
    private void startCreationActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
