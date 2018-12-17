package org.dreamexposure.startapped.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.dreamexposure.startapped.utils.SettingsManager;

/**
 * @author NovaFox161
 * Date Created: 12/16/2018
 * For Project: StarTapped
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Load app settings
        SettingsManager.getManager().init();

        //Determine if user is logged in
        if (!SettingsManager.getManager().getSettings().getRefreshToken().equalsIgnoreCase("N/a")) {
            //TODO: Attempt token reauthorizing...

            //Auth successful...
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            //Send to login/register activity
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
