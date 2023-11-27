package com.example.taskmaster.Activites;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amplifyframework.core.Amplify;
import com.example.taskmaster.R;

public class verifyAccountActivity extends AppCompatActivity {
    public static final String TAG= "VerifyAccountActivity";

    public static final String VERIFY_ACCOUNT_EMAIL_TAG = "Verify_Account_Email_Tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_account);



        Intent callingIntent= getIntent();
        String email = callingIntent.getStringExtra(signUpActivity.SIGNUP_EMAIL_TAG);
        EditText usernameEditText = (EditText) findViewById(R.id.verifyAccountUsernameEditText);
        usernameEditText.setText(email);

        Button verifyAccountVerifyButton = findViewById(R.id.verifyAccountVerifyButton);
        verifyAccountVerifyButton.setOnClickListener(v ->
        {
            String username= usernameEditText.getText().toString();
            String verificationCode = ((EditText)findViewById(R.id.verifyAccountVerificatoinCodeEditText)).getText().toString();

            Amplify.Auth.confirmSignUp(username,
                    verificationCode,
                    good ->
                    {
                        Log.i(TAG,"verification succeeded: "+ good.toString());
                        Intent goToLoginIntent = new Intent(verifyAccountActivity.this, LoginActivity.class);
                        goToLoginIntent.putExtra(VERIFY_ACCOUNT_EMAIL_TAG, username);
                        startActivity(goToLoginIntent);

                    },
                    failure ->
                    {
                        Log.i(TAG,"verification failed: "+ failure.toString());
                        runOnUiThread(() ->
                        {
                            Toast.makeText(verifyAccountActivity.this, " Verify account failed!!", Toast.LENGTH_LONG);
                        });
                    }
            );
        });
    }
    }
