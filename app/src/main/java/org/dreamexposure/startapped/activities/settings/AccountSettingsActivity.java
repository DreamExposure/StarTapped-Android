package org.dreamexposure.startapped.activities.settings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Switch;

import org.dreamexposure.startapped.R;
import org.dreamexposure.startapped.network.account.UpdateAccountTask;
import org.dreamexposure.startapped.utils.SettingsManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;

public class AccountSettingsActivity extends AppCompatActivity {

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
            new UpdateAccountTask().execute(this);
        }
    }
}
