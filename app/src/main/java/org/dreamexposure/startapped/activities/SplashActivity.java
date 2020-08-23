package org.dreamexposure.startapped.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.dreamexposure.startapped.activities.auth.LoginActivity;
import org.dreamexposure.startapped.async.TaskCallback;
import org.dreamexposure.startapped.enums.TaskType;
import org.dreamexposure.startapped.network.account.GetAccountTask;
import org.dreamexposure.startapped.network.auth.TokenReauthTask;
import org.dreamexposure.startapped.objects.network.NetworkCallStatus;
import org.dreamexposure.startapped.utils.SettingsManager;

/**
 * @author NovaFox161
 * Date Created: 12/16/2018
 * For Project: StarTapped
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
public class SplashActivity extends AppCompatActivity implements TaskCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Load app settings
        SettingsManager.getManager().init();

        //Determine if user is logged in
        if (!SettingsManager.getManager().getSettings().getRefreshToken().equalsIgnoreCase("N/a")) {
            //Make sure tokens aren't revoked.
            new TokenReauthTask(this).execute();
        } else {
            //Send to login/register activity
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void taskCallback(NetworkCallStatus status) {
        if (status.getType() == TaskType.AUTH_TOKEN_REAUTH) {
            if (status.isSuccess()) {
                Intent intent = new Intent(this, HubActivity.class);
                startActivity(intent);
                finish();

                //Get the account settings
                new GetAccountTask().execute();
            } else {
                Toast.makeText(this, status.getMessage(), Toast.LENGTH_LONG).show();
                //Go back to login page, and invalidate saved credentials...
                SettingsManager.getManager().getSettings().setRefreshToken("N/a");
                SettingsManager.getManager().getSettings().setAccessToken("N/a");
                SettingsManager.getManager().getSettings().setTokenExpire(0);

                SettingsManager.getManager().getSettings().setUsername("N/a");
                SettingsManager.getManager().getSettings().setSafeSearch(false);
                SettingsManager.getManager().getSettings().setEmailConfirmed(false);
                SettingsManager.getManager().getSettings().setVerified(false);
                SettingsManager.getManager().getSettings().setBirthday("01-01-1970");
                SettingsManager.getManager().getSettings().setPhoneNumber("000.000.0000");
                SettingsManager.getManager().saveSettings();

                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }
}
