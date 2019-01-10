package org.dreamexposure.startapped.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.dreamexposure.startapped.R;
import org.dreamexposure.startapped.StarTappedApp;
import org.dreamexposure.startapped.activities.account.ViewFollowingActivity;
import org.dreamexposure.startapped.activities.blog.self.BlogListSelfActivity;
import org.dreamexposure.startapped.activities.settings.SettingsActivity;
import org.dreamexposure.startapped.async.TaskCallback;
import org.dreamexposure.startapped.dialogs.post.CreatePostDialogFragment;
import org.dreamexposure.startapped.objects.network.NetworkCallStatus;
import org.dreamexposure.startapped.utils.RequestPermissionHandler;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HubActivity extends AppCompatActivity implements TaskCallback {
    private RequestPermissionHandler mRequestPermissionHandler;
    String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.action_create_post)
    FloatingActionButton createPostFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hub);
        mRequestPermissionHandler = new RequestPermissionHandler();

        ButterKnife.bind(this);

        toolbar.setTitle("Hub");
        setSupportActionBar(toolbar);

        //Check permissions on start
        doPermissionsCheck();

        //TODO: Get posts from blogs user is following.
    }

    @OnClick(R.id.action_create_post)
    void onCreatePostFabClick() {
        CreatePostDialogFragment dialog = new CreatePostDialogFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        dialog.show(ft, StarTappedApp.TAG);
    }

    private void doPermissionsCheck() {
        mRequestPermissionHandler.requestPermission(this, permissions, 123, new RequestPermissionHandler.RequestPermissionListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailed() {
                Toast.makeText(HubActivity.this, "StarTapped may not function as intended without all permissions accepted!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_blogs:
                startActivity(new Intent(this, BlogListSelfActivity.class));
                return true;
            case R.id.action_following:
                startActivity(new Intent(this, ViewFollowingActivity.class));
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void taskCallback(NetworkCallStatus status) {
        //TODO: Handle post create, and post gets
    }
}
