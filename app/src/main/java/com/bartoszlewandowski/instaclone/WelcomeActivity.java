package com.bartoszlewandowski.instaclone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WelcomeActivity extends AppCompatActivity {

    @BindView(R.id.txtWelcome)
    TextView txtWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);
        txtWelcome.setText("Welcome! " + ParseUser.getCurrentUser().get("username"));
    }

    @OnClick(R.id.btnLogout)
    public void logout(View v){
        ParseUser.logOut();
        finish();
    }

}
