package com.esgi.androidPassword;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static com.esgi.androidPassword.constant.AndroidPasswordConstant.COPIE;

public class CreationActivity extends AppCompatActivity {

    private boolean isShowed;

    private EditText passwordGenerated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation);

        isShowed = false;
        passwordGenerated = findViewById(R.id.passwordGenerated);

        Button btnCopy = findViewById(R.id.copy);
        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (!passwordGenerated.getText().toString().trim().isEmpty()) {
                    ClipboardManager cm = (ClipboardManager) v.getContext()
                            .getSystemService(Context.CLIPBOARD_SERVICE);
                    cm.setText(passwordGenerated.getText());

                    StringBuilder txtToast = new StringBuilder();
                    txtToast.append(COPIE);
                    txtToast.append(passwordGenerated.getText().toString());

                    Toast.makeText(v.getContext(), txtToast, Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button btnShowPassword = findViewById(R.id.show);
        btnShowPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (!isShowed) {
                    passwordGenerated.setTransformationMethod(
                            HideReturnsTransformationMethod.getInstance());
                    isShowed = true;
                } else {
                    passwordGenerated.setTransformationMethod(
                            PasswordTransformationMethod.getInstance());
                    isShowed = false;
                }
            }
        });

        Button btnGenerate = findViewById(R.id.generate);
        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                EditText passwordSize = findViewById(R.id.passwordSize);
                EditText numberSize = findViewById(R.id.numberSize);
                EditText specialCharacterSize = findViewById(R.id.specialCharacterSize);

                Integer.parseInt(passwordSize.getText().toString());
                Integer.parseInt(numberSize.getText().toString());
                Integer.parseInt(specialCharacterSize.getText().toString());

                passwordGenerated.setText("coucou");
            }
        });
    }
}
