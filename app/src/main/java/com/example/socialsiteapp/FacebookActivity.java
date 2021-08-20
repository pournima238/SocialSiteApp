package com.example.socialsiteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class FacebookActivity extends AppCompatActivity {
    private TextView info, fid;
    private ImageView profile;
    private LoginButton login;
    CallbackManager callbackManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook);
        info=findViewById(R.id.info);
        profile=findViewById(R.id.profile);
        fid=findViewById(R.id.fid);
        //login=findViewById(R.id.login);
        login=findViewById(R.id.loginButton);

        callbackManager= CallbackManager.Factory.create();
        login.setPermissions(Arrays.asList("user_gender, user_friends"));
        //login.setPermissions(Arrays.asList("public_profile, user_freinds,email"));
        login.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                fid.setText("User Id= " + loginResult.getAccessToken().getUserId());
                //String imageURL="https://graph.facebook.com"+loginResult.getAccessToken().getUserId()+ "/picture?return_ssl_resources=1";
                // Picasso.get().load(imageURL).into(profile);
               // Picasso.get().load("https://graph.facebook.com/" + imageURL + "/picture?type=large").placeholder(R.drawable.flower).into(profile);

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
        GraphRequest graphRequest=GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            String name=object.getString("name");
                            String id=object.getString("id");
                            info.setText(name);
                            //Picasso.get().load("https://graph.facebook.com/" + id + "/picture?type=large").placeholder(R.drawable.flower).into(profile);
                            //Picasso.get().load("https://graph.facebook.com/" + id+ "/picture?type=large").into(profile);
                            Glide.with(FacebookActivity.this).load("https://graph.facebook.com/" + id + "/picture?type=large").into(profile);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle bundle= new Bundle();
        bundle.putString("fields","gender, name, id, first_name, last_name");
        graphRequest.setParameters(bundle);
        graphRequest.executeAsync();

    }
    AccessTokenTracker accessTokenTracker=new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if(currentAccessToken==null){
                LoginManager.getInstance().logOut();
                info.setText("");
                profile.setImageResource(0);
                fid.setText("");
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }


}