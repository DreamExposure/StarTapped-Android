package org.dreamexposure.startapped.activities.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import org.dreamexposure.startapped.R;
import org.dreamexposure.startapped.activities.auth.LoginActivity;
import org.dreamexposure.startapped.async.TaskCallback;
import org.dreamexposure.startapped.auth.AuthenticationHandler;
import org.dreamexposure.startapped.enums.TaskType;
import org.dreamexposure.startapped.objects.network.NetworkCallStatus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends AppCompatActivity implements TaskCallback {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.account_settings_button)
    Button accountSettingsButton;
    @BindView(R.id.notifications_settings_button)
    Button notificationsButton;
    @BindView(R.id.network_settings_button)
    Button networkSettingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ButterKnife.bind(this);

        //Setup toolbar
        toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_toolbar, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_signout:
                AuthenticationHandler.get().logout(this);
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @OnClick(R.id.account_settings_button)
    void setAccountSettingsButton() {
        startActivity(new Intent(this, AccountSettingsActivity.class));
    }

    @Override
    public void taskCallback(NetworkCallStatus status) {
        if (status.getType() == TaskType.AUTH_LOGOUT) {
            if (status.isSuccess()) {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, status.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
