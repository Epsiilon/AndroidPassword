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

import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;

import java.util.Arrays;
import java.util.List;

import static com.esgi.androidPassword.constant.AndroidPasswordConstant.INSUFFICIENT_SPECIAL;

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
                    txtToast.append(getString(R.string.wrong_password));
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

                if (checkFieldsValidity(v, passwordSize, numberSize, specialCharacterSize)) return;

                List<CharacterRule> rules = initRules(numberSize, specialCharacterSize);

                PasswordGenerator generator = new PasswordGenerator();

                String password = generator.generatePassword(Integer.parseInt(passwordSize.getText().toString()), rules);

                passwordGenerated.setText(password);
            }
        });
    }

    private boolean checkFieldsValidity(View v, EditText passwordSize, EditText numberSize, EditText specialCharacterSize) {
        if (passwordSize.getText().toString().trim().isEmpty()) {
            Toast.makeText(
                    v.getContext(),
                    getString(R.string.enter_password),
                    Toast.LENGTH_SHORT
            ).show();
            return true;
        }

        if (numberSize.getText().toString().trim().isEmpty()) {
            Toast.makeText(
                    v.getContext(),
                    getString(R.string.enter_chiffre),
                    Toast.LENGTH_SHORT
            ).show();
            return true;
        }

        if (specialCharacterSize.getText().toString().trim().isEmpty()) {
            Toast.makeText(
                    v.getContext(),
                    getString(R.string.enter_speciaux),
                    Toast.LENGTH_SHORT
            ).show();
            return true;
        }
        return false;
    }

    private List<CharacterRule> initRules(EditText numberSize, EditText specialCharacterSize) {
        char[] special = new char[]{'!', '"', '#', '$', '%', '&', '\'', '(', ')', '*',
                '+', '-', '.', '/', ':', '<', '=', '>', '?', '@', '[', '\\', ']',
                '^', '_', '`', '{', '|', '}', '~'};
        return Arrays.asList(
                // at least one upper-case character
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                // at least one lower-case character
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                // at least one digit character
                new CharacterRule(EnglishCharacterData.Digit, Integer.parseInt(numberSize.getText().toString())),
                new CharacterRule(new CharacterData() {
                    @Override
                    public String getErrorCode() {
                        return INSUFFICIENT_SPECIAL;
                    }

                    @Override
                    public String getCharacters() {
                        return new String(special);
                    }
                }, Integer.parseInt(specialCharacterSize.getText().toString())));
    }
}
