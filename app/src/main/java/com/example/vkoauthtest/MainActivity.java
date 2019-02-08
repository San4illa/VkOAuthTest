package com.example.vkoauthtest;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String REQUEST_FIELDS = "first_name,last_name,photo_100";
    private String[] scope = new String[]{VKScope.MESSAGES, VKScope.FRIENDS, VKScope.WALL};

    private TextView nameTextView;
    private ImageView photoImageView;
    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private UsersAdapter adapter;

    private ArrayList<User> users = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameTextView = findViewById(R.id.tv_name);
        photoImageView = findViewById(R.id.iv_photo);

        recyclerView = findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UsersAdapter(users);
        recyclerView.setAdapter(adapter);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> getFriends());

        if (!VKSdk.isLoggedIn()) {
            VKSdk.login(this, scope);
        } else {
            getUser();
            getFriends();
        }
    }

    private void getUser() {
        VKRequest request = VKApi.users().get(VKParameters.from(VKApiConst.FIELDS, REQUEST_FIELDS));
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);

                try {
                    JSONObject userJSON = response.json.getJSONArray("response")
                            .getJSONObject(0);
                    String userName = userJSON.getString("first_name") + " " + userJSON.getString("last_name");
                    String userPhotoUrl = userJSON.getString("photo_100");
                    Picasso.get().load(userPhotoUrl).into(photoImageView);
                    nameTextView.setText(userName);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getFriends() {
        VKRequest request = VKApi.friends().get(
                VKParameters.from(VKApiConst.FIELDS, REQUEST_FIELDS,
                        VKApiConst.COUNT, 5,
                        "order", "random"));
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);

                users.clear();
                try {
                    JSONArray usersJSON = response.json.getJSONObject("response").getJSONArray("items");
                    for (int i = 0; i < 5; i++) {
                        JSONObject userJSON = usersJSON.getJSONObject(i);
                        String userName = userJSON.getString("first_name") + " " + userJSON.getString("last_name");
                        String userPhotoUrl = userJSON.getString("photo_100");

                        users.add(new User(userName, userPhotoUrl));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(0);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                Toast.makeText(MainActivity.this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();

                getUser();
                getFriends();
            }

            @Override
            public void onError(VKError error) {
                Toast.makeText(MainActivity.this, getString(R.string.login_error), Toast.LENGTH_SHORT).show();
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            VKSdk.logout();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
