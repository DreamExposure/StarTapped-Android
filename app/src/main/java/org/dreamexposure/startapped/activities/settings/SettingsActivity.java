package org.dreamexposure.startapped.activities.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import org.dreamexposure.startapped.R;
import org.dreamexposure.startapped.activities.SearchActivity;
import org.dreamexposure.startapped.activities.account.ViewFollowingActivity;
import org.dreamexposure.startapped.activities.auth.LoginActivity;
import org.dreamexposure.startapped.activities.blog.self.BlogListSelfActivity;
import org.dreamexposure.startapped.async.TaskCallback;
import org.dreamexposure.startapped.auth.AuthenticationHandler;
import org.dreamexposure.startapped.enums.TaskType;
import org.dreamexposure.startapped.objects.network.NetworkCallStatus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, TaskCallback {

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

        //Drawer setup...
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

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
        if (item.getItemId() == R.id.action_signout) {
            AuthenticationHandler.get().logout(this);
        }
        // If we got here, the user's action was not recognized.
        // Invoke the superclass to handle it.
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (id == R.id.nav_hub) {
            finish();
            return true;
        } else if (id == R.id.nav_search) {
            startActivity(new Intent(this, SearchActivity.class));
            finish();
        } else if (id == R.id.nav_explore) {
            //TODO: Handle going to explore
        } else if (id == R.id.nav_blogs) {
            startActivity(new Intent(this, BlogListSelfActivity.class));
            finish();
        } else if (id == R.id.nav_following) {
            startActivity(new Intent(this, ViewFollowingActivity.class));
            finish();
        } else if (id == R.id.nav_settings) {
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
