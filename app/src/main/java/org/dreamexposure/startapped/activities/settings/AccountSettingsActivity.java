package org.dreamexposure.startapped.activities.settings;

import android.os.Bundle;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.dreamexposure.startapped.R;
import org.dreamexposure.startapped.async.TaskCallback;
import org.dreamexposure.startapped.enums.TaskType;
import org.dreamexposure.startapped.network.account.UpdateAccountTask;
import org.dreamexposure.startapped.objects.network.NetworkCallStatus;
import org.dreamexposure.startapped.utils.SettingsManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;

public class AccountSettingsActivity extends AppCompatActivity implements TaskCallback {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.safe_search_switch)
    Switch safeSearchSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
        ButterKnife.bind(this);

        //Setup toolbar
        toolbar.setTitle("Account Settings");

        //Load account settings
        safeSearchSwitch.setChecked(SettingsManager.getManager().getSettings().isSafeSearch());

    }

    @OnCheckedChanged(R.id.safe_search_switch)
    protected void onSafeSearchChange() {
        if (safeSearchSwitch.isPressed()) {
            SettingsManager.getManager().getSettings().setSafeSearch(safeSearchSwitch.isChecked());
            new UpdateAccountTask(this).execute();
        }
    }

    @Override
    public void taskCallback(NetworkCallStatus status) {
        if (status.getType() == TaskType.ACCOUNT_UPDATE) {
            if (status.isSuccess()) {
                //Save settings
                SettingsManager.getManager().saveSettings();
                Toast.makeText(this, status.getMessage(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, status.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
