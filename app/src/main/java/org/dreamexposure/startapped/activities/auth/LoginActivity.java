package org.dreamexposure.startapped.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.safetynet.SafetyNet;

import org.dreamexposure.startapped.R;
import org.dreamexposure.startapped.StarTappedApp;
import org.dreamexposure.startapped.activities.HubActivity;
import org.dreamexposure.startapped.async.TaskCallback;
import org.dreamexposure.startapped.auth.AuthenticationHandler;
import org.dreamexposure.startapped.enums.TaskType;
import org.dreamexposure.startapped.network.account.GetAccountTask;
import org.dreamexposure.startapped.objects.network.NetworkCallStatus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity implements TaskCallback {

    //Sign in objects
    @BindView(R.id.signInEmail)
    EditText signInEmail;
    @BindView(R.id.signInPassword)
    EditText signInPassword;
    @BindView(R.id.signInButton)
    Button signInButton;
    @BindView(R.id.signUpButton)
    Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.signUpButton)
    public void handleSignUp() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.signInButton)
    public void handleLogin() {
        // checking for empty feedback message
        if (TextUtils.isEmpty(signInEmail.getText().toString().trim()) || TextUtils.isEmpty(signInPassword.getText().toString().trim()))
            return;

        // Showing reCAPTCHA dialog
        SafetyNet.getClient(this).verifyWithRecaptcha("6LcJQ4IUAAAAAKjXoXfktNxWUe4F7S5HiEZJVwS5")
                .addOnSuccessListener(this, response -> {
                    Log.d(StarTappedApp.TAG, "onSuccess");

                    if (!response.getTokenResult().isEmpty()) {
                        //TODO: Show loading animation....


                        String email = signInEmail.getText().toString();
                        String pass = signInPassword.getText().toString();

                        //signInEmail.getRootView().setSelected(false);
                        //signInPassword.getRootView().setSelected(false);

                        //Async login...
                        AuthenticationHandler.get().login(email, pass, response.getTokenResult(), this);
                    }
                })
                .addOnFailureListener(this, e -> {
                    if (e instanceof ApiException) {
                        ApiException apiException = (ApiException) e;
                        Log.d(StarTappedApp.TAG, "Error message: " +
                                CommonStatusCodes.getStatusCodeString(apiException.getStatusCode()));
                    } else {
                        Log.d(StarTappedApp.TAG, "Unknown type of error: " + e.getMessage());
                    }
                });
    }

    @Override
    public void taskCallback(NetworkCallStatus status) {
        if (status.getType() == TaskType.AUTH_LOGIN) {
            if (status.isSuccess()) {
                Intent intent = new Intent(this, HubActivity.class);
                startActivity(intent);
                finish();

                //Get the account settings
                new GetAccountTask().execute();
            } else {
                Toast.makeText(this, status.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
