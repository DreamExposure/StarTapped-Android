package org.dreamexposure.startapped.activities.auth;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.safetynet.SafetyNet;

import org.dreamexposure.startapped.R;
import org.dreamexposure.startapped.StarTappedApp;
import org.dreamexposure.startapped.auth.AuthenticationHandler;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.signUpUsername)
    EditText signUpUsername;
    @BindView(R.id.signUpEmail)
    EditText signUpEmail;
    @BindView(R.id.birthday_button)
    Button signUpBirthdayButton;
    @BindView(R.id.signUpPassword)
    EditText signUpPassword;

    private int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        //Handle birthday stuffs
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month + 1, day);
    }

    @OnClick(R.id.signInButton)
    public void switchToSignIn() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.signUpButton)
    public void register() {
        // checking for empty feedback message
        if (TextUtils.isEmpty(signUpUsername.getText().toString().trim()) || TextUtils.isEmpty(signUpEmail.getText().toString().trim()) || TextUtils.isEmpty(signUpPassword.getText().toString().trim()))
            return;

        // Showing reCAPTCHA dialog
        SafetyNet.getClient(this).verifyWithRecaptcha("6LcJQ4IUAAAAAKjXoXfktNxWUe4F7S5HiEZJVwS5")
                .addOnSuccessListener(this, response -> {
                    Log.d(StarTappedApp.TAG, "onSuccess");

                    if (!response.getTokenResult().isEmpty()) {
                        //TODO: Show loading animation....


                        String user = signUpUsername.getText().toString();
                        String email = signUpEmail.getText().toString();
                        String pass = signUpPassword.getText().toString();
                        String birthday = getBirthday();

                        //signInEmail.getRootView().setSelected(false);
                        //signInPassword.getRootView().setSelected(false);

                        //Async login...
                        AuthenticationHandler.get().register(user, email, pass, birthday, response.getTokenResult(), this);
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


    @SuppressWarnings("deprecation")
    @OnClick(R.id.birthday_button)
    public void setDate() {
        showDialog(999);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = (arg0, arg1, arg2, arg3) -> {
        // arg1 = year
        // arg2 = month
        // arg3 = day
        showDate(arg1, arg2 + 1, arg3);
    };

    private void showDate(int year, int month, int day) {
        signUpBirthdayButton.setText(new StringBuilder().append("Birthday: ").append(day).append("/")
                .append(month).append("/").append(year));
    }

    private String getBirthday() {
        return year + "-" + month + "-" + day;
    }
}
