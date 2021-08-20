package com.example.socialsiteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class ProfileActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private ImageView profile_image;
    private TextView name;
    private TextView email;
    private TextView id;
    private Button signOutBtn;
    private GoogleApiClient googleApiClient;
    private GoogleSignInOptions gso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        name=findViewById(R.id.name);
        email=findViewById(R.id.email);
        id=findViewById(R.id.id);
        signOutBtn=findViewById(R.id.signOutBtn);

        gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient=new GoogleApiClient.Builder(this).enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();
        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull  Status status) {
                 if(status.isSuccess()){
                     gotoMainActivity();
                 }else{
                     Toast.makeText(ProfileActivity.this, "Logout Failed", Toast.LENGTH_SHORT).show();
                 }
                }
            });
            }
        });
    }

    private void gotoMainActivity() {

        startActivity(new Intent(ProfileActivity.this,MainActivity.class));
        finish();
    }

    @Override
    public void onConnectionFailed(@NonNull  ConnectionResult connectionResult) {

    }
    private void handleSignInResult(GoogleSignInResult result){
       if(result.isSuccess()){
           GoogleSignInAccount account=result.getSignInAccount();
           name.setText(account.getDisplayName());
           email.setText(account.getEmail());
           id.setText(account.getId());

          // Picasso.get().load(account.getPhotoUrl()).into(profile_image);

       }else{
          gotoMainActivity();
       }
    }

    @Override
    protected void onStart() {
        super.onStart();
        OptionalPendingResult<GoogleSignInResult> opr=Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if(opr.isDone()){
            GoogleSignInResult result=opr.get();
            handleSignInResult(result);
        }else{
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull  GoogleSignInResult googleSignInResult) {
               handleSignInResult(googleSignInResult);
                }
            });
        }
    }
}