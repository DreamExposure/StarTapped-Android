package org.dreamexposure.startapped.activities.blog.self;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.dreamexposure.startapped.R;
import org.dreamexposure.startapped.async.TaskCallback;
import org.dreamexposure.startapped.enums.TaskType;
import org.dreamexposure.startapped.network.blog.self.CreateBlogTask;
import org.dreamexposure.startapped.objects.network.NetworkCallStatus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BlogCreateActivity extends AppCompatActivity implements TaskCallback {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.blog_create_url)
    EditText blogUrl;

    @BindView(R.id.blog_create_button)
    Button blogCreateButton;

    //TODO: Add Blog type spinner to support blog types

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_create);

        ButterKnife.bind(this);

        toolbar.setTitle("Create Blog");
        setSupportActionBar(toolbar);
    }

    @OnClick(R.id.blog_create_button)
    public void handleCreateClick() {
        //handle creation task..
        if (!TextUtils.isEmpty(blogUrl.getText().toString().trim()))
            new CreateBlogTask(this, blogUrl.getText().toString().trim()).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.blog_create_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cancel:
                //Cancel creation, go past Go, do not collect $200. Go to self blog list.
                finish();
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void taskCallback(NetworkCallStatus status) {
        if (status.getType() == TaskType.BLOG_CREATE) {
            if (status.isSuccess()) {
                Toast.makeText(this, status.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, status.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
